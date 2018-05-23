package com.example.seung.visioncollege;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {


    private TextView tv1, tv2;
    private static int SPLASH_TIME_OUT = 3000;
    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        session = new Session(this);

        // 스플래쉬 화면은 로그인 안된 상태일때만 켜지도록 함.

        if(session.loggedIn()){
            Intent intent = new Intent(SplashActivity.this, LoggedActivity.class);
            startActivity(intent);
            finish();
        }
        else{
            splashScreen();
        }
    }



    private void splashScreen(){

        setContentView(R.layout.activity_splash);
        tv1 = (TextView) findViewById(R.id.textView);
        tv2 = (TextView) findViewById(R.id.textView2);
        Animation myanim = AnimationUtils.loadAnimation(this, R.anim.transistion);
        tv1.startAnimation(myanim);
        tv2.startAnimation(myanim);

        // 스플래쉬 화면 그라디언트 애니메이션

        ConstraintLayout constraintLayout = findViewById(R.id.layout);
        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(1000);
        animationDrawable.setExitFadeDuration(3000);
        animationDrawable.start();

        // 스플래쉬 화면 넘기기

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

    // 로그인된 상태로 앱을 완전히 닫을 경우에, 스플래쉬 화면을 띄움

}
