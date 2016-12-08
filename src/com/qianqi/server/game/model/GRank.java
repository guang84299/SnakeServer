package com.qianqi.server.game.model;

public class GRank {
	private int rank;
	private String uid;
	private String name;
	private int exp;
	private int kill;
	private int die;
	private int rewardNum;
	
	public int getRank() {
		return rank;
	}
	public void setRank(int rank) {
		this.rank = rank;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getExp() {
		return exp;
	}
	public void setExp(int exp) {
		this.exp = exp;
	}
	public int getKill() {
		return kill;
	}
	public void setKill(int kill) {
		this.kill = kill;
	}
	public int getDie() {
		return die;
	}
	public void setDie(int die) {
		this.die = die;
	}
	public int getRewardNum() {
		return rewardNum;
	}
	public void setRewardNum(int rewardNum) {
		this.rewardNum = rewardNum;
	}

	
}
