package com.qianqi.server;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.qianqi.server.game.tools.GMapTool;
import com.qianqi.server.game.tools.GModelTool;
import com.qianqi.web.tools.GTools;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


public class GServerConfig {
	public static JSONObject config;
	public static JSONObject bubble;
//	public static JSONArray bullets;
	public static JSONArray maps;
	public static JSONArray skins;
	public static JSONArray statements;
	public static String[] names;
	public static String res_name;
	//服务器列表
	public static final Map<String,String> serverList = new HashMap<String, String>();
	
	//房间配置
	public static int maxRooms;
	public static int maxRoomSessions;
	public static long roomTime;
	public static int numBlock;//水滴标准数量
	public static int refreshTime;//刷新间隔	
	public static int numClound;//初始云朵个数
	public static int maxClound;//云朵最大百分比
//	public static int bulletId;//初始子弹id
	public static int maxRobotNum;
//	public static int roomWidth;
//	public static int roomHeight;
	public static int numBullet;//初始特殊子弹个数
	public static int refreshBullet;//刷新子弹个数
	public static int killBlock;//击杀奖励
	public static int lifeRefreshTime;//血包刷新时间
	public static int shareAward;//分享奖励

	//经验
	public static List<Integer> exps = new ArrayList<Integer>();
	//升级外观大小
	public static List<Float> grows = new ArrayList<Float>();
	
	
	public static void initConfig()
	{
		serverList.clear();
		exps.clear();
		grows.clear();
		
		String res = initConfig("gameConfig.json");
//		String res_bullets = initConfig("bullets.json");
		String res_maps = initConfig("maps.json");
		String res_skins = initConfig("skins.json");
		String res_statements = initConfig("statements.json");
		String res_names = initConfig("names.txt");
	     
	     config = JSONObject.fromObject(res);
	     
	     maxRooms = config.getInt("maxRooms");
	     maxRoomSessions = config.getInt("maxRoomSessions");
	     roomTime = config.getInt("roomTime") * 60 * 1000;
	     numBlock = config.getInt("numBlock");
	     refreshTime = config.getInt("refreshTime") * 1000;
//	     bulletId = config.getInt("bulletId");
	     numClound = config.getInt("numClound");
	     maxClound = config.getInt("maxClound");
	     maxRobotNum = config.getInt("maxRobotNum");
//	     roomWidth = config.getInt("roomWidth");
//	     roomHeight = config.getInt("roomHeight");
	     numBullet = config.getInt("numBullet");
	     refreshBullet = config.getInt("refreshBullet");
	     killBlock = config.getInt("killBlock");
	     lifeRefreshTime = config.getInt("lifeRefreshTime") * 1000;
	     shareAward = config.getInt("shareAward");
	     
	     serverList.put("1", config.getString("ip"));
	     	     
	     bubble = config.getJSONObject("bubble");
//	     bullets = JSONArray.fromObject(res_bullets);
	     maps = JSONArray.fromObject(res_maps);
	     skins = JSONArray.fromObject(res_skins);
	     statements = JSONArray.fromObject(res_statements);
	     
	     res_names = res_names.replaceAll("\\s", "");
	     res_name = res_names;
		 names = res_names.split(",");
		 
	     //解析地图
	     JSONArray mapArr = new JSONArray();
	     for(int i=0;i<maps.size();i++)
	     {
	    	 JSONObject map = maps.getJSONObject(i);
	    	 JSONObject m = GMapTool.init(initConfig(map.getString("path")));
	    	 m.put("id", map.getInt("id"));
	    	 mapArr.add(m);
	     }
	     maps = mapArr;
	     
	     GModelTool.initBullet();
	     
	     String exp = bubble.getString("exp");
	     String grow = bubble.getString("grow");
	     String []exp_arr = exp.split("/");
	     for(String exp_str : exp_arr)
	     {
	    	 exps.add(Integer.parseInt(exp_str));
	     }
	     
	     String []grow_arr = grow.split("/");
	     for(String grow_str : grow_arr)
	     {
	    	 grows.add(Float.parseFloat(grow_str));
	     }
	}
	
	public static int getExpForLevel(int level)
	{
		if(level <= 0)
			return 0;
		if(level >= exps.size())
			return exps.get(exps.size()-1);
		
		return exps.get(level-1);
	}
	
	public static int getLevelForExp(int exp)
	{
		if(exp <= 0)
			return 0;
		for(int i=0;i<exps.size()-1;i++)
		{
			if(exp >= exps.get(i) && exp < exps.get(i+1))
				return i+1;
		}
		return exps.size();
	}
	
	public static float getGrowForLevel(int level)
	{
		if(level <= 0)
			return 0.25f;
		if(level >= grows.size())
			return grows.get(grows.size()-1);
		
		return grows.get(level-1);
	}
	
	public static int getCrystalForRank(int rank)
	{
		if(rank <= 0 || rank > statements.size())
			return 0;
		return statements.getJSONObject(rank-1).getInt("num");
	}
	
	public static String getRandName()
	{
		String name = names[GTools.getRand(0, names.length-1)];
		return name.replace("\"", "");
	}
	
	public static String initConfig(String path)
	{
		URL url = GServer.class.getClassLoader().getResource(path);	
		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
	     String res = "";   
	     try {
	        fis = new FileInputStream(url.getPath());
	 	    isr =new InputStreamReader(fis,"UTF-8");
	 	    br = new BufferedReader(isr);
	 	    String s = null;  
			while((s=br.readLine())!=null){
				res += s;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	     finally
	     {
	    	 try {
				br.close();
				isr.close();
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}	    	 
	     }
	     return res;
	}
}
