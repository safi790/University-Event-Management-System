package project.uevents.management.Models;

import java.io.Serializable;
import java.util.Map;

public class Event implements Serializable {
    private String eventId, title, desc, studentId, imageUrl, eventDate, eventDuration, eventStartTime;
    private Map<String, Boolean> participatedStudentsList;

    public Event() {
    }

    public Event(String eventId, String title, String desc, String studentId, String imageUrl, String eventDate, String eventDuration, String eventStartTime) {
        this.eventId = eventId;
        this.title = title;
        this.desc = desc;
        this.studentId = studentId;
        this.imageUrl = imageUrl;
        this.eventDate = eventDate;
        this.eventDuration = eventDuration;
        this.eventStartTime = eventStartTime;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventDuration() {
        return eventDuration;
    }

    public void setEventDuration(String eventDuration) {
        this.eventDuration = eventDuration;
    }

    public String getEventStartTime() {
        return eventStartTime;
    }

    public void setEventStartTime(String eventStartTime) {
        this.eventStartTime = eventStartTime;
    }

    public Map<String, Boolean> getParticipatedStudentsList() {
        return participatedStudentsList;
    }

    public void setParticipatedStudentsList(Map<String, Boolean> participatedStudentsList) {
        this.participatedStudentsList = participatedStudentsList;
    }
}
