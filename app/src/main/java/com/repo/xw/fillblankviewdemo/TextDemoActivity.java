package com.repo.xw.fillblankviewdemo;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

public class TextDemoActivity extends AppCompatActivity implements View.OnClickListener {

    private FillBlankView mFillBlankView1, mFillBlankView2, mFillBlankView3, mFillBlankView4;
    private TextView matchedText1, matchedText2, matchedText3, matchedText4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_demo);

        mFillBlankView1 = (FillBlankView) findViewById(R.id.fill_blank_view1);
        mFillBlankView2 = (FillBlankView) findViewById(R.id.fill_blank_view2);
        mFillBlankView3 = (FillBlankView) findViewById(R.id.fill_blank_view3);
        mFillBlankView4 = (FillBlankView) findViewById(R.id.fill_blank_view4);
        matchedText1 = (TextView) findViewById(R.id.matched_text1);
        matchedText2 = (TextView) findViewById(R.id.matched_text2);
        matchedText3 = (TextView) findViewById(R.id.matched_text3);
        matchedText4 = (TextView) findViewById(R.id.matched_text4);

        mFillBlankView1.setOnClickListener(this);
        mFillBlankView2.setOnClickListener(this);
        mFillBlankView3.setOnClickListener(this);
        mFillBlankView4.setOnClickListener(this);

        mFillBlankView1.setOriginalText("Android");
        mFillBlankView2.setOriginalText("Hello, world", 7, 0);
        mFillBlankView3.setOriginalText("13800000000", 3, 4);
        mFillBlankView4.setOriginalText("FillBlank", 0, 5);

        mFillBlankView1.setOnTextMatchedListener(new FillBlankView.OnTextMatchedListener() {
            @Override
            public void matched(boolean isMatched, String originalText) {
                if (isMatched) {
                    matchedText1.setText("matched !");
                } else {
                    matchedText1.setText("");
                }
            }
        });
        mFillBlankView2.setOnTextMatchedListener(new FillBlankView.OnTextMatchedListener() {
            @Override
            public void matched(boolean isMatched, String originalText) {
                if (isMatched) {
                    matchedText2.setText("matched !");
                } else {
                    matchedText2.setText("");
                }
            }
        });
        mFillBlankView3.setOnTextMatchedListener(new FillBlankView.OnTextMatchedListener() {
            @Override
            public void matched(boolean isMatched, String originalText) {
                if (isMatched) {
                    matchedText3.setText("matched !");
                    matchedText3.setTextColor(getResources().getColor(R.color.green));
                } else if (mFillBlankView3.getFilledText().length() == "13800000000".length()) {
                    matchedText3.setText("not match !");
                    matchedText3.setTextColor(getResources().getColor(R.color.colorAccent));
                } else {
                    matchedText3.setText("");
                }
            }
        });
        mFillBlankView4.setOnTextMatchedListener(new FillBlankView.OnTextMatchedListener() {
            @Override
            public void matched(boolean isMatched, String originalText) {
                if (isMatched) {
                    matchedText4.setText("matched !");
                } else {
                    matchedText4.setText("");
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fill_blank_view1:
                showInputMethod(mFillBlankView1);
                break;
            case R.id.fill_blank_view2:
                showInputMethod(mFillBlankView2);
                break;
            case R.id.fill_blank_view3:
                showInputMethod(mFillBlankView3);
                break;
            case R.id.fill_blank_view4:
                showInputMethod(mFillBlankView4);
                break;
        }
    }

    private void showInputMethod(EditText editText) {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();

        InputMethodManager inputManager = (InputMethodManager) editText.getContext().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(editText, 0);
    }

}
