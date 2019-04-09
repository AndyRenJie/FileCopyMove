package com.andy.filecopymove.sample.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class TestWidget extends View {

    private Paint mPaint;
    private Path mPath;

    public TestWidget(Context context) {
        this(context, null);
    }

    public TestWidget(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TestWidget(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPath = new Path();
    }

    @SuppressLint("NewApi")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //画矩形
        mPaint.setColor(Color.BLUE);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(100, 100, 300, 300, mPaint);
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(400, 100, 600, 300, mPaint);
        //画点
        mPaint.setColor(Color.GREEN);
        mPaint.setStrokeWidth(20);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        float[] points = {50, 50, 50, 100, 100, 50, 100, 100, 150, 50, 150, 100};
        canvas.drawPoints(points, mPaint);
        //画圆角矩形
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawRoundRect(100, 400, 300, 600, 10, 10, mPaint);
        //画扇形
        mPaint.setColor(Color.YELLOW);
        canvas.drawArc(400, 400, 600, 600, 0, 90, true, mPaint);
        //画心
        mPath.addArc(100, 700, 300, 900, -225, 225);

    }
}
