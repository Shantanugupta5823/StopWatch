package com.example.stopwatch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private TextView textView;
    private  boolean isResume;
    Handler handler;
    private int seconds = 0;
    int sec,min,hrs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.chronometer);
        hideSystemBars();
        handler = new Handler();
        textView.setOnClickListener(new DoubleClickListener(){
            @Override
            public void onDoubleClick() {
                seconds = 0;
                sec = 0; min = 0; hrs = 0;
                isResume = false;
                textView.setText("00:00:00");
            }

            @Override
            public void onSingleClick() {
                if(!isResume){
                    handler.postDelayed(runnable,0);
                    isResume = true;
                }else{
                    handler.removeCallbacks(runnable);
                    isResume = false;
                }
            }
        });
    }
    public Runnable runnable = new Runnable(){
        @Override
        public void run() {
            hrs = seconds/3600;
            min = seconds/60;
            sec = seconds%60;
            textView.setText(String.format(Locale.getDefault(),"%02d:%02d:%02d",hrs,min,sec));
            if(isResume){
                seconds++;
            }
            handler.postDelayed(this,1000);
        }
    };
    private void hideSystemBars() {
        WindowInsetsControllerCompat windowInsetsController =
                ViewCompat.getWindowInsetsController(getWindow().getDecorView());
        if (windowInsetsController == null) {
            return;
        }
        // Configure the behavior of the hidden system bars
        windowInsetsController.setSystemBarsBehavior(
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        );
        // Hide both the status bar and the navigation bar
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());
    }
}

abstract class DoubleClickListener implements View.OnClickListener {
    private static final long DEFAULT_QUALIFICATION_SPAN = 200;
    private boolean isSingleEvent;
    private long doubleClickQualificationSpanInMillis;
    private long timestampLastClick;
    private Handler handler;
    private Runnable runnabl;

    public DoubleClickListener() {
        doubleClickQualificationSpanInMillis = DEFAULT_QUALIFICATION_SPAN;
        timestampLastClick = 0;
        handler = new Handler();
        runnabl = new Runnable() {
            @Override
            public void run() {
                if (isSingleEvent) {
                    onSingleClick();
                }
            }
        };
    }

    @Override
    public void onClick(View v) {
        if((SystemClock.elapsedRealtime() - timestampLastClick) < doubleClickQualificationSpanInMillis) {
            isSingleEvent = false;
            handler.removeCallbacks(runnabl);
            onDoubleClick();
            return;
        }

        isSingleEvent = true;
        handler.postDelayed(runnabl, DEFAULT_QUALIFICATION_SPAN);
        timestampLastClick = SystemClock.elapsedRealtime();
    }

    public abstract void onDoubleClick();
    public abstract void onSingleClick();
}