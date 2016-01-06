package com.repo.xw.fillblankviewdemo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.EditText;

/**
 * FillBlankView
 * Created by woxingxiao on 2016/01/06.
 */
public class FillBlankView extends EditText {

    private int mBlankNum;
    private int mBlankMargin;
    private int mBlankSolidColor;
    private int mBlankStrokeColor;
    private int mBlankStrokeWidth;
    private int mBlankCornerRadius;
    private int mDotSize;
    private int mDotColor;

    private Paint mPaintBg;
    private Paint mPaintText;
    private RectF mRectF0, mRectF1, mRectF2, mRectF3;
    private Rect mRect;
    private Rect mTextRect;
    private String mPrefixStr = "";
    private String mSuffixStr = "";
    private String mBlankStr0 = "";
    private String mBlankStr1 = "";
    private String mBlankStr2 = "";
    private String mBlankStr3 = "";
    private int mMargin;

    private OnMobileMatchedListener mListener;

    private String mobile;

    public FillBlankView(Context context) {
        this(context, null);
    }

    public FillBlankView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FillBlankView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FillBlankView, defStyleAttr, 0);
        mBlankNum = a.getInteger(R.styleable.FillBlankView_blankNum, 6);
        mBlankMargin = a.getDimensionPixelSize(R.styleable.FillBlankView_blankMargin, 0);
        mBlankSolidColor = a.getColor(R.styleable.FillBlankView_blankSolidColor, Color.parseColor("#B6B6B6"));
        mBlankStrokeColor = a.getColor(R.styleable.FillBlankView_blankStrokeColor, Color.parseColor("#B6B6B6"));
        mBlankStrokeWidth = a.getDimensionPixelSize(R.styleable.FillBlankView_blankStrokeWidth, 0);
        mBlankCornerRadius = a.getDimensionPixelSize(R.styleable.FillBlankView_blankCornerRadius, 0);
        mDotSize = a.getDimensionPixelSize(R.styleable.FillBlankView_dotSize, dp2px(3));
        mDotColor = a.getColor(R.styleable.FillBlankView_dotColor, Color.parseColor("#212121"));
        a.recycle();

        initObjects();
    }

    private void initObjects() {
        setInputType(InputType.TYPE_CLASS_NUMBER);
        setCursorVisible(false);

        mPaintBg = new Paint();
        mPaintBg.setAntiAlias(true);
        mPaintBg.setColor(Color.parseColor("#B6B6B6"));

        mTextRect = new Rect();
        mPaintText = new Paint();
        mPaintText.setAntiAlias(true);
        mPaintText.setColor(Color.parseColor("#212121"));
        mPaintText.setTextSize(dp2px(22));
        mPaintText.getTextBounds(mPrefixStr, 0, mPrefixStr.length(), mTextRect);

        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 4)
                    return;

                mPaintText.setColor(Color.parseColor("#212121"));
                switch (s.length()) {
                    case 0:
                        mBlankStr0 = mBlankStr1 = mBlankStr2 = mBlankStr3 = "";
                        break;
                    case 1:
                        mBlankStr0 = s.toString();
                        mBlankStr1 = mBlankStr2 = mBlankStr3 = "";
                        break;
                    case 2:
                        mBlankStr1 = s.subSequence(1, 2).toString();
                        mBlankStr2 = mBlankStr3 = "";
                        break;
                    case 3:
                        mBlankStr2 = s.subSequence(2, 3).toString();
                        mBlankStr3 = "";
                        break;
                    case 4:
                        mBlankStr3 = s.subSequence(3, 4).toString();
                        break;
                }

                if (s.length() == 4 && mListener != null) {
                    if (getFilledMobile().equals(mobile)) {
                        mPaintText.setColor(Color.parseColor("#4CAF50"));
                        mListener.matched(true, mobile);
                    } else {
                        mPaintText.setColor(Color.parseColor("#F44336"));
                        mListener.matched(false, null);
                    }
                }

                invalidate();
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        initSizes();
    }

    private void initSizes() {
        int viewWidth = getWidth();
        int viewHeight = getHeight();
        mMargin = dp2px(10);
        int width = (viewWidth - mMargin * 7) / 6;
        int height = viewHeight - mMargin * 2;
        mRectF0 = new RectF(width + mMargin * 2, mMargin, (mMargin + width) * 2, mMargin + height);
        mRectF1 = new RectF(width * 2 + mMargin * 3, mMargin, (mMargin + width) * 3, mMargin + height);
        mRectF2 = new RectF(width * 3 + mMargin * 4, mMargin, (mMargin + width) * 4, mMargin + height);
        mRectF3 = new RectF(width * 4 + mMargin * 5, mMargin, (mMargin + width) * 5, mMargin + height);
        mRect = new Rect(0, 0, getWidth(), getHeight());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(Color.WHITE);

        canvas.drawRoundRect(mRectF0, dp2px(3), dp2px(3), mPaintBg);
        canvas.drawRoundRect(mRectF1, dp2px(3), dp2px(3), mPaintBg);
        canvas.drawRoundRect(mRectF2, dp2px(3), dp2px(3), mPaintBg);
        canvas.drawRoundRect(mRectF3, dp2px(3), dp2px(3), mPaintBg);

        Paint.FontMetricsInt fontMetrics = mPaintText.getFontMetricsInt();
        int textCenterY = (mRect.bottom + mRect.top - fontMetrics.bottom - fontMetrics.top) / 2;

        mPaintText.setTextAlign(Paint.Align.RIGHT);
        mPaintText.getTextBounds(mPrefixStr, 0, mPrefixStr.length(), mTextRect);
        canvas.drawText(mPrefixStr, mRectF0.left - mMargin, textCenterY, mPaintText);

        mPaintText.setTextAlign(Paint.Align.CENTER);
        mPaintText.getTextBounds(mBlankStr0, 0, mBlankStr0.length(), mTextRect);
        canvas.drawText(mBlankStr0, mRectF0.centerX(), textCenterY, mPaintText);

        mPaintText.getTextBounds(mBlankStr1, 0, mBlankStr1.length(), mTextRect);
        canvas.drawText(mBlankStr1, mRectF1.centerX(), textCenterY, mPaintText);

        mPaintText.getTextBounds(mBlankStr2, 0, mBlankStr2.length(), mTextRect);
        canvas.drawText(mBlankStr2, mRectF2.centerX(), textCenterY, mPaintText);

        mPaintText.getTextBounds(mBlankStr3, 0, mBlankStr3.length(), mTextRect);
        canvas.drawText(mBlankStr3, mRectF3.centerX(), textCenterY, mPaintText);

        mPaintText.setTextAlign(Paint.Align.LEFT);
        mPaintText.getTextBounds(mSuffixStr, 0, mSuffixStr.length(), mTextRect);
        canvas.drawText(mSuffixStr, mRectF3.right + mMargin, textCenterY, mPaintText);
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
        if (mobile == null || mobile.isEmpty())
            return;
        mPrefixStr = mobile.substring(0, 3);
        mSuffixStr = mobile.substring(mobile.length() - 4, mobile.length());
        invalidate();
    }

    public String getFilledMobile() {
        StringBuilder builder = new StringBuilder();
        builder.append(mPrefixStr).append(mBlankStr0).append(mBlankStr1)
                .append(mBlankStr2).append(mBlankStr3).append(mSuffixStr);
        return builder.toString();
    }

    public OnMobileMatchedListener getOnMobileMatchedListener() {
        return mListener;
    }

    public void setOnMobileMatchedListener(OnMobileMatchedListener listener) {
        mListener = listener;
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    private int sp2px(int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getResources().getDisplayMetrics());
    }

    public interface OnMobileMatchedListener {
        void matched(boolean isMatched, String mobile);
    }
}
