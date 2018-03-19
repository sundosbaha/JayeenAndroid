/**
 * 
 */
package com.jayeen.customer.models;

import android.os.Parcel;
import android.os.Parcelable;


public class Driver implements Parcelable {

	private String firstName;
	private String lastName;
	int driverId;
	private String phone;
	private String bio;
	private String picture;
	private double latitude;
	private double longitude;
	private double rating;
	private String lastTime;
	private String lastDistance;
	private String carModel;
	private String carNumber;
	private Bill bill;
	private int vehicleTypeId;
	private double bearing;
	private String priceperKm;
	private String priceperHr;


	public String getPriceperKm() {
		return priceperKm;
	}

	public void setPriceperKm(String priceperKm) {
		this.priceperKm = priceperKm;
	}

	public String getPriceperHr() {
		return priceperHr;
	}

	public void setPriceperHr(String priceperHr) {
		this.priceperHr = priceperHr;
	}

	public double getBearing() {
		return bearing;
	}

	public void setBearing(double bearing) {
		this.bearing = bearing;
	}

	public Bill getBill() {
		return bill;
	}

	public void setBill(Bill bill) {
		this.bill = bill;
	}

	public double getRating() {
		return rating;
	}

	public void setRating(double rating) {
		this.rating = rating;
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

	public String getBio() {
		return bio;
	}

	public void setBio(String bio) {
		this.bio = bio;
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

	public String getLastTime() {
		return lastTime;
	}

	public void setLastTime(String lastTime) {
		this.lastTime = lastTime;
	}

	public String getLastDistance() {
		return lastDistance;
	}

	public void setLastDistance(String lastDistance) {
		this.lastDistance = lastDistance;
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

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(firstName);
		dest.writeString(lastName);
		dest.writeInt(driverId);
		dest.writeString(phone);
		dest.writeString(bio);
		dest.writeString(picture);
		dest.writeDouble(latitude);
		dest.writeDouble(longitude);
		dest.writeDouble(rating);
		dest.writeString(lastTime);
		dest.writeString(lastDistance);
		dest.writeString(carModel);
		dest.writeString(carNumber);
		dest.writeInt(vehicleTypeId);
		dest.writeDouble(bearing);
	}

	public int getDriverId() {
		return driverId;
	}

	public void setDriverId(int driverId) {
		this.driverId = driverId;
	}

}
