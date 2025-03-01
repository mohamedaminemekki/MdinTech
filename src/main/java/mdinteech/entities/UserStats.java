package mdinteech.entities;

public class UserStats {
    private int totalTrips;
    private double totalDistance;
    private int totalCO2;
    private int transportTypesUsed;
    private int currentStreak;

    public UserStats(int totalTrips, double totalDistance, int totalCO2,
                     int transportTypesUsed, int currentStreak) {
        this.totalTrips = totalTrips;
        this.totalDistance = totalDistance;
        this.totalCO2 = totalCO2;
        this.transportTypesUsed = transportTypesUsed;
        this.currentStreak = currentStreak;
    }

    // Getters
    public int getTotalTrips() { return totalTrips; }
    public double getTotalDistance() { return totalDistance; }
    public int getTotalCO2() { return totalCO2; }
    public int getTransportTypesUsed() { return transportTypesUsed; }
    public int getCurrentStreak() { return currentStreak; }
}