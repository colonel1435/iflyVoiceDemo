package com.example.iflyvoicedemo.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.Spanned;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;


/**
 * Created by Zero on 2016/12/17.
 */

public class RecyclerViewHolder extends RecyclerView.ViewHolder {
    private SparseArray<View> mViews;
    private View mConvertView;
    private Context mContext;
    public RecyclerViewHolder(Context context, View itemView) {
        super(itemView);
        mContext = context;
        mConvertView = itemView;
        mViews = new SparseArray<View>();
    }

    public static RecyclerViewHolder getVeiwHolder(Context context, View itemView) {
        RecyclerViewHolder viewHolder = new RecyclerViewHolder(context, itemView);
        return viewHolder;
    }
    public static RecyclerViewHolder getViewHolder(Context context, ViewGroup parent, int layoutId) {
        View itemView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        RecyclerViewHolder viewHolder = new RecyclerViewHolder(context, itemView);
        return viewHolder;
    }

    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    public View getConvertView() {
        return mConvertView;
    }

    public RecyclerViewHolder setText(int viewId, Spanned text) {
        TextView tv = getView(viewId);
        tv.setText(text);
        return this;
    }


    public RecyclerViewHolder setText(int viewId, String text) {
        TextView tv = getView(viewId);
        tv.setText(text);
        return this;
    }

    public RecyclerViewHolder setEditText(int viewId, String text) {
        EditText et = getView(viewId);
        et.setText(text);
        et.setEnabled(false);
        return this;
    }

    public RecyclerViewHolder setInputType(int viewId, int type) {
        EditText et = getView(viewId);
        et.setInputType(type);
        return this;
    }

    public RecyclerViewHolder setTextColor(int viewId, int color) {
        TextView tv = getView(viewId);
        tv.setTextColor(color);
        return this;
    }

    public RecyclerViewHolder setTextColorRes(int viewId, int colorRes) {
        TextView tv = getView(viewId);
        tv.setTextColor(mContext.getResources().getColor(colorRes));
        return this;
    }

    public RecyclerViewHolder setImageResource(int viewId, int resId) {
        ImageView iv = getView(viewId);
        iv.setImageResource(resId);
        return this;
    }

    public RecyclerViewHolder setImageBitmap(int viewId, Bitmap bmp) {
        ImageView iv = getView(viewId);
        iv.setImageBitmap(bmp);
        return this;
    }

    public RecyclerViewHolder setImageDrable(int viewId, Drawable drawable) {
        ImageView iv = getView(viewId);
        iv.setImageDrawable(drawable);
        return this;
    }

    public RecyclerViewHolder setBackgroundColor(int viewId, int color) {
        View view = getView(viewId);
        view.setBackgroundColor(color);
        return this;
    }

    public RecyclerViewHolder setBackgroundRes(int viewId, int resId) {
        View view = getView(viewId);
        view.setBackgroundResource(resId);
        return this;
    }

    public RecyclerViewHolder setBackground(int viewId, Drawable drawable) {
        View view = getView(viewId);
        view.setBackground(drawable);
        return this;
    }

    public RecyclerViewHolder setHide(int viewId, boolean visiable) {
        View view = getView(viewId);
        view.setVisibility(visiable ? View.VISIBLE : View.INVISIBLE);
        return this;
    }

    public RecyclerViewHolder setVisiable(int viewId, boolean visiable) {
        View view = getView(viewId);
        view.setVisibility(visiable ? View.VISIBLE : View.GONE);
        return this;
    }

    public RecyclerViewHolder setAlpha(int viewId, float value) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getView(viewId).setAlpha(value);
        } else {
            AlphaAnimation alphaAnimation = new AlphaAnimation(value, value);
            alphaAnimation.setDuration(0);
            alphaAnimation.setFillAfter(true);
            getView(viewId).startAnimation(alphaAnimation);
        }
        return this;
    }

    public RecyclerViewHolder setTag(int viewId, Object tag) {
        View view = getView(viewId);
        view.setTag(tag);
        return this;
    }

    public RecyclerViewHolder setTag(int viewId, int key, Object tag) {
        View view = getView(viewId);
        view.setTag(key, tag);
        return this;
    }

    public RecyclerViewHolder setChecked(int viewId, boolean checked) {
        CheckBox view = getView(viewId);
        view.setChecked(checked);
        return this;
    }

    public RecyclerViewHolder setProgress(int viewId, int progress) {
        ProgressBar view = getView(viewId);
        view.setProgress(progress);
        return this;
    }

    public RecyclerViewHolder setProcess(int viewId, int max, int progress) {
        ProgressBar view = getView(viewId);
        view.setMax(max);
        view.setProgress(progress);
        return this;
    }
    public RecyclerViewHolder setRating(int viewId, int rate) {
        RatingBar view = getView(viewId);
        view.setRating(rate);
        return this;
    }

    public RecyclerViewHolder setRating(int viewId, int max, int rate) {
        RatingBar view = getView(viewId);
        view.setMax(max);
        view.setRating(rate);
        return this;
    }

    public RecyclerViewHolder setClickable(int viewId, boolean clicked) {
        View view = getView(viewId);
        view.setClickable(clicked);
        return this;
    }

    public RecyclerViewHolder setOnClickListener(int viewId, View.OnClickListener listener) {
        View view = getView(viewId);
        view.setOnClickListener(listener);
        return this;
    }

    public RecyclerViewHolder setOnLongClickListener(int viewId, View.OnLongClickListener listener) {
        View view = getView(viewId);
        view.setOnLongClickListener(listener);
        return this;
    }

    public RecyclerViewHolder setOnTouchListener(int viewId, View.OnTouchListener listener) {
        View view = getView(viewId);
        view.setOnTouchListener(listener);
        return this;
    }

    public RecyclerViewHolder setOnFocusChangeListener(int viewId, View.OnFocusChangeListener listener) {
        View view = getView(viewId);
        view.setOnFocusChangeListener(listener);
        return this;
    }
}
