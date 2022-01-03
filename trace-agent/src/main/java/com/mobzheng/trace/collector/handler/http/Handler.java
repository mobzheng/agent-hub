package com.mobzheng.trace.collector.handler.http;

import com.mobzheng.trace.collector.handler.proxy.URLProxy;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;

public class Handler extends sun.net.www.protocol.http.Handler {
    @Override
    protected URLConnection openConnection(URL u) throws IOException {
        return openConnection(u, null);
    }

    @Override
    protected URLConnection openConnection(URL u, Proxy p) throws IOException {
        URLConnection urlConnection = super.openConnection(u, p);
        return new URLProxy(u, ((HttpURLConnection) urlConnection));
    }
}
