package com.mobzheng.controller;

import com.mobzheng.dao.TraceRepository;
import com.mobzheng.model.GraphView;
import com.mobzheng.model.TraceNode;
import com.mobzheng.service.TraceNode2Info;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("graph")
public class GraphController {

    @Autowired
    private TraceRepository traceRepository;


    @Autowired
    TraceNode2Info traceNode2Info;

    @RequestMapping("detail/{traceId}")
    @ResponseBody
    public GraphView getGraph(@PathVariable("traceId") String traceId) {
        List<TraceNode> traceNodes = traceRepository.findByTraceId(traceId);
        List<Object> objects = traceNode2Info.buildTraceNode(traceNodes);
        return traceNode2Info.buildView(objects);
    }


    @RequestMapping("open")
    public String openGraph() {
        return "trace_view";
    }
}
