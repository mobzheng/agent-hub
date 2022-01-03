/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mobzheng.trace.log;


import java.util.logging.*;

/**
 * Hardcoded java.util.logging commons-logging implementation.
 * <p>
 * In addition, it curr
 */
class DirectJDKLog implements Log {

    private static FileHandler fileHandler;
    private static ConsoleHandler consoleHandler;

    private Level currentLevel = Level.ALL;
    private String name;
    private Handler[] handlers;

    public DirectJDKLog(String name) {
        consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(currentLevel);
        this.name = name;
        if (fileHandler == null && consoleHandler == null) {
            throw new IllegalStateException("未初始化日志处理器。调用DirectJDKLog.init 进行初始化");
        }

        handlers = new Handler[]{ consoleHandler};
    }


    private static Level toLevel(String levelName) {
        if ("off".equals(levelName)) {
            return Level.OFF;
        } else if ("info".equals(levelName)) {
            return Level.INFO;
        } else if ("warn".equals(levelName)) {
            return Level.WARNING;
        } else if ("error".equals(levelName)) {
            return Level.SEVERE;
        } else if ("debug".equals(levelName)) {
            return Level.FINE;
        } else {
            throw new IllegalArgumentException("log level '" + levelName + "' is Illegality");
        }
    }

    @Override
    public final boolean isErrorEnabled() {
        return isLoggable(Level.SEVERE);
    }

    @Override
    public final boolean isWarnEnabled() {
        return isLoggable(Level.WARNING);
    }

    @Override
    public final boolean isInfoEnabled() {
        return isLoggable(Level.INFO);
    }

    @Override
    public final boolean isDebugEnabled() {
        return isLoggable(Level.FINE);
    }

    @Override
    public final boolean isFatalEnabled() {
        return isLoggable(Level.SEVERE);
    }

    @Override
    public final boolean isTraceEnabled() {
        return isLoggable(Level.FINER);
    }

    public boolean isLoggable(Level level) {
        return currentLevel.intValue() <= level.intValue();
    }

    @Override
    public final void debug(Object message) {
        log(Level.FINE, String.valueOf(message), null);
    }

    @Override
    public final void debug(Object message, Throwable t) {
        log(Level.FINE, String.valueOf(message), t);
    }

    @Override
    public final void trace(Object message) {
        log(Level.FINER, String.valueOf(message), null);
    }

    @Override
    public final void trace(Object message, Throwable t) {
        log(Level.FINER, String.valueOf(message), t);
    }

    @Override
    public final void info(Object message) {
        log(Level.INFO, String.valueOf(message), null);
    }

    @Override
    public final void info(Object message, Throwable t) {
        log(Level.INFO, String.valueOf(message), t);
    }

    @Override
    public final void warn(Object message) {
        log(Level.WARNING, String.valueOf(message), null);
    }

    @Override
    public final void warn(Object message, Throwable t) {
        log(Level.WARNING, String.valueOf(message), t);
    }

    @Override
    public final void error(Object message) {
        log(Level.SEVERE, String.valueOf(message), null);
    }

    @Override
    public final void error(Object message, Throwable t) {
        log(Level.SEVERE, String.valueOf(message), t);
    }

    @Override
    public final void fatal(Object message) {
        log(Level.SEVERE, String.valueOf(message), null);
    }

    @Override
    public final void fatal(Object message, Throwable t) {
        log(Level.SEVERE, String.valueOf(message), t);
    }

    // from commons logging. This would be my number one reason why java.util.logging
    // is bad - design by committee can be really bad ! The impact on performance of 
    // using java.util.logging - and the ugliness if you need to wrap it - is far
    // worse than the unfriendly and uncommon default format for logs. 

    private void log(Level level, String msg, Throwable ex) {
        if (!isLoggable(level)) {
            return;
        }
        // Hack (?) to get the stack trace.
        Throwable dummyException = new Throwable();
        StackTraceElement locations[] = dummyException.getStackTrace();
        // Caller will be the third element
        String cname = "unknown";
        String method = "unknown";
        if (locations != null && locations.length > 2) {
            StackTraceElement caller = locations[2];
            cname = caller.getClassName();
            method = caller.getMethodName();
        }


        LogRecord lr = new LogRecord(level, msg);
        lr.setSourceClassName(cname);
        lr.setSourceMethodName(method);
        lr.setThrown(ex);
        lr.setLoggerName(name);
        //  写入日志
        for (Handler handler : handlers) {
            handler.publish(lr);
        }
    }


    // for LogFactory
    static void release() {

    }

    static Log getInstance(String name) {
        return new DirectJDKLog(name);
    }
}


