package com.example.yuyu.gitbubtest.http.base;

import java.io.File;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by yujinzhao on 16/10/18.
 */

public class OkHttpUtils {

    public static final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown; charset=utf-8");
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private OkHttpClient.Builder okHttpClientBuilder;           //ok请求的客户端
    private static OkHttpUtils okHttpUtils;
    private OkHttpClient okHttpClient;

    private OkHttpUtils() {
        okHttpClientBuilder = new OkHttpClient.Builder();

    }

    public static OkHttpUtils getInstance() {
        if (okHttpUtils == null) {
            synchronized (OkHttpUtils.class) {
                if (okHttpUtils == null) {
                    okHttpUtils = new OkHttpUtils();
                }
            }
        }
        return okHttpUtils;
    }

    public MyCall get(String url) {
        Request.Builder requestBuilder = new Request.Builder().url(url);
        //可以省略，默认是GET请求
        requestBuilder.method("GET", null);
        Request request = requestBuilder.build();
        Call mCall = okHttpClientBuilder.build().newCall(request);
        return new MyCall(mCall);
    }

    public Call post(FormBody formBody, String url) {
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();
        Call mCall = okHttpClientBuilder.build().newCall(request);
        return mCall;
    }

    public Call postJson(String url, String json) {
        RequestBody requestBody = RequestBody.create(JSON, json);
        Request request = new Request.Builder().url(url).post(requestBody).build();
        return okHttpClientBuilder.build().newCall(request);
    }

    public Call postFile(String url, File file){
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(MEDIA_TYPE_MARKDOWN, file))
                .build();
        return okHttpClientBuilder.build().newCall(request);
    }
}
