package com.example.CleverRobot_1_0;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

/**
 * Created by loredan13 on 9/5/14.
 */
public class FieldSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    public static final int PAPER_RADIUS = 25;

    public CleverRobot mBot;
    private DrawThread mDrawThread;
    private ArrayList<Point> mPapers;
    private Paint mPaperPaint;

    public FieldSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
        mPaperPaint = new Paint();
        mPaperPaint.setColor(Color.GREEN);
        mPaperPaint.setStyle(Paint.Style.FILL);
        mPapers = new ArrayList<Point>();
        mPapers.add(new Point(300, 300));
    }

    public void checkCollision(CleverRobot robot) {
        Rect frame = getHolder().getSurfaceFrame();
        if (robot.getX() < CleverRobot.RADIUS) {
            robot.forceMove(CleverRobot.RADIUS, robot.getY());
            robot.feedback(false);
        }
        if (robot.getY() < CleverRobot.RADIUS) {
            robot.forceMove(robot.getX(), CleverRobot.RADIUS);
            robot.feedback(false);
        }
        if (frame.width() - robot.getX() < CleverRobot.RADIUS) {
            robot.forceMove(frame.width() - CleverRobot.RADIUS, robot.getY());
            robot.feedback(false);
        }
        if (frame.height() - robot.getY() < CleverRobot.RADIUS) {
            robot.forceMove(robot.getX(), frame.height() - CleverRobot.RADIUS);
            robot.feedback(false);
        }
    }

    public boolean check(double x, double y) {
        Rect field = getHolder().getSurfaceFrame();
        return field.contains((int) x, (int) y);
    }

    public boolean checkPaper(double x, double y) {
        for(Point paper : mPapers) {
            if(Math.sqrt((x - paper.x)*(x - paper.x) + (y - paper.y)*(y - paper.y)) < PAPER_RADIUS) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void draw(Canvas canvas) {
        if (isInEditMode()) {
            return;
        }
        for(Point paper : mPapers) {
            canvas.drawCircle(paper.x, paper.y, PAPER_RADIUS, mPaperPaint);
        }
        mBot.draw(canvas);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        mBot = new CleverRobot(this, surfaceHolder.getSurfaceFrame().centerX(), surfaceHolder.getSurfaceFrame().centerY());
        mDrawThread = new DrawThread(this);
        mDrawThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        mDrawThread.stopDrawing();
    }

    private class DrawThread extends Thread {
        private FieldSurfaceView mFieldSurfaceView;
        private SurfaceHolder mFieldSurfaceHolder;
        private boolean mRun;

        public DrawThread(FieldSurfaceView fieldSurfaceView) {
            mFieldSurfaceView = fieldSurfaceView;
            mFieldSurfaceHolder = fieldSurfaceView.getHolder();
        }

        @Override
        public void run() {
            mRun = true;
            Canvas canvas;
            while (mRun) {
                canvas = null;
                try {
                    canvas = mFieldSurfaceHolder.lockCanvas();
                    canvas.drawColor(Color.WHITE);
                    mFieldSurfaceView.draw(canvas);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (canvas != null) {
                        mFieldSurfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }

            }
        }

        public void stopDrawing() {
            mRun = false;
        }
    }
}
