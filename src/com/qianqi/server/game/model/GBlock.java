package com.qianqi.server.game.model;


public class GBlock {
	
	public enum STATE
	{
		 IDLE,//待机
	     DIE//死亡
	}
	
	private int id;
	private int type;//0:普通水滴 1：掉落水晶 2：血袋
	private int blockType;
	private int exp;
	private float x;
	private float y;
	private STATE state;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public float getX() {
		return x;
	}
	public void setX(float x) {
		this.x = x;
	}
	public float getY() {
		return y;
	}
	public void setY(float y) {
		this.y = y;
	}
	public STATE getState() {
		return state;
	}
	public void setState(STATE state) {
		this.state = state;
	}
	public int getBlockType() {
		return blockType;
	}
	public void setBlockType(int blockType) {
		this.blockType = blockType;
	}
	public int getExp() {
		return exp;
	}
	public void setExp(int exp) {
		this.exp = exp;
	}
	
	
}
