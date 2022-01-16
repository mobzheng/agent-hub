package com.mobzheng.model;


import com.mobzheng.mapstruct.HttpRequestInfoMapStruct;
import org.mapstruct.factory.Mappers;

public class HttpRequestInfo extends BaseNode {


    final static HttpRequestInfoMapStruct INSTANCE = Mappers.getMapper(HttpRequestInfoMapStruct.class);

    public String url;
    public String clientIp;
    public String error;

    public String httpParams;
    public String code;
    public String codeStack;


    public HttpRequestInfo() {
        super.modelType = "httpRequest";
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getHttpParams() {
        return httpParams;
    }

    public void setHttpParams(String httpParams) {
        this.httpParams = httpParams;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCodeStack() {
        return codeStack;
    }

    public void setCodeStack(String codeStack) {
        this.codeStack = codeStack;
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
        sb.append(",\"url\":\"")
                .append(url).append('\"');
        sb.append(",\"clientIp\":\"")
                .append(clientIp).append('\"');
        sb.append(",\"error\":\"")
                .append(error).append('\"');
        sb.append(",\"httpParams\":\"")
                .append(httpParams).append('\"');
        sb.append(",\"code\":\"")
                .append(code).append('\"');
        sb.append(",\"codeStack\":\"")
                .append(codeStack).append('\"');
        sb.append('}');
        return sb.toString();
    }


    public static HttpRequestInfo buildRequest(TraceNode traceNode) {
        if (traceNode == null) {
            return null;
        }
        if (!("httpInfo".equals(traceNode.getModelType()))) {
            return null;
        }

        return INSTANCE.traceNode2HttpRequestInfo(traceNode);
    }
}
