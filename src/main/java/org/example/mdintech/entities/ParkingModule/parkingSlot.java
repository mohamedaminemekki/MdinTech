package org.example.mdintech.entities.ParkingModule;

public class parkingSlot {

    private int parkingID;
    private int slotID;
    private String slotName;
    private boolean available;

    public parkingSlot(int parkingID, String slotName) {
        this.parkingID = parkingID;
        this.slotName = slotName;
        this.available = true;
    }

    public parkingSlot(){}

    public int getSlotID() {
        return slotID;
    }

    public void setSlotID(int slotID) {
        this.slotID = slotID;
    }

    public int getParkingID() {
        return parkingID;
    }

    public void setParkingID(int parkingID) {
        this.parkingID = parkingID;
    }

    public String getSlotName() {
        return slotName;
    }

    public void setSlotName(String slotName) {
        this.slotName = slotName;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }


    @Override
    public String toString() {
        return "parkingSlot{" +
                "parkingID=" + parkingID +
                ", slotID=" + slotID +
                ", slotName='" + slotName + '\'' +
                ", available=" + available +
                '}';
    }
}
