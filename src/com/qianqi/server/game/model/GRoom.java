package com.qianqi.server.game.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.qianqi.server.GServerConfig;
import com.qianqi.server.GServerController;
import com.qianqi.server.game.tools.GModelTool;
import com.qianqi.server.protocol.mode.GModeGame;
import com.qianqi.web.tools.GTools;

public class GRoom {
	private long roomId;
	private long time;
	private long dt = 0;
	private long lastTime;
	private long commonDt = 0;
	private long blockDt = 0;
	private int blockId = 0;
	private long robotDt = 0;
	private long robotId = 100000000;
	private long dropDt = 0;
	private long blockHpDt = 0;
//	private long updatePosDt = 0;
	private long blockHpTime = GServerConfig.lifeRefreshTime;
	private HashMap<Long,GBubble> bubbles = null;
	private HashMap<Integer,GBlock> blocks = null;
	private HashMap<String,GBubble> robots = null;
	private List<GClound> clounds = null;
	private List<GDrop> drops = null;
	//地图相关
	private int mapId = 0;
	private int mapWidth;
	private int mapHeight;
	private float mapPosX;
	private float mapPosY;
	public GRoom(long roomId) {
		super();
		this.roomId = roomId;
		bubbles = new HashMap<Long, GBubble>();
		blocks = new HashMap<Integer, GBlock>();
		robots = new HashMap<String, GBubble>();
		clounds = new ArrayList<GClound>();
		drops = new ArrayList<GDrop>();
		time = GServerConfig.roomTime;
		lastTime = System.currentTimeMillis();
		
		mapId = GTools.getRand(0, GServerConfig.maps.size());
		initClound();
		initBlock();	
	}
	//添加
	public void add(long sessionId,GBubble plane)
	{
		bubbles.put(sessionId, plane);
	}
	//移除
	public void remove(long sessionId)
	{
		bubbles.remove(sessionId);
	}
	//查找
	public GBubble find(long sessionId)
	{
		return bubbles.get(sessionId);
	}
	//获取房间人数
	public int getSessionNum()
	{
		return bubbles.size();
	}
	//移除水晶
	public void removeBlock(int id)
	{
		blocks.remove(id);
	}
	//移除机器人
	public void removeRobot(String uid)
	{
		robots.remove(uid);
	}
	//查找机器人
	public GBubble findRobot(String uid)
	{
		return robots.get(uid);
	}
	public long getRoomId() {
		return roomId;
	}
	public void setRoomId(long roomId) {
		this.roomId = roomId;
	}		
	
	public int getMapId() {
		return mapId;
	}
	public int getMapWidth() {
		return mapWidth;
	}
	public int getMapHeight() {
		return mapHeight;
	}
	public float getMapPosX() {
		return mapPosX;
	}
	public float getMapPosY() {
		return mapPosY;
	}
	public HashMap<Long, GBubble> getBubbles() {
		return bubbles;
	}
	public void setBubbles(HashMap<Long, GBubble> bubbles) {
		this.bubbles = bubbles;
	}
	public HashMap<Integer, GBlock> getBlocks() {
		return blocks;
	}
	public void setBlocks(HashMap<Integer, GBlock> blocks) {
		this.blocks = blocks;
	}
	public HashMap<String, GBubble> getRobots() {
		return robots;
	}
	public void setRobots(HashMap<String, GBubble> robots) {
		this.robots = robots;
	}
	public List<GClound> getClounds() {
		return clounds;
	}
	public void setClounds(List<GClound> clounds) {
		this.clounds = clounds;
	}	
	public List<GDrop> getDrops() {
		return drops;
	}
	public void setDrops(List<GDrop> drops) {
		this.drops = drops;
	}
	public long getTime() {
		return time;
	}
	public void start() {
		new Thread(){
			public void run() {
				while(time > 0)
				{
					try {
						long currTime = System.currentTimeMillis();
						dt = currTime - lastTime;
						lastTime = currTime;	
						update(dt);
						Thread.sleep(33);								
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				GModeGame.getInstance().roomTimeOut(roomId);
				GServerController.getInstance().removeRoom(roomId);
			};
		}.start();	
	}
	
	private void update(long dt)
	{		
		time -= dt;
		
//		if(updatePosDt >= 40)
//		{
//			updatePosDt = 0;
//			GModeGame.getInstance().sendPos(roomId);
//		}
//		updatePosDt += dt;
		if(commonDt >= 1000)
		{
			GModeGame.getInstance().roomCountDown(roomId,time);
			GModeGame.getInstance().ranking(roomId);
			commonDt = 0;
		}
		commonDt+=dt;
		
		updateBlock(dt);
		updateRobot(dt);
		updateDrop(dt);
	}
	
	private void updateBlock(long dt)
	{
		if(blockDt >= GServerConfig.refreshTime)
		{
			blockDt = 0;
			
			if(blocks.size() < GServerConfig.numBlock)
			{
				addBlock();		
			}
			int num = getBlockBulletNum();
			if((bubbles.size()+robots.size())*GServerConfig.refreshBullet > num)
			{
				addBlockBullet(num);
			}

		}
		blockDt+=dt;
		
		//血包刷新
		
		if(blockHpDt >= blockHpTime)
		{
			blockHpDt = 0;
			
			int r = GTools.getRand(0, 100);
			long t = (long) (GServerConfig.lifeRefreshTime * (GTools.getRand(0, 30) / 100.f));
			if(r > 50)
				blockHpTime = GServerConfig.lifeRefreshTime + t;
			else
				blockHpTime = GServerConfig.lifeRefreshTime - t;
			
			int num = getBlockHPNum();
			if(num < (bubbles.size()+robots.size()))
			addBlockHp(1);
		}
		blockHpDt+=dt;
	}
	private void initBlock()
	{
		int num = GServerConfig.numBlock - blocks.size();
		for(int i=0;i<num;i++)
		{
			blockId++;
			GBlock block = GModelTool.getBlock(blockId,mapWidth, mapHeight);
			while(isCoincide(block))
			{
				block.setX(GTools.getRand(30, mapWidth-30));
				block.setY(GTools.getRand(30, mapHeight-30));
			}
			blocks.put(blockId, block);
		}
	}
	
	private void addBlock()
	{
		int num = GServerConfig.numBlock - blocks.size();
		List<GBlock> list = new ArrayList<GBlock>();
		for(int i=0;i<num;i++)
		{
			blockId++;			
			GBlock block = GModelTool.getBlock(blockId,mapWidth, mapHeight);
			while(isCoincide(block))
			{
				block.setX(GTools.getRand(30, mapWidth-30));
				block.setY(GTools.getRand(30, mapHeight-30));
			}
			list.add(block);
			blocks.put(blockId, block);
		}
		GModeGame.getInstance().addBlock(roomId,list,0,0);
	}
	
	private void addBlockBullet(int num)
	{
		num = (robots.size()+bubbles.size())*GServerConfig.refreshBullet - num;
		List<GBlock> list = new ArrayList<GBlock>();
		for(int i=0;i<num;i++)
		{
			blockId++;			
			GBlock block = GModelTool.getBlockBullet(blockId,mapWidth, mapHeight);
			while(isCoincide(block))
			{
				block.setX(GTools.getRand(30, mapWidth-30));
				block.setY(GTools.getRand(30, mapHeight-30));
			}
			list.add(block);
			blocks.put(blockId, block);
		}
		GModeGame.getInstance().addBlock(roomId,list,0,0);
	}
	
	private void addBlockHp(int num)
	{
		List<GBlock> list = new ArrayList<GBlock>();
		for(int i=0;i<num;i++)
		{
			blockId++;			
			GBlock block = GModelTool.getBlockHp(blockId,mapWidth, mapHeight);
			while(isCoincide(block))
			{
				block.setX(GTools.getRand(30, mapWidth-30));
				block.setY(GTools.getRand(30, mapHeight-30));
			}
			list.add(block);
			blocks.put(blockId, block);
		}
		GModeGame.getInstance().addBlock(roomId,list,0,0);
	}
	
	private void initClound()
	{
		//找到地图 解析云
		JSONObject obj = GServerConfig.maps.getJSONObject(mapId);
		if(obj != null)
		{
			float tileW = (float) obj.getDouble("tileW");
			float tileH = (float) obj.getDouble("tileH");
			mapWidth = (int) obj.getDouble("w");
			mapHeight = (int) obj.getDouble("h");
			mapPosX = (float) obj.getDouble("x");
			mapPosY = (float) obj.getDouble("y");

			
			JSONArray points = obj.getJSONArray("points");
			for(int i=0;i<points.size();i++)
			{
				JSONObject point = points.getJSONObject(i);
				float x = (float) point.getDouble("x");
				float y = (float) point.getDouble("y");
				
				GClound clound = GModelTool.getClound(x,y,tileW,tileH);
				clounds.add(clound);
			}
		}
//		for(int i=0;i<GServerConfig.numClound;i++)
//		{
//			GClound clound = GModelTool.getClound();
//			while(isCoincide(clound))
//			{
//				clound.setX(GTools.getRand(30, GServerConfig.roomWidth-30));
//				clound.setY(GTools.getRand(30, GServerConfig.roomHeight-30));
//			}
//			clounds.add(clound);
//		}
	}
//	private boolean isCoincide(GClound clound)
//	{
//		for(GClound c : getClounds())
//		{
//			float dis = (float) Math.sqrt((c.getX()-clound.getX())*(c.getX()-clound.getX()) +
//					(c.getY()-clound.getY())*(c.getY()-clound.getY()));
//			if(dis < (c.getScale()*100 + clound.getScale()*100))
//				return true;
//		}
//		return false;
//	}
	private boolean isCoincide(GBlock block)
	{
		for(GClound c : getClounds())
		{
			float dis = (float) Math.sqrt((c.getX()-block.getX())*(c.getX()-block.getX()) +
					(c.getY()-block.getY())*(c.getY()-block.getY()));
			if(dis < c.getWidth())
				return true;
		}
		return false;
	}
	public boolean isCoincide(GBubble bubble)
	{
		for(GClound c : getClounds())
		{
			float dis = (float) Math.sqrt((c.getX()-bubble.getX())*(c.getX()-bubble.getX()) +
					(c.getY()-bubble.getY())*(c.getY()-bubble.getY()));
			if(dis < c.getWidth()+70)
				return true;
		}
		return false;
	}
	private int getBlockBulletNum()
	{
		int num = 0;
		synchronized(GModeGame.getInstance())
		{
			for(GBlock block : blocks.values())
			{
				if(block.getBulletType() > 1)
					num++;
			}
		}
		
		return num;
	}
	
	private int getBlockHPNum()
	{
		int num = 0;
		synchronized(GModeGame.getInstance())
		{
			for(GBlock block : blocks.values())
			{
				if(block.getType() == 2)
					num++;
			}
		}
		
		return num;
	}
	
	private void updateDrop(long dt)
	{
		dropDt+=dt;
		if(dropDt > 100)
		{
			dropDt = 0;			
			if(drops.size() > 0)
			{
				GDrop drop = drops.get(0);
				List<GBlock> list = new ArrayList<GBlock>();
				for(int i=0;i<drop.getNum();i++)
				{
					blockId++;			
					GBlock block = GModelTool.getBlock(blockId,mapWidth, mapHeight);	
					block.setX(clounds.get(0).getX());
					block.setY(clounds.get(0).getY());
					while(isCoincide(block))
					{
						int r1 = GTools.getRand(0,200);
						int r2 = GTools.getRand(0,200);
						r1 = r1 < 100 ? -r1 : 200-r1;
						r2 = r2 < 100 ? -r2 : 200-r2;
						block.setX(drop.getX()+r1);
						block.setY(drop.getY()+r2);	
						if(block.getX() > mapWidth)
							block.setX(mapWidth-10);
						if(block.getY() > mapHeight)
							block.setY(mapHeight-10);
						if(block.getX() < 0)
							block.setX(10);
						if(block.getY() < 0)
							block.setY(10);
					}
					
					list.add(block);
					blocks.put(blockId, block);
				}
				drops.remove(0);
				GModeGame.getInstance().addBlock(roomId,list,drop.getX(),drop.getY());
			}
		}
	}
	
	//--------------机器人----------------------
	private void updateRobot(long dt)
	{
		robotDt+=dt;

		if(robotDt >= 1000)
		{
			robotDt = 0;
			//遍历机器人 查看是否被分配
			for(GBubble robot : robots.values())
			{
				if(robot.getState() != GBubble.STATE.DIE)
				{
					boolean b = true;
					if(robot.getBubbleId() != 0)
					{
						GBubble p = find(robot.getBubbleId());
//						if(p != null && p.getState() != GBubble.STATE.DIE)
						if(p != null)
						{
							b = false;
						}
						else
						{
							b = true;
						}
					}
					else
					{
						b = true;
					}
					//分配给用户
					if(b)
					{		
						//找到携带机器人最少的飞机
						GBubble p2 = null;
						for(GBubble plane : bubbles.values())
						{
//							if(plane.getState() != GBubble.STATE.DIE)
//							{
								if(p2 == null)
								{
									p2 = plane;
								}
								else
								{
									if(plane.getRobotUid().size() < p2.getRobotUid().size())
									{
										p2 = plane;
									}
								}
//							}
						}
						if(p2 != null)
						{
							GModeGame.getInstance().allotRobot(p2.getUid(),robot.getUid());
						}
					}
				}
			}
			
			//是否需要添加机器人
			if(bubbles.size() + robots.size() < GServerConfig.maxRoomSessions && robots.size() < GServerConfig.maxRobotNum)
			{
				addRobot();
			}
		}
	}
	
	private void addRobot()
	{
		robotId++;
		String uid = "robot_"+robotId;
		GBubble bubble = GModelTool.getBubble(uid ,true,mapWidth,mapHeight);
		while(isCoincide(bubble))
		{
			bubble.setX(GTools.getRand(100, mapWidth-100));
			bubble.setY(GTools.getRand(100, mapHeight-100));
		}
		robots.put(uid, bubble);
		GModeGame.getInstance().addRobot(roomId,bubble);
	}
}
