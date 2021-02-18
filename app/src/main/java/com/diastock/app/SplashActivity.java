package com.diastock.app;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SplashActivity extends AppCompatActivity {

    private Intent intent;
    private ImageView logo;
    private int timeSplash = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        try {
            logo = (ImageView) findViewById(R.id.logo_app_name);

            intent = new Intent(this, LoginActivity.class);
            splashScreen(2000);

            Animation animation = AnimationUtils.loadAnimation(this, R.anim.splash_app_name_anim);
            logo.startAnimation(animation);
        } catch (Exception e) {
            String s = e.getMessage();
        }
        /*
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        }, timeSplash);
        */
    }

    public void splashScreen(final int x) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(x);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                startActivity(intent);
                finish();
            }
        }).run();
    }
}
