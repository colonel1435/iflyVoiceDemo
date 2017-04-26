package com.example.iflyvoicedemo.bean;

/**
 * Created by Administrator on 2017/4/26.
 */

public class MsgVoiceEvent {
    private int errCode;
    private int type;

    public MsgVoiceEvent(int errCode, int type) {
        this.errCode = errCode;
        this.type = type;
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
}
