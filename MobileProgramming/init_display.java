package org.techtown.usingmediarecorderapp;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import org.w3c.dom.Text;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class init_display extends AppCompatActivity implements SurfaceHolder.Callback, SensorEventListener {

    private static final String TAG = "";
    Button myButton;
    MediaRecorder mediaRecorder;
    SurfaceHolder surfaceHolder;
    boolean is_recording;
    private DBHelper mydb;
    String videoFile;
    String stringlongitude, stringlatitude, stringspeed;
    String start_latitude, start_longitude, end_latitude, end_longitude;
    String _date;
    int count = 0;
    private SensorManager mSensorManager;
    private Sensor mOrientation;
    double latitude, longitude, speed;
    private CompassView compass;
    TextView text;
    public static final int RECORD_AUDIO = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);
        text = (TextView) findViewById(R.id.textView01);
        //System.out.println("text check :" + text.getText().toString());
        is_recording = false;
        compass = new CompassView(this); //class view 를 현재 layout 위에 over layout 했습니다.
        LayoutInflater inflater = getLayoutInflater();
        LocationManager locationManager = (LocationManager) this
                .getSystemService(Context.LOCATION_SERVICE);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mOrientation = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        mydb = new DBHelper(this);
        //DBHelper에 대한 객체를 형성합니다.
        mediaRecorder = new MediaRecorder(); //멀티미디어를 위한 객체를 형성하고
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //permission을 확인하고,
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE}, 10);
            //권한을 요청할시 해당 함수를 즉시 리턴합니다.
        } else {
            initMediaRecorder();//mediaRecorder를 가지고 와서,
            //setContentView(R.layout.activity_main);
            SurfaceView myVideoView = (SurfaceView) findViewById(R.id.videoview);//videoview를 객체를 생성하고
            surfaceHolder = myVideoView.getHolder();//Return the SurfaceHolder providing access and control over this SurfaceView's underlying surface.
            surfaceHolder.addCallback(this); //Add a Callback interface for this holder.
            myButton = (Button) findViewById(R.id.mybutton);
            myButton.setOnClickListener(myButtonOnClickListener);
            //Button에 대한 객체를 형성한뒤 Listener를 set 했습니다.
            addContentView(compass, new ConstraintLayout.LayoutParams(500, 500));
            //text.setText("test02");
        }
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //위치 값이 갱신되면 이벤트가 발생합니다.
                //여기서 자기 위치가 나옵니다.
                //자기 위치에 대한 위도,경도 정보를 알면
                //그 위도, 경도 정보에 대한 마커를 만들어서
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                speed = location.getSpeed() * 3.6;
                //위도, 경도를 파악하고
                stringlatitude = Double.toString(latitude);
                stringlongitude = Double.toString(longitude);
                stringspeed = Double.toString(speed);
                text.setText("위도 :" + stringlatitude + "\n" + "경도 :" + stringlongitude + "\n" + "스피드 :" + stringspeed + " km/h");
                //System.out.println("위도/경도/스피드:"+text.getText().toString());
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
            //콜백 메서드로서, 반드시 불러줘야 합니다.
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) !=
                        PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) { //access location이 가능하도록 설정이 되어있는지 check 한다. 설정이 안되어있으면
            //이 위치에 대한 정보를 업데이트하는 요청을 못하는것이고 기본 코드에서 return 하도록 되어있다. 그냥 돌리면 running은 되는데 그냥 끝난다. 설정을 안해놨으니까
            //requestpermission을 요청하는 코드가 여기있습니다.
            //설정한 만들어놓은 앱 이름에 따라, 권한에 대한 정보가 있습니다. 권한에 대한 정보에 위치가 켜있어야 위치 정보를 받아올 수 있습니다.
            //실시간으로 켜주도록 하는 창을 띄울 수 있는데, onRequestPermissionResult 로
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            //Toast.makeText(this, "First enable LOCATION ACCESS in settings.", Toast.LENGTH_LONG).show();
            // ActivityCompat.requestPermissions((Activity)this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 101);return;
            //ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, RECORD_AUDIO);

        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000,
                    1, locationListener);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        SurfaceView myVideoView = (SurfaceView) findViewById(R.id.videoview); //videoview를 객체를 생성하고
        surfaceHolder = myVideoView.getHolder(); //Return the SurfaceHolder providing access and control over this SurfaceView's underlying surface.
        surfaceHolder.addCallback(this); //Add a Callback interface for this holder.
        myButton = (Button) findViewById(R.id.mybutton);
        myButton.setOnClickListener(myButtonOnClickListener);
        //Button에 대한 객체를 형성한뒤 Listener를 set 했습니다.
    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mOrientation,
                SensorManager.SENSOR_DELAY_UI);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
            compass.setAzimuth(event.values[0]);
            compass.setPitch(event.values[1]);
            compass.setRoll(-event.values[2]);
            compass.invalidate();
            //text.setText();
        }
    }

    public Button.OnClickListener myButtonOnClickListener = new Button.OnClickListener() {

        public void onClick(View arg0) {
            if (is_recording) {
                mediaRecorder.stop(); //stop recoding
                mediaRecorder.release(); //Releases resources associated with this MediaRecorder object.
                end_latitude = stringlatitude;
                end_longitude = stringlongitude;
                finish();
                //is_recording=false;
                if (mydb.insertRecord(videoFile, _date, start_latitude, start_longitude, end_latitude, end_longitude)) {
                    //stop 버튼 클릭시, 경로를 database에 insert 해주었습니다.
                    //System.out.println("황인규"+info);
                    Toast.makeText(getApplicationContext(), _date + " 파일이 추가되었습니다.", Toast.LENGTH_SHORT).show();
                    //Toast.makeText(getApplicationContext(),"녹화가 중지 되었습니다", Toast.LENGTH_SHORT.show();
                } else {

                    Toast.makeText(getApplicationContext(), "추가되지 않았음", Toast.LENGTH_SHORT).show();
                }
                //toast message로 나타내었다.

            } else {
                start_latitude = stringlatitude;
                start_longitude = stringlongitude;
                mediaRecorder.start();
                is_recording = true;
                myButton.setText("녹화중지");
                //시작과 동시에 text가 변합니다.
            }
        }
    };

    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        //서피스뷰가 변경될때 호출된다.
    }

    public void surfaceCreated(SurfaceHolder arg0) {
        prepareMediaRecorder();
        //서피스 뷰가 만들어질때 호출된다.
    }

    public void surfaceDestroyed(SurfaceHolder arg0) {
        //서비스 뷰가 종료될때 호출됩니다.
    }
    //메인 스레드가 표면(Surfcae)의 변화를 감지해서 스레드에게 그리기 허용 여부를 알려 줘야 하며, 이는 SurfaceHolder.Callback 으로 합니다.


/*
    서피스 뷰를 생성하면 디폴트로 구현해야할 메소드가 있습니다.
    public void surfaceChanged() : 뷰가 변경될 때 호출된다.
    public void surfaceCreated() : 뷰가 생성될 때 호출된다.
    public void surfaceDestroyed() : 뷰가 종료될 때 호출된다.
     */


    private void initMediaRecorder() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy년 MM월 dd일 hh시 mm분 ss초");
        String date = dateFormat.format(new Date());
        //SimpleDateFormat은 주어진 pattern을 사용합니다. 쉽게 날짜를 Formatting 합니다.
        //Date() 클래스를 SimpleDateFormat 에 format()으로 포매팅 하면 현재 날짜 시간으로 지정한 포맷으로 데이터를 변환합니다.

        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
        //AudioSource와 VideoSource를 녹음과 녹화를 하기위해서 set 합니다.

        CamcorderProfile camcorderProfile_HQ = CamcorderProfile
                .get(CamcorderProfile.QUALITY_LOW);
        //주어진 퀄리티 레벨에 따라서 디바이스의 카메라 프로파일을 리턴합니다.
        mediaRecorder.setProfile(camcorderProfile_HQ);
        //지정한 프로파일 종류의 출력 파일 포맷과 인코더 설정을 가지고 와서 mediaRecorder에서 사용할 수 있습니다.
        String folder = null;
        String ex = Environment.getExternalStorageState(); //저장된 상태를 체크해서
        if (ex.equals(Environment.MEDIA_MOUNTED)) { //외장 메모리라면
            Log.d(TAG, "외장메모리");
            folder = Environment.getExternalStorageDirectory().getAbsolutePath(); //외부 절대 경로를 folder에 저장합니다.
        } else { //그렇지 않으면
            Log.d(TAG, "내장메모리");
            folder = Environment.MEDIA_UNMOUNTED; //내장 메모리로 저장합니다.
        }
        videoFile = folder + "/" + date + ".mp4"; //경로를 생성해서
        _date = date;
        mediaRecorder.setOutputFile(videoFile); //Sets the path of the output file to be produced.
        //생성된 output file 경로를 설정합니다.
        mediaRecorder.setMaxDuration(60000); // 최대 시간을 60초로 한정한다.
        mediaRecorder.setMaxFileSize(5000000); // 최대 파일  크기를 5MB로 한정한다.
    }

    private void prepareMediaRecorder() {
        mediaRecorder.setPreviewDisplay(surfaceHolder.getSurface()); //Sets a Surface to show a preview of recorded media (video).
        try {
            mediaRecorder.prepare(); //Prepares the recorder to begin capturing and encoding data.
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class CompassView extends View {
        float azimuth = 0;
        float pitch = 0;
        float roll = 0;

        public void setAzimuth(float azimuth) {
            this.azimuth = azimuth;
        }

        public void setPitch(float pitch) {
            this.pitch = pitch;
        }

        public void setRoll(float roll) {
            this.roll = roll;
        }

        public CompassView(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(Color.YELLOW);
            canvas.save();
            canvas.rotate(-azimuth, 250, 250);
            canvas.drawCircle(250, 250, 200, paint);
            paint.setColor(Color.BLACK);
            paint.setTextSize(50);
            canvas.drawText("N", 250, 80, paint);
            canvas.drawText("S", 250, 430, paint);
            canvas.drawRect(240, 80, 260, 430, paint);
            canvas.restore();
        }
    }
}

