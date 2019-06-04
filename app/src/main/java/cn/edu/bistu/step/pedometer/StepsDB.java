package cn.edu.bistu.step.pedometer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class StepsDB extends SQLiteOpenHelper {

    private final static String DB_NAME = "pedometer";
    private final static int DB_VERSION = 1;

    //步数表
    public static final String TABLE_NAME_S = "step";
    public static final String COLUMN_NAME_DATE = "date";
    public static final String COLUMN_NAME_STEPS = "steps";
    public static final String COLUMN_NAME_PLANSTEPS = "plansteps";
    public static final String COLUMN_NAME_USERNAME = "username";
    //用户表
    public static final String TABLE_NAME_U = "user";
    public static final String COLUMN_NAME_PASSWORD = "password";

    //步数建表，删表
    private final static String SQL_CREATE_SDB = "CREATE TABLE " + TABLE_NAME_S + " ("
            + COLUMN_NAME_DATE + " TEXT NOT NULL,"
            + COLUMN_NAME_STEPS + " TEXT DEFAULT '0',"
            + COLUMN_NAME_PLANSTEPS + " TEXT DEFAULT '0',"
            + COLUMN_NAME_USERNAME + " TEXT NOT NULL)";
    private final static String SQL_DELETE_SDB = "DROP TABLE IF EXISTS " + TABLE_NAME_S;
    //用户建表，删表
    private final static String SQL_CREATE_UDB = "CREATE TABLE " + TABLE_NAME_U + " ("
            + COLUMN_NAME_USERNAME + " TEXT PRIMARY KEY,"
            + COLUMN_NAME_PASSWORD + " TEXT NOT NULL)";
    private final static String SQL_DELETE_UDB = "DROP TABLE IF EXISTS " + TABLE_NAME_U;


    public StepsDB(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_SDB);
        sqLiteDatabase.execSQL(SQL_CREATE_UDB);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }
}
