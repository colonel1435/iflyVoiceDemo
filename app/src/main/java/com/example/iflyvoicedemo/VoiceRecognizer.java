package com.example.iflyvoicedemo;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.iflyvoicedemo.bean.MsgVoiceEvent;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.LexiconListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Administrator on 2016/12/5.
 */

public class VoiceRecognizer {
    private static String TAG = "wumin VoiceRecognizer";
    private Handler mHandler = null;
    private Context mContext = null;
    private RecognizerDialog recognizerDialog;
    private SpeechRecognizer mIat;
    // 引擎类型
    private String mEngineType = SpeechConstant.TYPE_LOCAL;
    // 安装语记助手类
    ApkInstaller mInstaller;
    public static final int START_VOICE = 0;
    public static final int START_SPEAK = 1;
    public static final int STOP_SPEAK = 2;
    public static final int START_RECOGNIZE = 3;
    public static final int STOP_RECOGNIZE = 4;
    public static final int RECOGNIZE_ERROR = 5;
    public static final int RECOGNIZE_FINISH = 6;
    public static final int SPEAK_NULL = 7;
    public static final int NET_ERROR = 8;

    public static final int OFFLINE_START_VOICE = 10;
    public static final int OFFLINE_START_SPEAK = 11;
    public static final int OFFLINE_STOP_SPEAK = 12;
    public static final int OFFLINE_START_RECOGNIZE = 13;
    public static final int OFFLINE_STOP_RECOGNIZE = 14;
    public static final int OFFLINE_RECOGNIZE_ERROR = 15;
    public static final int OFFLINE_RECOGNIZE_FINISH = 16;
    public static final int OFFLINE_SPEAK_NULL = 17;
    public static final int OFFLINE_NET_ERROR = 18;

    public VoiceRecognizer(Activity activity) {
        mContext = activity;
        mInstaller = new ApkInstaller(activity);
        mIat= SpeechRecognizer.createRecognizer(mContext, mInitListener);
    }

    private RecognizerDialogListener mRecognizeListener = new RecognizerDialogListener() {
        @Override
        public void onResult(RecognizerResult recognizerResult, boolean b) {
            Log.i(TAG, "Recognize result : "+recognizerResult.getResultString());
            String voiceInfo = JsonParser.parseIatResult(recognizerResult.getResultString());
            if (voiceInfo.compareTo("last") == 0 ||
                voiceInfo.equals("。"))
                return;
            Toast.makeText(mContext, voiceInfo, Toast.LENGTH_LONG).show();
            EventBus.getDefault().post(new MsgVoiceEvent(RECOGNIZE_FINISH, voiceInfo));
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

    public void showVoiceRecognizeDialog() {
        recognizerDialog = new RecognizerDialog(mContext, mInitListener);
        recognizerDialog.setParameter(SpeechConstant.DOMAIN, "iat");
        recognizerDialog.setParameter(SpeechConstant.ACCENT, "mandarin");
        recognizerDialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        recognizerDialog.setParameter(SpeechConstant.SAMPLE_RATE, "16000");
        recognizerDialog.setParameter(SpeechConstant.ASR_PTT, "1");
        recognizerDialog.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        recognizerDialog.setListener(mRecognizeListener);
        recognizerDialog.show();
    }

    private InitListener mInitListener = new InitListener() {
        @Override
        public void onInit(int i) {
            Log.d(TAG, "SpeechRecognizer init() code = " + i);
            if (i != ErrorCode.SUCCESS) {
                Toast.makeText(mContext, "初始化失败，错误码：" + i, Toast.LENGTH_SHORT).show();
            }
        }
    };

    private RecognizerListener mRecogListener = new RecognizerListener(){
        public void onResult(RecognizerResult results, boolean isLast) {
            Log.i(TAG, "Recognize result : "+ results.getResultString());
            String voiceInfo = JsonParser.parseIatResult(results.getResultString());
            if (voiceInfo.compareTo("last") == 0) {
                return;
            }

            Log.i(TAG, "Recognize => "+ voiceInfo);
//            ChinesePhoneticUtils chinesePhoneticUtils = new ChinesePhoneticUtils(true, mContext);
//            String numberString = chinesePhoneticUtils.changeWordsWithChinesePhonetic(voiceInfo);
//            String number = chinesePhoneticUtils.chineseNumber2Arabic(numberString);
//            Log.i(TAG, "Change => "+ number);
            Toast.makeText(mContext, voiceInfo + " -> " + voiceInfo, Toast.LENGTH_LONG).show();
            EventBus.getDefault().post(new MsgVoiceEvent(OFFLINE_RECOGNIZE_FINISH, voiceInfo));
        }
        public void onError(SpeechError error) {
            error.getPlainDescription(true);
            int errCode = error.getErrorCode();
            int errType = OFFLINE_RECOGNIZE_ERROR;
            switch (errCode) {
                case 10118:
                    errType = OFFLINE_SPEAK_NULL;
                    break;
                case 20001:
                    errType = OFFLINE_NET_ERROR;
                    break;
                default:
                    break;
            }
            Log.i(TAG, "onError => "+ errCode);
            EventBus.getDefault().post(new MsgVoiceEvent(errCode, errType));
        }

        @Override
        public void onVolumeChanged(int i, byte[] bytes) {

        }

        public void onBeginOfSpeech() {
            Log.i(TAG, "onBeginOfSpeech...");
        }
        public void onVolumeChanged(int volume){}
        public void onEndOfSpeech() {
            Log.i(TAG, "onEndOfSpeech...");
            EventBus.getDefault().post(new MsgVoiceEvent(OFFLINE_START_RECOGNIZE, ""));
        }
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {}
    };
    public void startSpeechRecognizer() {
        switch (mEngineType) {
            case SpeechConstant.TYPE_LOCAL:
                /**
                 * 选择本地听写 判断是否安装语记,未安装则跳转到提示安装页面
                 */
                if (!SpeechUtility.getUtility().checkServiceInstalled()) {
                    mInstaller.install();
                } else {
                    String result = FucUtil.checkLocalResource();
                    if (!TextUtils.isEmpty(result)) {
                        Toast.makeText(mContext, result, Toast.LENGTH_SHORT);
                    }
                }
                break;
            default:
                break;
        }

        setParam();
        Log.i(TAG, "startSpeechRecognizer...");
        mIat.startListening(mRecogListener);
        EventBus.getDefault().post(new MsgVoiceEvent(OFFLINE_START_VOICE));
    }

    public void stopSpeechRecognizer() {
        Log.i(TAG, "stopSpeechRecognizer...");
        if (mIat.isListening()) {
            Log.i(TAG, "isListening...");
            mIat.stopListening();
        }
        mIat.cancel();
    }

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

    /**
     * set params
     *
     * @param
     * @return
     */
    public void setParam() {
        mIat.setParameter(SpeechConstant.PARAMS, null);
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");
        // set file encode
        mIat.setParameter(SpeechConstant.TEXT_ENCODING, "utf-8");

        mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        mIat.setParameter(SpeechConstant.ACCENT, "mandarin");

        mIat.setParameter(SpeechConstant.VAD_BOS, "3000");

        mIat.setParameter(SpeechConstant.VAD_EOS, "2000");

        mIat.setParameter(SpeechConstant.ASR_PTT, "1");
    }

}
