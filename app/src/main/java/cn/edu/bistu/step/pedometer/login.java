package cn.edu.bistu.step.pedometer;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);

        Intent intent = getIntent();
        final String s = intent.getStringExtra("username");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(login.this, StepActivity.class);
                intent.putExtra("username",s);
                startActivity(intent);
                finish();
            }

        }, 1000);

    }
}
