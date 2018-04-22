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

    ///scale
    private ScaleGestureDetector mScaleDetector;
    private final static float MIN_ZOOM = 1f;
    private final static float MAX_ZOOM = 5f;
    private float mScaleFactor = 1.f;

    //These constants specify the mode that we're in
    private final static int NONE = 0;
    private final static int DRAG = 1;
    private final static int ZOOM = 2;
    private int mMode;
    private boolean mDragged;

    //These two variables keep track of the X and Y coordinate of the finger when it first
    //touches the screen
    private float mStartX = 0f;
    private float mStartY = 0f;
    //These two variables keep track of the amount we need to translate the canvas along the X
    //and the Y coordinate
    private float mTranslateX = 0f;
    private float mTranslateY = 0f;
    //These two variables keep track of the amount we translated the X and Y coordinates, the last time we
    //panned.
    private float mPreviousTranslateX = 0f;
    private float mPreviousTranslateY = 0f;

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
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();
            mScaleFactor = Math.max(MIN_ZOOM, Math.min(mScaleFactor, MAX_ZOOM));
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

        canvas.save();

        //We're going to scale the X and Y coordinates by the same amount
        canvas.scale(this.mScaleFactor, this.mScaleFactor, this.mScaleDetector.getFocusX(), this.mScaleDetector.getFocusY());
        //If translateX times -1 is lesser than zero, let's set it to zero. This takes care of the left bound
        if ((mTranslateX * -1) < 0) {
            mTranslateX = 0;
        }

        //This is where we take care of the right bound. We compare translateX times -1 to (scaleFactor - 1) * displayWidth.
        //If translateX is greater than that value, then we know that we've gone over the bound. So we set the value of
        //translateX to (1 - scaleFactor) times the display width. Notice that the terms are interchanged; it's the same
        //as doing -1 * (scaleFactor - 1) * displayWidth
        else if ((mTranslateX * -1) > (mScaleFactor - 1) * mWidthEventContainer) {
            mTranslateX = (1 - mScaleFactor) * mWidthEventContainer;
        }

        if (mTranslateY * -1 < 0) {
            mTranslateY = 0;
        }

        //We do the exact same thing for the bottom bound, except in this case we use the height of the display
        else if ((mTranslateY * -1) > (mScaleFactor - 1) * mHeightEventContainer) {
            mTranslateY = (1 - mScaleFactor) * mHeightEventContainer;
        }

        //We need to divide by the scale factor here, otherwise we end up with excessive panning based on our zoom level
        //because the translation amount also gets scaled according to how much we've zoomed into the canvas.
        canvas.translate(mTranslateX / mScaleFactor, mTranslateY / mScaleFactor);


        drawOlock(canvas);
        drawHeaderRowAndEvents(canvas);
        drawTime(canvas);
              /* The rest of your canvas-drawing code */
        canvas.restore();
    }

    private void drawHeaderRowAndEvents(Canvas canvas) {
        Paint paintEvenBg = new Paint();
        paintEvenBg.setColor(getResources().getColor(R.color.bg));
        Paint paintEvenBg2 = new Paint();
        paintEvenBg2.setColor(Color.RED);
        Paint paintOddBg = new Paint();
        paintOddBg.setColor(Color.GRAY);

      //  if (mAreDimensionsInvalid) {
         //   mAreDimensionsInvalid = false;
//            if (mScrollToHour >= 0)
//                 goToHour(mScrollToHour);
//
//                mScrollToDay = null;
//            mScrollToHour = -1;
//            mAreDimensionsInvalid = false;
//        }

        // If the new mCurrentOrigin.y is invalid, make it valid.
        if (mCurrentOrigin.y < getHeight() - mHeightEachEvent * 24 - mHeightEachEvent)
            mCurrentOrigin.y = getHeight() - mHeightEachEvent * 24 - mHeightEachEvent + mTranslateY;

        // Don't put an "else if" because it will trigger a glitch when completely zoomed out and
        // scrolling vertically.
        if (mCurrentOrigin.y > 0) {
            mCurrentOrigin.y = mTranslateY;
        }

        if (mCurrentOrigin.x < getWidth() - mWidthEachEvent * mSizeStagesList - mWidthEachEvent)
            mCurrentOrigin.x = getWidth() - mWidthEachEvent * mSizeStagesList - mWidthEachEvent;

        if (mCurrentOrigin.x > 0) {
            mCurrentOrigin.x = 0;
        }

        Paint paint = new Paint();
        paint.setColor(Color.RED);
        canvas.clipRect(mWidthEachEvent, 0, getWidth(), getHeight(), Region.Op.REPLACE);
        canvas.drawRect(mWidthEachEvent, 0, getWidth(), getHeight(), paint);

        float startY = mHeightEachEvent + mCurrentOrigin.y;
        for (int i = 0; i < mSizeStagesList; i++) {
            int dx = (int) (mCurrentOrigin.x + mWidthEachEvent * i + mWidthEachEvent);
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
            Log.d("TAG", "drawHeaderRowAndEvents: ");
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
            float top = mCurrentOrigin.y + mHeightEachEvent * hourNumber + 10 + mHeightEachEvent;
            int dx = (int) (mCurrentOrigin.x) + mWidthEachEvent;
            if (top < getHeight()) {
                drawEvent(canvas, top, hourNumber, dx);
                canvas.drawLine(dx, (int) top, mWidthEventContainer, (int) top, paintLineNoStroke);
                canvas.drawLine(dx, (int) top + mHeightEachEvent / 2, mWidthEventContainer, (int) top + mHeightEachEvent / 2, paintLineNoStroke);
            }
        }

        // Clip to paint header row only.
        canvas.clipRect(mWidthEachEvent, 0, getWidth(), mHeightEachEvent, Region.Op.REPLACE);

        // Draw the header background.
        canvas.drawRect(mWidthEachEvent, 0, getWidth(), mHeightEachEvent, paintEvenBg2);

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
        canvas.drawBitmap(bitmapOclick, (mWidthEachEvent / 2 - bitmapOclick.getWidth() / 2) + mTranslateX,
                (mHeightEachEvent / 2 - bitmapOclick.getHeight() / 2) +mTranslateY, paintOclock);
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

    private int count = 0;

    private void drawEvent(Canvas canvas, float top, int i, int dx) {
        Paint paintTextCounter = new Paint();
        paintTextCounter.setColor(Color.GREEN);
        Paint paintTextCounter1 = new Paint();
        paintTextCounter1.setColor(Color.RED);
        RectF rectF;
        for (int j = 0; j < mStages.size(); j++) {
            if (mStages.get(j).getEvents().size() != 0) {
                for (int k = 0; k < mStages.get(j).getEvents().size(); k++) {
                    String timeHourStart = mStages.get(j).getEvents().get(k).getTimeStart().substring(0, 2);
                    String timeMinStart = mStages.get(j).getEvents().get(k).getTimeStart().substring(3, 5);
                    String timeHourEnd = mStages.get(j).getEvents().get(k).getTimeEnd().substring(0, 2);
                    String timeMinEnd = mStages.get(j).getEvents().get(k).getTimeEnd().substring(3, 5);
                    if (Integer.parseInt(timeHourStart) == i) {
                        Log.d("TAG", "drawEvent:x " + count++);

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
            // mScroller.forceFinished(true);
            //mStickyScroller.forceFinished(true);
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
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mMode = DRAG;
                //We assign the current X and Y coordinate of the finger to startX and startY minus the previously translated
                //amount for each coordinates This works even when we are translating the first time because the initial
                //values for these two variables is zero.
                mStartX = event.getX() - mPreviousTranslateX;
                mStartY = event.getY() - mPreviousTranslateY;
                break;

            case MotionEvent.ACTION_MOVE:
                mTranslateX = event.getX() - mStartX;
                mTranslateY = event.getY() - mStartY;

                //We cannot use startX and startY directly because we have adjusted their values using the previous translation values.
                //This is why we need to add those values to startX and startY so that we can get the actual coordinates of the finger.
                double distance = Math.sqrt(Math.pow(event.getX() - (mStartX + mPreviousTranslateX), 2) +
                        Math.pow(event.getY() - (mStartY + mPreviousTranslateY), 2));
                if (distance > 0) {
                    mDragged = true;
                }
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                mMode = ZOOM;
                break;

            case MotionEvent.ACTION_UP:
                mMode = NONE;
                mDragged = false;

                //All fingers went up, so let's save the value of translateX and translateY into previousTranslateX and
                //previousTranslate
                mPreviousTranslateX = mTranslateX;
                mPreviousTranslateY = mTranslateY;
                mCurrentScrollDirection = Direction.NONE;
                break;

            case MotionEvent.ACTION_POINTER_UP:
                mMode = DRAG;

                //This is not strictly necessary; we save the value of translateX and translateY into previousTranslateX
                //and previousTranslateY when the second finger goes up
                mPreviousTranslateX = mTranslateX;
                mPreviousTranslateY = mTranslateY;
                break;
        }
        // Check after call of mGestureDetector, so mCurrentFlingDirection and mCurrentScrollDirection are set.
//        if (event.getAction() == MotionEvent.ACTION_UP && mCurrentFlingDirection == Direction.NONE) {
//            Log.d("TAG", "onTouchEvent: " + mEventRects.size());
//            mCurrentScrollDirection = Direction.NONE;
//        }

        if ((mMode == DRAG && mScaleFactor != 1f && mDragged) || mMode == ZOOM) {
            invalidate();
        }

        return val;
    }

    private enum Direction {
        NONE, HORIZONTAL, VERTICAL
    }

    public interface IScrollListener {
        void scrollHorizontal(float currentX);

        void scrollVertical(float currentY);
    }
}
