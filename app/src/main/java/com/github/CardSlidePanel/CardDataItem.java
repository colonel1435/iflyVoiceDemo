package com.github.CardSlidePanel;

/**
 * Description
 * Author : Mr.wumin
 * Email  : fusu1435@163.com
 * Date   : 2017/9/6 0006 11:28
 */

public class CardDataItem {
    private int color;
    private String refText;
    private String recogText;
    private float accuracyRate;
    private float similarity;
    private int error;
    private int sum;

    public CardDataItem() {
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getRefText() {
        return refText;
    }

    public void setRefText(String refText) {
        this.refText = refText;
    }

    public String getRecogText() {
        return recogText;
    }

    public void setRecogText(String recogText) {
        this.recogText = recogText;
    }

    public float getAccuracyRate() {
        return accuracyRate;
    }

    public void setAccuracyRate(float accuracyRate) {
        this.accuracyRate = accuracyRate;
    }

    public float getSimilarity() {
        return similarity;
    }

    public void setSimilarity(float similarity) {
        this.similarity = similarity;
    }

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public int getSum() {
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }

    @Override
    public String toString() {
        return "CardDataItem{" +
                "color=" + color +
                ", refText='" + refText + '\'' +
                ", recogText='" + recogText + '\'' +
                ", accuracyRate=" + accuracyRate +
                ", similarity=" + similarity +
                ", error=" + error +
                ", sum=" + sum +
                '}';
    }
}
