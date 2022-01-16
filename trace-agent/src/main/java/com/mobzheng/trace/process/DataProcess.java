package com.mobzheng.trace.process;

public interface DataProcess<T> {


    default int ordered() {
        return Integer.MAX_VALUE;
    }


    T accept(Object data);



    enum Operate {
        OVER,
        CONTINUE,
        SKIP;
    }

}
