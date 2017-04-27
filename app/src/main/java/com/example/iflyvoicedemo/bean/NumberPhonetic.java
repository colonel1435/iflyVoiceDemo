package com.example.iflyvoicedemo.bean;

import java.util.List;
import java.util.Set;

/**
 * Created by Administrator on 2017/4/27.
 */

public class NumberPhonetic {
    private String number;
    private Set<String> phonetics;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Set<String> getPhonetics() {
        return phonetics;
    }

    public void setPhonetics(Set<String> phonetics) {
        this.phonetics = phonetics;
    }

    @Override
    public String toString() {
        return "NumberPhonetic{" +
                "number='" + number + '\'' +
                ", phonetics=" + phonetics.toString()+
                '}';
    }
}
