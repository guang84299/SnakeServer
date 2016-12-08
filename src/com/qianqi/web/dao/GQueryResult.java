package com.qianqi.web.dao;

import java.util.List;

public class GQueryResult<T> {
	private List<T> list;
	private Long num;
	public List<T> getList() {
		return list;
	}
	public void setList(List<T> list) {
		this.list = list;
	}
	public Long getNum() {
		return num;
	}
	public void setNum(Long num) {
		this.num = num;
	}
	
	
}
