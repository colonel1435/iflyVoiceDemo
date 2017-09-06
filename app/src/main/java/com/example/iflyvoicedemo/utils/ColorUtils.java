package com.example.iflyvoicedemo.utils;


import android.content.Context;
import android.support.v4.content.ContextCompat;

import com.example.iflyvoicedemo.R;

import java.util.Random;

/**
 * Description
 * Author : Mr.wumin
 * Email  : fusu1435@163.com
 * Date   : 2017/8/14 0014 14:37
 */

public class ColorUtils {

    private static final int INDEX_SEA_GREEN = 0;
    private static final int INDEX_DARK_CYAN = 1;
    private static final int INDEX_LIGHT_SEA_GREEN = 2;
    private static final int INDEX_MEDIUM_TURQUOISE = 3;
    private static final int INDEX_DARK_TURQUOISE = 4;
    private static final int INDEX_SLATE_GRAY = 5;
    private static final int INDEX_TEAL = 6;
    private static final int INDEX_STEEL_BLUE = 7;
    private static final int INDEX_MEDIUM_AQUAMAINE = 8;
    private static final int INDEX_SKY_BLUE = 9;
    private static final int INDEX_MAROON = 10;
    private static final int INDEX_BROWN = 11;
    private static final int INDEX_CHOCOLATE = 12;
    private static final int INDEX_CORAL = 13;
    private static final int INDEX_ROSY_BROWN = 14;
    private static final int INDEX_SLATE_BLUE = 15;
    private static final int INDEX_LIGHT_SLATE_BLUE = 16;

    public static int getRandomColor(Context context) {
        Random random = new Random();
        int index = random.nextInt(INDEX_LIGHT_SLATE_BLUE);
        int colorId;
        switch (index) {
            case INDEX_SEA_GREEN:
                colorId = R.color.colorSeaGreen;
                break;
            case INDEX_DARK_CYAN:
                colorId = R.color.colorDarkCyan;
                break;
            case INDEX_LIGHT_SEA_GREEN:
                colorId = R.color.colorLightSeaGreen;
                break;
            case INDEX_MEDIUM_AQUAMAINE:
                colorId = R.color.colorMediumAquamaine;
                break;
            case INDEX_DARK_TURQUOISE:
                colorId = R.color.colorDarkTurquoise;
                break;
            case INDEX_SLATE_GRAY:
                colorId = R.color.colorSlateGray;
                break;
            case INDEX_TEAL:
                colorId = R.color.colorTeal;
                break;
            case INDEX_STEEL_BLUE:
                colorId = R.color.colorSteelBlue;
                break;
            case INDEX_MEDIUM_TURQUOISE:
                colorId = R.color.colorMediumTurquoise;
                break;
            case INDEX_SKY_BLUE:
                colorId = R.color.colorSkyBlue;
                break;
            case INDEX_MAROON:
                colorId = R.color.colorMaroon;
                break;
            case INDEX_BROWN:
                colorId = R.color.colorBrown;
                break;
            case INDEX_CHOCOLATE:
                colorId = R.color.colorChocolate;
                break;
            case INDEX_CORAL:
                colorId = R.color.colorCoral;
                break;
            case INDEX_ROSY_BROWN:
                colorId = R.color.colorRosyBrown;
                break;
            case INDEX_SLATE_BLUE:
                colorId = R.color.colorSlateBlue;
                break;
            case INDEX_LIGHT_SLATE_BLUE:
                colorId = R.color.colorLightSlateBlue;
                break;
            default:
                colorId = R.color.colorSlateGray;
        }

        return ContextCompat.getColor(context, colorId);
    }
}
