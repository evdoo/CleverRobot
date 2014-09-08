package com.example.CleverRobot_1_0;

import android.graphics.*;

/**
 * Created by Olga on 23.08.2014.
 */
public class CleverRobot {
    public static final int RADIUS = 50;
    public static final double DEFAULT_SPEED = 0.3;
    public static final double DEFAULT_TURNING_SPEED = Math.PI / 500;

    // текущие координаты на поле.
    private int x;
    private int y;
    private double direction;
    private double speed;
    private double turnSpeed;
    private MoveThread mMoveThread;
    private FieldSurfaceView mFieldSurfaceView;
    private Paint mRobotPaint;

    public CleverRobot(FieldSurfaceView fieldSurfaceView) {
        this(fieldSurfaceView, 2 * RADIUS, 2 * RADIUS);
    }

    public CleverRobot(FieldSurfaceView fieldSurfaceView, int initX, int initY) {
        x = initX;
        y = initY;
        direction = 0;
        speed = 0;
        turnSpeed = 0;
        mFieldSurfaceView = fieldSurfaceView;
        mMoveThread = new MoveThread(this);
        mMoveThread.start();

        mRobotPaint = new Paint();
        mRobotPaint.setStyle(Paint.Style.FILL);
        mRobotPaint.setColor(Color.BLACK);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    private void move(long elapsedTime) {
        x += speed * elapsedTime * Math.cos(direction);
        y += speed * elapsedTime * Math.sin(direction);
        direction += turnSpeed * elapsedTime;
        mFieldSurfaceView.checkCollision(this);
    }

    public void forceMove(int newX, int newY)
    {
        x = newX;
        y = newY;
    }

    public void feedback(boolean positive) {

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
            while (true) {
                long time = System.currentTimeMillis();
                mRobot.move(time - prevTime);
                prevTime = time;
            }
        }
    }
}
