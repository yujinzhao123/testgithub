package com.example.yuyu.gitbubtest.http;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.example.yuyu.gitbubtest.http.base.MyCallback;
import com.example.yuyu.gitbubtest.http.base.MyRequst;
import com.example.yuyu.gitbubtest.http.base.MyResponse;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;

/**
 * Created by yujinzhao on 17/2/20.
 */

public class Http2request {
    private static String TAG = "Http2Request";
    public Context context;

    public Http2request(Context context) {
        this.context = context;
    }


    public <T> void loadDataPost(final String url, final HashMap<String, String> map, final Http2Interface httpInterface) {
        Log.d(TAG, "doPost()=》url:" + url + ",\n请求参数:" + map.toString());
        new MyRequst("POST", url).writeForm(map).fire(context, new MyCallback() {
            @Override
            public void onSuccess(MyResponse response) throws Exception {
                Log.d(TAG, "doPostJson()=》\nresponse!====>" + response.getString());
                httpInterface.ok(response.getString());
            }

            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onFailure");
                httpInterface.error(-1, "");

            }

            @Override
            public void onError(Exception e) {
                super.onError(e);
                httpInterface.error(-1, "网络连接失败，请检查网络!!!");
                Log.e(TAG, "onError");
            }

            @Override
            public void onFail(MyResponse response) throws Exception {
                super.onFail(response);
                Log.e(TAG, "onFail");
                httpInterface.error(response.getStatusCode(), response.getString());
                Log.e(TAG, "错误码:" + response.getStatusCode() + "\n错误信息:" + response.getString());
            }
        });
    }

    public <T> void doPostJson(final Object o, final String url, final Http2Interface httpInterface) {
        Log.d(TAG, "doPostJson()=》url:" + url + ",\n请求参数:" + JSON.toJSONString(o));

        new MyRequst("POST", url).writeJson(o).fire(context, new MyCallback() {
            @Override
            public void onSuccess(MyResponse response) throws Exception {
                Log.d(TAG, "doPostJson()=》\nresponse!====>" + response.getString());
                httpInterface.ok(response.getString());

            }

            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onFailure");
                httpInterface.error(-1, "");

            }

            @Override
            public void onError(Exception e) {
                super.onError(e);
                httpInterface.error(-1, "网络连接失败，请检查网络!!!");
                Log.e(TAG, "onError");
            }

            @Override
            public void onFail(MyResponse response) throws Exception {
                super.onFail(response);
                Log.e(TAG, "onFail");
                httpInterface.error(response.getStatusCode(), response.getString());
                Log.e(TAG, "错误码:" + response.getStatusCode() + "\n错误信息:" + response.getString());
            }
        });
    }

    public <T> void loadDataGet(String url, final Http2Interface httpInterface) {
        Log.d(TAG, "doGet()=》url:" + url);
        new MyRequst("GET", url).fire(context, new MyCallback() {
            @Override
            public void onSuccess(MyResponse response) throws Exception {
                Log.d(TAG, "response!====>" + response.getStatusCode() + response.getString());
                httpInterface.ok(response.getString());

            }

            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onFailure");
                httpInterface.error(-1, "");

            }

            @Override
            public void onError(Exception e) {
                super.onError(e);
                httpInterface.error(-1, "网络连接失败，请检查网络!!!");
                Log.e(TAG, "onError");
            }

            @Override
            public void onFail(MyResponse response) throws Exception {
                super.onFail(response);
                Log.e(TAG, "onFail");
//                Log.e(TAG,"错误码："+response.getStatusCode());
//                Log.e(TAG, "错误码:" + response.getStatusCode() + "\n错误信息:" + response.getString());
                httpInterface.error(response.getStatusCode(), response.getString());


            }
        });
    }
    public abstract class Http2Interface {
        public abstract void ok(String string);
        public void error(int code,String msg){
        }

    }

}

