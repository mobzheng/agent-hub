package com.mobzheng.trace.collector.handler.proxy;

import com.mobzheng.trace.AgentSession;
import com.mobzheng.trace.log.Log;
import com.mobzheng.trace.log.LogFactory;
import com.mobzheng.trace.model.HttpRequestInfo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.security.Permission;
import java.util.List;
import java.util.Map;

public class URLProxy extends HttpURLConnection {

    static final Log logger = LogFactory.getLog(HttpURLConnection.class);

    HttpURLConnection urlConnection;

    /**
     * Constructor for the HttpURLConnection.
     *
     * @param u             the URL
     * @param urlConnection
     */
    public URLProxy(URL u, HttpURLConnection urlConnection) {
        super(u);
        this.urlConnection = urlConnection;
        AgentSession session = AgentSession.get();
        if (session != null) {
            addRequestProperty(AgentSession.TRACE_ID_KEY, session.getTraceId());
            addRequestProperty(AgentSession.PARENT_ID_KEY, session.nextSpanId());
        }
    }

    @Override
    public void disconnect() {
        urlConnection.disconnect();
    }

    @Override
    public boolean usingProxy() {
        return urlConnection.usingProxy();
    }

    @Override
    public void connect() throws IOException {
        logger.debug("begin connect：" + urlConnection.getURL());
        HttpRequestInfo info = new HttpRequestInfo();
        long begin = System.nanoTime();
        info.begin = begin;
        urlConnection.connect();
        info.end = System.nanoTime();
        info.useTime = info.end - info.begin;
        AgentSession session = AgentSession.get();
        if (session != null) {
            info.traceId = session.getTraceId();
            info.spanId = session.getParentId();
            session.put(info);
        }
        logger.debug("connect cost time ：" + info.useTime);
    }

    @Override
    public String getHeaderFieldKey(int n) {
        return urlConnection.getHeaderFieldKey(n);
    }

    @Override
    public void setFixedLengthStreamingMode(int contentLength) {
        urlConnection.setFixedLengthStreamingMode(contentLength);
    }

    @Override
    public void setFixedLengthStreamingMode(long contentLength) {
        urlConnection.setFixedLengthStreamingMode(contentLength);
    }

    @Override
    public void setChunkedStreamingMode(int chunklen) {
        urlConnection.setChunkedStreamingMode(chunklen);
    }

    @Override
    public String getHeaderField(int n) {
        return urlConnection.getHeaderField(n);
    }

    @Override
    public void setInstanceFollowRedirects(boolean followRedirects) {
        urlConnection.setInstanceFollowRedirects(followRedirects);
    }

    @Override
    public boolean getInstanceFollowRedirects() {
        return urlConnection.getInstanceFollowRedirects();
    }

    @Override
    public void setRequestMethod(String method) throws ProtocolException {
        urlConnection.setRequestMethod(method);
    }

    @Override
    public String getRequestMethod() {
        return urlConnection.getRequestMethod();
    }

    @Override
    public int getResponseCode() throws IOException {
        return urlConnection.getResponseCode();
    }

    @Override
    public String getResponseMessage() throws IOException {
        return urlConnection.getResponseMessage();
    }

    @Override
    public long getHeaderFieldDate(String name, long Default) {
        return urlConnection.getHeaderFieldDate(name, Default);
    }

    @Override
    public Permission getPermission() throws IOException {
        return urlConnection.getPermission();
    }

    @Override
    public InputStream getErrorStream() {
        return urlConnection.getErrorStream();
    }

    @Override
    public void setConnectTimeout(int timeout) {
        urlConnection.setConnectTimeout(timeout);
    }

    @Override
    public int getConnectTimeout() {
        return urlConnection.getConnectTimeout();
    }

    @Override
    public void setReadTimeout(int timeout) {
        urlConnection.setReadTimeout(timeout);
    }

    @Override
    public int getReadTimeout() {
        return urlConnection.getReadTimeout();
    }

    @Override
    public URL getURL() {
        return urlConnection.getURL();
    }

    @Override
    public int getContentLength() {
        return urlConnection.getContentLength();
    }

    @Override
    public long getContentLengthLong() {
        return urlConnection.getContentLengthLong();
    }

    @Override
    public String getContentType() {
        return urlConnection.getContentType();
    }

    @Override
    public String getContentEncoding() {
        return urlConnection.getContentEncoding();
    }

    @Override
    public long getExpiration() {
        return urlConnection.getExpiration();
    }

    @Override
    public long getDate() {
        return urlConnection.getDate();
    }

    @Override
    public long getLastModified() {
        return urlConnection.getLastModified();
    }

    @Override
    public String getHeaderField(String name) {
        return urlConnection.getHeaderField(name);
    }

    @Override
    public Map<String, List<String>> getHeaderFields() {
        return urlConnection.getHeaderFields();
    }

    @Override
    public int getHeaderFieldInt(String name, int Default) {
        return urlConnection.getHeaderFieldInt(name, Default);
    }

    @Override
    public long getHeaderFieldLong(String name, long Default) {
        return urlConnection.getHeaderFieldLong(name, Default);
    }

    @Override
    public Object getContent() throws IOException {
        return urlConnection.getContent();
    }

    @Override
    public Object getContent(Class[] classes) throws IOException {
        return urlConnection.getContent(classes);
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return urlConnection.getInputStream();
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return urlConnection.getOutputStream();
    }

    @Override
    public String toString() {
        return urlConnection.toString();
    }

    @Override
    public void setDoInput(boolean doinput) {
        urlConnection.setDoInput(doinput);
    }

    @Override
    public boolean getDoInput() {
        return urlConnection.getDoInput();
    }

    @Override
    public void setDoOutput(boolean dooutput) {
        urlConnection.setDoOutput(dooutput);
    }

    @Override
    public boolean getDoOutput() {
        return urlConnection.getDoOutput();
    }

    @Override
    public void setAllowUserInteraction(boolean allowuserinteraction) {
        urlConnection.setAllowUserInteraction(allowuserinteraction);
    }

    @Override
    public boolean getAllowUserInteraction() {
        return urlConnection.getAllowUserInteraction();
    }

    @Override
    public void setUseCaches(boolean usecaches) {
        urlConnection.setUseCaches(usecaches);
    }

    @Override
    public boolean getUseCaches() {
        return urlConnection.getUseCaches();
    }

    @Override
    public void setIfModifiedSince(long ifmodifiedsince) {
        urlConnection.setIfModifiedSince(ifmodifiedsince);
    }

    @Override
    public long getIfModifiedSince() {
        return urlConnection.getIfModifiedSince();
    }

    @Override
    public boolean getDefaultUseCaches() {
        return urlConnection.getDefaultUseCaches();
    }

    @Override
    public void setDefaultUseCaches(boolean defaultusecaches) {
        urlConnection.setDefaultUseCaches(defaultusecaches);
    }

    @Override
    public void setRequestProperty(String key, String value) {
        urlConnection.setRequestProperty(key, value);
    }

    @Override
    public void addRequestProperty(String key, String value) {
        urlConnection.addRequestProperty(key, value);
    }

    @Override
    public String getRequestProperty(String key) {
        return urlConnection.getRequestProperty(key);
    }

    @Override
    public Map<String, List<String>> getRequestProperties() {
        return urlConnection.getRequestProperties();
    }


}
