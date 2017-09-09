package com.example.iflyvoicedemo.bean;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Description
 * Author : Mr.wumin
 * Email  : fusu1435@163.com
 * Date   : 2017/9/7 0007 14:50
 */

public class VoiceResult extends RealmObject{
    @PrimaryKey
    private String uid;
    private String text;
    private String result;
    private int errs;
    private float accuracy;
    private String type;

    public VoiceResult() {
    }

    public VoiceResult(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public int getErrs() {
        return errs;
    }

    public void setErrs(int errs) {
        this.errs = errs;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "VoiceResult{" +
                "uid='" + uid + '\'' +
                ", text='" + text + '\'' +
                ", result='" + result + '\'' +
                ", errs=" + errs +
                ", accuracy=" + accuracy +
                ", type='" + type + '\'' +
                '}';
    }
}
