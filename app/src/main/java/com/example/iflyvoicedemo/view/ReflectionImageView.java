package com.example.iflyvoicedemo.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Description
 * Author : Mr.wumin
 * Email  : fusu1435@163.com
 * Date   : 2017/9/9 0009 9:06
 */

public class ReflectionImageView extends ImageView {

        private boolean mReflectionMode = true;
        public ReflectionImageView(Context context) {
            this(context, null);
        }
        public ReflectionImageView(Context context, AttributeSet attrs) {
            this(context, attrs, 0);
        }
        public ReflectionImageView(Context context, AttributeSet attrs,
                               int defStyle) {
            super(context, attrs, defStyle);
            initView();
        }

        private void initView() {
            Bitmap originalImage = ((BitmapDrawable)this.getDrawable()).getBitmap();
            DoReflection(originalImage);
        }


        public void setReflectionMode(boolean isRef) {
            mReflectionMode = isRef;
        }
        public boolean getReflectionMode() {
            return mReflectionMode;
        }

//        @Override
//        public void setImageResource(int resId) {
//            Bitmap originalImage = BitmapFactory.decodeResource(
//                    getResources(), resId);
//            DoReflection(originalImage);
//        }

        private void DoReflection(Bitmap originalImage) {
            final int reflectionGap = 4;
            int width = originalImage.getWidth();
            int height = originalImage.getHeight();

            //反转
            Matrix matrix = new Matrix();
            matrix.preScale(1, -1);
            //reflectionImage就是下面透明的那部分,可以设置它的高度为原始的3/4,这样效果会更好些
            Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0,
                    0, width, height, matrix, false);
            //创建一个新的bitmap,高度为原来的两倍
            Bitmap bitmapWithReflection = Bitmap.createBitmap(width, (height + height), Bitmap.Config.ARGB_8888);
            Canvas canvasRef = new Canvas(bitmapWithReflection);

            //先画原始的图片
            canvasRef.drawBitmap(originalImage, 0, 0, null);
            //画间距
            //Paint deafaultPaint = new Paint();
            //canvasRef.drawRect(0, height, width, height + reflectionGap, deafaultPaint);

            //画被反转以后的图片
            canvasRef.drawBitmap(reflectionImage, 0, height + reflectionGap, null);
            // 创建一个渐变的蒙版放在下面被反转的图片上面
            Paint paint = new Paint();
            LinearGradient shader = new LinearGradient(0,
                    originalImage.getHeight(), 0, bitmapWithReflection.getHeight()
                    + reflectionGap, 0x80ffffff, 0x00ffffff, Shader.TileMode.CLAMP);
            // Set the paint to use this shader (linear gradient)
            paint.setShader(shader);
            // Set the Transfer mode to be porter duff and destination in
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
            // Draw a rectangle using the paint with our linear gradient
            canvasRef.drawRect(0, height, width, bitmapWithReflection.getHeight()
                    + reflectionGap, paint);
            //调用ImageView中的setImageBitmap
            this.setImageBitmap(bitmapWithReflection);
        }
}

