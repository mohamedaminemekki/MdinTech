package mdinteech.entities;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Trip {
    private int tripId;
    private int transportId;
    private Timestamp departureTime;
    private Timestamp arrivalTime;
    private double price;
    private String departure;
    private String destination;
    private String transportName;

    // Constructeur
    public Trip(int tripId, int transportId, Timestamp departureTime, Timestamp arrivalTime, double price, String departure, String destination, String transportName) {
        this.tripId = tripId;
        this.transportId = transportId;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.price = price;
        this.departure = departure;
        this.destination = destination;
        this.transportName = transportName;
    }

    // Getters et Setters
    public int getTripId() {
        return tripId;
    }

    public void setTripId(int tripId) {
        this.tripId = tripId;
    }

    public int getTransportId() {
        return transportId;
    }

    public void setTransportId(int transportId) {
        this.transportId = transportId;
    }

    public Timestamp getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(Timestamp departureTime) {
        this.departureTime = departureTime;
    }

    public Timestamp getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(Timestamp arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDeparture() {return departure;}

    public void setDeparture(String departure) {
        this.departure = departure;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getTransportName() {
        return transportName;
    }

    public void setTransportName(String transportName) {
        this.transportName = transportName;
    }

    // Méthode pour extraire la date au format jj/MM/aaaa
    public LocalDate getDate() {
        if (departureTime != null) {
            return departureTime.toLocalDateTime().toLocalDate();
        }
        return null;
    }

    // Méthode pour formater la date en jj/MM/aaaa
    public String getFormattedDate() {
        LocalDate date = getDate();
        if (date != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            return date.format(formatter);
        }
        return "Date non disponible";
    }

    @Override
    public String toString() {
        return "Trip{" +
                "tripId=" + tripId +
                ", transportId=" + transportId +
                ", departureTime=" + departureTime +
                ", arrivalTime=" + arrivalTime +
                ", price=" + price +
                ", departure='" + departure + '\'' +
                ", destination='" + destination + '\'' +
                ", transportName='" + transportName + '\'' +
                ", date=" + getFormattedDate() +
                '}';
    }
}