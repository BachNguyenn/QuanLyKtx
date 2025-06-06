package model;

public enum FeeType {
    ROOM_FEE("Room Fee"),
    ELECTRICITY("Electricity"),
    WATER("Water"),
    CLEANING("Cleaning"),
    INTERNET("Internet"),
    MAINTENANCE("Maintenance");
    
    private final String displayName;
    
    FeeType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}



