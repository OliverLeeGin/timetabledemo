package com.example.macintosh.timetabledemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.example.macintosh.timetabledemo.models.Stage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by macintosh on 4/13/18.
 */

public class TimeTableHeaderView extends View {
    private int mHeightTimeRuler;
    private int mWidthTimeRuler;
    private Paint mPaintTimeRulerBackground = new Paint();
    private List<Stage> mEvents = new ArrayList<>();
    private float mTextTimeRuler = 0;
    private int mWidthEachEvent;
    private int mWidthTimetableContainer;

    public TimeTableHeaderView(Context context, int heightTimeRuler, int widthTimeContainer, List<Stage> events) {
        super(context);
        mWidthTimetableContainer = widthTimeContainer;
        mWidthEachEvent = (widthTimeContainer - (widthTimeContainer / 5)) / 5;
        mHeightTimeRuler = heightTimeRuler;
        mWidthTimeRuler = widthTimeContainer / 5;
        mEvents = events;
        mTextTimeRuler = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                15,
                context.getResources().getDisplayMetrics());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawOclock(canvas);
        drawEvents(canvas);
    }

    private void drawEvents(Canvas canvas) {
        Paint paintLargeTimeTextNoStroke = new Paint();
        paintLargeTimeTextNoStroke.setColor(Color.WHITE);
        paintLargeTimeTextNoStroke.setStrokeWidth(1);
        paintLargeTimeTextNoStroke.setStyle(Paint.Style.STROKE);
        mPaintTimeRulerBackground.setColor(getResources().getColor(R.color.bg));
        Paint paintTextCounter = new Paint();
        paintTextCounter.setColor(Color.WHITE);
        paintTextCounter.setTextSize(mTextTimeRuler);
        paintTextCounter.setTextAlign(Paint.Align.CENTER);
        paintTextCounter.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        int dx = mWidthTimeRuler;
        Log.d("TAG", "drawEvents: " + dx);
        Log.d("TAG", "drawEvents: " + mWidthEachEvent);
        Paint paintEvenBg = new Paint();
        paintEvenBg.setColor(getResources().getColor(R.color.bg));
        Paint paintOddBg = new Paint();
        paintOddBg.setColor(Color.GRAY);
        for (int i = 0; i < mEvents.size(); i++) {
            Log.d("TAG", "drawEvents: " + dx);
            if (i % 2 == 0) {
                canvas.drawRect(dx, 0, dx + mWidthEachEvent, mHeightTimeRuler, paintEvenBg);
            } else {
                canvas.drawRect(dx, 0, dx + mWidthEachEvent, mHeightTimeRuler, paintOddBg);
            }
            Rect bounds = new Rect();
            String value = mEvents.get(i).getName();
            paintTextCounter.getTextBounds(value, 0, value.length(), bounds);
            int height = bounds.height();
            canvas.drawText(value, dx + (mWidthEachEvent / 2),
                    mHeightTimeRuler / 2 - (height / 4) + (mTextTimeRuler / 2), paintTextCounter);
            dx = dx + mWidthEachEvent;
        }
       // canvas.drawLine(mWidthTimeRuler, mHeightTimeRuler, mWidthTimetableContainer, mHeightTimeRuler, paintLargeTimeTextNoStroke);
    }

    private void drawOclock(Canvas canvas) {
        mPaintTimeRulerBackground.setColor(getResources().getColor(R.color.bg));
        canvas.drawRect(0, 0, mWidthTimeRuler, mHeightTimeRuler, mPaintTimeRulerBackground);
        Paint paintViewContainer = new Paint();
        paintViewContainer.setStrokeWidth(2);
        paintViewContainer.setStyle(Paint.Style.STROKE);
        // TODO: 4/12/18 load bitmap but not saving on cache
        Bitmap bitmapOclick = BitmapFactory.decodeResource(getResources(), R.drawable.time);
        canvas.drawBitmap(bitmapOclick, (mWidthTimeRuler / 2 - bitmapOclick.getWidth() / 2),
                (mHeightTimeRuler / 2 - bitmapOclick.getHeight() / 2), paintViewContainer);
    }
}
