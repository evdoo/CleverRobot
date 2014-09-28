package com.example.CleverRobot_1_0;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.TextView;

import static android.view.View.*;

public class MainActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        OnClickListener btnOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = (String) ((Button) view).getText();
                ((FieldSurfaceView) findViewById(R.id.fieldSurfaceView)).mBot.setAction(Integer.parseInt(title)-1);
            }
        };

        findViewById(R.id.btn1).setOnClickListener(btnOnClickListener);
        findViewById(R.id.btn2).setOnClickListener(btnOnClickListener);
        findViewById(R.id.btn3).setOnClickListener(btnOnClickListener);
        findViewById(R.id.btn4).setOnClickListener(btnOnClickListener);
        findViewById(R.id.btn5).setOnClickListener(btnOnClickListener);
        findViewById(R.id.btn6).setOnClickListener(btnOnClickListener);
        findViewById(R.id.btn7).setOnClickListener(btnOnClickListener);
        findViewById(R.id.btn8).setOnClickListener(btnOnClickListener);
        findViewById(R.id.btn9).setOnClickListener(btnOnClickListener);
    }
}
