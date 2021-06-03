package com.android.dzclock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.BatteryManager;
import android.util.Log;
import android.view.View;

import com.android.systemui.plugins.ClockPlugin;
import com.android.systemui.plugins.annotations.Requires;

import java.util.TimeZone;

@Requires(target = ClockPlugin.class, version = ClockPlugin.VERSION)
public class DzClock implements ClockPlugin {
    private PowerClock mClock;
    private Context mContext;
    private Context mSysContext;
    private final String TAG="DzClock";
    BroadcastReceiver mPcr;

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
        return null;
    }

    @Override
    public View getView() {
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
        Log.i(TAG,"setDarkAmount:"+v);
        if(v==1.0f) {
            mClock.requestLayout();
        }
    }

    @Override
    public void onTimeZoneChanged(TimeZone timeZone) {
        if(mClock!=null)
            mClock.setTimezone(timeZone);
    }

    @Override
    public void onTimeTick() {
        Log.i(TAG,"onTimeTick");
        if(mClock!=null)
            mClock.onTimeChanged();
    }

}
