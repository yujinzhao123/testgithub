package com.example.yuyu.gitbubtest.http.base;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by yujinzhao on 16/10/19.
 *
 */

public abstract class MyCallback implements Callback {
    @Override
    public void onFailure(Call call, IOException e) {
        onError(e);
    }

    @Override
    public void onResponse(Call call, Response response) {
        try {
             MyResponse res = new MyResponse(response);
            if (response.code() != 200) {
                onFail(res);
                always0();
                return;
            }
            onSuccess(res);
            always0();
        } catch (Exception e) {
            onError(e);
            always0();
        }
    }

    public void onError(Exception e) {
        // TODO: handle error
        e.printStackTrace();
    }

    public abstract void onSuccess(MyResponse response) throws Exception;

    public void onFail(MyResponse response) throws Exception {
        // TODO: handle fail
    }

    private boolean alwaysDone = false;
    private void always0() {
        if (alwaysDone) {
            return;
        }
        always();
    }
    public void always() {
        // TODO:
    }

    public void upProgress(long currentSize, long totalSize, float progress, long networkSpeed) {

    }

    public void downloadProgress(long currentSize, long totalSize, float progress, long networkSpeed) {

    }
}
