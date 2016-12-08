package com.qianqi.web.tools;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class GSpringTools {
private static ApplicationContext applicationContext;
	
	private GSpringTools()
	{
		
	}
	
	static
	{
		applicationContext = new ClassPathXmlApplicationContext("beans.xml");
	}
	public static  ApplicationContext getApplicationContext()
	{
		return applicationContext;
	}
}
