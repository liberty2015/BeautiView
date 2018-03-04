package com.an_liberty.luguianview.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.an_liberty.luguianview.R;

/**
 * Created by liberty on 2018/2/11.
 */

public class BeautyView extends View {

    int btnIndex = 0;

    private boolean isClick = false;

    private static final int LEFT = 0;
    private static final int RIGHT = 1;

    private static final String TAG = BeautyView.class.getSimpleName();

    private float btnChildAngle;


    private TextPaint textPaint;
    private TextPaint textStrokePaint;
    private StaticLayout staticLayout;
    private StaticLayout staticStrokeLayout;
    private int textSize;
    private @ColorInt
    int btnTxtColor;

    private Paint imgPaint;

    private Paint shapePaint;
    private Path shapePath;
    private Paint shapeBorderPaint;
    private Path shapeBorderPath;

    private @ColorInt
    int btnColor;

    private String btnTxt;

    private Bitmap imgBtm;

    private @DrawableRes
    int imgRes;

    public BeautyView(Context context) {
        this(context, null);
    }

    public BeautyView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
//        setLayerType(LAYER_TYPE_HARDWARE, null);
        shapePaint = new Paint();
        shapePaint.setAntiAlias(true);
        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.BeautyView);
        btnColor = array.getColor(R.styleable.BeautyView_btnColor, getResources().getColor(R.color.colorAccent));
        shapePaint.setStyle(Paint.Style.FILL);
        shapePaint.setColor(btnColor);
        imgRes = array.getResourceId(R.styleable.BeautyView_btnImg, R.mipmap.ic_launcher);
        btnTxt = array.getString(R.styleable.BeautyView_btnTxt);
        btnTxtColor = array.getColor(R.styleable.BeautyView_btnTxtColor, Color.BLACK);
        Log.d(TAG, "btnTxt=" + btnTxt);
        if (btnTxt == null) {
            btnTxt = "";
        }
        textSize = array.getDimensionPixelSize(R.styleable.BeautyView_btnTxtSize, 15);

        array.recycle();

        imgPaint = new Paint();
        imgPaint.setAntiAlias(true);

        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(textSize);
//        textPaint.setColor(Color.WHITE);

        textStrokePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textStrokePaint.setTextSize(textSize);
        textStrokePaint.setStyle(Paint.Style.STROKE);
        textStrokePaint.setStrokeWidth(10);
//        textStrokePaint.setColor(btnTxtColor);

        textPaintColor(btnTxtColor, textPaint, textStrokePaint);


    }


    private void borderPaintColor(@ColorInt int color,Paint shapeBorderPaint){
        if (!isClick){
            float[] hsv = {0, 0, 0};
            Color.colorToHSV(color, hsv);
            int color1 = Color.HSVToColor(new float[]{hsv[0], 0.40f, 1f});
            LinearGradient gradient=new LinearGradient(0,0,getWidth(),getHeight(),new int[]{Color.TRANSPARENT,color1,Color.TRANSPARENT},new float[]{0f,0.1f,0.9f}, Shader.TileMode.CLAMP);
            shapeBorderPaint.setShader(gradient);
        }else {

        }
    }

    /**
     * 对设置的颜色值调整颜色的亮度和饱和度
     *
     * @param color
     * @param txtPaint
     * @param txtStrokePaint
     */
    private void textPaintColor(@ColorInt int color, Paint txtPaint, Paint txtStrokePaint) {

        if (isClick) {
            if (color == Color.BLACK) {
                txtPaint.setColor(Color.WHITE);
                txtStrokePaint.setColor(color);
            } else {
                float[] hsv = {0, 0, 0};
                txtStrokePaint.setColor(color);
                Color.colorToHSV(color, hsv);
                Log.d(TAG, "hsv=");
                StringBuilder string = new StringBuilder();
                for (float f :
                        hsv) {
                    string.append(f);
                    string.append("");
                }
                Log.d(TAG, "" + string.toString());
                int color1 = Color.HSVToColor(new float[]{hsv[0], 0.10f, 1f});
                txtPaint.setColor(color1);
            }
        } else {
            txtPaint.setColor(getResources().getColor(R.color.txtGrayColor));
            txtStrokePaint.setColor(getResources().getColor(R.color.txtStrokeGrayColor));

        }

    }

    public void setBtnChildAngle(float btnChildAngle) {
        this.btnChildAngle = btnChildAngle;
    }

    public float getBtnChildAngle() {
        return btnChildAngle;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = measureWidth(widthMeasureSpec);
        int height = measureHeight(heightMeasureSpec);

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        drawShape(canvas);
        drawImg(canvas);
        if (!isClick)
            drawShadowLayer(canvas);

        drawTxt(canvas);
        drawShapeBorder(canvas);
    }

    private Bitmap obtainBitmap(int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), imgRes, options);
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
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imgRes, options);

        bitmap = Bitmap.createScaledBitmap(bitmap, reqWidth, reqHeight, true);
        return bitmap;
    }

    private Bitmap obtainTransparentImg(int reqWidth, int reqHeight, LinearGradient shader) {
        Bitmap bitmap = obtainBitmap(reqWidth, reqHeight);
        Bitmap groupbBitmap = Bitmap.createBitmap(reqWidth, reqHeight, bitmap.getConfig());
        Canvas gCanvas = new Canvas(groupbBitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setFilterBitmap(true);
        gCanvas.drawBitmap(bitmap, 0, 0, null);
        paint.setShader(shader);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        gCanvas.drawRect(0, 0, reqWidth, reqHeight, paint);

        return groupbBitmap;
    }

    private void drawImg(Canvas canvas) {
        int reqWidth = 2 * getWidth() / 3;
        int reqHeight = getHeight();
        if (imgBtm == null) {
            if (btnIndex == LEFT) {
                imgBtm = obtainTransparentImg(reqWidth, reqHeight, new LinearGradient(0, reqHeight, reqWidth, reqHeight, Color.BLACK, Color.TRANSPARENT, Shader.TileMode.CLAMP));
            } else if (btnIndex == RIGHT) {
                imgBtm = obtainTransparentImg(reqWidth, reqHeight, new LinearGradient(0, reqHeight, reqWidth, reqHeight, Color.TRANSPARENT, Color.BLACK, Shader.TileMode.CLAMP));
            }
        }
        int save = canvas.save();

//        LinearGradient gradient=new LinearGradient()

        if (btnIndex == LEFT) {
            canvas.drawBitmap(imgBtm, 0, 0, imgPaint);

        } else if (btnIndex == RIGHT) {
            canvas.drawBitmap(imgBtm, getWidth() - reqWidth, 0, shapePaint);
        }


        canvas.restoreToCount(save);
    }

    // 三角形区域的偏移量
    private double thirdAngleDistance = 0;

    private void drawShape(Canvas canvas) {
        int save = canvas.save();

        if (shapePath == null) {
            shapePath = new Path();
            shapeBorderPath = new Path();
            Log.d(TAG, "left = " + getLeft() + "  right = " + getRight() + " top = " + getTop() + " bottom = " + getBottom());
            float distance1 = getLeft();
            double cot = Math.cos(btnChildAngle / 180 * Math.PI) / Math.sin(btnChildAngle / 180 * Math.PI);
            double distance = cot * getHeight();
            thirdAngleDistance = Math.abs(distance);
            if (btnChildAngle == 90 || btnChildAngle == 0) {
                distance = 0;
            }
            if (distance > 0) {
                switch (btnIndex) {
                    case LEFT: {
                        Log.d(TAG, "getRight() - distance : " + Math.round(getRight() - Math.abs(distance)) + "  " + (getRight() - Math.abs(distance)));
                        shapePath.moveTo(getLeft() - distance1, getTop());
                        shapePath.lineTo((float) (Math.round(getRight() - Math.abs(distance))), getTop());
                        shapePath.lineTo(getRight(), getBottom());
                        shapePath.lineTo(getLeft(), getBottom());

                        shapeBorderPath.moveTo(getLeft() - distance1, getTop()+borderWidth/2);
                        shapeBorderPath.lineTo((float) (Math.round(getRight() - Math.abs(distance))-borderWidth/2), getTop()+borderWidth/2);
                        shapeBorderPath.lineTo(getRight()-borderWidth/2, getBottom());
                    }
                    break;
                    case RIGHT: {
                        shapePath.moveTo((float) distance, getTop());
                        shapePath.lineTo(getRight() - distance1, getTop());
                        shapePath.lineTo(getRight() - distance1, getBottom());
                        shapePath.lineTo(getLeft() - distance1, getBottom());

                        shapeBorderPath.moveTo(getLeft() - distance1+borderWidth/2, getBottom());
                        shapeBorderPath.lineTo((float) distance+borderWidth/2, getTop()+borderWidth/2);
                        shapeBorderPath.lineTo(getRight()- distance1, getTop()+borderWidth/2);
                    }
                    break;
                }

            } else if (distance < 0) {
                switch (btnIndex) {
                    case LEFT: {
                        shapePath.moveTo(getLeft(), getTop());
                        shapePath.lineTo(getRight(), getTop());
                        shapePath.lineTo((float) (getRight() + distance), getBottom());
                        shapePath.lineTo(getLeft(), getBottom());

                        shapeBorderPath.moveTo(getLeft(), getTop()+borderWidth/2);
                        shapeBorderPath.lineTo(getRight()-borderWidth/2, getTop()+borderWidth/2);
                        shapeBorderPath.lineTo((float) (getRight() + distance-borderWidth/2), getBottom());
                    }
                    break;
                    case RIGHT: {
                        shapePath.moveTo(getLeft() - distance1, getTop());
                        shapePath.lineTo(getRight() - distance1, getTop());
                        shapePath.lineTo(getRight() - distance1, getBottom());
                        Log.d(TAG, "((ViewGroup) getParent()).getChildAt(0).getRight() = " + ((ViewGroup) getParent()).getChildAt(0).getRight());
                        //                shapePath.lineTo(((ViewGroup) getParent()).getChildAt(0).getRight()-distance1, getBottom());
                        shapePath.lineTo((float) (-distance), getBottom());

                        shapeBorderPath.moveTo((float) -distance+borderWidth/2, getBottom());
                        shapeBorderPath.lineTo(getLeft()- distance1+borderWidth/2, getTop()+borderWidth/2);
                        shapeBorderPath.lineTo(getRight()- distance1, getTop()+borderWidth/2);
                    }
                    break;
                }

            } else {
                shapePath.lineTo(getRight() - distance1, getTop());
                shapePath.lineTo(getRight() - distance1, getBottom());
                shapePath.lineTo(getLeft() - distance1, getBottom());
                switch (btnIndex){
                    case LEFT:{
                        shapeBorderPath.moveTo(getLeft(),getTop());
                        shapeBorderPath.lineTo(getRight()-distance1,getTop());
                        shapeBorderPath.lineTo(getRight()-distance1,getBottom());
                    }
                    break;
                    case RIGHT:{
                        shapeBorderPath.moveTo(getLeft()-distance1,getBottom());
                        shapeBorderPath.lineTo(getLeft()-distance1,getTop());
                        shapeBorderPath.lineTo(getRight()-distance1,getTop());
                    }
                    break;
                }
            }
            shapePath.close();
        }

        canvas.drawPath(shapePath, shapePaint);
        canvas.restoreToCount(save);
    }

    private static final String shadowLayerColor = "#92454545";

    private void drawShadowLayer(Canvas canvas) {
        int i = canvas.save();
        shapePaint.setColor(Color.parseColor(shadowLayerColor));
        canvas.drawPath(shapePath, shapePaint);
        canvas.restoreToCount(i);
        shapePaint.setColor(btnColor);
    }

    private int borderWidth=10;

    private void drawShapeBorder(Canvas canvas) {
        if (shapeBorderPaint==null){
            shapeBorderPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
            shapeBorderPaint.setStrokeJoin(Paint.Join.ROUND);
            shapeBorderPaint.setStyle(Paint.Style.STROKE);
            shapeBorderPaint.setStrokeWidth(borderWidth);
            borderPaintColor(btnColor,shapeBorderPaint);
        }

        if (shapeBorderPath != null) {
            shapeBorderPaint.setShadowLayer(5,5,5,Color.parseColor("#F7E413"));

            canvas.drawPath(shapeBorderPath,shapeBorderPaint);
        }
    }

    private void drawTxt(Canvas canvas) {
        Log.d(TAG, "thirdAngleDistance: " + thirdAngleDistance);
        if (staticLayout == null) {
            staticLayout = new StaticLayout(btnTxt, textPaint, (int) (getWidth() / 3 - thirdAngleDistance / 2), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        }
        if (staticStrokeLayout == null) {
            staticStrokeLayout = new StaticLayout(btnTxt, textStrokePaint, (int) (getWidth() / 3 - thirdAngleDistance / 2), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        }
        int save = canvas.save();
        if (btnIndex == LEFT) {
            canvas.translate((float) (2 * getWidth() / 3 - thirdAngleDistance), getHeight() / 4);
        } else if (btnIndex == RIGHT) {
            canvas.translate((float) (getWidth() / 6 + thirdAngleDistance), 3 * getHeight() / 4 - staticLayout.getHeight());
        }

        staticStrokeLayout.draw(canvas);
        staticLayout.draw(canvas);


        canvas.restoreToCount(save);
    }

    private int measureWidth(int widthMeasureSpec) {
        int width = 0;

        int mode = MeasureSpec.getMode(widthMeasureSpec);
        int size = MeasureSpec.getSize(widthMeasureSpec);

        switch (mode) {
            case MeasureSpec.EXACTLY: {
                width = size;
            }
            break;
            case MeasureSpec.AT_MOST: {
                width = size / ((ViewGroup) getParent()).getChildCount();
            }
        }

        return width;
    }

    private int measureHeight(int heightMeasureSpec) {
        int height = 0;

        int mode = MeasureSpec.getMode(heightMeasureSpec);
        int size = MeasureSpec.getSize(heightMeasureSpec);

        switch (mode) {
            case MeasureSpec.EXACTLY: {
                height = size;
            }
            break;
            case MeasureSpec.AT_MOST: {

            }
            break;
        }

        return height;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:{
                isClick=true;
                invalidate();
            }
            break;
            case MotionEvent.ACTION_UP:{
                isClick=false;
                invalidate();
            }
            break;
        }

        return super.onTouchEvent(event);
    }
}
