package org.techtown.usingmediarecorderapp;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class VideoPlay extends AppCompatActivity {
    int id = 0;
    private DBHelper mydb;
    TextView text;

    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.videoplay);
        text = (TextView) findViewById(R.id.textView03);
        mydb = new DBHelper(this);
        Bundle extras = getIntent().getExtras();
        /*Intent로 넘어온값을 가져올 수 있습니다. id 값을 받아올 수 있습니다.
         * */
        if (extras != null) {
            int Value = extras.getInt("id"); //id값을 받아와서
            if (Value > 0) { //1부터 시작하므로 0보다 크고
                Cursor rs = mydb.getData(Value); //records 테이블에 Value에 해당하는 id값을 가지고 와서 Cursor에 전달합니다.

                rs.moveToFirst(); //Cursor를 제일 첫번째 행(Row)으로 이동 시킵니다, 커서가 empty시 false를 return 합니다.


                VideoView videoview = (VideoView) this.findViewById(R.id.videoview); //video file을 보여주기 위해서 VideoView class를 사용합니다. 동영상 재생 위젯
                MediaController mc = new MediaController(this); //VideoView의 컨트롤 UI를 담는 뷰입니다. 재생/정지, 되감기, 빨리감기, Progress Slider를 내장하고 있습니다.
                videoview.setMediaController(mc); //View위에서 작동하는 미디어 컨트롤러 객체
                String folder =
                        Environment.getExternalStorageDirectory().getAbsolutePath(); //외장 메모리의 절대 경로를 저장합니다.
                String start_latitude = rs.getString(rs.getColumnIndex(DBHelper.RECORDS_COLUMN_STARTLATITUDE));
                String start_longitude = rs.getString(rs.getColumnIndex(DBHelper.RECORDS_COLUMN_STARTLONGITUDE));
                String end_latitude = rs.getString(rs.getColumnIndex(DBHelper.RECORDS_COLUMN_ENDLATITUDE));
                String end_longitude = rs.getString(rs.getColumnIndex(DBHelper.RECORDS_COLUMN_ENDLONGITUDE));

                String id = rs.getString(rs.getColumnIndex(DBHelper.RECORDS_COLUMN_PATH)); //records 테이블에 해당되는 id의 path를 가지고 옵니다.
                // System.out.println("황인규 : "+ id);
                text.setText("시작위도 :" + start_latitude + "\n" + "시작경도 :" + start_longitude + "\n" + "끝위도 :" + end_latitude + "\n" + "끝경도 :" + end_longitude);
                videoview.setVideoPath(id); //id에 대한 경로를 확인한다음에
                videoview.requestFocus(); //강제로 포커스를 요청합니다.
                videoview.start(); //videoview를 시작합니다.


            }
        }
    }
}