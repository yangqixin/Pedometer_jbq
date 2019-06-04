package cn.edu.bistu.step.pedometer;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {

    EditText r_username;
    EditText r_password;
    EditText r_password_confirm;

    StepsDB DB;
    SQLiteDatabase sqLiteDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        DB = new StepsDB(this);
        sqLiteDatabase = DB.getReadableDatabase();

        r_username = (EditText) findViewById(R.id.register_username);
        r_password = (EditText) findViewById(R.id.register_password);
        r_password_confirm = (EditText) findViewById(R.id.register_password_confirm);


        Button register = (Button) findViewById(R.id.register_button);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String r_u = r_username.getText().toString();
                String r_p = r_password.getText().toString();
                String r_p_c = r_password_confirm.getText().toString();
                insert(r_u, r_p, r_p_c);
            }
        });

        Button cancel = (Button) findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void insert(String r_u, String r_p, String r_p_c){
        if(checkUsername(r_u) && checkPassword(r_p) && checkPasswordConfirm(r_p,r_p_c)){
            String r_insert = "Insert into user values('" + r_u + "', '" + r_p + "')";
            sqLiteDatabase.execSQL(r_insert);
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            Toast.makeText(this, R.string.success, Toast.LENGTH_SHORT).show();
        }else {
            //Toast.makeText(this, R.string.error_register, Toast.LENGTH_SHORT).show();
        }
    }

    //下面三个判断，还可以写seterror
    public boolean checkUsername(String r_u){//不为空，查重
        if(r_u.equals("")) {
            Toast.makeText(this, R.string.error_empty_username, Toast.LENGTH_SHORT).show();
            return false;
        }else {
            String r = "select * from user where username=?";
            Cursor cursor = sqLiteDatabase.rawQuery(r, new String[]{r_u});
            while (cursor.moveToNext()){
                Toast.makeText(this, R.string.error_exist_username, Toast.LENGTH_SHORT).show();
                return false;
            }
            cursor.close();
        }
        return true;
    }

    public boolean checkPassword(String r_p){//不为空
        if(r_p.equals("")) {
            Toast.makeText(this, R.string.error_empty_password, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public boolean checkPasswordConfirm(String r_p, String r_p_c){//两次不一致
        if(r_p.equals(r_p_c))
            return true;
        Toast.makeText(this, R.string.error_password_confirm, Toast.LENGTH_SHORT).show();
        return false;
    }
}
