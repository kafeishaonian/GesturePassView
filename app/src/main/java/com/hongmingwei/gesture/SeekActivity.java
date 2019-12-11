package com.hongmingwei.gesture;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.hongmingwei.gesture.ui.MySeekBar;

import java.util.ArrayList;
import java.util.List;

public class SeekActivity extends Activity {

    private static final String TAG = SeekActivity.class.getSimpleName();

    private MySeekBar seekBar;
    private List<String> percent = new ArrayList<>();
    private List<String> name = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seek_bar);
        seekBar = findViewById(R.id.expect_buy_seekbar);
        initdata();
        initListener();
    }


    private void initdata(){
        percent.add("150%");
        percent.add("200%");
        percent.add("300%");
        percent.add("400%");
        percent.add("500%");

        name.add("涨幅>10");
        name.add("涨幅>20");
        name.add("涨幅>30");
        name.add("涨幅>40");
        name.add("涨幅>50");
        seekBar.setTitles(name, percent);
    }

    private void initListener(){
        seekBar.setOnTouchEventListener(new MySeekBar.OnTouchEventListener() {
            @Override
            public void getPosition(int position) {
                Log.e(TAG, "getPosition: " + position);
            }
        });
    }
}
