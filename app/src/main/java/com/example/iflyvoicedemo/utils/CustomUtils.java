package com.example.iflyvoicedemo.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.example.iflyvoicedemo.bean.NumberPhonetic;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Zero on 2016/12/12.
 */
public class CustomUtils {

    public static final String TAG = "wumin";


    public static String getCurVersion(Context context) {
        return getPackageInfo(context).versionName;
    }

    private static PackageInfo getPackageInfo(Context context) {
        PackageInfo pi = null;
        try {
            PackageManager pm = context.getPackageManager();
            pi = pm.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);
            return pi;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pi;
    }

    public static void writeJson(String json, String dir, String fileName,
                                 boolean append) {
        FileOutputStream fos = null;
        ObjectOutputStream os = null;
        try {
            File ff = new File(dir, fileName);
            boolean boo = ff.exists();
            fos = new FileOutputStream(ff, append);
            os = new ObjectOutputStream(fos);
            if (append && boo) {
                FileChannel fc = fos.getChannel();
                fc.truncate(fc.position() - 4);

            }

            os.writeObject(json);

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {

            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
            if (os != null) {

                try {
                    os.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        }

    }

    public static String readFile(String dir, String fileName) {
        BufferedReader reader = null;
        String path = dir + File.separator + fileName;
        String laststr = "";
        try {
            FileInputStream fileInputStream = new FileInputStream(path);
            InputStreamReader inputStreamReader = new InputStreamReader(
                    fileInputStream, "utf-8");
            reader = new BufferedReader(inputStreamReader);
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                laststr += tempString;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return laststr;
    }

    public static String readFile(String filePath) {
        BufferedReader reader = null;
        String laststr = "";
        try {
            FileInputStream fileInputStream = new FileInputStream(filePath);
            InputStreamReader inputStreamReader = new InputStreamReader(
                    fileInputStream, "utf-8");
            reader = new BufferedReader(inputStreamReader);
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                laststr += tempString;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return laststr;
    }

    public static List<String> readJson(String dir, String fileName) {
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        List<String> result = new ArrayList<String>();
        File des = new File(dir, fileName);
        try {
            fis = new FileInputStream(des);
            ois = new ObjectInputStream(fis);
            while (fis.available() > 0)
                result.add((String) ois.readObject());

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        }

        return result;
    }

    public static List<NumberPhonetic> read4XML(InputStream fis) throws IOException, XmlPullParserException {
        XmlPullParser xmlPullParser= Xml.newPullParser();
        xmlPullParser.setInput(fis, "UTF-8");
        int eventType=xmlPullParser.getEventType();
        List<NumberPhonetic> numbers = null;
        NumberPhonetic phonetic = null;
        Set<String> phonetics = null;
        while(eventType!=XmlPullParser.END_DOCUMENT){
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    numbers = new ArrayList<>();
                    break;
                case XmlPullParser.START_TAG:
                    if (xmlPullParser.getName().equals("number")) {
                        phonetic = new NumberPhonetic();
                        phonetics = new HashSet<>();
                        phonetic.setNumber(xmlPullParser.getAttributeValue(null, "name"));
                    } else if (xmlPullParser.getName().equals("phonetic")) {
                        phonetics.add(xmlPullParser.nextText());
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if(xmlPullParser.getName().equals("number")){
                        phonetic.setPhonetics(phonetics);
                        numbers.add(phonetic);
                        phonetic = null;
                        phonetics = null;
                    }
                    break;
                case XmlPullParser.END_DOCUMENT:
                    fis.close();
            }
            eventType = xmlPullParser.next();
        }
        return numbers;
    }

    public static void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
