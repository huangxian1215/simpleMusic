package com.example.simplemusic.tool;

public class HttpReqData {
    public String url;
    public String cookie;
    public String referer;
    public String content_type;
    public String x_requested_with;
    public StringBuffer params;
    public String charset;
    public String boundary;
    public String host;

    public HttpReqData() {
        url = "";
        cookie = "";
        referer = "";
        content_type = "";
        x_requested_with = "";
        params = new StringBuffer();
        charset = "utf-8";
        boundary = "";
        host = "";
    }

    public HttpReqData(String url) {
        this();
        this.url = url;
    }
}
