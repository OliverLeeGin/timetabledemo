package com.example.macintosh.timetabledemo.models;

import java.util.List;

/**
 * Created by macintosh on 4/13/18.
 */

public class Stage {
    private String name;
    private int key;
    private List<Event> events;

    public Stage(String name, int key, List<Event> events) {
        this.name = name;
        this.key = key;
        this.events = events;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "Stage{" +
                "name='" + name + '\'' +
                '}';
    }
}
