package com.qianqi.server.cache;

import net.sf.json.JSONObject;

import com.qianqi.web.model.GUser;
import com.qianqi.web.tools.GStringTools;


public class GUserCache {
	private static GUserCache _instance;
	private GUserCache(){}
	
	public static GUserCache getInstance()
	{
		if(_instance == null)
		{
			_instance = new GUserCache();
		}
		return _instance;
	}
	
	
}
