package com.an_liberty.luguianview.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.an_liberty.luguianview.R;

/**
 * Created by liberty on 2018/2/22.
 */

public class TestView extends View {

    private Paint shapePaint;
    private Paint imgPaint;

    private Bitmap bitmap;

    public TestView(Context context) {
        this(context,null);
    }

    public TestView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        shapePaint=new Paint();
        shapePaint.setAntiAlias(true);
        shapePaint.setColor(Color.BLUE);

        imgPaint=new Paint();
        imgPaint.setAntiAlias(true);
        imgPaint.setDither(true);
        imgPaint.setFilterBitmap(true);

    }

    private Bitmap obtainBitmap(int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), R.mipmap.scene, options);
        int width = options.outWidth;
        int height = options.outHeight;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            int halfHeight = height / 2;
            int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        options.inSampleSize = inSampleSize;
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.scene, options);

        bitmap=Bitmap.createScaledBitmap(bitmap,reqWidth,reqHeight,true);
        return bitmap;
    }

    private Bitmap obtainTransparentImg(int reqWidth, int reqHeight){
        Bitmap bitmap=obtainBitmap(reqWidth,reqHeight);
        Bitmap groupbBitmap=Bitmap.createBitmap(reqWidth,reqHeight,bitmap.getConfig());
        Canvas gCanvas=new Canvas(groupbBitmap);
        Paint paint=new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setFilterBitmap(true);
        gCanvas.drawBitmap(bitmap,0,0,null);
        paint.setShader(new LinearGradient(0,reqHeight,reqWidth,reqHeight,Color.BLACK,Color.TRANSPARENT, Shader.TileMode.CLAMP));
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        gCanvas.drawRect(0,0,reqWidth,reqHeight,paint);

        return groupbBitmap;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(500,350);
    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(0,0,getRight(),getBottom(),shapePaint);

        int reqWidth = 2 * getWidth() / 3;
        int reqHeight = getHeight();

        if (bitmap==null){
            bitmap=obtainTransparentImg(reqWidth,reqHeight);
        }

        int save = canvas.save();
        canvas.drawBitmap(bitmap,0,0,null);
//        imgPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
//        LinearGradient gradient=new LinearGradient(0,reqHeight,reqWidth/2,reqHeight,Color.BLACK,Color.TRANSPARENT, Shader.TileMode.CLAMP);
//        imgPaint.setShader(gradient);
//        canvas.drawRect(0,0,reqWidth,reqHeight,imgPaint);
//        imgPaint.setXfermode(null);

        canvas.restoreToCount(save);
    }
}
