package com.example.pastilasalvatoare;

public class CalendarProvider {
    public long calendarID;
    public String displayName;
    public String accountName;
    public String ownerName;


    public CalendarProvider(long calendarID, String displayName, String accountName, String ownerName) {
        this.calendarID = calendarID;
        this.displayName = displayName;
        this.accountName = accountName;
        this.ownerName = ownerName;
    }


    public long getCalendarID() {
        return calendarID;
    }


    public String getAccountName() {
        return accountName;
    }


    public String getDisplayName() {
        return displayName;
    }


    public String getOwnerName() {
        return ownerName;
    }
}
