package com.qianqi.server.protocol.mode;




import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qianqi.server.GServerConfig;
import com.qianqi.server.GServerController;
import com.qianqi.server.game.model.GBlock;
import com.qianqi.server.game.model.GBubble;
import com.qianqi.server.game.model.GRoom;
import com.qianqi.server.game.tools.GModelTool;
import com.qianqi.server.handler.GSessionHandler;
import com.qianqi.server.protocol.GData;
import com.qianqi.server.protocol.GProtocol;
import com.qianqi.server.session.GSession;
import com.qianqi.web.model.GUser;
import com.qianqi.web.service.GUserService;
import com.qianqi.web.tools.GBeanUtils;
import com.qianqi.web.tools.GTools;

public class GModeUser {
	private final static Logger logger = LoggerFactory.getLogger(GModeUser.class);
	private static GModeUser instance;
	private static GUserService userService;
	
		
	public static GModeUser getInstance()
	{
		if(instance == null)
		{
			instance = new GModeUser();
			userService = GBeanUtils.getBean("GUserServiceImpl");
			
		}					
		return instance;
	}
	
	public void updateUser(GUser user)
	{
		userService.update(user);
	}
	
	public synchronized void login(IoSession session, String data)
	{		
		logger.info(data);
		JSONObject revData = JSONObject.fromObject(data);
		String uid = revData.getString("uid");
		//判断是否已在线 如果是就关闭
		GSessionHandler.getInstance().closeSessionByUid(uid);
		GUser user = userService.find(uid);
		GSession gsession = GSessionHandler.getInstance().getGSessionById(session.getId());
		gsession.setUser(user);
		//登录成功返回公用数据
		JSONObject obj = new JSONObject();
		obj.put("result", true);
//		obj.put("bulletId", GServerConfig.bulletId);
//		obj.put("bullets", GServerConfig.bullets);
//		obj.put("roomWidth", GServerConfig.roomWidth);
//		obj.put("roomHeight", GServerConfig.roomHeight);
		obj.put("exps", GServerConfig.bubble.getString("exp"));
		obj.put("grows", GServerConfig.bubble.getString("grow"));
		obj.put("skins", GServerConfig.skins);
		obj.put("res_name", GServerConfig.res_name);
		obj.put("shareAward", GServerConfig.shareAward);
		GBubble bubble = GModelTool.getBubble(gsession.getUser().getUid(),true,1000,1000);
		obj.put("bubble", JSONObject.fromObject(bubble));
		
		int mapId = GTools.getRand(0, GServerConfig.maps.size());
		JSONObject map = GServerConfig.maps.getJSONObject(mapId);
		
		obj.put("mapId", mapId );
		obj.put("roomWidth", (int) map.getDouble("w"));
		obj.put("roomHeight", (int) map.getDouble("h"));
		obj.put("mapPosX", (float) map.getDouble("x"));
		obj.put("mapPosY", (float) map.getDouble("y"));
		
		GData data2 = new GData(GProtocol.MODE_USER_LOGIN_RESULT, obj.toString());
		session.write(data2.pack());  
	}
	
	public synchronized void heartBeat(IoSession session, String data)
	{
		GSession gsession = GSessionHandler.getInstance().getGSessionById(session.getId());
		if(gsession != null)
		{
			gsession.setHeartBeatTime(System.currentTimeMillis());
			GData data2 = new GData(GProtocol.MODE_USER_HERTBEAT_RESULT, "1");
			gsession.send(data2.pack());  
		}
	}
	
	public synchronized void enterRoom(IoSession session, String data)
	{
		//进入房间之前，让已经在房间的更新数据，包括坐标，角度，状态
		GSession gsession = GSessionHandler.getInstance().getGSessionById(session.getId());
		GBubble bubble = GModelTool.getBubble(gsession.getUser().getUid(),false,1000,1000);
		
		boolean b = GServerController.getInstance().allotRoom(session.getId(), bubble);
		JSONObject obj = new JSONObject();
		if(b)
		{
			GRoom room = GServerController.getInstance().findRoom(gsession.getRoomId());
			bubble.setSkinId(gsession.getUser().getSkinId());
			bubble.setX(GTools.getRand(100, room.getMapWidth()-100));
			bubble.setY(GTools.getRand(100, room.getMapHeight()-100));
			while(room.isCoincide(bubble))
			{
				bubble.setX(GTools.getRand(100, room.getMapWidth()-100));
				bubble.setY(GTools.getRand(100, room.getMapHeight()-100));
			}
			//自己
			JSONObject bubble_data = JSONObject.fromObject(bubble);
			//水滴
//			Collection<GBlock> list =  room.getBlocks().values();
//			JSONArray block_data = JSONArray.fromObject(list);
			//云
//			JSONArray clounds_data = JSONArray.fromObject(room.getClounds());			
			//机器人
			Collection<GBubble> robots_list = room.getRobots().values();
			JSONArray robots_data = JSONArray.fromObject(robots_list);
			//其他玩家
			Collection<GBubble> bubble_list = room.getBubbles().values();
			JSONArray bubbles_data = JSONArray.fromObject(bubble_list);
			
//			obj.put("bubble", bubble_data);
//			obj.put("blocks", block_data);
			obj.put("robots", robots_data);
			obj.put("bubbles", bubbles_data);
//			obj.put("clounds", clounds_data);
			
			obj.put("mapId", room.getMapId());
			obj.put("roomWidth", room.getMapWidth());
			obj.put("roomHeight", room.getMapHeight());
			obj.put("mapPosX", room.getMapPosX());
			obj.put("mapPosY", room.getMapPosY());
			
			obj.put("uid", gsession.getUser().getUid());
			obj.put("result", b);	

			GData data2 = new GData(GProtocol.MODE_USER_ENTERROOM_RESULT, obj.toString());
			session.write(data2.pack());

			//给其他玩家发送自己进入房间信息
			obj = new JSONObject();
			obj.put("uid", gsession.getUser().getUid());
			obj.put("result", b);	
			obj.put("bubble", bubble_data);
			obj.put("clear",bubble.clear);
			String otherData = obj.toString();
			GData otherData2 = new GData(GProtocol.MODE_GAME_JOINROOM_RESULT, otherData);
			otherData = otherData2.pack();
		
			for (Map.Entry<Long, GBubble> entry : room.getBubbles().entrySet()) 
			{
				long sessionId = entry.getKey();
				GSession gs = GSessionHandler.getInstance().getGSessionById(sessionId);
				if(gs != null && sessionId != session.getId())
				{
					gs.getSession().write(otherData);
				}
			}			
		}		
		else
		{
			obj.put("uid", gsession.getUser().getUid());
			obj.put("result", b);		
			GData data2 = new GData(GProtocol.MODE_USER_ENTERROOM_RESULT, obj.toString());
			session.write(data2.pack());
		}
	}
	
	public synchronized void addBlock(IoSession session, String data)
	{
		GSession gsession = GSessionHandler.getInstance().getGSessionById(session.getId());
		GRoom room = GServerController.getInstance().findRoom(gsession.getRoomId());
		if(room == null)
			return;
		//水晶需要分批加载
		Collection<GBlock> list = room.getBlocks().values();
		int index = 0;
		int i=0;
		List<GBlock> b_list = new ArrayList<GBlock>();
		for(GBlock b : list)
		{
			if(index == 100 || i == list.size()-1)
			{
				JSONObject obj2 = new JSONObject();
				obj2.put("result", (i == list.size()-1));
				obj2.put("list", JSONArray.fromObject(b_list));
				GData data5 = new GData(GProtocol.MODE_USER_ADDBLOCK_RESULT, obj2.toString());
				session.write(data5.pack());
				index = 0;					
				b_list = new ArrayList<GBlock>();
			}
			b_list.add(b);
			index++;	
			i++;
		}
	}
	
	//断线重连
	public synchronized void recConn(IoSession session, String data)
	{
		JSONObject obj = JSONObject.fromObject(data);
		String uid = obj.getString("uid");
		//判断是否已在线 如果是就关闭
		GSessionHandler.getInstance().closeSessionByUid(uid);
		GUser user = userService.find(uid);
		GSession gsession = GSessionHandler.getInstance().getGSessionById(session.getId());
		gsession.setUser(user);
		
		String type = obj.getString("type");
		//战斗时断线
		if("game".equals(type))
		{
			GBubble bubble = GModelTool.getBubble(gsession.getUser().getUid(),false,1000,1000);
			bubble.setX((float)obj.getDouble("x"));
			bubble.setY((float)obj.getDouble("y"));
			bubble.setRotate((float)obj.getDouble("rotate"));
			bubble.setDirX((float)obj.getDouble("dirX"));
			bubble.setDirY((float)obj.getDouble("dirY"));
			bubble.setSkinId(gsession.getUser().getSkinId());
			
			boolean b = GServerController.getInstance().allotRoom(session.getId(), bubble);
			obj = new JSONObject();
			if(b)
			{
				GRoom room = GServerController.getInstance().findRoom(gsession.getRoomId());
				//自己
				JSONObject bubble_data = JSONObject.fromObject(bubble);
				//水滴
//				Collection<GBlock> list = room.getBlocks().values();
//				JSONArray block_data = JSONArray.fromObject(list);
				//云
//				JSONArray clounds_data = JSONArray.fromObject(room.getClounds());			
				//机器人
				Collection<GBubble> robots_list = room.getRobots().values();
				JSONArray robots_data = JSONArray.fromObject(robots_list);
				//其他玩家
				Collection<GBubble> bubble_list = room.getBubbles().values();
				JSONArray bubbles_data = JSONArray.fromObject(bubble_list);
				
//				obj.put("bubble", bubble_data);
				obj.put("type", type);
//				obj.put("blocks", block_data);
				obj.put("robots", robots_data);
				obj.put("bubbles", bubbles_data);
//				obj.put("clounds", clounds_data);
				
				obj.put("uid", gsession.getUser().getUid());
				obj.put("result", b);		
				GData data2 = new GData(GProtocol.MODE_USER_RECCONN_RESULT, obj.toString());
				session.write(data2.pack());
				
				//给其他玩家发送自己进入房间信息
				obj = new JSONObject();
				obj.put("uid", gsession.getUser().getUid());
				obj.put("result", b);	
				obj.put("bubble", bubble_data);
				String otherData = obj.toString();
				GData otherData2 = new GData(GProtocol.MODE_GAME_JOINROOM_RESULT, otherData);
				otherData = otherData2.pack();
				for (Map.Entry<Long, GBubble> entry : room.getBubbles().entrySet()) 
				{
					long sessionId = entry.getKey();
					GSession gs = GSessionHandler.getInstance().getGSessionById(sessionId);
					if(gs != null && sessionId != session.getId())
					{
						gs.getSession().write(otherData);
					}
				}			
			}		
			else
			{
				obj.put("type", type);
				obj.put("uid", gsession.getUser().getUid());
				obj.put("result", b);		
				GData data2 = new GData(GProtocol.MODE_USER_RECCONN_RESULT, obj.toString());
				session.write(data2.pack());
			}
		}
	}
	
	//修改名字
	public synchronized void updateName(IoSession session, String data)
	{
		GSession gsession = GSessionHandler.getInstance().getGSessionById(session.getId());
		JSONObject obj = JSONObject.fromObject(data);
		String name = obj.getString("name");
		
		GUser u = userService.findByName(name);
		boolean result = true;
		if(u == null)
		{
			GUser user = gsession.getUser();
			String uName = user.getName();
			boolean uVisitor = user.isVisitor();
			
			user.setName(name);
			user.setVisitor(false);
			result = userService.update(user);
			
			if(!result)
			{
				user.setName(uName);
				user.setVisitor(uVisitor);
			}
		}
		else
		{
			result = false;
		}
		
		obj = new JSONObject();
		obj.put("result", result);	
		obj.put("name", name);
		String otherData = obj.toString();
		GData otherData2 = new GData(GProtocol.MODE_USER_UPDATENAME_RESULT, otherData);
		otherData = otherData2.pack();
		gsession.send(otherData);
	}
	//修改头像
	public synchronized void updateHead(IoSession session, String data)
	{
		GSession gsession = GSessionHandler.getInstance().getGSessionById(session.getId());
		JSONObject obj = JSONObject.fromObject(data);
		int headId = obj.getInt("headId");
		
		GUser user = gsession.getUser();
		user.setHeadId(headId);
		boolean result = userService.update(user);
		
		obj = new JSONObject();
		obj.put("result", result);	
		obj.put("headId", headId);
		GData otherData = new GData(GProtocol.MODE_USER_UPDATHEAD_RESULT, obj.toString());
		gsession.send(otherData.pack());
	}
	//购买皮肤
	public synchronized void buySkin(IoSession session, String data)
	{
		GSession gsession = GSessionHandler.getInstance().getGSessionById(session.getId());
		JSONObject obj = JSONObject.fromObject(data);
		int skinId = obj.getInt("skinId");
		JSONObject skin = GServerConfig.skins.getJSONObject(skinId-1);
		GUser user = gsession.getUser();
		boolean result = false;
		if(skin != null && user != null)
		{
			int price = skin.getInt("price");
			if(user.getCrystal() >= price)
			{
				user.setCrystal(user.getCrystal() - price);
				user.setSkins(user.getSkins() + "," + skinId);
				user.setSkinNum(user.getSkinNum() + 1);
				result = true;
			}
		}
		if(result)
		result = userService.update(user);
		
		obj = new JSONObject();
		obj.put("result", result);	
		obj.put("crystal", user.getCrystal());
		obj.put("skins", user.getSkins());
		obj.put("skinNum", user.getSkinNum());
		GData otherData = new GData(GProtocol.MODE_USER_BUYSKIN_RESULT, obj.toString());
		gsession.send(otherData.pack());
	}
	//使用皮肤
	public synchronized void useSkin(IoSession session, String data)
	{
		GSession gsession = GSessionHandler.getInstance().getGSessionById(session.getId());
		JSONObject obj = JSONObject.fromObject(data);
		int skinId = obj.getInt("skinId");		
		GUser user = gsession.getUser();
		boolean result = false;
		if(user != null)
		{
			user.setSkinId(skinId);
			result = true;
		}
		if(result)
		result = userService.update(user);
		
		obj = new JSONObject();
		obj.put("result", result);	
		obj.put("skinId", skinId);
		GData otherData = new GData(GProtocol.MODE_USER_USESKIN_RESULT, obj.toString());
		gsession.send(otherData.pack());
	}
	//最新个人信息
	public synchronized void infos(IoSession session, String data)
	{
		GSession gsession = GSessionHandler.getInstance().getGSessionById(session.getId());
		GUser user = gsession.getUser();
		float skinNumRank = 1;
		float cumKillRank = 1;
		float mvpRank = 1;
		float maxKillRank = 1;
		LinkedHashMap<String, String> lhm = new LinkedHashMap<String, String>();
		lhm.put("skinNum", "desc");
		List<GUser> list = userService.find(null, lhm).getList();
		for(int i=0;i<list.size();i++)
		{
			GUser u = list.get(i);
			if(user.getId() == u.getId())
			{
				skinNumRank = (float)(i+1)/(float)list.size();
				break;
			}
		}
		
		lhm.clear();
		lhm.put("cumKill", "desc");
		list = userService.find(null, lhm).getList();
		for(int i=0;i<list.size();i++)
		{
			GUser u = list.get(i);
			if(user.getId() == u.getId())
			{
				cumKillRank = (float)(i+1)/(float)list.size();
				break;
			}
		}
		
		lhm.clear();
		lhm.put("mvp", "desc");
		list = userService.find(null, lhm).getList();
		for(int i=0;i<list.size();i++)
		{
			GUser u = list.get(i);
			if(user.getId() == u.getId())
			{
				mvpRank = (float)(i+1)/(float)list.size();
				break;
			}
		}
		
		lhm.clear();
		lhm.put("maxKill", "desc");
		list = userService.find(null, lhm).getList();
		for(int i=0;i<list.size();i++)
		{
			GUser u = list.get(i);
			if(user.getId() == u.getId())
			{
				maxKillRank = (float)(i+1)/(float)list.size();
				break;
			}
		}
		
		JSONObject obj = new JSONObject();
		obj.put("user", JSONObject.fromObject(user));	
		obj.put("skinNumRank", skinNumRank);
		obj.put("cumKillRank", cumKillRank);
		obj.put("mvpRank", mvpRank);
		obj.put("maxKillRank", maxKillRank);
		GData otherData = new GData(GProtocol.MODE_USER_INFOS_RESULT, obj.toString());
		gsession.send(otherData.pack());
	}
	//分享成功
	public synchronized void share(IoSession session, String data)
	{
		GSession gsession = GSessionHandler.getInstance().getGSessionById(session.getId());
		JSONObject obj = JSONObject.fromObject(data);
		GUser user = gsession.getUser();
		boolean result = false;
		if(user != null)
		{
			long now = System.currentTimeMillis();
			long last = now - 24*60*60*1000;
			if(user.getRewardTime() != null && user.getRewardTime() != 0)
				last = user.getRewardTime();
			
			if(now - last >= 12*60*60*1000)
			{
				user.setCrystal(user.getCrystal() + GServerConfig.shareAward);
				user.setRewardTime(now);
				result = true;
			}			
		}
		if(result)
		result = userService.update(user);
		
		obj = new JSONObject();
		obj.put("result", result);	
		obj.put("crystal", user.getCrystal());
		GData otherData = new GData(GProtocol.MODE_USER_SHARE_RESULT, obj.toString());
		gsession.send(otherData.pack());
	}
}
