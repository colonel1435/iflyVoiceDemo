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
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

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

    public void dardBackground(Activity context, float alpha) {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = alpha;
        context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        context.getWindow().setAttributes(lp);
    }

    public void startVoice(View view) {
        View popupView = LayoutInflater.from(mContext).inflate(R.layout.popupwindow_voice, null);
        mProgressLayout = (FrameLayout)popupView.findViewById(R.id.fl_progressbar);
        mSpeakLayout = (LinearLayout)popupView.findViewById(R.id.ll_speak_image);
        mSpeakErrLayout = (LinearLayout)popupView.findViewById(R.id.ll_speak_err);
        mMsgText = (TextView)popupView.findViewById(R.id.tv_msg_text);
        mMsgImage = (ImageView)popupView.findViewById(R.id.iv_msg_image);
        mPopupWindows = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindows.setTouchable(true);
        mPopupWindows.setFocusable(true);
        mPopupWindows.setOutsideTouchable(true);
        mPopupWindows.setBackgroundDrawable(new ColorDrawable());
        mPopupWindows.setAnimationStyle(R.style.popwindow_voice_anim);
        mPopupWindows.getBackground().setAlpha(50);
        dardBackground(this, 0.4f);
        mPopupWindows.showAtLocation(view, Gravity.CENTER, 0 ,0);
        mPopupWindows.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mVRecognizer.stopSpeechRecognizer();
                dardBackground(MainActivity.this, 1.0f);
            }
        });
    }
    public void onDebug(View view) {
        mVRecognizer.showVoiceRecognizeDialog();
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
                    mSpeakLayout.setVisibility(View.INVISIBLE);
                    mProgressLayout.setVisibility(View.VISIBLE);
                    break;
                case VoiceRecognizer.RECOGNIZE_ERROR:
                    mMsgText.setText(getString(R.string.recognize_err));
                    mMsgImage.setImageResource(R.drawable.warning);
                    mProgressLayout.setVisibility(View.GONE);
                    mSpeakErrLayout.setVisibility(View.VISIBLE);
                    break;
                case VoiceRecognizer.SPEAK_NULL:
                    mMsgText.setText(getString(R.string.speak_null));
                    mMsgImage.setImageResource(R.drawable.warning);
                    mProgressLayout.setVisibility(View.GONE);
                    mSpeakErrLayout.setVisibility(View.VISIBLE);
                    break;
                case VoiceRecognizer.NET_ERROR:
                    mMsgText.setText(getString(R.string.net_err));
                    mMsgImage.setImageResource(R.drawable.warning);
                    mProgressLayout.setVisibility(View.GONE);
                    mSpeakErrLayout.setVisibility(View.VISIBLE);
                    break;
                case VoiceRecognizer.RECOGNIZE_FINISH:
                    mPopupWindows.dismiss();
                    break;
            }
        }
    };
    @Override
    protected void onDestroy() {
        Log.i(TAG,"Stop wakeupListener");
        mVWakeup.stopWakeup();
        super.onDestroy();
    }
}