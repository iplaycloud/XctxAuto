package com.tchip.gaodenavi.model;

public class NaviResultInfo {

	/**
	 * 序号
	 */
	int number;

	/**
	 * 名称
	 */
	String name;

	/**
	 * 地址
	 */
	String address;

	/**
	 * 经度
	 */
	double longitude;

	/**
	 * 纬度
	 */
	double latitude;

	/**
	 * 距离
	 */
	double distance;

	public NaviResultInfo(int number, String name, String address,
			double longitude, double latitude, double distance) {
		super();
		this.number = number;
		this.name = name;
		this.address = address;
		this.longitude = longitude;
		this.latitude = latitude;
		this.distance = distance;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}
}
