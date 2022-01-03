package com.mobzheng.trace;


import java.util.UUID;

public class AgentSession {
    // 需要解决跨线程问题
    private static ThreadLocal<AgentSession> currentSession = new ThreadLocal<>();
    public static final String TRACE_ID_KEY = "agent-traceId";
    public static final String PARENT_ID_KEY = "agent-parentId";
    private String traceId;
    private String parentId;
    private int nextId = 0;


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

        System.out.println("开始推送数据到各个节点");
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
