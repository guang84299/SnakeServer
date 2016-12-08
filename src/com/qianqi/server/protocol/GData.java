package com.qianqi.server.protocol;

import net.sf.json.JSONObject;

public class GData {
	private String mode;//模块
	private long length;//数据长度
	private String body;//数据体
	private long bodyLength;//数据体长度
	
	public GData()
	{
		
	}
	
	public GData(String mode,String body)
	{
		init(mode,body);
	}
	
	private void init(String mode,String body)
	{
		this.mode = mode;
		this.body = body;
		if(this.body != null)
		{
			this.bodyLength = body.length();
			this.length = pack().length();
		}		
	}
	
	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
		init(mode,this.body);
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
		init(this.mode,body);
	}

	public String pack()
	{		
		String packs = JSONObject.fromObject(this).toString();
		//在这里对数据做处理
		return packs;
	}
	
	
}
