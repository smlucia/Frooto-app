package com.example.frootsapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {


    Button btnSqueeze;
    Handler handler;
    //FirebaseAuth fAuth;
    Animation bounce_anim;
    TextView textView;
    Runnable runnable;
    ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        textView = findViewById(R.id.registerUser);

        bounce_anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
        textView.startAnimation(bounce_anim);

        btnSqueeze = findViewById(R.id.btnSave);
        //fAuth = FirebaseAuth.getInstance();
        imageView = findViewById(R.id.imageView2);

//        imageView.animate().alpha(4000).setDuration(0);
//
//        handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Intent dsp = new Intent(MainActivity.this,LoginActivity.class);
//                startActivity(dsp);
//                finish();
//            }
//        },4000);

        btnSqueeze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateLogin();
            }
        });

    }


    public void navigateLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}