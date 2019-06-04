package cn.edu.bistu.step.pedometer;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoryActivity extends AppCompatActivity {

    ListView listView;//列表
    List<Map<String,String>> list;//日期，步数
    StepsDB DB;
    SQLiteDatabase sqLiteDatabase;
    SimpleAdapter simpleAdapter;
int index = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        listView = (ListView)findViewById(R.id.his_list);//获取列表
        list = new ArrayList<Map<String, String>>();

        DB = new StepsDB(this);//创建
        sqLiteDatabase = DB.getReadableDatabase();//读取
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> Vparent, View view, final int position, long id) {
                AlertDialog.Builder builder=new AlertDialog.Builder(HistoryActivity.this);
                builder.setMessage("确定删除？");
                builder.setTitle("提示");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (list.remove(position)!=null){
                            System.out.println("success");
                        }else {
                            System.out.println("failed");
                        }

                        simpleAdapter.notifyDataSetChanged();
                        Toast.makeText(getBaseContext(),"删除列表项",Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });


                builder.create().show();
                return false;
            }
        });
        refreshList();//刷新列表

    }

    //刷新列表
    public void refreshList(){
        clear();
        simpleAdapter = new SimpleAdapter(this, getAll(), R.layout.item_history,
                new String[]{DB.COLUMN_NAME_DATE,DB.COLUMN_NAME_USERNAME, DB.COLUMN_NAME_STEPS},
                new int[]{R.id.tv_date,R.id.tv_name, R.id.tv_step});
        listView.setAdapter(simpleAdapter);

    }
    //清空列表
    public void clear(){
        int count = list.size();
        if(count > 0){//清空
            list.removeAll(list);
            simpleAdapter.notifyDataSetChanged();//重绘
            listView.setAdapter(simpleAdapter);
        }
    }

    //获取全部记录
    public List<Map<String, String>> getAll() {
        Cursor cursor = sqLiteDatabase.query(DB.TABLE_NAME_S, null, null, null, null, null, null);
        while (cursor.moveToNext()){
            Map<String,String> item = new HashMap<String,String>();
            item.put(DB.COLUMN_NAME_DATE,String.valueOf(cursor.getString(cursor.getColumnIndex(DB.COLUMN_NAME_DATE))));
            item.put(DB.COLUMN_NAME_USERNAME,String.valueOf(cursor.getString(cursor.getColumnIndex(DB.COLUMN_NAME_USERNAME))));
            item.put(DB.COLUMN_NAME_STEPS,String.valueOf(cursor.getString(cursor.getColumnIndex(DB.COLUMN_NAME_STEPS))));
            list.add(item);
        }
        cursor.close();
        return list;
    }


}
