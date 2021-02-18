package com.diastock.app;

import android.content.Intent;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class StartActivity extends AppCompatActivity {

    ImageView imageView;
    ImageView imageViewTitle;
    private static int SPLASH_TIME_OUT = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        imageView = (ImageView) findViewById(R.id.logo_view);
        imageViewTitle = (ImageView) findViewById(R.id.logo_view_title);

        Animation animationAppFadeIn = AnimationUtils.loadAnimation(this, R.anim.splash_app_name_anim);
        animationAppFadeIn.setStartOffset(500);
        imageView.startAnimation(animationAppFadeIn);

        /*
        Animation animationAppFadeOut = AnimationUtils.loadAnimation(this, R.anim.splash_app_name_anim_fade_out);
        animationAppFadeOut.setStartOffset(2000);
        imageView.startAnimation(animationAppFadeOut);
        */

        Animation animationTitle1 = AnimationUtils.loadAnimation(this, R.anim.splash_app_name_anim);
        animationTitle1.setStartOffset(2000);
        imageViewTitle.startAnimation(animationTitle1);

        new Handler().postDelayed(new Runnable() {
            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity

                Intent i = new Intent(StartActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
