package com.mobzheng.trace.process;

public class ToJSONString implements DataProcess<String> {


    @Override
    public int ordered() {
        return -1;
    }

    @Override
    public String accept(Object node) {
        return node.toString();
    }
}
