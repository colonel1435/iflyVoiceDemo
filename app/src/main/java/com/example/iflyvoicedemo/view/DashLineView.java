package com.example.iflyvoicedemo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Mr.wumin on 2017/2/13.
 */

public class DashLineView extends View {
    public DashLineView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.DKGRAY);
        Path path = new Path();
        path.moveTo(0, 10);
        path.lineTo(getMeasuredWidth(),10);
        PathEffect effects = new DashPathEffect(new float[]{5,5,5,5},1);
        paint.setPathEffect(effects);
        canvas.drawPath(path, paint);
    }
}
