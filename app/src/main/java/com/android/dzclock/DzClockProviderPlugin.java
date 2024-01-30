package com.android.dzclock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.BatteryManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.systemui.plugins.ClockController;
import com.android.systemui.plugins.ClockMetadata;
import com.android.systemui.plugins.ClockProviderPlugin;
import com.android.systemui.plugins.ClockSettings;
import com.android.systemui.plugins.annotations.Requires;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Requires(target = ClockProviderPlugin.class, version = ClockProviderPlugin.VERSION)
public class DzClockProviderPlugin implements ClockProviderPlugin {
    private static final String TAG = "DzClockProviderPlugin";
    private Context mContext;
    private Context mSysContext;
    @Override
    public void onCreate(Context context, Context mycontext) {
        mContext=mycontext;
        mSysContext=context;
    }

    @NonNull
    @Override
    public ClockController createClock(@NonNull String s) {
        Log.i(TAG,"createClock "+s);
        return new DzClockController(mContext);
    }

    @NonNull
    @Override
    public ClockController createClock(@NonNull ClockSettings clockSettings) {
        Log.i(TAG,"createClock1 "+clockSettings.getClockId());
        return new DzClockController(mContext);
    }

    @Nullable
    @Override
    public Drawable getClockThumbnail(@NonNull String s) {
        Log.i(TAG,"getClockThumbnail "+s);
        return mContext.getResources().getDrawable(R.drawable.clock_thumb);
    }

    @NonNull
    @Override
    public List<ClockMetadata> getClocks() {
        Log.i(TAG,"getClocks");
        return new ArrayList<>(Arrays.asList(new ClockMetadata("DzClock","Power Clock"),
                new ClockMetadata("DEFAULT","Default Clock")));
    }
}
