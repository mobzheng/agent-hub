package com.mobzheng.dao;

import com.mobzheng.model.TraceNode;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface TraceRepository extends ElasticsearchRepository<TraceNode, String> {

    List<TraceNode> findByTraceId(String traceId);
}
