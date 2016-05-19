package com.example.administrator.ebook;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by Lxr on 2016/5/15.
 */
public class Singleton  {
    //保存一些全局变量和通用的方法

    private int gridViewItemWidth;
    private int gridViewItemHeight;
    private int screenHeight;
    private int screenWidth;

    private  Singleton(){}

    public  static  Singleton instance;

    public  static  Singleton getInstance (){
        if ( instance == null ) {
            instance = new Singleton ();
        }
        return  instance;
    }


    public int getGridViewItemHeight() {
        return gridViewItemHeight;
    }

    public void setGridViewItemHeight(int gridViewItemHeight) {
        this.gridViewItemHeight = gridViewItemHeight;
    }

    public int getGridViewItemWidth() {
        return gridViewItemWidth;
    }

    public void setGridViewItemWidth(int gridViewItemWidth) {
        this.gridViewItemWidth = gridViewItemWidth;
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public void setScreenWidth(int screenWidth) {
        this.screenWidth = screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public void setScreenHeight(int screenHeight) {
        this.screenHeight = screenHeight;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // 源图片的高度和宽度
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            // 计算出实际宽高和目标宽高的比率
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            // 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
            // 一定都会大于等于目标的宽和高。
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
        // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);
        // 调用上面定义的方法计算inSampleSize值
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        // 使用获取到的inSampleSize值再次解析图片
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }
}
