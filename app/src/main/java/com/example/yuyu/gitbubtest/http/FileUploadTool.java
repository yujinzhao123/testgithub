package com.example.yuyu.gitbubtest.http;

import android.app.Activity;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.utils.EncryptUtils;
import com.blankj.utilcode.utils.LogUtils;
import com.example.yuyu.gitbubtest.beans.KeyBean;
import com.example.yuyu.gitbubtest.http.base.MyCallback;
import com.example.yuyu.gitbubtest.http.base.MyRequst;
import com.example.yuyu.gitbubtest.http.base.MyResponse;
import com.example.yuyu.gitbubtest.tools.Const;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


/**
 * Created by yujinzhao on 16/10/27.
 */

public class FileUploadTool extends HttpBaseHelper {

    public final static String FILE_UP_BASE_URL = "https://storage.duliday.com/";


    /**
     * 查询文件是否存在
     *
     * @param key
     */
    public static void query(String key, Activity activity, final QueryCallback query) {

        MyRequst myRequst = new MyRequst("GET", FILE_UP_BASE_URL + "stat?key=" + key);
        myRequst.fire(activity, new MyCallback() {
            @Override
            public void onSuccess(MyResponse response) throws Exception {
                LogUtils.e("yjz", "query()"+response.getString());
                query.have();
            }

            @Override
            public void onFail(MyResponse response) throws Exception {
                LogUtils.e("yjz", response.getString());
                LogUtils.e("yjz", response.getStatusCode());
                if (response.getStatusCode() == 404) {
                    query.notHave();
                } else {
                    query.erro();
                }
            }

            @Override
            public void onError(Exception e) {
                LogUtils.e("yjz", "eeeee");
                query.erro();
            }
        });


    }


    /**
     * 获取 文件路径（图片等）
     * @param hash MD5的值
     * @param width 图片的画宽高
     * @param height
     * @return
     */
    public String getFileURL(String hash, int width, int height, int ...scale) {
        int scale1 = 2;
        if (scale.length > 0) {
            scale1 = scale[0];
        }
        CodeTimestamp condeTime = getCodeTimestamp();
        Map<String, String> map = new HashMap<>();
        map.put("key", hash);
        map.put("timestamp", condeTime.Timestamp);
        map.put("code", condeTime.code);
        String filter = getFilter(width, height, scale1);
        if (filter != null) {
            map.put("filter", filter);
        }
        return getUrl(FILE_UP_BASE_URL + "get", map);
    }

    /**
     * 获取过滤器字符串
     *
     * @param width
     * @param height
     * @return
     */
    private String getFilter(int width, int height, int scale) {
        if (width < 1 && height < 1) {
            return null;
        }
        String str = Const.FILTER_NAME + "/auto-orient/thumbnail/!";
        int w = width * scale;
        int h = height * scale;
        str += "" + w;
        str += "x" + h + "r/gravity/Center/crop/" + w + "x" + h;
        return str;
    }

    public static void upload(File file, Activity activity, MyCallback myCallback) {
        List<File> files = new ArrayList<>();
        files.add(file);
        MyRequst myRequst = new MyRequst("POST", "https://storage.duliday.com/put").writeFile("file", files);
        myRequst.fire(activity, myCallback);
    }

    public static Observable<Boolean> rxQuery(String key, Activity activity) {
//        query(key, activity, new QueryCallback() {
//            @Override
//            public void have() {
//
//            }
//
//            @Override
//            public void notHave() {
//
//            }
//
//            @Override
//            public void erro() {
//
//            }
//        });


        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                subscriber.onNext(true);
            }
        });
    }

    public static Observable<String> test(final Activity activity, final File file, final FileProgress fileProgress) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(final Subscriber<? super String> subscriber) {
                final String md5 = EncryptUtils.encryptMD5File2String(file);
                query(md5, activity, new QueryCallback() {
                    @Override
                    public void have() {
                        subscriber.onNext(md5);
                        fileProgress.progress(1);

                    }
                    @Override
                    public void notHave() {
                        subscriber.onNext(null);
                    }

                    @Override
                    public void erro() {
                        subscriber.onError(new Exception("null"));
                    }
                });
            }
        }).flatMap(new Func1<String, Observable<String>>() {
            @Override
            public Observable<String> call(final String md5) {

                return Observable.create(new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(final Subscriber<? super String> subscriber) {
                        if (md5 != null) {
                            subscriber.onNext(md5);
                            return;
                        }
                        upload(file, activity, new MyCallback() {
                            @Override
                            public void onSuccess(MyResponse response) throws Exception {
                                KeyBean keyBean= JSON.parseObject(response.getString(), KeyBean.class);
                                subscriber.onNext(keyBean.getKey());
                            }

                            @Override
                            public void onFail(MyResponse response) throws Exception {
                                subscriber.onError(null);
                            }

                            @Override
                            public void onError(Exception e) {
                                subscriber.onError(new Exception());
                            }
                            @Override
                            public void upProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                                Log.e("yjz", progress + "%上传");
                               fileProgress.progress(progress);
                            }
                        });
                    }
                });
            }
        }).
                subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());




//                myHello().subscribe(new Action1<Object>() {
//                    @Override
//                    public void call(Object s) {
//
//                    }
//                });

    }

    public interface QueryCallback {
        public void have();

        public void notHave();

        public void erro();
    }


    public static Observable<Object> myHello() {

        return Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(final Subscriber<? super Object> subscriber) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        subscriber.onNext(null);
                    }
                }).start();
            }
        });
    }
    public interface FileProgress {
        public void progress(float value);
    }

}
