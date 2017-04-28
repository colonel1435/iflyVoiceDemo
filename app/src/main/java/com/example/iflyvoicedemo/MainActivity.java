package com.example.iflyvoicedemo;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.iflyvoicedemo.bean.MsgVoiceEvent;
import com.example.iflyvoicedemo.view.VoicePopupWindows;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "wumin";
    private TextView mVoiceText = null;
    private VoiceSynthesizer mVSynthesizer;
    private VoiceRecognizer mVRecognizer;
    private VoiceWakeup mVWakeup;
    private Toast mToast;
    private Context mContext = null;
    private ProgressBar mProgressBar;
    private PopupWindow mPopupWindows;
    private FrameLayout mProgressLayout;
    private LinearLayout mSpeakLayout;
    private LinearLayout mSpeakErrLayout;
    private TextView mMsgText;
    private ImageView mMsgImage;
    private VoicePopupWindows mVoiceWindows;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        EventBus.getDefault().register(this);
        mContext = this;
        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        mVoiceText = (TextView) findViewById(R.id.tv_voice_info);
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=" + getString(R.string.appid));

        mVSynthesizer = new VoiceSynthesizer(MainActivity.this);
        mVSynthesizer.startSpeechSynthesizer();

        mVRecognizer = new VoiceRecognizer(this, mHandler);

        mVWakeup = new VoiceWakeup(this);
        mVWakeup.startWakeup();


        mProgressLayout = (FrameLayout)findViewById(R.id.fl_progressbar);
        mSpeakLayout = (LinearLayout)findViewById(R.id.ll_speak_image);
        mProgressBar = (ProgressBar) findViewById(R.id.pbar_voice);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "start voice");
                mVRecognizer.startSpeechRecognizer();
//                mVRecognizer.showVoiceRecognizeDialog();
//                mVSynthesizer.speakMsg("欢迎使用小七语音助手");

            }
        });
    }

    public void startVoice(View view) {
        mVoiceWindows = new VoicePopupWindows(mContext);
        mVoiceWindows.setDismissListener(this, new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mVRecognizer.stopSpeechRecognizer();
                mVoiceWindows.dismiss(MainActivity.this);
            }
        });
        mVoiceWindows.show(this, view);
    }

    public void onDebug(View view) {
        mVRecognizer.showVoiceRecognizeDialog();
    }

    @Subscribe(threadMode=ThreadMode.MAIN)
    public void onVoiceHandle(MsgVoiceEvent event) {
        int type = event.getType();
        String msg;
        switch (type) {
            case VoiceRecognizer.START_VOICE:
                startVoice(getWindow().getDecorView());
                break;
            case VoiceRecognizer.START_RECOGNIZE:
                mVoiceWindows.showProgress();
                break;
            case VoiceRecognizer.RECOGNIZE_ERROR:
                msg = getString(R.string.recognize_err) + getString(R.string.err_code) + event.getErrCode() +")";
                mVoiceWindows.showMsg(msg, R.drawable.warning);
                break;
            case VoiceRecognizer.SPEAK_NULL:
                msg = getString(R.string.speak_null);
                mVoiceWindows.showMsg(msg, R.drawable.warning);
                break;
            case VoiceRecognizer.NET_ERROR:
                msg = getString(R.string.net_err);
                mVoiceWindows.showMsg(msg, R.drawable.warning);
                break;
            case VoiceRecognizer.RECOGNIZE_FINISH:
                mVoiceWindows.dismiss();
                if (event.getMsg())
                mVSynthesizer.speakMsg(getString(R.string.synthesier_msg) + event.getMsg());
                break;
            default:
                mVoiceWindows.dismiss();
                break;
        }
    }

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int id = msg.what;
            switch (id) {
                case VoiceRecognizer.START_VOICE:
                    startVoice(getWindow().getDecorView());
                    break;
                case VoiceRecognizer.START_RECOGNIZE:
                    mVoiceWindows.showProgress();
                    break;
                case VoiceRecognizer.RECOGNIZE_ERROR:
                    mVoiceWindows.showMsg(getString(R.string.recognize_err), R.drawable.warning);
                    break;
                case VoiceRecognizer.SPEAK_NULL:
                    mVoiceWindows.showMsg(getString(R.string.speak_null), R.drawable.warning);
                    break;
                case VoiceRecognizer.NET_ERROR:
                    mVoiceWindows.showMsg(getString(R.string.net_err), R.drawable.warning);
                    break;
                case VoiceRecognizer.RECOGNIZE_FINISH:
                    mVoiceWindows.dismiss();
                    break;
                default:
                    mVoiceWindows.dismiss();
                    break;
            }
        }
    };
    @Override
    protected void onDestroy() {
        Log.i(TAG,"Stop wakeupListener");
        mVWakeup.stopWakeup();
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}