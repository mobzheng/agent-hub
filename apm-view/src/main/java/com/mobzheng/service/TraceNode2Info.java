package com.mobzheng.service;

import com.mobzheng.model.GraphView;
import com.mobzheng.model.HttpRequestInfo;
import com.mobzheng.model.SqlInfo;
import com.mobzheng.model.TraceNode;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class TraceNode2Info {

    static List<Function<TraceNode, Object>> process = Arrays.asList(
            SqlInfo::buildSqlInfo,
            HttpRequestInfo::buildRequest
    );


    public List<Object> buildTraceNode(List<TraceNode> traceNodes) {
        return traceNodes.stream().flatMap(n -> process.stream().map(p -> p.apply(n))).filter(Objects::nonNull).collect(Collectors.toList());
    }


    public GraphView buildView(List<Object> nodes) {
        GraphView graphView = new GraphView();
        List<GraphView.Nodes> nodesList = nodes.stream().map(this::buildNodes).collect(Collectors.toList());
        graphView.getNodes().addAll(nodesList);
        graphView.getEdges().addAll(buildEdges(nodesList));
        return graphView;
    }

    public List<GraphView.Edges> buildEdges(List<GraphView.Nodes> nodes) {
        List<GraphView.Edges> edgesList = new ArrayList<>();
        SortedMap<String, GraphView.Nodes> nodeMap = nodes.stream().collect(Collectors.toMap(GraphView.Nodes::getId, v -> v, (v1, v2) -> v2, TreeMap::new));
        GraphView.Nodes preNode;
        for (Map.Entry<String, GraphView.Nodes> nodesEntry : nodeMap.entrySet()) {
            GraphView.Edges edges = new GraphView.Edges();
            String key = nodesEntry.getKey();
            preNode = nodeMap.get(key.substring(key.indexOf(".")));
            edges.setTo(preNode.getId());
            edges.setFrom(nodesEntry.getValue().getId());
            edges.setLabel(nodesEntry.getValue().getTitle());
            edgesList.add(edges);
        }
        return edgesList;
    }


    public GraphView.Nodes buildNodes(Object o) {
        GraphView.Nodes node = new GraphView.Nodes();
        if (o instanceof HttpRequestInfo) {
            HttpRequestInfo requestInfo = (HttpRequestInfo) o;
            node.setId(requestInfo.spanId);
            node.setTitle("http");
            node.setData(requestInfo);
        } else if (o instanceof SqlInfo) {
            SqlInfo sqlInfo = (SqlInfo) o;
            node.setId(sqlInfo.spanId);
            node.setTitle("sql");
            node.setData(sqlInfo);
        }
        return node;

    }

}
