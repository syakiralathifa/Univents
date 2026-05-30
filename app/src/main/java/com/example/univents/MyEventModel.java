package com.example.univents;

public class MyEventModel {
    public String eventId;
    public String title;
    public String date;
    public String status;

    public MyEventModel() {}

    public MyEventModel(String eventId, String title, String date, String status) {
        this.eventId = eventId;
        this.title = title;
        this.date = date;
        this.status = status;
    }
}
