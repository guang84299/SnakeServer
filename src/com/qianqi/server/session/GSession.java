package com.qianqi.server.session;

import org.apache.mina.core.session.IoSession;

import com.qianqi.web.model.GUser;

public class GSession {
	private IoSession session;
	private GUser user;
	private long roomId;
	private long heartBeatTime;
	
	public GSession(IoSession session) {
		super();
		this.session = session;
		heartBeatTime = System.currentTimeMillis();
	}
	
	public void send(Object message)
	{
		if(session.isConnected() && !session.isClosing())
		{
			session.write(message);
		}
	}

	public IoSession getSession() {
		return session;
	}

	public void setSession(IoSession session) {
		this.session = session;
	}

	public GUser getUser() {
		return user;
	}

	public void setUser(GUser user) {
		this.user = user;
	}

	public long getRoomId() {
		return roomId;
	}

	public void setRoomId(long roomId) {
		this.roomId = roomId;
	}

	public long getHeartBeatTime() {
		return heartBeatTime;
	}

	public void setHeartBeatTime(long heartBeatTime) {
		this.heartBeatTime = heartBeatTime;
	}

	
	
}
