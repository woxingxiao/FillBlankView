package com.repo.xw.fillblankviewdemo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.text.Editable;
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
    private int mBlankSpace;
    private int mBlankSolidColor;
    private int mBlankStrokeColor;
    private int mBlankStrokeWidth;
    private int mBlankCornerRadius;
    private boolean isHideText;
    private int mDotSize;
    private int mDotColor;
    private int mTextMatchedColor;
    private int mTextNotMatchedColor;

    private Paint mPaintBlank;
    private Paint mPaintText;
    private Paint mPaintDot;
    private RectF[] mRectFs;
    private Rect mRect;
    private RectF mRectBig;
    private Rect mTextRect;
    private String mPrefixStr;
    private String mSuffixStr;
    private String[] mBlankStrings;
    private int dotCount;

    private OnMobileMatchedListener mListener;
    private String originalText;

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
        mBlankSpace = a.getDimensionPixelSize(R.styleable.FillBlankView_blankSpace, 0);
        mBlankSolidColor = a.getColor(R.styleable.FillBlankView_blankSolidColor, getDrawingCacheBackgroundColor());
        mBlankStrokeColor = a.getColor(R.styleable.FillBlankView_blankStrokeColor, getCurrentTextColor());
        mBlankStrokeWidth = a.getDimensionPixelSize(R.styleable.FillBlankView_blankStrokeWidth, 1);
        mBlankCornerRadius = a.getDimensionPixelSize(R.styleable.FillBlankView_blankCornerRadius, 0);
        isHideText = a.getBoolean(R.styleable.FillBlankView_hideText, false);
        mDotSize = a.getDimensionPixelSize(R.styleable.FillBlankView_dotSize, dp2px(4));
        mDotColor = a.getColor(R.styleable.FillBlankView_dotColor, getCurrentTextColor());
        mTextMatchedColor = a.getColor(R.styleable.FillBlankView_textMatchedColor, getCurrentTextColor());
        mTextNotMatchedColor = a.getColor(R.styleable.FillBlankView_textNotMatchedColor, getCurrentTextColor());
        a.recycle();

        initObjects();
    }

    private void initObjects() {
        setCursorVisible(false);

        if (mBlankNum <= 0) {
            throw new IllegalArgumentException("the 'blankNum' must be greater than zero !");
        }
        mBlankStrings = new String[mBlankNum];
        for (int i = 0; i < mBlankStrings.length; i++)
            mBlankStrings[i] = "";

        mPaintBlank = new Paint();
        mPaintBlank.setAntiAlias(true);

        mTextRect = new Rect();
        mPaintText = new Paint();
        mPaintText.setAntiAlias(true);
        mPaintText.setColor(getCurrentTextColor());
        mPaintText.setTextSize(getTextSize());

        mPaintDot = new Paint();
        mPaintDot.setAntiAlias(true);

        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > mBlankNum) {
                    getText().delete(s.length() - 1, s.length());
                    dotCount = mBlankNum;
                    return;
                }
                dotCount = s.length();

                mPaintText.setColor(getCurrentTextColor());
                for (int i = 0; i < mBlankNum; i++) {
                    if (i < s.length()) {
                        mBlankStrings[i] = s.subSequence(i, i + 1).toString();
                    } else {
                        mBlankStrings[i] = "";
                    }
                }

                if (s.length() == mBlankNum && mListener != null) {
                    if (getFilledText().equals(originalText)) {
                        mPaintText.setColor(mTextMatchedColor);
                        mListener.matched(true, originalText);
                    } else {
                        mPaintText.setColor(mTextNotMatchedColor);
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
        int viewWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        int column;
        if (mPrefixStr == null && mSuffixStr == null) {
            column = mBlankNum;
        } else if (mPrefixStr != null && mSuffixStr != null) {
            column = mBlankNum + 2;
        } else {
            column = mBlankNum + 1;
        }
        mRectFs = new RectF[column];
        int width = (viewWidth - mBlankSpace * (column - 1)) / column;
        float top = getPaddingTop();
        float bottom = getHeight() - getPaddingBottom();
        float left;
        float right;
        for (int i = 0; i < mRectFs.length; i++) {
            if (i == 0) {
                left = getPaddingLeft();
                right = width + getPaddingLeft();
            } else {
                left = width * i + mBlankSpace * i + getPaddingLeft();
                right = width * (i + 1) + mBlankSpace * i + getPaddingLeft();
            }

            mRectFs[i] = new RectF(left, top, right, bottom);
        }
        mRect = new Rect(0, 0, getWidth(), getHeight());

        if (mBlankSpace == 0) {
            mRectBig = new RectF(getPaddingLeft(), getPaddingTop(),
                    getWidth() - getPaddingRight(), getHeight() - getPaddingBottom());
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (getBackground() == null)
            canvas.drawColor(Color.WHITE);

        for (int i = 0; i < mRectFs.length; i++) {
            if (i == 0 && mPrefixStr != null)
                continue;
            if (i == mRectFs.length - 1 && mSuffixStr != null)
                break;

            mPaintBlank.setStyle(Paint.Style.FILL);
            mPaintBlank.setColor(mBlankSolidColor);
            canvas.drawRoundRect(mRectFs[i], mBlankCornerRadius, mBlankCornerRadius, mPaintBlank);

            if (mBlankStrokeWidth > 0) {
                mPaintBlank.setStyle(Paint.Style.STROKE);
                mPaintBlank.setColor(mBlankStrokeColor);
                mPaintBlank.setStrokeWidth(mBlankStrokeWidth);
                if (mBlankSpace > 0 && mBlankSolidColor != mBlankStrokeColor) {
                    canvas.drawRoundRect(mRectFs[i], mBlankCornerRadius, mBlankCornerRadius, mPaintBlank);
                } else if (mBlankSpace == 0) {
                    mPaintBlank.setAlpha(110);
                    mPaintBlank.setStrokeWidth(mBlankStrokeWidth / 2.0f);
                    canvas.drawLine(mRectFs[i].right, mRectFs[i].top, mRectFs[i].right, mRectFs[i].bottom, mPaintBlank);

                    if (i == mRectFs.length - 2) {
                        mPaintBlank.setAlpha(255);
                        mPaintBlank.setStrokeWidth(mBlankStrokeWidth);
                        canvas.drawRoundRect(mRectBig, mBlankCornerRadius, mBlankCornerRadius, mPaintBlank);
                        break;
                    }
                }
            }
        }

        Paint.FontMetricsInt fontMetrics = mPaintText.getFontMetricsInt();
        int textCenterY = (mRect.bottom + mRect.top - fontMetrics.bottom - fontMetrics.top) / 2;

        if (mPrefixStr != null) {
            mPaintText.setTextAlign(Paint.Align.RIGHT);
            mPaintText.getTextBounds(mPrefixStr, 0, mPrefixStr.length(), mTextRect);
            canvas.drawText(mPrefixStr, mRectFs[0].centerX(), textCenterY, mPaintText);
        }

        mPaintDot.setColor(mDotColor);
        mPaintText.setTextAlign(Paint.Align.CENTER);
        for (int i = 0; i < mBlankNum; i++) {
            if (isHideText && dotCount > 0) {
                if (i + 1 > dotCount)
                    break;
                canvas.drawCircle(mRectFs[i].centerX(), mRectFs[i].centerY(), mDotSize, mPaintDot);
            } else {
                mPaintText.getTextBounds(mBlankStrings[i], 0, mBlankStrings[i].length(), mTextRect);
                canvas.drawText(mBlankStrings[i], mRectFs[i].centerX(), textCenterY, mPaintText);
            }
        }

        if (mSuffixStr != null) {
            mPaintText.setTextAlign(Paint.Align.LEFT);
            mPaintText.getTextBounds(mSuffixStr, 0, mSuffixStr.length(), mTextRect);
            canvas.drawText(mSuffixStr, mRectFs[mRectFs.length - 1].centerX(), textCenterY, mPaintText);
        }
    }

    public String getOriginalText() {
        return originalText;
    }

    public void setOriginalText(@NonNull String originalText) {
        this.originalText = originalText;
        if (originalText.isEmpty())
            return;
        invalidate();
    }

    public void setOriginalText(@NonNull String originalText, int prefixLength, int suffixLength) {
        this.originalText = originalText;
        if (originalText.isEmpty())
            return;
        if (originalText.length() <= prefixLength + suffixLength) {
            throw new IllegalArgumentException("the sum of prefixLength and suffixLength must be less " +
                    "than length of originalText");
        }
        mPrefixStr = originalText.substring(0, prefixLength);
        mSuffixStr = originalText.substring(originalText.length() - suffixLength, originalText.length());

        initSizes();
        invalidate();
    }

    public String getFilledText() {
        StringBuilder builder = new StringBuilder();
        if (mPrefixStr != null)
            builder.append(mPrefixStr);
        for (String s : mBlankStrings)
            builder.append(s);
        if (mSuffixStr != null)
            builder.append(mSuffixStr);
        return builder.toString();
    }

    public int getBlankNum() {
        return mBlankNum;
    }

    public void setBlankNum(int blankNum) {
        mBlankNum = blankNum;
        if (mBlankNum <= 0) {
            throw new IllegalArgumentException("the 'blankNum' must be greater than zero !");
        }
        mBlankStrings = new String[mBlankNum];
        for (int i = 0; i < mBlankStrings.length; i++)
            mBlankStrings[i] = "";
        initSizes();
        invalidate();
    }

    public int getBlankSpace() {
        return mBlankSpace;
    }

    public void setBlankSpace(int blankSpace) {
        mBlankSpace = blankSpace;
        if (mBlankSpace < 0) {
            throw new IllegalArgumentException("the 'blankSpace' can be less than zero !");
        }
        initSizes();
        invalidate();
    }

    public int getBlankSolidColor() {
        return mBlankSolidColor;
    }

    public void setBlankSolidColor(int blankSolidColor) {
        mBlankSolidColor = blankSolidColor;
        invalidate();
    }

    public int getBlankStrokeColor() {
        return mBlankStrokeColor;
    }

    public void setBlankStrokeColor(int blankStrokeColor) {
        mBlankStrokeColor = blankStrokeColor;
        invalidate();
    }

    public int getBlankStrokeWidth() {
        return mBlankStrokeWidth;
    }

    public void setBlankStrokeWidth(int blankStrokeWidth) {
        mBlankStrokeWidth = blankStrokeWidth;
        invalidate();
    }

    public int getBlankCornerRadius() {
        return mBlankCornerRadius;
    }

    public void setBlankCornerRadius(int blankCornerRadius) {
        mBlankCornerRadius = blankCornerRadius;
        invalidate();
    }

    public boolean isHideText() {
        return isHideText;
    }

    public void setHideText(boolean isHideText) {
        this.isHideText = isHideText;
        invalidate();
    }

    public int getDotSize() {
        return mDotSize;
    }

    public void setDotSize(int dotSize) {
        mDotSize = dotSize;
        invalidate();
    }

    public int getDotColor() {
        return mDotColor;
    }

    public void setDotColor(int dotColor) {
        mDotColor = dotColor;
        invalidate();
    }

    public int getTextMatchedColor() {
        return mTextMatchedColor;
    }

    public void setTextMatchedColor(int textMatchedColor) {
        mTextMatchedColor = textMatchedColor;
    }

    public int getTextNotMatchedColor() {
        return mTextNotMatchedColor;
    }

    public void setTextNotMatchedColor(int textNotMatchedColor) {
        mTextNotMatchedColor = textNotMatchedColor;
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
        void matched(boolean isMatched, String originalText);
    }
}
