/**
 *
 */
package com.jayeen.driver.model;

import java.io.Serializable;


public class RequestDetail implements Serializable {
    private int requestId;
    private long timeLeft;
    private int jobStatus;
    private long startTime;
    private String time, distance, unit, amount, date, total, basePrice,
            distanceCost, timecost, referralBonus, promoBonus;
    private String priceperKm, priceperHr;
    private String dropAddr,pickupAddr,mapURL;
    private String clientName, clientProfile, clientLatitude, clientLongitude,
            clientPhoneNumber, destinationLatitude, destinationLongitude;
    private String adminPerPayment, driverPerPayment;
    private float estimate;

    public float getEstimate() {
        return estimate;
    }

    public void setEstimate(float estimate) {
        this.estimate = estimate;
    }

    public String getMapURL() {
        return mapURL;
    }

    public void setMapURL(String mapURL) {
        this.mapURL = mapURL;
    }

    public String getDropAddr() {
        return dropAddr;
    }

    public void setDropAddr(String dropAddr) {
        this.dropAddr = dropAddr;
    }

    public String getPickupAddr() {
        return pickupAddr;
    }

    public void setPickupAddr(String pickupAddr) {
        this.pickupAddr = pickupAddr;
    }

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

    public String getDestinationLatitude() {
        return destinationLatitude;
    }

    public void setDestinationLatitude(String destinationLatitude) {
        this.destinationLatitude = destinationLatitude;
    }

    public String getDestinationLongitude() {
        return destinationLongitude;
    }

    public void setDestinationLongitude(String destinationLongitude) {
        this.destinationLongitude = destinationLongitude;
    }

    private float clientRating;

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
        return adminPerPayment;
    }

    public void setAdmin_per_payment(String adminPerPayment) {
        this.adminPerPayment = adminPerPayment;
    }


    public String getDriver_per_payment() {
        return driverPerPayment;
    }

    public void setDriver_per_payment(String driverPerPayment) {
        this.driverPerPayment = driverPerPayment;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
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

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }


    public long getStartTime() {
        return startTime;
    }


    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public int getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(int jobStatus) {
        this.jobStatus = jobStatus;
    }

    public int getRequestId() {
        return requestId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientProfile() {
        return clientProfile;
    }

    public void setClientProfile(String clientProfile) {
        this.clientProfile = clientProfile;
    }

    public float getClientRating() {
        return clientRating;
    }

    public void setClientRating(float clientRating) {
        this.clientRating = clientRating;
    }

    public String getClientLatitude() {
        return clientLatitude;
    }

    public void setClientLatitude(String clientLatitude) {
        this.clientLatitude = clientLatitude;
    }

    public String getClientLongitude() {
        return clientLongitude;
    }

    public void setClientLongitude(String clientLongitude) {
        this.clientLongitude = clientLongitude;
    }

    public String getClientPhoneNumber() {
        return clientPhoneNumber;
    }

    public void setClientPhoneNumber(String clientPhoneNumber) {
        this.clientPhoneNumber = clientPhoneNumber;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public long getTimeLeft() {
        return timeLeft;
    }

    public void setTimeLeft(long timeLeft) {
        this.timeLeft = timeLeft;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

}
