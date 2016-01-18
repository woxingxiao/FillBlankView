package com.repo.xw.fillblankviewdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private static final String s1 = "password";
    private static final String s2 = "text";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FillBlankView passwordFillBlankView = (FillBlankView) findViewById(R.id.show_password_demo);
        passwordFillBlankView.setBlankNum(s1.length());
        passwordFillBlankView.setText(s1);
        passwordFillBlankView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, PasswordDemoActivity.class));
            }
        });

        FillBlankView textFillBlankView = (FillBlankView) findViewById(R.id.show_text_demo);
        textFillBlankView.setBlankNum(s2.length());
        textFillBlankView.setText(s2);
        textFillBlankView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, TextDemoActivity.class));
            }
        });
    }

}
