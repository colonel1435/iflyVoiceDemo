package com.example.iflyvoicedemo;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.iflyvoicedemo.bean.MsgVoiceEvent;
import com.example.iflyvoicedemo.utils.ChinesePhoneticUtils;
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

    public VoiceRecognizer(Activity activity, Handler handler) {
        mContext = activity;
        mHandler = handler;
        mInstaller = new ApkInstaller(activity);
        mIat= SpeechRecognizer.createRecognizer(mContext, mInitListener);
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

    private InitListener mInitListener = new InitListener() {
        @Override
        public void onInit(int i) {
            Log.d(TAG, "SpeechRecognizer init() code = " + i);
            if (i != ErrorCode.SUCCESS) {
                Toast.makeText(mContext, "初始化失败，错误码：" + i, Toast.LENGTH_SHORT).show();
            }
        }
    };

    //听写监听器
    private RecognizerListener mRecogListener = new RecognizerListener(){
        public void onResult(RecognizerResult results, boolean isLast) {
            Log.i(TAG, "Recognize result : "+ results.getResultString());
            String voiceInfo = JsonParser.parseIatResult(results.getResultString());
            if (voiceInfo.compareTo("last") == 0) {
                return;
            }

            Log.i(TAG, "Recognize => "+ voiceInfo);
            ChinesePhoneticUtils chinesePhoneticUtils = new ChinesePhoneticUtils(true);
            String numberString = chinesePhoneticUtils.changeWordsWithChinesePhonetic(voiceInfo);
            String number = chinesePhoneticUtils.chineseNumber2Arabic(numberString);
            Log.i(TAG, "Change => "+ number);
            Toast.makeText(mContext, voiceInfo + " -> " + number, Toast.LENGTH_LONG).show();
            EventBus.getDefault().post(new MsgVoiceEvent(RECOGNIZE_FINISH, number));
        }
        //会话发生错误回调接口
        public void onError(SpeechError error) {
            error.getPlainDescription(true); //获取错误码描述
            int errCode = error.getErrorCode();
            int errType = RECOGNIZE_ERROR;
            switch (errCode) {
                case 10118:
                    errType = SPEAK_NULL;
                    break;
                case 20001:
                    errType = NET_ERROR;
                    break;
                default:
                    break;
            }
            EventBus.getDefault().post(new MsgVoiceEvent(errCode, errType));
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
        EventBus.getDefault().post(new MsgVoiceEvent(START_VOICE));
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

    /**
     * set params
     *
     * @param
     * @return
     */
    public void setParam() {
        // 清空参数
        mIat.setParameter(SpeechConstant.PARAMS, null);
        // 设置听写引擎
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
        // 设置返回结果格式
        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");
        // set file encode
        mIat.setParameter(SpeechConstant.TEXT_ENCODING, "utf-8");

        // 设置语言
        mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        // 设置语言区域
        mIat.setParameter(SpeechConstant.ACCENT, "mandarin");

        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mIat.setParameter(SpeechConstant.VAD_BOS, "3000");

        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mIat.setParameter(SpeechConstant.VAD_EOS, "1000");

        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIat.setParameter(SpeechConstant.ASR_PTT, "0");
//
//        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
//        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
//        mIat.setParameter(SpeechConstant.AUDIO_FORMAT,"wav");
//        mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory()+"/msc/iat.wav");
    }

}
