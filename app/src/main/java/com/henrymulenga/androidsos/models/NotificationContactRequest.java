package com.henrymulenga.androidsos.models;

import java.sql.Timestamp;

public class NotificationContactRequest {
    private String attemptId;
    private Timestamp timestamp;
    public String time;
    private String attemptType;

    public NotificationContactRequest() {
    }

    public String getAttemptId() {
        return attemptId;
    }

    public void setAttemptId(String attemptId) {
        this.attemptId = attemptId;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAttemptType() {
        return attemptType;
    }

    public void setAttemptType(String attemptType) {
        this.attemptType = attemptType;
    }

    @Override
    public String toString() {
        return "NotificationContactRequest{" +
                "attemptId='" + attemptId + '\'' +
                ", timestamp=" + timestamp +
                ", time='" + time + '\'' +
                ", attemptType='" + attemptType + '\'' +
                '}';
    }
}
