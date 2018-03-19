/**
 * 
 */
package com.jayeen.customer.models;


/**
 * @author Hardik A Bhalodi
 * 
 */
public class AllProviderList {

	private String firstName;
	private String lastName;
	int driverId;
	private String phone;
	private String email;
	private String bio;
	private String picture;
	private double latitude;
	private double longitude;
	private int vehicleTypeId;
	private String name;
	private String carModel;
	private String carNumber;
	private String icon;
	private int isDefault;
	private double pricePerUnitTime;
	private double basePrice, pricePerUnitDistance;
	private int baseDistance;
	public boolean isSelected;
	private String minFare;
	private String maxSize;
	private String unit;
	private String duration;


	public int getDriverId() {
		return driverId;
	}

	public void setDriverId(int driverId) {
		this.driverId = driverId;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}



	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public String getCarModel() {
		return carModel;
	}

	public void setCarModel(String carModel) {
		this.carModel = carModel;
	}

	public String getCarNumber() {
		return carNumber;
	}

	public void setCarNumber(String carNumber) {
		this.carNumber = carNumber;
	}

	public int getVehicleTypeId() {
		return vehicleTypeId;
	}

	public void setVehicleTypeId(int vehicleTypeId) {
		this.vehicleTypeId = vehicleTypeId;
	}


	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public int getBaseDistance() {
		return baseDistance;
	}

	public void setBaseDistance(int baseDistance) {
		this.baseDistance = baseDistance;
	}



	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public int getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(int isDefault) {
		this.isDefault = isDefault;
	}

	public double getPricePerUnitTime() {
		return pricePerUnitTime;
	}

	public void setPricePerUnitTime(double pricePerUnitTime) {
		this.pricePerUnitTime = pricePerUnitTime;
	}

	public double getPricePerUnitDistance() {
		return pricePerUnitDistance;
	}

	public void setPricePerUnitDistance(double pricePerUnitDistance) {
		this.pricePerUnitDistance = pricePerUnitDistance;
	}

	public double getBasePrice() {
		return basePrice;
	}

	public void setBasePrice(double basePrice) {
		this.basePrice = basePrice;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public String getMinFare() {
		return minFare;
	}

	public void setMinFare(String minFare) {
		this.minFare = minFare;
	}

	public String getMaxSize() {
		return maxSize;
	}

	public void setMaxSize(String maxSize) {
		this.maxSize = maxSize;
	}

}
