package com.example.pastilasalvatoare;

import java.time.LocalDate;

public class CalendarEvent {
    public long id;
    public String startDate;
    public String endDate;
    public String title;
    public String description;
    public String organizer;


    public CalendarEvent(long id, String startDate, String endDate, String title, String description, String organizer) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.title = title;
        this.description = description;
        this.organizer = organizer;
    }

    public long getId() {
        return id;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getOrganizer() {
        return organizer;
    }
}
