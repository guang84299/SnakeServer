package com.qianqi.server.handler;

import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;


public class GCoreHandler implements IoHandler{
	private GSessionHandler gSessionHandler = GSessionHandler.getInstance();
	public void sessionCreated(IoSession session) throws Exception {
		gSessionHandler.create(session);
	}

	public void sessionOpened(IoSession session) throws Exception {
		// TODO Auto-generated method stub
	}

	public void sessionClosed(IoSession session) throws Exception {
		gSessionHandler.close(session);
	}

	public void sessionIdle(IoSession session, IdleStatus status)
			throws Exception {
		gSessionHandler.idle(session);
	}

	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
		gSessionHandler.exceptionCaught(session, cause);
	}

	public void messageReceived(IoSession session, Object message)
			throws Exception {
		gSessionHandler.received(session, message);		
	}

	public void messageSent(IoSession session, Object message) throws Exception {
		gSessionHandler.send(session, message);
	}

	public void inputClosed(IoSession session) throws Exception {
		gSessionHandler.inputClosed(session);
	}
}
