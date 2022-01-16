package com.mobzheng.trace;


import com.alibaba.ttl.TransmittableThreadLocal;
import com.mobzheng.trace.log.Log;
import com.mobzheng.trace.log.LogFactory;
import com.mobzheng.trace.process.DataProcess;
import com.mobzheng.trace.util.PackageScaner;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class AgentSession {
    static final Log logger = LogFactory.getLog(AgentSession.class);
    // 需要解决跨线程问题
    private static final ThreadLocal<AgentSession> currentSession = new TransmittableThreadLocal<>();
    public static final String TRACE_ID_KEY = "agent-traceId";
    public static final String PARENT_ID_KEY = "agent-parentId";
    private String traceId;
    private String parentId;
    public Object info;
    private int nextId = 0;
    static List<DataProcess> processes = new ArrayList<>();

    static {
        PackageScaner.scanPackage(AgentSession.class.getPackage().getName(), c -> {
            if (DataProcess.class.isAssignableFrom(c) && !c.isInterface()
                    && !Modifier.isAbstract(c.getModifiers())) {
                DataProcess process = null;
                try {
                    process = (DataProcess) c.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
                if (process == null) {
                    return;
                }
                processes.add(process);
                processes = processes.stream().sorted(Comparator.comparingInt(DataProcess::ordered)).collect(Collectors.toList());
            }
        });
    }

    private AgentSession() {

    }

    public static AgentSession get() {
        return currentSession.get();
    }

    public static AgentSession open() {
        String traceId = UUID.randomUUID().toString()
                .replaceAll("-", "");
        String parentId = "0";
        return open(traceId, parentId);
    }

    public static AgentSession open(String traceId, String parentId) {
        AgentSession agentSession = new AgentSession();
        currentSession.set(agentSession);
        agentSession.traceId = traceId;
        agentSession.parentId = parentId;
        return agentSession;
    }

    public static void close() {
        // 完成所有处理器
        try {

        } finally {
            currentSession.remove();
        }
    }

    // 推送采集到的数据
    public void put(Object node) {
        for (int i = 0; i < processes.size(); i++) {
            DataProcess process = processes.get(i);
            Object accept = process.accept(node);
            if (accept instanceof DataProcess.Operate) {
                if (accept == DataProcess.Operate.OVER) {
                    return;
                }
            }
        }
        logger.debug("开始推送数据到各个节点");
    }

    public String getTraceId() {
        return traceId;
    }

    public String nextSpanId() {
        nextId++;
        return parentId + "." + nextId;
    }

    public String getParentId() {
        return parentId;
    }
}
