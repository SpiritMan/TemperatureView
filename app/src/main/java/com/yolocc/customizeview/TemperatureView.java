package com.yolocc.customizeview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

/**
 */
public class TemperatureView extends View {

    private static int MIN_SIZE = 200;
    private static final int OFFSET = 5;

    private int progressPaintWith = 15;

    private int mSize; // 控件大小

    private int progressRadius; //进度弧的半径;

    private int scaleArcRadius; // 刻度弧的半径

    private int mTicksCount = 40; // 40条刻度(包括长短)

    private int mLongTicksHeight = dp2px(10); // 长刻度
    private int mShortTicksHeight = dp2px(5); // 短刻度

    private int pointRadius = dp2px(17); // 中心圆半径

    private float currentTemp;

    private Paint leftPointerPaint; // 表针左半部分
    private Paint rightPointerPaint; // 表针右半部分

    private Paint outCirclePaint, progressPaint, progressTextPaint, scaleArcPaint, scalePaint, pointPaint, pointerCirclePaint;

    private Paint panelTextPaint; // 表盘文字

    public TemperatureView(Context context) {
        super(context);
        initPaint();
    }

    public TemperatureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    public TemperatureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    private void initPaint() {
        outCirclePaint = new Paint();
        outCirclePaint.setColor(Color.WHITE);
        outCirclePaint.setAntiAlias(true);

        progressPaint = new Paint();
        progressPaint.setAntiAlias(true);
        progressPaint.setStrokeWidth(dp2px(progressPaintWith));
        progressPaint.setStyle(Paint.Style.STROKE);

        progressTextPaint = new Paint();
        progressTextPaint.setAntiAlias(true);
        progressTextPaint.setColor(Color.BLACK);

        scaleArcPaint = new Paint();
        scaleArcPaint.setAntiAlias(true);
        scaleArcPaint.setColor(Color.BLACK);
        scaleArcPaint.setStyle(Paint.Style.STROKE);
        scaleArcPaint.setStrokeWidth(4);
        scaleArcPaint.setStrokeCap(Paint.Cap.ROUND);

        panelTextPaint = new Paint();
        panelTextPaint.setAntiAlias(true);
        panelTextPaint.setColor(Color.BLACK);

        scalePaint = new Paint();
        scalePaint.setAntiAlias(true);
        scalePaint.setStrokeWidth(5);
        scalePaint.setStyle(Paint.Style.STROKE);
        panelTextPaint.setColor(Color.BLACK);

        pointPaint = new Paint();
        pointPaint.setAntiAlias(true);
        pointPaint.setStyle(Paint.Style.FILL);
        pointPaint.setColor(Color.GRAY);

        leftPointerPaint = new Paint();
        leftPointerPaint.setAntiAlias(true);
        leftPointerPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        leftPointerPaint.setColor(getResources().getColor(R.color.leftPointer));
//        添加阴影
        leftPointerPaint.setShadowLayer(4, 2, 2, 0x80000000);

        rightPointerPaint = new Paint();
        rightPointerPaint.setAntiAlias(true);
        rightPointerPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        rightPointerPaint.setColor(getResources().getColor(R.color.rightPointer));

        pointerCirclePaint = new Paint();
        pointerCirclePaint.setAntiAlias(true);
        pointerCirclePaint.setColor(Color.GRAY);
        pointerCirclePaint.setStyle(Paint.Style.FILL);
        pointerCirclePaint.setDither(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = startMeasure(widthMeasureSpec);
        int height = startMeasure(heightMeasureSpec);

        mSize = Math.min(width, height);
        setMeasuredDimension(mSize, mSize);
    }


    /**
     * 根据不同的模式,设置控件的大小;
     *
     * @param whSpec
     *
     * @return 最后控件的大小
     */
    private int startMeasure(int whSpec) {
        int result;
        int size = MeasureSpec.getSize(whSpec);
        int mode = MeasureSpec.getMode(whSpec);
        if (mode == MeasureSpec.EXACTLY) {
            if (size < dp2px(MIN_SIZE)) {
                result = dp2px(MIN_SIZE);
            } else {
                result = size;
            }
        } else {
            result = dp2px(MIN_SIZE);
        }
        return result;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //将画布移到中央
        canvas.translate(mSize / 2, mSize / 2);

        drawOutCircle(canvas);

        drawProgress(canvas);

        drawProgressText(canvas);

        // 绘制表盘
        drawPanel(canvas);
    }

    /**
     * 画外圆
     *
     * @param canvas 画布
     */
    private void drawOutCircle(Canvas canvas) {
        canvas.drawCircle(0, 0, mSize / 2, outCirclePaint);
        canvas.save();
    }

    /**
     * 画进度弧
     *
     * @param canvas 画布
     */
    private void drawProgress(Canvas canvas) {
        canvas.save();
        progressRadius = mSize / 2 - dp2px(progressPaintWith + 1) / 2;
        RectF rectF = new RectF(-progressRadius, -progressRadius, progressRadius, progressRadius);

        //设置为圆角
        progressPaint.setStrokeCap(Paint.Cap.ROUND);
        progressPaint.setColor(Color.GREEN);
        canvas.drawArc(rectF, 150, 120, false, progressPaint);

        progressPaint.setColor(Color.RED);
        canvas.drawArc(rectF, 330, 60, false, progressPaint);

        progressPaint.setStrokeCap(Paint.Cap.BUTT);
        progressPaint.setColor(Color.YELLOW);
        canvas.drawArc(rectF, 270, 60, false, progressPaint);

        canvas.restore();
    }

    private void drawProgressText(Canvas canvas) {
        canvas.save();
        String normal = "正常";
        String warn = "预警";
        String danger = "警告";

        progressTextPaint.setTextSize(sp2px(12));
//        旋转坐标系
        canvas.rotate(-60, 0, 0);
        canvas.drawText(normal, -dp2px(12), -progressRadius + 12, progressTextPaint);
        canvas.rotate(90, 0, 0);
        canvas.drawText(warn, -dp2px(12), -progressRadius + 12, progressTextPaint);
        canvas.rotate(60, 0, 0);
        canvas.drawText(danger, -dp2px(12), -progressRadius + 12, progressTextPaint);
        canvas.restore();
    }

    private void drawPanel(Canvas canvas) {
        //画刻度圆弧
        drawScaleArc(canvas);
        // 画中间圆
        drawInPoint(canvas);
        // 画指针
        drawPointer(canvas);

        drawPanelText(canvas);
    }

    /**
     * 画刻度表
     *
     * @param canvas 画布
     */
    private void drawScaleArc(Canvas canvas) {
        // 刻度弧紧靠进度弧
        scaleArcRadius = progressRadius - dp2px(progressPaintWith / 2);
        canvas.save();

        RectF rectF = new RectF(-scaleArcRadius, -scaleArcRadius, scaleArcRadius, scaleArcRadius);
        canvas.drawArc(rectF, 150, 240, false, scaleArcPaint);

        float mAngle = 240f / mTicksCount;

        panelTextPaint.setTextSize(sp2px(15));

        String scale;
        for (int i = 0; i <= mTicksCount / 2; i++) {
            if (i % 5 == 0) {
                scale = 20 + i + "";
                float scaleWidth = panelTextPaint.measureText(scale);
                canvas.drawLine(0, -scaleArcRadius, 0, -scaleArcRadius + mLongTicksHeight, scalePaint);
                canvas.drawText(scale, -scaleWidth / 2, -scaleArcRadius + mLongTicksHeight + dp2px(15), panelTextPaint);
            } else {
                canvas.drawLine(0, -scaleArcRadius, 0, -scaleArcRadius + mShortTicksHeight, scalePaint);
            }
            // 旋转坐标系
            canvas.rotate(mAngle, 0, 0);
        }

        canvas.rotate(-mAngle * mTicksCount / 2 - 6, 0, 0);
        for (int i = 0; i <= mTicksCount / 2; i++) {
            if (i % 5 == 0) {
                scale = 20 - i + "";
                float scaleWidth = panelTextPaint.measureText(scale);
                canvas.drawLine(0, -scaleArcRadius, 0, -scaleArcRadius + mLongTicksHeight, scalePaint);
                canvas.drawText(scale, -scaleWidth / 2, -scaleArcRadius + mLongTicksHeight + dp2px(15), panelTextPaint);
            } else {
                canvas.drawLine(0, -scaleArcRadius, 0, -scaleArcRadius + mShortTicksHeight, scalePaint);
            }
            // 旋转坐标系
            canvas.rotate(-mAngle, 0, 0);
        }
        // 画布回正
        canvas.rotate(-mAngle * mTicksCount / 2 + 6, 0, 0);
        canvas.restore();
    }

    /**
     * 画中间圆
     *
     * @param canvas 画布
     */
    private void drawInPoint(Canvas canvas) {
        canvas.save();
        canvas.drawCircle(0, 0, pointRadius, pointPaint);
        canvas.restore();
    }

    /**
     * 画指针
     *
     * @param canvas 画布
     */
    private void drawPointer(Canvas canvas) {
        RectF rectf = new RectF(-pointRadius / 2, -pointRadius / 2, pointRadius / 2, pointRadius / 2);
        canvas.save();

        canvas.rotate(60, 0, 0);
        float angle = currentTemp * 6.0f;
        canvas.rotate(angle, 0, 0);

        Path leftPointerPath = new Path();
        leftPointerPath.moveTo(pointRadius / 2, 0);
        leftPointerPath.addArc(rectf, 0, 360);
        leftPointerPath.lineTo(0, scaleArcRadius - mLongTicksHeight - dp2px(OFFSET) - dp2px(15));
        leftPointerPath.lineTo(-pointRadius / 2, 0);
        leftPointerPath.close();

        Path rightPointerPath = new Path();
        rightPointerPath.moveTo(-pointRadius / 2, 0);
        rightPointerPath.addArc(rectf, 0, -180);
        rightPointerPath.lineTo(0, scaleArcRadius - mLongTicksHeight - dp2px(OFFSET) - dp2px(15));
        rightPointerPath.lineTo(0, pointRadius / 2);
        rightPointerPath.close();

        Path circlePath = new Path();
        circlePath.addCircle(0, 0, pointRadius / 4, Path.Direction.CW);

        canvas.drawPath(leftPointerPath, leftPointerPaint);
        canvas.drawPath(rightPointerPath, rightPointerPaint);
        canvas.drawPath(circlePath, pointerCirclePaint);
        canvas.restore();

    }

    private void drawPanelText(Canvas canvas) {
        canvas.save();
        String text = "当前温度";
        float length = panelTextPaint.measureText(text);
        panelTextPaint.setTextSize(sp2px(15));
        canvas.drawText(text, -length / 2, scaleArcRadius / 2 + dp2px(20), panelTextPaint);
        String temp = currentTemp + "℃";
        float tempTextLength = panelTextPaint.measureText(temp);
        canvas.drawText(temp, -tempTextLength / 2, scaleArcRadius, panelTextPaint);
        canvas.restore();
    }

    public void setCurrentTemp(float currentTemp) {
        if (currentTemp < 0) {
            currentTemp = 0;
        } else if (currentTemp > 40) {
            currentTemp = 40;
        }
        this.currentTemp = currentTemp;
        postInvalidate();
    }

    /**
     * 将 dp 转换为 px
     *
     * @param dp 需转换数
     *
     * @return 返回转换结果
     */
    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    private int sp2px(int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getResources().getDisplayMetrics());
    }
}

