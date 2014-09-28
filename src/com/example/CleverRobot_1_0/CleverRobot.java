package com.example.CleverRobot_1_0;

import android.graphics.*;
import android.util.Log;

/**
 * Created by Olga on 23.08.2014.
 */
public class CleverRobot {
    public static final int RADIUS = 50;
    public static final double DEFAULT_SPEED = 1;
    public static final double DEFAULT_TURNING_SPEED = Math.PI / 500;

    public static final double VIEW_ANGLE = Math.PI / 2;
    public static final double VIEW_DISTANCE = RADIUS * 4;
    public static final int LEVELS = 4;
    public static final int SECTORS = 5;

    // текущие координаты на поле.
    private int x;
    private int y;
    private double direction;
    private double speed;
    private double turnSpeed;
    private MoveThread mMoveThread;
    private UpdateThread mUpdateThread;
    private FieldSurfaceView mFieldSurfaceView;
    private Paint mRobotPaint;
    private AdaptiveControl control;

    private boolean[] sensorDataWall;
    private boolean[] sensorDataPaper;

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
        mUpdateThread = new UpdateThread(this);
        mUpdateThread.start();

        mRobotPaint = new Paint();
        mRobotPaint.setStyle(Paint.Style.FILL);
        mRobotPaint.setColor(Color.BLACK);

        sensorDataWall = new boolean[LEVELS * SECTORS];
        sensorDataPaper = new boolean[LEVELS * SECTORS];
        for (int i = 0; i < LEVELS * SECTORS; i++) {
            sensorDataWall[i] = false;
            sensorDataPaper[i] = false;
        }

        control = new AdaptiveControl();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    private void move(long elapsedTime) {
        x += Math.round(speed * elapsedTime * Math.cos(direction));
        y += Math.round(speed * elapsedTime * Math.sin(direction));
        direction += turnSpeed * elapsedTime;
        mFieldSurfaceView.checkCollision(this);
    }

    public void forceMove(int newX, int newY) {
        x = newX;
        y = newY;
    }

    public void feedback(boolean positive) {
        control.evaluate(positive);
    }

    public void setAction(int controlSignal) {
        Log.d("signal", String.valueOf(controlSignal));
        switch (controlSignal) {
            case 0:
                turnSpeed = -DEFAULT_TURNING_SPEED;
                speed = DEFAULT_SPEED;
                break;
            case 1:
                turnSpeed = 0;
                speed = DEFAULT_SPEED;
                break;
            case 2:
                turnSpeed = DEFAULT_TURNING_SPEED;
                speed = DEFAULT_SPEED;
                break;
            case 3:
                turnSpeed = -DEFAULT_TURNING_SPEED;
                speed = 0;
                break;
            case 4:
                turnSpeed = 0;
                speed = 0;
                break;
            case 5:
                turnSpeed = DEFAULT_TURNING_SPEED;
                speed = 0;
                break;
            case 6:
                turnSpeed = -DEFAULT_TURNING_SPEED;
                speed = -DEFAULT_SPEED;
                break;
            case 7:
                turnSpeed = 0;
                speed = -DEFAULT_SPEED;
                break;
            case 8:
                turnSpeed = DEFAULT_TURNING_SPEED;
                speed = -DEFAULT_SPEED;
                break;
        }
    }

    public void updateSensors() {
        for (int i = -SECTORS / 2; i <= SECTORS / 2; i++) {
            for (int j = 1; j <= LEVELS; j++) {
                double azimuth = i * VIEW_ANGLE / SECTORS + direction;
                double distance = RADIUS + j * VIEW_DISTANCE / LEVELS;
                double sensorX = x + distance * Math.cos(azimuth);
                double sensorY = y + distance * Math.sin(azimuth);
                sensorDataWall[j * SECTORS + i] = mFieldSurfaceView.check(sensorX, sensorY);
                sensorDataPaper[j * SECTORS + i] = mFieldSurfaceView.checkPaper(sensorX, sensorY);
            }
        }
        setAction(control.analyze(sensorDataWall, sensorDataPaper));
    }

    public void draw(Canvas canvas) {
        canvas.drawCircle(x, y, RADIUS, mRobotPaint);
        canvas.drawLine(x, y, (float) (RADIUS * 2 * Math.cos(direction)) + x, (float) (RADIUS * 2 * Math.sin(direction)) + y, mRobotPaint);

    }

    private class MoveThread extends Thread {
        private CleverRobot mRobot;
        private long prevTime;
        private boolean mRun;

        public MoveThread(CleverRobot robot) {
            mRobot = robot;
        }

        @Override
        public void run() {
            prevTime = System.currentTimeMillis();
            mRun = true;
            while (mRun) {
                long time = System.currentTimeMillis();
                mRobot.move(time - prevTime);
                prevTime = time;
            }
        }

        public void interrupt() {
            mRun = false;
        }
    }

    private class UpdateThread extends Thread {
        private CleverRobot mRobot;
        private boolean mRun;

        public UpdateThread(CleverRobot robot) {
            mRobot = robot;
        }

        @Override
        public void run() {
            while(mRun) {
                mRobot.updateSensors();
            }
        }

        public void interrupt() {
            mRun = false;
        }
    }
}
