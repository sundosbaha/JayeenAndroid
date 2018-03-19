/**
 * 
 */
package com.jayeen.customer.models;


public class ScheduleHistory {

	private int id;

	private int iscancled;
	private String date, time, pickupaddress,dropaddress,vehicletype;

	public int getIscancled() {
		return iscancled;
	}

	public void setIscancled(int iscancled) {
		this.iscancled = iscancled;
	}

	public String getPickupaddress() {
		return pickupaddress;
	}

	public void setPickupaddress(String pickupaddress) {
		this.pickupaddress = pickupaddress;
	}

	public String getDropaddress() {
		return dropaddress;
	}

	public void setDropaddress(String dropaddress) {
		this.dropaddress = dropaddress;
	}

	public String getVehicletype() {
		return vehicletype;
	}

	public void setVehicletype(String vehicletype) {
		this.vehicletype = vehicletype;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the date
	 */
	public String getDate() {
		return date;
	}

	/**
	 * @param date
	 *            the date to set
	 */
	public void setDate(String date) {
		this.date = date;
	}



	/**
	 * @return the time
	 */
	public String getTime() {
		return time;
	}

	/**
	 * @param time
	 *            the time to set
	 */
	public void setTime(String time) {
		this.time = time;
	}






	public String getPickupAddress() {
		return pickupaddress;
	}

	public void setPickupAddress(String pickupaddress) {
		this.pickupaddress = pickupaddress;
	}

}
