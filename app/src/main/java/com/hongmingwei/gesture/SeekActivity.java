package com.hongmingwei.gesture;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import com.hongmingwei.gesture.ui.CustomWaveView;
import com.hongmingwei.gesture.ui.MySeekBar;

import java.util.ArrayList;
import java.util.List;

public class SeekActivity extends Activity {

    private static final String TAG = SeekActivity.class.getSimpleName();

    private MySeekBar seekBar;
    private List<String> percent = new ArrayList<>();
    private List<String> name = new ArrayList<>();


    private CustomWaveView waveView;
    private int currentProgress = 0;
    private int maxProgress = 100;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what){
                case 0:
                    waveView.start();
                    waveView.setCurrentProgress(currentProgress);
                    currentProgress ++;
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seek_bar);
        seekBar = findViewById(R.id.expect_buy_seekbar);
        initdata();
        initListener();


        waveView = findViewById(R.id.custom_circle_wave_view);
        //设置圆的半径
        waveView.setRadius(100);
        //设置进度最大值
        waveView.setMaxProgress(maxProgress);
        //设置进度的当前值
        waveView.setCurrentProgress(currentProgress);
        //模拟下载。每个
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (currentProgress < maxProgress){
                    try {
                        Thread.sleep(100);
                        handler.sendEmptyMessage(0);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        }).start();
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
