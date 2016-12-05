package com.example.iflyvoicedemo;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.GrammarListener;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.LexiconListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechEvent;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.VoiceWakeuper;
import com.iflytek.cloud.WakeuperListener;
import com.iflytek.cloud.WakeuperResult;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.iflytek.cloud.util.ResourceUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "wumin";
    private TextView mVoiceText = null;
    private VoiceSynthesizer mVSynthesizer;
    private VoiceRecognizer mVRecognizer;
    private VoiceWakeup mVWakeup;
    private Toast mToast;
    private Context mContext = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mContext = this;
        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        mVoiceText = (TextView) findViewById(R.id.tv_voice_info);
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=" + getString(R.string.appid));

        mVSynthesizer = new VoiceSynthesizer(MainActivity.this);
        mVSynthesizer.startSpeechSynthesizer();

        mVRecognizer = new VoiceRecognizer(this);

        mVWakeup = new VoiceWakeup(this);
        mVWakeup.startWakeup();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "start voice");
                mVRecognizer.showVoiceRecognizeDialog();
//                mVSynthesizer.speakMsg("欢迎使用小七语音助手");

            }
        });
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG,"Stop wakeupListener");
        mVWakeup.stopWakeup();
        super.onDestroy();
    }
}