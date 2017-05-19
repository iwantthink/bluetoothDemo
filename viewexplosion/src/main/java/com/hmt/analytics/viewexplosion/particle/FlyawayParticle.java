package com.hmt.analytics.viewexplosion.particle;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import com.hmt.analytics.viewexplosion.Utils;
import com.hmt.analytics.viewexplosion.factory.FlyawayFactory;

import java.util.Random;


/**
 * Created by Administrator on 2015/11/29 0029.
 */
public class FlyawayParticle extends Particle {
    float mRadius = FlyawayFactory.PART_WH;//小球半径
    float mAlpha;
    Rect mBound;
    float ox, oy;
    float mOuterRadius;//圆点距离中心的距离
    float mCenterX;
    float mCenterY;
    Utils.Polar mPolar;
    float mRandomAngle;
    float mStartRadius;//圆点最开始距圆心的半径
    float mMaxRadius; //圆点能显示的最大范围
    float mStartShowRadius;//圆点开始显示的半径
    Random mRandom = new Random();

    /**
     * @param color 颜色
     * @param x
     * @param y
     */
    public FlyawayParticle(int color, float x, float y, Rect bound) {
        super(color, x, y);
        ox = x;
        oy = y;
        mBound = bound;
    }

    public FlyawayParticle(int color, float x, float y, float radius,
                           float randomAngle, float startRadius,
                           Rect bound) {
        super(color, x, y);
        ox = x;
        oy = y;
        mBound = bound;
        mRadius = radius;
        mOuterRadius = startRadius;
        mStartRadius = startRadius;
        mCenterX = bound.centerX();
        mCenterY = bound.centerY();
        mRandomAngle = randomAngle;
        mMaxRadius = mBound.width() / 10 * 13;
        mAlpha = mRandom.nextFloat();
        mStartShowRadius = mBound.width() > mBound.height() ? mBound.height() / 2 : mBound.width() / 2;
    }


    @Override
    protected void draw(Canvas canvas, Paint paint) {
        drawPoint(canvas, paint);
    }

    private void drawPoint(Canvas canvas, Paint paint) {
        paint.setColor(color);
        paint.setAlpha((int) (Color.alpha(color) * mAlpha)); //这样透明颜色就不是黑色了
        canvas.save();
        canvas.translate(mCenterX, mCenterY);
        paint.setStyle(Paint.Style.FILL);
//        paint.setColor(Color.WHITE);
//        if (mRandomAngle < 90 && mRandomAngle > 0) {
//            paint.setColor(Color.RED);
//        } else if (mRandomAngle < 180 && mRandomAngle >= 90) {
//            paint.setColor(Color.GREEN);
//        } else if (mRandomAngle < 270 && mRandomAngle >= 180) {
//            paint.setColor(Color.BLUE);
//        } else {
//            paint.setColor(Color.BLACK);
//        }
//        if (mOuterRadius < mBound.width() / 2 || mOuterRadius >= mMaxRadius - mMaxRadius / 10) {
//            paint.setColor(Color.TRANSPARENT);
//        }
        paint.setAlpha(255);
        if (mOuterRadius >= mStartShowRadius) {
            canvas.drawCircle(cx, cy, mRadius, paint);
        }
        canvas.restore();
    }

    float mFlashFrequency = 0.01f;
    float mMoveSpeed = 0.5f;

    @Override
    protected void caculate(float factor) {
        //控制移动速度
        float moveSpeed = 1 - FlyawayFactory.mInterpolator.getInterpolation(mOuterRadius / mMaxRadius);
        if (moveSpeed > 0.6f) {
            moveSpeed = 0.5f;
        } else if (moveSpeed < 0.4f) {
            moveSpeed = 0.5f;
        }


        mOuterRadius += mMoveSpeed;
        //控制显示区域
        if (mOuterRadius > mMaxRadius) {
            mOuterRadius = mStartRadius;
        }

        cx = (float) (mOuterRadius * Math.sin(mRandomAngle));
        cy = (float) (mOuterRadius * Math.cos(mRandomAngle));
        if (mRandomAngle < 90 && mRandomAngle > 0) {
            cx = Math.abs(cx);
            cy = Math.abs(cy);
        } else if (mRandomAngle < 180 && mRandomAngle >= 90) {
            cx = -Math.abs(cx);
            cy = Math.abs(cy);
        } else if (mRandomAngle < 270 && mRandomAngle >= 180) {
            cx = -Math.abs(cx);
            cy = -Math.abs(cy);
        } else {
            cx = +Math.abs(cx);
            cy = -Math.abs(cy);
        }

        mAlpha = 0.8f;
    }
}
