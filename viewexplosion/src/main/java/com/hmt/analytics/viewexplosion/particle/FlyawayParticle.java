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
    float mPoint2CenterDis;//圆点距离中心的距离
    float mCenterX;
    float mCenterY;
    Utils.Polar mPolar;
    float mRandomAngle;
    float mStartRadius;//圆点最开始距圆心的半径
    float mMaxRadius; //圆点能显示的最大范围
    float mStartShowRadius;//圆点开始显示的半径
    Random mRandom = new Random();

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
        mPoint2CenterDis = startRadius;
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
//        if (mPoint2CenterDis < mBound.width() / 2 || mPoint2CenterDis >= mMaxRadius - mMaxRadius / 10) {
//            paint.setColor(Color.TRANSPARENT);
//        }
        paint.setAlpha(255);
        if (mPoint2CenterDis >= mStartShowRadius) {
            canvas.drawCircle(cx, cy, mRadius, paint);
        }
        canvas.restore();
    }

    float mMoveSpeed = 0.5f;

    @Override
    protected void caculate(float factor) {
        //控制移动速度
        if (mPoint2CenterDis > mStartShowRadius) {
            mMoveSpeed = 0.5f;
        } else {
            mMoveSpeed = 0.3f;
        }

        mPoint2CenterDis += mMoveSpeed;
        //控制显示区域
        if (mPoint2CenterDis > mMaxRadius) {
            mPoint2CenterDis = mStartRadius;
        }

        cx = (float) (mPoint2CenterDis * Math.sin(mRandomAngle));
        cy = (float) (mPoint2CenterDis * Math.cos(mRandomAngle));
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
