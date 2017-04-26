package com.example.iflyvoicedemo.utils;

import android.util.Log;


import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/4/26.
 */

public class ChinesePhoneticUtils {
    private final String TAG = this.getClass().getSimpleName()+"@wumin";
    String[] englishPhonetic = {
      "EI1", "BI4", "SEI4", "DI4", "YI4", "EFU1", "JI4",
      "EIQI1", "AI4", "JEI4", "KEI4", "EOU1", "EMEN1", "EN1",
      "OU1", "PI1", "KIU1", "A4", "ESI1", "TI4",
      "YOU4", "WEI4", "DABULIU3", "EKESI1", "WAI4", "ZEI4"
    };
    String englishString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    String arabicNumberString = "0123456789";
    String chineseNumberString = "零一二三四五六七八九十百千万点";
    String outCharctor = chineseNumberString;

    List<String> numberChinesePhonetic = new ArrayList<>();
    List<String> outChinesePhonetic = new ArrayList<>();

    boolean fuzzyMatching = true;

    public ChinesePhoneticUtils(boolean fuzzyMatching) {
        this.fuzzyMatching = fuzzyMatching;
        initSettings();
    }

    private void initSettings() {
        try {
            String str;
            HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
            format.setCaseType(HanyuPinyinCaseType.UPPERCASE);
            format.setToneType(HanyuPinyinToneType.WITH_TONE_NUMBER);

            str = arabicNumberString;
            for (int i = 0; i < str.length(); i ++) {
                char c = str.charAt(i);
                String[] strs = PinyinHelper.toHanyuPinyinStringArray(c, format);
                numberChinesePhonetic.add(strs[0]);
            }

            outChinesePhonetic.addAll(numberChinesePhonetic);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String changeWordsWithChinesePhonetic(String input) {
        String output = input;
        try {
            output = changeWordProcessSignal(output);
            output = changeWordProcessEnglish(output);

            Log.d(TAG, "Before CHANGED : " + output);
            int index;
            String str;
            String strChanged;
            StringBuilder stringBuilder = new StringBuilder();
            for (index = 0; index < input.length(); index++) {
                str = input.substring(index, index+1);
                strChanged = changeOneWord(str);
                stringBuilder.append(strChanged);
            }

            output = stringBuilder.toString();
            Log.d(TAG, "After CHANGED : " + output);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return output;
    }

    private String changeWordProcessSignal(String input) {
        String output = input;

        output = output.replace("，", "");
        output = output.replace("。", "");
        output = output.replace("-", "");
        output = output.replace(" ", "");

        return output;
    }

    private String changeWordProcessEnglish(String input) {
        String output = input;

        output =output.toUpperCase();

        return output;
    }
    
    private String changeOneWord(String input) {
        if (chineseNumberString.contains(input) || arabicNumberString.contains(input)) {
            Log.d(TAG, "Number -> " + input);
            return input;
        }

        String strChanged;
        strChanged = changeWord(input, numberChinesePhonetic, chineseNumberString);
        if (chineseNumberString.contains(strChanged)) {
            Log.d(TAG, "CHANGE NUMBER -> " + strChanged);
            return strChanged;
        }

        return input;

    }

    private String changeWord(String input, List<String> phonetics, String strs) {
        String output = "";
        String str = input.substring(0, 1);
        String strPhonetic = "";
        boolean flag = false;

        try {
            Log.d(TAG, "BEFORE STR - > " + str);
            if (str.matches("^[A-Z]{1}$")) {
                strPhonetic = englishPhonetic[englishString.indexOf(str)];
                Log.d(TAG, "STR - > " + str + " CHINESE PHONETIC - > " + strPhonetic);
                flag = true;
            }
            else if (str.matches("^[0-9]{1}$")) {
                strPhonetic = numberChinesePhonetic.get(arabicNumberString.indexOf(str));
                Log.d(TAG, "NUM - > " + str + "CHINESE PHONETIC - > " + strPhonetic);
                flag = true;
            }
            else if(str.matches("^[\u4e00-\u9fa5]{1}$")) {
                HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
                format.setCaseType(HanyuPinyinCaseType.UPPERCASE);
                format.setToneType(HanyuPinyinToneType.WITH_TONE_NUMBER);

                char ch = str.charAt(0);
                String[] vals = PinyinHelper.toHanyuPinyinStringArray(ch, format);

                strPhonetic = vals[0];
                flag = true;
            }

            if (flag) {
                int num = phonetics.indexOf(strPhonetic);
                if (num >= 0) {
                    return strs.substring(num, num+1);
                } else {
                    if (fuzzyMatching) {
                        String strPhoneticFuzzy = new String(strPhonetic);
                        strPhoneticFuzzy = replaceHead(strPhoneticFuzzy);
                        boolean flagReplaceHead = (strPhoneticFuzzy == null) ? false: true;
                        if (flagReplaceHead) {
                            num = phonetics.indexOf(strPhoneticFuzzy);
                            if (num >= 0) {
                                return strs.substring(num, num+1);
                            }
                        }

                        strPhoneticFuzzy = new String(strPhonetic);
                        strPhoneticFuzzy = replaceTail(strPhoneticFuzzy);
                        boolean flagReplaceTail = (strPhoneticFuzzy == null) ? false:true;
                        if (flagReplaceTail) {
                            num = phonetics.indexOf(strPhoneticFuzzy);
                            if (num >= 0) {
                                return strs.substring(num, num+1);
                            }
                        }

                        if (flagReplaceHead && flagReplaceTail) {
                            strPhoneticFuzzy = replaceHead(strPhoneticFuzzy);
                            num = phonetics.indexOf(strPhoneticFuzzy);
                            if (num >= 0) {
                                return strs.substring(num, num+1);
                            }
                        }

                        strPhonetic = strPhonetic.substring(0, strPhonetic.length());
                        strPhoneticFuzzy = new String(strPhonetic);
                        num = findPhonetic(strPhoneticFuzzy, phonetics);
                        if (num >= 0) {
                            return strs.substring(num, num + 1);
                        }

                        strPhoneticFuzzy = replaceHead(strPhoneticFuzzy);
                        flagReplaceHead = (strPhoneticFuzzy == null) ? false:true;
                        if (flagReplaceHead) {
                            num = findPhonetic(strPhoneticFuzzy, phonetics);
                            if (num >= 0) {
                                return strs.substring(num, num+1);
                            }
                        }

                        strPhoneticFuzzy = new String(strPhonetic);
                        strPhoneticFuzzy = replaceTail(strPhoneticFuzzy);
                        flagReplaceTail = (strPhoneticFuzzy == null) ? false:true;
                        if (flagReplaceTail) {
                            num = findPhonetic(strPhoneticFuzzy, phonetics);
                            if (num >= 0) {
                                return strs.substring(num, num+1);
                            }
                        }

                        if (flagReplaceHead && flagReplaceTail) {
                            strPhoneticFuzzy = replaceHead(strPhoneticFuzzy);
                            num = findPhonetic(strPhoneticFuzzy, phonetics);
                            if (num >= 0) {
                                return strs.substring(num, num+1);
                            }
                        }

                        return str;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return output;
    }

    private String replaceHead(String phonetic) {
        String out = null;
        if (phonetic.contains("ZH")) {
            out = phonetic.replace("ZH", "Z");
        } else if (phonetic.contains("CH")) {
            out = phonetic.replace("CH", "C");
        } else if (phonetic.contains("SH")) {
            out = phonetic.replace("SH", "S");
        } else if (phonetic.contains("Z")) {
            out = phonetic.replace("Z", "ZH");
        } else if (phonetic.contains("C")) {
            out = phonetic.replace("C", "CH");
        } else if (phonetic.contains("S")) {
            out = phonetic.replace("S", "SH");
        } else if (phonetic.contains("L")) {
            out = phonetic.replace("L", "N");
        } else if (phonetic.contains("N")) {
            out = phonetic.replace("N", "L");
        } else {
            return null;
        }

        Log.d(TAG, "HEAD -> " + out);
        return out;
    }

    private String replaceTail(String phonetic) {
        String out = null;
        if (phonetic.contains("ANG")) {
            out = phonetic.replace("ANG", "AN");
        } else if (phonetic.contains("ENG")) {
            out = phonetic.replace("ENG", "EN");
        } else if (phonetic.contains("ING")) {
            out = phonetic.replace("ING", "IN");
        } else if (phonetic.contains("AN")) {
            out = phonetic.replace("AN", "ANG");
        } else if (phonetic.contains("EN")) {
            out = phonetic.replace("EN", "ENG");
        } else if (phonetic.contains("IN")) {
            out = phonetic.replace("IN", "ING");
        } else {
            return  null;
        }

        return out;
    }

    private int findPhonetic(String phonetic, List<String> phonetics) {
        int num = 0;
        for (String str : phonetics) {
            if (str.contains(phonetic) && phonetic.length() == (str.length() - 1)) {
                return num;
            }
            num ++;
        }

        return -1;
    }
    /**
     * chinese 2 arabic number
     * @author wumin.sunland
     * @param chineseNumber
     * @return
     */
    @SuppressWarnings("unused")
    public int chineseNumber2Arabic(String chineseNumber){
        int result = 0;
        int temp = 1;//存放一个单位的数字如：十万
        int count = 0;//判断是否有chArr
        char[] cnArr = new char[]{'一','二','三','四','五','六','七','八','九'};
        char[] chArr = new char[]{'十','百','千','万','亿'};
        for (int i = 0; i < chineseNumber.length(); i++) {
            boolean b = true;//判断是否是chArr
            char c = chineseNumber.charAt(i);
            for (int j = 0; j < cnArr.length; j++) {//非单位，即数字
                if (c == cnArr[j]) {
                    if(0 != count){//添加下一个单位之前，先把上一个单位值添加到结果中
                        result += temp;
                        temp = 1;
                        count = 0;
                    }
                    // 下标+1，就是对应的值
                    temp = j + 1;
                    b = false;
                    break;
                }
            }
            if(b){//单位{'十','百','千','万','亿'}
                for (int j = 0; j < chArr.length; j++) {
                    if (c == chArr[j]) {
                        switch (j) {
                            case 0:
                                temp *= 10;
                                break;
                            case 1:
                                temp *= 100;
                                break;
                            case 2:
                                temp *= 1000;
                                break;
                            case 3:
                                temp *= 10000;
                                break;
                            case 4:
                                temp *= 100000000;
                                break;
                            default:
                                break;
                        }
                        count++;
                    }
                }
            }
            if (i == chineseNumber.length() - 1) {//遍历到最后一个字符
                result += temp;
            }
        }
        return result;
    }
}
