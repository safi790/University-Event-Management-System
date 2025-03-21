package project.uevents.management.Models;

import java.io.Serializable;

public class Notification implements Serializable {
    private String title, desc, time;

    public Notification(String title, String desc, String time) {
        this.title = title;
        this.desc = desc;
        this.time = time;
    }

    public Notification() {
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
