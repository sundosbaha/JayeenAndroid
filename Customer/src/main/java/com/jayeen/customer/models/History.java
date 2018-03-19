/**
 * 
 */
package com.jayeen.customer.models;


public class History {

	private int id;
	private String date, distance, time, basePrice, distanceCost, timecost,
			total, firstName, lastName, phone, email, picture, bio, type, unit,
			promoBonus, referralBonus,startLat,startLong,endLat,endLong,map_url;
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

	public String getSetbase_distance() {
		return setbase_distance;
	}

	public void setSetbase_distance(String setbase_distance) {
		this.setbase_distance = setbase_distance;
	}

	private String setbase_distance;
	public String getMap_url() {
		return map_url;
	}

	public void setMap_url(String map_url) {
		this.map_url = map_url;
	}

	public String getStartLat() {
		return startLat;
	}

	public void setStartLat(String startLat) {
		this.startLat = startLat;
	}

	public String getStartLong() {
		return startLong;
	}

	public void setStartLong(String startLong) {
		this.startLong = startLong;
	}

	public String getEndLat() {
		return endLat;
	}

	public void setEndLat(String endLat) {
		this.endLat = endLat;
	}

	public String getEndLong() {
		return endLong;
	}

	public void setEndLong(String endLong) {
		this.endLong = endLong;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	/**
	 * @return the basePrice
	 */
	public String getBasePrice() {
		return basePrice;
	}

	/**
	 * @param basePrice
	 *            the basePrice to set
	 */
	public void setBasePrice(String basePrice) {
		this.basePrice = basePrice;
	}

	/**
	 * @return the distanceCost
	 */
	public String getDistanceCost() {
		return distanceCost;
	}

	/**
	 * @param distanceCost
	 *            the distanceCost to set
	 */
	public void setDistanceCost(String distanceCost) {
		this.distanceCost = distanceCost;
	}

	/**
	 * @return the timecost
	 */
	public String getTimecost() {
		return timecost;
	}

	/**
	 * @param timecost
	 *            the timecost to set
	 */
	public void setTimecost(String timecost) {
		this.timecost = timecost;
	}

	/**
	 * @return the total
	 */
	public String getTotal() {
		return total;
	}

	/**
	 * @param total
	 *            the total to set
	 */
	public void setTotal(String total) {
		this.total = total;
	}

	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @param firstName
	 *            the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @param lastName
	 *            the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * @return the phone
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * @param phone
	 *            the phone to set
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email
	 *            the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the picture
	 */
	public String getPicture() {
		return picture;
	}

	/**
	 * @param picture
	 *            the picture to set
	 */
	public void setPicture(String picture) {
		this.picture = picture;
	}

	/**
	 * @return the bio
	 */
	public String getBio() {
		return bio;
	}

	/**
	 * @param bio
	 *            the bio to set
	 */
	public void setBio(String bio) {
		this.bio = bio;
	}

	public String getPromoBonus() {
		return promoBonus;
	}

	public void setPromoBonus(String promoBonus) {
		this.promoBonus = promoBonus;
	}

	public String getReferralBonus() {
		return referralBonus;
	}

	public void setReferralBonus(String referralBonus) {
		this.referralBonus = referralBonus;
	}

}
