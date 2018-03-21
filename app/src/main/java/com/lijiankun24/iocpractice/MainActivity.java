package com.lijiankun24.iocpractice;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.lijiankun24.iocpractice.ioc.ContentView;
import com.lijiankun24.iocpractice.ioc.OnClick;
import com.lijiankun24.iocpractice.ioc.ViewInject;
import com.lijiankun24.iocpractice.ioc.ViewInjectUtils;

@ContentView(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {

    @ViewInject(R.id.tv1)
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewInjectUtils.inject(this);
        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "IOC For Test", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @OnClick(R.id.tv1)
    private void onClick() {

    }
}
