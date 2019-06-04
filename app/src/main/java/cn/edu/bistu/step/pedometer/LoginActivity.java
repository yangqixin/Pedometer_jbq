package cn.edu.bistu.step.pedometer;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;

import android.database.Cursor;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    SQLiteDatabase usqLiteDatabase;
    StepsDB DB;

    EditText username;
    EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        DB = new StepsDB(this);
        usqLiteDatabase = DB.getReadableDatabase();

        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);

        Button btn_sign_in = (Button)findViewById(R.id.action_sign_in);
        btn_sign_in.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String un = username.getText().toString();
                String pw = password.getText().toString();
                validate(un,pw);
            }
        });

        Button btn_register = (Button)findViewById(R.id.action_register);
        btn_register.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    //验证用户名密码
    public void validate(String un, String pw){
        if(un.equals("") || pw.equals(""))
            Toast.makeText(LoginActivity.this,R.string.error_empty,Toast.LENGTH_SHORT).show();
        else {
            String login = "select password from user where username=?";
            Cursor cursor = usqLiteDatabase.rawQuery(login, new String[]{un});
            if(cursor.moveToNext()){
                if(pw.equals(String.valueOf(cursor.getString(cursor.getColumnIndex(DB.COLUMN_NAME_PASSWORD))))){
                    Intent intent = new Intent(LoginActivity.this, login.class);
                    intent.putExtra("username", un);
                    startActivity(intent);
                    finish();
                }else
                    Toast.makeText(LoginActivity.this,R.string.error,Toast.LENGTH_SHORT).show();
            }else
                Toast.makeText(LoginActivity.this,R.string.error,Toast.LENGTH_SHORT).show();
            cursor.close();
        }
    }

}

