/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.dzclock;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.BatteryManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.TimeZone;

public class PowerClock extends View {

    private static final String TAG = "PowerClock";
    private Clock mClock;
    private final Context mContext;

    private int mDialWidth=400;
    private int mDialColour=-1;
    private int mHandColour=-1;
    private int mTextColour=-1;

    private final Paint mPaint=new Paint();
    private final Paint mHourPaint=new Paint();
    private final Paint mMinPaint=new Paint();
    private final Paint mTextPaint=new Paint();
    private float mCurrent;
    private float mCurrentAv;

    private float mVoltage;

    private float mMinutes=20;
    private float mHour=9+mMinutes/60;
  //  private boolean mChanged;
    private long mLastCurrentTime;
    private float mTemperature=0;
    private int mStatus;
    private boolean mShowText=true;

    public PowerClock(Context context) {
        this(context, null);
    }

    public PowerClock(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PowerClock(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public PowerClock(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mClock = Clock.systemDefaultZone();
        mContext = context;

    }

    public void setVoltage(float mVoltage) {
        this.mVoltage = mVoltage;
    }

    public void setTemperature(float temp) {
        mTemperature = temp;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if(!isInEditMode()) {
            Cursor c = mContext.getContentResolver().query(Uri.parse("content://dzclock"), null, null, null, null);
            c.moveToFirst();
            mDialWidth = c.getInt(c.getColumnIndex(SettingsContentProvider.SIZE));
            mDialColour = c.getInt(c.getColumnIndex(SettingsContentProvider.DIALCOLOUR));
            mHandColour = c.getInt(c.getColumnIndex(SettingsContentProvider.HANDCOLOUR));
            mTextColour = c.getInt(c.getColumnIndex(SettingsContentProvider.TEXTCOLOUR));
            mShowText = c.getInt(c.getColumnIndex(SettingsContentProvider.SHOWTEXT)) == 1;
            c.close();
        }
        Log.i(TAG,"onMeasure="+widthMeasureSpec+","+heightMeasureSpec);
        if(mDialWidth<100) mDialWidth=400;
    //    try {
    //        mDialWidth = mContext.createPackageContext("com.android.dzclock",0).getSharedPreferences("DzClock",Context.MODE_PRIVATE).getInt("SIZE",400);
    //    } catch (PackageManager.NameNotFoundException e) {
    //        e.printStackTrace();
    //    }
        int mDialHeight = (mDialWidth * 5) / 4;
        mPaint.setColor(mDialColour);
        mHourPaint.setColor(mHandColour);
        mMinPaint.setColor(mHandColour);
        mHourPaint.setStrokeWidth(mDialWidth/33.3f);
        mMinPaint.setStrokeWidth(mDialWidth/66.6f);
      //  mPaint.setStyle(Paint.Style.FILL);

        mPaint.setStrokeWidth(mDialWidth/100.0f);
        mPaint.setAntiAlias(true);
        mTextPaint.setAntiAlias(true);
        mHourPaint.setAntiAlias(true);
        mMinPaint.setAntiAlias(true);
        mMinPaint.setStyle(Paint.Style.STROKE);
        mTextPaint.setColor(mTextColour);
        mTextPaint.setTextSize(mDialWidth/10.0f);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize =  MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize =  MeasureSpec.getSize(heightMeasureSpec);

        float hScale = 1.0f;
        float vScale = 1.0f;

        if (widthMode != MeasureSpec.UNSPECIFIED && widthSize < mDialWidth) {
            hScale = (float) widthSize / (float) mDialWidth;
        }

        if (heightMode != MeasureSpec.UNSPECIFIED && heightSize < mDialHeight) {
            vScale = (float )heightSize / (float) mDialHeight;
        }

        float scale = Math.min(hScale, vScale);

        setMeasuredDimension(resolveSizeAndState((int) (mDialWidth * scale), widthMeasureSpec, 0),
                resolveSizeAndState((int) (mDialHeight * scale), heightMeasureSpec, 0));


    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        Log.i(TAG,"onSizeChanged");
        super.onSizeChanged(w, h, oldw, oldh);
     //   mChanged = true;
    }

    private void updateCurrent() {
        if((mClock.millis()-mLastCurrentTime)<1000)
            return;
        mLastCurrentTime=mClock.millis();
        BatteryManager batteryManager = (BatteryManager)mContext.getSystemService(Context.BATTERY_SERVICE);
        mCurrent=batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW)/1000.0f;
        mCurrentAv=batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_AVERAGE)/1000.0f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //     if(isInEditMode()) canvas.drawColor(Color.BLACK);
   /*     boolean changed = mChanged;
        if (changed) {
            mChanged = false;
        }

    */
        if (!isInEditMode()) updateCurrent();
        int extraheight = mDialWidth / 5;
        int availableWidth = getWidth();
        int availableHeight = getHeight() - extraheight;

        int x = availableWidth / 2;
        int y = availableHeight / 2;

        int w = mDialWidth;
        int h = w;

        if (mShowText) {
            String s;
            if (mStatus == BatteryManager.BATTERY_STATUS_CHARGING) {
                if (mCurrent < 0)
                    s = "Charging: " + String.format("%.2f", -mCurrent / 1000) + "A · " +
                            String.format("%.2f", -mCurrent * mVoltage / 1000) + "W · " +
                            mTemperature + "°C";
                else s = "Charging";
            } else s = (int) mCurrent + "mA · Av " + (int) mCurrentAv + "mA";
            canvas.drawText(s, x, y + w / 2 + mDialWidth * .075f, mTextPaint);
        }

        canvas.save();
        for(int i=0;i<60;i++) {
            int len=mDialWidth/40;
            if(i%5==0) len=mDialWidth/16;
            canvas.drawLine(x,y-(h/2)+mDialWidth/10,x,y-(h/2)+mDialWidth/10+len,mPaint);
            canvas.rotate(6,x,y);
        }
        canvas.restore();
        canvas.save();
        canvas.rotate(mHour / 12.0f * 360.0f, x, y);

        canvas.drawLine(x,y-(h/2)+mDialWidth/4.4f,x,y-mDialWidth/40,mHourPaint);

        canvas.restore();
        canvas.save();
        canvas.rotate(mMinutes / 60.0f * 360.0f, x, y);

        canvas.drawLine(x,y-(h/2)+mDialWidth/5.88f,x,y-mDialWidth/40,mMinPaint);
        canvas.restore();
        canvas.drawCircle(x,y,mDialWidth/40,mMinPaint);
    }

    public void onTimeChanged() {
        long nowMillis = mClock.millis();
        LocalDateTime localDateTime = toLocalDateTime(nowMillis, mClock.getZone());

        int hour = localDateTime.getHour();
        int minute = localDateTime.getMinute();
        int second = localDateTime.getSecond();

        mMinutes = minute + second / 60.0f;
        mHour = hour + mMinutes / 60.0f;
        invalidate();
    }

    private static LocalDateTime toLocalDateTime(long timeMillis, ZoneId zoneId) {
        Instant instant = Instant.ofEpochMilli(timeMillis);
        return LocalDateTime.ofInstant(instant, zoneId);
    }
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Log.d(TAG, "onAttachedToWindow");
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.d(TAG, "onDetachedFromWindow");
    }

    public void setStatus(int status) {
        mStatus=status;
    }

    public void setTimezone(TimeZone timeZone) {
        mClock=Clock.system(timeZone.toZoneId());
    }
}
