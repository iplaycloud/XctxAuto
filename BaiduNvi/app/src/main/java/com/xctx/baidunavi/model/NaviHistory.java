package com.xctx.baidunavi.model;

public class NaviHistory {

	int id; // 导航历史ID
	String key; // 导航关键字
	String city; // 导航城市

	public NaviHistory(String key, String city) {
		super();
		this.key = key;
		this.city = city;
	}

	public NaviHistory(int id, String key, String city) {
		super();
		this.id = id;
		this.key = key;
		this.city = city;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

}
