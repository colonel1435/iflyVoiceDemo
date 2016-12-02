package com.example.iflyvoicedemo;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.text.TextUtils;

//import com.iflytek.speech.ErrorCode;
//import com.iflytek.speech.SpeechError;
/**
 * 锟斤拷锟狡端凤拷锟截碉拷Json锟斤拷锟斤拷锟斤拷薪锟斤拷锟�
 * @author iFlytek
 * @since 20131211
 */
public class JsonParser {
	
	/**
	 * 锟斤拷写锟斤拷锟斤拷锟絁son锟斤拷式锟斤拷锟斤拷
	 * @param json
	 * @return
	 */
	public static String parseIatResult(String json) {
		if(TextUtils.isEmpty(json))
			return "";
		
		StringBuffer ret = new StringBuffer();
		try {
			JSONTokener tokener = new JSONTokener(json);
			JSONObject joResult = new JSONObject(tokener);

			String last = joResult.getString("ls");
			if (last == "true")
				ret.append("last");
			else {
				JSONArray words = joResult.getJSONArray("ws");
				for (int i = 0; i < words.length(); i++) {
					JSONArray items = words.getJSONObject(i).getJSONArray("cw");
					JSONObject obj = items.getJSONObject(0);
					ret.append(obj.getString("w"));
	//				for(int j = 0; j < items.length(); j++)
	//				{
	//					JSONObject obj = items.getJSONObject(j);
	//					ret.append(obj.getString("w"));
	//				}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return ret.toString();
	}
	
	/**
	 * 识锟斤拷锟斤拷锟斤拷Json锟斤拷式锟斤拷锟斤拷
	 * @param json
	 * @return
	 */
	public static String parseGrammarResult(String json) {
		StringBuffer ret = new StringBuffer();
		try {
			JSONTokener tokener = new JSONTokener(json);
			JSONObject joResult = new JSONObject(tokener);

			JSONArray words = joResult.getJSONArray("ws");
			for (int i = 0; i < words.length(); i++) {
				JSONArray items = words.getJSONObject(i).getJSONArray("cw");
				for(int j = 0; j < items.length(); j++)
				{
					JSONObject obj = items.getJSONObject(j);
					if(obj.getString("w").contains("nomatch"))
					{
						ret.append("没锟斤拷匹锟斤拷锟斤拷.");
						return ret.toString();
					}
					ret.append("锟斤拷锟斤拷锟斤拷锟�" + obj.getString("w"));
					ret.append("锟斤拷锟斤拷锟脚度★拷" + obj.getInt("sc"));
					ret.append("\n");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			ret.append("没锟斤拷匹锟斤拷锟斤拷.");
		} 
		return ret.toString();
	}
	
	/**
	 * 锟斤拷锟斤拷锟斤拷锟斤拷Json锟斤拷式锟斤拷锟斤拷
	 * @param json
	 * @return
	 */
	public static String parseUnderstandResult(String json) {
		StringBuffer ret = new StringBuffer();
		try {
			JSONTokener tokener = new JSONTokener(json);
			JSONObject joResult = new JSONObject(tokener);

			ret.append("锟斤拷应锟斤拷锟诫】" + joResult.getString("rc") + "\n");
			ret.append("锟斤拷转写锟斤拷锟斤拷锟�" + joResult.getString("text") + "\n");
			ret.append("锟斤拷锟斤拷锟斤拷锟斤拷锟狡★拷" + joResult.getString("service") + "\n");
			ret.append("锟斤拷锟斤拷锟斤拷锟斤拷锟狡★拷" + joResult.getString("operation") + "\n");
			ret.append("锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟�" + json);
		} catch (Exception e) {
			e.printStackTrace();
			ret.append("没锟斤拷匹锟斤拷锟斤拷.");
		} 
		return ret.toString();
	}
}
