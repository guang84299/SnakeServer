package com.qianqi.web.action;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.apache.commons.io.FileUtils;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.qianqi.server.GServerConfig;
import com.qianqi.server.GServerController;
import com.qianqi.web.common.GCommon;
import com.qianqi.web.model.GUser;
import com.qianqi.web.service.GUserService;
import com.qianqi.web.tools.GStringTools;
import com.qianqi.web.tools.GTools;
import com.qianqi.web.tools.GZipTool;

public class GUserAction extends ActionSupport{

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(GUserAction.class);
	@Resource private  GUserService userService;
	
	
	private File config;
	private File source;
	private String configFileName;
	private String sourceFileName;
	
	public void login()
	{
		String data = ServletActionContext.getRequest().getParameter("data");
		JSONObject resobj = GStringTools.getResPonseData();
		String respose = resobj.toString();
		if(GStringTools.isEmpty(data))
		{
			resobj.put(GCommon.response_type, GCommon.login_fail);
			respose = resobj.toString();
		}
		else
		{
			JSONObject obj = JSONObject.fromObject(data);			
			String uid = obj.getString("uid");			
			GUser user = userService.find(uid);
			if(user != null)
			{
				//登录成功
				user.setServerIp(GServerController.getInstance().getServerIp(user.getServerId()));
				resobj.put(GCommon.response_type, GCommon.login_success);
				resobj.put("data", JSONObject.fromObject(user).toString());
				respose = resobj.toString();
			}
			else
			{
				//登录失败
				//尝试注册
				
				String name = GTools.getRandomUUID();
				String password = "123";
				user = new GUser(uid,name, password,true, GTools.dataToString(new Date()));
				//先默认分配到最新的服务器
				user.setServerId(GServerController.getInstance().allotServerId());
				user.setServerIp(GServerController.getInstance().getServerIp(user.getServerId()));
				boolean b = userService.add(user);	
				if(b)
				{
					user.setName(user.getId()+"");
					b = userService.update(user);				
				}
				//注册成功 返回给客户端开始连接游戏服务器
				if(b)
				{
					//登录成功
					resobj.put(GCommon.response_type, GCommon.login_success);
					resobj.put("data", JSONObject.fromObject(user).toString());
					respose = resobj.toString();
					logger.warn(name + "  注册成功！");
				}
				else
				{
					//注册失败
					resobj.put(GCommon.response_type, GCommon.login_noexist);
					respose = resobj.toString();
					logger.warn(name + "  注册失败！");
				}						
			}
		}	
		print(respose);
	}
	
	public void print(Object data) {
		try {
			ServletActionContext.getResponse().getWriter().print(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String uploadConfig()
	{
		if(config == null)
		{
			ActionContext.getContext().put("uploadConfig", "上传失败！");
			return "index";
		}
		
		URL url = GUserAction.class.getClassLoader().getResource("log4j.properties");
		String relpath = url.getPath().replace("log4j.properties", "");
		//上传
		File file = new File(new File(relpath), configFileName);
		if (!file.getParentFile().exists())
			file.getParentFile().mkdirs();
		try {
			FileUtils.copyFile(config, file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		GServerConfig.initConfig();
		GServerController.getInstance().removeAllUserRoom();
		ActionContext.getContext().put("uploadConfig", "上传成功！");
		return "index";
	}
	
	public String uploadSource()
	{
		if(source == null)
		{
			ActionContext.getContext().put("uploadSource", "上传失败！");
			return "index";
		}
		
		URL url = GUserAction.class.getClassLoader().getResource("log4j.properties");
		String relpath = url.getPath().replace("classes/log4j.properties", "");
		//上传
		File file = new File(new File(relpath), sourceFileName);
		if (!file.getParentFile().exists())
			file.getParentFile().mkdirs();
		try {
			FileUtils.copyFile(source, file);
			//解压
			GZipTool.unzip(file.getAbsolutePath());
			file.delete();
		} catch (IOException e) {
			e.printStackTrace();
		}
		ActionContext.getContext().put("uploadSource", "上传成功！");
		return "index";
	}
	
	public void test()
	{
		for (int i = 0; i < 100; i++) {
			String name =  GTools.getRandomUUID();;
			String password = "123";
			GUser user = new GUser("test_"+i,name, password,true, GTools.dataToString(new Date()));
			//先默认分配到最新的服务器
			user.setServerId(GServerController.getInstance().allotServerId());
			user.setServerIp(GServerController.getInstance().getServerIp(user.getServerId()));
			boolean b = userService.add(user);	
			if(b)
			{
				user.setName(user.getId()+"");
				b = userService.update(user);				
			}
			
			print("完成-->"+user.getUid());
		}
	}
	
	public void test2()
	{
		List<GUser> list = userService.findAlls().getList();
		for(GUser user : list)
		{
			user.setMaxKill(0);
			user.setCumKill(0);
			user.setSkinNum(0);
			user.setMvp(0);
			user.setHeadId(0);
		}
	}

	public File getConfig() {
		return config;
	}

	public void setConfig(File config) {
		this.config = config;
	}

	public String getConfigFileName() {
		return configFileName;
	}

	public void setConfigFileName(String configFileName) {
		this.configFileName = configFileName;
	}

	public File getSource() {
		return source;
	}

	public void setSource(File source) {
		this.source = source;
	}

	public String getSourceFileName() {
		return sourceFileName;
	}

	public void setSourceFileName(String sourceFileName) {
		this.sourceFileName = sourceFileName;
	}

	
}
