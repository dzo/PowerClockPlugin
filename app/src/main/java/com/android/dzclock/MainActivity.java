package com.android.dzclock;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.android.dzclock.databinding.ActivityMain2Binding;
import com.android.dzclock.databinding.ActivityMainBinding;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity {

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    }
}