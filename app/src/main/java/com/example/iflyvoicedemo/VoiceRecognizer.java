package com.example.iflyvoicedemo;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.LexiconListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

import java.util.logging.MemoryHandler;

/**
 * Created by Administrator on 2016/12/5.
 */

public class VoiceRecognizer {
    private static String TAG = "wumin VoiceRecognizer";
    private Handler mHandler = null;
    private Context mContext = null;
    private RecognizerDialog recognizerDialog;
    private SpeechRecognizer mIat;
    public static final int START_VOICE = 0;
    public static final int START_SPEAK = 1;
    public static final int STOP_SPEAK = 2;
    public static final int START_RECOGNIZE = 3;
    public static final int STOP_RECOGNIZE = 4;
    public static final int RECOGNIZE_ERROR = 5;
    public static final int RECOGNIZE_FINISH = 6;
    public static final int SPEAK_NULL = 7;
    public static final int NET_ERROR = 8;

    public VoiceRecognizer(Context context, Handler handler) {
        mContext = context;
        mHandler = handler;
    }

    private RecognizerDialogListener mRecognizeListener = new RecognizerDialogListener() {
        @Override
        public void onResult(RecognizerResult recognizerResult, boolean b) {
            Log.i(TAG, "Recognize result : "+recognizerResult.getResultString());
            String voiceInfo = JsonParser.parseIatResult(recognizerResult.getResultString());
            if (voiceInfo.compareTo("last") == 0)
                return;
            Toast.makeText(mContext, voiceInfo, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onError(SpeechError speechError) {
            int errCode = speechError.getErrorCode();
            String msg;
            switch (errCode) {
                case 10118:
                    msg = "您未说话，请重试！";
                    break;
                default:
                    msg = "识别错误，请重试！";
                    break;
            }
            Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
        }
    };
    private InitListener mInitListener = new InitListener() {
        @Override
        public void onInit(int i) {
            Log.d(TAG, "SpeechRecognizer init() code = " + i);
            if (i != ErrorCode.SUCCESS) {
                Toast.makeText(mContext, "初始化失败，错误码：" + i, Toast.LENGTH_SHORT).show();
            }
        }
    };

    public void showVoiceRecognizeDialog() {
        recognizerDialog = new RecognizerDialog(mContext, mInitListener);
        recognizerDialog.setParameter(SpeechConstant.DOMAIN, "iat");
        recognizerDialog.setParameter(SpeechConstant.ACCENT, "mandarin");
        recognizerDialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        recognizerDialog.setParameter(SpeechConstant.SAMPLE_RATE, "16000");
        //mAsr.buildGrammar("abnf", mCloudGrammar, grammarListener);
        recognizerDialog.setListener(mRecognizeListener);
        recognizerDialog.show();
    }

    //听写监听器
    private RecognizerListener mRecogListener = new RecognizerListener(){
        public void onResult(RecognizerResult results, boolean isLast) {
            Log.i(TAG, "Recognize result : "+ results.getResultString());
            String voiceInfo = JsonParser.parseIatResult(results.getResultString());
            if (voiceInfo.compareTo("last") == 0)
                return;
            Log.i(TAG, "Recognize => "+ voiceInfo);
            Toast.makeText(mContext, voiceInfo, Toast.LENGTH_LONG).show();
            mHandler.sendEmptyMessage(RECOGNIZE_FINISH);
        }
        //会话发生错误回调接口
        public void onError(SpeechError error) {
            error.getPlainDescription(true); //获取错误码描述
            int errCode = error.getErrorCode();
            String msg;
            int errType = RECOGNIZE_ERROR;
            switch (errCode) {
                case 10118:
                    msg = "您未说话，请重试！";
                    errType = SPEAK_NULL;
                    break;
                case 20001:
                    msg = "网络未连接，请重试！";
                    errType = NET_ERROR;
                    break;
                default:
                    msg = "会话失败，请重试！";
                    break;
            }
            mHandler.sendEmptyMessage(errType);
            Toast.makeText(mContext, msg + "Error code : " + error.getErrorCode(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onVolumeChanged(int i, byte[] bytes) {

        }

        //开始录音
        public void onBeginOfSpeech() {
            Log.i(TAG, "onBeginOfSpeech...");
        }
        //音量值0~30
        public void onVolumeChanged(int volume){}
        //结束录音
        public void onEndOfSpeech() {
            Log.i(TAG, "onEndOfSpeech...");
            mHandler.sendEmptyMessage(START_RECOGNIZE);
        }
        //扩展用接口
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {}
    };
    public void startSpeechRecognizer() {
        Log.i(TAG, "startSpeechRecognizer...");
        mIat= SpeechRecognizer.createRecognizer(mContext, null);
        upload_userword();
        mIat.setParameter(SpeechConstant.DOMAIN, "iat");
        mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        mIat.setParameter(SpeechConstant.ACCENT, "mandarin ");
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        mIat.setParameter(SpeechConstant.TEXT_ENCODING, "utf-8");
        //mIat.buildGrammar("abnf", mCloudGrammar, grammarListener);
        mIat.startListening(mRecogListener);
        mHandler.sendEmptyMessage(START_VOICE);
    }

    public void stopSpeechRecognizer() {
        Log.i(TAG, "stopSpeechRecognizer...");
        if (mIat.isListening()) {
            Log.i(TAG, "isListening...");
            mIat.stopListening();
        }
        mIat.cancel();
    }
    /**
     * 上传联系人/词表监听器。
     */
    private LexiconListener mLexiconListener = new LexiconListener() {
        @Override
        public void onLexiconUpdated(String lexiconId, SpeechError error) {
            if (error != null) {
                Toast.makeText(mContext, error.toString(), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(mContext, "Upload success!", Toast.LENGTH_LONG).show();
            }
        }
    };
    public void upload_userword() {
        String contents = VoiceUtils.readFile(mContext, "userwords","utf-8");
        // 指定引擎类型
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        // 置编码类型
        mIat.setParameter(SpeechConstant.TEXT_ENCODING, "utf-8");
        int ret = mIat.updateLexicon("userword", contents, mLexiconListener);
        if (ret != ErrorCode.SUCCESS)
            Toast.makeText(mContext, "上传热词失败,错误码：" + ret, Toast.LENGTH_LONG).show();
    }
}
