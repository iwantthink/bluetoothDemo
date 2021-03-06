package com.hmt.analytics.viewexplosion.factory;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.Log;

import com.hmt.analytics.viewexplosion.particle.FlyawayParticle;
import com.hmt.analytics.viewexplosion.particle.Particle;

import java.util.Random;



public class FlyawayFactory extends ParticleFactory {
    public static final int PART_WH = 8; //默认小球宽高
    private static Random sRandom = new Random();

    /**
     * @param bitmap
     * @param bound  代表原来view空间的宽高信息
     * @return
     */
    public Particle[][] generateParticles(Bitmap bitmap, Rect bound) {
        int w = bound.width(); //场景宽度
        int h = bound.height();//场景高度
        Log.d("FlyawayFactory", "w:" + w);
        Log.d("FlyawayFactory", "h:" + h);
        Log.d("FlyawayFactory", "bound.centerX():" + bound.centerX());
        Log.d("FlyawayFactory", "bound.centerY():" + bound.centerY());

        int partW_Count = w / PART_WH; //横向个数
        int partH_Count = h / PART_WH; //竖向个数
        Log.d("FlyawayFactory", "partH_Count:" + partH_Count);
        Log.d("FlyawayFactory", "partW_Count:" + partW_Count);
//        int bitmap_part_w = bitmap.getWidth() / partW_Count;
//        int bitmap_part_h = bitmap.getHeight() / partH_Count;
        int color = Color.rgb(255, 255, 255);
        Particle[][] particles = new Particle[partH_Count][partW_Count];
        generate(bound, partW_Count, partH_Count, color, particles);
        return particles;
    }

    private void generate(Rect bound, int partW_Count, int partH_Count, int color, Particle[][] particles) {

        StringBuilder sb = new StringBuilder();
        for (int row = 0; row < partH_Count; row++) { //行
            if (row % 2 != 0) {
                continue;
            }
            for (int column = 0; column < partW_Count; column++) { //列
                //取得当前粒子所在位置的颜色
//                int color = bitmap.getPixel(column * bitmap_part_w, row * bitmap_part_h);
                if (column % 2 == 0) {
//                    float x = bound.left + FlyawayFactory.PART_WH * column;
//                    float y = bound.top + FlyawayFactory.PART_WH * row;
                    float radius = PART_WH - sRandom.nextInt(6);//小球大小
                    float randomAngle = sRandom.nextInt(360);
                    float startRadius = bound.width() > bound.height() ? bound.height() / 2 :
                            bound.width() / 2;
                    startRadius -= sRandom.nextInt(bound.width() / 2);
                    particles[row][column] = new FlyawayParticle(color, 0, 0, radius,
                            randomAngle, startRadius, bound);
                }
            }
        }
    }

}
