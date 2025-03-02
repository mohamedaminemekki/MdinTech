package org.example.mdintech.entities.ParkingModule;

import java.util.Date;

public class ParkingTicket {
    private int id;
    private int userID;
    private int parkingID;
    private int parkingSlotID;
    private Date issuingDate;
    private Date expirationDate;
    private boolean status;

    public ParkingTicket(int userID, int parkingID, int parkingSlotID, Date issuingDate, Date expirationDate,boolean status) {
        this.userID = userID;
        this.parkingID = parkingID;
        this.parkingSlotID = parkingSlotID;
        this.issuingDate = issuingDate;
        this.status = status;
        this.expirationDate = expirationDate;
    }

    public ParkingTicket(int id,int userID, int parkingID, int parkingSlotID, Date issuingDate, Date expirationDate,boolean status) {
        this.id = id;
        this.userID = userID;
        this.parkingID = parkingID;
        this.parkingSlotID = parkingSlotID;
        this.issuingDate = issuingDate;
        this.status = status;
        this.expirationDate = expirationDate;
    }

    public ParkingTicket() {}

    public int getParkingSlotID() {
        return parkingSlotID;
    }

    public void setParkingSlotID(int parkingSlotID) {
        this.parkingSlotID = parkingSlotID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getParkingID() {
        return parkingID;
    }

    public void setParkingID(int parkingID) {
        this.parkingID = parkingID;
    }

    public Date getIssuingDate() {
        return issuingDate;
    }

    public void setIssuingDate(Date issuingDate) {
        this.issuingDate = issuingDate;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
