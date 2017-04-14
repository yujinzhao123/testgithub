package com.example.yuyu.gitbubtest.http.base;

import com.alibaba.fastjson.JSON;

import java.io.IOException;

import okhttp3.Response;

/**
 * Created by yujinzhao on 16/10/19.
 */

public class MyResponse {
    public MyResponse(Response res) {
        this.raw = res;
    }

    private Response raw;
    private byte[] data;
    private boolean readed = false;

    public <T> T parseJson(Class<T> t) throws IOException {
        return JSON.parseObject(new String(getBytes()), t);
    }

    public int getStatusCode() {
        return raw.code();
    }

    public byte[] getBytes() throws IOException {
        if (!readed) {
            data = raw.body().bytes();
            readed = true;
        }
        return data;
    }

    public void setBytesProgress(byte[] data) {
        this.data = data;
        readed = true;
    }

    public String getString() throws IOException {
        return new String(getBytes());
    }
}
