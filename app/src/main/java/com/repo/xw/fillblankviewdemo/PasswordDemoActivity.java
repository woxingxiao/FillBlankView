package com.repo.xw.fillblankviewdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class PasswordDemoActivity extends AppCompatActivity implements View.OnClickListener {

    private FillBlankView mFillBlankView1, mFillBlankView2, mFillBlankView3, mFillBlankView4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_demo);

        mFillBlankView1 = (FillBlankView) findViewById(R.id.fill_blank_view1);
        mFillBlankView2 = (FillBlankView) findViewById(R.id.fill_blank_view2);
        mFillBlankView3 = (FillBlankView) findViewById(R.id.fill_blank_view3);
        mFillBlankView4 = (FillBlankView) findViewById(R.id.fill_blank_view4);
        Button mButton1 = (Button) findViewById(R.id.button1);
        Button mButton2 = (Button) findViewById(R.id.button2);
        Button mButton3 = (Button) findViewById(R.id.button3);
        Button mButton4 = (Button) findViewById(R.id.button4);
        Button mButton5 = (Button) findViewById(R.id.button5);
        Button mButton6 = (Button) findViewById(R.id.button6);
        Button mButton7 = (Button) findViewById(R.id.button7);
        Button mButton8 = (Button) findViewById(R.id.button8);
        Button mButton9 = (Button) findViewById(R.id.button9);
        Button mButton11 = (Button) findViewById(R.id.button11);
        Button mButton12 = (Button) findViewById(R.id.button12);

        mButton1.setOnClickListener(this);
        mButton2.setOnClickListener(this);
        mButton3.setOnClickListener(this);
        mButton4.setOnClickListener(this);
        mButton5.setOnClickListener(this);
        mButton6.setOnClickListener(this);
        mButton7.setOnClickListener(this);
        mButton8.setOnClickListener(this);
        mButton9.setOnClickListener(this);
        mButton11.setOnClickListener(this);
        mButton12.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button1:
                mFillBlankView1.getText().append("1");
                mFillBlankView2.getText().append("1");
                mFillBlankView3.getText().append("1");
                mFillBlankView4.getText().append("1");
                break;
            case R.id.button2:
                mFillBlankView1.getText().append("2");
                mFillBlankView2.getText().append("2");
                mFillBlankView3.getText().append("2");
                mFillBlankView4.getText().append("2");
                break;
            case R.id.button3:
                mFillBlankView1.getText().append("3");
                mFillBlankView2.getText().append("3");
                mFillBlankView3.getText().append("3");
                mFillBlankView4.getText().append("3");
                break;
            case R.id.button4:
                mFillBlankView1.getText().append("4");
                mFillBlankView2.getText().append("4");
                mFillBlankView3.getText().append("4");
                mFillBlankView4.getText().append("4");
                break;
            case R.id.button5:
                mFillBlankView1.getText().append("5");
                mFillBlankView2.getText().append("5");
                mFillBlankView3.getText().append("5");
                mFillBlankView4.getText().append("5");
                break;
            case R.id.button6:
                mFillBlankView1.getText().append("6");
                mFillBlankView2.getText().append("6");
                mFillBlankView3.getText().append("6");
                mFillBlankView4.getText().append("6");
                break;
            case R.id.button7:
                mFillBlankView1.getText().append("7");
                mFillBlankView2.getText().append("7");
                mFillBlankView3.getText().append("7");
                mFillBlankView4.getText().append("7");
                break;
            case R.id.button8:
                mFillBlankView1.getText().append("8");
                mFillBlankView2.getText().append("8");
                mFillBlankView3.getText().append("8");
                mFillBlankView4.getText().append("8");
                break;
            case R.id.button9:
                mFillBlankView1.getText().append("9");
                mFillBlankView2.getText().append("9");
                mFillBlankView3.getText().append("9");
                mFillBlankView4.getText().append("9");
                break;
            case R.id.button11:
                mFillBlankView1.getText().append("0");
                mFillBlankView2.getText().append("0");
                mFillBlankView3.getText().append("0");
                mFillBlankView4.getText().append("0");
                break;
            case R.id.button12:
                if (mFillBlankView1.getText().length() * mFillBlankView2.getText().length() *
                        mFillBlankView3.getText().length() * mFillBlankView4.getText().length() == 0)
                    return;
                mFillBlankView1.getText().delete(mFillBlankView1.getText().length() - 1,
                        mFillBlankView1.getText().length());
                mFillBlankView2.getText().delete(mFillBlankView2.getText().length() - 1,
                        mFillBlankView2.getText().length());
                mFillBlankView3.getText().delete(mFillBlankView3.getText().length() - 1,
                        mFillBlankView3.getText().length());
                mFillBlankView4.getText().delete(mFillBlankView4.getText().length() - 1,
                        mFillBlankView4.getText().length());
                break;
        }
    }

}