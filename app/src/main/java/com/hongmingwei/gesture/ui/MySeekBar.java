package com.hongmingwei.gesture.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.hongmingwei.gesture.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 可选择SeeoBar
 */
public class MySeekBar extends View {

    /**
     * TAG
     */
    private static final String TAG = MySeekBar.class.getSimpleName();

    /**
     * Params
     */
    //设置间隔
    private int LEFT_THE_OFFSET;
    //左右间隔
    private int MARGIN_LEFT_RIGHT;
    private int PADDING_BOTTOM;
    private int RCET_HEIGHT;

    private Context mContext;
    private int mWidth; //控件宽度
    private int mHeight; //控件高度
    private int mColor = R.color.c_356ce0; //主题颜色,通过外部方法设置
    private int mSzie = 4; //设置分我几段
    private List<String> mTextLists = new ArrayList<>();
    private List<String> mTexts = new ArrayList<>();
    //开始坐标轴
    private int mStartX = 0;
    private int mStartY;
    //是否在拖拽的区域
    private boolean isFlag;
    int width= 0;
    //长方形的宽高
    private int mRectHeight = 35;
    //标记当前在那个位置
    private int mLocation;
    //判断按下抬起
    private boolean isDownUpFlag = false;
    //三角形宽度
    private int mPathWidth = 5;
    //显示框圆角
    private int mRectRadrus = 10;
    private OnTouchEventListener mListener;

    public MySeekBar(Context context) {
        super(context);
        init(context);
    }

    public MySeekBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MySeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    /**
     * 初始化
     * @param context
     */
    private void init(Context context){
        mContext = context;
        LEFT_THE_OFFSET = ConvertUtils.dp2px(mContext,30);
        MARGIN_LEFT_RIGHT = ConvertUtils.dp2px(mContext,10);
        PADDING_BOTTOM = ConvertUtils.dp2px(mContext,5);
        RCET_HEIGHT = ConvertUtils.dp2px(mContext,30);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (action){
            case MotionEvent.ACTION_DOWN:
                mStartX = x;
                mStartY = y;
                if (((mHeight / 3 * 2) + ConvertUtils.dp2px(mContext,30)) > mStartY &&
                        ((mHeight / 3 * 2) - ConvertUtils.dp2px(mContext,30)) < mStartY){
                    isFlag = true;
                    isDownUpFlag = true;
                    if ((width / 2 + LEFT_THE_OFFSET) > mStartX){
                        mLocation = 0;
                    } else if ((width / 2 + LEFT_THE_OFFSET < mStartX) && (width / 2 + width + LEFT_THE_OFFSET) > mStartX){
                        mLocation = 1;
                    } else if ((width / 2 + width + LEFT_THE_OFFSET < mStartX) && (width / 2 + width * 2 + LEFT_THE_OFFSET) > mStartX){
                        mLocation = 2;
                    } else if ((width / 2 + width * 2 + LEFT_THE_OFFSET < mStartX) && (width / 2 + width * 3 + LEFT_THE_OFFSET) > mStartX){
                        mLocation = 3;
                    } else if ((width / 2 + width * 3 + LEFT_THE_OFFSET < mStartX) && (width / 2 + width * 4 + LEFT_THE_OFFSET) > mStartX){
                        mLocation = 4;
                    }
                    invalidate();
                } else {
                    isFlag = false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (isFlag){
                    mStartX = (int) event.getX();
                    if (mStartX - LEFT_THE_OFFSET < 0){
                        mStartX = 0;
                    }
                    if (mStartX > mWidth + LEFT_THE_OFFSET){
                        mStartX = mWidth + LEFT_THE_OFFSET;
                    }

                    for (int i = 0; i <= mSzie; i++){
                        if ((width * i + LEFT_THE_OFFSET) <= mStartX){
                            mLocation = i;
                        }
                    }
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                if (isFlag){
                    isDownUpFlag = false;
                    if ((width / 2 + LEFT_THE_OFFSET) > mStartX){
                        mStartX = 0;
                        mLocation = 0;
                    } else if ((width / 2 + LEFT_THE_OFFSET < mStartX) && (width / 2 + width + LEFT_THE_OFFSET) > mStartX){
                        mStartX = width + LEFT_THE_OFFSET;
                        mLocation = 1;
                    } else if ((width / 2 + width + LEFT_THE_OFFSET < mStartX) && (width / 2 + width * 2 + LEFT_THE_OFFSET) > mStartX){
                        mStartX = width * 2  + LEFT_THE_OFFSET;
                        mLocation = 2;
                    } else if ((width / 2 + width * 2 + LEFT_THE_OFFSET < mStartX) && (width / 2 + width * 3 + LEFT_THE_OFFSET) > mStartX){
                        mStartX = width * 3 + LEFT_THE_OFFSET;
                        mLocation = 3;
                    } else if ((width / 2 + width * 3 + LEFT_THE_OFFSET < mStartX) && (width / 2 + width * 4 + LEFT_THE_OFFSET) > mStartX){
                        mStartX = mWidth + LEFT_THE_OFFSET;
                        mLocation = 4;
                    }
                    invalidate();
                }
                break;
        }
        mListener.getPosition(mLocation);
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth() - ConvertUtils.dp2px(mContext,60);
        mHeight = getMeasuredHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        width = mWidth / mSzie;
        setCanvasLine(canvas);
        setCanvasMobile(canvas);
        if (!ListUtils.isEmpty(mTexts) && !ListUtils.isEmpty(mTextLists)) {
            setCanvasText(canvas);
            setCanvasRcet(canvas);
        }
    }


    /**
     * 绘制显示文字
     * @param canvasRcet
     */
    private void setCanvasRcet(Canvas canvasRcet){
        Paint paintText = new Paint();
        paintText.setAntiAlias(true);
        paintText.setColor(mContext.getResources().getColor(R.color.c_ffffff));
        paintText.setStyle(Paint.Style.FILL);
        if (isDownUpFlag) {
            paintText.setTextSize(55);
            mRectHeight = 40;
            mPathWidth = 7;
        } else {
            paintText.setTextSize(45);
            mRectHeight = 35;
            mPathWidth = 5;
        }

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(mContext.getResources().getColor(mColor));
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(5);

        Path path = new Path();
        float titleWidth = paintText.measureText(mTexts.get(mLocation));
        int height = mHeight / 3 * 2;
        int titleHeight = getTextHeight(paintText);

        if (mStartX < LEFT_THE_OFFSET + (titleWidth - MARGIN_LEFT_RIGHT) / 2){
            RectF rectF = new RectF(LEFT_THE_OFFSET - MARGIN_LEFT_RIGHT, height - RCET_HEIGHT - ConvertUtils.dp2px(mContext,mRectHeight),
                    LEFT_THE_OFFSET + titleWidth, height - RCET_HEIGHT);
            canvasRcet.drawRoundRect(rectF, mRectRadrus, mRectRadrus, paint);
            canvasRcet.drawText(mTexts.get(mLocation), LEFT_THE_OFFSET - MARGIN_LEFT_RIGHT / 2,
                    height - RCET_HEIGHT - titleHeight / 3 * 2, paintText);
        } else if (mStartX > mWidth + LEFT_THE_OFFSET - (titleWidth - MARGIN_LEFT_RIGHT) / 2){
            RectF rectF = new RectF(mWidth + LEFT_THE_OFFSET - titleWidth , height - RCET_HEIGHT - ConvertUtils.dp2px(mContext,mRectHeight),
                    mWidth + LEFT_THE_OFFSET + MARGIN_LEFT_RIGHT, height - RCET_HEIGHT);
            canvasRcet.drawRoundRect(rectF, mRectRadrus, mRectRadrus, paint);
            canvasRcet.drawText(mTexts.get(mLocation), mWidth + LEFT_THE_OFFSET - titleWidth + MARGIN_LEFT_RIGHT / 2,
                    height - RCET_HEIGHT - titleHeight / 3 * 2, paintText);
        } else {
            RectF rectF = new RectF(mStartX - (MARGIN_LEFT_RIGHT + titleWidth) / 2, height - RCET_HEIGHT - ConvertUtils.dp2px(mContext,mRectHeight),
                    mStartX + (MARGIN_LEFT_RIGHT + titleWidth) / 2 , height - RCET_HEIGHT);
            canvasRcet.drawRoundRect(rectF, mRectRadrus, mRectRadrus, paint);
            canvasRcet.drawText(mTexts.get(mLocation), mStartX - titleWidth / 2,
                    height - RCET_HEIGHT - titleHeight / 3 * 2, paintText);
        }
        if (mStartX == 0){
            path.moveTo(LEFT_THE_OFFSET, height - ConvertUtils.dp2px(mContext,15));
            path.lineTo(LEFT_THE_OFFSET - ConvertUtils.dp2px(mContext,mPathWidth), height - RCET_HEIGHT);
            path.lineTo(LEFT_THE_OFFSET + ConvertUtils.dp2px(mContext,mPathWidth), height - RCET_HEIGHT);
        } else {
            path.moveTo(mStartX, height - ConvertUtils.dp2px(mContext,15));
            path.lineTo(mStartX - ConvertUtils.dp2px(mContext,mPathWidth), height - RCET_HEIGHT);
            path.lineTo(mStartX + ConvertUtils.dp2px(mContext,mPathWidth), height - RCET_HEIGHT);
        }
        canvasRcet.drawPath(path, paint);
    }



    /**
     * 绘制滑动圆圈
     * @param canvasMobile
     */
    private void setCanvasMobile(Canvas canvasMobile){
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(mContext.getResources().getColor(R.color.c_ffffff));
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(5);
        int height = mHeight / 3 * 2;
        if (mStartX == 0){
            canvasMobile.drawCircle(LEFT_THE_OFFSET, height, 30, paint);
        } else {
            canvasMobile.drawCircle(mStartX, height, 30, paint);
        }

        paint.setColor(mContext.getResources().getColor(R.color.c_c6c6c6));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        if (mStartX == 0){
            canvasMobile.drawCircle(LEFT_THE_OFFSET , height, 30, paint);
        } else {
            canvasMobile.drawCircle(mStartX, height, 30, paint);
        }
    }


    /**
     * 绘制涨跌幅线
     * @param canvas
     */
    private void setCanvasLine(Canvas canvas){
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(8);
        int height = mHeight / 3 * 2;
        for (int i = 0; i < mSzie; i ++){
            if (mStartX >= ((i + 1) * width + LEFT_THE_OFFSET)){
                paint.setColor(mContext.getResources().getColor(mColor));
                canvas.drawLine((i * width) + LEFT_THE_OFFSET, height, ((i + 1) * width) + LEFT_THE_OFFSET, height, paint);
            } else {
                paint.setColor(mContext.getResources().getColor(R.color.c_eaebf0));
                canvas.drawLine((i * width) + LEFT_THE_OFFSET, height, ((i + 1) * width) + LEFT_THE_OFFSET, height, paint);
            }
            if (mStartX >= (i * width + LEFT_THE_OFFSET)){
                paint.setColor(mContext.getResources().getColor(mColor));
                canvas.drawLine((i * width) + LEFT_THE_OFFSET, height, mStartX, height, paint);
            }
        }

        paint.setColor(mContext.getResources().getColor(R.color.c_ffffff));
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(5);
        for (int i = 0; i < mSzie + 1; i++){
            canvas.drawCircle((i * width) + LEFT_THE_OFFSET, height, 15, paint);
        }

        paint.setColor(mContext.getResources().getColor(R.color.c_c6c6c6));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        for (int i = 0; i < mSzie + 1; i++){
            canvas.drawCircle((i * width) + LEFT_THE_OFFSET, height, 15, paint);
        }
    }


    /**
     * 绘制百分比
     * @param canvasText
     */
    private void setCanvasText(Canvas canvasText){
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(mContext.getResources().getColor(R.color.c_333333));
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(3);
        paint.setTextSize(45);

        for (int i = 0; i < mSzie + 1; i++){
            float titleWidth = paint.measureText(mTextLists.get(i));
            canvasText.drawText(mTextLists.get(i), (i * width) + LEFT_THE_OFFSET - (titleWidth / 2), mHeight - MARGIN_LEFT_RIGHT - PADDING_BOTTOM, paint);
        }
    }

    /**
     * 获取文字高度
     * @param paint
     * @return
     */
    private int getTextHeight(Paint paint) {
        Paint.FontMetrics forFontMetrics = paint.getFontMetrics();
        return (int) (forFontMetrics.descent - forFontMetrics.ascent);
    }


    /**
     * 设置现实涨跌幅范围
     * @param titles
     */
    public void setTitles(List<String> titles, List<String> percents){
        mTexts.clear();
        mTexts.addAll(titles);
        mTextLists.clear();
        mTextLists.addAll(percents);
        invalidate();
    }

    /**
     * 设置主背景的颜色
     * @param color
     */
    public void setColor(int color){
        mColor = color;
        invalidate();
    }



    public interface OnTouchEventListener{
        void getPosition(int position);
    }

    public void setOnTouchEventListener(OnTouchEventListener listener){
        mListener = listener;
    }
}
