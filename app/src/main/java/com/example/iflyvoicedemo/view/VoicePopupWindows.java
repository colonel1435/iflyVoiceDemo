package com.example.iflyvoicedemo.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.iflyvoicedemo.R;

/**
 * Created by Administrator on 2017/4/26.
 */

public class VoicePopupWindows extends PopupWindow {
    private Context mContext;
    private FrameLayout mProgressLayout;
    private LinearLayout mSpeakLayout;
    private LinearLayout mSpeakErrLayout;
    private TextView mMsgText;
    private ImageView mMsgImage;
    private OnDismissListener mDismissListener;
    private ImageView mListeningImage;


    public VoicePopupWindows(Context context) {
        super(context);
        mContext = context;
        initSettings();
    }

    private void initSettings() {
        View popupView = LayoutInflater.from(mContext).inflate(R.layout.popupwindow_voice, null);
        mProgressLayout = (FrameLayout)popupView.findViewById(R.id.fl_progressbar);
        mSpeakLayout = (LinearLayout)popupView.findViewById(R.id.ll_speak_image);
        mSpeakErrLayout = (LinearLayout)popupView.findViewById(R.id.ll_speak_err);
        mMsgText = (TextView)popupView.findViewById(R.id.tv_msg_text);
        mMsgImage = (ImageView)popupView.findViewById(R.id.iv_msg_image);
        mListeningImage = (ImageView) popupView.findViewById(R.id.iv_voice_list);
        ((Animatable) mListeningImage.getDrawable()).start();

        this.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setContentView(popupView);
        this.setTouchable(true);
        this.setFocusable(true);
        this.setOutsideTouchable(false);
        this.setBackgroundDrawable(new ColorDrawable());
        this.setAnimationStyle(R.style.popwindow_voice_anim);
        this.setOnDismissListener(mDismissListener);

    }

    public void dardBackground(Activity context, float alpha) {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = alpha;
        context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        context.getWindow().setAttributes(lp);
    }

    public void showProgress() {
        mSpeakLayout.setVisibility(View.INVISIBLE);
        mSpeakErrLayout.setVisibility(View.INVISIBLE);
        mProgressLayout.setVisibility(View.VISIBLE);
    }

    public void showMsg(String msg, int iconId) {
        mMsgText.setText(msg);
        mMsgImage.setImageResource(iconId);
        mSpeakLayout.setVisibility(View.INVISIBLE);
        mProgressLayout.setVisibility(View.INVISIBLE);
        mSpeakErrLayout.setVisibility(View.VISIBLE);
    }

    public void setDismissListener(Activity context, OnDismissListener listener) {
        mDismissListener = listener;
        this.setOnDismissListener(mDismissListener);
        dardBackground(context, 1.0f);
    }

    public void show(Activity context, View view) {
        dardBackground(context, 0.6f);
        this.showAtLocation(view, Gravity.CENTER, 0 , 0);
    }

    public void show(Activity context, View view, int gravity) {
        dardBackground(context, 0.6f);
        this.showAtLocation(view, gravity, 0, 0);
    }

    public void show(Activity context, View view, int gravity, int offsetX, int offsetY) {
        dardBackground(context, 0.6f);
        this.showAtLocation(view, gravity, offsetX, offsetY);
    }

    public void dismiss(Activity context) {
        super.dismiss();
        dardBackground(context, 1.0f);
    }
}
