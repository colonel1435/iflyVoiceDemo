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
    private int errs;
    private float accuracy;

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

    @Override
    public String toString() {
        return "VoiceResult{" +
                "uid='" + uid + '\'' +
                ", errs=" + errs +
                ", accuracy=" + accuracy +
                '}';
    }
}
