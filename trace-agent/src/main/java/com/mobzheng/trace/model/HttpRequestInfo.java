package com.mobzheng.trace.model;

public class HttpRequestInfo extends BaseNode{

    public String url;
    public String clientIp;
    public String error;

    public String httpParams;
    public String code;
    public String codeStack;

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
        return "HttpRequestInfo{" +
                "traceId='" + traceId + '\'' +
                ", spanId='" + spanId + '\'' +
                ", begin=" + begin +
                ", useTime=" + useTime +
                ", end=" + end +
                ", appName='" + appName + '\'' +
                ", host='" + host + '\'' +
                ", modelType='" + modelType + '\'' +
                ", url='" + url + '\'' +
                ", clientIp='" + clientIp + '\'' +
                ", error='" + error + '\'' +
                ", httpParams='" + httpParams + '\'' +
                ", code='" + code + '\'' +
                ", codeStack='" + codeStack + '\'' +
                '}';
    }
}
