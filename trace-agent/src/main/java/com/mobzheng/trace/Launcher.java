package com.mobzheng.trace;


import com.mobzheng.trace.collector.Collector;
import com.mobzheng.trace.log.Log;
import com.mobzheng.trace.log.LogFactory;
import com.mobzheng.trace.util.PackageScaner;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Modifier;

/**
 * 启动主类
 */
public class Launcher {
    static final Log logger = LogFactory.getLog(Launcher.class);

    public static void premain(String args, Instrumentation instr) {
        logger.info("mobzheng v1.0");

        PackageScaner.scanPackage(Launcher.class.getPackage().getName(), c -> {
            if (Collector.class.isAssignableFrom(c) && !c.isInterface()
                    && !Modifier.isAbstract(c.getModifiers())) {
                try {
                    Collector collector = (Collector) c.newInstance();
                    collector.register(instr);
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        });


    }

}
