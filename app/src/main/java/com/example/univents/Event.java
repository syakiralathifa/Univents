package com.example.univents;

public class Event {

    private String eventId;
    private String title;
    private String description;
    private String date;
    private String time;
    private String location;

    private boolean needsCommittee;
    private int participantsCount;

    public Event() {}

    // GETTERS
    public String getEventId() { return eventId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getLocation() { return location; }
    public boolean getNeedsCommittee() { return needsCommittee; }
    public int getParticipantsCount() { return participantsCount; }

    // SETTERS
    public void setEventId(String eventId) { this.eventId = eventId; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setDate(String date) { this.date = date; }
    public void setTime(String time) { this.time = time; }
    public void setLocation(String location) { this.location = location; }
    public void setNeedsCommittee(boolean needsCommittee) { this.needsCommittee = needsCommittee; }
    public void setParticipantsCount(int participantsCount) { this.participantsCount = participantsCount; }
}
