package com.hongmingwei.gesture;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.hongmingwei.gesture.ui.LocusPassWordView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private LocusPassWordView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        view = (LocusPassWordView) findViewById(R.id.agp_locus_pass);
        initListener();
    }




    private void initListener(){
        view.setOnCompleteListener(new LocusPassWordView.OnCompleteListener() {
            @Override
            public void onComplete(String password) {
                view.clearPassword();
                if (password.length() > 3) {
                    Log.e(TAG, password + "啦啦啦");
                } else {
                    //密码错误，提示重新输入
//                    view.error();
//                    view.clearPassword(1000);
                }

            }
        });
    }
}
