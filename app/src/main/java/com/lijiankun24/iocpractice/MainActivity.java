package com.lijiankun24.iocpractice;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.lijiankun24.iocpractice.ioc.BindView;
import com.lijiankun24.iocpractice.ioc.ContentView;
import com.lijiankun24.iocpractice.ioc.OnClick;
import com.lijiankun24.iocpractice.ioc.ButterKnife;

@ContentView(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {

    @BindView(R.id.tv1)
    private TextView TV1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
        TV1.setText("Change the text");
    }

    @OnClick({R.id.tv1, R.id.tv2})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv1:
                Toast.makeText(MainActivity.this, "IOC For Test tv1", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tv2:
                Toast.makeText(MainActivity.this, "IOC For Test tv2", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
