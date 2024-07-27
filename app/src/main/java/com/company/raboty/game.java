package com.company.raboty;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintSet;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.company.raboty.databinding.ActivityGameBinding;

public class game extends AppCompatActivity {
    static boolean active = false;
    ImageView[] knives;
    ObjectAnimator animKnife;
    ObjectAnimator cheeseAnim;
    Dialog dialog;
    ObjectAnimator[] knifeAnimators = new ObjectAnimator[7];
    boolean isAnimate = false;

    final  int[] count = {R.drawable.knifes_7, R.drawable.knifes_6, R.drawable.knifes_5, R.drawable.knifes_4, R.drawable.knifes_3, R.drawable.knifes_2, R.drawable.knifes_1, R.drawable.knifes_0};
    int attempt = 7;
    private ActivityGameBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Base_Theme_KnifeThrower);
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        binding = ActivityGameBinding.inflate(getLayoutInflater());

        knives = new ImageView[]{binding.knife1, binding.knife2, binding.knife3, binding.knife4, binding.knife5, binding.knife6, binding.knife7};
        setContentView(binding.getRoot());
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamMute(AudioManager.STREAM_SYSTEM, true);
        active = true;

        cheeseAnim = ObjectAnimator.ofFloat(binding.gameCheese, "rotation",  binding.gameCheese.getRotation(), binding.gameCheese.getRotation() + 360f).setDuration((attempt + 1) * 1000);
        cheeseAnim.setInterpolator(new LinearInterpolator());
        cheeseAnim.setRepeatCount(ValueAnimator.INFINITE);
        cheeseAnim.setRepeatMode(ValueAnimator.RESTART);

        cheeseAnim.start();

        binding.getRoot().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (attempt > 0 && !isAnimate) {
                    if (getSharedPreferences("settings", MODE_PRIVATE).getBoolean("vibro_enabled", true)) {
                        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                        if (vibrator != null) {
                            vibrator.vibrate(100);
                        }
                    }
                    woosh();
                     attempt--;

                     isAnimate = true;

                     animKnife = ObjectAnimator.ofFloat(knives[knives.length - attempt - 1], "translationY", -knives[knives.length - attempt - 1].getY() + binding.gameCheese.getY() + binding.gameCheese.getHeight() - knives[knives.length - attempt - 1].getHeight() / 2);
                     animKnife.setDuration(300);
                     animKnife.setInterpolator(new LinearInterpolator());
                     animKnife.start();

                     animKnife.addListener(new AnimatorListenerAdapter() {
                         @Override
                         public void onAnimationEnd(Animator animation) {

                             super.onAnimationEnd(animation);

                                 knifeAnimators[knifeAnimators.length - attempt - 1] = ObjectAnimator.ofFloat(knives[knives.length - attempt - 1], "rotation", knives[knifeAnimators.length - attempt - 1].getRotation(), knives[knifeAnimators.length - attempt - 1].getRotation() + 360f).setDuration((attempt + 1) * 1000);
                                 knifeAnimators[knifeAnimators.length - attempt - 1].setInterpolator(new LinearInterpolator());
                                 knifeAnimators[knifeAnimators.length - attempt - 1].setRepeatCount(ValueAnimator.INFINITE);
                                 knifeAnimators[knifeAnimators.length - attempt - 1].setRepeatMode(ValueAnimator.RESTART);

                                 binding.count.setImageResource(count[count.length - attempt - 1]);
                                 knives[knives.length - attempt - 1].setPivotY(-binding.gameCheese.getHeight() / 2 + knives[knives.length - attempt - 1].getHeight() / 2);
                                 if (knives.length - attempt < 7) {
                                     knives[knives.length - attempt].setVisibility(View.VISIBLE);
                                 }
                                 if (knifeAnimators.length - attempt - 2 >= 0) {
                                     knifeAnimators[knifeAnimators.length - attempt - 2].removeAllListeners();
                                 }
                                 knifeAnimators[knifeAnimators.length - attempt - 1].addUpdateListener(animator -> {
                                     Rect[] collision = new Rect[7];
                                     for (int i = 0; i < 7 - attempt; ++i) {
                                         collision[i] = new Rect();
                                         knives[i].getHitRect(collision[i]);
                                     }

                                     for (int i = 0; i < 7 - attempt; ++i) {
                                         if (i != knives.length - attempt - 1 && Rect.intersects(collision[knives.length - attempt - 1], collision[i])) {
                                             finishGame(false);
                                             knifeAnimators[knifeAnimators.length - attempt - 1].removeAllListeners();
                                             break;
                                         }
                                     }

                                 });


                                 knifeAnimators[knifeAnimators.length - attempt - 1].start();

                                 cheeseAnim.cancel();
                                 binding.gameCheese.setRotation(binding.gameCheese.getRotation());
                                 cheeseAnim = ObjectAnimator.ofFloat(binding.gameCheese, "rotation", binding.gameCheese.getRotation(), binding.gameCheese.getRotation() + 360f).setDuration((attempt + 1) * 1000);
                                 cheeseAnim.setInterpolator(new LinearInterpolator());
                                 cheeseAnim.setRepeatCount(ValueAnimator.INFINITE);
                                 cheeseAnim.setRepeatMode(ValueAnimator.RESTART);

                                 cheeseAnim.setDuration((attempt + 1) * 1000);
                                 cheeseAnim.start();
                                 for (int i = 0; i < knifeAnimators.length; ++i) {
                                     if (knifeAnimators[knifeAnimators.length - i - 1] != null) {
                                         knifeAnimators[knifeAnimators.length - i - 1].cancel();
                                         knives[knifeAnimators.length - i - 1].setRotation(knives[knifeAnimators.length - i - 1].getRotation());
                                         knifeAnimators[knifeAnimators.length - i - 1] = ObjectAnimator.ofFloat(knives[knives.length - i - 1], "rotation", knives[knifeAnimators.length - i - 1].getRotation(), knives[knifeAnimators.length - i - 1].getRotation() + 360f).setDuration((attempt + 1) * 1000);
                                         knifeAnimators[knifeAnimators.length - i - 1].setInterpolator(new LinearInterpolator());
                                         knifeAnimators[knifeAnimators.length - i - 1].setRepeatCount(ValueAnimator.INFINITE);
                                         knifeAnimators[knifeAnimators.length - i - 1].setRepeatMode(ValueAnimator.RESTART);

                                         knifeAnimators[knifeAnimators.length - i - 1].setDuration((attempt + 1) * 1000);
                                         knifeAnimators[knifeAnimators.length - i - 1].start();
                                     }
                                 }
                                 isAnimate = false;
                                 if (attempt == 0) {
                                     finishGame(true);
                                 }

                         }
                     });
                }

                return false;
            }
        });

    }

    public void finishGame(boolean isWin) {
        if (dialog != null && dialog.isShowing()) {
            return;
        }
        Handler hand = new Handler();
        Runnable run = new Runnable() {
            @Override
            public void run() {
                cheeseAnim.pause();
                animKnife.pause();
                animKnife.removeAllListeners();
                for (int i = 0; i < knifeAnimators.length; ++i) {
                    if (knifeAnimators[i] != null) {
                        knifeAnimators[i].pause();
                    }
                }
            }
        };
        hand.postDelayed(run, 100);


        dialog = new Dialog(this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(android.R.color.transparent);

            WindowManager.LayoutParams wlp = window.getAttributes();
            wlp.dimAmount = 0.7f;

            window.setAttributes(wlp);
        }
        dialog.setContentView(R.layout.dialog);

        if (isWin) {
            ImageView losewin = dialog.findViewById(R.id.losewin);
            losewin.setImageResource(R.drawable.win);
        }
        else {
            ImageView losewin = dialog.findViewById(R.id.losewin);
            losewin.setImageResource(R.drawable.lose);
        }

        dialog.show();
    }

    public void retry(View v) {
        action();
        if (dialog.isShowing()) {
            dialog.dismiss();
            for (int i = 0; i < knives.length; ++i) {
                if (i != 0) {
                    knives[i].setVisibility(View.INVISIBLE);
                }
                knives[i].setRotation(0);
                knives[i].setTranslationY(0);
                knifeAnimators[i] = null;
            }
            binding.gameCheese.setRotation(0);
            cheeseAnim.setDuration(8000);
            cheeseAnim.start();
            binding.count.setImageResource(R.drawable.knifes_7);
            attempt = 7;
        }

    }

    public void woosh() {
        if (getSharedPreferences("settings", MODE_PRIVATE).getBoolean("sound_enabled", true)) {
            MediaPlayer mediaPlayer = MediaPlayer.create(game.this, R.raw.woosh);
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

    public void action() {
        if (getSharedPreferences("settings", MODE_PRIVATE).getBoolean("vibro_enabled", true)) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            if (vibrator != null) {
                vibrator.vibrate(100);
            }
        }

        if (getSharedPreferences("settings", MODE_PRIVATE).getBoolean("sound_enabled", true)) {
            MediaPlayer mediaPlayer = MediaPlayer.create(game.this, R.raw.click);
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

    public void back(View v) {
        action();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        active = false;
    }
}