// src/main/java/com/example/testaqua/model/Aquarium.java
package com.example.testaqua.model;

public class Aquarium {
    private long id;
    private String name;
    private String owner;

    public Aquarium() {}

    public Aquarium(long id, String name, String owner) {
        this.id = id;
        this.name = name;
        this.owner = owner;
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }
    public void setOwner(String owner) {
        this.owner = owner;
    }
}
