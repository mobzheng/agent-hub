package com.mobzheng.trace.collector;

import com.mobzheng.trace.AgentSession;
import com.mobzheng.trace.log.Log;
import com.mobzheng.trace.log.LogFactory;
import com.mobzheng.trace.model.HttpRequestInfo;
import javassist.*;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;
import java.util.Arrays;

/**
 * 3.14.4
 * <p>
 */
public class OkHttp3Collector implements Collector, ClassFileTransformer {

    static final Log logger = LogFactory.getLog(OkHttp3Collector.class);

    private static final String TARGET_CLASS = "okhttp3.RealCall";
    private static final String TARGET_METHOD = "execute";

    @Override
    public void register(Instrumentation instr) {
        instr.addTransformer(this);
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        return buildClass(loader, className);
    }


    byte[] buildClass(ClassLoader classLoader, String className) {
        if (className == null || !TARGET_CLASS.equals(className.replace("/", "."))) {
            return null;
        }

        logger.debug("===========================okhttp3插桩开始===========================");

        byte[] bytes = null;
        try {
            ClassPool classPool = new ClassPool();
            classPool.appendSystemPath();
            classPool.insertClassPath(new LoaderClassPath(classLoader));
            CtClass ctClass = classPool.get(TARGET_CLASS);
            CtMethod ctMethod = ctClass.getDeclaredMethod(TARGET_METHOD);
            CtConstructor constructor = ctClass.getConstructor("(Lokhttp3/OkHttpClient;Lokhttp3/Request;Z)V");
            constructor.insertAfter("this.originalRequest = (okhttp3.Request)com.mobzheng.trace.collector.OkHttp3Collector.buildRequest($2);");
            CtMethod newMethod = CtNewMethod.copy(ctMethod, ctClass, null);
            newMethod.setName(ctMethod.getName() + "$agent");
            ctClass.addMethod(newMethod);
//            CtField interceptor = CtField.make("Interceptor interceptor;", classPool.getOrNull("okhttp3.Interceptor"));
//            ctClass.addField(interceptor,"new Interceptor() {\\n\" +\n" +
//                    "            \"        @Override\\n\" +\n" +
//                    "            \"        public Response intercept(Chain chain) throws IOException {\\n\" +\n" +
//                    "            \"            Request request = chain.request().newBuilder()\\n\" +\n" +
//                    "            \"                    .build();\\n\" +\n" +
//                    "            \"            return chain.proceed(request);\\n\" +\n" +
//                    "            \"        }\\n\" +\n" +
//                    "            \"    };\\n\"");

            String body = "{\n" +
                    "com.mobzheng.trace.collector.OkHttp3Collector.begin();\n" +
                    "return this.execute$agent($$);\n" +
                    "}";
            ctMethod.setBody(body);
            bytes = ctClass.toBytecode();
            logger.debug("===========================okhttp3插桩成功===========================");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("===========================okhttp3插桩失败===========================");
        }

        return bytes;
    }

    public static void begin() {
        AgentSession agentSession = AgentSession.get();
        logger.debug("okhttp请求开始：" + agentSession.info);
    }


    public static Object buildRequest(Object request) {
        OkhttpRequestAdapter adapter = new OkhttpRequestAdapter(request);
        HttpRequestInfo info = new HttpRequestInfo();
        info.url = adapter.getUrl();
        AgentSession session = AgentSession.get();
        if (session != null) {
            info.traceId = session.getTraceId();
            info.spanId = session.nextSpanId();
            adapter.addHeader(AgentSession.TRACE_ID_KEY, session.getTraceId());
            adapter.addHeader(AgentSession.PARENT_ID_KEY, session.nextSpanId());
            session.put(info);
            return adapter.build();
        }

        return request;
    }


    static class OkhttpRequestAdapter {
        private Object request;
        private Object builder;
        private Method addHeader, build, getUrl;

        public OkhttpRequestAdapter(Object request) {
            this.request = request;
            try {
                Method newBuilder = request.getClass().getDeclaredMethod("newBuilder");
                this.builder = newBuilder.invoke(request);
                this.addHeader = Arrays.stream(this.builder.getClass().getDeclaredMethods()).filter(m -> "addHeader" .equals(m.getName())).findFirst().get();
                this.build = this.builder.getClass().getDeclaredMethod("build");
                this.getUrl = this.request.getClass().getDeclaredMethod("url");
            } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }

        }

        public void addHeader(String key, String value) {
            try {
                this.addHeader.invoke(builder, key, value);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }


        public String getUrl() {
            try {
                Object url = this.getUrl.invoke(this.request);
                return url.toString();
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
            return null;
        }

        public Object build() {
            try {
                return build.invoke(builder);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }


    }


    public OkHttp3Collector() {
    }

    public OkHttp3Collector(String a, CtClass ctClass) {

    }

    public static void main(String[] args) throws NotFoundException {
        System.out.println();
        ClassPool aDefault = ClassPool.getDefault();
        CtClass ctClass = aDefault.get(OkHttp3Collector.class.getName());
        System.out.println(ctClass.getConstructor("(Ljava/lang/String;Ljavassist/CtClass;)V"));
    }

}
