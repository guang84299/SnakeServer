package com.qianqi.server.game.model;

import net.sf.json.JSONArray;

public class GDrop {

	
	private int exp;
	private JSONArray pos;
	
	public GDrop(int exp, JSONArray pos) {
		super();
		this.exp = exp;
		this.pos = pos;
	}

	public int getExp() {
		return exp;
	}

	public void setExp(int exp) {
		this.exp = exp;
	}

	public JSONArray getPos() {
		return pos;
	}

	public void setPos(JSONArray pos) {
		this.pos = pos;
	}
	
	
}
