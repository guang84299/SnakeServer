package com.qianqi.server.handler;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.mina.core.service.AbstractIoService;
import org.apache.mina.core.service.IoService;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qianqi.server.GServer;
import com.qianqi.server.GServerController;
import com.qianqi.server.protocol.GProtocolParse;
import com.qianqi.server.session.GSession;


public class GSessionHandler {

	private final Logger logger = LoggerFactory.getLogger(GSessionHandler.class);

	private static GSessionHandler handler;
	private static HashMap<Long, GSession> sessions;

	private AbstractIoService service;

	private GSessionHandler() {
		service = (AbstractIoService)GServer.getInstance();
		sessions = new HashMap<Long, GSession>();
//		startHeartBeat();
	}

	public static GSessionHandler getInstance() {
		if (handler == null) {
			handler = new GSessionHandler();
		}
		return handler;
	}

	public static HashMap<Long, GSession> getSessions() {
		return sessions;
	}

	public void create(IoSession session) {
		logger.info(session.getId() + " create...");
		synchronized (sessions) {
			sessions.put(session.getId(), new GSession(session));
		}
	}

	public void close(IoSession session) {	
		logger.info("session size=" + service.getManagedSessionCount());
		synchronized (sessions) {
			logger.info(session.getId() + "  close...");
			
			GSession gs = sessions.get(session.getId());
			GServerController.getInstance().leaveRoom(gs);
			sessions.remove(session.getId());
			
			logger.info("map size=" + sessions.size());
		}			
	}

	public void idle(IoSession session) {
		session.close(true);
	}

	public void received(IoSession session, Object message) {
		//解决客户端同时发送消息后，消息合一的问题
		String s[] = message.toString().split("\\s");
		if(s.length > 1)
		{
			for(int i=0;i<s.length-1;i++)
			{
				GProtocolParse.parse(session, s[i]);
			}
			String last = s[s.length-1];
			if(last != null && last.trim().length()>6)
			{
				GProtocolParse.parse(session, last);
			}
		}else
		{
			GProtocolParse.parse(session, message);
		}
	}

	public void send(IoSession session, Object message) {

	}
	
	public void exceptionCaught(IoSession session, Throwable cause){
//		service.getListeners().fireSessionDestroyed(session);
		logger.error("exceptionCaught:"+cause.getMessage());
	}
	
	public void inputClosed(IoSession session) {
		if(session.isConnected() || !session.isClosing())
		{
			session.close(true);
		}				
	}
	
	public GSession getGSessionById(long sessionId)
	{
		return sessions.get(sessionId);
	}
	
	//根据uid关闭session
	public void closeSessionByUid(String uid)
	{
		GSession session = getSessionByUid(uid);
		if(session != null)
		{
			session.getSession().close(true);
		}
	}

	// 根据uid 获得在线session
	public GSession getSessionByUid(String uid)
	{
		synchronized (sessions) {
			for(Map.Entry<Long,GSession> entry : sessions.entrySet())
			{
				GSession val = entry.getValue();
				if(val.getUser() != null && uid.equals(val.getUser().getUid()))
				{
					return val;
				}
			}			
		}
		return null;
	}
	
	//关闭所有
	public void closeAllSession()
	{
		synchronized (sessions)
		{
			for(GSession val : sessions.values())
			{
				val.getSession().close(true);
			}
		}
	}
	
	//心跳检测
	public void startHeartBeat()
	{
		new Thread(){
			public void run() {
				while(true)
				{
					try {
						Thread.sleep(10*1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					long time = System.currentTimeMillis();
					synchronized (sessions)
					{
						for(GSession val : sessions.values())
						{
							if(time - val.getHeartBeatTime() > 10*1000)
							{						
								val.getSession().close(false);								
							}							
						}
					}
				}
			};
		}.start();
	}
}
