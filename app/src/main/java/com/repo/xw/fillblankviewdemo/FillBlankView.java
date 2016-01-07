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
    private boolean isHideText;
    private int mDotSize;
    private int mDotColor;
    private int mTextMatchedColor;
    private int mTextNotMatchedColor;

    private Paint mPaintBlank;
    private Paint mPaintText;
    private RectF mRectF0, mRectF1, mRectF2, mRectF3;
    private RectF[] mRectFs;
    private Rect mRect;
    private Rect mTextRect;
    private String mPrefixStr;
    private String mSuffixStr;
    private String mBlankStr0 = "";
    private String mBlankStr1 = "";
    private String mBlankStr2 = "";
    private String mBlankStr3 = "";
    private String[] mBlankStrings;

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
        mBlankMargin = a.getDimensionPixelSize(R.styleable.FillBlankView_blankMargin, 0);
        mBlankSolidColor = a.getColor(R.styleable.FillBlankView_blankSolidColor, Color.parseColor("#B6B6B6"));
        mBlankStrokeColor = a.getColor(R.styleable.FillBlankView_blankStrokeColor, getCurrentTextColor());
        mBlankStrokeWidth = a.getDimensionPixelSize(R.styleable.FillBlankView_blankStrokeWidth, 0);
        mBlankCornerRadius = a.getDimensionPixelSize(R.styleable.FillBlankView_blankCornerRadius, 0);
        isHideText = a.getBoolean(R.styleable.FillBlankView_blankCornerRadius, false);
        mDotSize = a.getDimensionPixelSize(R.styleable.FillBlankView_dotSize, dp2px(3));
        mDotColor = a.getColor(R.styleable.FillBlankView_dotColor, getCurrentTextColor());
        mTextMatchedColor = a.getColor(R.styleable.FillBlankView_textMatchedColor, getCurrentTextColor());
        mTextNotMatchedColor = a.getColor(R.styleable.FillBlankView_textNotMatchedColor, getCurrentTextColor());
        a.recycle();

        initObjects();
    }

    private void initObjects() {
        setInputType(InputType.TYPE_CLASS_NUMBER);
        setCursorVisible(false);

        if (mBlankNum <= 0) {
            throw new IllegalArgumentException("the 'blankNum' must greater than zero !");
        }
        mRectFs = new RectF[mBlankNum];
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
                if (s.length() > mBlankNum)
                    return;

                mPaintText.setColor(getCurrentTextColor());
                for (int i = 0; i < mBlankNum; i++) {
                    if (i < s.length()) {
                        mBlankStrings[i] = s.subSequence(i, i + 1).toString();
                    } else {
                        mBlankStrings[i] = "";
                    }
                }
//                switch (s.length()) {
//                    case 0:
//                        mBlankStr0 = mBlankStr1 = mBlankStr2 = mBlankStr3 = "";
//                        break;
//                    case 1:
//                        mBlankStr0 = s.toString();
//                        mBlankStr1 = mBlankStr2 = mBlankStr3 = "";
//                        break;
//                    case 2:
//                        mBlankStr1 = s.subSequence(1, 2).toString();
//                        mBlankStr2 = mBlankStr3 = "";
//                        break;
//                    case 3:
//                        mBlankStr2 = s.subSequence(2, 3).toString();
//                        mBlankStr3 = "";
//                        break;
//                    case 4:
//                        mBlankStr3 = s.subSequence(3, 4).toString();
//                        break;
//                }

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
        int viewHeight = getHeight() - getPaddingTop() - getPaddingBottom();
        int column;
        if (mPrefixStr == null && mSuffixStr == null) {
            column = mBlankNum;
        } else if (mPrefixStr != null && mSuffixStr != null) {
            column = mBlankNum + 2;
        } else {
            column = mBlankNum + 1;
        }
        int width = (viewWidth - mBlankMargin * (column + 1)) / column;
        int height = viewHeight - mBlankMargin * 2;
        for (int i = 0; i < mRectFs.length; i++) {
            mRectFs[i] = new RectF(
                    width * (i + 1) + mBlankMargin * (i + 2),
                    mBlankMargin,
                    (mBlankMargin + width) * (i + 2),
                    mBlankMargin + height
            );
        }
//        mRectF0 = new RectF(width + mBlankMargin * 2, mBlankMargin, (mBlankMargin + width) * 2, mBlankMargin + height);
//        mRectF1 = new RectF(width * 2 + mBlankMargin * 3, mBlankMargin, (mBlankMargin + width) * 3, mBlankMargin + height);
//        mRectF2 = new RectF(width * 3 + mBlankMargin * 4, mBlankMargin, (mBlankMargin + width) * 4, mBlankMargin + height);
//        mRectF3 = new RectF(width * 4 + mBlankMargin * 5, mBlankMargin, (mBlankMargin + width) * 5, mBlankMargin + height);
        mRect = new Rect(0, 0, getWidth(), getHeight());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (getBackground() == null)
            canvas.drawColor(Color.WHITE);

        for (RectF rectF : mRectFs) {
            canvas.drawRoundRect(rectF, mBlankCornerRadius, mBlankCornerRadius, mPaintBlank);
        }
//        canvas.drawRoundRect(mRectF0, dp2px(3), dp2px(3), mPaintBlank);
//        canvas.drawRoundRect(mRectF1, dp2px(3), dp2px(3), mPaintBlank);
//        canvas.drawRoundRect(mRectF2, dp2px(3), dp2px(3), mPaintBlank);
//        canvas.drawRoundRect(mRectF3, dp2px(3), dp2px(3), mPaintBlank);

        Paint.FontMetricsInt fontMetrics = mPaintText.getFontMetricsInt();
        int textCenterY = (mRect.bottom + mRect.top - fontMetrics.bottom - fontMetrics.top) / 2;

        mPaintText.setTextAlign(Paint.Align.RIGHT);
        mPaintText.getTextBounds(mPrefixStr, 0, mPrefixStr.length(), mTextRect);
        canvas.drawText(mPrefixStr, mRectF0.left - mBlankMargin, textCenterY, mPaintText);

        mPaintText.setTextAlign(Paint.Align.CENTER);
        for (int i = 0; i < mBlankNum; i++) {
            mPaintText.getTextBounds(mBlankStr0, 0, mBlankStr0.length(), mTextRect);
            canvas.drawText(mBlankStrings[i], mRectFs[i].centerX(), textCenterY, mPaintText);
        }
//        mPaintText.getTextBounds(mBlankStr0, 0, mBlankStr0.length(), mTextRect);
//        canvas.drawText(mBlankStr0, mRectF0.centerX(), textCenterY, mPaintText);
//
//        mPaintText.getTextBounds(mBlankStr1, 0, mBlankStr1.length(), mTextRect);
//        canvas.drawText(mBlankStr1, mRectF1.centerX(), textCenterY, mPaintText);
//
//        mPaintText.getTextBounds(mBlankStr2, 0, mBlankStr2.length(), mTextRect);
//        canvas.drawText(mBlankStr2, mRectF2.centerX(), textCenterY, mPaintText);
//
//        mPaintText.getTextBounds(mBlankStr3, 0, mBlankStr3.length(), mTextRect);
//        canvas.drawText(mBlankStr3, mRectF3.centerX(), textCenterY, mPaintText);

        mPaintText.setTextAlign(Paint.Align.LEFT);
        mPaintText.getTextBounds(mSuffixStr, 0, mSuffixStr.length(), mTextRect);
        canvas.drawText(mSuffixStr, mRectF3.right + mBlankMargin, textCenterY, mPaintText);
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
            throw new IllegalArgumentException("the sum of prefixLength and suffixLength must less " +
                    "than length of originalText");
        }
        mPrefixStr = originalText.substring(0, prefixLength);
        mSuffixStr = originalText.substring(originalText.length() - suffixLength, originalText.length());

        initSizes();
        invalidate();
    }

    public String getFilledText() {
        StringBuilder builder = new StringBuilder();
        for (String s : mBlankStrings) {
            builder.append(s);
        }
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
        void matched(boolean isMatched, String originalText);
    }
}
