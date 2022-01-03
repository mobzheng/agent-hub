package com.mobzheng.trace.collector;

import java.lang.instrument.Instrumentation;

public interface Collector {


    /**
     * 注册日志采集器
     * @param instr
     */
    void register(Instrumentation instr);


}
