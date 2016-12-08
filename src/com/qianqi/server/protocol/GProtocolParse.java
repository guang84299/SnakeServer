package com.qianqi.server.protocol;

import java.lang.reflect.Method;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import net.sf.json.JSONObject;

/**
 * 自定义协议 基于json
 * @author guang
 *
 */

public class GProtocolParse {
	private static final Logger logger = LoggerFactory.getLogger(GProtocolParse.class);
	public static final int MODE_TEST = -1;
	public static final String PROTOCOL_CLASS_HEAD = "com.qianqi.server.protocol.mode.";
	public static final String PROTOCOL_CLASS_SESSION = "org.apache.mina.core.session.IoSession";
	public static final String PROTOCOL_CLASS_STRING = "java.lang.String";
	public static final String PROTOCOL_CLASS_INSTANCE = "getInstance";
	

	public static void parse(IoSession session, Object message)
	{		
		try {
			JSONObject data = JSONObject.fromObject(message.toString());
			String[] mode = data.getString("mode").split("_");
			String className = mode[0];
			String methodName = mode[1];	
			Class<?> c = Class.forName(PROTOCOL_CLASS_HEAD + className);
			Method m = c.getMethod(PROTOCOL_CLASS_INSTANCE, new Class[]{});	
			Object obj = m.invoke(c);
			Class<?> args[] = new Class[]{Class.forName(PROTOCOL_CLASS_SESSION),Class.forName(PROTOCOL_CLASS_STRING)};
			m = c.getMethod(methodName, args);	
			m.invoke(obj,session,data.getString("body"));
		} catch (Exception e) {
			logger.error("数据解析失败！"+e.getMessage() + "  data="+message.toString());
		}
	}
}