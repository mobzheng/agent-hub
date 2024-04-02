package com.mobzheng.trace.collector;

import com.mobzheng.trace.AgentSession;
import com.mobzheng.trace.log.Log;
import com.mobzheng.trace.log.LogFactory;
import com.mobzheng.trace.model.SqlInfo;
import com.mobzheng.trace.util.SqlFormatUtil;
import javassist.*;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.security.ProtectionDomain;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Arrays;


public class JdbcCollector implements Collector, ClassFileTransformer {

    private final static Log logger = LogFactory.getLog(JdbcCollector.class);

    @Override
    public void register(Instrumentation instr) {
        instr.addTransformer(this);
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        return buildClass(loader, className);
    }


    // mysql
    private static final String TARGET_CLASS = "com.mysql.cj.jdbc.NonRegisteringDriver";
    private static final String TARGET_METHOD = "connect";


    byte[] buildClass(ClassLoader classLoader, String className) {
        if (className == null || !TARGET_CLASS.equals(className.replace("/", "."))) {
            return null;
        }

        logger.debug("===========================mysql  enhance init===========================");

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
            String body = "{return (java.sql.Connection)com.mobzheng.trace.collector.JdbcCollector.getConnectionProxy((java.sql.Connection)this.connect$agent($$));}";
            ctMethod.setBody(body);
            bytes = ctClass.toBytecode();
            logger.debug("===========================mysql enhance success===========================");
        } catch (Exception e) {
            logger.error("===========================mysql enhance fail===========================");
        }

        return bytes;
    }

    public static Connection getConnectionProxy(Connection connection) {
        return (Connection) Proxy.newProxyInstance(JdbcCollector.class.getClassLoader(), new Class[]{Connection.class}, new ConnectionProxy(connection));
    }

    static class ConnectionProxy implements InvocationHandler {


        String[] proxyMethods = new String[]{"prepareStatement"};

        final Connection originalConnection;

        ConnectionProxy(Connection originalConnection) {
            this.originalConnection = originalConnection;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (Arrays.stream(proxyMethods).anyMatch(m -> m.equals(method.getName()))) {
                Object sqlInfo = begin(args);
                return Proxy.newProxyInstance(JdbcCollector.class.getClassLoader(),
                        new Class[]{PreparedStatement.class},
                        new PreparedStatementProxy((PreparedStatement) method.invoke(originalConnection, args), sqlInfo));
            }
            return method.invoke(originalConnection, args);
        }
    }

    static class PreparedStatementProxy implements InvocationHandler {

        static final String METHOD_PREFIX = "execute";

        Object sqlInfo;
        final PreparedStatement originalStatement;

        PreparedStatementProxy(PreparedStatement originalStatement, Object sqlInfo) {
            this.originalStatement = originalStatement;
            this.sqlInfo = sqlInfo;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            boolean isTrace = method.getName().startsWith(METHOD_PREFIX);
            Object result = null;
            try {
                result = method.invoke(originalStatement, args);
            } catch (Exception e) {
                if (isTrace) {
                    error(e, this.sqlInfo);
                }
            } finally {
                if (isTrace) {
                    end(originalStatement, this.sqlInfo);
                }
            }
            return result;
        }
    }


    static Object begin(Object[] arg) {
        SqlInfo sqlInfo = new SqlInfo();
        sqlInfo.begin = System.nanoTime();
        sqlInfo.databaseName = "test";
        AgentSession session = AgentSession.get();
        if (session == null) {
            return sqlInfo;
        }
        sqlInfo.traceId = session.getTraceId();
        sqlInfo.spanId = session.nextSpanId();
        return sqlInfo;
    }

    static void end(Object sql, Object sqlInfo) {
        String sqlStr = SqlFormatUtil.format(sql.toString().split(":")[1]);
        SqlInfo info = (SqlInfo) sqlInfo;
        info.sql = sqlStr;
        info.end = System.nanoTime();
        info.useTime = info.end - info.begin;
        logger.info("sql execute { " + info + " }");
    }


    static void error(Exception e, Object sqlInfo) {
        Throwable exception = e;
        if (e instanceof InvocationTargetException) {
            exception = ((InvocationTargetException) e).getTargetException();
        }
        SqlInfo info = (SqlInfo) sqlInfo;
        info.error = exception.getMessage();
    }


}
