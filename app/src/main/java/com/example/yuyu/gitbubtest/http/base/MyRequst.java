package com.example.yuyu.gitbubtest.http.base;

import com.alibaba.fastjson.JSON;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.cookie.store.PersistentCookieStore;
import com.lzy.okgo.request.GetRequest;
import com.lzy.okgo.request.PostRequest;
import com.lzy.okgo.utils.OkLogger;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by yujinzhao on 16/10/19.
 */

public class MyRequst {
    static {
        OkGo.getInstance()
                .setCookieStore(new PersistentCookieStore())
                .setConnectTimeout(5000);
    }

    public MyRequst(String method, String url) {

        if (method.toUpperCase().equals("POST")) {

            postRequest = OkGo.post(url);
        } else {
            getRequest = OkGo.get(url);
        }
    }

    private GetRequest getRequest;
    private PostRequest postRequest;

    public MyRequst header(String key, String value) {
        if (getRequest != null) {
            getRequest.headers(key, value);
            return this;
        }
        postRequest.headers(key, value);
        return this;
    }

    public MyRequst write(byte[] bytes) {
        if (postRequest != null) {
            postRequest.upBytes(bytes);
        }
        return this;
    }

    public MyRequst write(String string) {
//        write(string.getBytes());
        if (postRequest != null) {
            postRequest.upString(string);
        }
        return this;
    }

    public MyRequst writeJson(Object o) {
//        write(JSON.toJSONBytes(o));
        if (postRequest != null) {
            postRequest.upJson(JSON.toJSONString(o));
        }
        return this;
    }

    public MyRequst writeForm(Map<String, String> data) {

//        StringBuilder builder = new StringBuilder();
//        boolean first = true;
//        for (Map.Entry<String, String> entry : data.entrySet()) {
//            if (first) {
//                builder.append(entry.getKey() + "=" + entry.getValue());
//                first = false;
//            } else {
//                builder.append("&" + entry.getKey() + "=" + entry.getValue());
//            }
//        }
//
//        write(builder.toString());
//        headers("Content-Type", "application/x-www-form-urlencoded");
        if (postRequest != null) {
            postRequest.params(data);
        }
        return this;
    }

    public MyRequst writeFile(String name, List<File> files) {
        if (postRequest != null) {
            postRequest.addFileParams(name, files);
        }
        return this;
    }

    public void cancel(Object o) {
        OkGo.getInstance().cancelTag(o);
    }

    public void fire(Object tag, final MyCallback callback) {
        AbsCallback<MyResponse> absCallback = new AbsCallback<MyResponse>() {
            MyResponse myResponse;

            @Override
            public void onSuccess(MyResponse o, Call call, Response response) {
                try {
                    callback.onSuccess(o);
                } catch (Exception e) {
                    callback.onError(e);
                } finally {
                    callback.always();
                }
            }

            @Override
            public MyResponse convertSuccess(Response response) throws Exception {
                myResponse = new MyResponse(response);
//                    myResponse.getBytes();

                ByteArrayOutputStream os = new ByteArrayOutputStream();

                long lastRefreshUiTime = 0;  //最后一次刷新的时间
                long lastWriteBytes = 0;     //最后一次写入字节数据

                InputStream is = null;
                byte[] buf = new byte[2048];
//                    FileOutputStream fos = null;
                try {
                    is = response.body().byteStream();
                    final long total = response.body().contentLength();
                    long sum = 0;
                    int len;
//                        fos = new FileOutputStream(file);
                    while ((len = is.read(buf)) != -1) {
                        sum += len;
//                            fos.write(buf, 0, len);
                        os.write(buf, 0, len);

                        //下载进度回调
                        if (callback != null) {
                            final long finalSum = sum;
                            long curTime = System.currentTimeMillis();
                            //每200毫秒刷新一次数据
                            if (curTime - lastRefreshUiTime >= 200 || finalSum == total) {
                                //计算下载速度
                                long diffTime = (curTime - lastRefreshUiTime) / 1000;
                                if (diffTime == 0) diffTime += 1;
                                long diffBytes = finalSum - lastWriteBytes;
                                final long networkSpeed = diffBytes / diffTime;
                                OkGo.getInstance().getDelivery().post(new Runnable() {
                                    @Override
                                    public void run() {
                                        callback.downloadProgress(finalSum, total, finalSum * 1.0f / total, networkSpeed);   //进度回调的方法
                                    }
                                });

                                lastRefreshUiTime = System.currentTimeMillis();
                                lastWriteBytes = finalSum;
                            }
                        }
                    }
//                        fos.flush();
                    os.flush();
//                        return file;
                    myResponse.setBytesProgress(os.toByteArray());

                } finally {
                    try {
                        if (is != null) is.close();
                    } catch (IOException e) {
                        OkLogger.e(e);
                        if (callback != null) {
                            callback.always();
                        }
                    }

                    try {
//                            if (fos != null) fos.close();
                        os.close();
                    } catch (IOException e) {
                        OkLogger.e(e);
                        if (callback != null) {
                            callback.always();
                        }
                    }

                }
                return myResponse;
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                if (response != null) {
                    try {
                        if (myResponse == null) {
                            myResponse = new MyResponse(response);
                        }
                        callback.onFail(myResponse);
                    } catch (Exception e1) {
                        callback.onError(e1);
                    } finally {
                        callback.always();
                    }
                    return;
                }
                callback.onError(e);
                callback.always();
            }

            @Override
            public void upProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                callback.upProgress(currentSize, totalSize, progress, networkSpeed);
            }

            @Override
            public void downloadProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                callback.downloadProgress(currentSize, totalSize, progress, networkSpeed);
            }
        };


        if (getRequest != null) {
            if (tag != null) {
                getRequest.tag(tag);
            }
            getRequest.execute(absCallback);
            return;
        }
        if (tag != null) {
            postRequest.tag(tag);
        }
        postRequest.execute(absCallback);
    }


}
