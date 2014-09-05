package com.example.CleverRobot_1_0;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by loredan13 on 9/5/14.
 */
public class FieldSurfaceView extends SurfaceView implements SurfaceHolder.Callback {


    private CleverRobot mBot;
    private DrawThread mDrawThread;

    public FieldSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
    }

    @Override
    public void draw(Canvas canvas) {
        if(isInEditMode()) {
            return;
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
