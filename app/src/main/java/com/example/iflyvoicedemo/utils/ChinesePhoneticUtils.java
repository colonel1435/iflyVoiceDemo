package com.example.iflyvoicedemo.utils;

import android.content.Context;
import android.util.Log;


import com.example.iflyvoicedemo.bean.NumberPhonetic;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;


import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/4/26.
 */

public class ChinesePhoneticUtils {
    private final String TAG = this.getClass().getSimpleName()+"@wumin";
    private Context mContext;
    String[] englishPhonetic = {
      "EI1", "BI4", "SEI4", "DI4", "YI4", "EFU1", "JI4",
      "EIQI1", "AI4", "JEI4", "KEI4", "EOU1", "EMEN1", "EN1",
      "OU1", "PI1", "KIU1", "A4", "ESI1", "TI4",
      "YOU4", "WEI4", "DABULIU3", "EKESI1", "WAI4", "ZEI4"
    };

    String[] unitArray = {
      "十", "百", "千", "万"
    };

    String arabicArray = "0123456789.";
    String chineseArray = "零一二三四五六七八九点";
    String englishString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    String arabicNumberString = "0123456789";
    String chineseNumberString = "零一二三四五六七八九十百千万点";
    String outCharctor = chineseNumberString;

    List<NumberPhonetic> chinesePhoneticTable = null;
    List<String> numberChinesePhonetic = new ArrayList<>();
    List<String> outChinesePhonetic = new ArrayList<>();

    boolean fuzzyMatching = true;

    public ChinesePhoneticUtils(boolean fuzzyMatching, Context context) {
        this.fuzzyMatching = fuzzyMatching;
        mContext = context;
        initSettings();
    }

    /*
     *  @Method initSettings
     *  @Description    initilize
     *  @Param []
     *  @Return void
     *  @Exception
     *  @Author Mr.wumin
     *  @Time 2017/4/27 11:37
     */
    private void initSettings() {
        try {
            String str;
            HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
            format.setCaseType(HanyuPinyinCaseType.UPPERCASE);
            format.setToneType(HanyuPinyinToneType.WITH_TONE_NUMBER);

            str = chineseNumberString;
            for (int i = 0; i < str.length(); i ++) {
                char c = str.charAt(i);
                String[] strs = PinyinHelper.toHanyuPinyinStringArray(c, format);
                numberChinesePhonetic.add(strs[0]);
            }

            Log.d(TAG, "Phonetic -> " + numberChinesePhonetic.toString());

            outChinesePhonetic.addAll(numberChinesePhonetic);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     *  @Method changeWordsWithChinesePhonetic
     *  @Description    Get chinese number with fuzzy matching
     *  @Param [input]  : the chinese number
     *  @Return java.lang.String
     *  @Exception
     *  @Author Mr.wumin
     *  @Time 2017/4/25 10:40
     */
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

    /*
     *  @Method changeWordProcessSignal
     *  @Description    Delete some special charactor, like space
     *  @Param [input]
     *  @Return java.lang.String
     *  @Exception
     *  @Author Mr.wumin
     *  @Time 2017/4/25 10:42
     */
    private String changeWordProcessSignal(String input) {
        String output = input;
        output = output.replace("，", "");
        output = output.replace("。", "");
        output = output.replace("-", "");
        output = output.replace(" ", "");

        return output;
    }

    /*
     *  @Method changeWordProcessEnglish
     *  @Description    UpperCase the input english charactor
     *  @Param [input]
     *  @Return java.lang.String
     *  @Exception
     *  @Author Mr.wumin
     *  @Time 2017/4/25 10:44
     */
    private String changeWordProcessEnglish(String input) {
        String output = input;
        output =output.toUpperCase();

        return output;
    }

    /*
     *  @Method changeOneWord
     *  @Description    Schedule fuzzy match
     *  @Param [input]
     *  @Return java.lang.String
     *  @Exception
     *  @Author Mr.wumin
     *  @Time 2017/4/25 15:45
     */
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


    /*
     *  @Method changeWord
     *  @Description    Fuzzy match chinese with chinese phonetic
     *  @Param [input, phonetics, strs]
     *  @Return java.lang.String
     *  @Exception
     *  @Author Mr.wumin
     *  @Time 2017/4/25 14:52
     */
    private String changeWord(String input, List<String> phonetics, String strs) {
        String output = "";
        String str = input.substring(0, 1);
        String strPhonetic = "";
        boolean flag = false;

        /***    Judge the type of input, like english, number or chinese  ***/
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
                Log.d(TAG, "CHINESE - > " + str + "CHINESE PHONETIC - > " + strPhonetic);
                flag = true;
            }

            /***    Match chinese phonetic   ***/
            if (flag) {
                /***    Accurate match     ***/
                int num = phonetics.indexOf(strPhonetic);
                if (num >= 0) {
                    return strs.substring(num, num+1);
                } else {
                    /***    Chinese phonetic fuzzy match    ***/
                    if (fuzzyMatching) {
                        /***   Match initial consonant   ***/
                        String strPhoneticFuzzy = new String(strPhonetic);
                        strPhoneticFuzzy = replaceHead(strPhoneticFuzzy);
                        boolean flagReplaceHead = (strPhoneticFuzzy == null) ? false: true;
                        if (flagReplaceHead) {
                            num = phonetics.indexOf(strPhoneticFuzzy);
                            if (num >= 0) {
                                Log.d(TAG, "Match initial consonant...");
                                return strs.substring(num, num+1);
                            }
                        }

                        /***   Match vowel   ***/
                        strPhoneticFuzzy = new String(strPhonetic);
                        strPhoneticFuzzy = replaceTail(strPhoneticFuzzy);
                        boolean flagReplaceTail = (strPhoneticFuzzy == null) ? false:true;
                        if (flagReplaceTail) {
                            num = phonetics.indexOf(strPhoneticFuzzy);
                            if (num >= 0) {
                                Log.d(TAG, "Match vowel...");
                                return strs.substring(num, num+1);
                            }
                        }

                        /***    Match both intial consonant and vowel   ***/
                        if (flagReplaceHead && flagReplaceTail) {
                            strPhoneticFuzzy = replaceHead(strPhoneticFuzzy);
                            num = phonetics.indexOf(strPhoneticFuzzy);
                            if (num >= 0) {
                                Log.d(TAG, "Match both intial consonant and vowel...");
                                return strs.substring(num, num+1);
                            }
                        }

                        /***    Accurate match when delete the tone     ***/
                        strPhonetic = strPhonetic.substring(0, strPhonetic.length()-1);
                        strPhoneticFuzzy = new String(strPhonetic);
                        num = findPhonetic(strPhoneticFuzzy, phonetics);
                        if (num >= 0) {
                            Log.d(TAG, "Accurate match when delete the tone...");
                            return strs.substring(num, num + 1);
                        }

                        /***    Fuzzy match in confused table   ***/
                        String fuzzy = findPhoneticWithTable(strPhoneticFuzzy);
                        if(!fuzzy.equals("")) {
                            Log.d(TAG, "Fuzzy match in confused table when delete the tone...");
                            return fuzzy;
                        }

                        /***   Match initial consonant   ***/
                        strPhoneticFuzzy = replaceHead(strPhoneticFuzzy);
                        flagReplaceHead = (strPhoneticFuzzy == null) ? false:true;
                        if (flagReplaceHead) {
                            num = findPhonetic(strPhoneticFuzzy, phonetics);
                            if (num >= 0) {
                                Log.d(TAG, "Match initial consonant when delete the tone...");
                                return strs.substring(num, num+1);
                            }
                        }

                        /***   Match vowel   ***/
                        strPhoneticFuzzy = new String(strPhonetic);
                        strPhoneticFuzzy = replaceTail(strPhoneticFuzzy);
                        flagReplaceTail = (strPhoneticFuzzy == null) ? false:true;
                        if (flagReplaceTail) {
                            num = findPhonetic(strPhoneticFuzzy, phonetics);
                            if (num >= 0) {
                                Log.d(TAG, "Match vowel when delete the tone...");
                                return strs.substring(num, num+1);
                            }
                        }

                        /***    Match both intial consonant and vowel   ***/
                        if (flagReplaceHead && flagReplaceTail) {
                            strPhoneticFuzzy = replaceHead(strPhoneticFuzzy);
                            num = findPhonetic(strPhoneticFuzzy, phonetics);
                            if (num >= 0) {
                                Log.d(TAG, "Match both intial consonant and vowel when delete the tone...");
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

    /*
     *  @Method replaceHead
     *  @Description    Replace the given phonetic that acts initial consonant with similar phonetic
     *  @Param [phonetic]
     *  @Return java.lang.String
     *  @Exception
     *  @Author Mr.wumin
     *  @Time 2017/4/25 15:05
     */
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

    /*
     *  @Method replaceTail
     *  @Description    Replace the given phonetic that acts vowel with similar phonetic
     *  @Param [phonetic]
     *  @Return java.lang.String
     *  @Exception
     *  @Author Mr.wumin
     *  @Time 2017/4/25 15:04
     */
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

    /*
     *  @Method findPhonetic
     *  @Description    Search phonetic in list
     *  @Param [phonetic, phonetics]
     *  @Return int : the position of phonetic in list
     *  @Exception
     *  @Author Mr.wumin
     *  @Time 2017/4/25 15:02
     */
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

    private String findPhoneticWithTable(String phonetic) {
        Log.d(TAG, "Find phonetic in table -> " + phonetic);
        String output = "";
        if (chinesePhoneticTable == null) {
            chinesePhoneticTable = new ArrayList<>();
            InputStream inputStream = null;
            try {
                inputStream = mContext.getAssets().open("chinese_phonetic_table.xml");
                chinesePhoneticTable.addAll(CustomUtils.read4XML(inputStream));

                for(NumberPhonetic num : chinesePhoneticTable) {
                    Log.d(TAG, "CONFUSED NUM -> " + num.toString());
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }

        }

        for(NumberPhonetic item : chinesePhoneticTable) {
            if (item.getPhonetics().contains(phonetic)) {
                output = item.getNumber();
                break;
            }
        }
        return output;
    }
    /*
     *  @Method chineseNumber2Arabic
     *  @Description    According to the type of string, change number string to arabic string
     *                  type : true -> the string contains count unit, like ten、hundred and so on,
     *                         Using chineseNumber2ArabicWithFull() to get number.
     *                          false -> the string is spell in short without count unit.
     *                         Using chineseNumber2ArabicWithShort() to get number.
     *  @Param [numberString]
     *  @Return java.lang.String
     *  @Exception
     *  @Author Mr.wumin
     *  @Time 2017/4/25 9:05
     */
    public String chineseNumber2Arabic(String numberString) {
        String output;
        boolean type = false;
        /***   Judge that whether there is count unit or not in numberString ***/
        for (int i = 0; i < unitArray.length; i++) {
            if (numberString.contains(unitArray[i])) {
                type = true;
                break;
            }
        }

        if (type){
            output = chineseNumber2ArabicWithFull(numberString);
        } else {
            output = chineseNumber2ArabicWithShort(numberString);
        }

        return output;
    }

    /*
     *  @Method chineseNumber2ArabicWithShort
     *  @Description    Change chinese number string to arabic number ,
     *                  Replace chinese char with arabic in order
     *  @Param [numberString]
     *  @Return java.lang.String
     *  @Exception
     *  @Author Mr.wumin
     *  @Time 2017/4/25 9:10
     */
    public String chineseNumber2ArabicWithShort(String numberString) {
        Log.d(TAG, "Start chineseNumber2ArabicWithShort...");
        String output = "";
        char ch;
        int index;
        int size = numberString.length();
        for (int i = 0; i < size; i++) {
            ch = numberString.charAt(i);
            index = chineseArray.indexOf(ch);
            if (index != -1){
                output += arabicArray.charAt(index);
            }
            Log.d(TAG, "Char -> " + ch + " INDEX -> " + index + " OUTPUT -> " + output);
        }

        return output;
    }

    /*
    *  @Method chineseNumber2ArabicWithFull
    *  @Description     Change chinese number string to arabic number ,
    *                   With decimal multiplication
    *  @Param [chineseNumber]
    *  @Return java.lang.String
    *  @Exception
    *  @Author Mr.wumin
    *  @Time 2017/4/25 9:12
    */
    @SuppressWarnings("unused")
    public String chineseNumber2ArabicWithFull(String chineseNumber){

        Log.d(TAG, "Start chineseNumber2ArabicWithFull...");
        double output = 0;
        double number = 1; // single number content
        boolean count = false;  //whether there is previous unit or not to handle
        char[] cnArr = new char[]{'零','一','二','三','四','五','六','七','八','九'};
        char[] chArr = new char[]{'个','十','百','千','万'};
        char dot = '点';
        String integerStr = chineseNumber;
        String decimalStr = "";
        /***    Seperate integer and decimal    ***/
        if (chineseNumber.indexOf(String.valueOf(dot)) != -1) {
            String[] numbers = chineseNumber.split("[点]");
            integerStr = numbers[0];
            decimalStr = numbers[1];
            Log.d(TAG, "Integer -> " + integerStr + " Decimal - > " + decimalStr);
        }

        /***    Handle integer part  ***/
        for (int i = 0; i < integerStr.length(); i++) {
            boolean unit = true;// whether is unit or number, true : unit ; false : number
            char c = integerStr.charAt(i);

            /***   Search char's position  ***/
            for (int j = 0; j < cnArr.length; j++) {
                if (c == cnArr[j]) {
                    /***    Add previous unit     ***/
                    if(count){
                        output += number;
                        count = false;
                    }
                    /***    The right number value is j   ***/
                    number = j;
                    unit = false;
                    break;
                }
            }
            /***    Multiple unit  to number  ***/
            if(unit){
                for (int j = 0; j < chArr.length; j++) {
                    if (c == chArr[j]) {
                        number *= (Math.pow(10, j));
                        count = true;
                    }
                }
            }
            /***    Stop it when it's the last char     ***/
            if (i == integerStr.length() - 1) {
                output += number;
                number = 0.0;
            }
        }

        /***    Handle decimal part   ***/
        for(int k = 0; k < decimalStr.length(); k++) {
            for (int j = 0; j < cnArr.length; j++) {
                char ch = decimalStr.charAt(k);
                if (ch == cnArr[j]) {
                    /***    The right number value is j*Math.pow(10, -(j+1))   ***/
                    number += (double) j / Math.pow(10, k+1);
                    Log.d(TAG, "ch -> " + ch + " number -> " + (double) j / Math.pow(10, k+1) + " sum -> " + number);
                    break;
                }
            }
            /***    Stop it when it's the last char     ***/
            if (k == decimalStr.length() - 1) {
                output += number;
            }
        }
        return Double.toString(output);
    }
}
