package com.mobzheng.mapstruct;

import com.mobzheng.model.HttpRequestInfo;
import com.mobzheng.model.TraceNode;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface HttpRequestInfoMapStruct {
    HttpRequestInfoMapStruct INSTANCE = Mappers.getMapper(HttpRequestInfoMapStruct.class);


    @Mappings({
            @Mapping(target = "begin", expression = "java(com.mobzheng.utils.DateUtil.dateToTimestamp(traceNode.getBegin()))")
    })
    HttpRequestInfo traceNode2HttpRequestInfo(TraceNode traceNode);

}
