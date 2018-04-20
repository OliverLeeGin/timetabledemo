package com.example.macintosh.timetabledemo.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Typeface;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.OverScroller;
import android.widget.Scroller;
import android.widget.Toast;

import com.example.macintosh.timetabledemo.R;
import com.example.macintosh.timetabledemo.models.EventRect;
import com.example.macintosh.timetabledemo.models.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Copyright © Nals
 * Created by macintosh on 4/19/18.
 */

public class TimetableContainer extends View {

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
    private EventView.IScrollListener mScrollListener;
    private boolean mIsScrolled;

    private List<EventRect> mEventRects = new ArrayList<>();

    private float mTextTimeRuler = 0;
    private float mTextTitleStage;
    private int mSizeStagesList;

    ///
    private boolean mAreDimensionsInvalid = true;


    public TimetableContainer(Context context, List<Stage> stages, int width, int height) {
        super(context);
        mWidthEachEvent = (width - (width / 5)) / 4;
        mWidthEventContainer = width;
        mHeightEventContainer = height;
        mHeightEachEvent = height / 12;
        mNormalDistance = mHeightEachEvent / TOTAL_DISTANCE_EACH_NORNAL_TIME_STONE;
        mLargeDistance = mHeightEachEvent / TOTAL_DISTANCE_EACH_LARGE_TIME_STONE;
        mStages = stages;
        initScroll(context);
        // mScrollListener = scrollListener;
        mTextTimeRuler = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                12,
                context.getResources().getDisplayMetrics());
        mTextTitleStage = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                15,
                context.getResources().getDisplayMetrics());
        mSizeStagesList = mStages.size();
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
        drawHeaderRowAndEvents(canvas);
        drawTime(canvas);
        drawOlock(canvas);

        //    drawStages(canvas);
//        drawBackground(canvas, mStages);
//        drawEvents(canvas);
    }

    private void drawHeaderRowAndEvents(Canvas canvas) {
        // Calculate the available width for each day.
//        mHeaderColumnWidth = mTimeTextWidth + mHeaderColumnPadding * 2;
//        mWidthPerDay = getWidth() - mHeaderColumnWidth - mColumnGap * (mNumberOfVisibleDays - 1);
//        mWidthPerDay = mWidthPerDay / mNumberOfVisibleDays;

        //  calculateHeaderHeight(); //Make sure the header is the right size (depends on AllDay events)

        // Calendar today = today();

        Paint paintEvenBg = new Paint();
        paintEvenBg.setColor(getResources().getColor(R.color.bg));
        Paint paintOddBg = new Paint();
        paintOddBg.setColor(Color.GRAY);

        if (mAreDimensionsInvalid) {

            mAreDimensionsInvalid = false;
//            if (mScrollToHour >= 0)
//                 goToHour(mScrollToHour);
//
//                mScrollToDay = null;
//            mScrollToHour = -1;
            mAreDimensionsInvalid = false;
        }

        // If the new mCurrentOrigin.y is invalid, make it valid.
        if (mCurrentOrigin.y < getHeight() - mHeightEachEvent * 24 - mHeightEachEvent)
            mCurrentOrigin.y = getHeight() - mHeightEachEvent * 24 - mHeightEachEvent;

        // Don't put an "else if" because it will trigger a glitch when completely zoomed out and
        // scrolling vertically.
        if (mCurrentOrigin.y > 0) {
            mCurrentOrigin.y = 0;
        }
        if (mCurrentOrigin.x > 0) {
            mCurrentOrigin.x = 0;
        }
        if (mCurrentOrigin.x < getWidth() - mWidthEachEvent * mSizeStagesList - mWidthEachEvent) {
            mCurrentOrigin.x = getWidth() - mWidthEachEvent * mSizeStagesList - mWidthEachEvent;
        }

        // Consider scroll offset.
//        int leftDaysWithGaps = (int) -(Math.ceil(mCurrentOrigin.x / (mWidthEachEvent)));
//        Log.d("TAG", "mCurrentOrigin.x: " + mCurrentOrigin.x);
//        float startFromPixel = mCurrentOrigin.x + (mWidthEachEvent) * leftDaysWithGaps +
//                mWidthEachEvent;
//        float startPixel = startFromPixel;
//        Log.d("TAG", "drawHeaderRowAndEvents: " + startFromPixel);

        // Prepare to iterate for each hour to draw the hour lines.
//        int lineCount = (int) ((getHeight() - mHeightEachEvent - mHeaderRowPadding * 2 -
//                mHeaderMarginBottom) / mHourHeight) + 1;
//        lineCount = (lineCount) * (mNumberOfVisibleDays + 1);
//        float[] hourLines = new float[lineCount * 4];

        // Clear the cache for event rectangles.
//        if (mEventRects != null) {
//            for (EventRect eventRect : mEventRects) {
//                eventRect.rectF = null;
//            }
//        }

        // Clip to paint events only.
        // canvas.clipRect(mHeaderColumnWidth, mHeaderHeight + mHeaderRowPadding * 2 + mHeaderMarginBottom + mTimeTextHeight / 2, getWidth(), getHeight(), Region.Op.REPLACE);

        // Iterate through each day.
//        Calendar oldFirstVisibleDay = mFirstVisibleDay;
//        mFirstVisibleDay = (Calendar) today.clone();
//        mFirstVisibleDay.add(Calendar.DATE, -(Math.round(mCurrentOrigin.x / (mWidthPerDay + mColumnGap))));
//        if (!mFirstVisibleDay.equals(oldFirstVisibleDay) && mScrollListener != null) {
//            mScrollListener.onFirstVisibleDayChanged(mFirstVisibleDay, oldFirstVisibleDay);
//        }
//        for (int dayNumber = leftDaysWithGaps + 1;
//             dayNumber <= leftDaysWithGaps + mNumberOfVisibleDays + 1;
//             dayNumber++) {
//
//            // Check if the day is today.
//            day = (Calendar) today.clone();
//            mLastVisibleDay = (Calendar) day.clone();
//            day.add(Calendar.DATE, dayNumber - 1);
//            mLastVisibleDay.add(Calendar.DATE, dayNumber - 2);
//            boolean sameDay = isSameDay(day, today);

        // Get more events if necessary. We want to store the events 3 months beforehand. Get
        // events only when it is the first iteration of the loop.
//            if (mEventRects == null || mRefreshEvents ||
//                    (dayNumber == leftDaysWithGaps + 1 && mFetchedPeriod != (int) mWeekViewLoader.toWeekViewPeriodIndex(day) &&
//                            Math.abs(mFetchedPeriod - mWeekViewLoader.toWeekViewPeriodIndex(day)) > 0.5)) {
//                getMoreEvents(day);
//                mRefreshEvents = false;
//            }

        // Draw background color for each day.
//        Log.d("TAG", "startPixel: " + startPixel);
//        Log.d("TAG", "mWidthEachEvent: " + mWidthEachEvent);
//        float start = (startPixel < mWidthEachEvent ? mWidthEachEvent : startPixel);
        // float start = startPixel;
        //  if (mWidthEachEvent + startPixel - start > 0) {
        float startY = mHeightEachEvent + mCurrentOrigin.y;
        for (int i = 0; i < mSizeStagesList; i++) {
            int dx = (int) (mCurrentOrigin.x + mWidthEachEvent * i + mWidthEachEvent);
            Log.d("TAG", "xx: " + dx);
            if (i % 2 == 0) {
                canvas.drawRect(dx, startY, dx + mWidthEachEvent, getHeight(), paintEvenBg);
            } else {
                canvas.drawRect(dx, startY, dx + mWidthEachEvent, getHeight(), paintOddBg);
            }
            //   }
        }

        // Prepare the separator lines for hours.
        // int i = 0;
//            for (int hourNumber = 0; hourNumber < 24; hourNumber++) {
//                float top = mHeaderHeight + mHeaderRowPadding * 2 + mCurrentOrigin.y + mHourHeight * hourNumber + mTimeTextHeight / 2 + mHeaderMarginBottom;
//                //  float top = mHeaderHeight + mCurrentOrigin.y + mHourHeight * hourNumber ;
//                if (top > mHeaderHeight + mHeaderRowPadding * 2 + mTimeTextHeight / 2 + mHeaderMarginBottom - mHourSeparatorHeight && top < getHeight() && startPixel + mWidthPerDay - start > 0) {
//                    hourLines[i * 4] = start;
//                    hourLines[i * 4 + 1] = top;
//                    hourLines[i * 4 + 2] =  mWidthPerDay;
//                    hourLines[i * 4 + 3] = top;
//                    i++;
//                }
//            }

        // Draw the lines for hours.
        //  canvas.drawLines(hourLines, mHourSeparatorPaint);

        // Draw the events.
        //  drawEvents(day, startPixel, canvas);

        // Draw the line at the current time.
//            if (mShowNowLine && sameDay) {
//                float startY = mHeaderHeight + mHeaderRowPadding * 2 + mTimeTextHeight / 2 + mHeaderMarginBottom + mCurrentOrigin.y;
//                Calendar now = Calendar.getInstance();
//                float beforeNow = (now.get(Calendar.HOUR_OF_DAY) + now.get(Calendar.MINUTE) / 60.0f) * mHourHeight;
//                canvas.drawLine(start, startY + beforeNow, startPixel + mWidthPerDay, startY + beforeNow, mNowLinePaint);
//            }

        // In the next iteration, start from the next day.
        // startPixel += mWidthPerDay + mColumnGap;
        //    }

        // Hide everything in the first cell (top left corner).
        canvas.clipRect(0, 0, mWidthEachEvent, mHeightEachEvent, Region.Op.REPLACE);
        canvas.drawRect(0, 0, mWidthEachEvent, mHeightEachEvent, paintEvenBg);

        // Clip to paint header row only.
        canvas.clipRect(mWidthEachEvent, 0, getWidth(), mHeightEachEvent, Region.Op.REPLACE);

        // Draw the header background.
        canvas.drawRect(0, 0, getWidth(), mHeightEachEvent, paintEvenBg);


        // Draw the header row texts.
        Paint paintTextCounter = new Paint();
        paintTextCounter.setColor(Color.WHITE);
        paintTextCounter.setTextSize(mTextTitleStage);
        paintTextCounter.setTextAlign(Paint.Align.CENTER);
        paintTextCounter.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        for (int i = 0; i < mSizeStagesList; i++) {
            int dx = (int) (mCurrentOrigin.x + mWidthEachEvent * i + mWidthEachEvent);
            if (i % 2 == 0) {
                canvas.drawRect(dx, 0, dx + mWidthEachEvent, mHeightEachEvent, paintEvenBg);
            } else {
                canvas.drawRect(dx, 0, dx + mWidthEachEvent, mHeightEachEvent, paintOddBg);
            }
            canvas.drawText(mStages.get(i).getName(), dx + mWidthEachEvent / 2, mHeightEachEvent / 2, paintTextCounter);
        }
    }


    private void drawStages(Canvas canvas) {

        Paint paintTextCounter = new Paint();
        paintTextCounter.setColor(Color.WHITE);
        paintTextCounter.setTextSize(mTextTitleStage);
        paintTextCounter.setTextAlign(Paint.Align.CENTER);
        paintTextCounter.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        Paint paintEvenBg = new Paint();
        paintEvenBg.setColor(getResources().getColor(R.color.bg));
        Paint paintOddBg = new Paint();
        paintOddBg.setColor(Color.GRAY);

        if (mCurrentScrollDirection == Direction.HORIZONTAL) {
            if (mCurrentOrigin.x - mDistanceX > 0) {
                mCurrentOrigin.x = 0;
            } else if (mCurrentOrigin.x - mDistanceX < -(mWidthEachEvent * mSizeStagesList - (getWidth()))) {
                mCurrentOrigin.x = -(mWidthEachEvent * mSizeStagesList - (getWidth()));
            } else {
                mCurrentOrigin.x = mCurrentOrigin.x - mDistanceX;
            }
        }

        for (int i = 0; i < mStages.size(); i++) {
            int dx = (int) (mCurrentOrigin.x + mWidthEachEvent * i + mWidthEachEvent);
            if (i % 2 == 0) {
                canvas.drawRect(dx, 0, dx + mWidthEachEvent, mHeightEachEvent, paintEvenBg);
            } else {
                canvas.drawRect(dx, 0, dx + mWidthEachEvent, mHeightEachEvent, paintOddBg);
            }
            Rect bounds = new Rect();
            String value = mStages.get(i).getName();
            paintTextCounter.getTextBounds(value, 0, value.length(), bounds);
            int height = bounds.height();
            canvas.drawText(value, dx + (mWidthEachEvent / 2),
                    mHeightEachEvent / 2 - (height / 4) + (mTextTitleStage / 2), paintTextCounter);
        }
    }

    private void drawTime(Canvas canvas) {
        Paint paintEvenBg = new Paint();
        paintEvenBg.setColor(Color.GREEN);
        // Draw the background color for the header column.
        Log.d("TAG", "drawTime: " + getHeight());
        canvas.drawRect(0, mHeightEachEvent, mWidthEachEvent, getHeight(), paintEvenBg);

        // Clip to paint in left column only.
        canvas.clipRect(0, mHeightEachEvent, mWidthEachEvent, getHeight(), Region.Op.REPLACE);

        Paint painLineStroke = new Paint();
        painLineStroke.setColor(Color.WHITE);
        painLineStroke.setStrokeWidth(3);

        Paint paintLineNoStroke = new Paint();
        paintLineNoStroke.setColor(Color.WHITE);
        paintLineNoStroke.setStrokeWidth(1);

        for (int i = 0; i < 24; i++) {
            float top = mCurrentOrigin.y + mHeightEachEvent * i + 10 + mHeightEachEvent;
            if (top < getHeight()) {
                drawTextTime((int) top, canvas, i, mWidthEachEvent);
                canvas.drawLine(mWidthEachEvent, (int) top, mWidthEachEvent - 20, (int) top, painLineStroke);
                canvas.drawLine(mWidthEachEvent, (int) top + mNormalDistance, mWidthEachEvent - 10,
                        (int) top + mNormalDistance, paintLineNoStroke);
                canvas.drawLine(mWidthEachEvent, (int) top + mNormalDistance * 2,
                        mWidthEachEvent - 10, (int) top + mNormalDistance * 2, paintLineNoStroke);
                canvas.drawLine(
                        mWidthEachEvent, (int) top + mHeightEachEvent / 2, mWidthEachEvent - 20,
                        (int) top + mHeightEachEvent / 2, painLineStroke);
                canvas.drawLine(
                        mWidthEachEvent, (int) top + mNormalDistance * 4,
                        mWidthEachEvent - 10, (int) top + mNormalDistance * 4, paintLineNoStroke);
                canvas.drawLine(
                        mWidthEachEvent, (int) top + mNormalDistance * 5,
                        mWidthEachEvent - 10, (int) top + mNormalDistance * 5, paintLineNoStroke);
            }
        }
    }

    @Override
    public void computeScroll() {
        super.computeScroll();

        if (mScroller.isFinished()) {
            if (mCurrentFlingDirection != Direction.NONE) {
                //// Snap to day after fling is finished.
                //goToNearestOrigin();
            }
        } else {
            if (mCurrentFlingDirection != Direction.NONE) {
                //  goToNearestOrigin();
            } else if (mScroller.computeScrollOffset()) {
                mCurrentOrigin.y = mScroller.getCurrY();
                mCurrentOrigin.x = mScroller.getCurrX();
                ViewCompat.postInvalidateOnAnimation(this);
            }
        }
    }

    private void drawTextTime(int top, Canvas canvas, int i, int mWidthEachEvent) {
        Paint paintTextCounter = new Paint();
        paintTextCounter.setColor(Color.WHITE);
        paintTextCounter.setTextSize(mTextTimeRuler);
        paintTextCounter.setTextAlign(Paint.Align.CENTER);
        paintTextCounter.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));

        Rect bounds = new Rect();
        String value = checkTime(i);
        paintTextCounter.getTextBounds(value, 0, value.length(), bounds);
        int height = bounds.height();
        int widthText = bounds.width();

        canvas.drawText(value,
                mWidthEachEvent / 2 - (widthText / 4) + (mTextTimeRuler / 2),
                top - (height / 4) + (mTextTimeRuler / 2), paintTextCounter);
    }

    private String checkTime(int time) {
        String text = ":00";
        return String.format(Locale.getDefault(), "%02d%s", time, text);
    }


    private void drawOlock(Canvas canvas) {
        Paint paintOclock = new Paint();
        paintOclock.setStrokeWidth(2);
        paintOclock.setStyle(Paint.Style.STROKE);
        // TODO: 4/12/18 load bitmap but not saving on cache
        Bitmap bitmapOclick = BitmapFactory.decodeResource(getResources(), R.drawable.time);
        canvas.drawBitmap(bitmapOclick, (mWidthEachEvent / 2 - bitmapOclick.getWidth() / 2),
                (mHeightEachEvent / 2 - bitmapOclick.getHeight() / 2), paintOclock);
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
//            if (mCurrentScrollDirection == Direction.NONE) {
//                if (Math.abs(distanceX) > Math.abs(distanceY)) {
//                    Log.d("TAG", "onScroll: ");
//                    mCurrentScrollDirection = Direction.HORIZONTAL;
//                    mCurrentFlingDirection = Direction.HORIZONTAL;
//                } else {
//                    mCurrentFlingDirection = Direction.VERTICAL;
//                    mCurrentScrollDirection = Direction.VERTICAL;
//                }
//            }
//            mDistanceX = distanceX * mXScrollingSpeed;
//            mDistanceY = distanceY;
//            mIsScrolled = true;
//            invalidate();
//            return true;
            switch (mCurrentScrollDirection) {
                case NONE: {
                    // Allow scrolling only in one direction.
                    if (Math.abs(distanceX) > Math.abs(distanceY)) {
                        mCurrentScrollDirection = Direction.HORIZONTAL;
                    } else {
                        mCurrentScrollDirection = Direction.VERTICAL;
                    }
                    break;
                }
//                case LEFT: {
//                    // Change direction if there was enough change.
//                    if (Math.abs(distanceX) > Math.abs(distanceY) && (distanceX < -mScaledTouchSlop)) {
//                        mCurrentScrollDirection = Direction.RIGHT;
//                    }
//                    break;
//                }
//                case RIGHT: {
//                    // Change direction if there was enough change.
//                    if (Math.abs(distanceX) > Math.abs(distanceY) && (distanceX > mScaledTouchSlop)) {
//                        mCurrentScrollDirection = Direction.LEFT;
//                    }
//                    break;
//                }
            }

            // Calculate the new origin after scroll.
            switch (mCurrentScrollDirection) {
//                case LEFT:
                case HORIZONTAL:
                    mCurrentOrigin.x -= distanceX * mXScrollingSpeed;
                    ViewCompat.postInvalidateOnAnimation(TimetableContainer.this);
                    break;
                case VERTICAL:
                    mCurrentOrigin.y -= distanceY;
                    ViewCompat.postInvalidateOnAnimation(TimetableContainer.this);
                    break;
            }
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
