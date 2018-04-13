package com.example.macintosh.timetabledemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.OverScroller;
import android.widget.Scroller;

import com.example.macintosh.timetabledemo.models.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by macintosh on 4/11/18.
 */

public class TimeTableView extends View {
    private int mHeightTimeRuler;
    private int mHeightOfEachTimeStone;
    private int mWidthTimeRuler;
    private static final int TOTAL_DISTANCE_TIMER = 12;
    private static final int TOTAL_DISTANCE_EACH_NORNAL_TIME_STONE = 6;
    private static final int TOTAL_DISTANCE_EACH_LARGE_TIME_STONE = 2;
    private int mNormalDistance;
    private int mLargeDistance;
    private float mTextTimeRuler = 0;

    //scroll
    private GestureDetectorCompat mGestureDetector;
    private OverScroller mScroller;
    private Scroller mStickyScroller;
    private Direction mCurrentScrollDirection = Direction.NONE;
    private Direction mCurrentFlingDirection = Direction.NONE;
    private float mDistanceY = 0;
    private float mDistanceX = 0;
    private float mXScrollingSpeed = 1f;
    private PointF mCurrentOrigin = new PointF(0f, 0f);
    private int mWidthEachEvent;
    private int mWidthTimetableContainer;

    //draw time ruler
    private Paint mPaintTimeRulerBackground = new Paint();
    private List<Stage> mEvents = new ArrayList<>();

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
                    mCurrentScrollDirection = Direction.HORIZONTAL;
                    mCurrentFlingDirection = Direction.HORIZONTAL;
                } else {
                    mCurrentFlingDirection = Direction.VERTICAL;
                    mCurrentScrollDirection = Direction.VERTICAL;
                }
            }
            mDistanceX = distanceX * mXScrollingSpeed;
            mDistanceY = distanceY;
            invalidate();
            return true;
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            Log.d("TAG", "onTouchEvent: ");
//            if (mCurrentScrollDirection == Direction.HORIZONTAL) {
//                float leftDays = Math.round(mCurrentOrigin.x / (mWidthPerDay + mColumnGap));
//                int nearestOrigin = (int) (mCurrentOrigin.x - leftDays * (mWidthPerDay + mColumnGap));
//                mStickyScroller.startScroll((int) mCurrentOrigin.x, 0, -nearestOrigin, 0);
//                ViewCompat.postInvalidateOnAnimation(WeekDayView.this);
//            }
//            int leftDays = Math.round(mStickyScroller.getFinalX() / (mWidthPerDay + mColumnGap));
//            mSelectedDate = (Calendar) mToday.clone();
//            mSelectedDate.add(Calendar.DATE, -leftDays);
//            if (mSelectedDate.get(Calendar.DAY_OF_YEAR) != mLastSelectedDate.get(Calendar.DAY_OF_YEAR)) {
//                mScrollListener.onSelectedDaeChange(mSelectedDate);
//            }
            mCurrentScrollDirection = Direction.NONE;
        }
        return mGestureDetector.onTouchEvent(event);
    }

    public TimeTableView(Context context, int heightTimeRuler, int widthTimeContainer, List<Stage> events) {
        super(context);
        mWidthTimetableContainer = widthTimeContainer;
        mHeightTimeRuler = heightTimeRuler;
        mWidthTimeRuler = widthTimeContainer / 5;
        mHeightOfEachTimeStone = mHeightTimeRuler / TOTAL_DISTANCE_TIMER;
        mNormalDistance = mHeightOfEachTimeStone / TOTAL_DISTANCE_EACH_NORNAL_TIME_STONE;
        mLargeDistance = mHeightOfEachTimeStone / TOTAL_DISTANCE_EACH_LARGE_TIME_STONE;
        mTextTimeRuler = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                12,
                context.getResources().getDisplayMetrics());
        initScroll(context);
        mPaintTimeRulerBackground.setColor(getResources().getColor(R.color.bg));
        mWidthEachEvent = (widthTimeContainer - (widthTimeContainer / 5)) / 5;
        mEvents = events;
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
        drawBackground(canvas, mEvents);
        drawTime(canvas, mEvents);
    }

    private void drawEvent(Canvas canvas, float top, int i) {
        int dx = mWidthTimeRuler;
        Paint paintTextCounter = new Paint();
        paintTextCounter.setColor(Color.GREEN);
        for (int j = 0; j < mEvents.size(); j++) {
            if (mEvents.get(j).getEvents().size() != 0) {
                for (int k = 0; k < mEvents.get(j).getEvents().size(); k++) {
                    String timeHourStart = mEvents.get(j).getEvents().get(k).getTimeStart().substring(0, 2);
                    String timeMinStart = mEvents.get(j).getEvents().get(k).getTimeStart().substring(3, 5);
                    String timeHourEnd = mEvents.get(j).getEvents().get(k).getTimeEnd().substring(0, 2);
                    String timeMinEnd = mEvents.get(j).getEvents().get(k).getTimeEnd().substring(3, 5);
                    if (Integer.parseInt(timeHourStart) == i) {
                        float dy = top + ((Integer.parseInt(timeMinStart) * mNormalDistance) / 10);
                        float lastDy = top + mHeightOfEachTimeStone * (Integer.parseInt(timeHourEnd) - Integer.parseInt(timeHourStart))
                                + ((Integer.parseInt(timeMinEnd) * mNormalDistance) / 10);
                        canvas.drawRect(dx + mWidthEachEvent * (mEvents.get(j).getKey() - 1), dy,
                                dx + mWidthEachEvent * mEvents.get(j).getKey(), lastDy,
                                paintTextCounter);
                    }
                }
            }
        }
    }

    private void drawBackground(Canvas canvas, List<Stage> mEvents) {
        mPaintTimeRulerBackground.setColor(getResources().getColor(R.color.bg));
        Paint paintTextCounter = new Paint();
        paintTextCounter.setColor(Color.WHITE);
        paintTextCounter.setTextSize(mTextTimeRuler);
        paintTextCounter.setTextAlign(Paint.Align.CENTER);
        paintTextCounter.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        int dx = mWidthTimeRuler;
        Paint paintEvenBg = new Paint();
        paintEvenBg.setColor(getResources().getColor(R.color.bg));
        Paint paintOddBg = new Paint();
        paintOddBg.setColor(Color.GRAY);
        for (int i = 0; i < mEvents.size(); i++) {
            if (i % 2 == 0) {
                canvas.drawRect(dx, 0, dx + mWidthEachEvent, mHeightTimeRuler, paintEvenBg);
            } else {
                canvas.drawRect(dx, 0, dx + mWidthEachEvent, mHeightTimeRuler, paintOddBg);
            }
            dx = dx + mWidthEachEvent;
        }
    }

    private void drawTime(Canvas canvas, List<Stage> mEvents) {
        if (mCurrentScrollDirection == Direction.VERTICAL) {
            if (mCurrentOrigin.y - mDistanceY > 0) {
                mCurrentOrigin.y = 0;
            } else if (mCurrentOrigin.y - mDistanceY < -(mHeightOfEachTimeStone * 24 - getHeight())) {
                mCurrentOrigin.y = -(mHeightOfEachTimeStone * 24 - getHeight());
            } else {
                mCurrentOrigin.y -= mDistanceY;
            }
        }

        // Draw the background color for the header column.
        canvas.drawRect(0, 0, mWidthTimeRuler, getHeight(), mPaintTimeRulerBackground);
        Paint paintLargeTimeText = new Paint();
        paintLargeTimeText.setColor(Color.WHITE);
        paintLargeTimeText.setStrokeWidth(3);
        Paint paintLargeTimeTextNoStroke = new Paint();
        paintLargeTimeTextNoStroke.setColor(Color.WHITE);
        paintLargeTimeTextNoStroke.setStrokeWidth(1);
        paintLargeTimeTextNoStroke.setStyle(Paint.Style.STROKE);
        Paint paintTextCounter = new Paint();
        paintTextCounter.setColor(Color.GREEN);
        for (int i = 0; i < 24; i++) {
            float top = mCurrentOrigin.y + mHeightOfEachTimeStone * i;
            if (top < getHeight()) {
                drawEvent(canvas, top, i);
                drawTextTime((int) top, canvas, i);
                canvas.drawLine(mWidthTimeRuler, (int) top, mWidthTimetableContainer, (int) top, paintLargeTimeTextNoStroke);
                canvas.drawLine(mWidthTimeRuler, (int) top + mHeightOfEachTimeStone / 2, mWidthTimetableContainer, (int) top + mHeightOfEachTimeStone / 2, paintLargeTimeTextNoStroke);
                Log.d("TAG", "drawTime: " + mWidthTimeRuler);
                Log.d("TAG", "drawTime: " + mWidthTimetableContainer);

                canvas.drawLine(mWidthTimeRuler, (int) top, mWidthTimeRuler - 20, (int) top, paintLargeTimeText);
                canvas.drawLine(mWidthTimeRuler, (int) top + mNormalDistance, mWidthTimeRuler - 10, (int) top + mNormalDistance, paintLargeTimeTextNoStroke);
                canvas.drawLine(mWidthTimeRuler, (int) top + mNormalDistance * 2, mWidthTimeRuler - 10, (int) top + mNormalDistance * 2, paintLargeTimeTextNoStroke);
                canvas.drawLine(mWidthTimeRuler, (int) top + mHeightOfEachTimeStone / 2, mWidthTimeRuler - 20,
                        (int) top + mHeightOfEachTimeStone / 2, paintLargeTimeTextNoStroke);
                canvas.drawLine(mWidthTimeRuler, (int) top + mNormalDistance * 4, mWidthTimeRuler - 10, (int) top + mNormalDistance * 4, paintLargeTimeTextNoStroke);
                canvas.drawLine(mWidthTimeRuler, (int) top + mNormalDistance * 5, mWidthTimeRuler - 10, (int) top + mNormalDistance * 5, paintLargeTimeTextNoStroke);
            }
        }
    }

    private void drawTextTime(int dy, Canvas canvas, int time) {
        Paint paintTextCounter = new Paint();
        paintTextCounter.setColor(Color.WHITE);
        paintTextCounter.setTextSize(mTextTimeRuler);
        paintTextCounter.setTextAlign(Paint.Align.CENTER);
        paintTextCounter.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        Rect bounds = new Rect();
        String value = checkTime(time);
        paintTextCounter.getTextBounds(value, 0, value.length(), bounds);
        int height = bounds.height();
        int width = bounds.width();
        canvas.drawText(value, mWidthTimeRuler / 2 - (width / 4) + (mTextTimeRuler / 2),
                dy - (height / 4) + (mTextTimeRuler / 2), paintTextCounter);
    }

    private String checkTime(int time) {
        String text = ":00";
        return String.format(Locale.getDefault(), "%02d%s", time, text);
    }

    private enum Direction {
        NONE, HORIZONTAL, VERTICAL
    }
}
