package com.qianqi.web.service.impl;

import java.util.LinkedHashMap;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.qianqi.web.dao.GDaoTools;
import com.qianqi.web.dao.GQueryResult;
import com.qianqi.web.model.GUser;
import com.qianqi.web.service.GUserService;

@Service
public class GUserServiceImpl implements GUserService{
	@Resource private  GDaoTools daoTools;
	@Override
	public boolean add(GUser user) {
		try {
			daoTools.add(user);
			return true;
		} catch (Exception e) {
		}	
		return false;
	}

	@Override
	public boolean delete(long id) {
		try {
			daoTools.delete(GUser.class, id);
			return true;
		} catch (Exception e) {
		}
		return false;
	}

	@Override
	public boolean update(GUser user) {
		try {
			daoTools.update(user);
			return true;
		} catch (Exception e) {
		}
		return false;
	}

	@Override
	public GUser find(long id) {
		return daoTools.find(GUser.class, id);
	}
	
	@Override
	public GUser findByName(String name)
	{
		GQueryResult<GUser> qr = daoTools.find(GUser.class, "name",name, 0, 1, null);
		if(qr.getList() != null && qr.getList().size() > 0)
		return qr.getList().get(0);
		return null;
	}

	@Override
	public GUser find(String name, String password) {
		GQueryResult<GUser> qr = daoTools.find(GUser.class, "name",name,"password",password, 0, 1, null);
		if(qr.getList() != null && qr.getList().size() > 0)
		return qr.getList().get(0);
		return null;
	}

	@Override
	public GQueryResult<GUser> findAlls() {
		LinkedHashMap<String, String> lhm = new LinkedHashMap<String, String>();
		lhm.put("id", "desc");
		return daoTools.find(GUser.class, null, null, 0, 10000000, lhm);
	}

	@Override
	public GQueryResult<GUser> find(LinkedHashMap<String, String> colvals) {
		return daoTools.find(GUser.class, colvals, 0, 100000000, null);
	}

	@Override
	public GUser find(String uid) {
		GQueryResult<GUser> qr = daoTools.find(GUser.class, "uid",uid, 0, 1, null);
		if(qr.getList() != null && qr.getList().size() > 0)
		return qr.getList().get(0);
		return null;
	}

	@Override
	public GQueryResult<GUser> find(LinkedHashMap<String, String> colvals,LinkedHashMap<String, String> desc) {
		return daoTools.find(GUser.class, colvals, 0, 100000000, desc);
	}
}
