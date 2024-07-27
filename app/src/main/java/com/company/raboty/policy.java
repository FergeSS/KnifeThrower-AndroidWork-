package com.company.raboty;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.Window;

import com.company.raboty.databinding.ActivityPolicyBinding;

public class policy extends AppCompatActivity {
    static boolean active = false;
    private ActivityPolicyBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        binding = ActivityPolicyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamMute(AudioManager.STREAM_SYSTEM, true);
        active = true;
        binding.policy.loadUrl("https://telegra.ph/Knife-Thrower-07-23");
        binding.policy.clearCache(true);
    }

    public void back(View v) {
        action();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        active = false;
    }

    public void action() {
        if (getSharedPreferences("settings", MODE_PRIVATE).getBoolean("vibro_enabled", true)) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            if (vibrator != null) {
                vibrator.vibrate(100);
            }
        }

        if (getSharedPreferences("settings", MODE_PRIVATE).getBoolean("sound_enabled", true)) {
            MediaPlayer mediaPlayer = MediaPlayer.create(policy.this, R.raw.click);
            mediaPlayer.setVolume(0.2f, 0.2f);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.release();
                }
            });
        }
    }


}