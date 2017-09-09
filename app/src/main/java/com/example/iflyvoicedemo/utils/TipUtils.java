package com.example.iflyvoicedemo.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by Administrator on 2017/3/16.
 */

public class TipUtils {

    public static void showTip(Context context, String title, String msg) {
        new SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE)
                .setTitleText(title)
                .setContentText(msg)
                .show();
    }

    public static void showTip(Context context, String title, String msg, int type) {
        new SweetAlertDialog(context, type)
                .setTitleText(title)
                .setContentText(msg)
                .show();
    }

    public static void showTip(Context context, String title, String msg, int type, String confirm, SweetAlertDialog.OnSweetClickListener listener) {
        new SweetAlertDialog(context, type)
                .setTitleText(title)
                .setContentText(msg)
                .setConfirmText(confirm)
                .setConfirmClickListener(listener)
                .show();
    }

    public static void showTip(Context context, String title, String msg, int type, String confirm, SweetAlertDialog.OnSweetClickListener confirmListener,
                               String cancle, SweetAlertDialog.OnSweetClickListener cancleListener) {
        new SweetAlertDialog(context, type)
                .setTitleText(title)
                .setContentText(msg)
                .setConfirmText(confirm)
                .setConfirmClickListener(confirmListener)
                .setCancelText(cancle)
                .setCancelClickListener(cancleListener)
                .show();
    }

    public static void showTip(Context context, String title, String msg, Drawable drawable) {
        new SweetAlertDialog(context, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                .setTitleText(title)
                .setContentText(msg)
                .setCustomImage(drawable)
                .show();
    }

    public static void showTip(Context context, String title, String msg, Drawable drawable, String confirm, SweetAlertDialog.OnSweetClickListener listener) {
        new SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE)
                .setTitleText(title)
                .setContentText(msg)
                .setCustomImage(drawable)
                .setConfirmText(confirm)
                .setConfirmClickListener(listener)
                .show();
    }

    public static void showTip(Context context, int barColor, String title) {
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog.getProgressHelper().setBarColor(barColor);
        sweetAlertDialog.setTitleText(title)
                        .setCancelable(false);
        sweetAlertDialog.show();
    }

}
