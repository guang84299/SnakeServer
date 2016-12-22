package com.qianqi.server.protocol.mode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qianqi.server.GServerConfig;
import com.qianqi.server.GServerController;
import com.qianqi.server.game.model.GBullet;
import com.qianqi.server.game.model.GBlock;
import com.qianqi.server.game.model.GBubble;
import com.qianqi.server.game.model.GDrop;
import com.qianqi.server.game.model.GRank;
import com.qianqi.server.game.model.GRoom;
import com.qianqi.server.game.tools.GModelTool;
import com.qianqi.server.handler.GSessionHandler;
import com.qianqi.server.protocol.GData;
import com.qianqi.server.protocol.GProtocol;
import com.qianqi.server.session.GSession;
import com.qianqi.web.tools.GTools;

public class GModeGame {
	private final static Logger logger = LoggerFactory.getLogger(GModeGame.class);
	private static GModeGame _instance;
	private GModeGame(){}
	
	public static GModeGame getInstance()
	{
		if(_instance == null)
		{
			_instance = new GModeGame();
		}
		return _instance;
	}
	
	public synchronized void startGame(IoSession session, String data)
	{		
		GSession gsession = GSessionHandler.getInstance().getGSessionById(session.getId());		
		GRoom room = GServerController.getInstance().findRoom(gsession.getRoomId());		
		GBubble plane = room.find(gsession.getSession().getId());		
		plane.setState(GBubble.STATE.IDLE);
		
		//增加mvp次数
		int mvp = gsession.getUser().getMvp()+1;
		gsession.getUser().setMvp(mvp);
		
		JSONObject obj = new JSONObject();
		obj.put("uid", plane.getUid());
		
		GData data2 = new GData(GProtocol.MODE_GAME_STARTGAME_RESULT, obj.toString());
		data = data2.pack();
		for (Map.Entry<Long, GBubble> entry : room.getBubbles().entrySet()) 
		{
			long sessionId = entry.getKey();
			GSession gs = GSessionHandler.getInstance().getGSessionById(sessionId);
			if(gs != null)
			{
				gs.send(data);
			}
		}				
	}
	//移动
	public synchronized void move(IoSession session, String data)
	{
		JSONObject obj = JSONObject.fromObject(data);
//		float dirX = (float) obj.getDouble("dirX");
//	    float dirY = (float) obj.getDouble("dirY");
//	    float x = (float) obj.getDouble("x");
//	    float y = (float) obj.getDouble("y");
	    float angle = (float) obj.getDouble("angle");
	    boolean robot = obj.getBoolean("robot");
	    boolean speedUp = obj.getBoolean("speedUp");
	    String uid = obj.getString("uid");
		
		GSession gsession = GSessionHandler.getInstance().getGSessionById(session.getId());		
		GRoom room = GServerController.getInstance().findRoom(gsession.getRoomId());
		if(room == null)
			return;
		GBubble bubble = null;
		if(robot)
			bubble = room.findRobot(uid);
		else
			bubble = room.find(gsession.getSession().getId());
		
		if(bubble != null && bubble.getState() != GBubble.STATE.DIE)
		{
			if(speedUp)
				bubble.setState(GBubble.STATE.SPEEDUP);
			else
				bubble.setState(GBubble.STATE.MOVE);
//			bubble.setDirX(dirX);
//			bubble.setDirY(dirY);
//			bubble.setX(x);
//			bubble.setY(y);
//			bubble.setRotate(rotate);
			bubble.setAngle(angle);
//			GData data3 = new GData(GProtocol.MODE_GAME_MOVE_RESULT, obj.toString());
//			data = data3.pack();
			
//			for (Map.Entry<Long, GBubble> entry : room.getBubbles().entrySet()) 
//			{
//				long sessionId = entry.getKey();
//				GSession gs = GSessionHandler.getInstance().getGSessionById(sessionId);
//				if(gs != null)
//				{
//					gs.send(data);
//				}
//			}	
		}	
	}
	//停止移动
	public synchronized void stopMove(IoSession session, String data)
	{
		JSONObject obj = JSONObject.fromObject(data);
		
		GSession gsession = GSessionHandler.getInstance().getGSessionById(session.getId());		
		GRoom room = GServerController.getInstance().findRoom(gsession.getRoomId());
		if(room == null)
			return;
		GBubble bubble = room.find(gsession.getSession().getId());	
		
		float x = (float) obj.getDouble("x");
	    float y = (float) obj.getDouble("y");
	    float rotate = (float) obj.getDouble("rotate");
	    boolean speedUp = obj.getBoolean("speedUp");
	    
//	    bubble.setX(x);
//	    bubble.setY(y);
	    bubble.setRotate(rotate);
	    bubble.setState(GBubble.STATE.IDLE);

		obj.put("uid", bubble.getUid());
		
		GData data3 = new GData(GProtocol.MODE_GAME_STOPMOVE_RESULT, obj.toString());
		data = data3.pack();
//		gsession.send(data);
		
		for (Map.Entry<Long, GBubble> entry : room.getBubbles().entrySet()) 
		{
			long sessionId = entry.getKey();
			GSession gs = GSessionHandler.getInstance().getGSessionById(sessionId);
			if(gs != null)
			{
				gs.send(data);
			}
		}
	}
	//倒计时
	public synchronized void roomCountDown(long roomId,long time)
	{
		GRoom room = GServerController.getInstance().findRoom(roomId);	
		for (Map.Entry<Long, GBubble> entry : room.getBubbles().entrySet()) 
		{
			long sessionId = entry.getKey();			
			GSession gs = GSessionHandler.getInstance().getGSessionById(sessionId);
			if(gs != null)
			{
				JSONObject obj = new JSONObject();	
				int t = (int) time;
				obj.put("time", t);
				GData data = new GData(GProtocol.MODE_GAME_ROOMCOUNTDOWN_RESULT, obj.toString());
				String data2 = data.pack();
				
				gs.send(data2);
			}
		}			
	}
	//房间时间到，结算界面
	public synchronized void roomTimeOut(long roomId)
	{
		GRoom room = GServerController.getInstance().findRoom(roomId);
		//发送自己结算数据
		List<GBubble> list = new ArrayList<GBubble>();
		for(GBubble bubble : room.getBubbles().values())
		{
			list.add(bubble);
		}
		for(GBubble bubble : room.getRobots().values())
		{
			list.add(bubble);
		}
		Collections.sort(list, new Comparator<GBubble>(){
            public int compare(GBubble arg0, GBubble arg1) {
                return arg1.getExp() - arg0.getExp();
            }
        });
		List<GRank> ranks = new ArrayList<GRank>();
		for(int i=0;i<list.size();i++)
		{
			GBubble bubble = list.get(i);
			GRank rank = new GRank();
			rank.setRank(i+1);
			rank.setUid(bubble.getUid());
			rank.setName(bubble.getName());
			rank.setKill(bubble.getKill());
			rank.setExp(bubble.getExp());
			rank.setDie(bubble.getDie());
			rank.setRewardNum(GServerConfig.getCrystalForRank(rank.getRank()));
			ranks.add(rank);
			
			GSession gs = GSessionHandler.getInstance().getSessionByUid(bubble.getUid());
			if(gs != null)
			{
				gs.getUser().setCrystal(gs.getUser().getCrystal() + rank.getRewardNum());
			}
		}
		JSONObject obj = new JSONObject();
		obj.put("ranks", JSONArray.fromObject(ranks));
		
		GData data = new GData(GProtocol.MODE_GAME_ROOMTIMEOUT_RESULT, obj.toString());
		String data2 = data.pack();		
		for (Map.Entry<Long, GBubble> entry : room.getBubbles().entrySet()) 
		{
			long sessionId = entry.getKey();
			GSession gs = GSessionHandler.getInstance().getGSessionById(sessionId);
			if(gs != null)
			{
				gs.send(data2);
			}
		}
		
		//告诉所有人，相当于死亡
//		obj = new JSONObject();		
//		obj.put("uid", gsession.getUser().getUid());
//		data = new GData(GProtocol.MODE_GAME_ROOMTIMEOUT_RESULT, obj.toString());
//		data2 = data.pack();
//		for (Map.Entry<Long, GBubble> entry : room.getBubbles().entrySet()) 
//		{
//			long sessionId2 = entry.getKey();
//			GSession gs = GSessionHandler.getInstance().getGSessionById(sessionId2);
//			if(gs != null && sessionId2 != sessionId)
//			{
//				gs.send(data2);
//			}
//		}
	}
	//离开房间 掉线
	public synchronized void leaveRoom(long sessionId)
	{
		GSession gsession = GSessionHandler.getInstance().getGSessionById(sessionId);
		GRoom room = GServerController.getInstance().findRoom(gsession.getRoomId());
		
		GModeUser.getInstance().updateUser(gsession.getUser());
		
		if(room != null)
		{
			GBubble bubble = room.find(sessionId);
			if(bubble != null)
			{
				//找到机器人，解除被携带 离开房间的时候执行
				for(String uid : bubble.getRobotUid())
				{
					GBubble conPlane = room.findRobot(uid);
					if(conPlane != null)
					{
						conPlane.setBubbleId(0);
					}
				}
				bubble.getRobotUid().clear();
			}		
			//清除房间数据
			room.remove(sessionId);
		}
		
		
		JSONObject obj = new JSONObject();	
		obj.put("uid", gsession.getUser().getUid());
		GData data = new GData(GProtocol.MODE_GAME_LEAVEROOM_RESULT, obj.toString());
		String data2 = data.pack();
		for (Map.Entry<Long, GBubble> entry : room.getBubbles().entrySet()) 
		{
			sessionId = entry.getKey();
			GSession gs = GSessionHandler.getInstance().getGSessionById(sessionId);
			if(gs != null)
			{
				gs.send(data2);
			}
		}
	}
	//主动请求离开
	public synchronized void questLeaveRoom(IoSession session, String data)
	{
		GSession gsession = GSessionHandler.getInstance().getGSessionById(session.getId());
		GRoom room = GServerController.getInstance().findRoom(gsession.getRoomId());

		GModeUser.getInstance().updateUser(gsession.getUser());
		
		JSONObject obj = new JSONObject();	
		obj.put("uid", gsession.getUser().getUid());
		GData data2 = new GData(GProtocol.MODE_GAME_QUESTLEAVEROOM_RESULT, obj.toString());
		String data3 = data2.pack();
		
		//房间已经销毁的情况
		if(room == null)
		{
			gsession.send(data3);
			return;
		}		
		for (Map.Entry<Long, GBubble> entry : room.getBubbles().entrySet()) 
		{
			long sessionId = entry.getKey();
			GSession gs = GSessionHandler.getInstance().getGSessionById(sessionId);
			if(gs != null)
			{
				gs.send(data3);
			}
		}
		GBubble bubble = room.find(session.getId());
		if(bubble != null)
		{
			//找到机器人，解除被携带 离开房间的时候执行
			for(String uid : bubble.getRobotUid())
			{
				GBubble conPlane = room.findRobot(uid);
				if(conPlane != null)
				{
					conPlane.setBubbleId(0);
				}
			}
			bubble.getRobotUid().clear();
		}		
		//清除房间数据
		room.remove(session.getId());
	}
	//添加水滴
	public synchronized void addBlock(long roomId,List<GBlock> list,int x,int y)
	{
		GRoom room = GServerController.getInstance().findRoom(roomId);	
		JSONObject obj = new JSONObject();
		obj.put("x", x);
		obj.put("y", y);
		obj.put("list", JSONArray.fromObject(list));
		GData data = new GData(GProtocol.MODE_GAME_ADDBLOCK_RESULT, obj.toString());
		String data2 = data.pack();
		for (Map.Entry<Long, GBubble> entry : room.getBubbles().entrySet()) 
		{
			long sessionId = entry.getKey();
			GSession gs = GSessionHandler.getInstance().getGSessionById(sessionId);
			if(gs != null)
			{
				gs.send(data2);
			}
		}
	}
	//吃水滴
	public synchronized void eatBlock(IoSession session, String data)
	{
		JSONObject obj = JSONObject.fromObject(data);
		int id = obj.getInt("id");
		int type = obj.getInt("type");
		int exp = obj.getInt("exp");
		boolean robot = obj.getBoolean("robot");
		String uid = obj.getString("uid");
		
		GSession gsession = GSessionHandler.getInstance().getGSessionById(session.getId());		
		GRoom room = GServerController.getInstance().findRoom(gsession.getRoomId());		
		if(room == null)
			return;
		
		GBlock block = room.getBlocks().get(id);
		if(block == null)
			return;
		int blockType = block.getBlockType();
		obj.put("blockType", blockType);
		
		room.removeBlock(id);
		
		GBubble bubble = null;
		if(robot)
			bubble = room.findRobot(uid);
		else
			bubble = room.find(session.getId());
		if(bubble != null)
		{
			if(type == 0 || type == 1)
			{
				bubble.setExp(bubble.getExp()+exp);
				if(bubble.getExp() > GServerConfig.getExpForLevel(bubble.getLevel()+1) && 
						bubble.getLevel() < GServerConfig.exps.size())
				{
					bubble.setLevel(bubble.getLevel()+1);
					bubble.setGrow(GServerConfig.getGrowForLevel(bubble.getLevel()));
				}
				obj.put("level", bubble.getLevel());
				obj.put("exp", bubble.getExp());
				obj.put("grow", bubble.getGrow());
			}
			//类型为2为血袋
			else if(type == 2)
			{
				
			}
			GData data3 = new GData(GProtocol.MODE_GAME_EATBLOCK_RESULT, obj.toString());
			data = data3.pack();
			for (Map.Entry<Long, GBubble> entry : room.getBubbles().entrySet()) 
			{
				long sessionId = entry.getKey();
				GSession gs = GSessionHandler.getInstance().getGSessionById(sessionId);
				if(gs != null)
				{
					gs.send(data);
				}
			}
		}		
	}
	//攻击
	public synchronized void attack(IoSession session, String data)
	{
		GSession gsession = GSessionHandler.getInstance().getGSessionById(session.getId());		
		GRoom room = GServerController.getInstance().findRoom(gsession.getRoomId());
		if(room == null)
			return;
		JSONObject obj = JSONObject.fromObject(data);		
		boolean robot = obj.getBoolean("robot");
		String uid = obj.getString("uid");
		int bulletId = obj.getInt("bulletId");
		GBubble bubble = null;
		if(robot)
			bubble = room.findRobot(uid);
		else
		{
			GSession gs = GSessionHandler.getInstance().getSessionByUid(uid);
			bubble = room.find(gs.getSession().getId());
		}
		
		if(bubble != null)
		{
			//判断是否达到攻击时间
			long dt = System.currentTimeMillis() - bubble.currAttackTime;
			GBullet bullet = GModelTool.getBullet(bulletId);
			
			if(bullet != null)
			{		
				if(bulletId == 1 && dt/1000.f < bullet.getCD())
					return;
				bubble.currAttackTime = System.currentTimeMillis();
				//发射子弹
				String bullets = bullet.getTid()+",";
				if(bulletId == 1)
				{
					for(int i=1;i<=bubble.getLevel();i++)
					{
						bullet = GModelTool.getBullet(bulletId);
						bullets += bullet.getTid()+",";
					}
				}
				else
				{
					for(int i=1;i<bullet.getOnceNum();i++)
					{
						bullet = GModelTool.getBullet(bulletId);
						bullets += bullet.getTid()+",";
					}
				}
				bullets = bullets.substring(0, bullets.length()-1);
				obj.put("tids", bullets);
				
				GData data3 = new GData(GProtocol.MODE_GAME_ATTACKL_RESULT, obj.toString());
				data = data3.pack();
				for (Map.Entry<Long, GBubble> entry : room.getBubbles().entrySet()) 
				{
					long sessionId = entry.getKey();
					GSession gs = GSessionHandler.getInstance().getGSessionById(sessionId);
					if(gs != null)
					{
						gs.send(data);
					}
				}
			}
		}		
	}
	
	//子弹碰撞
	public synchronized void bulletCollision(IoSession session, String data)
	{
		GSession gsession = GSessionHandler.getInstance().getGSessionById(session.getId());		
		GRoom room = GServerController.getInstance().findRoom(gsession.getRoomId());
		if(room == null)
			return;
		JSONObject obj = JSONObject.fromObject(data);

		boolean target_robot = obj.getBoolean("target_robot");
		String targetUid = obj.getString("target");
		boolean bubble_robot = obj.getBoolean("bubble_robot");
		String bubbleUid = obj.getString("bubble");
		
		GBubble targetBubble = null;
		if(target_robot)
		{
			targetBubble = room.findRobot(targetUid);
		}
		else
		{
	        GSession targetSession = GSessionHandler.getInstance().getSessionByUid(targetUid);
	        targetBubble = room.find(targetSession.getSession().getId());
		}
		GBubble bubble = null;
		GSession bubbleSession = null;
		//0是和边界碰撞
		if(!bubbleUid.equals("0"))
		{
			if(bubble_robot)
			{
				bubble = room.findRobot(bubbleUid);
			}
			else
			{
				bubbleSession = GSessionHandler.getInstance().getSessionByUid(bubbleUid);
		        bubble = room.find(bubbleSession.getSession().getId());
			}
		}
		
		JSONObject dieJson = null;
		if(targetBubble != null)
		{
			if(bubble != null)
			{
				if(bubble.getState() == GBubble.STATE.DIE)
				{
					return;
				}
				bubble.setKill(bubble.getKill()+1);
				long time = System.currentTimeMillis();
				if(time - bubble.killTime < 5*1000)
				{
					bubble.killNum += 1;
				}
				else
				{
					bubble.killNum = 1;
				}
				bubble.killTime = time;
				//不是机器人才会更新最大击杀
				if(bubbleSession != null)
				{
					int cumKill = bubbleSession.getUser().getCumKill() + 1;
					bubbleSession.getUser().setCumKill(cumKill);

					if(bubble.getKill() > bubbleSession.getUser().getMaxKill())
					{
						bubbleSession.getUser().setMaxKill(bubble.getKill());
					}
				}					
			}	
				
			targetBubble.setState(GBubble.STATE.DIE);
			targetBubble.setDie(targetBubble.getDie()+1);
			if(target_robot)
			{
				//找到机器人的携带者，解除携带				
				GSession conSession = GSessionHandler.getInstance().getGSessionById(targetBubble.getBubbleId());
				if(conSession != null)
				{
					GBubble conPlane = room.find(conSession.getSession().getId());
					if(conPlane != null)
					{
						conPlane.getRobotUid().remove(targetUid);
					}
				}					
			}
			dieJson = new JSONObject();
			//先计算排名
			List<GBubble> list = new ArrayList<GBubble>();
			for(GBubble bubble2 : room.getBubbles().values())
			{
				list.add(bubble2);
			}
			for(GBubble bubble2 : room.getRobots().values())
			{
				list.add(bubble2);
			}
			Collections.sort(list, new Comparator<GBubble>(){
	            public int compare(GBubble arg0, GBubble arg1) {
	                return arg1.getExp() - arg0.getExp();
	            }
	        });
			int rank = 1;
			for(int i=0;i<list.size();i++)
			{
				GBubble bubble2 = list.get(i);
				if(bubble2.getUid().equals(targetBubble.getUid()))
				{
					rank = i+1;
					break;
				}				
			}
			
			
			dieJson.put("killMe", bubbleUid);
			if(bubble != null)
			{
				dieJson.put("killMeName", bubble.getName());
				dieJson.put("killNum", bubble.killNum);
			}
			else
			{
				dieJson.put("killMeName", bubbleUid);
				dieJson.put("killNum", 1);
			}
			dieJson.put("rank", rank);
			dieJson.put("exp", targetBubble.getExp());
			dieJson.put("kill", targetBubble.getKill());
		}
				
		JSONObject obj2 = new JSONObject();
		obj2.put("target", targetUid);
		obj2.put("target_robot", target_robot);
		obj2.put("dieData", dieJson);
		//下发通知
		GData data3 = new GData(GProtocol.MODE_GAME_BULLETCOLLISION_RESULT, obj2.toString());
		data = data3.pack();
		for (Map.Entry<Long, GBubble> entry : room.getBubbles().entrySet()) 
		{
			long sessionId = entry.getKey();
			GSession gs = GSessionHandler.getInstance().getGSessionById(sessionId);
			if(gs != null )
			{
				gs.send(data);
			}
		}
	}
	//子弹改变目标
	public synchronized void bulletChangeTarget(IoSession session, String data)
	{
		GSession gsession = GSessionHandler.getInstance().getGSessionById(session.getId());		
		GRoom room = GServerController.getInstance().findRoom(gsession.getRoomId());
		if(room == null)
			return;
		JSONObject obj = JSONObject.fromObject(data);
		obj.put("uid", gsession.getUser().getUid());
		//下发通知
		GData data3 = new GData(GProtocol.MODE_GAME_BULLETCHANGETARGET_RESULT, obj.toString());
		data = data3.pack();
		for (Map.Entry<Long, GBubble> entry : room.getBubbles().entrySet()) 
		{
			long sessionId = entry.getKey();
			GSession gs = GSessionHandler.getInstance().getGSessionById(sessionId);
			if(gs != null)
			{
				gs.send(data);
			}
		}
	}
	//加速
	public synchronized void speedUp(IoSession session, String data)
	{
		JSONObject obj = JSONObject.fromObject(data);
		
		GSession gsession = GSessionHandler.getInstance().getGSessionById(session.getId());		
		GRoom room = GServerController.getInstance().findRoom(gsession.getRoomId());
		if(room == null)
			return;
		GBubble bubble = room.find(session.getId());
		
		float x = (float) obj.getDouble("x");
	    float y = (float) obj.getDouble("y");
	    float rotate = (float) obj.getDouble("rotate");
	    
//	    bubble.setX(x);
//	    bubble.setY(y);
	    bubble.setRotate(rotate);
		
//		if(bubble.getCurrHp() > bubble.getHP()*0.1f)
	    if(true)
		{
//			int hp = (int) (bubble.getHP()*0.1f);
//			if(hp < 20)
//				hp = 20;
			
//			bubble.setCurrHp(bubble.getCurrHp() - hp);
			bubble.setState(GBubble.STATE.SPEEDUP);
			
			obj.put("uid", gsession.getUser().getUid());
//			obj.put("hp", bubble.getCurrHp());
			//下发通知
			GData data3 = new GData(GProtocol.MODE_GAME_SPEEDUP_RESULT, obj.toString());
			data = data3.pack();
			for (Map.Entry<Long, GBubble> entry : room.getBubbles().entrySet()) 
			{
				long sessionId = entry.getKey();
				GSession gs = GSessionHandler.getInstance().getGSessionById(sessionId);
				if(gs != null)
				{
					gs.send(data);
				}
			}
		}		
	}
	//停止加速
	public synchronized void stopSpeedUp(IoSession session, String data)
	{
		GSession gsession = GSessionHandler.getInstance().getGSessionById(session.getId());		
		GRoom room = GServerController.getInstance().findRoom(gsession.getRoomId());
		if(room == null)
			return;
		JSONObject obj = JSONObject.fromObject(data);
		String targetUid = obj.getString("uid");
		
		GBubble bubble = room.findRobot(targetUid);
		if(bubble == null)
		{
	        bubble = room.find(gsession.getSession().getId());
		}		
		if(bubble == null)
			return;
		
		float x = (float) obj.getDouble("x");
	    float y = (float) obj.getDouble("y");
	    float rotate = (float) obj.getDouble("rotate");
	    
//	    bubble.setX(x);
//	    bubble.setY(y);
	    bubble.setRotate(rotate);
	    
	    bubble.setState(GBubble.STATE.IDLE);
//	    obj.put("hp", bubble.getCurrHp());
		//下发通知
		GData data3 = new GData(GProtocol.MODE_GAME_STOPSPEEDUP_RESULT, obj.toString());
		data = data3.pack();
		for (Map.Entry<Long, GBubble> entry : room.getBubbles().entrySet()) 
		{
			long sessionId = entry.getKey();
			GSession gs = GSessionHandler.getInstance().getGSessionById(sessionId);
			if(gs != null)
			{
				gs.send(data);
			}
		}
	}
	//排名
	public synchronized void ranking(long roomId)
	{
		GRoom room = GServerController.getInstance().findRoom(roomId);
		if(room != null)
		{
			List<GBubble> list = new ArrayList<GBubble>();
			for(GBubble bubble : room.getBubbles().values())
			{
				list.add(bubble);
			}
			for(GBubble bubble : room.getRobots().values())
			{
				list.add(bubble);
			}
			Collections.sort(list, new Comparator<GBubble>(){
	            public int compare(GBubble arg0, GBubble arg1) {
	                return arg1.getExp() - arg0.getExp();
	            }
	        });
			List<GRank> ranks = new ArrayList<GRank>();
			for(int i=0;i<list.size();i++)
			{
				GBubble bubble = list.get(i);
				GRank rank = new GRank();
				rank.setRank(i+1);
				rank.setUid(bubble.getUid());
				rank.setName(bubble.getName());
				rank.setExp(bubble.getExp());
				rank.setKill(bubble.getKill());
				ranks.add(rank);
			}
			JSONObject obj = new JSONObject();
			obj.put("ranks", JSONArray.fromObject(ranks));
			
			GData data = new GData(GProtocol.MODE_GAME_RANKING_RESULT, obj.toString());
			String data2 = data.pack();
			for (Map.Entry<Long, GBubble> entry : room.getBubbles().entrySet()) 
			{
				long sessionId = entry.getKey();
				GSession gs = GSessionHandler.getInstance().getGSessionById(sessionId);
				if(gs != null)
				{
					gs.send(data2);
				}
			}
			
		}
	}
	
	//复活
	public synchronized void relived(IoSession session, String data)
	{
		GSession gsession = GSessionHandler.getInstance().getGSessionById(session.getId());		
		GRoom room = GServerController.getInstance().findRoom(gsession.getRoomId());
		if(room == null)
			return;
		GBubble bubble = room.find(gsession.getSession().getId());
		if(bubble != null)
		{
			JSONObject obj = JSONObject.fromObject(data);
			//初始化数据
			GBubble newBubble = GModelTool.getBubble(gsession.getUser().getUid(),false,room.getMapWidth(),room.getMapHeight());
			
			bubble.setX(newBubble.getX());
			bubble.setY(newBubble.getY());
			bubble.setState(GBubble.STATE.IDLE);			
			bubble.setDirX(0);
			bubble.setDirY(1);
			bubble.setAngle(90);
			bubble.setLevel(0);
			bubble.setExp(0);
			bubble.setGrow(newBubble.getGrow());
			bubble.setSkinId(gsession.getUser().getSkinId());
			
			while(room.isCoincide(bubble))
			{
				bubble.setX(GTools.getRand(100, room.getMapWidth()-100));
				bubble.setY(GTools.getRand(100, room.getMapHeight()-100));
			}
			
			JSONObject bubble_data = JSONObject.fromObject(bubble);
			
			obj = new JSONObject();
			obj.put("bubble", bubble_data);
			//给自己发送复活数据
			GData bData = new GData(GProtocol.MODE_GAME_RELIVED_RESULT, obj.toString());
			gsession.send(bData.pack());
			
			//给其他玩家发送自己进入房间信息
			obj = new JSONObject();
			obj.put("uid", gsession.getUser().getUid());
			obj.put("result", true);	
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
					gs.send(otherData);
				}
			}	
		}
	}
	//掉落
	public synchronized void drop(IoSession session, String data)
	{
		GSession gsession = GSessionHandler.getInstance().getGSessionById(session.getId());		
		GRoom room = GServerController.getInstance().findRoom(gsession.getRoomId());
		if(room == null)
			return;
		
		JSONObject obj = JSONObject.fromObject(data);

		boolean target_robot = obj.getBoolean("robot");
		String targetUid = obj.getString("target");
		GBubble targetBubble = null;
		if(target_robot)
		{
			targetBubble = room.findRobot(targetUid);
		}
		else
		{
	        GSession targetSession = GSessionHandler.getInstance().getSessionByUid(targetUid);
	        targetBubble = room.find(targetSession.getSession().getId());
		}
		
		if(targetBubble != null)
		{
			float x = (float) obj.getDouble("x");
			float y = (float) obj.getDouble("y");
			targetBubble.setX(x);
			targetBubble.setY(y);
			JSONArray arr = obj.getJSONArray("pos");
			int exp = targetBubble.getExp() / (arr.size()/2);
			if(exp < 1)
				exp = 1;
			//掉落水滴
			room.getDrops().add(new GDrop(exp,arr));
			//机器人复活
			if(target_robot)
			{
				robotRelived(gsession,targetBubble);
			}				
		}		
	}
	//机器人复活
	public synchronized void robotRelived(GSession gsession, GBubble targetBubble)
	{
		GRoom room = GServerController.getInstance().findRoom(gsession.getRoomId());
		if(room == null)
			return;
		GBubble bubble = targetBubble;
		if(bubble != null)
		{
			//初始化数据
			GBubble newBubble = GModelTool.getBubble(bubble.getUid(),true,room.getMapWidth(),room.getMapHeight());
			
			bubble.setX(newBubble.getX());
			bubble.setY(newBubble.getY());
			bubble.setState(GBubble.STATE.IDLE);			
			bubble.setDirX(0);
			bubble.setDirY(1);
			bubble.setAngle(90);
			bubble.setLevel(0);
			bubble.setExp(0);
			bubble.setGrow(newBubble.getGrow());
			bubble.setSkinId(gsession.getUser().getSkinId());
			bubble.setBubbleId(newBubble.getBubbleId());
			
			while(room.isCoincide(bubble))
			{
				bubble.setX(GTools.getRand(100, room.getMapWidth()-100));
				bubble.setY(GTools.getRand(100, room.getMapHeight()-100));
			}
			
			JSONObject bubble_data = JSONObject.fromObject(bubble);		
			JSONObject obj = new JSONObject();
			obj.put("robot",bubble_data);
			
			String otherData = obj.toString();
			GData otherData2 = new GData(GProtocol.MODE_GAME_ADDROBOT_RESULT, otherData);
			otherData = otherData2.pack();
			for (Map.Entry<Long, GBubble> entry : room.getBubbles().entrySet()) 
			{
				long sessionId = entry.getKey();
				GSession gs = GSessionHandler.getInstance().getGSessionById(sessionId);
				if(gs != null)
				{
					gs.send(otherData);
				}
			}	
		}
	}
	//泡泡碰撞
	public synchronized void coll(IoSession session, String data)
	{
		JSONObject obj = JSONObject.fromObject(data);
		
		GSession gsession = GSessionHandler.getInstance().getGSessionById(session.getId());		
		GRoom room = GServerController.getInstance().findRoom(gsession.getRoomId());
		if(room == null)
			return;
		GBubble bubble = room.find(gsession.getSession().getId());	
		
		float x = (float) obj.getDouble("x");
	    float y = (float) obj.getDouble("y");
	    float rotate = (float) obj.getDouble("rotate");
	    
	    bubble.setX(x);
	    bubble.setY(y);
	    bubble.setRotate(rotate);
	    bubble.setState(GBubble.STATE.IDLE);

		obj.put("uid", bubble.getUid());
		
		GData data3 = new GData(GProtocol.MODE_GAME_COLL_RESULT, obj.toString());
		data = data3.pack();
		for (Map.Entry<Long, GBubble> entry : room.getBubbles().entrySet()) 
		{
			long sessionId = entry.getKey();
			GSession gs = GSessionHandler.getInstance().getGSessionById(sessionId);
			if(gs != null)
			{
				gs.send(data);
			}
		}
	}
	
	//实时更新位置
	public synchronized void updatePos(IoSession session, String data)
	{
		GSession gsession = GSessionHandler.getInstance().getGSessionById(session.getId());		
		GRoom room = GServerController.getInstance().findRoom(gsession.getRoomId());
		if(room == null)
			return;
		JSONObject obj = JSONObject.fromObject(data);
		String targetUid = obj.getString("uid");
		
		GBubble bubble = room.findRobot(targetUid);
		if(bubble == null)
		{
	        bubble = room.find(gsession.getSession().getId());
		}		
		if(bubble == null)
			return;
		
//		float x = (float) obj.getDouble("x");
//	    float y = (float) obj.getDouble("y");
	    float rotate = (float) obj.getDouble("rotate");
		float dirX = (float) obj.getDouble("dirX");
		float dirY = (float) obj.getDouble("dirY");
	    int state = obj.getInt("state");
	    
//	    bubble.setX(x);
//	    bubble.setY(y);
	    bubble.setDirX(dirX);
	    bubble.setDirY(dirY);
	    bubble.setRotate(rotate);
	    if(state == 0)
	    	bubble.setState(GBubble.STATE.BORN);
	    else if(state == 1)
	    	bubble.setState(GBubble.STATE.IDLE); 
	    else if(state == 2)
	    	bubble.setState(GBubble.STATE.MOVE);  
	    else if(state == 3)
	    	bubble.setState(GBubble.STATE.SPEEDUP);  
	    else if(state == 4)
	    	bubble.setState(GBubble.STATE.DIE);  
	    
	    
//	    GData data3 = new GData(GProtocol.MODE_GAME_UPDATEPOS_RESULT, obj.toString());
//		data = data3.pack();
//		for (Map.Entry<Long, GBubble> entry : room.getBubbles().entrySet()) 
//		{
//			long sessionId = entry.getKey();
//			GSession gs = GSessionHandler.getInstance().getGSessionById(sessionId);
//			if(gs != null)
//			{
//				gs.send(data);
//			}
//		}
	}
	public synchronized void updateRotate(long roomId,long dt)
	{
		GRoom room = GServerController.getInstance().findRoom(roomId);
		if(room == null)
			return;
		if(room.getBubbles().size() == 0)
			return;
		float time = dt / 1000.f;
		for(GBubble bubble : room.getBubbles().values())
		{
			if(bubble.getState() != GBubble.STATE.DIE)
			{
				float angle = bubble.getRotate();
				if(bubble.getAngle() > bubble.getRotate())
				{
					float dis = bubble.getAngle() - bubble.getRotate();
					float dis2 = 360 - bubble.getAngle() + bubble.getRotate();
					if(dis < dis2)
					{
						angle = angle + bubble.getRotateSpeed()*time;
						if(angle > bubble.getAngle())
							angle = bubble.getAngle();
					}
					else
					{
						angle = angle - bubble.getRotateSpeed()*time;
						if(angle < 0)
							angle = 360 + angle;
					}
				}
				else
				{
					float dis = bubble.getRotate() - bubble.getAngle();
					float dis2 = 360 - bubble.getRotate() + bubble.getAngle();
					if(dis < dis2)
					{
						angle = angle - bubble.getRotateSpeed()*time;
						if(angle < bubble.getAngle())
							angle = bubble.getAngle();
					}
					else
					{
						angle = angle + bubble.getRotateSpeed()*time;
						if(angle > 360)
							angle = angle - 360;
					}
				}
//				System.out.println(bubble.getRotate() + "  "+angle + "  "+bubble.getAngle());

				bubble.setRotate(angle);
				
				bubble.setDirX(GModelTool.getDirX(angle));
				bubble.setDirY(GModelTool.getDirY(angle));				
			}		
		}
		
		for(GBubble bubble : room.getRobots().values())
		{
			if(bubble.getState() != GBubble.STATE.DIE)
			{
				float angle = bubble.getRotate();
				if(bubble.getAngle() > bubble.getRotate())
				{
					float dis = bubble.getAngle() - bubble.getRotate();
					float dis2 = 360 - bubble.getAngle() + bubble.getRotate();
					if(dis < dis2)
					{
						angle = angle + bubble.getRotateSpeed()*time;
						if(angle > bubble.getAngle())
							angle = bubble.getAngle();
					}
					else
					{
						angle = angle - bubble.getRotateSpeed()*time;
						if(angle < 0)
							angle = 360 + angle;
					}
				}
				else
				{
					float dis = bubble.getRotate() - bubble.getAngle();
					float dis2 = 360 - bubble.getRotate() + bubble.getAngle();
					if(dis < dis2)
					{
						angle = angle - bubble.getRotateSpeed()*time;
						if(angle < bubble.getAngle())
							angle = bubble.getAngle();
					}
					else
					{
						angle = angle + bubble.getRotateSpeed()*time;
						if(angle > 360)
							angle = angle - 360;
					}
				}
				bubble.setRotate(angle);
				
				bubble.setDirX(GModelTool.getDirX(angle));
				bubble.setDirY(GModelTool.getDirY(angle));
			}		
		}
	}
	//发送位置
	public synchronized void sendPos(long roomId,long dt)
	{
		GRoom room = GServerController.getInstance().findRoom(roomId);
		if(room == null)
			return;
		if(room.getBubbles().size() == 0)
			return;
		float time = dt / 1000.f;
		JSONArray arr = new JSONArray();
		for(GBubble bubble : room.getBubbles().values())
		{
			if(bubble.getState() != GBubble.STATE.DIE)
			{
				boolean up = false;
				if(bubble.getState() == GBubble.STATE.MOVE)
				{
					float x = bubble.getDirX()*time*bubble.getSpeed() + bubble.getX();
					float y = bubble.getDirY()*time*bubble.getSpeed() + bubble.getY();
					bubble.setX(x);
					bubble.setY(y);
				}
				else if(bubble.getState() == GBubble.STATE.SPEEDUP)
				{
					if(bubble.getExp() <= 0)
					{
						float x = bubble.getDirX()*time*bubble.getSpeed() + bubble.getX();
						float y = bubble.getDirY()*time*bubble.getSpeed() + bubble.getY();
						bubble.setX(x);
						bubble.setY(y);
					}
					else
					{
						float x = bubble.getDirX()*time*bubble.getsSpeed()+ bubble.getX();
						float y = bubble.getDirY()*time*bubble.getsSpeed()+ bubble.getY();
						bubble.setX(x);
						bubble.setY(y);
						up = true;
					}
				}				
				
				JSONObject obj = new JSONObject();
				obj.put("x", bubble.getX());
				obj.put("y", bubble.getY());
//				obj.put("dirX", bubble.getDirX());
//				obj.put("dirY", bubble.getDirY());
				obj.put("rotate", bubble.getRotate());
				obj.put("uid", bubble.getUid());
				obj.put("time", time);
				obj.put("up", up);
//				int state = 0;
//				if(bubble.getState() == GBubble.STATE.IDLE)		
//					state = 1;
//				else if(bubble.getState() == GBubble.STATE.MOVE)		
//					state = 2;
//				else if(bubble.getState() == GBubble.STATE.SPEEDUP)		
//					state = 3;
//				else if(bubble.getState() == GBubble.STATE.DIE)		
//					state = 4;
//				obj.put("state", state);
				
				arr.add(obj);
			}		
		}
		
		for(GBubble bubble : room.getRobots().values())
		{
			if(bubble.getState() != GBubble.STATE.DIE)
			{
				boolean up = false;
				if(bubble.getState() == GBubble.STATE.MOVE)
				{
					float x = bubble.getDirX()*time*bubble.getSpeed() + bubble.getX();
					float y = bubble.getDirY()*time*bubble.getSpeed() + bubble.getY();
					bubble.setX(x);
					bubble.setY(y);
				}
				else if(bubble.getState() == GBubble.STATE.SPEEDUP)
				{
					if(bubble.getExp() <= 0)
					{
						float x = bubble.getDirX()*time*bubble.getSpeed() + bubble.getX();
						float y = bubble.getDirY()*time*bubble.getSpeed() + bubble.getY();
						bubble.setX(x);
						bubble.setY(y);
					}
					else
					{
						float x = bubble.getDirX()*time*bubble.getsSpeed()+ bubble.getX();
						float y = bubble.getDirY()*time*bubble.getsSpeed()+ bubble.getY();
						bubble.setX(x);
						bubble.setY(y);
						up = true;
					}
				}

				
				JSONObject obj = new JSONObject();
				obj.put("x", bubble.getX());
				obj.put("y", bubble.getY());
//				obj.put("dirX", bubble.getDirX());
//				obj.put("dirY", bubble.getDirY());
				obj.put("rotate", bubble.getRotate());
				obj.put("uid", bubble.getUid());
				obj.put("time", time);
				obj.put("up", up);
//				int state = 0;
//				if(bubble.getState() == GBubble.STATE.IDLE)		
//					state = 1;
//				else if(bubble.getState() == GBubble.STATE.MOVE)		
//					state = 2;
//				else if(bubble.getState() == GBubble.STATE.SPEEDUP)		
//					state = 3;
//				else if(bubble.getState() == GBubble.STATE.DIE)		
//					state = 4;
//				obj.put("state", state);
				
				arr.add(obj);
			}		
		}
		
		GData data3 = new GData(GProtocol.MODE_GAME_UPDATEPOS_RESULT, arr.toString());
		String data = data3.pack();
		for (Map.Entry<Long, GBubble> entry : room.getBubbles().entrySet()) 
		{
			long sessionId = entry.getKey();
			GSession gs = GSessionHandler.getInstance().getGSessionById(sessionId);
			if(gs != null)
			{
				gs.send(data);
			}
		}
	}
	
	//更新血量
	public synchronized void updateHp(IoSession session, String data)
	{
		GSession gsession = GSessionHandler.getInstance().getGSessionById(session.getId());		
		GRoom room = GServerController.getInstance().findRoom(gsession.getRoomId());
		if(room == null)
			return;
		JSONObject obj = JSONObject.fromObject(data);
		String targetUid = obj.getString("uid");
		
		GBubble bubble = room.findRobot(targetUid);
		if(bubble == null)
		{
	        bubble = room.find(gsession.getSession().getId());
		}		
		if(bubble == null)
			return;
		
	    int type = obj.getInt("type");//0 减 1加
	    if(type == 0)
	    {
	    	bubble.setExp(bubble.getExp() - bubble.getReduceHP());
	    	if(bubble.getExp() < 0)
	    		bubble.setExp(0);
	    	
	    	bubble.setLevel(GServerConfig.getLevelForExp(bubble.getExp()));
	    	bubble.setGrow(GServerConfig.getGrowForLevel(bubble.getLevel()));
			
			obj.put("level", bubble.getLevel());
			obj.put("exp", bubble.getExp());
			obj.put("grow", bubble.getGrow());
			
			//掉落水滴
			int x = obj.getInt("x");
			int y = obj.getInt("y");
			JSONArray arr = new JSONArray();
			JSONObject pos = new JSONObject();
			pos.put("x", x);
			pos.put("y", y);
			arr.add(pos);
			room.getDrops().add(new GDrop(1,arr));
	    }
	    else
	    {
//	    	bubble.setCurrHp(bubble.getCurrHp() + bubble.getRecoverHP());
//	    	if(bubble.getCurrHp() > bubble.getHP())
//	    		bubble.setCurrHp(bubble.getHP());
	    }
	    
	    GData data3 = new GData(GProtocol.MODE_GAME_UPDATEHP_RESULT, obj.toString());
		data = data3.pack();
		for (Map.Entry<Long, GBubble> entry : room.getBubbles().entrySet()) 
		{
			long sessionId = entry.getKey();
			GSession gs = GSessionHandler.getInstance().getGSessionById(sessionId);
			if(gs != null)
			{
				gs.send(data);
			}
		}
	}
	
	//-----------------------------------------------------
	//给房间内的玩家分配机器人
	public synchronized void allotRobot(String uid,String robotUid)
	{
		GSession gsession = GSessionHandler.getInstance().getSessionByUid(uid);		
		GRoom room = GServerController.getInstance().findRoom(gsession.getRoomId());
		if(room == null)
		{
			return;
		}
		long sessionid = gsession.getSession().getId();
		GBubble plane = room.find(sessionid);
		plane.getRobotUid().add(robotUid);
		
		GBubble robot = room.findRobot(robotUid);
		robot.setBubbleId(sessionid);
				
		JSONObject obj = new JSONObject();
		obj.put("uid", plane.getUid());
		obj.put("robotUid", robotUid);
		
		GData data3 = new GData(GProtocol.MODE_GAME_ALLOTROBOT_RESULT, obj.toString());
		String data = data3.pack();
		gsession.send(data);		
	}
	//添加机器人
	public synchronized void addRobot(long roomId,GBubble plane)
	{
		GRoom room = GServerController.getInstance().findRoom(roomId);

		JSONObject robot = JSONObject.fromObject(plane);
		
		JSONObject obj = new JSONObject();
		obj.put("robot",robot);
		
		GData data3 = new GData(GProtocol.MODE_GAME_ADDROBOT_RESULT, obj.toString());
		String data = data3.pack();
		for (Map.Entry<Long, GBubble> entry : room.getBubbles().entrySet()) 
		{
			long sessionId = entry.getKey();
			GSession gs = GSessionHandler.getInstance().getGSessionById(sessionId);
			if(gs != null)
			{
				gs.send(data);
			}
		}
	}
	
}
