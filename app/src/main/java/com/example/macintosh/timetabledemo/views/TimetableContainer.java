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
import android.text.Layout;
import android.text.StaticLayout;
import android.util.Log;
import android.util.TypedValue;
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
import java.util.Locale;

/**
 * Copyright © Nals
 * Created by TrangLT on 4/19/18.
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

    ///scale
    private ScaleGestureDetector mScaleDetector;
    private int mNewWidthEachEvent;
    private int mNewHeightEachEvent;
    private int mWidthHeader;
    private int mHeightHeader;
    private int mMinHourHeight; //no minimum specified (will be dynamic, based on screen)
    private int mEffectiveMinHourHeight; //compensates for the fact that you can't keep zooming out.
    private int mMaxHourHeight = 120;
    private int mMinHourWidth; //no minimum specified (will be dynamic, based on screen)
    private int mEffectiveMinHourWidth; //compensates for the fact that you can't keep zooming out.
    private int mMaxHourWidth = 180;

    public TimetableContainer(Context context, List<Stage> stages, int width, int height) {
        super(context);
        mWidthHeader = width / 5;
        mHeightHeader = height / 12;
        mWidthEachEvent = (width - (width / 5)) / 4;
        mWidthEventContainer = width;
        mHeightEventContainer = height;
        mMinHourHeight = height / 12;
        mEffectiveMinHourHeight = mMinHourHeight;
        mMinHourWidth = (width - (width / 5)) / 4;
        mEffectiveMinHourWidth = mMinHourWidth;
        mHeightEachEvent = height / 12;
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
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            // mScaleFactor *= detector.getScaleFactor();
            //mScaleFactor = Math.max(MIN_ZOOM, Math.min(mScaleFactor, MAX_ZOOM));
            mNewHeightEachEvent = Math.round(mHeightEachEvent * detector.getScaleFactor());
            mNewWidthEachEvent = Math.round(mWidthEachEvent * detector.getScaleFactor());
            invalidate();
            return true;
        }
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
        drawOlock(canvas);
        drawHeaderRowAndEvents(canvas);
        drawTime(canvas);
    }

    private void drawHeaderRowAndEvents(Canvas canvas) {
        Paint paintEvenBg = new Paint();
        paintEvenBg.setColor(getResources().getColor(R.color.bg));
        Paint paintEvenBg2 = new Paint();
        paintEvenBg2.setColor(Color.RED);
        Paint paintOddBg = new Paint();
        paintOddBg.setColor(Color.GRAY);

        if (mNewHeightEachEvent > 0) {
            if (mNewHeightEachEvent < mEffectiveMinHourHeight)
                mNewHeightEachEvent = mEffectiveMinHourHeight;
            else if (mNewHeightEachEvent > mMaxHourHeight)
                mNewHeightEachEvent = mMaxHourHeight;

            mCurrentOrigin.y = (mCurrentOrigin.y / mHeightEachEvent) * mNewHeightEachEvent;
            mHeightEachEvent = mNewHeightEachEvent;
            mNewHeightEachEvent = -1;
        }
        if (mNewWidthEachEvent > 0) {
            if (mNewWidthEachEvent < mEffectiveMinHourWidth)
                mNewWidthEachEvent = mEffectiveMinHourWidth;
            else if (mNewWidthEachEvent > mMaxHourWidth)
                mNewWidthEachEvent = mMaxHourWidth;

            mCurrentOrigin.x = (mCurrentOrigin.x / mWidthEachEvent) * mNewWidthEachEvent;
            mWidthEachEvent = mNewWidthEachEvent;
            mNewWidthEachEvent = -1;
        }
        mNormalDistance = mHeightEachEvent / TOTAL_DISTANCE_EACH_NORNAL_TIME_STONE;
        mLargeDistance = mHeightEachEvent / TOTAL_DISTANCE_EACH_LARGE_TIME_STONE;

        // If the new mCurrentOrigin.y is invalid, make it valid.
        if (mCurrentOrigin.y < getHeight() - mHeightEachEvent * 24 - mHeightEachEvent)
            mCurrentOrigin.y = getHeight() - mHeightEachEvent * 24 - mHeightEachEvent;

        // Don't put an "else if" because it will trigger a glitch when completely zoomed out and
        // scrolling vertically.
        if (mCurrentOrigin.y > 0) {
            mCurrentOrigin.y = 0;
        }

        if (mCurrentOrigin.x < getWidth() - mWidthEachEvent * mSizeStagesList - mWidthEachEvent)
            mCurrentOrigin.x = getWidth() - mWidthEachEvent * mSizeStagesList - mWidthEachEvent;

        if (mCurrentOrigin.x > 0) {
            mCurrentOrigin.x = 0;
        }

        Paint paint = new Paint();
        paint.setColor(Color.RED);
        canvas.clipRect(mWidthHeader, mHeightHeader, getWidth(), getHeight(), Region.Op.REPLACE);
        canvas.drawRect(mWidthHeader, mHeightHeader, getWidth(), getHeight(), paint);

        float startY = mHeightHeader + mCurrentOrigin.y;
        for (int i = 0; i < mSizeStagesList; i++) {
            int dx = (int) (mCurrentOrigin.x + mWidthEachEvent * i + mWidthHeader);
            if (i % 2 == 0) {
                canvas.drawRect(dx, startY, dx + mWidthEachEvent, getHeight(), paintEvenBg);
            } else {
                canvas.drawRect(dx, startY, dx + mWidthEachEvent, getHeight(), paintOddBg);
            }
        }

        // Prepare the separator lines for hours.
        Paint painLineStroke = new Paint();
        painLineStroke.setColor(Color.WHITE);
        painLineStroke.setStrokeWidth(3);

        mEventRects.clear();
// Clear the cache for event rectangles.
//        if (mEventRects != null) {
//            for (EventRect eventRect : mEventRects) {
//                eventRect.rectF = null;
//            }
//        }
        for (int i = 0; i < mSizeStagesList; i++) {
            if (mStages.get(i).getEvents().size() != 0) {
                for (int j = 0; j < mStages.get(j).getEvents().size(); j++) {
                    mEventRects.add(new EventRect(null, mStages.get(i).getEvents().get(j)));
                }
            }
        }

        Paint paintLineNoStroke = new Paint();
        paintLineNoStroke.setColor(Color.WHITE);
        paintLineNoStroke.setStrokeWidth(1);
        for (int hourNumber = 0; hourNumber < 24; hourNumber++) {
            float top = mCurrentOrigin.y + mHeightEachEvent * hourNumber + 10 + mHeightHeader;
            int dx = (int) (mCurrentOrigin.x) + mWidthHeader;
            if (top < getHeight()) {
                drawEvent(canvas, top, hourNumber, dx);
                canvas.drawLine(dx, (int) top, mWidthEventContainer, (int) top, paintLineNoStroke);
                canvas.drawLine(dx, (int) top + mHeightEachEvent / 2, mWidthEventContainer, (int) top + mHeightEachEvent / 2, paintLineNoStroke);
            }
        }


        // Clip to paint header row only.
        canvas.clipRect(mWidthHeader, 0, getWidth(), mHeightHeader, Region.Op.REPLACE);

        // Draw the header background.
        canvas.drawRect(mWidthHeader, 0, getWidth(), mHeightHeader, paintEvenBg2);

        // Draw the header row texts.
        Paint paintTextCounter = new Paint();
        paintTextCounter.setColor(Color.WHITE);
        paintTextCounter.setTextSize(mTextTitleStage);
        paintTextCounter.setTextAlign(Paint.Align.CENTER);
        paintTextCounter.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        Rect bounds = new Rect();

        for (int i = 0; i < mSizeStagesList; i++) {
            int dx = (int) (mCurrentOrigin.x + mWidthEachEvent * i + mWidthHeader);
            if (i % 2 == 0) {
                canvas.drawRect(dx, 0, dx + mWidthEachEvent, mHeightHeader, paintEvenBg);
            } else {
                canvas.drawRect(dx, 0, dx + mWidthEachEvent, mHeightHeader, paintOddBg);
            }
            String value = mStages.get(i).getName();
            paintTextCounter.getTextBounds(value, 0, value.length(), bounds);

            if (bounds.width() > mWidthEachEvent - 20) {
                canvas.drawText("AAA", dx + (mWidthEachEvent / 2),
                        mHeightEachEvent / 2 - (bounds.height() / 4) + (mTextTitleStage / 2), paintTextCounter);
            } else {
                canvas.drawText(value, dx + (mWidthEachEvent / 2),
                        mHeightHeader / 2 - (bounds.height() / 4) + (mTextTitleStage / 2), paintTextCounter);
            }
//            StaticLayout mTextLayout = new StaticLayout("AAAAAAAAAA", mTextPaint, canvas.getWidth(), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);


            //   canvas.drawText(mStages.get(i).getName(), dx + mWidthEachEvent / 2, mHeightHeader / 2, paintTextCounter);
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
            paintTextCounter.setTextAlign(Paint.Align.CENTER);
            int height = bounds.height();
            int width = bounds.width();
            Log.d("TAG", "width: " + width);
            Log.d("TAG", "drawStages: " + mWidthEachEvent);
            if (width > mWidthEachEvent - 20) {
                canvas.drawText("AAA", dx + (mWidthEachEvent / 2),
                        mHeightEachEvent / 2 - (height / 4) + (mTextTitleStage / 2), paintTextCounter);
            } else {
                canvas.drawText(value, dx + (mWidthEachEvent / 2),
                        mHeightEachEvent / 2 - (height / 4) + (mTextTitleStage / 2), paintTextCounter);
            }

        }
    }

    private void drawTime(Canvas canvas) {
        Paint paintEvenBg = new Paint();
        paintEvenBg.setColor(Color.GREEN);
        // Draw the background color for the header column.
        canvas.drawRect(0, mHeightHeader, mWidthHeader, getHeight(), paintEvenBg);
        // Clip to paint in left column only.
        canvas.clipRect(0, mHeightHeader, mWidthHeader, getHeight(), Region.Op.REPLACE);

        Paint painLineStroke = new Paint();
        painLineStroke.setColor(Color.WHITE);
        painLineStroke.setStrokeWidth(3);

        Paint paintLineNoStroke = new Paint();
        paintLineNoStroke.setColor(Color.WHITE);
        paintLineNoStroke.setStrokeWidth(1);

        for (int i = 0; i < 24; i++) {
            float top = mCurrentOrigin.y + mHeightEachEvent * i + 10 + mHeightHeader;
            if (top < getHeight()) {
                drawTextTime((int) top, canvas, i, mWidthEachEvent);
                canvas.drawLine(mWidthHeader, (int) top, mWidthHeader - 20, (int) top, painLineStroke);
                canvas.drawLine(mWidthHeader, (int) top + mNormalDistance, mWidthHeader - 10,
                        (int) top + mNormalDistance, paintLineNoStroke);
                canvas.drawLine(mWidthHeader, (int) top + mNormalDistance * 2,
                        mWidthHeader - 10, (int) top + mNormalDistance * 2, paintLineNoStroke);
                canvas.drawLine(
                        mWidthHeader, (int) top + mHeightEachEvent / 2, mWidthHeader - 20,
                        (int) top + mHeightEachEvent / 2, painLineStroke);
                canvas.drawLine(
                        mWidthHeader, (int) top + mNormalDistance * 4,
                        mWidthHeader - 10, (int) top + mNormalDistance * 4, paintLineNoStroke);
                canvas.drawLine(
                        mWidthHeader, (int) top + mNormalDistance * 5,
                        mWidthHeader - 10, (int) top + mNormalDistance * 5, paintLineNoStroke);
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
                mWidthHeader / 2 - (widthText / 4) + (mTextTimeRuler / 2),
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
        canvas.drawBitmap(bitmapOclick, (mWidthHeader / 2 - bitmapOclick.getWidth() / 2),
                (mHeightHeader / 2 - bitmapOclick.getHeight() / 2), paintOclock);
    }

    private void drawEvents(Canvas canvas) {

        Paint painLineStroke = new Paint();
        painLineStroke.setColor(Color.WHITE);
        painLineStroke.setStrokeWidth(3);

        Paint paintLineNoStroke = new Paint();
        paintLineNoStroke.setColor(Color.WHITE);
        paintLineNoStroke.setStrokeWidth(1);
        for (int i = 0; i < 24; i++) {
            float top = mCurrentOrigin.y + mHeightEachEvent * i + 10 + mHeightEachEvent;
            int dx = (int) (mCurrentOrigin.x) + mWidthEachEvent;
            if (top < getHeight()) {
                drawEvent(canvas, top, i, dx);
            }
        }
    }

    private void drawEvent(Canvas canvas, float top, int i, int dx) {
        Paint paintTextCounter = new Paint();
      //  paintTextCounter.setColor(Color.GREEN);
        Paint paintTextCounter1 = new Paint();
        paintTextCounter1.setColor(Color.RED);
        // stroke
        paintTextCounter.setStyle(Paint.Style.STROKE);
        paintTextCounter.setColor(Color.RED);
        paintTextCounter.setStrokeWidth(2);
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
                        // if (!mIsScrolled) {
                        // mEventRects.add(new EventRect(rectF, mStages.get(j).getEvents().get(k)));
//                        } else {
                        if (mEventRects != null && mEventRects.size() > 0) {
                            for (EventRect eventRect : mEventRects) {
                                if (eventRect.getEvent() == mStages.get(j).getEvents().get(k)) {
                                    eventRect.setRect(rectF);
                                }
                            }
                        }
//                        }
                        int cornerRadius = 5;
                        //  canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, paintTextCounter1);
                        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, paintTextCounter);
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
            }

            // Calculate the new origin after scroll.
            switch (mCurrentScrollDirection) {
                case HORIZONTAL:
                    mCurrentOrigin.x -= distanceX * mXScrollingSpeed;
                    ViewCompat.postInvalidateOnAnimation(TimetableContainer.this);
                    break;
                case VERTICAL:
                    mCurrentOrigin.y -= distanceY;
                    ViewCompat.postInvalidateOnAnimation(TimetableContainer.this);
                    break;
            }
            mIsScrolled = true;
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            for (EventRect eventRect : mEventRects) {
                if (eventRect.getRect() != null) {
                    if (eventRect.getRect().contains(e.getX(), e.getY())) {
                        Toast.makeText(getContext(), "Name of events is " + eventRect.getEvent().getName(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
            return super.onSingleTapConfirmed(e);
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mScaleDetector.onTouchEvent(event);
        boolean val = mGestureDetector.onTouchEvent(event);
        if (event.getAction() == MotionEvent.ACTION_UP && mCurrentFlingDirection == Direction.NONE) {
            mCurrentScrollDirection = Direction.NONE;
        }
        return val;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }


    private enum Direction {
        NONE, HORIZONTAL, VERTICAL
    }
}
