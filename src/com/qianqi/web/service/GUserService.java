package com.qianqi.web.service;

import java.util.LinkedHashMap;

import org.springframework.stereotype.Service;

import com.qianqi.web.dao.GQueryResult;
import com.qianqi.web.model.GUser;

@Service
public interface GUserService {
	boolean add(GUser user);
	boolean delete(long id);
	boolean update(GUser user);
	GUser find(long id);
	GUser find(String uid);
	GUser findByName(String name);
	GUser find(String name,String password);
	GQueryResult<GUser> findAlls();
	GQueryResult<GUser> find(LinkedHashMap<String, String> colvals);
	GQueryResult<GUser> find(LinkedHashMap<String, String> colvals,LinkedHashMap<String, String> desc);
}
