package com.example.macintosh.timetabledemo.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

import com.example.macintosh.timetabledemo.R;

/**
 * Created by macintosh on 4/16/18.
 */

public class OlockView extends View {

    private int mWidthOclock;
    private int mHeightOclock;

    public OlockView(Context context, int width, int height) {
        super(context);
        mWidthOclock = width;
        mHeightOclock = height;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawContainerOclock(canvas);
        drawOlock(canvas);
    }

    private void drawContainerOclock(Canvas canvas) {
        Paint paintOlockContainer = new Paint();
        // TODO: 4/16/18 change color container inhere
        paintOlockContainer.setColor(getResources().getColor(R.color.bg));
        canvas.drawRect(0, 0, mWidthOclock, mHeightOclock, paintOlockContainer);
    }

    private void drawOlock(Canvas canvas) {
        Paint paintOclock = new Paint();
        paintOclock.setStrokeWidth(2);
        paintOclock.setStyle(Paint.Style.STROKE);
        // TODO: 4/12/18 load bitmap but not saving on cache
        Bitmap bitmapOclick = BitmapFactory.decodeResource(getResources(), R.drawable.time);
        canvas.drawBitmap(bitmapOclick, (mWidthOclock / 2 - bitmapOclick.getWidth() / 2),
                (mHeightOclock / 2 - bitmapOclick.getHeight() / 2), paintOclock);
    }
}
