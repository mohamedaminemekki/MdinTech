package mdinteech.entities;

import java.sql.Timestamp;

public class Reservation {
    private int id;
    private int userId;
    private int tripId;
    private int transportId;
    private Timestamp reservationTime;
    private String status;
    private int seatNumber;
    private String seatType;
    private String paymentStatus;

    // Constructeurs
    public Reservation(int id, int userId, int tripId, int transportId, Timestamp reservationTime, String status, int seatNumber, String seatType, String paymentStatus) {
        this.id = id;
        this.userId = userId;
        this.tripId = tripId;
        this.transportId = transportId;
        this.reservationTime = reservationTime;
        this.status = status;
        this.seatNumber = seatNumber;
        this.seatType = seatType;
        this.paymentStatus = paymentStatus;
    }

    public Reservation(int userId, int tripId, int transportId, Timestamp reservationTime, String status, int seatNumber, String seatType, String paymentStatus) {
        this.userId = userId;
        this.tripId = tripId;
        this.transportId = transportId;
        this.reservationTime = reservationTime;
        this.status = status;
        this.seatNumber = seatNumber;
        this.seatType = seatType;
        this.paymentStatus = paymentStatus;
    }

    public Reservation() {}

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getTripId() { return tripId; }
    public void setTripId(int tripId) { this.tripId = tripId; }

    public int getTransportId() { return transportId; }
    public void setTransportId(int transportId) { this.transportId = transportId; }

    public Timestamp getReservationTime() { return reservationTime; }
    public void setReservationTime(Timestamp reservationTime) { this.reservationTime = reservationTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getSeatNumber() { return seatNumber; }
    public void setSeatNumber(int seatNumber) { this.seatNumber = seatNumber; }

    public String getSeatType() { return seatType; }
    public void setSeatType(String seatType) { this.seatType = seatType; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", userId=" + userId +
                ", tripId=" + tripId +
                ", transportId=" + transportId +
                ", reservationTime=" + reservationTime +
                ", status='" + status + '\'' +
                ", seatNumber=" + seatNumber +
                ", seatType='" + seatType + '\'' +
                ", paymentStatus='" + paymentStatus + '\'' +
                '}';
    }

    // Correction des méthodes setReservationId et getReservationId
    public void setReservationId(int reservationId) {
        this.id = reservationId;
    }

    public int getReservationId() {
        return this.id;
    }
}