package com.example.univents;

public class Participant {

    private String name;
    private String email;
    private String status;
    private String timestamp;

    public Participant() {}

    public Participant(String name, String email, String status, String timestamp) {
        this.name = name;
        this.email = email;
        this.status = status;
        this.timestamp = timestamp;
    }

    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getStatus() { return status; }
    public String getTimestamp() { return timestamp; }

    public void setName(String s) { name = s; }
    public void setEmail(String s) { email = s; }
    public void setStatus(String s) { status = s; }
    public void setTimestamp(String s) { timestamp = s; }
}
