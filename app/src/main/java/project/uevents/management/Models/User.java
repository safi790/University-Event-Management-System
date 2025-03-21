package project.uevents.management.Models;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {
    private String uid, name, email, username, address, phone, dept, sem, batch, fcmToken;
    private boolean isOrganizer = false;

    private ArrayList<Notification> Notifications;

    public User(String name, String email, String username, String address, String phone, String dept, String sem, String batch) {
        this.name = name;
        this.email = email;
        this.username = username;
        this.address = address;
        this.phone = phone;
        this.dept = dept;
        this.sem = sem;
        this.batch = batch;
    }

    public ArrayList<Notification> getNotifications() {
        return Notifications;
    }

    public void setNotifications(ArrayList<Notification> notifications) {
        Notifications = notifications;
    }

    public User() {
    }

    public boolean isOrganizer() {
        return isOrganizer;
    }

    public void setOrganizer(boolean organizer) {
        isOrganizer = organizer;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public String getSem() {
        return sem;
    }

    public void setSem(String sem) {
        this.sem = sem;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
}
