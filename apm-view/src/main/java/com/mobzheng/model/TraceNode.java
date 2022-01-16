package com.mobzheng.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;


@Data
@Document(indexName = "apm", type = "doc")
public class TraceNode {
    @Id
    private String docId;
    @Field(type = FieldType.Auto)
    private String traceId;
    @Field(type = FieldType.Auto)
    private String spanId;
    @Field(type = FieldType.Date)
    private Date begin;
    @Field(type = FieldType.Auto)
    private long useTime;
    @Field(type = FieldType.Auto)
    private long end;
    @Field(type = FieldType.Auto)
    private String appName;
    @Field(type = FieldType.Auto)
    private String host;
    @Field(type = FieldType.Auto)
    private String modelType;// http  sql service
    @Field(type = FieldType.Auto)
    private String remoteIp;
    @Field(type = FieldType.Auto)
    private String remoteUrl;
    @Field(type = FieldType.Auto)
    private String clientIp;
    @Field(type = FieldType.Auto)
    private String serviceInterface;
    @Field(type = FieldType.Auto)
    private String serviceMethodName;
    @Field(type = FieldType.Auto)
    private String error;
    @Field(type = FieldType.Auto)
    private String seat;//所在地：client|server
    @Field(type = FieldType.Auto)
    private String url;
    @Field(type = FieldType.Auto)
    private String errorMsg;
    @Field(type = FieldType.Auto)
    private String errorType;
    @Field(type = FieldType.Auto)
    private String serviceName; //服务名称
    @Field(type = FieldType.Auto)
    private String simpleName; //服务简称
    @Field(type = FieldType.Auto)
    private String methodName; //方法名称
    @Field(type = FieldType.Auto)
    private String jdbcUrl;
    @Field(type = FieldType.Auto)
    private String sql;
    @Field(type = FieldType.Auto)
    private String databaseName;
    @Field(type = FieldType.Auto)
    private String version;
    @Field(type = FieldType.Auto)
    private String httpParams;
    @Field(type = FieldType.Auto)
    private String code;
    @Field(type = FieldType.Auto)
    private String inParam;
    @Field(type = FieldType.Auto)
    private String outParam;

}
