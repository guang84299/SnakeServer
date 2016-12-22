package com.qianqi.server.game.tools;

import java.util.ArrayList;
import java.util.HashMap;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.qianqi.server.GServerConfig;
import com.qianqi.server.game.model.GBullet;
import com.qianqi.server.game.model.GBlock;
import com.qianqi.server.game.model.GBubble;
import com.qianqi.server.game.model.GClound;
import com.qianqi.server.handler.GSessionHandler;
import com.qianqi.server.session.GSession;
import com.qianqi.web.tools.GTools;

public class GModelTool {
	private static int tid = 0;
	private static HashMap<Integer,GBullet> bullets = new HashMap<Integer, GBullet>();;
	public static void initBullet()
	{
		bullets.clear();
		
		JSONArray arr = GServerConfig.bullets;
		for(int i=0;i<arr.size();i++)
		{
			JSONObject obj = arr.getJSONObject(i);
			GBullet bullet = new GBullet();
			bullet.setId(obj.getInt("id"));
			bullet.setTid(getRandId());
			bullet.setName(obj.getString("name"));
			bullet.setBulletSpeed((float)obj.getDouble("bulletSpeed"));
			bullet.setBulletAccelerated((float)obj.getDouble("bulletAccelerated"));
			bullet.setBulletTime((float)obj.getDouble("bulletTime"));
			bullet.setDamage(obj.getInt("damage"));
			bullet.setBuffTime((float)obj.getDouble("buffTime"));
			bullet.setDownSpeed((float)obj.getDouble("downSpeed"));
			bullet.setNum(obj.getInt("num"));
			bullet.setRange(obj.getInt("range"));
			bullet.setTargetRange(obj.getInt("targetRange"));
			bullet.setCD((float)obj.getDouble("CD"));
			bullet.setFillBullet((float)obj.getDouble("fillBullet"));
			bullet.setLoadBullet((float)obj.getDouble("loadBullet"));
			bullet.setDescription(obj.getString("description"));
			
			bullet.setType(obj.getInt("type"));
			bullet.setContinueNum(obj.getInt("continueNum"));
			bullet.setOnceNum(obj.getInt("onceNum"));
			bullet.setChanceFrom((float)obj.getDouble("chanceFrom"));
			bullet.setChanceTo((float)obj.getDouble("chanceTo"));
			bullet.setChangeTargetTime((float)obj.getDouble("changeTargetTime"));
			bullet.setTwoBulletId(obj.getInt("twoBulletId"));
			bullet.setDamageCD((float)obj.getDouble("damageCD"));
			bullet.setSplitCD((float)obj.getDouble("splitCD"));
			bullet.setFireAngle(obj.getInt("fireAngle"));
			
			bullets.put(bullet.getId(), bullet);
		}
	}
	public static GBullet getBullet(int id)
	{
		GBullet bullet = bullets.get(id);
		if(bullet != null)
		{
			bullet.setTid(getRandId());
			return bullet;
		}
		return null;
	}
	
	public static GBubble getBubble(String uid,boolean isRobot,int width,int height)
	{
		JSONObject obj = GServerConfig.bubble;
		
		GBubble bubble = new GBubble();
		bubble.setState(GBubble.STATE.BORN);
		bubble.setUid(uid);
		bubble.setRobot(isRobot);
		bubble.setRobotUid(new ArrayList<String>());
		bubble.setBubbleId(0);
		bubble.setX(GTools.getRand(100, width-100));
		bubble.setY(GTools.getRand(100, height-100));
		
		bubble.setDirX(0);
		bubble.setDirY(1);

		bubble.setLevel(0);
		bubble.setExp(0);
		bubble.setGrow(0.25f);
		bubble.setSpeed((float)obj.getDouble("speed"));
		bubble.setsSpeed((float)obj.getDouble("sSpeed"));
		bubble.setRotateSpeed((float)obj.getDouble("rotateSpeed"));
		bubble.setAngle(0);
		bubble.setKill(0);
		bubble.setDie(0);
		bubble.setSkinId(1);
		bubble.setReduceHP(obj.getInt("reduceHP"));
		bubble.setReduceCD((float)obj.getDouble("reduceCD"));
		
		if(isRobot)
		{
			bubble.setName(GServerConfig.getRandName());
		}
		else
		{
			GSession gs = GSessionHandler.getInstance().getSessionByUid(uid);
			bubble.setName(gs.getUser().getName()); 
		}
		
		return bubble;
	}
	
	public static GBlock getBlock(int blockId,int width,int height)
	{
		GBlock block = new GBlock();
		block.setId(blockId);
		block.setType(0);
		block.setBlockType(GTools.getRand(1,7));
		block.setExp(1);
		block.setX(GTools.getRand(30, width-30));
		block.setY(GTools.getRand(30, height-30));
		block.setState(GBlock.STATE.IDLE);
		return block;
	}
	
//	public static GBlock getBlockBullet(int blockId,int width,int height)
//	{
//		int chance = GTools.getRand(1, 101);
//		int type = 0;
//		int id = 0;
//		for(GBullet bullet : bullets.values())
//		{
//			if(chance > bullet.getChanceFrom()*100 && chance <= bullet.getChanceTo()*100)
//			{
//				type = bullet.getType();
//				id = bullet.getId();
//				break;
//			}
//		}
//		GBlock block = new GBlock();
//		block.setId(blockId);
//		block.setType(1);
//		block.setBulletId(id);
//		block.setBulletType(type);
//		block.setX(GTools.getRand(30, width-30));
//		block.setY(GTools.getRand(30, height-30));
//		block.setState(GBlock.STATE.IDLE);
//		return block;
//	}
//	
//	
//	public static GBlock getBlockHp(int blockId,int width,int height)
//	{
//		GBlock block = new GBlock();
//		block.setId(blockId);
//		block.setType(2);
//		block.setBulletId(0);
//		block.setBulletType(0);
//		block.setX(GTools.getRand(30, width-30));
//		block.setY(GTools.getRand(30, height-30));
//		block.setState(GBlock.STATE.IDLE);
//		return block;
//	}
	
//	public static GClound getClound()
//	{
//		GClound clound = new GClound();
//		clound.setId(1);
//		clound.setX(GTools.getRand(30, GServerConfig.roomWidth-30));
//		clound.setY(GTools.getRand(30, GServerConfig.roomHeight-30));
//		clound.setScale(GTools.getRand(50, GServerConfig.maxClound*100)/100.f);
//		return clound;
//	}
	
	public static GClound getClound(float x,float y,float w,float h)
	{
		GClound clound = new GClound();
		clound.setId(1);
		clound.setX(x);
		clound.setY(y);
		clound.setWidth(w);
		clound.setHeight(h);
		return clound;
	}
	
	private synchronized static int getRandId()
	{
		if(tid > 10000000)
			tid = 0;
		return ++tid;
	}
	
	public static float getDirX(float angle)
	{
		angle = angle > 180 ? angle - 360 : angle;
	    angle = angle < -180 ? angle + 360 : angle;
	    
	    float currAngle = (float) ((angle-90)/(-180 / Math.PI));
	    return (float) Math.cos(currAngle);
	}
	
	public static float getDirY(float angle)
	{
		angle = angle > 180 ? angle - 360 : angle;
	    angle = angle < -180 ? angle + 360 : angle;
	    
	    float currAngle = (float) ((angle-90)/(-180 / Math.PI));
	    return (float) Math.sin(currAngle);
	}
}
