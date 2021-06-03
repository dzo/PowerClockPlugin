package com.android.dzclock;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;

import com.android.dzclock.databinding.ActivityMainBinding;
import com.skydoves.colorpickerview.ColorPickerDialog;
import com.skydoves.colorpickerview.listeners.ColorListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences mPrefs;
    String runcommand(String cmd) {
        try {
            Process process = Runtime.getRuntime().exec(cmd);//"/system/bin/dmesg");
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
            reader.close();
            return sb.toString();
        } catch (IOException e) {
            return "Error";
        }
    }
    private ActivityMainBinding mBinding;

    void colorDialog(String propname) {
        new ColorPickerDialog.Builder(this)
                    .setTitle("ColorPicker Dialog")
                    .setPreferenceName(propname)
                    .setPositiveButton(getString(android.R.string.ok), (ColorListener) (i, b) -> {
                        mPrefs.edit().putInt(propname, i).apply();
                        mBinding.powerClock.requestLayout();
                    })
                    .attachAlphaSlideBar(false) // the default value is true.
                    .attachBrightnessSlideBar(true)  // the default value is true.
                    .setBottomSpace(12) // set a bottom space between the last slidebar and buttons.
                    .show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPrefs=getSharedPreferences("DzClock", Context.MODE_PRIVATE);
        mBinding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        String enabled=runcommand("cmd overlay list com.android.dzclockpermission");
        if(enabled.contains("dzclockpermission")) {
            mBinding.checkBox.setVisibility(View.VISIBLE);
            mBinding.message.setText(R.string.mustreinstall);
            mBinding.checkBox.setChecked(enabled.contains("[x]"));
            mBinding.checkBox.setOnCheckedChangeListener((v, isChecked) -> {
                if (isChecked) {
                    runcommand("cmd overlay enable com.android.dzclockpermission");
                } else {
                    runcommand("cmd overlay disable com.android.dzclockpermission");
                }
            });
        } else {
            mBinding.checkBox.setVisibility(View.GONE);
            mBinding.message.setText(R.string.installperm);
        }
        int prog=(mPrefs.getInt(SettingsContentProvider.SIZE,400)-100)/6;
        mBinding.seekBar.setProgress(prog);
        mBinding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mPrefs.edit().putInt(SettingsContentProvider.SIZE,progress*6+100).apply();
                mBinding.powerClock.requestLayout();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mBinding.dialCol.setOnClickListener((v) -> colorDialog("DIALCOLOUR"));
        mBinding.textCol.setOnClickListener((v) -> colorDialog("TEXTCOLOUR"));
        mBinding.handCol.setOnClickListener((v) -> colorDialog("HANDCOLOUR"));
        mBinding.showText.setChecked(mPrefs.getInt(SettingsContentProvider.SHOWTEXT,1)==1);
        mBinding.showText.setOnCheckedChangeListener((v, isChecked)-> {
            mPrefs.edit().putInt(SettingsContentProvider.SHOWTEXT,isChecked?1:0).apply();
            mBinding.powerClock.requestLayout();
        });
    }
}