package com.qianqi.test;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.qianqi.web.tools.GZipTool;

public class GUpdate {

	public static void main(String[] args) {
		File file = GZipTool.zip("/Users/guang/Documents/work/apache-tomcat-7.0.69/webapps/SnakeServer/WEB-INF/classes");
		try {
			FileUtils.copyFile(file, new File("/Users/guang/web/classes.zip"));
			file.delete();
//			String os = System.getProperties().getProperty("os.name");
//			if(os.contains("Mac"))
//			{
//				Runtime.getRuntime().exec("/Users/guang/Documents/work/apache-tomcat-7.0.69/bin/shutdown.sh");
//				Runtime.getRuntime().exec("/Users/guang/Documents/work/apache-tomcat-7.0.69/bin/startup.sh");
//			}
//			else
//			{
//				Runtime.getRuntime().exec("C:/server/apache-tomcat-6.0.37/bin/shutdown.bat");
//				Runtime.getRuntime().exec("C:/server/apache-tomcat-6.0.37/bin/startup.bat");
//			}
			System.out.println("压缩成功");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
