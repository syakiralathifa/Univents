package com.example.univents;

public class ExperienceModel {
    public String eventId;
    public String title;
    public String date;

    public ExperienceModel() {}

    public ExperienceModel(String eventId, String title, String date) {
        this.eventId = eventId;
        this.title = title;
        this.date = date;
    }
}