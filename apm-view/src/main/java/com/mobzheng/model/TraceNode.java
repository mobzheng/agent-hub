package com.mobzheng.model;

import lombok.Data;

import java.util.Date;


@Data
public class TraceNode {
    private String id;

    private String traceId;

    private String spanId;

    private Date begin;

    private long useTime;

    private long end;

    private String appName;

    private String host;

    private String modelType;// http  sql service

    private String remoteIp;

    private String remoteUrl;

    private String clientIp;

    private String serviceInterface;

    private String serviceMethodName;

    private String error;

    private String seat;//所在地：client|server

    private String url;

    private String errorMsg;

    private String errorType;

    private String serviceName; //服务名称

    private String simpleName; //服务简称

    private String methodName; //方法名称

    private String jdbcUrl;

    private String sql;

    private String databaseName;

    private String version;

    private String httpParams;

    private String code;

    private String inParam;

    private String outParam;

}
