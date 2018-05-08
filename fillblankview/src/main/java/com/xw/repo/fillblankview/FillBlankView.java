package com.xw.repo.fillblankview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewTreeObserver;

/**
 * FillBlankView
 * <p>
 * Created by woxingxiao on 2016/01/06.
 * <p>
 * GitHub: https://github.com/woxingxiao/FillBlankView
 */
public class FillBlankView extends AppCompatEditText {

    private static final String INSTANCE_STATE = "saved_instance";
    private static final String INSTANCE_PREFIX_STR = "prefix_str";
    private static final String INSTANCE_SUFFIX_STR = "suffix_str";

    private int mBlankNum; // the number of blanks
    private int mBlankSpace; // the space between two blanks
    private int mBlankSolidColor;
    private int mBlankStrokeColor;
    private int mBlankStrokeWidth;
    private int mBlankCornerRadius;
    private int mBlankFocusedStrokeColor; // the stroke color of blank when it be focused.
    private boolean isPasswordMode; // if true, the contents inputted will be replaced by dots
    private int mDotSize;
    private int mDotColor;
    private int mTextMatchedColor; // if contents matched the original text, the text will show with this color
    private int mTextNotMatchedColor; // if contents didn't matched the original text, the text will show with this color
    private boolean showTextTemporarily;

    private Paint mPaintBlank;
    private Paint mPaintText;
    private Paint mPaintDot;
    private RectF[] mRectFs;
    private RectF mRectBig;
    private Rect mTextRect;
    private String mPrefixStr;
    private String mSuffixStr;
    private String[] mBlankStrings;
    private int mDotCount;

    private OnTextMatchedListener mListener;
    private String originalText;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            mHandler.removeCallbacksAndMessages(null);

            showTextTemporarily = false;
            invalidate();
        }
    };

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
        mBlankFocusedStrokeColor = a.getColor(R.styleable.FillBlankView_blankFocusedStrokeColor, mBlankStrokeColor);
        isPasswordMode = a.getBoolean(R.styleable.FillBlankView_isPasswordMode, false);
        mDotSize = a.getDimensionPixelSize(R.styleable.FillBlankView_dotSize, dp2px(4));
        mDotColor = a.getColor(R.styleable.FillBlankView_dotColor, getCurrentTextColor());
        mTextMatchedColor = a.getColor(R.styleable.FillBlankView_textMatchedColor, getCurrentTextColor());
        mTextNotMatchedColor = a.getColor(R.styleable.FillBlankView_textNotMatchedColor, getCurrentTextColor());
        a.recycle();

        int inputType = getInputType();
        if (inputType == 129 || inputType == 145 || inputType == 18 || inputType == 225) {
            isPasswordMode = true;
        }
        String text = getText().toString();
        if (!text.isEmpty()) {
            mBlankNum = text.length();
        }
        initObjects();

        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= 16) {
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }

                setText(getText());
                if (isClickable()) {
                    setFocusable(true);
                    setFocusableInTouchMode(true);
                    requestFocus();
                }

                mHandler.sendEmptyMessage(0);
            }
        });
    }

    private void initObjects() {
        setCursorVisible(false);

        if (mBlankNum <= 0) {
            throw new IllegalArgumentException("the 'blankNum' must be greater than zero !");
        }
        mBlankStrings = new String[mBlankNum];
        for (int i = 0; i < mBlankStrings.length; i++) {
            mBlankStrings[i] = "";
        }

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
                    mDotCount = mBlankNum;
                    return;
                }

                mPaintText.setColor(getCurrentTextColor());
                if (isPasswordMode) {
                    mPaintDot.setColor(mDotColor);
                }
                for (int i = 0; i < mBlankNum; i++) {
                    if (i < s.length()) {
                        mBlankStrings[i] = s.subSequence(i, i + 1).toString();
                    } else {
                        mBlankStrings[i] = "";
                    }
                }

                if (getAllText().equals(originalText)) {
                    if (s.length() == mBlankNum) {
                        mPaintText.setColor(mTextMatchedColor);
                        if (isPasswordMode && mTextMatchedColor != getCurrentTextColor()) {
                            mPaintDot.setColor(mTextMatchedColor);
                        }
                    }
                    if (mListener != null) {
                        mListener.matched(true, originalText);
                    }
                } else {
                    if (s.length() == mBlankNum) {
                        mPaintText.setColor(mTextNotMatchedColor);
                        if (isPasswordMode && mTextNotMatchedColor != getCurrentTextColor()) {
                            mPaintDot.setColor(mTextNotMatchedColor);
                        }
                    }
                    if (mListener != null) {
                        mListener.matched(false, null);
                    }
                }

                int length = s.length();
                if (length <= mDotCount) { // deleting
                    mDotCount = length;
                    invalidate();

                    return;
                }

                mDotCount = length;

                if (isPasswordMode) {
                    showTextTemporarily = true;
                    invalidate();

                    mHandler.sendEmptyMessageDelayed(0, 500);
                } else {
                    invalidate();
                }
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = resolveSize(dp2px(80), widthMeasureSpec);
        int height = getPaddingTop() + getPaddingBottom() +
                (width - getPaddingLeft() - getPaddingRight() - (mBlankNum - 1) * mBlankSpace) / mBlankNum;

        setMeasuredDimension(width, resolveSize(height, heightMeasureSpec));
        initSizes();
    }

    private void initSizes() {
        int viewWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        int column;
        if (isEmptyString(mPrefixStr) && isEmptyString(mSuffixStr)) {
            column = mBlankNum;
        } else if (!isEmptyString(mPrefixStr) && !isEmptyString(mSuffixStr)) {
            column = mBlankNum + 2;
        } else {
            column = mBlankNum + 1;
        }
        mRectFs = new RectF[column];
        int width = (viewWidth - mBlankSpace * (column - 1) - mBlankStrokeWidth) / column;
        float strokeHalf = mBlankStrokeWidth / 2f;
        float top = getPaddingTop() + strokeHalf;
        float bottom = getMeasuredHeight() - getPaddingBottom() - strokeHalf;
        float left;
        float right;
        for (int i = 0; i < mRectFs.length; i++) {
            if (i == 0) {
                left = getPaddingLeft() + strokeHalf;
            } else {
                left = width * i + mBlankSpace * i + getPaddingLeft() + strokeHalf;
            }
            right = left + width;

            mRectFs[i] = new RectF(left, top, right, bottom);
        }

        if (mBlankSpace == 0) {
            if (mRectBig == null) {
                mRectBig = new RectF();
            }
            if (!isEmptyString(mPrefixStr) && !isEmptyString(mSuffixStr)) {
                mRectBig.set(mRectFs[1].left, getPaddingTop(), mRectFs[mRectFs.length - 2].right,
                        getMeasuredHeight() - getPaddingBottom());
            } else if (!isEmptyString(mPrefixStr) && isEmptyString(mSuffixStr)) {
                mRectBig.set(mRectFs[1].left, getPaddingTop(), getMeasuredWidth() - getPaddingLeft(),
                        getMeasuredHeight() - getPaddingBottom());
            } else if (isEmptyString(mPrefixStr) && !isEmptyString(mSuffixStr)) {
                mRectBig.set(getPaddingLeft(), getPaddingTop(), mRectFs[mRectFs.length - 2].right,
                        getMeasuredHeight() - getPaddingBottom());
            } else {
                mRectBig.set(getPaddingLeft(), getPaddingTop(), getMeasuredWidth() - getPaddingLeft(),
                        getMeasuredHeight() - getPaddingBottom());
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        super.draw(canvas);

        // draw background
        if (getBackground() != null) {
            getBackground().draw(canvas);
        }

        // draw blanks
        for (int i = 0; i < mRectFs.length; i++) {
            if (i == 0 && !isEmptyString(mPrefixStr)) {
                continue;
            }
            if (mRectFs.length > 1 && i == mRectFs.length - 1 && !isEmptyString(mSuffixStr)) {
                break;
            }

            mPaintBlank.setStyle(Paint.Style.FILL);
            mPaintBlank.setColor(mBlankSolidColor);
            canvas.drawRoundRect(mRectFs[i], mBlankCornerRadius, mBlankCornerRadius, mPaintBlank);

            if (mBlankStrokeWidth > 0) {
                mPaintBlank.setStyle(Paint.Style.STROKE);
                int index = 0;
                boolean allEmpty = false;
                for (int j = 0; j < mBlankStrings.length; j++) {
                    if (mBlankStrings[j].isEmpty()) {
                        if (j == 0 && mBlankStrings[j].isEmpty()) {
                            allEmpty = true;
                        }
                        index = j;
                        break;
                    }
                }
                if (hasFocus() && i == index) {
                    mPaintBlank.setColor(mBlankFocusedStrokeColor);
                    if (index == 0 && !allEmpty) {
                        mPaintBlank.setColor(mBlankStrokeColor);
                    }
                } else {
                    mPaintBlank.setColor(mBlankStrokeColor);
                }
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
        Paint.FontMetrics fontMetrics = mPaintText.getFontMetrics();
        float textCenterY = (getHeight() - fontMetrics.ascent - fontMetrics.descent) / 2.0f;
        // draw prefix of original text
        if (!isEmptyString(mPrefixStr)) {
            mPaintText.setTextAlign(Paint.Align.RIGHT);
            mPaintText.getTextBounds(mPrefixStr, 0, mPrefixStr.length(), mTextRect);
            canvas.drawText(mPrefixStr, mRectFs[1].left - mBlankSpace, textCenterY, mPaintText);
        }
        // draw texts or dots on blanks
        mPaintText.setTextAlign(Paint.Align.CENTER);
        for (int i = 0; i < mBlankNum; i++) {
            if (isPasswordMode && mDotCount > 0 && i <= mDotCount - 1) {
                if (i + 1 > mDotCount) {
                    break;
                }
                if (showTextTemporarily && i == mDotCount - 1) {
                    mPaintText.getTextBounds(mBlankStrings[i], 0, mBlankStrings[i].length(), mTextRect);
                    if (isEmptyString(mPrefixStr)) {
                        canvas.drawText(mBlankStrings[i], mRectFs[i].centerX(), textCenterY, mPaintText);
                    } else {
                        canvas.drawText(mBlankStrings[i], mRectFs[i + 1].centerX(), textCenterY, mPaintText);
                    }
                } else {
                    if (isEmptyString(mPrefixStr)) {
                        canvas.drawCircle(mRectFs[i].centerX(), mRectFs[i].centerY(), mDotSize, mPaintDot);
                    } else {
                        canvas.drawCircle(mRectFs[i + 1].centerX(), mRectFs[i + 1].centerY(), mDotSize, mPaintDot);
                    }
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
        if (originalText.isEmpty()) {
            return;
        }

        mBlankNum = originalText.length();
        mBlankStrings = new String[mBlankNum];
        for (int i = 0; i < mBlankStrings.length; i++) {
            mBlankStrings[i] = "";
        }
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
        if (originalText.isEmpty()) {
            return;
        }
        if (originalText.length() <= prefixLength + suffixLength) {
            throw new IllegalArgumentException("the sum of prefixLength and suffixLength must be less " +
                    "than length of originalText");
        }
        mBlankNum = originalText.length() - prefixLength - suffixLength;
        mPrefixStr = originalText.substring(0, prefixLength);
        mSuffixStr = originalText.substring(originalText.length() - suffixLength, originalText.length());
        mBlankStrings = new String[mBlankNum];
        for (int i = 0; i < mBlankStrings.length; i++) {
            mBlankStrings[i] = "";
        }
        initSizes();
        invalidate();
    }

    /**
     * Get texts in the blanks
     */
    public String getFilledText() {
        StringBuilder builder = new StringBuilder();
        for (String s : mBlankStrings) {
            builder.append(s);
        }

        return builder.toString();
    }

    /**
     * prefix + text in the blanks + suffix
     */
    public String getAllText() {
        StringBuilder builder = new StringBuilder();
        if (!isEmptyString(mPrefixStr)) {
            builder.append(mPrefixStr);
        }
        for (String s : mBlankStrings) {
            builder.append(s);
        }
        if (!isEmptyString(mSuffixStr)) {
            builder.append(mSuffixStr);
        }

        return builder.toString();
    }

    public int getBlankNum() {
        return mBlankNum;
    }

    /**
     * set number of blanks
     */
    public void setBlankNum(int blankNum) {
        if (!isEmptyString(mPrefixStr) || !isEmptyString(mSuffixStr)) {
            return;
        }

        mBlankNum = blankNum;
        if (mBlankNum <= 0) {
            throw new IllegalArgumentException("the 'blankNum' must be greater than zero !");
        }
        mBlankStrings = new String[mBlankNum];
        for (int i = 0; i < mBlankStrings.length; i++) {
            mBlankStrings[i] = "";
        }
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
            throw new IllegalArgumentException("the number of 'blankSpace' can't be less than zero !");
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
        return string == null || string.isEmpty();
    }

    public interface OnTextMatchedListener {
        void matched(boolean isMatched, String originalText);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE_STATE, super.onSaveInstanceState());
        bundle.putString(INSTANCE_PREFIX_STR, mPrefixStr);
        bundle.putString(INSTANCE_SUFFIX_STR, mSuffixStr);

        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            mPrefixStr = bundle.getString(INSTANCE_PREFIX_STR);
            mSuffixStr = bundle.getString(INSTANCE_SUFFIX_STR);
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATE));

            return;
        }
        super.onRestoreInstanceState(state);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        mHandler.removeCallbacksAndMessages(null);
    }

}
