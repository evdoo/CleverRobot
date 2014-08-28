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
import android.widget.TextView;

public class MainActivity extends Activity implements OnTouchListener {
    /**
     * Called when the activity is first created.
     */
    float x;
    float y;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new DrawView(this));
    }

    public boolean onTouch(View v, MotionEvent event) {
        x = event.getX();
        y = event.getY();
        return true;
    }

    class DrawView extends View {

        Paint paint;

        public DrawView(Context context) {
            super(context);
            paint = new Paint();
        }

        protected void onDraw(Canvas canvas) {
            canvas.drawARGB(80, 255, 255, 255);

            float x = canvas.getWidth()/2;
            float y = canvas.getHeight()/2;
            drawRobot(x, y, canvas, paint);

            move(x, y, canvas);
        }

        public void move(float x, float y, Canvas canvas) {
            float x0 = CleverRobot.xo;
            float y0 = CleverRobot.yo;

            // находим соответствующую отрезку-пути до точки линейную функцию, y = kx + b.
            float k = (y0 - y)/(x - x0);
            float b = y - k * x;

            // проходим весь путь от начальных до конечных координат. В каждой точке вызываем drawRobot().
            for (float i = x0; i <= x; i++) {
                y = (k * i + b);
                drawRobot(x, y, canvas, paint);
            }
        }

        public void drawRobot(float x, float y, Canvas canvas, Paint paint) {

            float x1, y1, x2, y2;
            x1 = x - 10;
            y1 = y + 10;
            x2 = x + 10;
            y2 = y - 10;

            paint.setColor(Color.BLACK);
            canvas.drawRect(x1, y1, x2, y2, paint);
        }
    }
}
