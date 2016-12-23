package com.qianqi.web.model;


import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "user")
public class GUser {
	private long id;
	private String uid;
	private String name;
	private String password;
	private String createdDate;
	
	private String serverId;
	private String serverIp;
	private boolean visitor;
	
	//update user set crystal=0 where id > 0;
	private int maxKill;
	private int cumKill;//累计击杀
	//update user set skinNum=1 where id > 0;
	private int skinNum;
	private int mvp;
	//update user set crystal=1000 where id = 50;
	private int headId;
	private int crystal;
	//update user set skins='1' where id > 0;
	private String skins;
	//update user set skinId=1 where id > 0;
	private int skinId;
	//update user set maxLen=0 where id > 0;
	private int maxLen;
	
	private Long rewardTime;
	
	public GUser(){}
	public GUser(String uid,String name, String password, boolean visitor, String createdDate) {
		super();
		this.uid = uid;
		this.name = name;
		this.password = password;
		this.visitor = visitor;
		this.createdDate = createdDate;
		this.maxKill = 0;
		this.headId = 1;
		this.skinNum = 1;
		this.skins = "1";
		this.skinId = 1;
		this.rewardTime = 0l;
		this.maxLen = 0;
	}
	@Id
	@GeneratedValue
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	@Column(nullable = false, length = 64,unique = true)
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	@Column(nullable = false, length = 64,unique = true)
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Column(nullable = false, length = 64)
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	@Column(name = "created_date", updatable = false,nullable = false, length = 64)
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	@Column(length = 16)
	public String getServerId() {
		return serverId;
	}
	public void setServerId(String serverId) {
		this.serverId = serverId;
	}
	@Transient
	public String getServerIp() {
		return serverIp;
	}
	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}
	public boolean isVisitor() {
		return visitor;
	}
	public void setVisitor(boolean visitor) {
		this.visitor = visitor;
	}
	public int getMaxKill() {
		return maxKill;
	}
	public void setMaxKill(int maxKill) {
		this.maxKill = maxKill;
	}
	public int getHeadId() {
		return headId;
	}
	public void setHeadId(int headId) {
		this.headId = headId;
	}
	public int getCumKill() {
		return cumKill;
	}
	public void setCumKill(int cumKill) {
		this.cumKill = cumKill;
	}
	public int getSkinNum() {
		return skinNum;
	}
	public void setSkinNum(int skinNum) {
		this.skinNum = skinNum;
	}
	public int getMvp() {
		return mvp;
	}
	public void setMvp(int mvp) {
		this.mvp = mvp;
	}
	public int getCrystal() {
		return crystal;
	}
	public void setCrystal(int crystal) {
		this.crystal = crystal;
	}
	@Column(length = 64)
	public String getSkins() {
		return skins;
	}
	public void setSkins(String skins) {
		this.skins = skins;
	}
	public int getSkinId() {
		return skinId;
	}
	public void setSkinId(int skinId) {
		this.skinId = skinId;
	}
	public Long getRewardTime() {
		return rewardTime;
	}
	public void setRewardTime(Long rewardTime) {
		this.rewardTime = rewardTime;
	}
	public int getMaxLen() {
		return maxLen;
	}
	public void setMaxLen(int maxLen) {
		this.maxLen = maxLen;
	}
	
}
