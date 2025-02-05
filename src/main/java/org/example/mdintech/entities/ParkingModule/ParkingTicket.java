package org.example.mdintech.entities.ParkingModule;

import java.util.Date;

public class ParkingTicket {
    private int id;
    private int userID;
    private int parkingID;
    private Date issuingDate;
    private Date expirationDate;
    private boolean status;

    public ParkingTicket(int userID, int parkingID, Date issuingDate, Date expirationDate, String status) {
        this.userID = userID;
        this.parkingID = parkingID;
        this.issuingDate = issuingDate;
        this.expirationDate = expirationDate;
        this.status = true;
    }

    public ParkingTicket() {}

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
