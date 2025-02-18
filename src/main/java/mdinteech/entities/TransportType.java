package mdinteech.entities;

public class TransportType {
    private int transportTypeId;
    private String name;
    private int capacity;

    public TransportType(int transportTypeId, String name, int capacity) {
        this.transportTypeId = transportTypeId;
        this.name = name;
        this.capacity = capacity;
    }

    public int getTransportTypeId() {
        return transportTypeId;
    }

    public void setTransportTypeId(int transportTypeId) {
        this.transportTypeId = transportTypeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    @Override
    public String toString() {
        return "TransportType [transportTypeId=" + transportTypeId + ", name=" + name + ", capacity=" + capacity + "]";
    }
}
