package com.mobzheng.model;

import com.mobzheng.mapstruct.SqlInfoMapStruct;
import org.mapstruct.factory.Mappers;

public class SqlInfo extends BaseNode {
    final static SqlInfoMapStruct INSTANCE = Mappers.getMapper(SqlInfoMapStruct.class);

    // jdbc url
    public String jdbcUrl;
    // sql 语句
    public String sql;
    // 数据库名称
    public String databaseName;
    // 异常信息
    public String error;


    public SqlInfo() {
        super.modelType = "sql";
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"traceId\":\"")
                .append(traceId).append('\"');
        sb.append(",\"spanId\":\"")
                .append(spanId).append('\"');
        sb.append(",\"begin\":")
                .append(begin);
        sb.append(",\"useTime\":")
                .append(useTime);
        sb.append(",\"end\":")
                .append(end);
        sb.append(",\"appName\":\"")
                .append(appName).append('\"');
        sb.append(",\"host\":\"")
                .append(host).append('\"');
        sb.append(",\"modelType\":\"")
                .append(modelType).append('\"');
        sb.append(",\"jdbcUrl\":\"")
                .append(jdbcUrl).append('\"');
        sb.append(",\"sql\":\"")
                .append(sql).append('\"');
        sb.append(",\"databaseName\":\"")
                .append(databaseName).append('\"');
        sb.append(",\"error\":\"")
                .append(error).append('\"');
        sb.append('}');
        return sb.toString();
    }

    public static SqlInfo buildSqlInfo(TraceNode traceNode)
    {
        if (traceNode==null){
            return null;
        }
        if ("sqlInfo".equals(traceNode.getModelType()))
        {
            return null;
        }
        return INSTANCE.traceNode2HttpRequestInfo(traceNode);
    }
}
