package com.android.dzclock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.BatteryManager;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.systemui.plugins.ClockAnimations;
import com.android.systemui.plugins.ClockController;
import com.android.systemui.plugins.ClockEvents;
import com.android.systemui.plugins.ClockFaceController;
import com.android.systemui.plugins.ClockFaceEvents;
import com.android.systemui.plugins.ClockTickRate;
import com.android.systemui.plugins.WeatherData;
import com.android.systemui.plugins.log.LogBuffer;
import com.android.systemui.plugins.log.LogLevel;
import com.android.systemui.plugins.log.LogcatEchoTracker;
import com.android.systemui.plugins.log.LogcatEchoTrackerDebug;
import com.android.systemui.plugins.log.LogcatEchoTrackerProd;

import java.io.PrintWriter;
import java.util.Locale;
import java.util.TimeZone;

public class DzClockController implements ClockController {
    private DzClockFaceController mClock;
    private DzClockFaceController mBigClock;
    private DzClockEvents mEvents;
    private DzClockFaceEvents mFaceEvents;
    private Context mContext;
   // private Context mSysContext;
    private final String TAG="DzClock";
    BroadcastReceiver mPcr;

    class DzClockEvents implements ClockEvents {

        @Override
        public void onColorPaletteChanged(@NonNull Resources resources) {

        }

        @Override
        public void onLocaleChanged(@NonNull Locale locale) {

        }

        @Override
        public void onSeedColorChanged(@Nullable Integer integer) {

        }

        @Override
        public void onTimeFormatChanged(boolean b) {

        }

        @Override
        public void onTimeZoneChanged(@NonNull TimeZone timeZone) {
            if(mClock!=null)
                mClock.clock.setTimezone(timeZone);
            if(mBigClock!=null)
                mBigClock.clock.setTimezone(timeZone);
        }

        @Override
        public void onWeatherDataChanged(@NonNull WeatherData weatherData) {

        }
    }

    class DzClockFaceEvents implements ClockFaceEvents {

        @NonNull
        @Override
        public ClockTickRate getTickRate() {
            return ClockTickRate.PER_MINUTE;
        }

        @Override
        public void onFontSettingChanged(float v) {

        }

        @Override
        public void onRegionDarknessChanged(boolean b) {

        }

        @Override
        public void onTargetRegionChanged(@Nullable Rect rect) {

        }

        @Override
        public void onTimeTick() {
            Log.i(TAG,"onTimeTick");
            if(mClock!=null)
                mClock.clock.onTimeChanged();
            if(mBigClock!=null)
                mBigClock.clock.onTimeChanged();
        }
    }
    class DzClockFaceController implements ClockFaceController {

        LogBuffer mLogBuffer;
        LogcatEchoTracker mLet;
        public PowerClock clock;
        DzClockFaceController(PowerClock p) {
            Log.i(TAG,"DzClockFaceController "+p.toString());
            clock=p;
            mLet=new LogcatEchoTrackerProd();
            mLogBuffer=new LogBuffer("DzClock",100,mLet);
        }
        @NonNull
        @Override
        public ClockFaceEvents getEvents() {
            return mFaceEvents;
        }

        @Nullable
        @Override
        public LogBuffer getLogBuffer() {
            return mLogBuffer;
        }

        @Override
        public void setLogBuffer(@Nullable LogBuffer logBuffer) {
            if(logBuffer!=null)
                mLogBuffer=logBuffer;
        }

        @NonNull
        @Override
        public View getView() {
            return clock;
        }
    }
/*
    @Override
    public String getName() {
        return "Power Clock";
    }

    @Override
    public void onCreate(Context context, Context mycontext) {
        mContext=mycontext;
        mSysContext=context;
        Log.i(TAG,"onCreate"+mContext.getApplicationInfo().toString());
        mClock=new PowerClock(mContext);
        mBigClock=new PowerClock(mContext);
        mBigClock.setmBig(true);
        mPcr =  new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action=intent.getAction();
                if(Intent.ACTION_BATTERY_CHANGED.equals(action)) {
                    int voltage=intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE,0);
                    mClock.setVoltage(voltage/1000.0f);
                    int temp=intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE,0);
                    mClock.setTemperature(temp/10.0f);
                    int status=intent.getIntExtra(BatteryManager.EXTRA_STATUS,0);
                    mClock.setStatus(status);
                    mBigClock.setVoltage(voltage/1000.0f);
                    mBigClock.setTemperature(temp/10.0f);
                    mBigClock.setStatus(status);
                }
                mClock.onTimeChanged();
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        mContext.registerReceiver(mPcr,filter);
    }

    @Override
    public void onDestroy() {
        mClock=null;
        mBigClock=null;
        if(mPcr != null)
            mContext.unregisterReceiver(mPcr);
        mPcr=null;
    }

    @Override
    public String getTitle() {
        return "Power Clock";
    }

    @Override
    public Bitmap getThumbnail() {
        return BitmapFactory.decodeResource(mContext.getResources(), R.drawable.clock_thumb);
    }

    @Override
    public Bitmap getPreview(int width, int height) {
        onTimeTick();
        View view = getView();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.BLACK);
        view.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY));
        view.layout(0, 0, width, height);
        view.draw(canvas);

        return bitmap;

    }

    @Override
    public View getBigClockView() {
        //mClock=new PowerClock(mContext);
        return mBigClock;
    }

    @Override
    public View getView() {
      //  mClock=new PowerClock(mContext);
        return mClock;
    }

    @Override
    public int getPreferredY(int i) {
        return -1;
    }


    @Override
    public void onDestroyView() {
    }



    @Override
    public void setStyle(Paint.Style style) {

    }

    @Override
    public void setTextColor(int i) {

    }

    @Override
    public void setDarkAmount(float v) {
        //Log.i(TAG,"setDarkAmount:"+v);
        if(v==1.0f) {
            mClock.requestLayout();
        //    mBigClock.requestLayout();
       //     mContext.getContentResolver().query(Uri.parse("content://dzclock"),null, "doze", null, null);
        }
    }

    @Override
    public void onTimeZoneChanged(TimeZone timeZone) {
        if(mClock!=null)
            mClock.setTimezone(timeZone);
        if(mBigClock!=null)
            mBigClock.setTimezone(timeZone);
    }

    @Override
    public void onTimeTick() {
        Log.i(TAG,"onTimeTick");
        if(mClock!=null)
            mClock.onTimeChanged();
        if(mBigClock!=null)
            mBigClock.onTimeChanged();
    }
*/
    @NonNull
    @Override
    public ClockAnimations getAnimations() {
        return new ClockAnimations() {
            @Override
            public void enter() {

            }

            @Override
            public void doze(float v) {

            }

            @Override
            public void fold(float v) {

            }

            @Override
            public void charge() {

            }

            @Override
            public void onPositionUpdated(@NonNull Rect rect, @NonNull Rect rect1, float v) {

            }

            @Override
            public boolean getHasCustomPositionUpdatedAnimation() {
                return false;
            }
        };
    }

    @NonNull
    @Override
    public ClockEvents getEvents() {
        return mEvents;
    }

    @NonNull
    @Override
    public ClockFaceController getLargeClock() {
        return mBigClock;
    }

    @NonNull
    @Override
    public ClockFaceController getSmallClock() {
        return mClock;
    }

    @Override
    public void dump(@NonNull PrintWriter printWriter) {

    }

    DzClockController(Context ctx) {
        mContext=ctx;
        mEvents=new DzClockEvents();
        mFaceEvents=new DzClockFaceEvents();
        mClock=new DzClockFaceController(new PowerClock(mContext));
        mBigClock=new DzClockFaceController(new PowerClock(mContext));
        mBigClock.clock.setmBig(true);
        mEvents.onLocaleChanged(Locale.getDefault());
    }
    @Override
    public void initialize(@NonNull Resources resources, float v, float v1) {

        mPcr =  new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action=intent.getAction();
                if(Intent.ACTION_BATTERY_CHANGED.equals(action)) {
                    int voltage=intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE,0);
                    mClock.clock.setVoltage(voltage/1000.0f);
                    int temp=intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE,0);
                    mClock.clock.setTemperature(temp/10.0f);
                    int status=intent.getIntExtra(BatteryManager.EXTRA_STATUS,0);
                    mClock.clock.setStatus(status);
                    mBigClock.clock.setVoltage(voltage/1000.0f);
                    mBigClock.clock.setTemperature(temp/10.0f);
                    mBigClock.clock.setStatus(status);
                }
                mClock.clock.onTimeChanged();
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        mContext.registerReceiver(mPcr,filter);
    }
}
