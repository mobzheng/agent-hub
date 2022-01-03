package com.mobzheng.trace.model;

public class SqlInfo extends BaseNode{

    // jdbc url
    public String jdbcUrl;
    // sql 语句
    public String sql;
    // 数据库名称
    public String databaseName;
    // 异常信息
    public String error;

    @Override
    public String toString() {
        return "SqlInfo{" +
                "traceId='" + traceId + '\'' +
                ", spanId='" + spanId + '\'' +
                ", begin=" + begin +
                ", useTime=" + useTime +
                ", end=" + end +
                ", appName='" + appName + '\'' +
                ", host='" + host + '\'' +
                ", modelType='" + modelType + '\'' +
                ", jdbcUrl='" + jdbcUrl + '\'' +
                ", sql='" + sql + '\'' +
                ", databaseName='" + databaseName + '\'' +
                ", error='" + error + '\'' +
                '}';
    }
}
