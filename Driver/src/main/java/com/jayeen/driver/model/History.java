/**
 *
 */
package com.jayeen.driver.model;


public class History {

    private int id;
    private String date, distance, time, basePrice, distanceCost, timecost,
            total, firstName, lastName, phone, email, picture, bio,
            referralBonus, promoBonus;
    private String admin_per_payment, driver_per_payment;
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

    public String getReferralBonus() {
        return referralBonus;
    }

    public void setReferralBonus(String referralBonus) {
        this.referralBonus = referralBonus;
    }

    public String getPromoBonus() {
        return promoBonus;
    }

    public void setPromoBonus(String promoBonus) {
        this.promoBonus = promoBonus;
    }

    public String getAdmin_per_payment() {
        return admin_per_payment;
    }

    public void setAdmin_per_payment(String admin_per_payment) {
        this.admin_per_payment = admin_per_payment;
    }


    public String getDriver_per_payment() {
        return driver_per_payment;
    }

    public void setDriver_per_payment(String driver_per_payment) {
        this.driver_per_payment = driver_per_payment;
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

    public String getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(String basePrice) {
        this.basePrice = basePrice;
    }

    public String getDistanceCost() {
        return distanceCost;
    }

    public void setDistanceCost(String distanceCost) {
        this.distanceCost = distanceCost;
    }

    public String getTimecost() {
        return timecost;
    }

    public void setTimecost(String timecost) {
        this.timecost = timecost;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
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

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
