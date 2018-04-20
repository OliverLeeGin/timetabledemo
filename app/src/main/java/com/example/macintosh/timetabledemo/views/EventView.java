package com.example.macintosh.timetabledemo.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.OverScroller;
import android.widget.Scroller;
import android.widget.Toast;

import com.example.macintosh.timetabledemo.R;
import com.example.macintosh.timetabledemo.models.EventRect;
import com.example.macintosh.timetabledemo.models.Stage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by macintosh on 4/16/18.
 */

public class EventView extends View {

    private List<Stage> mStages = new ArrayList<>();
    private PointF mCurrentOrigin = new PointF(0, 0);
    private int mWidthEventContainer;
    private int mWidthEachEvent;
    private int mHeightEachEvent;
    private int mNormalDistance;
    private int mLargeDistance;
    private int mHeightEventContainer;

    private static final int TOTAL_DISTANCE_EACH_NORNAL_TIME_STONE = 6;
    private static final int TOTAL_DISTANCE_EACH_LARGE_TIME_STONE = 2;

    //scroll
    private GestureDetectorCompat mGestureDetector;
    private OverScroller mScroller;
    private Scroller mStickyScroller;
    private Direction mCurrentScrollDirection = Direction.NONE;
    private Direction mCurrentFlingDirection = Direction.NONE;
    private float mDistanceY = 0;
    private float mDistanceX = 0;
    private float mXScrollingSpeed = 1f;
    private IScrollListener mScrollListener;
    private boolean mIsScrolled;

    private List<EventRect> mEventRects = new ArrayList<>();

    public EventView(Context context, List<Stage> stages, int width, int height, IScrollListener scrollListener) {
        super(context);
        mWidthEachEvent = width / 4;
        mWidthEventContainer = width;
        mHeightEventContainer = height;
        mHeightEachEvent = height / 11;
        mNormalDistance = mHeightEachEvent / TOTAL_DISTANCE_EACH_NORNAL_TIME_STONE;
        mLargeDistance = mHeightEachEvent / TOTAL_DISTANCE_EACH_LARGE_TIME_STONE;
        mStages = stages;
        initScroll(context);
        mScrollListener = scrollListener;
    }

    private void initScroll(Context context) {
        // Scrolling initialization.
        mGestureDetector = new GestureDetectorCompat(context, mGestureListener);
        mScroller = new OverScroller(context);
        mStickyScroller = new Scroller(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBackground(canvas, mStages);
        drawEvents(canvas);
    }

    private void drawEvents(Canvas canvas) {

        Paint painLineStroke = new Paint();
        painLineStroke.setColor(Color.WHITE);
        painLineStroke.setStrokeWidth(3);

        Paint paintLineNoStroke = new Paint();
        paintLineNoStroke.setColor(Color.WHITE);
        paintLineNoStroke.setStrokeWidth(1);

        if (mCurrentScrollDirection == Direction.VERTICAL) {
            if (mCurrentOrigin.y - mDistanceY > 0) {
                mCurrentOrigin.y = 0;
            } else if (mCurrentOrigin.y - mDistanceY < -(mHeightEachEvent * 24 - (getHeight()))) {
                mCurrentOrigin.y = -(mHeightEachEvent * 24 - (getHeight()));
            } else {
                mCurrentOrigin.y = mCurrentOrigin.y - mDistanceY;
            }
            mScrollListener.scrollVertical(mCurrentOrigin.y);
        }
        if (mCurrentScrollDirection == Direction.HORIZONTAL) {
            if (mCurrentOrigin.x - mDistanceX > 0) {
                mCurrentOrigin.x = 0;
            } else if (mCurrentOrigin.x - mDistanceX < -(mWidthEachEvent * mStages.size() - (getWidth()))) {
                mCurrentOrigin.x = -(mWidthEachEvent * mStages.size() - (getWidth()));
            } else {
                mCurrentOrigin.x = mCurrentOrigin.x - mDistanceX;
            }
            mScrollListener.scrollHorizontal(mCurrentOrigin.x);
        }

        for (int i = 0; i < 24; i++) {
            float top = mCurrentOrigin.y + mHeightEachEvent * i + 10;
            int dx = (int) (mCurrentOrigin.x);

            if (top < getHeight()) {
                drawEvent(canvas, top, i, dx);
                canvas.drawLine(dx, (int) top, mWidthEventContainer, (int) top, paintLineNoStroke);
                canvas.drawLine(dx, (int) top + mHeightEachEvent / 2, mWidthEventContainer, (int) top + mHeightEachEvent / 2, paintLineNoStroke);
            }
        }
    }

    private void drawEvent(Canvas canvas, float top, int i, int dx) {
        Paint paintTextCounter = new Paint();
        paintTextCounter.setColor(Color.GREEN);
        List<EventRect> eventRects = new ArrayList<>();
        eventRects.addAll(mEventRects);
        RectF rectF;
        for (int j = 0; j < mStages.size(); j++) {
            if (mStages.get(j).getEvents().size() != 0) {
                for (int k = 0; k < mStages.get(j).getEvents().size(); k++) {
                    String timeHourStart = mStages.get(j).getEvents().get(k).getTimeStart().substring(0, 2);
                    String timeMinStart = mStages.get(j).getEvents().get(k).getTimeStart().substring(3, 5);
                    String timeHourEnd = mStages.get(j).getEvents().get(k).getTimeEnd().substring(0, 2);
                    String timeMinEnd = mStages.get(j).getEvents().get(k).getTimeEnd().substring(3, 5);
                    if (Integer.parseInt(timeHourStart) == i) {
                        float dy = top + ((Integer.parseInt(timeMinStart) * mNormalDistance) / 10);
                        float lastDy = top + mHeightEachEvent * (Integer.parseInt(timeHourEnd) - Integer.parseInt(timeHourStart))
                                + ((Integer.parseInt(timeMinEnd) * mNormalDistance) / 10);
                        rectF = new RectF();
                        rectF.bottom = (int) lastDy;
                        rectF.left = dx + mWidthEachEvent * (mStages.get(j).getKey() - 1);
                        rectF.right = dx + mWidthEachEvent * mStages.get(j).getKey();
                        rectF.top = (int) dy;
                        if (!mIsScrolled) {
                            mEventRects.add(new EventRect(rectF, mStages.get(j).getEvents().get(k)));
                        } else {
                            for (EventRect eventRect : eventRects) {
                                if (eventRect.getEvent() == mStages.get(j).getEvents().get(k)) {
                                    mEventRects.remove(eventRect);
                                    mEventRects.add(new EventRect(rectF, mStages.get(j).getEvents().get(k)));
                                }
                            }
                        }
                        canvas.drawRect(rectF, paintTextCounter);
//////
//                        canvas.drawRect(dx + mWidthEachEvent * (mStages.get(j).getKey() - 1), dy,
//                                dx + mWidthEachEvent * mStages.get(j).getKey(), lastDy,
//                                paintTextCounter);
                    }
                }
            }
        }
    }

    private void drawBackground(Canvas canvas, List<Stage> stages) {

        Paint paintEvenBg = new Paint();
        paintEvenBg.setColor(getResources().getColor(R.color.bg));

        Paint paintOddBg = new Paint();
        paintOddBg.setColor(Color.GRAY);

        if (mCurrentOrigin.x - mDistanceX > 0) {
            mCurrentOrigin.x = 0;
        } else if (mCurrentOrigin.x - mDistanceX < -(mWidthEachEvent * mStages.size() - (getWidth()))) {
            mCurrentOrigin.x = -(mWidthEachEvent * mStages.size() - (getWidth()));
        } else {
            mCurrentOrigin.x = mCurrentOrigin.x - mDistanceX;
        }

        int dx = (int) mCurrentOrigin.x;

        for (int i = 0; i < stages.size(); i++) {
            if (i % 2 == 0) {
                canvas.drawRect(dx, 0, dx + mWidthEachEvent, mHeightEventContainer, paintEvenBg);
            } else {
                canvas.drawRect(dx, 0, dx + mWidthEachEvent, mHeightEventContainer, paintOddBg);
            }
            dx = dx + mWidthEachEvent;
        }
    }

    private final GestureDetector.SimpleOnGestureListener mGestureListener = new GestureDetector.SimpleOnGestureListener() {

        @Override
        public boolean onDown(MotionEvent e) {
            mScroller.forceFinished(true);
            mStickyScroller.forceFinished(true);
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (mCurrentScrollDirection == Direction.NONE) {
                if (Math.abs(distanceX) > Math.abs(distanceY)) {
                    Log.d("TAG", "onScroll: ");
                    mCurrentScrollDirection = Direction.HORIZONTAL;
                    mCurrentFlingDirection = Direction.HORIZONTAL;
                } else {
                    mCurrentFlingDirection = Direction.VERTICAL;
                    mCurrentScrollDirection = Direction.VERTICAL;
                }
            }
            mDistanceX = distanceX * mXScrollingSpeed;
            mDistanceY = distanceY;
            mIsScrolled = true;
            invalidate();
            return true;
        }

    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            for (EventRect eventRect : mEventRects) {
                if (eventRect.getRect().contains(event.getX(), event.getY())) {
                    Toast.makeText(getContext(), "Name of events is " + eventRect.getEvent().getName(), Toast.LENGTH_SHORT).show();
                }
            }
            Log.d("TAG", "onTouchEvent: " + mEventRects.size());
        }
        return mGestureDetector.onTouchEvent(event);
    }

    private enum Direction {
        NONE, HORIZONTAL, VERTICAL
    }

    public interface IScrollListener {
        void scrollHorizontal(float currentX);

        void scrollVertical(float currentY);
    }

}
