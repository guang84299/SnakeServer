package com.qianqi.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URL;

import org.apache.log4j.PropertyConfigurator;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qianqi.server.filter.GFilter;
import com.qianqi.server.handler.GCoreHandler;
import com.qianqi.server.websocket.GCodecFactory;


public class GServer {
	private static final Logger logger = LoggerFactory.getLogger(GServer.class); 
	private final int PORT = 9123;  
	private final String FILTER_LOGGER = "logger";
	private final String FILTER_CODEC = "codec";
	
	private static NioSocketAcceptor acceptor;
	
	public GServer()
	{
		//初始化日志系统
//		URL url = GServer.class.getClassLoader().getResource("log4j.properties");		
//		PropertyConfigurator.configure( url.getPath() );
		//初始化配置
		GServerConfig.initConfig();
		
		if(acceptor == null)
			acceptor = new NioSocketAcceptor();               
	}
	
	public static IoAcceptor getInstance()
	{
		if(acceptor == null)
		{
			logger.error("服务端还未启动...");
		}
		return acceptor;
	}
	
	public void start()
	{
//		acceptor.getFilterChain().addLast( FILTER_LOGGER, new LoggingFilter() );  
        acceptor.getFilterChain().addLast( FILTER_CODEC, new ProtocolCodecFilter(new GCodecFactory()));  
       
        acceptor.setHandler(new GCoreHandler());  
        acceptor.getSessionConfig().setReadBufferSize(2048); 
        acceptor.getSessionConfig().setMinReadBufferSize(1024);
        acceptor.getSessionConfig().setMaxReadBufferSize(4096);
        acceptor.getSessionConfig().setIdleTime( IdleStatus.BOTH_IDLE, 20);
//        acceptor.getSessionConfig().setTcpNoDelay(true);
//        acceptor.getSessionConfig().setSoLinger(0);
//        acceptor.getSessionConfig().setSendBufferSize(2048);
//        acceptor.getSessionConfig().setReceiveBufferSize(2048);
        acceptor.setReuseAddress(true);//设置的是主服务监听的端口可以重用        
        acceptor.getSessionConfig().setReuseAddress(true);//设置每一个非主监听连接的端口可以重用  
		try {
			acceptor.bind(new InetSocketAddress(PORT));
			logger.info("服务端启动成功...     端口号为：" + PORT);
		} catch (IOException e) {
			logger.error("服务端启动异常....", e);
			e.printStackTrace();
		}
	}
	
	public void stop()
	{
		acceptor.unbind();
	}
	
	public void addFilter(GFilter filter)
	{
		acceptor.getFilterChain().addLast("",filter);
	}
}
