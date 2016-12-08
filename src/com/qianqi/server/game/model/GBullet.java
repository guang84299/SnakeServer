package com.qianqi.server.game.model;

public class GBullet {
	private int id;
	private int tid;
	private String name;
	private float bulletSpeed;//子弹飞行速度
	private float bulletTime;//子弹飞行时间
	private float bulletAccelerated;//子弹加速度
	private float rotateSpeed;//子弹旋转速度
	private int damage;//子弹伤害
	private float buffTime;//buff持续时间
	private float downSpeed;//减速
	private int num;//最大子弹数
	private int range;//子弹射程
	private int targetRange;//寻找目标的范围
	private float CD;//发射间隔时间
	private float fillBullet;//填满子弹时间
	private float loadBullet;//恢复子弹时间
	private String description;//特殊功能描述
	
	private int type;//子弹类型id
	private int continueNum;//连续次数
	private int onceNum;//一次发射数量
	private float chanceFrom;//出现几率
	private float chanceTo;//出现几率
	private float changeTargetTime;//改变目标时间
	private int twoBulletId;//二段子弹id
	private float damageCD;//伤害cd时间
	private float splitCD;//分裂cd
	private int fireAngle;//开火角度
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}	
	public int getTid() {
		return tid;
	}
	public void setTid(int tid) {
		this.tid = tid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public float getBulletSpeed() {
		return bulletSpeed;
	}
	public void setBulletSpeed(float bulletSpeed) {
		this.bulletSpeed = bulletSpeed;
	}
	public float getBulletTime() {
		return bulletTime;
	}
	public void setBulletTime(float bulletTime) {
		this.bulletTime = bulletTime;
	}
	public float getBulletAccelerated() {
		return bulletAccelerated;
	}
	public void setBulletAccelerated(float bulletAccelerated) {
		this.bulletAccelerated = bulletAccelerated;
	}
	public float getRotateSpeed() {
		return rotateSpeed;
	}
	public void setRotateSpeed(float rotateSpeed) {
		this.rotateSpeed = rotateSpeed;
	}
	public int getDamage() {
		return damage;
	}
	public void setDamage(int damage) {
		this.damage = damage;
	}
	public float getBuffTime() {
		return buffTime;
	}
	public void setBuffTime(float buffTime) {
		this.buffTime = buffTime;
	}
	public float getDownSpeed() {
		return downSpeed;
	}
	public void setDownSpeed(float downSpeed) {
		this.downSpeed = downSpeed;
	}
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public int getRange() {
		return range;
	}
	public void setRange(int range) {
		this.range = range;
	}
	public int getTargetRange() {
		return targetRange;
	}
	public void setTargetRange(int targetRange) {
		this.targetRange = targetRange;
	}
	public float getCD() {
		return CD;
	}
	public void setCD(float cD) {
		CD = cD;
	}
	public float getFillBullet() {
		return fillBullet;
	}
	public void setFillBullet(float fillBullet) {
		this.fillBullet = fillBullet;
	}
	public float getLoadBullet() {
		return loadBullet;
	}
	public void setLoadBullet(float loadBullet) {
		this.loadBullet = loadBullet;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getContinueNum() {
		return continueNum;
	}
	public void setContinueNum(int continueNum) {
		this.continueNum = continueNum;
	}
	public int getOnceNum() {
		return onceNum;
	}
	public void setOnceNum(int onceNum) {
		this.onceNum = onceNum;
	}
	public float getChanceFrom() {
		return chanceFrom;
	}
	public void setChanceFrom(float chanceFrom) {
		this.chanceFrom = chanceFrom;
	}
	public float getChanceTo() {
		return chanceTo;
	}
	public void setChanceTo(float chanceTo) {
		this.chanceTo = chanceTo;
	}
	public float getChangeTargetTime() {
		return changeTargetTime;
	}
	public void setChangeTargetTime(float changeTargetTime) {
		this.changeTargetTime = changeTargetTime;
	}
	public int getTwoBulletId() {
		return twoBulletId;
	}
	public void setTwoBulletId(int twoBulletId) {
		this.twoBulletId = twoBulletId;
	}
	public float getDamageCD() {
		return damageCD;
	}
	public void setDamageCD(float damageCD) {
		this.damageCD = damageCD;
	}
	public float getSplitCD() {
		return splitCD;
	}
	public void setSplitCD(float splitCD) {
		this.splitCD = splitCD;
	}
	public int getFireAngle() {
		return fireAngle;
	}
	public void setFireAngle(int fireAngle) {
		this.fireAngle = fireAngle;
	}
	
	
	
}
