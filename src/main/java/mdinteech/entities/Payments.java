package mdinteech.entities;

import java.sql.Timestamp;

public class Payments {
    private int paymentId;
    private double amount;
    private String method;
    private Timestamp paymentDate;  // Changer Date en Timestamp
    private int reservationId;
    private int userId;

    // Constructeur modifié pour accepter Timestamp
    public Payments(int paymentId, double amount, String method, Timestamp paymentDate, int reservationId, int userId) {
        this.paymentId = paymentId;
        this.amount = amount;
        this.method = method;
        this.paymentDate = paymentDate;  // Utilisation de Timestamp directement
        this.reservationId = reservationId;
        this.userId = userId;
    }

    // Getters et setters pour chaque attribut
    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Timestamp getPaymentDate() {  // Retourne un Timestamp
        return paymentDate;
    }

    public void setPaymentDate(Timestamp paymentDate) {
        this.paymentDate = paymentDate;
    }

    public int getReservationId() {
        return reservationId;
    }

    public void setReservationId(int reservationId) {
        this.reservationId = reservationId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Payments [paymentId=" + paymentId + ", amount=" + amount + ", method=" + method + ", paymentDate="
                + paymentDate + ", reservationId=" + reservationId + ", userId=" + userId + "]";
    }
}
