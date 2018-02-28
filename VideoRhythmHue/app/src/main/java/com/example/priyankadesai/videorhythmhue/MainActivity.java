package com.example.priyankadesai.videorhythmhue;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.calibrateIntensityButton).setOnClickListener(this);
        findViewById(R.id.videoPlayerButton).setOnClickListener(this);
    }

    public void openVideoPlayer(View view) {
        final Intent intent = new Intent(this, VideoPlayerActivity.class);
        startActivity(intent);
    }

    public void openPhilipsHueApp(View view) {
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.philips.lighting.hue2");
        if (launchIntent != null) {
            startActivity(launchIntent);
        }
    }

    public void calibrateIntensity(View view) {
        final Intent intent = new Intent(this, CalibrateIntensityActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.calibrateIntensityButton:
                calibrateIntensity(v);
                break;

            case R.id.videoPlayerButton:
                openVideoPlayer(v);
                break;
        }
    }
}
