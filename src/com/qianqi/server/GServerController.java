package com.qianqi.server;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qianqi.server.game.model.GBubble;
import com.qianqi.server.game.model.GRoom;
import com.qianqi.server.handler.GSessionHandler;
import com.qianqi.server.protocol.mode.GModeGame;
import com.qianqi.server.session.GSession;

public class GServerController {
	private static final Logger logger = LoggerFactory.getLogger(GServerController.class);
	private static GServerController _instance = null;
	private int roomIndex = 0;
	private HashMap<Long,GRoom> rooms = new HashMap<Long, GRoom>();
	
	private GServerController(){}
	public static GServerController getInstance()
	{
		if(_instance == null)
		{
			_instance = new GServerController();
		}
		return _instance;
	}
	//分配一个服务器
	public String allotServerId()
	{	
		String serverId = "";
		Set<String> keSet=GServerConfig.serverList.keySet();  
        for (Iterator<String> iterator = keSet.iterator(); iterator.hasNext();) {  
        	serverId = iterator.next();                
        }  
        return serverId;
	}
	//根据serverId获得ip
	public String getServerIp(String serverId)
	{
		return GServerConfig.serverList.get(serverId);
	}
	//为房间分配一个id
	private long allotRoomId()
	{
		return ++roomIndex;
	}
	//为一个用户分配一个房间
	public boolean allotRoom(long sessionId, GBubble bubble)
	{
		if(rooms.size() > GServerConfig.maxRooms)
		{
			logger.error("房间数超出限制!!! 当前房间数："+rooms.size());
			return false;
		}
		GSession gs = GSessionHandler.getInstance().getGSessionById(sessionId);
		//先判断是否有房间 
		//如果还没有，就先创建一个
		if(rooms.size() == 0)
		{
			GRoom room = createRoom();
			bubble.setRoomId(room.getRoomId());
			gs.setRoomId(room.getRoomId());
			room.add(gs.getSession().getId(), bubble);	
			room.start();
			return true;
		}
		else
		{
			// 先找到人数最少的房间
			GRoom room = null;
			for (Map.Entry<Long, GRoom> entry : rooms.entrySet()) 
			{
				GRoom r = entry.getValue();
				if(r.getTime() < GServerConfig.roomTime*0.4f)
					continue;
				if(room == null)
				{
					room = r;
				}
				else
				{
					if(r.getSessionNum() < room.getSessionNum())
					{
						room = r;
					}
				}
			}			
			if(room != null && room.getSessionNum() < GServerConfig.maxRoomSessions)
			{
				bubble.setRoomId(room.getRoomId());
				gs.setRoomId(room.getRoomId());
				room.add(gs.getSession().getId(), bubble);			
				return true;
			}
			else
			{
				room = createRoom();
				bubble.setRoomId(room.getRoomId());
				gs.setRoomId(room.getRoomId());
				room.add(gs.getSession().getId(), bubble);
				room.start();
				return true;
			}
		}		
	}
	//离开房间
	public void leaveRoom(GSession gs)
	{
		if(gs.getRoomId() != 0)
		{
			GRoom room = rooms.get(gs.getRoomId());	
			if(room != null)
			{
				//给其他人发送离开通知
				GModeGame.getInstance().leaveRoom(gs.getSession().getId());
				//先移除房间内sessionid
				room.remove(gs.getSession().getId());
				logger.info("当前房间数："+rooms.size() + "  当前房间人数："+ room.getSessionNum());	
			}				
		}		
	}
	
	private GRoom createRoom()
	{
		GRoom room = new GRoom(allotRoomId());
		rooms.put(room.getRoomId(), room);
		//房间数超出限制
		if(rooms.size() > GServerConfig.maxRooms)
		{
			logger.warn("房间数超出限制!!! 当前房间数："+rooms.size());
		}
		logger.info("创建房间："+room.getRoomId() + " 当前房间数："+rooms.size());
		return room;
	}
	
	public void removeRoom(long roomId)
	{
		rooms.remove(roomId);
		logger.info("移除房间："+roomId + " 当前房间数："+rooms.size());
	}
	
	//根据房间id查找房间
	public GRoom findRoom(long roomId)
	{
		return rooms.get(roomId);
	}
	//根据房间id 和 sessionid 查找 plane
	public GBubble findPlane(long roomId,long sessionId)
	{
		GRoom room = findRoom(roomId);
		if(room != null)
		{
			return room.find(sessionId);
		}
		return null;
	}
	//移除所有用户和房间
	public void removeAllUserRoom()
	{
		GSessionHandler.getInstance().closeAllSession();
		rooms.clear();
		logger.info("当前房间数："+rooms.size());
	}	
}
