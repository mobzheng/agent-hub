package com.mobzheng.service;

import com.mobzheng.model.HttpRequestInfo;
import com.mobzheng.model.SqlInfo;
import com.mobzheng.model.TraceNode;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class TraceNode2Info {

    static List<Function<TraceNode, Object>> process = Arrays.asList(
            SqlInfo::buildSqlInfo,
            HttpRequestInfo::buildRequest
    );


    public List<Object> buildTraceNode(List<TraceNode> traceNodes) {
        return traceNodes.stream().flatMap(n -> process.stream().map(p -> p.apply(n))).collect(Collectors.toList());
    }

}
