package com.example.priyankadesai.videorhythmhue;

import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;

import com.philips.lighting.hue.sdk.utilities.PHUtilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class VideoPlayerActivity extends AppCompatActivity {

    private static final long REFRESH_RATE = 100;
    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String TAG = "VIDEO_RHYTHM_HUE";
    private Handler mHandler;
    private Runnable mScreenShotTask;
    private OkHttpClient mOkHttpClient;
    private MediaPlayer mMediaPlayer;
    private TextureView mTextureView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        mMediaPlayer = new MediaPlayer();
        mOkHttpClient = new OkHttpClient();
        mHandler = new Handler();
        //Uri uri = Uri.parse("android.resource://com.example.priyankadesai.videorhythmhue/" + R.raw.sample);
        final Uri uri = Uri.parse("android.resource://com.example.priyankadesai.videorhythmhue/" + R.raw.nature);

        //final ImageView imageViewTemp = findViewById(R.id.imageViewTemp);
        mScreenShotTask = new Runnable() {
            @Override
            public void run() {
                if (mMediaPlayer.isPlaying()) {
                    Bitmap frame = mTextureView.getBitmap();
                    if (frame != null) {
                        //imageViewTemp.setImageBitmap(frame);
                        AverageColor averageColor = AverageColor.fromBitmap(frame, 1);
                        frame.recycle();
                        sendColorToHue(averageColor);
                    }
                }
                mHandler.postDelayed(this, REFRESH_RATE);
            }
        };

        mTextureView = findViewById(R.id.textureView);
        mTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                try {
                    mMediaPlayer.setDataSource(VideoPlayerActivity.this, uri);
                    mMediaPlayer.setSurface(new Surface(surface));
                    mMediaPlayer.prepare();
                    mMediaPlayer.start();
                    mScreenShotTask.run();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {

            }
        });
    }

    private void sendColorToHue(AverageColor averageColor) {
        float xy[] = PHUtilities.calculateXYFromRGB(averageColor.getR(),
                averageColor.getG(),
                averageColor.getB(),
                "LCT014");

        String url = "http://192.168.2.3/api/m1PdiJCDPDPWOg7oIOVBt-Dzja2nSigINkN4dryx/lights/1/state";

        JSONObject jsonBody = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            jsonArray.put(xy[0]);
            jsonArray.put(xy[1]);
            jsonBody.put("xy", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, jsonBody.toString());
        final Request request = new Request.Builder()
                .url(url)
                .put(body)
                .build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "Hue API call onFailure", e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Log.d(TAG, "Hue API call onResponse");
                Log.d(TAG, "Response code: " + response.code());
                //noinspection ConstantConditions
                Log.d(TAG, "Response: " + response.body().string());
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mMediaPlayer.isPlaying()) {
            mMediaPlayer.start();
            mScreenShotTask.run();
        }
    }
}
