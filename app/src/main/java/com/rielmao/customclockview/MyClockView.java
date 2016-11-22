package com.rielmao.customclockview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.util.Calendar;

/**
 * Created by Riel on 2016/11/20.
 */

public class MyClockView extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    //圆和刻度的画笔
    private Paint mBgPaint;
    //指针的画笔
    private Paint mHandsPaint;

    private int mCanvasWidth, mCanvasHeight;
    private int mRadius = 200;

    //三个指针的长度
    private int mHourHandLength;
    private int mMinHandLength;
    private int mSecHandLength;

    // 时刻度长度
    private int mHourScaleLength;
    // 秒刻度
    private int mSecondScaleLength;

    // 时钟显示的时、分、秒
    private int mHour, mMinute, mSecond;

    private SurfaceHolder mHolder;
    private boolean flag;
    Thread mThread;

    public MyClockView(Context context) {
        this(context, null);
    }

    public MyClockView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyClockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        mMinute = Calendar.getInstance().get(Calendar.MINUTE);
        mSecond = Calendar.getInstance().get(Calendar.SECOND);

        mHolder = getHolder();
        mHolder.addCallback(this);
        mThread = new Thread(this);

        mBgPaint = new Paint();
        mHandsPaint = new Paint();

        mBgPaint.setColor(Color.BLACK);
        mBgPaint.setAntiAlias(true);
        mBgPaint.setStyle(Paint.Style.STROKE);

        mHandsPaint.setColor(Color.BLACK);
        mHandsPaint.setAntiAlias(true);
        mHandsPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mHandsPaint.setTextSize(42);
        mHandsPaint.setTextAlign(Paint.Align.CENTER);

        setFocusable(true);
        setFocusableInTouchMode(true);
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        flag = true;
        new Thread(this).start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        flag = false;
    }

    @Override
    public void run() {
        long start, end;
        while (flag) {
            start = System.currentTimeMillis();
            handler.sendEmptyMessage(0);
            draw();
            logic();
            end = System.currentTimeMillis();
            if (end - start < 1000) {
                try {
                    Thread.sleep(1000 - (end - start));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int desiredWidth, desiredHeight;
        if (widthMode == MeasureSpec.EXACTLY) {
            desiredWidth = widthSize;
        } else {
            desiredWidth = mRadius * 2 + getPaddingLeft() + getPaddingRight();
            if (widthMode == MeasureSpec.AT_MOST) {
                desiredWidth = Math.min(widthSize, desiredWidth);
            }
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            desiredHeight = heightSize;
        } else {
            desiredHeight = mRadius * 2 + getPaddingTop() + getPaddingBottom();
            if (heightMode == MeasureSpec.AT_MOST) {
                desiredHeight = Math.min(heightSize, desiredHeight);
            }
        }
        // +4是为了设置默认的2px的内边距，因为绘制时钟的圆的画笔设置的宽度是2px
        setMeasuredDimension(mCanvasWidth = desiredWidth + 4, mCanvasHeight = desiredHeight + 4);

        mRadius = (int) (Math.min(desiredWidth - getPaddingLeft() - getPaddingRight(),
                desiredHeight - getPaddingTop() - getPaddingBottom()) * 1.0f / 2);
        calculateLengths();
    }

    private void calculateLengths() {
        mHourScaleLength = mRadius / 7;
        mSecondScaleLength = mHourScaleLength / 2;

        mHourHandLength = mRadius / 2;
        mMinHandLength = (int) (mHourHandLength * 1.25);
        mSecHandLength = (int) (mHourHandLength * 1.6);
    }

    private void draw() {
        Canvas mCanvas = mHolder.lockCanvas();
        try {
            if (mCanvas != null) {
                mCanvas.drawColor(Color.WHITE);//刷屏
                // 1.将坐标系原点移至去除内边距后的画布中心，默认在画布左上角，这样做是为了更方便的绘制
                mCanvas.translate(mCanvasWidth * 1.0f / 2 - getPaddingRight() + getPaddingLeft(),
                        mCanvasHeight * 1.0f / 2 - getPaddingBottom() + getPaddingTop());
                //绘制圆心
                mBgPaint.setStrokeWidth(4f);
                mCanvas.drawCircle(0,0,5,mBgPaint);
                //绘制圆盘
                mCanvas.drawCircle(0,0,mRadius,mBgPaint);
                //绘制时刻度
                for (int i = 0; i < 12; i++) {
                    mCanvas.drawLine(0,mRadius,0,mRadius-mHourScaleLength,mBgPaint);
                    mCanvas.rotate(30);
                }
                //绘制秒刻度
                mBgPaint.setStrokeWidth(1.5f);
                for (int i = 0; i < 60; i++) {
                    if (i % 5 != 0){
                        mCanvas.drawLine(0,mRadius,0,mRadius-mSecondScaleLength,mBgPaint);
                    }
                    mCanvas.rotate(6);
                }
                //绘制数字
                mHandsPaint.setColor(Color.BLACK);
                for (int i = 0; i < 12; i++) {
                    String number = 6 + i < 12 ? String.valueOf(6 + i) : (6 + i) > 12
                            ? String.valueOf(i - 6) : "12";
                    mCanvas.save();
                    mCanvas.translate(0,mRadius * 5.5f / 7);
                    mCanvas.rotate(-i*30);
                    mCanvas.drawText(number, 0 , 0 , mHandsPaint);
                    mCanvas.restore();
                    mCanvas.rotate(30);
                }
                //绘制上下午
                mCanvas.drawText(mHour<12?"AM":"PM",0,mRadius/3,mHandsPaint);
                //绘制时针
                mHandsPaint.setColor(Color.BLACK);
                Path path = new Path();
                path.moveTo(0, 0);
                int[] hourPointerCoordinates = getPointerCoordinates(mHourHandLength);
                path.lineTo(hourPointerCoordinates[0], hourPointerCoordinates[1]);
                path.lineTo(hourPointerCoordinates[2], hourPointerCoordinates[3]);
                path.lineTo(hourPointerCoordinates[4], hourPointerCoordinates[5]);
                path.close();
                mCanvas.save();
                mCanvas.rotate(180 + mHour % 12 * 30 + mMinute * 1.0f / 60 * 30);
                mCanvas.drawPath(path, mHandsPaint);
                mCanvas.restore();
                //绘制分针
                mHandsPaint.setColor(Color.BLUE);
                path.reset();
                path.moveTo(0, 0);
                int[] minutePointerCoordinates = getPointerCoordinates(mMinHandLength);
                path.lineTo(minutePointerCoordinates[0], minutePointerCoordinates[1]);
                path.lineTo(minutePointerCoordinates[2], minutePointerCoordinates[3]);
                path.lineTo(minutePointerCoordinates[4], minutePointerCoordinates[5]);
                path.close();
                mCanvas.save();
                mCanvas.rotate(180 + mMinute * 6);
                mCanvas.drawPath(path, mHandsPaint);
                mCanvas.restore();
                // 9.绘制秒针
                mHandsPaint.setColor(Color.RED);
                path.reset();
                path.moveTo(0, 0);
                int[] secondPointerCoordinates = getPointerCoordinates(mSecHandLength);
                path.lineTo(secondPointerCoordinates[0], secondPointerCoordinates[1]);
                path.lineTo(secondPointerCoordinates[2], secondPointerCoordinates[3]);
                path.lineTo(secondPointerCoordinates[4], secondPointerCoordinates[5]);
                path.close();
                mCanvas.save();
                mCanvas.rotate(180 + mSecond * 6);
                mCanvas.drawPath(path, mHandsPaint);
                mCanvas.restore();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mCanvas != null) {
                mHolder.unlockCanvasAndPost(mCanvas);
            }
        }
    }

    private void logic() {
        mSecond++;
        if (mSecond==60){
            mSecond=0;
            mMinute++;
            if (mMinute==60){
                mMinute=0;
                mHour++;
                if (mHour==24){
                    mHour=0;
                }
            }
        }
    }

    /**
     * 获取指针坐标
     *
     * @param pointerLength 指针长度
     * @return int[]{x1,y1,x2,y2,x3,y3}
     */
    private int[] getPointerCoordinates(int pointerLength) {
        int y = (int) (pointerLength * 3.0f / 4);
        int x = (int) (y * Math.tan(Math.PI / 180 * 2));
        return new int[]{-x, y, 0, pointerLength, x, y};
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (onTimeChangeListener != null) {
                onTimeChangeListener.onTimeChange(MyClockView.this, mHour, mMinute, mSecond);
            }
            return false;
        }
    });
    private OnTimeChangeListener onTimeChangeListener;

    public void setOnTimeChangeListener(OnTimeChangeListener onTimeChangeListener) {
        this.onTimeChangeListener = onTimeChangeListener;
    }

    public interface OnTimeChangeListener {
        /**
         * 时间发生改变时调用
         *
         * @param view   时间正在改变的view
         * @param hour   改变后的小时时刻
         * @param minute 改变后的分钟时刻
         * @param second 改变后的秒时刻
         */
        void onTimeChange(View view, int hour, int minute, int second);
    }


}
