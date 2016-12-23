package com.qianqi.server.game.model;

import java.util.ArrayList;
import java.util.List;

import com.qianqi.server.GServerConfig;

public class GBubble {
	
	public enum STATE
	{
		 BORN,//出生
		 IDLE,//待机
	     MOVE,//移动
	     SPEEDUP,//加速
	     DIE//死亡
	}
	
	private String uid;
	private String name;
	private long roomId;
	private STATE state;
	
	private float speed;
	private float sSpeed;//冲刺速度
	private float rotateSpeed;//旋转速度
	private float x;
	private float y;
	private float rotate;
	private float angle;
	float dirX;//方向
    float dirY;
    private int level;
    private int exp;
    private float grow;
    private int kill;
    private int die;
    private int reduceHP;//消耗血量
    private float reduceCD;//消耗CD
    
    private boolean robot;
    //机器人独有属性 
    private long bubbleId;
    //飞机独有属性
    private List<String> robotUid;
    private int skinId;
    
    //服务器独有属性
    public long currAttackTime = 0;
    public long killTime = 0;
    public int killNum = 0;
    public boolean clear = false;
    public long robotDieTime = 0;
    
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
	public long getRoomId() {
		return roomId;
	}
	public void setRoomId(long roomId) {
		this.roomId = roomId;
	}
	public STATE getState() {
		return state;
	}
	public void setState(STATE state) {
		this.state = state;
	}
	public float getSpeed() {
		return speed;
	}
	public void setSpeed(float speed) {
		this.speed = speed;
	}
	public float getsSpeed() {
		return sSpeed;
	}
	public void setsSpeed(float sSpeed) {
		this.sSpeed = sSpeed;
	}
	public float getRotateSpeed() {
		return rotateSpeed;
	}
	public void setRotateSpeed(float rotateSpeed) {
		this.rotateSpeed = rotateSpeed;
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
	public float getRotate() {
		return rotate;
	}
	public void setRotate(float rotate) {
		this.rotate = rotate;
	}
	public float getDirX() {
		return dirX;
	}
	public void setDirX(float dirX) {
		this.dirX = dirX;
	}
	public float getDirY() {
		return dirY;
	}
	public void setDirY(float dirY) {
		this.dirY = dirY;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public int getExp() {
		return exp;
	}
	public void setExp(int exp) {
		this.exp = exp;
	}
	public float getGrow() {
		return grow;
	}
	public void setGrow(float grow) {
		this.grow = grow;
	}
	public boolean isRobot() {
		return robot;
	}
	public void setRobot(boolean robot) {
		this.robot = robot;
	}
	public long getBubbleId() {
		return bubbleId;
	}
	public void setBubbleId(long bubbleId) {
		this.bubbleId = bubbleId;
	}
	public List<String> getRobotUid() {
		return robotUid;
	}
	public void setRobotUid(List<String> robotUid) {
		this.robotUid = robotUid;
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
	public int getSkinId() {
		return skinId;
	}
	public void setSkinId(int skinId) {
		this.skinId = skinId;
	}
	public int getReduceHP() {
		return reduceHP;
	}
	public void setReduceHP(int reduceHP) {
		this.reduceHP = reduceHP;
	}
	public float getReduceCD() {
		return reduceCD;
	}
	public void setReduceCD(float reduceCD) {
		this.reduceCD = reduceCD;
	}
	public float getAngle() {
		return angle;
	}
	public void setAngle(float angle) {
		this.angle = angle;
	}

	
}
