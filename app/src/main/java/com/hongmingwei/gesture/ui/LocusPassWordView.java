package com.hongmingwei.gesture.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 自定义手势密码
 */
public class LocusPassWordView extends View {
    /**
     * TAG
     */
    private static final String TAG = LocusPassWordView.class.getSimpleName();

    private float width = 0;  
    private float height = 0;  
  
    //  
    private boolean isCache = false;  
    // 设置画笔
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
  
    //
    private Point[][] mPoints = new Point[3][3];
    //  
    private float dotRadius = 0;
    //  
    private List<Point> sPoints = new ArrayList<>();
    private boolean checking = false;
    //定义画的线存留的时间
    private long CLEAR_TIME = 360;
    //最大连接数
    private int pwdMaxLen = 9;
    //最小连接数
    private int pwdMinLen = 2;
    private boolean isTouch = true;

    //画笔
//    private Paint arrowPaint;   //画箭头
    private Paint linePaint;   //画线
    private Paint selectedPaint;   //选中的
    private Paint errorPaint;   //错误的
    private Paint normalPaint;   //正常的
    private Paint maxPaint;  //小圆的
    private Paint fillPaint; //背景

    //颜色属性
    private int errorColor = 0xffea0945;    //错误的线颜色
    private int selectedColor = 0xff5887dc;    //正确的线的颜色
    private int outterSelectedColor = 0xff5887dc;  //正确的圆圈颜色
    private int outterErrorColor = 0xff901032;    //错误圆圈的颜色
    private int dotColor = 0xff7a7d80;     //一开始的圆圈颜色
    private int outterDotColor = 0xff2e3133;   //里面小圆圈的颜色
    private int paddingColor = 0xff2f4875;  //填充色

    //三参的构造方法
    public LocusPassWordView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);  
    }  
    //两参的构造方法的
    public LocusPassWordView(Context context, AttributeSet attrs) {
        super(context, attrs);  
    }  
    //一参的构造方法
    public LocusPassWordView(Context context) {
        super(context);  
    }  
  
    @Override
    public void onDraw(Canvas canvas) {
        if (!isCache) {  
            initCache();  
        }  
        drawToCanvas(canvas);  
    }  


    private void drawToCanvas(Canvas canvas) {
        boolean inErrorState = false;
        for (int i = 0; i < mPoints.length; i++) {
            for (int j = 0; j < mPoints[i].length; j++) {
                Point p = mPoints[i][j];
                if (p.state == Point.STATE_CHECK) {     //正确
                    selectedPaint.setColor(outterSelectedColor);   //外面大圆圆圈
                    canvas.drawCircle(p.x, p.y, dotRadius, selectedPaint);
                    fillPaint.setColor(paddingColor);
                    canvas.drawCircle(p.x, p.y, dotRadius, fillPaint);
                    maxPaint.setColor(selectedColor);   //里面小圆
                    canvas.drawCircle(p.x, p.y, dotRadius/4, maxPaint);
                } else if (p.state == Point.STATE_CHECK_ERROR) {   //错误
                    inErrorState = true;
                    errorPaint.setColor(outterErrorColor);
                    canvas.drawCircle(p.x, p.y, dotRadius, errorPaint);
                    maxPaint.setColor(errorColor);
                    canvas.drawCircle(p.x, p.y, dotRadius/4, maxPaint);
                } else {    //原始
                    normalPaint.setColor(dotColor);
                    canvas.drawCircle(p.x, p.y, dotRadius, normalPaint);
                    maxPaint.setColor(outterDotColor);
                    canvas.drawCircle(p.x, p.y, dotRadius/4, maxPaint);
                }
            }
        }

        if (inErrorState) {
//            arrowPaint.setColor(errorColor);
            linePaint.setColor(errorColor);
        } else {
//            arrowPaint.setColor(selectedColor);
            linePaint.setColor(selectedColor);
        }

        if (sPoints.size() > 0) {
            int tmpAlpha = mPaint.getAlpha();
            Point tp = sPoints.get(0);
            for (int i = 1; i < sPoints.size(); i++) {
                Point p = sPoints.get(i);
                drawLine(tp, p, canvas, linePaint);
//                drawArrow(canvas, arrowPaint, tp, p, dotRadius/4, 38);  //修改箭头大小
                tp = p;
            }
            if (this.movingNoPoint) {
                drawLine(tp, new Point(moveingX, moveingY, -1), canvas, linePaint);
            }
            mPaint.setAlpha(tmpAlpha);
        }

    }
    //设置线
    private void drawLine(Point start, Point end, Canvas canvas, Paint paint) {
        double d = MathUtil.distance(start.x, start.y, end.x, end.y);
        float rx = (float) ((end.x-start.x) * dotRadius / 4 / d);  
        float ry = (float) ((end.y-start.y) * dotRadius / 4 / d);  
        canvas.drawLine(start.x+rx, start.y+ry, end.x-rx, end.y-ry, paint);  
    }  
    //设置箭头
    private void drawArrow(Canvas canvas, Paint paint, Point start, Point end, float arrowHeight, int angle) {
        double d = MathUtil.distance(start.x, start.y, end.x, end.y);  
        float sin_B = (float) ((end.x - start.x) / d);  
        float cos_B = (float) ((end.y - start.y) / d);  
        float tan_A = (float) Math.tan(Math.toRadians(angle));
        float h = (float) (d - arrowHeight - dotRadius * 1.1);  
        float l = arrowHeight * tan_A;  
        float a = l * sin_B;  
        float b = l * cos_B;  
        float x0 = h * sin_B;  
        float y0 = h * cos_B;  
        float x1 = start.x + (h + arrowHeight) * sin_B;  
        float y1 = start.y + (h + arrowHeight) * cos_B;  
        float x2 = start.x + x0 - b;  
        float y2 = start.y + y0 + a;  
        float x3 = start.x + x0 + b;  
        float y3 = start.y + y0 - a;  
        Path path = new Path();
        path.moveTo(x1, y1);  
        path.lineTo(x2, y2);  
        path.lineTo(x3, y3);  
        path.close();  
        canvas.drawPath(path, paint);  
    }  
  
    private void initCache() {  
        width = this.getWidth();  
        height = this.getHeight();  
        float x = 0;  
        float y = 0;  
  
        if (width > height) {  
            x = (width - height) / 2;  
            width = height;  
        } else {  
            y = (height - width) / 2;  
            height = width;  
        }  
          
        int leftPadding = 15;
        float dotPadding = width / 3 - leftPadding;  
        float middleX = width / 2;
        float middleY = height / 2;
  
        mPoints[0][0] = new Point(x + middleX - dotPadding, y + middleY - dotPadding, 1);  
        mPoints[0][1] = new Point(x + middleX, y + middleY - dotPadding, 2);  
        mPoints[0][2] = new Point(x + middleX + dotPadding, y + middleY - dotPadding, 3);  
        mPoints[1][0] = new Point(x + middleX - dotPadding, y + middleY, 4);  
        mPoints[1][1] = new Point(x + middleX, y + middleY, 5);  
        mPoints[1][2] = new Point(x + middleX + dotPadding, y + middleY, 6);  
        mPoints[2][0] = new Point(x + middleX - dotPadding, y + middleY + dotPadding, 7);  
        mPoints[2][1] = new Point(x + middleX, y + middleY + dotPadding, 8);  
        mPoints[2][2] = new Point(x + middleX + dotPadding, y + middleY + dotPadding, 9);  
          
        Log.d("jerome", "canvas width:" + width);
        dotRadius = width / 10;  
        isCache = true;  
          
        initPaints();  
    }  
    //开始回话
    private void initPaints() {
        //箭头
//        arrowPaint = new Paint();
//        arrowPaint.setColor(selectedColor);
//        arrowPaint.setStyle(Style.FILL);
//        arrowPaint.setAntiAlias(true);

        //线
        linePaint = new Paint();
        linePaint.setColor(selectedColor);  
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setAntiAlias(true);  
        linePaint.setStrokeWidth(dotRadius / 6);

        //正确
        selectedPaint = new Paint();
        selectedPaint.setStyle(Style.STROKE);
        selectedPaint.setAntiAlias(true);
        selectedPaint.setStrokeWidth(dotRadius / 12);

        //错误
        errorPaint = new Paint();
        errorPaint.setStyle(Style.STROKE);
        errorPaint.setAntiAlias(true);
        errorPaint.setStrokeWidth(dotRadius / 12);
        //原始
        normalPaint = new Paint();
        normalPaint.setStyle(Style.STROKE);
        normalPaint.setAntiAlias(true);
        normalPaint.setStrokeWidth(dotRadius / 12);

        //小圆
        maxPaint = new Paint();
        maxPaint.setStyle(Style.FILL);
        maxPaint.setAntiAlias(true);

        fillPaint = new Paint();
        fillPaint.setStyle(Style.FILL);
        fillPaint.setAntiAlias(true);
    }
  
    /** 
     *
     * @param index 
     * @return 
     */  
    public int[] getArrayIndex(int index) {  
        int[] ai = new int[2];  
        ai[0] = index / 3;  
        ai[1] = index % 3;  
        return ai;  
    }  
  
    /** 
     * @param x 
     * @param y 
     * @return 
     */  
    private Point checkSelectPoint(float x, float y) {
        for (int i = 0; i < mPoints.length; i++) {  
            for (int j = 0; j < mPoints[i].length; j++) {
                if (mPoints[i][j]!=null) {
                    Point p = mPoints[i][j];
                    if (MathUtil.checkInRound(p.x, p.y, dotRadius, x, y)) {
                        return p;
                    }
                }
            }  
        }  
        return null;  
    }  
  
    /** 
     *  重置
     */  
    private void reset() {  
        for (Point p : sPoints) {  
            p.state = Point.STATE_NORMAL;  
        }  
        sPoints.clear();  
        this.enableTouch();  
    }  
  
    /** 
     * 重新绘制
     *  
     * @param p 
     * @return 
     */  
    private int crossPoint(Point p) {  
        // 重置
        if (sPoints.contains(p)) {  
            if (sPoints.size() > 2) {  
                //
                if (sPoints.get(sPoints.size() - 1).index != p.index) {  
                    return 2;  
                }  
            }  
            return 1; //
        } else {  
            return 0; //  
        }  
    }  
  
    /** 
     * 
     *  
     * @param point 
     */  
    private void addPoint(Point point) {  
        if (sPoints.size() > 0) {  
            Point lastPoint = sPoints.get(sPoints.size() - 1);  
            int dx = Math.abs(lastPoint.getColNum() - point.getColNum());
            int dy = Math.abs(lastPoint.getRowNum() - point.getRowNum());
            if ((dx > 1 || dy > 1) && (dx == 0 || dy == 0 || dx == dy)) {
//          if ((dx > 1 || dy > 1) && (dx != 2 * dy) && (dy != 2 * dx)) {
                int middleIndex = (point.index + lastPoint.index) / 2 - 1;  
                Point middlePoint = mPoints[middleIndex / 3][middleIndex % 3];  
                if (middlePoint.state != Point.STATE_CHECK) {  
                    middlePoint.state = Point.STATE_CHECK;  
                    sPoints.add(middlePoint);  
                }  
            }  
        }  
        this.sPoints.add(point);  
    }  
  
    /**
     * 判断连接数
     * @param 
     * @return 
     */  
    private String toPointString() {
        if (sPoints.size() >= pwdMinLen && sPoints.size() <= pwdMaxLen) {  
            StringBuffer sf = new StringBuffer();
            for (Point p : sPoints) {  
                sf.append(p.index);  
            }  
            return sf.toString();  
        } else {  
            return "";  
        }  
    }  
  
    boolean movingNoPoint = false;  
    float moveingX, moveingY;


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        //
        if (!isTouch) {
            return false;
        }

        movingNoPoint = false;

        float ex = 0;
        ex = event.getX();
        float ey = 0;
        ey = event.getY();
        boolean isFinish = false;
        boolean redraw = false;
        Point p = null;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: //开始触摸
                //
                if (task != null) {
                    task.cancel();
                    task = null;
                    Log.d("task", "touch cancel()");
                }
                //
                reset();
                p = checkSelectPoint(ex, ey);
                if (p != null) {
                    checking = true;
                }
                break;
            case MotionEvent.ACTION_MOVE: //移动
                if (checking) {
                    p = checkSelectPoint(ex, ey);
                    if (p == null) {
                        movingNoPoint = true;
                        moveingX = ex;
                        moveingY = ey;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:  //离开
                p = checkSelectPoint(ex, ey);
                checking = false;
                isFinish = true;
                break;
        }
        if (!isFinish && checking && p != null) {

            int rk = crossPoint(p);
            if (rk == 2) //
            {
                // reset();
                // checking = false;

                movingNoPoint = true;
                moveingX = ex;
                moveingY = ey;

                redraw = true;
            } else if (rk == 0) //
            {
                p.state = Point.STATE_CHECK;
                addPoint(p);
                redraw = true;
            }
            // rk == 1

        }

        //
        if (redraw) {

        }
        if (isFinish) {
            //当大于一的时候
            if (this.sPoints.size() == 1) {
                this.reset();
            } else if(sPoints.size() == 0 ){

            } else if (sPoints.size() < pwdMinLen || sPoints.size() > pwdMaxLen) {
                // mCompleteListener.onPasswordTooMin(sPoints.size());
                error();
                clearPassword();
            } else if (mCompleteListener != null) {
//                this.disableTouch();
                clearPassword(1500);
                mCompleteListener.onComplete(toPointString());
            }
        }
        this.postInvalidate();
        return true;
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        //
//        if (!isTouch) {
//            return false;
//        }
//
//        movingNoPoint = false;
//
//        float ex = 0;
//        ex = event.getX();
//        float ey = 0;
//        ey = event.getY();
//        boolean isFinish = false;
//        boolean redraw = false;
//        Point p = null;
//        switch (event.getAction()) {
//        case MotionEvent.ACTION_DOWN: //开始触摸
//            //
//            if (task != null) {
//                task.cancel();
//                task = null;
//                Log.d("task", "touch cancel()");
//            }
//            //
//            reset();
//            p = checkSelectPoint(ex, ey);
//            if (p != null) {
//                checking = true;
//            }
//            break;
//        case MotionEvent.ACTION_MOVE: //移动
//            if (checking) {
//                p = checkSelectPoint(ex, ey);
//                if (p == null) {
//                    movingNoPoint = true;
//                    moveingX = ex;
//                    moveingY = ey;
//                }
//            }
//            break;
//        case MotionEvent.ACTION_UP:  //离开
//            p = checkSelectPoint(ex, ey);
//            checking = false;
//            isFinish = true;
//            break;
//        }
//        if (!isFinish && checking && p != null) {
//
//            int rk = crossPoint(p);
//            if (rk == 2) //
//            {
//                // reset();
//                // checking = false;
//
//                movingNoPoint = true;
//                moveingX = ex;
//                moveingY = ey;
//
//                redraw = true;
//            } else if (rk == 0) //
//            {
//                p.state = Point.STATE_CHECK;
//                addPoint(p);
//                redraw = true;
//            }
//            // rk == 1
//
//        }
//
//        //
//        if (redraw) {
//
//        }
//        if (isFinish) {
//            //当大于一的时候
//            if (this.sPoints.size() == 1) {
//                this.reset();
//            } else if(sPoints.size() == 0 ){
//
//            } else if (sPoints.size() < pwdMinLen || sPoints.size() > pwdMaxLen) {
//                // mCompleteListener.onPasswordTooMin(sPoints.size());
//                error();
//                clearPassword();
//            } else if (mCompleteListener != null) {
////                this.disableTouch();
//                clearPassword(1500);
//                mCompleteListener.onComplete(toPointString());
//            }
//        }
//        this.postInvalidate();
//        return true;
//    }

    /** 
     *  设置错误的颜色
     */  
    public void error() {
        for (Point p : sPoints) {  
            p.state = Point.STATE_CHECK_ERROR;  
        }  
    }  
  
    public void markError() {  
        markError(CLEAR_TIME);  
    }  
  
    public void markError(final long time) {  
        for (Point p : sPoints) {  
            p.state = Point.STATE_CHECK_ERROR;  
        }  
        this.clearPassword(time);  
    }  
  
    public void enableTouch() {  
        isTouch = true;  
    }  
  
    public void disableTouch() {  
        isTouch = false;  
    }  
  
    private Timer timer = new Timer();
    private TimerTask task = null;

    /**
     * 去掉画布
     */
    public void clearPassword() {  
        clearPassword(CLEAR_TIME);  
    }  
  
    public void clearPassword(final long time) {  
        if (time > 1) {  
            if (task != null) {  
                task.cancel();  
                Log.d("task", "clearPassword cancel()");
            }  
            postInvalidate();  
            task = new TimerTask() {
                public void run() {  
                    reset();  
                    postInvalidate();  
                }  
            };  
            Log.d("task", "clearPassword schedule(" + time + ")");
            timer.schedule(task, time);  
        } else {  
            reset();  
            postInvalidate();  
        }  
  
    }  
  
    //  
    private OnCompleteListener mCompleteListener;  
  
    /** 
     * @param mCompleteListener 
     */  
    public void setOnCompleteListener(OnCompleteListener mCompleteListener) {  
        this.mCompleteListener = mCompleteListener;  
    }  
  
    public interface OnCompleteListener {  
          
        public void onComplete(String password);
    }  
}  