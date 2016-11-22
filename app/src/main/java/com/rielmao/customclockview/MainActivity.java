package com.rielmao.customclockview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Riel on 2016/11/21.
 */

public class MainActivity extends AppCompatActivity {
    MyClockView clock;
    TextView tv;
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        clock = (MyClockView) findViewById(R.id.clock);
        tv = (TextView) findViewById(R.id.tv);

        clock.setOnTimeChangeListener(new MyClockView.OnTimeChangeListener() {
            @Override
            public void onTimeChange(View view, int hour, int minute, int second) {
                tv.setText(String.format("%s:%s:%s", format(hour), format(minute), format(second)));
            }

            private String format(int number) {
                String a = String.valueOf(number);
                if (number<10){
                     a = "0"+a;
                    return a;
                }else{
                    return a;
                }
            }

        });
    }
}
