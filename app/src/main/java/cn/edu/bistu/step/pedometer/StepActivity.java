package cn.edu.bistu.step.pedometer;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class StepActivity extends AppCompatActivity {

    TextView stepnow;//当前步数
    TextView stepplan;//计划步数
    ProgressBar pBar;//进度条
    TextView finish;//是否达标

    StepsDB DB;
    SQLiteDatabase sqLiteDatabase;
    String user;
    String date;

    //计步器服务连接
    StepService stepService;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                String now_step = stepnow.getText().toString();
                int count = Integer.parseInt(now_step) + (int)stepService.getmDetector();
                stepnow.setText(String.valueOf(count));
                stepService.setmDetector(0);
                checkPlan();//进度条，达标确认
                setStepnow(user,date);
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);

        stepnow = (TextView)findViewById(R.id.step_now);
        stepplan = (TextView)findViewById(R.id.step_plan);
        pBar = (ProgressBar)findViewById(R.id.progressBar);
        finish = (TextView)findViewById(R.id.finish);

        DB = new StepsDB(this);
        sqLiteDatabase = DB.getReadableDatabase();
        sqLiteDatabase = DB.getWritableDatabase();

        Intent intent = getIntent();
        user = intent.getStringExtra("username");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        date = sdf.format(new Date());

        getTodayHistory(user,date);
        checkPlan();

        stepService = new StepService(this);
        stepService.register();

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new Task(),1,1000);

    }

    class Task extends TimerTask{

        @Override
        public void run() {
            Message msg = new Message();
            msg.what = 1;
            handler.sendMessage(msg);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.plan:
                setPlan(date,user);
                return true;
            case R.id.history:
                Intent intent1 = new Intent(StepActivity.this, HistoryActivity.class);
                startActivity(intent1);
                return true;
            case R.id.logout:
                Intent intent2 = new Intent(StepActivity.this, LoginActivity.class);
                startActivity(intent2);
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //计划设置更新过程
    public void setPlan(final String date, final String user){
        final TableLayout tableLayout = (TableLayout) getLayoutInflater().inflate(R.layout.dialog_plan, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(StepActivity.this);
        builder.setTitle("设置");
        builder.setView(tableLayout);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int n) {
                String plan_step = ((EditText)tableLayout.findViewById(R.id.step_plan)).getText().toString();
                if(plan_step.equals(""))
                    plan_step = "0";
                stepplan.setText(plan_step);

                //将计划同步至数据库
                String sql = "select * from step where date=? and username=?";
                Cursor cursor = sqLiteDatabase.rawQuery(sql, new String[]{date,user});
                if(cursor.moveToNext()){//有当天记录，修改
                    String update = "Update step set plansteps = '" + plan_step + "' where date='" + date + "' and username='" + user + "'";
                    sqLiteDatabase.execSQL(update);
                }else {//没有当天记录，插入
                    String now_step = stepnow.getText().toString();
                    String insert = "Insert into step values('" + date + "','" + now_step + "','" + plan_step + "','" + user + "')";
                    sqLiteDatabase.execSQL(insert);
                }
                cursor.close();
                checkPlan();
            }
        })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {}
                });
        builder.create();
        builder.show();
    }

    //当前步数同步至数据库
    public void setStepnow(String user, String date){
        String now_step = stepnow.getText().toString();
        String sql = "select * from step where date=? and username=?";
        Cursor cursor = sqLiteDatabase.rawQuery(sql, new String[]{date,user});
        if(cursor.moveToNext()){//有当天记录，修改
            String update = "Update step set steps = '" + now_step + "' where date='" + date + "' and username='" + user + "'";
            sqLiteDatabase.execSQL(update);
        }else {//没有当天记录，插入
            String plan_step = stepplan.getText().toString();
            String insert = "Insert into step values('" + date + "','" + now_step + "','" + plan_step + "','" + user + "')";
            sqLiteDatabase.execSQL(insert);
        }
        cursor.close();
    }

    //检查0/0结果，设定进度条，设定是否达标
    public void checkPlan(){
        String plan_step = stepplan.getText().toString();
        int ps = Integer.parseInt(plan_step);
        if (ps==0){
            pBar.setProgress(100);
            finish.setText("已达标");
            finish.setTextColor(Color.BLUE);
        }else {
            String now_step = stepnow.getText().toString();
            int ns = Integer.parseInt(now_step);
            if(ns/ps >= 1){
                pBar.setProgress(100);
                finish.setText("已达标");
                finish.setTextColor(Color.BLUE);
            }else {
                int c = ns*100/ps;
                pBar.setProgress(c);
                finish.setText("未达标");
                finish.setTextColor(Color.RED);
            }
        }
    }

    //当程序加载，获取当天的记录
    public void getTodayHistory(String user, String date){
        String sql = "select * from step where date=? and username=?";
        Cursor cursor = sqLiteDatabase.rawQuery(sql, new String[]{date,user});
        if(cursor.moveToNext()){
            String ns = String.valueOf(cursor.getString(cursor.getColumnIndex(DB.COLUMN_NAME_STEPS)));
            String ps = String.valueOf(cursor.getString(cursor.getColumnIndex(DB.COLUMN_NAME_PLANSTEPS)));
            stepplan.setText(ps);
            stepnow.setText(ns);
        }else {
            stepplan.setText("0");
            stepnow.setText("0");
        }
        cursor.close();
    }
}
