package com.mobzheng.trace.collector;

import java.lang.instrument.Instrumentation;

public class HttpURLCollector implements Collector {
    @Override
    public void register(Instrumentation instr) {
        registerProtocol();
    }

    private static String PROTOCOL_HANDLER = "java.protocol.handler.pkgs";
    private static String HANDLERS_PACKAGE = "com.mobzheng.trace.collector.handler";

    public static void registerProtocol() {
        String handlers = System.getProperty(PROTOCOL_HANDLER, "");
        System.setProperty(PROTOCOL_HANDLER,
                ((handlers == null || handlers.isEmpty()) ?
                        HANDLERS_PACKAGE : handlers + "|" + HANDLERS_PACKAGE));
    }
}
