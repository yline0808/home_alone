package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MapCourseActivity extends AppCompatActivity implements SensorEventListener {
    SupportMapFragment mapFragment;
    ImageView imgvNow;
    ImageButton imgbtnSetting;
    ImageButton imgbtnqr;
    TextView txtvMyLoc;
    TextView txtvPT;
    TextView txtvWalk;
    TextView txtvKm;
    TextView txtvKcal;
    TextView txtvCourseTitle;
    Button btnStart;
    LinearLayout llSetting;
    Switch swcLocMy;
    Switch swcLocFr;
    ProgressBar pgbProgress;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("android");
    SensorManager sensorManager;
    Sensor stepCountSensor;

    GoogleMap map;
    ArrayList<Marker> arrayMarker = new ArrayList<Marker>();
    ArrayList<LatLng> arrayCourse = new ArrayList<LatLng>();
    ArrayList<LatLng> arrayMyWay = new ArrayList<LatLng>();
    ArrayList<UserDTO> arrayFUser = new ArrayList<UserDTO>();
    ArrayList<String> friendList = new ArrayList<String>();

    CourseDTO course;
    String uid;
    boolean isSettingOn = false;
    boolean isStart = false;

    LocationManager lm;

    boolean[] checkCourse;
    double longitude = 0;
    double latitude = 0;
    UserDTO me;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_course);

        course = (CourseDTO) getIntent().getSerializableExtra("selCourse");
        checkCourse = new boolean[course.getWg().size()];
        uid = (String) getIntent().getSerializableExtra("uid");

        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        conFriendDB();          //친구목록
        setMyLocation();        //gps리스너
        convertCourse();        //문자 경로를 위경도로 변환
        setMapPermission();     //권한을 얻기 위함
        findId();               //각 뷰들을 찾음
        btnQr();
        mapFragmentGetMapAsync();//경로지정
        settingOnOff();         //설정 보여주기
        startStopBtn();         //시작 종료 버튼
        sendMyLocation();       //내위치 친구에게 보내기
        findFriendLocation();   //친구위치 보기
        saveFriendDB();         //친구 정보
    }
    //qr코드
    private void btnQr(){
        imgbtnqr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), QrActivity.class);
                startActivity(intent);
            }
        });
    }
    //내위치 친구에게 보내기
    private void sendMyLocation(){
        swcLocMy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    txtvMyLoc.setText("내위치 표시중");
                    myRef.child("user").child(uid).child("location").setValue(String.format("%.6f,%.6f", latitude, longitude));
                }else{
                    txtvMyLoc.setText("내위치 가려짐");
                    myRef.child("user").child(uid).child("location").setValue("0,0");
                }
            }
        });
    }
    //친구위치 보기
    private void findFriendLocation(){
        swcLocFr.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    for(int i = 0; i < arrayMarker.size(); i++){
                        arrayMarker.get(i).setVisible(true);
                    }
                }else {
                    for(int i = 0; i < arrayMarker.size(); i++){
                        arrayMarker.get(i).setVisible(false);
                    }
                }
            }
        });
    }
    private void saveFriendDB(){
        myRef.child("user").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                for(int i = 0; i < friendList.size(); i++){
                    if(friendList.get(i).equals(dataSnapshot.getKey())){
                        UserDTO userDTO = dataSnapshot.getValue(UserDTO.class);
                        MarkerOptions mo = new MarkerOptions().position(new LatLng(convertWG(userDTO.getLocation())[0], convertWG(userDTO.getLocation())[1]))
                                .title(userDTO.getName())
                                .anchor((float) 0.5, (float) 0.5)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.main24))
                                .snippet(userDTO.getWalk() + " 보");
                        arrayFUser.add(userDTO);
                        arrayMarker.add(map.addMarker(mo));
                        break;
                    }
                }
                UserDTO user = dataSnapshot.getValue(UserDTO.class);
                if(uid.equals(user.getUid())){
                    me = user;
                    readWalkDB();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String str = dataSnapshot.getKey();
                for(int i = 0; i < arrayFUser.size(); i++){
                    if(arrayFUser.get(i).getUid().equals(str)){
                        arrayMarker.get(i).remove();
                        arrayMarker.remove(i);
                        arrayFUser.remove(i);
                        onChildAdded(dataSnapshot, s);
                        break;
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    //시작, 중지버튼
    private void startStopBtn() {
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isStart) {
                    btnStart.setBackgroundResource(R.drawable.button_background);
                    btnStart.setText("시  작");
                } else {
                    btnStart.setBackgroundResource(R.drawable.button_background2);
                    btnStart.setText("중  지");
                }
                isStart = !isStart;
            }
        });
    }
    //내위치 설정
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setMyLocation(){
        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                100,
                1,
                mLocationListener);
    }
    //gps리스너
    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            longitude = location.getLongitude();
            latitude = location.getLatitude();

            if(swcLocMy.isChecked()){
                myRef.child("user").child(uid).child("location").setValue(String.format("%.6f,%.6f", latitude, longitude));
            }else {
                myRef.child("user").child(uid).child("location").setValue("0,0");
            }

            int i;

            for(i = 0; i < arrayCourse.size() - 1; i++) {
                if (arrayCourse.get(i).longitude - 0.0001 <= longitude && longitude <= arrayCourse.get(i).longitude + 0.0001 &&
                        arrayCourse.get(i).latitude - 0.0001 <= latitude && latitude <= arrayCourse.get(i).latitude + 0.0001 && isStart) {
                    if (!checkCourse[i]) {
                        arrayMyWay.add(arrayCourse.get(i));

                        checkCourse[i] = true;

                        pgbProgress.setProgress(pgbProgress.getProgress()+1);
                        txtvPT.setText(String.format("%.2f", (100.0 * pgbProgress.getProgress() / pgbProgress.getMax())));

                        Polyline myLine = map.addPolyline(new PolylineOptions()
                                .addAll(arrayMyWay)
                                .width(10)
                                .color(getResources().getColor(R.color.main)));
                    }
                    break;
                }
            }
            int last = arrayCourse.size() - 1;
            if(checkCourse[arrayCourse.size() - 2] && !checkCourse[last] &&
                    arrayCourse.get(last).longitude - 0.0001 <= longitude && longitude <= arrayCourse.get(last).longitude + 0.0001 &&
                    arrayCourse.get(last).latitude - 0.0001 <= latitude && latitude <= arrayCourse.get(last).latitude + 0.0001){
                arrayMyWay.add(arrayCourse.get(last));
                checkCourse[last] = true;
                pgbProgress.setProgress(pgbProgress.getProgress()+1);
                txtvPT.setText(String.format("%.2f", (100.0 * pgbProgress.getProgress() / pgbProgress.getMax())));

                Polyline myLine = map.addPolyline(new PolylineOptions()
                        .addAll(arrayMyWay)
                        .width(10)
                        .color(getResources().getColor(R.color.main)));
            }
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
    };
    //디비 연결
    private void conFriendDB(){
        myRef.child("friend").child(uid).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                friendList.add(dataSnapshot.getValue().toString().trim());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String friend = dataSnapshot.getValue().toString().trim();

                int cnt = friendList.size();
                for(int i = 0; i < cnt; i++){
                    if(friendList.get(i).equals(friend)){
                        friendList.remove(i);
                        break;
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    //설정, 저장 버튼
    private void settingOnOff(){
        imgbtnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isSettingOn){
                    llSetting.setVisibility(View.INVISIBLE);
                    btnStart.setVisibility(View.VISIBLE);
                    imgbtnSetting.setImageResource(R.drawable.setting);
                }else {
                    llSetting.setVisibility(View.VISIBLE);
                    btnStart.setVisibility(View.INVISIBLE);
                    imgbtnSetting.setImageResource(R.drawable.save);
                }
                isSettingOn = !isSettingOn;
            }
        });
    }
    //지도 퍼미션 메서드
    private void setMapPermission(){
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //권한이 없을 경우 최초 권한 요청 또는 사용자에 의한 재요청 확인
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION) &&
                    ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)) {
                // 권한 재요청
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
                return;
            }
            else {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
                return;
            }
        }
    }
    //지도 & 경로 설정
    private void mapFragmentGetMapAsync(){
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
//                Log.d("MapActivity", "GoogleMap 객체가 준비됨.");
                map = googleMap;
                map.setMyLocationEnabled(true);
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(arrayCourse.get(0), 16));

                Polyline line = map.addPolyline(new PolylineOptions()
                        .addAll(arrayCourse)
                        .width(10)
                        .color(getResources().getColor(R.color.gray)));

                map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        Toast.makeText(getApplicationContext(), marker.getSnippet(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
    //뷰 아이디 찾기
    private void findId(){
        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        stepCountSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        imgvNow = (ImageView)findViewById(R.id.imgvNow);
        imgbtnSetting = (ImageButton) findViewById(R.id.imgbtnSetting);
        imgbtnqr = (ImageButton)findViewById(R.id.imgbtnqr);
        txtvMyLoc = (TextView) findViewById(R.id.txtvMyLoc);
        txtvPT = (TextView) findViewById(R.id.txtvPT);
        txtvWalk = (TextView)findViewById(R.id.txtvWalk);
        txtvKm = (TextView)findViewById(R.id.txtvKm);
        txtvKcal = (TextView)findViewById(R.id.txtvKcal);
        txtvCourseTitle = (TextView)findViewById(R.id.txtvCourseTitle);
        btnStart = (Button) findViewById(R.id.btnStart);
        llSetting = (LinearLayout) findViewById(R.id.llSetting);
        swcLocMy = (Switch) findViewById(R.id.swcLocMy);
        swcLocFr = (Switch) findViewById(R.id.swcLocFr);
        pgbProgress = (ProgressBar)findViewById(R.id.pgbProgress);

        pgbProgress.setMax(arrayCourse.size());
        txtvCourseTitle.setText(course.getTitle());
    }
    //변환된 위경도 리스트화
    private void convertCourse(){
        for(int i = 0; i < course.getWg().size(); i++){
            arrayCourse.add(new LatLng(convertWG(course.getWg().get(i))[0], convertWG(course.getWg().get(i))[1]));
        }
    }
    //텍스트 위경도 변환
    private double[] convertWG(String str){
        double[] wg = new double[2];
        String[] arr = str.split(",");

        wg[0] = Double.parseDouble(arr[0].trim());
        wg[1] = Double.parseDouble(arr[1].trim());

        return wg;
    }
    //사용자정보 읽기
    private void readWalkDB(){
        myRef.child("walk").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if(uid.equals(dataSnapshot.getKey())){
                    int now = dataSnapshot.getValue(WalkDTO.class).getNow();

                    //칼로리 계산
                    double kcal_1 = getWalkToKcal();
                    double km_1 = getWalkKm();

                    txtvWalk.setText(now + " 보");
                    txtvKm.setText(String.format("%.2f km", now * km_1));
                    txtvKcal.setText(String.format("%.2f kcal", now * kcal_1));
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                onChildAdded(dataSnapshot, s);

//                String firebaseKey = dataSnapshot.getKey();
//                int count = alUserDTO.size();
//                for(int i = 0; i < count; i++){
//                    if(alUserDTO.get(i).getUid().equals(firebaseKey)){
//                        alUserDTO.remove(alUserDTO.get(i));
//                        onChildAdded(dataSnapshot, s);
//                        break;
//                    }
//                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//                String firebaseKey = dataSnapshot.getKey();
//                int count = alUserDTO.size();
//                for(int i = 0; i < count; i++){
//                    if(alUserDTO.get(i).getUid().equals(firebaseKey)){
//                        alUserDTO.remove(alUserDTO.get(i));
//                        break;
//                    }
//                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private double getWalkKm(){
        double result;
        if(me.getSex().equals("남자")){
            result = 0.000925;
        }else {
            result = 0.0007;
        }
        return result;
    }

    private double getWalkToKcal(){
        double result;
        if(me.getSex().equals("남자")){
            if(me.getWeight() < 45){
                result = 0.0184;
            }else if(45 <= me.getWeight() && me.getWeight() < 56){
                result = 0.0232;
            }else if(56 <= me.getWeight() && me.getWeight() < 68){
                result = 0.028;
            }else if(68 <= me.getWeight() && me.getWeight() < 79){
                result = 0.0328;
            }else if(79 <= me.getWeight() && me.getWeight() < 90){
                result = 0.0376;
            }else {
                result = 0.0416;
            }
        }else if(me.getSex().equals("여자")){
            if(me.getWeight() < 45){
                result = 0.023;
            }else if(45 <= me.getWeight() && me.getWeight() < 56){
                result = 0.029;
            }else if(56 <= me.getWeight() && me.getWeight() < 68){
                result = 0.035;
            }else if(68 <= me.getWeight() && me.getWeight() < 79){
                result = 0.041;
            }else if(79 <= me.getWeight() && me.getWeight() < 90){
                result = 0.047;
            }else {
                result = 0.052;
            }
        }else if(me.getSex().isEmpty()){
            result = 1;
        }else {result = 0;}

        return result;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_STEP_COUNTER){
            myRef.child("user").child(uid).child("walk").setValue((int)event.values[0]);
            myRef.child("walk").child(uid).child("now").setValue((int)event.values[0]);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try{
        sensorManager.registerListener(this, stepCountSensor, SensorManager.SENSOR_DELAY_NORMAL);}
        catch (Exception e){Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();}
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
