package com.example.univents;

public class RegistrationModel {

    private String regId;
    private String userId;
    private String eventId;
    private String userName;
    private String userEmail;
    private String status;
    private String role;
    private String division;

    // Default constructor diperlukan Firestore
    public RegistrationModel() {}

    // ----- GETTERS -----
    public String getRegId() { return regId; }
    public String getUserId() { return userId; }
    public String getEventId() { return eventId; }
    public String getUserName() { return userName; }
    public String getUserEmail() { return userEmail; }
    public String getStatus() { return status; }
    public String getRole() { return role; }
    public String getDivision() { return division; }

    // ----- SETTERS -----
    public void setRegId(String regId) {
        this.regId = regId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void setDivision(String division) {
        this.division = division;
    }
}
