package com.mobzheng.trace;


import com.mobzheng.trace.collector.HttpServletCollector;
import com.mobzheng.trace.collector.HttpURLCollector;

import java.lang.instrument.Instrumentation;

/**
 * 启动主类
 */
public class Launcher {

    public static void premain(String args, Instrumentation instr) {
        System.out.println("mobzheng v1.0");
        instr.addTransformer(new HttpServletCollector());
        HttpURLCollector.registerProtocol();
    }
}
