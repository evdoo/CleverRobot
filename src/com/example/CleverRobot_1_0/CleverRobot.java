package com.example.CleverRobot_1_0;

import android.graphics.*;

/**
 * Created by Olga on 23.08.2014.
 */
public class CleverRobot {
    public static final int RADIUS = 50;

    // текущие координаты на поле.
    private int x;
    private int y;
    private double direction;
    private MoveThread mMoveThread;
    private FieldSurfaceView mFieldSurfaceView;
    private Paint mRobotPaint;

    public CleverRobot(FieldSurfaceView fieldSurfaceView) {
        this(fieldSurfaceView, 2*RADIUS, 2*RADIUS);
    }

    public CleverRobot(FieldSurfaceView fieldSurfaceView, int initX, int initY) {
        x = initX;
        y = initY;
        direction = 0;
        mFieldSurfaceView = fieldSurfaceView;
        mMoveThread = new MoveThread(this);
        mMoveThread.start();

        mRobotPaint = new Paint();
        mRobotPaint.setStyle(Paint.Style.FILL);
        mRobotPaint.setColor(Color.BLACK);
    }

    public void move(long elapsedTime) {

    }

    public void setAction(int controlSignal) {

    }

    public void draw(Canvas canvas) {
        canvas.drawCircle(x, y, RADIUS, mRobotPaint);
    }

    private class MoveThread extends Thread {
        private CleverRobot mRobot;
        private long prevTime;

        public MoveThread(CleverRobot robot) {
            mRobot = robot;
        }

        @Override
        public void run() {
            prevTime = System.currentTimeMillis();
            while (true)
            {
                long time = System.currentTimeMillis();
                mRobot.move(time - prevTime);
                prevTime = time;
            }
        }
    }
}
