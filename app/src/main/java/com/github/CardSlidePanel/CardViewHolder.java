package com.github.CardSlidePanel;

import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.iflyvoicedemo.R;
import com.example.iflyvoicedemo.utils.ColorUtils;
import com.example.iflyvoicedemo.utils.CustomUtils;
import com.example.iflyvoicedemo.utils.StringUtils;

import org.w3c.dom.Text;

/**
 * Description
 * Author : Mr.wumin
 * Email  : fusu1435@163.com
 * Date   : 2017/9/6 0006 11:27
 */

public class CardViewHolder {
    CardView mContainer;
    TextView mRefText;
    TextView mRecogText;
    TextView mAccuracy;
    TextView mSimilarity;
    TextView mErr;

    public CardViewHolder(View view) {
        mRefText = (TextView) view.findViewById(R.id.tv_ref_text);
        mRecogText = (TextView) view.findViewById(R.id.tv_recog_text);
        mAccuracy = (TextView) view.findViewById(R.id.tv_accuracy);
        mContainer = (CardView) view.findViewById(R.id.cv_container);
        mSimilarity = (TextView) view.findViewById(R.id.tv_similarity);
        mErr = (TextView) view.findViewById(R.id.tv_err);

    }

    public void bindData(CardDataItem item) {
        mRefText.setText(item.getRefText());
        mRecogText.setText(item.getRecogText());
        mAccuracy.setText("精确度：" + item.getAccuracyRate());
        mContainer.setCardBackgroundColor(item.getColor());
        mSimilarity.setText("准确率：" + item.getSimilarity());
        mErr.setText("误差数: " + item.getError());
    }
}
