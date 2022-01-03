package com.mobzheng.trace.collector;

import com.mobzheng.trace.AgentSession;
import com.mobzheng.trace.model.HttpRequestInfo;
import javassist.*;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;
import java.util.Map;

public class HttpServletCollector implements Collector, ClassFileTransformer {

    private static final String TARGET_CLASS = "javax.servlet.http.HttpServlet";
    private static final String TARGET_METHOD = "service";


    @Override
    public void register(Instrumentation instr) {
        instr.addTransformer(this);
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        return buildClass(loader, className);
    }


    byte[] buildClass(ClassLoader classLoader, String className) {
        if (null == className || !TARGET_CLASS.equals(className.replace("/", "."))) {
            return null;
        }
        System.out.println("===========================servlet插桩开始===========================");

        byte[] bytes = null;
        try {
            ClassPool classPool = new ClassPool();
            classPool.appendSystemPath();
            classPool.insertClassPath(new LoaderClassPath(classLoader));
            CtClass ctClass = classPool.get(TARGET_CLASS);
            CtMethod ctMethod = ctClass.getDeclaredMethod(TARGET_METHOD);

            CtMethod newMethod = CtNewMethod.copy(ctMethod, ctClass, null);
            newMethod.setName(ctMethod.getName() + "$agent");
            ctClass.addMethod(newMethod);
            String beginSrc = String.format("Object stat=%s.begin($args);", getClass().getName());
            String errorSrc = String.format("%s.error(e,stat);", getClass().getName());
            String endSrc = String.format("%s.end(stat);", getClass().getName());
            String body = "{        %s\n" +
                    "  try {\n" +
                    "            service$agent($$);\n" +
                    "        } catch (Exception e) {\n" +
                    "            %s\n" +
                    "        } finally {\n" +
                    "            %s\n" +
                    "        }}";
            body = String.format(body, beginSrc, errorSrc, endSrc);
            ctMethod.setBody(body);


            bytes = ctClass.toBytecode();
            System.out.println("===========================servlet插桩成功===========================");
        } catch (Exception e) {
            System.out.println("===========================servlet插桩失败===========================");
            e.printStackTrace();
        }
        return bytes;
    }


    public static HttpRequestInfo begin(Object[] args) {

        HttpRequestAdapter adapter = new HttpRequestAdapter(args[0]);
        HttpRequestInfo info = new HttpRequestInfo();
        info.begin = System.nanoTime();
        info.url = adapter.getRequestURL();
        info.httpParams = adapter.getQueryString();
        info.clientIp = adapter.getClientIp();
        // 如果从请求头中取不到可以认为是前段请求
        String parentId = adapter.getHeader(AgentSession.PARENT_ID_KEY);
        String traceId = adapter.getHeader(AgentSession.TRACE_ID_KEY);
        AgentSession session = null;

        if (parentId != null && traceId != null) {
            session = AgentSession.open(traceId, parentId);
        }

        if (session == null) {
            session = AgentSession.open();
        }
        info.spanId = session.getParentId();
        info.traceId = session.getTraceId();
        session.put(info);
        System.out.println("请求开始：" + info);
        return info;
    }


    public static void end(Object info) {
        HttpRequestInfo requestInfo = (HttpRequestInfo) info;
        requestInfo.end = System.nanoTime();
        AgentSession.close();
        System.out.println("请求结束：" + info);
    }


    public static void error(Exception e, Object info) {
        HttpRequestInfo requestInfo = (HttpRequestInfo) info;
        requestInfo.error = e.getMessage();
        System.out.println("请求错误");
        e.printStackTrace();
    }

    static class HttpRequestAdapter {
        private static final String TARGET_CLASS_ = "javax.servlet.http.HttpServletRequest";

        private Method _getRequestURI,
                _getRequestURL,
                _getParameterMap,
                _getQueryString,
                _getMethod,
                _getHeader,
                _getRemoteAddr;


        private final Object target;


        HttpRequestAdapter(Object target) {
            try {
                this.target = target;
                Class<?> targetClass = target.getClass().getClassLoader().loadClass(TARGET_CLASS_);
                _getRequestURI = targetClass.getMethod("getRequestURI");
                _getParameterMap = targetClass.getMethod("getParameterMap");
                _getMethod = targetClass.getMethod("getMethod");
                _getHeader = targetClass.getMethod("getHeader", String.class);
                _getRemoteAddr = targetClass.getMethod("getRemoteAddr");
                _getRequestURL = targetClass.getMethod("getRequestURL");
                _getQueryString = targetClass.getMethod("getQueryString");
            } catch (ClassNotFoundException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }


        public String getRequestURI() {
            try {
                return (String) _getRequestURI.invoke(target);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public String getRequestURL() {
            try {
                return _getRequestURL.invoke(target).toString();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public Map<String, String[]> getParameterMap() {
            try {
                return (Map<String, String[]>) _getParameterMap.invoke(target);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public String getQueryString() {
            try {
                return (String) _getQueryString.invoke(target);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public String getMethod() {
            try {
                return (String) _getMethod.invoke(target);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public String getHeader(String name) {
            try {
                return (String) _getHeader.invoke(target, name);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public String getRemoteAddr() {
            try {
                return (String) _getRemoteAddr.invoke(target);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public String getClientIp() {
            String ip = getHeader("x-forwarded-for");
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = getHeader("Proxy-Client-IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = getHeader("WL-Proxy-Client-IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = getRemoteAddr();
            }
            return ip;
        }
    }
}
