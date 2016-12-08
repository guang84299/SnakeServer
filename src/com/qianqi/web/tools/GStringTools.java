package com.qianqi.web.tools;

import net.sf.json.JSONObject;

public class GStringTools {

	public static boolean isEmpty(String str)
	{
		if(str == null || "".equals(str) || "".equals(str.trim()))
			return true;
		return false;
	}
	
	//返回一个json数据
	public static JSONObject getResPonseData()
	{
		JSONObject obj = new JSONObject();
		return obj;
	}
}
