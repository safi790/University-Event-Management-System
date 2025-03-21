package project.uevents.management.Models;

import java.io.Serializable;

public class Department implements Serializable {
    private String id, name;

    public Department(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public Department() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
