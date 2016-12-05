package com.example.iflyvoicedemo;

import android.content.Context;

import com.iflytek.cloud.util.ResourceUtil;

import java.io.InputStream;

/**
 * Created by Administrator on 2016/12/5.
 */

public class VoiceUtils {
    /**
     * 读取asset目录下文件。
     * @return content
     */
    public static String readFile(Context mContext, String file, String code)
    {
        int len = 0;
        byte []buf = null;
        String result = "";
        try {
            InputStream in = mContext.getAssets().open(file);
            len  = in.available();
            buf = new byte[len];
            in.read(buf, 0, len);

            result = new String(buf,code);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String getWakeupResource(Context mContext, String wake_res) {
        return ResourceUtil.generateResourcePath(mContext, ResourceUtil.RESOURCE_TYPE.assets, wake_res);
    }

}
