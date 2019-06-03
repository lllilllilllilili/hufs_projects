package org.techtown.usingmediarecorderapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "";
    Button Recording, List;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.init_display);
        Recording = (Button) findViewById(R.id.button2);
        List = (Button) findViewById(R.id.button);
        Recording.setOnClickListener(this);
        List.setOnClickListener(this);
        //버튼에 대한 객체를 형성합니다.(Recording, List)
        //Recording과 List에 대한 ClickListener를 set합니다.
    }

    public void onClick(View src) {
        switch (src.getId()) {
            case R.id.button2:

                Log.d(TAG, "onClick() Recording");
                Intent intent = new Intent(getApplicationContext(), init_display.class);
                startActivity(intent);
                //button2 = Recording 이 Click이 되면 init_display.class 로 intent를 설정하고
                //startActivity(intent)로 activity가 전환됩니다.
                break;

            case R.id.button:
                Log.d(TAG, "onClick() List");
                Intent intent2 = new Intent(getApplicationContext(), list_display.class);
                startActivity(intent2);
                //button = List 가 Click이 되면 list_display.class 로 intent를 설정하고
                //startActivity(intent)로 activity가 전환됩니다.
                break;
        }

    }
}

