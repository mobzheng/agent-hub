package com.mobzheng.mapstruct;

import com.mobzheng.model.SqlInfo;
import com.mobzheng.model.TraceNode;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface SqlInfoMapStruct {
    SqlInfoMapStruct INSTANCE = Mappers.getMapper(SqlInfoMapStruct.class);


    @Mappings({
            @Mapping(target = "begin", expression = "java(com.mobzheng.utils.DateUtil.dateToTimestamp(traceNode.getBegin()))")
    })
    SqlInfo traceNode2HttpRequestInfo(TraceNode traceNode);

}
