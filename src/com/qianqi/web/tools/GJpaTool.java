package com.qianqi.web.tools;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class GJpaTool {
	private static EntityManagerFactory emf;
	private static EntityManager em;
	
	static
	{
		emf = Persistence.createEntityManagerFactory("others");
		em = emf.createEntityManager();
	}
	
	public static EntityManager getEntityManager()
	{
		return em;
	}
}
