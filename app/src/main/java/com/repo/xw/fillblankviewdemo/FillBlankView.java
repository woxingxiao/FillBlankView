package com.repo.xw.fillblankviewdemo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.EditText;

/**
 * FillBlankView
 * Created by woxingxiao on 2016/01/06.
 * GitHub: https://github.com/woxingxiao/FillBlankViewDemo
 */
public class FillBlankView extends EditText {

    private static final String INSTANCE_STATE = "saved_instance";
    private static final String INSTANCE_BLANK_NUM = "blank_num";
    private static final String INSTANCE_BLANK_SPACE = "blank_space";
    private static final String INSTANCE_BLANK_SOLID_COLOR = "blank_solid_color";
    private static final String INSTANCE_BLANK_STROKE_COLOR = "blank_stroke_color";
    private static final String INSTANCE_BLANK_STROKE_WIDTH = "blank_stroke_width";
    private static final String INSTANCE_BLANK_CORNER_RADIUS = "blank_corner_radius";
    private static final String INSTANCE_IS_HIDE_TEXT = "is_hide_text";
    private static final String INSTANCE_DOT_SIZE = "dot_size";
    private static final String INSTANCE_DOT_COLOR = "dot_color";
    private static final String INSTANCE_TEXT_MATCHED_COLOR = "text_matched_color";
    private static final String INSTANCE_TEXT_NOT_MATCHED_COLOR = "text_not_matched_color";
    private static final String INSTANCE_PREFIX_STR = "prefix_str";
    private static final String INSTANCE_SUFFIX_STR = "suffix_str";

    private int mBlankNum; // the number of blanks
    private int mBlankSpace; // the space between two blanks
    private int mBlankSolidColor;
    private int mBlankStrokeColor;
    private int mBlankStrokeWidth;
    private int mBlankCornerRadius;
    private boolean isHideText; // if hide text, the contents inputted will be replaced by dots
    private int mDotSize;
    private int mDotColor;
    private int mTextMatchedColor; // if contents matched the original text, the text will show with this color
    private int mTextNotMatchedColor; // if contents didn't matched the original text, the text will show with this color

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

    private OnTextMatchedListener mListener;
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
                if (isHideText)
                    mPaintDot.setColor(mDotColor);
                for (int i = 0; i < mBlankNum; i++) {
                    if (i < s.length()) {
                        mBlankStrings[i] = s.subSequence(i, i + 1).toString();
                    } else {
                        mBlankStrings[i] = "";
                    }
                }

                if (getFilledText().equals(originalText)) {
                    if (s.length() == mBlankNum) {
                        mPaintText.setColor(mTextMatchedColor);
                        if (isHideText && mTextMatchedColor != getCurrentTextColor())
                            mPaintDot.setColor(mTextMatchedColor);
                    }
                    if (mListener != null)
                        mListener.matched(true, originalText);
                } else {
                    if (s.length() == mBlankNum) {
                        mPaintText.setColor(mTextNotMatchedColor);
                        if (isHideText && mTextNotMatchedColor != getCurrentTextColor())
                            mPaintDot.setColor(mTextNotMatchedColor);
                    }
                    if (mListener != null)
                        mListener.matched(false, null);
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
        if (isEmptyString(mPrefixStr) && isEmptyString(mSuffixStr)) {
            column = mBlankNum;
        } else if (!isEmptyString(mPrefixStr) && !isEmptyString(mSuffixStr)) {
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
                right = left + width;
            } else {
                left = width * i + mBlankSpace * i + getPaddingLeft();
                right = width * (i + 1) + mBlankSpace * i + getPaddingLeft();
            }

            mRectFs[i] = new RectF(left, top, right, bottom);
        }
        mRect = new Rect(0, 0, getWidth(), getHeight());

        if (mBlankSpace == 0) {
            if (!isEmptyString(mPrefixStr) && !isEmptyString(mSuffixStr)) {
                mRectBig = new RectF(mRectFs[1].left, getPaddingTop(),
                        mRectFs[mRectFs.length - 2].right, getHeight() - getPaddingBottom());
            } else if (!isEmptyString(mPrefixStr) && isEmptyString(mSuffixStr)) {
                mRectBig = new RectF(mRectFs[1].left, getPaddingTop(),
                        getWidth() - getPaddingLeft(), getHeight() - getPaddingBottom());
            } else if (isEmptyString(mPrefixStr) && !isEmptyString(mSuffixStr)) {
                mRectBig = new RectF(getPaddingLeft(), getPaddingTop(),
                        mRectFs[mRectFs.length - 2].right, getHeight() - getPaddingBottom());
            } else {
                mRectBig = new RectF(getPaddingLeft(), getPaddingTop(),
                        getWidth() - getPaddingLeft(), getHeight() - getPaddingBottom());
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // draw background
        if (getBackground() == null) {
            canvas.drawColor(Color.WHITE);
        } else {
            getBackground().draw(canvas);
        }

        // draw blanks
        for (int i = 0; i < mRectFs.length; i++) {
            if (i == 0 && !isEmptyString(mPrefixStr))
                continue;
            if (mRectFs.length > 1 && i == mRectFs.length - 1 && !isEmptyString(mSuffixStr))
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
                    if (mBlankNum > 1) {
                        mPaintBlank.setAlpha(110);
                        mPaintBlank.setStrokeWidth(mBlankStrokeWidth / 2.0f);
                        canvas.drawLine(mRectFs[i].right, mRectFs[i].top, mRectFs[i].right, mRectFs[i].bottom, mPaintBlank);

                        if (i == mRectFs.length - 2) {
                            mPaintBlank.setAlpha(255);
                            mPaintBlank.setStrokeWidth(mBlankStrokeWidth);
                            canvas.drawRoundRect(mRectBig, mBlankCornerRadius, mBlankCornerRadius, mPaintBlank);
                            break;
                        }
                    } else if (mBlankNum == 1) {
                        canvas.drawRoundRect(mRectBig, mBlankCornerRadius, mBlankCornerRadius, mPaintBlank);
                    }
                }
            }
        }

        // texts align center of the blank
        Paint.FontMetricsInt fontMetrics = mPaintText.getFontMetricsInt();
        int textCenterY = (mRect.bottom + mRect.top - fontMetrics.bottom - fontMetrics.top) / 2;
        // draw prefix of original text
        if (!isEmptyString(mPrefixStr)) {
            mPaintText.setTextAlign(Paint.Align.RIGHT);
            mPaintText.getTextBounds(mPrefixStr, 0, mPrefixStr.length(), mTextRect);
            canvas.drawText(mPrefixStr, mRectFs[1].left - mBlankSpace, textCenterY, mPaintText);
        }
        // draw texts or dots on blanks
        mPaintText.setTextAlign(Paint.Align.CENTER);
        for (int i = 0; i < mBlankNum; i++) {
            if (isHideText && dotCount > 0) {
                if (i + 1 > dotCount)
                    break;
                if (isEmptyString(mPrefixStr)) {
                    canvas.drawCircle(mRectFs[i].centerX(), mRectFs[i].centerY(), mDotSize, mPaintDot);
                } else {
                    canvas.drawCircle(mRectFs[i + 1].centerX(), mRectFs[i + 1].centerY(), mDotSize, mPaintDot);
                }
            } else {
                mPaintText.getTextBounds(mBlankStrings[i], 0, mBlankStrings[i].length(), mTextRect);
                if (isEmptyString(mPrefixStr)) {
                    canvas.drawText(mBlankStrings[i], mRectFs[i].centerX(), textCenterY, mPaintText);
                } else {
                    canvas.drawText(mBlankStrings[i], mRectFs[i + 1].centerX(), textCenterY, mPaintText);
                }
            }
        }
        // draw suffix of original text
        if (!isEmptyString(mSuffixStr)) {
            mPaintText.setTextAlign(Paint.Align.LEFT);
            mPaintText.getTextBounds(mSuffixStr, 0, mSuffixStr.length(), mTextRect);
            canvas.drawText(mSuffixStr, mRectFs[mRectFs.length - 1].left, textCenterY, mPaintText);
        }
    }

    public String getOriginalText() {
        return originalText;
    }

    /**
     * set text that waiting to be matched
     *
     * @param originalText original text
     */
    public void setOriginalText(@NonNull String originalText) {
        this.originalText = originalText;
        if (originalText.isEmpty())
            return;

        mBlankNum = originalText.length();
        mBlankStrings = new String[mBlankNum];
        for (int i = 0; i < mBlankStrings.length; i++)
            mBlankStrings[i] = "";
        initSizes();
        invalidate();
    }

    /**
     * set text that waiting to be matched
     *
     * @param originalText original text
     * @param prefixLength show length of originalText at start
     * @param suffixLength show length of originalText at end
     */
    public void setOriginalText(@NonNull String originalText, int prefixLength, int suffixLength) {
        this.originalText = originalText;
        if (originalText.isEmpty())
            return;
        if (originalText.length() <= prefixLength + suffixLength) {
            throw new IllegalArgumentException("the sum of prefixLength and suffixLength must be less " +
                    "than length of originalText");
        }
        mBlankNum = originalText.length() - prefixLength - suffixLength;
        mPrefixStr = originalText.substring(0, prefixLength);
        mSuffixStr = originalText.substring(originalText.length() - suffixLength, originalText.length());
        mBlankStrings = new String[mBlankNum];
        for (int i = 0; i < mBlankStrings.length; i++)
            mBlankStrings[i] = "";
        initSizes();
        invalidate();
    }

    /**
     * prefix + text in the blanks + suffix
     */
    public String getFilledText() {
        StringBuilder builder = new StringBuilder();
        if (!isEmptyString(mPrefixStr))
            builder.append(mPrefixStr);
        for (String s : mBlankStrings)
            builder.append(s);
        if (!isEmptyString(mSuffixStr))
            builder.append(mSuffixStr);
        return builder.toString();
    }

    public int getBlankNum() {
        return mBlankNum;
    }

    /**
     * set number of blanks
     */
    public void setBlankNum(int blankNum) {
        if (!isEmptyString(mPrefixStr) || !isEmptyString(mSuffixStr))
            return;

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

    /**
     * set distance between tow blanks
     */
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

    /**
     * use dots to replace text or not
     */
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

    public OnTextMatchedListener getOnTextMatchedListener() {
        return mListener;
    }

    public void setOnTextMatchedListener(OnTextMatchedListener listener) {
        mListener = listener;
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    private boolean isEmptyString(String string) {
        if (string == null || string.isEmpty())
            return true;
        return false;
    }

    public interface OnTextMatchedListener {
        void matched(boolean isMatched, String originalText);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE_STATE, super.onSaveInstanceState());
        bundle.putInt(INSTANCE_BLANK_NUM, mBlankNum);
        bundle.putInt(INSTANCE_BLANK_SPACE, mBlankSpace);
        bundle.putInt(INSTANCE_BLANK_SOLID_COLOR, mBlankSolidColor);
        bundle.putInt(INSTANCE_BLANK_STROKE_COLOR, mBlankStrokeColor);
        bundle.putInt(INSTANCE_BLANK_STROKE_WIDTH, mBlankStrokeWidth);
        bundle.putInt(INSTANCE_BLANK_CORNER_RADIUS, mBlankCornerRadius);
        bundle.putBoolean(INSTANCE_IS_HIDE_TEXT, isHideText);
        bundle.putInt(INSTANCE_DOT_SIZE, mDotSize);
        bundle.putInt(INSTANCE_DOT_COLOR, mDotColor);
        bundle.putInt(INSTANCE_TEXT_MATCHED_COLOR, mTextMatchedColor);
        bundle.putInt(INSTANCE_TEXT_NOT_MATCHED_COLOR, mTextNotMatchedColor);
        bundle.putString(INSTANCE_PREFIX_STR, mPrefixStr);
        bundle.putString(INSTANCE_SUFFIX_STR, mSuffixStr);

        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            mBlankNum = bundle.getInt(INSTANCE_BLANK_NUM);
            mBlankSpace = bundle.getInt(INSTANCE_BLANK_SPACE);
            mBlankSolidColor = bundle.getInt(INSTANCE_BLANK_SOLID_COLOR);
            mBlankStrokeColor = bundle.getInt(INSTANCE_BLANK_STROKE_COLOR);
            mBlankStrokeWidth = bundle.getInt(INSTANCE_BLANK_STROKE_WIDTH);
            mBlankCornerRadius = bundle.getInt(INSTANCE_BLANK_CORNER_RADIUS);
            isHideText = bundle.getBoolean(INSTANCE_IS_HIDE_TEXT);
            mDotSize = bundle.getInt(INSTANCE_DOT_SIZE);
            mDotColor = bundle.getInt(INSTANCE_DOT_COLOR);
            mTextMatchedColor = bundle.getInt(INSTANCE_TEXT_MATCHED_COLOR);
            mTextNotMatchedColor = bundle.getInt(INSTANCE_TEXT_NOT_MATCHED_COLOR);
            mPrefixStr = bundle.getString(INSTANCE_PREFIX_STR);
            mSuffixStr = bundle.getString(INSTANCE_SUFFIX_STR);
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATE));

            return;
        }
        super.onRestoreInstanceState(state);
    }
}
