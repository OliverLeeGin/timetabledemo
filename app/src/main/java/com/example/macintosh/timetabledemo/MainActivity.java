package com.example.macintosh.timetabledemo;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.macintosh.timetabledemo.models.Event;
import com.example.macintosh.timetabledemo.models.Stage;
import com.example.macintosh.timetabledemo.utils.ZoomLayout;
import com.example.macintosh.timetabledemo.views.EventView;
import com.example.macintosh.timetabledemo.views.OlockView;
import com.example.macintosh.timetabledemo.views.StagesView;
import com.example.macintosh.timetabledemo.views.TimeRulerView;
import com.example.macintosh.timetabledemo.views.TimetableContainer;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements EventView.IScrollListener {

    private ZoomLayout mRlTimetableContainer;
    private RelativeLayout mRlOclockContainer;
    private RelativeLayout mRlTimeRulerContainer;
    private RelativeLayout mRlStagesContainer;
    private ZoomLayout mRlEventsContainer;
    private TimeRulerView mTimeRulerView;
    private StagesView mStagesView;

    private List<Stage> mEvents = new ArrayList<>();

    private static final int TOTAL_RATIO_WIDTH_SCREEN_ELEMENT = 5;
    private static final int TOTAL_RATIO_HEIGHT_SCREEN_ELEMENT = 12;

    ScaleGestureDetector scaleGestureDetector;

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
//        mRlOclockContainer = findViewById(R.id.rlOclockContainer);
//        mRlTimeRulerContainer = findViewById(R.id.rlTimeRulerContainer);
//        mRlStagesContainer = findViewById(R.id.rlStagesContainer);
//        mRlEventsContainer = findViewById(R.id.rlEventsContainer);
           //initViews();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initViews() {
        getListEvent();
        getWidthAndHeightOfTimetableContainer();
      //  setListener();
    }

    private void setListener() {
        scaleGestureDetector = new ScaleGestureDetector(this, new OnPinchListener());
        mRlEventsContainer.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method
                if (event.getAction() == MotionEvent.ACTION_POINTER_DOWN) {
                    scaleGestureDetector.onTouchEvent(event);
                }
                return true;
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
        events4.add(new Event(5, "A", "22:00", "23:30"));
        mEvents.add(new Stage("RockA", 1, events));
        mEvents.add(new Stage("RockB", 2, events1));
        mEvents.add(new Stage("RockC", 3, events2));
        mEvents.add(new Stage("RockD", 4, events3));
        mEvents.add(new Stage("RockE", 5, events4));
        mEvents.add(new Stage("RockE", 5, events4));
        mEvents.add(new Stage("RockE", 5, events4));
        mEvents.add(new Stage("RockE", 5, events4));
        //
        // "== mEvents.add(new Stage("RockF"));
    }


    private void getWidthAndHeightOfTimetableContainer() {
        mRlTimetableContainer.post(new Runnable() {
            @Override
            public void run() {
                int width = mRlTimetableContainer.getWidth();
                int height = mRlTimetableContainer.getHeight();
                setLayoutParamsOlockContainer(width / TOTAL_RATIO_WIDTH_SCREEN_ELEMENT,
                        height / TOTAL_RATIO_HEIGHT_SCREEN_ELEMENT);
                setLayoutParamsTimeRulerContainer(width / TOTAL_RATIO_WIDTH_SCREEN_ELEMENT);
                setLayoutParamsStagesContainer(width, height / TOTAL_RATIO_HEIGHT_SCREEN_ELEMENT);
            }
        });
    }

    private void setLayoutParamsOlockContainer(int width, int height) {
        RelativeLayout.LayoutParams mParams;
        mParams = (RelativeLayout.LayoutParams) mRlOclockContainer.getLayoutParams();
        mParams.width = width;
        mParams.height = height;
        mRlOclockContainer.setLayoutParams(mParams);
        mRlOclockContainer.postInvalidate();
        drawOclock(width, height);
    }

    private void drawOclock(int width, int height) {
        mRlOclockContainer.addView(new OlockView(getApplicationContext(), width, height));
    }

    private void setLayoutParamsStagesContainer(final int width, final int height) {
        mRlStagesContainer.post(new Runnable() {
            @Override
            public void run() {
                RelativeLayout.LayoutParams mParams;
                mParams = (RelativeLayout.LayoutParams) mRlStagesContainer.getLayoutParams();
                mParams.height = height;
                mParams.width = width - (width / 5);
                mRlStagesContainer.setLayoutParams(mParams);
                mRlStagesContainer.postInvalidate();
                Log.d("TAG", "run: " + mParams.width);
                drawStages(height, mParams.width);
            }
        });

    }

    private void drawStages(int height, int width) {
        mStagesView = new StagesView(getApplicationContext(), mEvents, height, width);
        mRlStagesContainer.addView(mStagesView);
        setLayoutParamsEventContainer();
    }

    private void setLayoutParamsTimeRulerContainer(final int width) {
        mRlTimeRulerContainer.post(new Runnable() {
            @Override
            public void run() {
                RelativeLayout.LayoutParams mParams;
                mParams = (RelativeLayout.LayoutParams) mRlTimeRulerContainer.getLayoutParams();
                mParams.width = width;
                mRlTimeRulerContainer.setLayoutParams(mParams);
                mRlTimeRulerContainer.postInvalidate();
                drawTimeRuler(width, mRlTimeRulerContainer.getHeight());
            }
        });
    }

    private void drawTimeRuler(int width, int height) {
        mTimeRulerView = new TimeRulerView(getApplicationContext(), width, height);
        mRlTimeRulerContainer.addView(mTimeRulerView);
    }

    private void setLayoutParamsEventContainer() {
        mRlEventsContainer.post(new Runnable() {
            @Override
            public void run() {
                drawEventContainer(mRlEventsContainer.getWidth(), mRlEventsContainer.getHeight());
            }
        });
    }

    private void drawEventContainer(int width, int height) {
        mRlEventsContainer.addView(new EventView(getApplicationContext(), mEvents, width,
                height, this));
    }

    @Override
    public void scrollHorizontal(float currentX) {
        mStagesView.invalidate(currentX);
    }

    @Override
    public void scrollVertical(float currentY) {
        mTimeRulerView.invalidate(currentY);
    }

    private class OnPinchListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        float startingSpan;
        float endSpan;
        float startFocusX;
        float startFocusY;


        public boolean onScaleBegin(ScaleGestureDetector detector) {
            startingSpan = detector.getCurrentSpan();
            startFocusX = detector.getFocusX();
            startFocusY = detector.getFocusY();
            return true;
        }

        public boolean onScale(ScaleGestureDetector detector) {
            mRlEventsContainer.scale(detector.getCurrentSpan() / startingSpan, startFocusX, startFocusY);
            return true;
        }

        public void onScaleEnd(ScaleGestureDetector detector) {
            mRlEventsContainer.release();
        }
    }
}
