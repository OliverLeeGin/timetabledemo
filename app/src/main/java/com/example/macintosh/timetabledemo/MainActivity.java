package com.example.macintosh.timetabledemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.RelativeLayout;

import com.example.macintosh.timetabledemo.models.Event;
import com.example.macintosh.timetabledemo.models.Stage;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout mRlTimeTableContainer;
    private RelativeLayout mRlTimeTableHeaderContainer;

    private List<Stage> mEvents = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRlTimeTableContainer = findViewById(R.id.rlTimeRulerContainer);
        mRlTimeTableHeaderContainer = findViewById(R.id.rlHearderTimeTableContainer);
        initViews();
    }

    private void initViews() {
        getListEvent();
        getWidthAndHeightOfTimeRuler();
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
        events4.add(new Event(5, "A", "22:00", "23:30"));
        mEvents.add(new Stage("RockA", 1, events));
        mEvents.add(new Stage("RockB", 2, events1));
        mEvents.add(new Stage("RockC", 3, events2));
        mEvents.add(new Stage("RockD", 4, events3));
        mEvents.add(new Stage("RockE", 5, events4));
        // mEvents.add(new Stage("RockF"));
    }

    private void setHeightTimetableHeaderContainer(final int height, final int width) {
        mRlTimeTableHeaderContainer.post(new Runnable() {
            @Override
            public void run() {
                RelativeLayout.LayoutParams mParams;
                mParams = (RelativeLayout.LayoutParams) mRlTimeTableHeaderContainer.getLayoutParams();
                mParams.height = height / 12;
                mRlTimeTableHeaderContainer.setLayoutParams(mParams);
                mRlTimeTableHeaderContainer.postInvalidate();
                TimeTableHeaderView timeTableHeaderView = new TimeTableHeaderView(getApplicationContext(),
                        mParams.height, mRlTimeTableHeaderContainer.getWidth(), mEvents);
                mRlTimeTableHeaderContainer.addView(timeTableHeaderView);
                TimeTableView timeRulerView = new TimeTableView(getApplicationContext()
                        , height, width, mEvents);
                mRlTimeTableContainer.addView(timeRulerView);
            }
        });
    }

    private void getWidthAndHeightOfTimeRuler() {
        mRlTimeTableContainer.post(new Runnable() {
            @Override
            public void run() {
                setHeightTimetableHeaderContainer(mRlTimeTableContainer.getHeight(), mRlTimeTableContainer.getWidth());
            }
        });
    }
}
