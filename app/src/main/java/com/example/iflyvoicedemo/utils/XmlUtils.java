package com.example.iflyvoicedemo.utils;

import android.nfc.Tag;
import android.util.Log;
import android.util.Xml;

import com.github.CardSlidePanel.CardDataItem;

import org.w3c.dom.ProcessingInstruction;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Description
 * Author : Mr.wumin
 * Email  : fusu1435@163.com
 * Date   : 2017/8/27 0027 15:58
 */

public class XmlUtils {
    public static List<CardDataItem> parseCardData(InputStream fis) throws IOException, XmlPullParserException {
        XmlPullParser xmlPullParser= Xml.newPullParser();
        xmlPullParser.setInput(fis, "UTF-8");
        int eventType=xmlPullParser.getEventType();
        List<CardDataItem> items = null;
        CardDataItem detail = null;
        while(eventType!=XmlPullParser.END_DOCUMENT){
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    items = new ArrayList<>();
                    break;
                case XmlPullParser.START_TAG:
                    if ("item".equals(xmlPullParser.getName())) {
                        detail = new CardDataItem();
                    } else if ("text".equals(xmlPullParser.getName())) {
                        detail.setRefText(xmlPullParser.nextText());
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if("item".equals(xmlPullParser.getName())){
                        items.add(detail);
                        detail = null;
                    }
                    break;
                case XmlPullParser.END_DOCUMENT:
                    fis.close();
            }
            eventType = xmlPullParser.next();
        }
        return items;
    }
}
