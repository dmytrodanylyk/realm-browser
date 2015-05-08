package com.dd.realmbrowser;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import com.dd.realmbrowser.model.RealmPreferences;

public class SettingsActivity extends AppCompatActivity {

    private RealmPreferences mRealmPreferences;

    public static void start(@NonNull Activity activity) {
        Intent intent = new Intent(activity, SettingsActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_settings);

        mRealmPreferences = new RealmPreferences(getApplicationContext());

        initView();
    }

    private void initView() {
        CheckBox cbWrapText = (CheckBox) findViewById(R.id.cbWrapText);
        cbWrapText.setChecked(mRealmPreferences.shouldWrapText());
        cbWrapText.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mRealmPreferences.setShouldWrapText(isChecked);
            }
        });
    }
}
