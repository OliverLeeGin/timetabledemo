package com.example.macintosh.timetabledemo.models;

import android.graphics.Rect;
import android.graphics.RectF;

/**
 * Copyright © Nals
 * Created by macintosh on 4/17/18.
 */

public class EventRect {
    private RectF rect;
    private Event event;

    public EventRect(Event event) {
        this.event = event;
    }

    public EventRect(RectF rect, Event event) {
        this.rect = rect;
        this.event = event;
    }

    public RectF getRect() {
        return rect;
    }

    public void setRect(RectF rect) {
        this.rect = rect;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }
}
