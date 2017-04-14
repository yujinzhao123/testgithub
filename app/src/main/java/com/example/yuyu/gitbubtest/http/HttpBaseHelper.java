package com.example.yuyu.gitbubtest.http;

import com.blankj.utilcode.utils.EncryptUtils;
import com.example.yuyu.gitbubtest.tools.Const;

import java.util.Map;

/**
 * Created by yujinzhao on 16/10/27.
 */

public class HttpBaseHelper {

    public CodeTimestamp getCodeTimestamp(){
        CodeTimestamp codeTimestamp = new CodeTimestamp();
        String timestamp = System.currentTimeMillis() / 1000 +"";
        String code = Const.SEED + timestamp;
        code = EncryptUtils.encryptMD5ToString(code);
        codeTimestamp.code  = code;
        codeTimestamp.Timestamp = timestamp;
        return codeTimestamp;
    }

    public String getUrl(String url, Map<String,String> data){
        StringBuilder builder = new StringBuilder();
        builder.append(url+"?");
        boolean first = true;
        for (Map.Entry<String, String> entry : data.entrySet()) {
            if (first) {
                builder.append(entry.getKey() + "=" + entry.getValue());
                first = false;
            } else {
                builder.append("&" + entry.getKey() + "=" + entry.getValue());
            }
        }

        return builder.toString();
    }

    public class CodeTimestamp{
        public String code;
        public String Timestamp;
    }

}
