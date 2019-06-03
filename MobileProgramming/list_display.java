package org.techtown.usingmediarecorderapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class list_display extends AppCompatActivity {

    private ListView myListView;
    ArrayAdapter mAdapter;
    DBHelper mydb;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_display);
        mydb = new DBHelper(this);
        ArrayList array_list = mydb.getAllRecords();
        mAdapter =
                new ArrayAdapter(this, android.R.layout.simple_list_item_1, array_list);
        //android.R.layout.simple_list_item_1 는 안드로이드가 미리 만들어 놓은 레이아웃입니다.
        //의미는 텍스트뷰 하나로 구성된 레이아웃입니다.

        myListView = (ListView) findViewById(R.id.listView1);
        myListView.setAdapter(mAdapter);
        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long arg4) {
                String item = (String) ((ListView) parent).getItemAtPosition(position); //1~
                String[] strArray = item.split(" ");
                int id = Integer.parseInt(strArray[0]);

                Bundle dataBundle = new Bundle();
                dataBundle.putInt("id", id);
                String TAG = "";
                Log.d(TAG, "눌렸음");
                Intent intent = new Intent(getApplicationContext(), VideoPlay.class);
                intent.putExtras(dataBundle); //id값을 넘겨줍니다.
                startActivity(intent);

                /*ListView의 아이템이 클릭이 되면
                ListView의 position의 값을 item으로 저장한뒤, item을 space기준으로 splite 하여 strArray 배열에 저장합니다.
                0번째 index를 참조하는것은 id 값이 되고, 다른 acitivity에 전달하기 위해서 Bundle 객체를 생성하였습니다.
                id값을 넣어주고, intent 하여 다른 activity로 전환할 수 있습니다.
                Intent로 전달할 값은 id를 넣은 bundle 값을 인자로 전달함으로써 id값을 전달할 수 있습니다.
                * */
            }
        });

    }
}
