package com.example.yuyu.gitbubtest.http;

import android.util.Log;


import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by yujinzhao on 16/12/1.
 */

public class HttpJsonBean<T> {
    private String content;
    private Class<T> t;

    public HttpJsonBean(String content, Class<T> t) {
        this.content = content;
        this.t = t;
    }

    public T getBean() {
        T bean = null;
        try {
            bean = JSON.parseObject(content, t);
        } catch (Exception e) {
            Log.e("yjz", "erro");
        }
        return bean;
    }

    public List<T> getBeanList() {
        try {
            return JSON.parseArray(content, t);
        } catch (Exception e) {
            Log.e("yjz", "erro");
        }
        return new ArrayList<T>();
    }
}
