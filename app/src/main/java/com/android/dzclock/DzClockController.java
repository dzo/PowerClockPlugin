package com.android.dzclock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
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
import com.android.systemui.plugins.log.LogcatEchoTracker;
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

    @NonNull
    @Override
    public ClockAnimations getAnimations() {
        return new ClockAnimations() {
            @Override
            public void enter() {

            }

            @Override
            public void doze(float v) {
                Log.i(TAG,"Doze "+v);
            }

            @Override
            public void fold(float v) {
                Log.i(TAG,"Fold "+v);
            }

            @Override
            public void charge() {
                Log.i(TAG,"Charge");
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
