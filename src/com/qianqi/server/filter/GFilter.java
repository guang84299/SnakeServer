package com.qianqi.server.filter;

import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.core.filterchain.IoFilterChain;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.write.WriteRequest;

public class GFilter implements IoFilter{

	public void init() throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void destroy() throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void onPreAdd(IoFilterChain parent, String name,
			NextFilter nextFilter) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void onPostAdd(IoFilterChain parent, String name,
			NextFilter nextFilter) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void onPreRemove(IoFilterChain parent, String name,
			NextFilter nextFilter) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void onPostRemove(IoFilterChain parent, String name,
			NextFilter nextFilter) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void sessionCreated(NextFilter nextFilter, IoSession session)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void sessionOpened(NextFilter nextFilter, IoSession session)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void sessionClosed(NextFilter nextFilter, IoSession session)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void sessionIdle(NextFilter nextFilter, IoSession session,
			IdleStatus status) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void exceptionCaught(NextFilter nextFilter, IoSession session,
			Throwable cause) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void inputClosed(NextFilter nextFilter, IoSession session)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void messageReceived(NextFilter nextFilter, IoSession session,
			Object message) throws Exception {
		// TODO Auto-generated method stub
		
	}

	
	public void messageSent(NextFilter nextFilter, IoSession session,
			WriteRequest writeRequest) throws Exception {
		// TODO Auto-generated method stub
		
	}

	
	public void filterClose(NextFilter nextFilter, IoSession session)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	
	public void filterWrite(NextFilter nextFilter, IoSession session,
			WriteRequest writeRequest) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
