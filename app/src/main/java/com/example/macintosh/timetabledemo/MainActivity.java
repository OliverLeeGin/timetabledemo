package com.example.macintosh.timetabledemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.macintosh.timetabledemo.models.Event;
import com.example.macintosh.timetabledemo.models.Stage;
import com.example.macintosh.timetabledemo.utils.ZoomLayout;
import com.example.macintosh.timetabledemo.views.TimetableContainer;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity  {

    private ZoomLayout mRlTimetableContainer;
    private List<Stage> mEvents = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getListEvent();
        mRlTimetableContainer = findViewById(R.id.rlTimetableContainer);
        mRlTimetableContainer.post(new Runnable() {
            @Override
            public void run() {
                TimetableContainer timetableContainer = new TimetableContainer(getApplicationContext(), mEvents, mRlTimetableContainer.getWidth(),
                        mRlTimetableContainer.getHeight());
                mRlTimetableContainer.addView(timetableContainer);
            }
        });
    }

    private void getListEvent() {
        List<Event> events = new ArrayList<>();
        events.add(new Event(1, "A", "01:00", "02:20"));
        List<Event> events1 = new ArrayList<>();
        events1.add(new Event(2, "b", "04:30", "06:00"));
        List<Event> events2 = new ArrayList<>();
        events2.add(new Event(3, "C", "01:00", "02:00"));
        List<Event> events3 = new ArrayList<>();
        events3.add(new Event(4, "D", "07:00", "08:00"));
        List<Event> events4 = new ArrayList<>();
        events4.add(new Event(5, "E", "22:00", "23:30"));
        List<Event> events5 = new ArrayList<>();
        events5.add(new Event(6, "F", "02:00", "03:30"));
        List<Event> events6 = new ArrayList<>();
        events6.add(new Event(7, "G", "10:00", "12:30"));
        List<Event> events7 = new ArrayList<>();
        events7.add(new Event(8, "H", "22:00", "22:30"));
        List<Event> events8 = new ArrayList<>();
        events8.add(new Event(9, "J", "09:00", "03:30"));
        List<Event> events9 = new ArrayList<>();
        mEvents.add(new Stage("RockA", 1, events));
        mEvents.add(new Stage("Rock Stages", 2, events1));
        mEvents.add(new Stage("RockC", 3, events2));
        mEvents.add(new Stage("RockD", 4, events3));
        mEvents.add(new Stage("RockE", 5, events4));
        mEvents.add(new Stage("RockF", 6, events5));
        mEvents.add(new Stage("RockI", 7, events6));
        mEvents.add(new Stage("RockH", 8, events7));
        mEvents.add(new Stage("Rockm", 9, events8));
        mEvents.add(new Stage("RockK", 10, events9));
    }
}
