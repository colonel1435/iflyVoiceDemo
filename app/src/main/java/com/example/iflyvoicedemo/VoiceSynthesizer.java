package com.example.iflyvoicedemo;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;

/**
 * Created by Administrator on 2016/12/5.
 */

public class VoiceSynthesizer {
    private static String TAG = "wumin VoiceSynthesizer";
    private Context mContext = null;
    private SpeechSynthesizer mTts = null;


    public VoiceSynthesizer(Context context) {
        mContext = context;
    }

    private SynthesizerListener mSynListener = new SynthesizerListener() {
        @Override
        public void onSpeakBegin() {
            Log.i(TAG, "onSpeakBegin...");
        }

        @Override
        public void onBufferProgress(int i, int i1, int i2, String s) {
            Log.i(TAG, "onBufferProgress...");
        }

        @Override
        public void onSpeakPaused() {
            Log.i(TAG, "onSpeakPaused...");
        }

        @Override
        public void onSpeakResumed() {
            Log.i(TAG, "onSpeakResumed...");

        }

        @Override
        public void onSpeakProgress(int i, int i1, int i2) {
            Log.i(TAG, "onSpeakProgress...");
        }

        @Override
        public void onCompleted(SpeechError speechError) {
            Log.i(TAG, "onCompleted...");
        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }
    };
    public void startSpeechSynthesizer() {
        mTts= SpeechSynthesizer.createSynthesizer(mContext, null);
        mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaorong");//设置发音人
        mTts.setParameter(SpeechConstant.SPEED, "50");//设置语速
        mTts.setParameter(SpeechConstant.VOLUME, "80");//设置音量，范围0~100
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL); //设置本地
    }
    public void speakMsg(String msg) {
        mTts.startSpeaking(msg, mSynListener);
    }
}
