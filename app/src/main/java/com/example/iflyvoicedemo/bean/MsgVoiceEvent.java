package com.example.iflyvoicedemo.bean;

/**
 * Created by Administrator on 2017/4/26.
 */

public class MsgVoiceEvent {
    private int errCode;
    private int type;
    private String msg;

    public MsgVoiceEvent(int errCode, int type) {
        this.errCode = errCode;
        this.type = type;
    }

    public MsgVoiceEvent(int type) {
        this.type = type;
    }

    public MsgVoiceEvent(int type, String msg) {
        this.type = type;
        this.msg = msg;
    }

    public int getErrCode() {
        return errCode;
    }

    public void setErrCode(int errCode) {
        this.errCode = errCode;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
