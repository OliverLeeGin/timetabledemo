package com.example.macintosh.timetabledemo.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.View;

import java.util.Locale;

/**
 * Created by macintosh on 4/16/18.
 */

public class TimeRulerView extends View {

    private PointF mCurrentOrigin = new PointF(0, 0);
    private float mDistanceY = 0;
    private int mHeightOfEachTimeStone;
    private int mWidthTimeRuler;
    private int mNormalDistance;
    private int mLargeDistance;

    private static final int TOTAL_RATIO_SCREEN_HEIGHT_ELEMENT = 11;
    private static final int TOTAL_DISTANCE_EACH_NORNAL_TIME_STONE = 6;
    private static final int TOTAL_DISTANCE_EACH_LARGE_TIME_STONE = 2;

    private float mTextTimeRuler = 0;
    private boolean mIsScrollVertical;

    public TimeRulerView(Context context, int width, int heigh) {
        super(context);
        mWidthTimeRuler = width;
        mHeightOfEachTimeStone = heigh / 11;
        mNormalDistance = mHeightOfEachTimeStone / TOTAL_DISTANCE_EACH_NORNAL_TIME_STONE;
        mLargeDistance = mHeightOfEachTimeStone / TOTAL_DISTANCE_EACH_LARGE_TIME_STONE;
        mTextTimeRuler = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                12,
                context.getResources().getDisplayMetrics());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawTime(canvas);
    }

    private void drawTime(Canvas canvas) {

        Paint painLineStroke = new Paint();
        painLineStroke.setColor(Color.WHITE);
        painLineStroke.setStrokeWidth(3);

        Paint paintLineNoStroke = new Paint();
        paintLineNoStroke.setColor(Color.WHITE);
        paintLineNoStroke.setStrokeWidth(1);

        if (!mIsScrollVertical) {
            if (mCurrentOrigin.y - mDistanceY > 0) {
                mCurrentOrigin.y = 0;
            } else if (mCurrentOrigin.y - mDistanceY < -(mHeightOfEachTimeStone * 24 - (getHeight()))) {
                mCurrentOrigin.y = -(mHeightOfEachTimeStone * 24 - (getHeight()));

            } else {
                mCurrentOrigin.y = mCurrentOrigin.y - mDistanceY;
            }
        }

        for (int i = 0; i < 24; i++) {
            float top = mCurrentOrigin.y + mHeightOfEachTimeStone * i + 10;
            if (top < getHeight()) {
                drawTextTime((int) top, canvas, i, mWidthTimeRuler);
                canvas.drawLine(mWidthTimeRuler, (int) top, mWidthTimeRuler - 20, (int) top, painLineStroke);
                canvas.drawLine(mWidthTimeRuler, (int) top + mNormalDistance, mWidthTimeRuler - 10,
                        (int) top + mNormalDistance, paintLineNoStroke);
                canvas.drawLine(mWidthTimeRuler, (int) top + mNormalDistance * 2,
                        mWidthTimeRuler - 10, (int) top + mNormalDistance * 2, paintLineNoStroke);
                canvas.drawLine(
                        mWidthTimeRuler, (int) top + mHeightOfEachTimeStone / 2, mWidthTimeRuler - 20,
                        (int) top + mHeightOfEachTimeStone / 2, painLineStroke);
                canvas.drawLine(
                        mWidthTimeRuler, (int) top + mNormalDistance * 4,
                        mWidthTimeRuler - 10, (int) top + mNormalDistance * 4, paintLineNoStroke);
                canvas.drawLine(
                        mWidthTimeRuler, (int) top + mNormalDistance * 5,
                        mWidthTimeRuler - 10, (int) top + mNormalDistance * 5, paintLineNoStroke);
            }
        }
    }

    private void drawTextTime(int top, Canvas canvas, int time, int width) {

        Paint paintTextCounter = new Paint();
        paintTextCounter.setColor(Color.WHITE);
        paintTextCounter.setTextSize(mTextTimeRuler);
        paintTextCounter.setTextAlign(Paint.Align.CENTER);
        paintTextCounter.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));

        Rect bounds = new Rect();
        String value = checkTime(time);
        paintTextCounter.getTextBounds(value, 0, value.length(), bounds);
        int height = bounds.height();
        int widthText = bounds.width();

        canvas.drawText(value,
                width / 2 - (widthText / 4) + (mTextTimeRuler / 2),
                top - (height / 4) + (mTextTimeRuler / 2), paintTextCounter);
    }

    private String checkTime(int time) {
        String text = ":00";
        return String.format(Locale.getDefault(), "%02d%s", time, text);
    }

    public void invalidate(float distanceY) {
        mCurrentOrigin.y = distanceY;
        mIsScrollVertical = true;
        invalidate();
    }
}
