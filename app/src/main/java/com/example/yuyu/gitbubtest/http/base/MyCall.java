package com.example.yuyu.gitbubtest.http.base;

import okhttp3.Call;
import okhttp3.Callback;

/**
 * Created by yujinzhao on 16/10/19.
 *
 */

public class MyCall {
    public MyCall(Call call) {
        this.call = call;
    }

    private Call call;

    public MyCall enqueue(Callback callback) {
        call.enqueue(callback);
        return this;
    }

    public MyCall cancel() {
        call.cancel();
        return this;
    }
}
