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

import com.example.macintosh.timetabledemo.R;
import com.example.macintosh.timetabledemo.models.Stage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by macintosh on 4/16/18.
 */

public class StagesView extends View {

    private List<Stage> mStages = new ArrayList<>();
    private int mWidthEachStage;
    private int mHeightEachStage;
    private PointF mCurrentOrigin = new PointF(0, 0);
    private float mDistanceX = 0;
    private float mTextTitleStage;
    private int mSizeStagesList;
    private boolean mIsScrollHorizontal;

    private static final int TOTAL_RATIO_WIDTH_SCREEN_ELEMENT = 4;

    public StagesView(Context context, List<Stage> stages, int height, int width) {
        super(context);
        mStages = stages;
        mWidthEachStage = width / TOTAL_RATIO_WIDTH_SCREEN_ELEMENT;
        mHeightEachStage = height;
        mTextTitleStage = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                15,
                context.getResources().getDisplayMetrics());
        mSizeStagesList = mStages.size();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawStages(canvas);
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

        if (!mIsScrollHorizontal) {
            if (mCurrentOrigin.x - mDistanceX > 0) {
                mCurrentOrigin.x = 0;
            } else if (mCurrentOrigin.x - mDistanceX < -(mWidthEachStage * mSizeStagesList - (getWidth()))) {
                mCurrentOrigin.x = -(mWidthEachStage * mSizeStagesList - (getWidth()));
            } else {
                mCurrentOrigin.x = mCurrentOrigin.x - mDistanceX;
            }
        }

        for (int i = 0; i < mStages.size(); i++) {
            int dx = (int) (mCurrentOrigin.x + mWidthEachStage * i);
            if (i % 2 == 0) {
                canvas.drawRect(dx, 0, dx + mWidthEachStage, mHeightEachStage, paintEvenBg);
            } else {
                canvas.drawRect(dx, 0, dx + mWidthEachStage, mHeightEachStage, paintOddBg);
            }
            Rect bounds = new Rect();
            String value = mStages.get(i).getName();
            paintTextCounter.getTextBounds(value, 0, value.length(), bounds);
            int height = bounds.height();
            canvas.drawText(value, dx + (mWidthEachStage / 2),
                    mHeightEachStage / 2 - (height / 4) + (mTextTitleStage / 2), paintTextCounter);
        }
    }

    public void invalidate(float distanceX) {
        mCurrentOrigin.x = distanceX;
        mIsScrollHorizontal = true;
        invalidate();
    }
}
