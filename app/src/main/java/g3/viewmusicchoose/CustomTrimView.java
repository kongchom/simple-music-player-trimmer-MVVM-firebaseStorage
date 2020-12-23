package g3.viewmusicchoose;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

public class CustomTrimView extends View {
    public interface OnTrimListener {
        void onTrim(int start, int end);
    }

    public interface OnSetTimeOfStickerListener {
        void onChange(float seconds);
    }

    private OnSetTimeOfStickerListener onSetTimeOfStickerListener;

    public void setOnSetTimeOfStickerListener(OnSetTimeOfStickerListener onSetTimeOfStickerListener) {
        this.onSetTimeOfStickerListener = onSetTimeOfStickerListener;
    }

    private static final String TAG = "CustomTrimView";
    private OnTrimListener onTrimListener;
    private Paint mPaint;
    private Paint mPaintText;
    private Bitmap mBitmapStart;
    private Bitmap mBitmapEnd;
    private Control mControlStart;
    private Control mControlEnd;
    private final Rect mRectS = new Rect();
    private final Rect mRectE = new Rect();
    private float minStartX = 0;
    private float minEndX = 0;
    private float maxStartX = 0;
    private float maxEndX = 0;
    private int mDuration = 0;
    private int mDurationS = 0;
    private int mDurationE = 0;
    private int colorGray;
    private int colorPink;
    private float mPadding;

    public CustomTrimView(Context context) {
        this(context, null);
    }

    public CustomTrimView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomTrimView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void setOnTrimListener(OnTrimListener listener) {
        this.onTrimListener = listener;
    }

    private void init(Context context) {
        Resources res = context.getResources();
        float density = res.getDisplayMetrics().density;

        float mBackgroundWidth = 3 * density;
        float sizeText = 14 * density;
//        Lo.d(TAG, "init: " + sizeText);

        mPadding = 10 * density;

        float scale = 1.5f;

        mBitmapStart = BitmapFactory.decodeResource(getResources(), R.drawable.icon_circledot);
        mBitmapStart = Bitmap.createScaledBitmap(mBitmapStart, (int) (mBitmapStart.getWidth() * scale), (int) (mBitmapStart.getHeight() * scale), false);
        mBitmapEnd = BitmapFactory.decodeResource(getResources(), R.drawable.icon_circledot);
        mBitmapEnd = Bitmap.createScaledBitmap(mBitmapEnd, (int) (mBitmapEnd.getWidth() * scale), (int) (mBitmapEnd.getHeight() * scale), false);

        colorGray = ContextCompat.getColor(context, R.color.c_fff);
        colorPink = ContextCompat.getColor(context, R.color.color_seekbar);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(colorGray);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mBackgroundWidth);

        mPaintText = new Paint();
        mPaintText.setTextSize(sizeText);
        mPaintText.setAntiAlias(true);
        mPaintText.setDither(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaintText.setColor(colorGray);
    }

    public void setDuration(int duration) {
//        LogUtils.d("TAGG", "set duration = " + duration);
        this.mDuration = duration;
        mDurationS = 0;
        mDurationE = duration;
        if (mControlStart != null) {
            String s = ConvertDurationUtils.convertDurationText(mDurationS);
            mPaintText.getTextBounds(s, 0, s.length(), mRectS);
            mControlStart.duration = mDurationS;
            mControlStart.x = mPadding + mRectS.width() / 2;
            mControlStart.y = getHeight() / 2 - mBitmapStart.getHeight() / 2;
            mControlStart.type = 0;
        }

        if (mControlEnd != null) {
            String s = ConvertDurationUtils.convertDurationText(mDurationE);
            mPaintText.getTextBounds(s, 0, s.length(), mRectE);
            mControlEnd.duration = duration;
            mControlEnd.x = getWidth() - mBitmapEnd.getWidth() - mRectE.width() / 2 - mPadding;
            mControlEnd.y = getHeight() / 2 - mBitmapEnd.getHeight() / 2;
            mControlEnd.type = 1;
        }
        invalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float width = getWidth();
        float height = getHeight();
        drawTrimMp3(canvas, width, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//        if (mControlStart == null) {
        mDurationS = 0;
        String s = ConvertDurationUtils.convertDurationText(mDurationS);
        mPaintText.getTextBounds(s, 0, s.length(), mRectS);
        mControlStart = new Control(mDurationS, mPadding + mRectS.width() / 2, h / 2 - mBitmapStart.getHeight() / 2, mBitmapStart, 0);
//        }

//        if (mControlEnd == null) {
        String s1 = ConvertDurationUtils.convertDurationText(mDurationE);
        mPaintText.getTextBounds(s1, 0, s1.length(), mRectE);
        mControlEnd = new Control(mDurationE, w - mBitmapEnd.getWidth() - mRectE.width() / 2 - mPadding, h / 2 - mBitmapEnd.getHeight() / 2, mBitmapEnd, 1);
//        }


        minStartX = mControlStart.x;
        maxEndX = mControlEnd.x;
        super.onSizeChanged(w, h, oldw, oldh);
//        Lo.d(TAG, "w h= " + w + ";" + h);
//        Lo.d(TAG, "old W, H =" + oldw + ";" + oldh);

    }

    private void drawTrimMp3(Canvas canvas, float width, float height) {
        mPaint.setColor(colorGray);
        canvas.drawLine(mRectS.width() / 2 + mPadding, height / 2 + 20, width - mRectE.width() / 2 - mPadding, height / 2 + 20, mPaint);

        mPaint.setColor(colorPink);
        canvas.drawLine(mControlStart.x, height / 2 + 20, mControlEnd.x, height / 2 + 20, mPaint);

        canvas.drawBitmap(mControlEnd.bitmap, mControlEnd.x, mControlEnd.y + 20, null);
        canvas.drawBitmap(mControlStart.bitmap, mControlStart.x, mControlStart.y + 20, null);

        canvas.drawText(mControlStart.changeSecondToDuration(), minStartX - mRectS.width() / 4, mControlStart.y, mPaintText);
        canvas.drawText(mControlEnd.changeSecondToDuration(), maxEndX - mRectE.width() / 4, mControlEnd.y, mPaintText);
    }

    private boolean isTouchInside(float x, float y, Control control) {
        return (x < control.x + control.bitmap.getWidth() + mPadding && x > control.x - mPadding) && (y < control.y + control.bitmap.getHeight() + 20 && y > control.y - 20);
    }

    private float mX = 0;
    private boolean mIsTouchStart;
    private boolean mIsTouchEnd;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mX = event.getX();
                if (isTouchInside(event.getX(), event.getY(), mControlStart)) {
                    mIsTouchStart = true;
                    maxStartX = mControlEnd.x - mControlEnd.bitmap.getWidth();
//                    Lo.d(TAG, "onTouchEvent: touch" + event.getX() + " : " + event.getY());
                }

                if (isTouchInside(event.getX(), event.getY(), mControlEnd)) {
                    mIsTouchEnd = true;
                    minEndX = mControlStart.x + mControlStart.bitmap.getWidth();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = event.getX() - mX;
                if (mIsTouchStart && mIsTouchEnd) {
                    if (dx > 0) {
                        mIsTouchEnd = true;
                        mIsTouchStart = false;
                    } else if (dx < 0) {
                        mIsTouchStart = true;
                        mIsTouchEnd = false;
                    }
                }

                if (mIsTouchStart) {
                    float mCurrentS = mControlStart.x;
                    mControlStart.x += dx;

                    if (mControlStart.x <= minStartX) {
                        mControlStart.x = minStartX;
                    }
                    if (mControlStart.x >= maxStartX) {
                        mControlStart.x = maxStartX;
                    }

                    float dS = (mControlStart.x - mCurrentS) * mDuration / (maxEndX - minStartX);
                    mControlStart.duration += dS;

                    mX = event.getX();
                    if (onSetTimeOfStickerListener != null) {
                        onSetTimeOfStickerListener.onChange(mControlStart.duration);

                    }

                }

                if (mIsTouchEnd) {
                    float mCurrentE = mControlEnd.x;
                    mControlEnd.x += dx;


                    if (mControlEnd.x <= minEndX) {
                        mControlEnd.x = minEndX;
                    } else if (mControlEnd.x >= maxEndX) {
                        mControlEnd.x = maxEndX;
                    }

                    float dS = (mControlEnd.x - mCurrentE) * mDuration / (maxEndX - minStartX);
                    mControlEnd.duration += dS;

                    mX = event.getX();
                    if (onSetTimeOfStickerListener != null) {
                        onSetTimeOfStickerListener.onChange(mControlEnd.duration);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (onTrimListener != null && (mIsTouchStart || mIsTouchEnd)) {
                    onTrimListener.onTrim(getTimeStart(), getTimeEnd());
                }
                mIsTouchStart = false;
                mIsTouchEnd = false;
                break;
        }
        invalidate();
        return true;
    }

    private Matrix getMatrixBitmap() {
        Matrix matrix = new Matrix();
        matrix.setScale(2, 2);
        return matrix;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

//        int desiredWidth = 100;
//        int desiredHeight = 80;
//
//        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
//        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
//        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
//        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
//
//        int width;
//        int height;
//
//        //Measure Width
//        if (widthMode == MeasureSpec.EXACTLY) {
//            //Must be this size
//            width = widthSize;
//            ;
//        } else if (widthMode == MeasureSpec.AT_MOST) {
//            //Can't be bigger than...
//            width = Math.min(desiredWidth, widthSize);
//        } else {
//            //Be whatever you want
//            width = desiredWidth;
//        }
//
//        //Measure Height
//        if (heightMode == MeasureSpec.EXACTLY) {
//            //Must be this size
//            height = Math.max(desiredHeight, heightSize);
//            ;
//        } else if (heightMode == MeasureSpec.AT_MOST) {
//            //Can't be bigger than...
//            height = Math.min(desiredHeight, heightSize);
//        } else {
//            //Be whatever you want
//            height = desiredHeight;
//        }
//
//
//        //MUST CALL THIS
//        setMeasuredDimension(width, height);
//        Lo.d(TAG, "onMeasure: " + getWidth() + " : " + getHeight());
    }


    private static class Control {
        float duration;
        float x;
        float y;
        final Bitmap bitmap;
        int type;

        Control(float duration, float x, float y, Bitmap bitmap, int type) {
            this.duration = duration;
            this.x = x;
            this.y = y;
            this.bitmap = bitmap;
            this.type = type;
        }

        String changeSecondToDuration() {
            int time = Math.round(duration);
            if (time > 0) {
                int minute = time / 60;
                int second = time % 60;
                return (minute >= 10 ? minute : "0" + minute) + ":" + (second >= 10 ? second : "0" + second);
            }
            return "00:00";
        }
    }

    public float changeDurationToPosition(int duration, int time) {
        return duration * (maxEndX - minStartX) / time;
    }

    public int getTimeStart() {
        return Math.round(mControlStart.duration);
    }

    public void setTime(int durationS, int durationE) {
        if (mControlStart == null || mControlEnd == null) return;
        mControlStart.duration = durationS;
        float x = mControlStart.x;
        mControlStart.x = x + changeDurationToPosition(durationS, mDuration);
        mControlEnd.duration = durationE;
        mControlEnd.x = x + changeDurationToPosition(durationE, mDuration);
        invalidate();
    }

    public float getTimeStartf() {
        return mControlStart.duration;
    }

    public float getTimeEndf() {
        return mControlEnd.duration;
    }

    public int getTimeEnd() {
        return Math.round(mControlEnd.duration);
    }

    public void reStart() {
        if (mControlStart != null) {
            mControlStart.duration = 0;
        }
        if (mControlEnd != null) {
            mControlEnd.duration = 0;
        }
        invalidate();
    }
}
