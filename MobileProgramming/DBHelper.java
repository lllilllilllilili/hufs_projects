package org.techtown.usingmediarecorderapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "MyRecords.db";
    public static final String RECORDS_COLUMN_ID = "id";
    public static final String RECORDS_COLUMN_PATH = "path";
    public static final String RECORDS_COLUMN_DATE = "Date";
    public static final String RECORDS_COLUMN_STARTLATITUDE = "START_LATITUDE";
    public static final String RECORDS_COLUMN_STARTLONGITUDE = "START_LONGITUDE";
    public static final String RECORDS_COLUMN_ENDLATITUDE = "END_LATITUDE";
    public static final String RECORDS_COLUMN_ENDLONGITUDE = "END_LONGITUDE";

    //DATABASE와 RECORDS에 관한 값들을 설정해 주었습니다.
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 3); //생성자
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS records"); //같은 records table이 존재하면 drop 합니다.
        db.execSQL(
                "create table records " +
                        "(id integer primary key,path text, Date text, START_LATITUDE text, START_LONGITUDE text, END_LATITUDE text, END_LONGITUDE text);"
        );
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS records"); //같은 records table이 존재하면 drop 합니다.
        //execSQL : Execute a single SQL statement that is NOT a SELECT or any other SQL statement that returns data.

        onCreate(db);
    }

    public boolean insertRecord(String path, String date, String start_latitude, String start_longitude, String end_latitude, String end_longitude) {
        SQLiteDatabase db = this.getWritableDatabase(); //database를 open 합니다.
        ContentValues contentValues = new ContentValues(); //Creates an empty set of values using the default initial size

        contentValues.put("path", path); //field 명과 data 가 한 묶음으로 저장됩니다. Adds a value to the set.
        contentValues.put("Date", date);
        contentValues.put("START_LATITUDE", start_latitude);
        contentValues.put("START_LONGITUDE", start_longitude);
        contentValues.put("END_LATITUDE", end_latitude);
        contentValues.put("END_LONGITUDE", end_longitude);

        db.insert("records", null, contentValues); //Convenience method for inserting a row into the database.
        //위 형식대로 데이터베이스에 insert 합니다.
        return true;
    }

    public Cursor getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase(); //database를 open 합니다.
        Cursor res = db.rawQuery("select * from records where id=" + id + "", null);
        //Runs the provided SQL and returns a Cursor over the result set. 커서에 저장합니다.
        return res; //커서를 반환합니다.
    }

    //DB객체를 가지고와서 던져진 쿼리에 대한 값을 res 객체가 받아 이를 반환합니다.

    public ArrayList getAllRecords() {
        ArrayList array_list = new ArrayList();
        SQLiteDatabase db = this.getReadableDatabase(); //database를 open 합니다.
        Cursor res = db.rawQuery("select * from records", null); //SQL문을 실행하고, 그 값들에 대해서 커서에 저장됩니다.
        res.moveToFirst(); //첫번째 열로 커서가 움직입니다.
        while (res.isAfterLast() == false) { //커서가 마지막 열을 가리키고 있는지 체크
            array_list.add(
                    res.getString(res.getColumnIndex(RECORDS_COLUMN_ID)) + " " +
                            res.getString(res.getColumnIndex(RECORDS_COLUMN_DATE)));
            res.moveToNext();
            //커서의 위치를 옮겨가면서 ID, PATH를 array_list에 저장합니다.
        }
        return array_list; //반환된 array_list는 list_display.java의 array_list에 저장되면서 ArrayAdapter를 통해서 ListView를 구성하게 됩니다.
    }


}
