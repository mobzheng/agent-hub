package com.mobzheng.dao;

import com.mobzheng.model.TraceNode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
@Component
public class TraceRepository {


    @Autowired
    private RestHighLevelClient client;


    public List<TraceNode> findByTraceId(String traceId) {
        SearchRequest searchRequest = new SearchRequest("apm");
        searchRequest.searchType(SearchType.DEFAULT);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.termQuery("traceid", traceId));
        searchRequest.source(searchSourceBuilder);
        SearchResponse response;
        try {
            response = client.search(searchRequest);
        } catch (IOException e) {
            return null;
        }
        if (response.status().equals(RestStatus.OK)) {
            SearchHits hits = response.getHits();
            return Arrays.stream(hits.getHits()).map(SearchHit::getHighlightFields)
                    .map(s -> {
                        try {
                            TraceNode traceNode = TraceNode.class.newInstance();
                            Field[] declaredFields = TraceNode.class.getDeclaredFields();
                            for (Field field : declaredFields) {
                                field.setAccessible(true);
                                field.set(traceNode, s.get(field.getName()));
                            }
                            return traceNode;
                        } catch (InstantiationException | IllegalAccessException ignored) {

                        }
                        return null;
                    }).filter(Objects::nonNull).collect(Collectors.toList());
        }
        return null;
    }
}
