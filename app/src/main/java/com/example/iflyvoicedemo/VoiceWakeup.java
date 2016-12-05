package com.example.iflyvoicedemo;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechEvent;
import com.iflytek.cloud.VoiceWakeuper;
import com.iflytek.cloud.WakeuperListener;
import com.iflytek.cloud.WakeuperResult;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2016/12/5.
 */

public class VoiceWakeup {
    private static String TAG = "wumin VoiceWakeup";
    private Context mContext = null;
    private VoiceWakeuper mIvw = null;
    private static  final int curThresh = 5;

    public VoiceWakeup(Context context) {
        mContext = context;
    }

    private WakeuperListener mWakeuperListener = new WakeuperListener() {
        public void onResult(WakeuperResult result) {
            String text = result.getResultString();
            JSONObject object = null;
            try {
                object = new JSONObject(text);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            StringBuffer buffer = new StringBuffer();
            buffer.append("【RAW】 " + text);
            buffer.append("\n");
            buffer.append("【操作类型】" + object.optString("sst"));
            buffer.append("\n");
            buffer.append("【唤醒词id】" + object.optString("id"));
            buffer.append("\n");
            buffer.append("【得分】" + object.optString("score"));
            buffer.append("\n");
            buffer.append("【前端点】" + object.optString("bos"));
            buffer.append("\n");
            buffer.append("【尾端点】" + object.optString("eos"));
            String resultString = buffer.toString();
            Log.i(TAG, "WakeuperListener result : "+resultString);
        }
        public void onError(SpeechError error) {
            Log.i(TAG, "WakeuperListener error");
        }
        public void onBeginOfSpeech() {
            Log.i(TAG, "WakeuperListener error");
        }
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            if (SpeechEvent.EVENT_IVW_RESULT == eventType) {
                //当使用唤醒+识别功能时获取识别结果
                //arg1:是否最后一个结果，1:是，0:否。
                RecognizerResult reslut = ((RecognizerResult)obj.get(SpeechEvent.KEY_EVENT_IVW_RESULT));
                Log.i(TAG, "Recognize result : "+reslut.getResultString());
            }
        }

        @Override
        public void onVolumeChanged(int i) {

        }
    };
    public void startWakeup() {
        Log.i(TAG,"Start wakeupListener");
        String wake_res = "ivw/" + mContext.getString(R.string.appid) + ".jet";
        mIvw = VoiceWakeuper.createWakeuper(mContext, null);
        mIvw.setParameter(SpeechConstant.PARAMS, null);
        mIvw.setParameter(SpeechConstant.IVW_THRESHOLD,"0:"+curThresh);
        mIvw.setParameter(SpeechConstant.IVW_SST,"wakeup");
        mIvw.setParameter(SpeechConstant.KEEP_ALIVE,"1");
        mIvw.setParameter(SpeechConstant.IVW_NET_MODE, "0");
        mIvw.setParameter(SpeechConstant.IVW_RES_PATH, VoiceUtils.getWakeupResource(mContext, wake_res));

        mIvw.startListening(mWakeuperListener);
    }

    public void stopWakeup() {
        mIvw.stopListening();
    }

}
