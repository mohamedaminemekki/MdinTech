package org.example.mdintech.entities.ParkingModule;

public class Parking {
    private int ID;
    private String Name;
    private String Localisation;
    private int capacity;


    public Parking(String name, String localisation, int capacity) {
        Name = name;
        Localisation = localisation;
        this.capacity = capacity;
    }

    public Parking(){}

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getLocalisation() {
        return Localisation;
    }

    public void setLocalisation(String localisation) {
        Localisation = localisation;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
}
