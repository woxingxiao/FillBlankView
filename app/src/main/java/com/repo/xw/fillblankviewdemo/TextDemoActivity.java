package com.repo.xw.fillblankviewdemo;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class TextDemoActivity extends AppCompatActivity implements View.OnClickListener {

    private FillBlankView mFillBlankView1, mFillBlankView2, mFillBlankView3, mFillBlankView4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_demo);

        mFillBlankView1 = (FillBlankView) findViewById(R.id.fill_blank_view1);
        mFillBlankView2 = (FillBlankView) findViewById(R.id.fill_blank_view2);
        mFillBlankView3 = (FillBlankView) findViewById(R.id.fill_blank_view3);
        mFillBlankView4 = (FillBlankView) findViewById(R.id.fill_blank_view4);

        mFillBlankView1.setOnClickListener(this);
        mFillBlankView2.setOnClickListener(this);

        mFillBlankView2.setOriginalText("13800000000", 3, 4);
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
