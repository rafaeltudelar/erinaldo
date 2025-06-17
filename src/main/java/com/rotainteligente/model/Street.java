package com.rotainteligente.model;

public class Street {
    private String streetName;
    private CongestionLevel congestionLevel;
    private boolean hasAccident;
    private boolean isBlocked;

    public Street(String streetName, CongestionLevel congestionLevel, boolean hasAccident, boolean isBlocked) {
        this.streetName = streetName;
        this.congestionLevel = congestionLevel;
        this.hasAccident = hasAccident;
        this.isBlocked = isBlocked;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public CongestionLevel getCongestionLevel() {
        return congestionLevel;
    }

    public void setCongestionLevel(CongestionLevel congestionLevel) {
        this.congestionLevel = congestionLevel;
    }

    public boolean hasAccident() {
        return hasAccident;
    }

    public void setHasAccident(boolean hasAccident) {
        this.hasAccident = hasAccident;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    @Override
    public String toString() {
        return "Street{" +
               "streetName='" + streetName + '\'' +
               ", congestionLevel=" + congestionLevel +
               ", hasAccident=" + hasAccident +
               ", isBlocked=" + isBlocked +
               '}';
    }
}
