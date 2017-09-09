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
import com.example.iflyvoicedemo.bean.VoiceResult;

import org.w3c.dom.Text;

/**
 * Description
 * Author : Mr.wumin
 * Email  : fusu1435@163.com
 * Date   : 2017/9/9 0009 15:09
 */

public class HistoryPopupWindows extends PopupWindow {
    private Context mContext;
    private TextView mRefText;
    private TextView mRecogText;
    private TextView mErrs;
    private TextView mAccuracy;

    public HistoryPopupWindows(Context context) {
        super(context);
        this.mContext = context;
        initSetting();
    }

    private void initSetting() {
        View popupView = LayoutInflater.from(mContext).inflate(R.layout.popupwindows_history, null);
        mRefText = (TextView) popupView.findViewById(R.id.tv_ref_text_history);
        mRecogText = (TextView) popupView.findViewById(R.id.tv_recog_text_history);
        mErrs = (TextView) popupView.findViewById(R.id.tv_err_history);
        mAccuracy = (TextView) popupView.findViewById(R.id.tv_similarity_history);

        this.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setContentView(popupView);
        this.setTouchable(true);
        this.setFocusable(true);
        this.setOutsideTouchable(false);
        this.setBackgroundDrawable(new ColorDrawable());
        this.setAnimationStyle(R.style.popwindow_voice_anim);
    }

    public void setResult(VoiceResult result) {
        mRefText.setText(result.getText());
        mRecogText.setText(result.getResult());
        mErrs.setText("误差数: " + result.getErrs());
        mAccuracy.setText("准确率：" + result.getAccuracy());
    }

    public void dardBackground(Activity context, float alpha) {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = alpha;
        context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        context.getWindow().setAttributes(lp);
    }

    public void show(Activity context, View view) {
        this.showAtLocation(view, Gravity.CENTER, 0 , 0);
    }

}
