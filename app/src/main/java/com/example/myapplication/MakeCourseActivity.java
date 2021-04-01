package com.example.myapplication;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MakeCourseActivity extends AppCompatActivity {
    SupportMapFragment mapFragment;
    TextView txtvCourseTitle;
    TextView txtvMode;
    TextView txtvKm;
    TextView txtvKcal;
    TextView txtvWalk;
    ImageButton imgbtnMove;
    ImageButton imgbtnAddCourse;
    ImageButton imgbtnDeleteCourse;
    ImageButton imgbtnInfo;
    Button btnSave;
    LinearLayout llInfo;

    GoogleMap map;
    MarkerOptions mo = new MarkerOptions();

    String uid;
    boolean isInfo = false;
    CourseDTO courseDTO = new CourseDTO();

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("android");
    ArrayList<String> wg = new ArrayList<String>();
    ArrayList<LatLng> arrayCourse = new ArrayList<LatLng>();
    boolean flat = false;
    boolean isAdd = false;
    PolylineOptions po;
    Polyline makeLine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_course);

        uid = (String) getIntent().getSerializableExtra("uid");

        findId();

        setMapPermission();
        mapFragmentGetMapAsync();
        editBtnTouch();
        btnInfo();
        btnSave();
    }

    //지도
    private void mapFragmentGetMapAsync(){
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
//                Log.d("MapActivity", "GoogleMap 객체가 준비됨.");
                map = googleMap;
                map.setMyLocationEnabled(true);
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(37.881267, 127.730148), 14));

                map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        Toast.makeText(getApplicationContext(), marker.getSnippet(), Toast.LENGTH_SHORT).show();
                    }
                });
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
    //info버튼
    private void btnInfo(){
        imgbtnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isInfo){
                    btnSave.setVisibility(View.VISIBLE);
                    llInfo.setVisibility(View.INVISIBLE);
                    imgbtnInfo.setImageResource(R.drawable.info);
                }else {
                    btnSave.setVisibility(View.INVISIBLE);
                    llInfo.setVisibility(View.VISIBLE);
                    imgbtnInfo.setImageResource(R.drawable.back);
                }
                isInfo = !isInfo;
            }
        });
    }
    //edit버튼 설정
    private void editBtnTouch(){
        imgbtnMove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flat = !flat;
                if(flat){
                    imgbtnMove.setImageResource(R.drawable.mountain_true);
                    courseDTO.setFlat(flat);
                }
                else {
                    imgbtnMove.setImageResource(R.drawable.mountain_false);
                    courseDTO.setFlat(flat);
                }
            }
        });
        imgbtnAddCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgbtnAddCourse.setImageResource(R.drawable.addloc_true);
                imgbtnDeleteCourse.setImageResource(R.drawable.delete_false);
                txtvMode.setText("추가 모드");
                if(txtvMode.getText().toString().equals("추가 모드")){
                    isAdd = true;
                    addf();
                }
            }
        });
        imgbtnDeleteCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgbtnAddCourse.setImageResource(R.drawable.addloc_false);
                imgbtnDeleteCourse.setImageResource(R.drawable.delete_true);
                txtvMode.setText("삭제 모드");

                map.clear();
                arrayCourse.clear();

                if(txtvMode.getText().toString().equals("삭제 모드")){
                    isAdd = false;
                }
                if(wg.size() > 0){
                    wg.remove(wg.size() - 1);
                    courseDTO.setTitle(txtvCourseTitle.getText().toString());
                    courseDTO.setWg(wg);
                }
                convertCourse(courseDTO);
                if(!wg.isEmpty()){
                    mo = new MarkerOptions()
                            .position(arrayCourse.get(0))
                            .title("시작")
                            .anchor((float) 0.5, (float) 0.5)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.main24));
                    map.addMarker(mo);
                }
                if(arrayCourse.size() >= 2){
                    double dis = getAllDistance();
                    int walk = (int)Math.round((dis / 1000.0)/0.0008125);
                    txtvKm.setText(String.format("%.2f km", dis / 1000.0));
                    txtvWalk.setText(walk + " 보");
                    txtvKcal.setText(String.format("%.2f kcal", walk * 0.04));
                    courseDTO.setKm(Math.round(dis / 10) / 100.0);
                }else{
                    txtvKm.setText("0.00 km");
                    txtvKcal.setText("0.00 kcal");
                    txtvWalk.setText("0 보");
                }
                makeLine = map.addPolyline(new PolylineOptions()
                        .addAll(arrayCourse)
                        .width(10)
                        .color(getResources().getColor(R.color.main)));
            }
        });
    }
    private void addf(){
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if(isAdd){
                    map.clear();
                    arrayCourse.clear();

                    wg.add(latLng.latitude + "," + latLng.longitude);
                    courseDTO.setTitle(txtvCourseTitle.getText().toString());
                    courseDTO.setWg(wg);

                    convertCourse(courseDTO);

                    makeLine = map.addPolyline(new PolylineOptions()
                    .addAll(arrayCourse)
                    .width(10)
                    .color(getResources().getColor(R.color.main)));

                    if(arrayCourse.size() >= 2){
                        double dis = getAllDistance();
                        int walk = (int)Math.round((dis / 1000.0)/0.0008125);
                        txtvKm.setText(String.format("%.2f km", dis / 1000.0));
                        txtvWalk.setText(walk + " 보");
                        txtvKcal.setText(String.format("%.2f kcal", walk * 0.04));
                        courseDTO.setKm(Math.round(dis / 10) / 100.0);
                    }else{
                        txtvKm.setText("0.00 km");
                        txtvKcal.setText("0.00 kcal");
                        txtvWalk.setText("0 보");
                    }

                    if(arrayCourse.size() >= 1){
                        mo = new MarkerOptions()
                                .position(arrayCourse.get(0))
                                .title("시작")
                                .anchor((float) 0.5, (float) 0.5)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.main24));
                        map.addMarker(mo);
                    }
                }
            }
        });
    }

    //변환된 위경도 리스트화
    private void convertCourse(CourseDTO course){
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
    //id를 찾아줌
    private void findId(){
        mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        txtvCourseTitle = (TextView)findViewById(R.id.txtvCourseTitle);
        txtvMode = (TextView)findViewById(R.id.txtvMode);
        txtvKm = (TextView)findViewById(R.id.txtvKm);
        txtvKcal = (TextView)findViewById(R.id.txtvKcal);
        txtvWalk = (TextView)findViewById(R.id.txtvWalk);
        imgbtnMove = (ImageButton)findViewById(R.id.imgbtnMove);
        imgbtnAddCourse = (ImageButton)findViewById(R.id.imgbtnAddLoc);
        imgbtnDeleteCourse = (ImageButton)findViewById(R.id.imgbtnDelete);
        imgbtnInfo = (ImageButton)findViewById(R.id.imgbtnInfo);
        btnSave = (Button)findViewById(R.id.btnSave);
        llInfo = (LinearLayout)findViewById(R.id.llInfo);
    }

    @Override
    protected void onStart() {
        super.onStart();

        final EditText editText = new EditText(this);

        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        editText.setHint("코스이름을 입력하세요");

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("코스 이름 설정");
        builder.setView(editText);

        builder.setPositiveButton("코스 만들기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(!TextUtils.isEmpty(editText.getText().toString().trim())){
                    txtvCourseTitle.setText(editText.getText());
                }
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setCancelable(false);

        builder.show();
    }
    private void btnSave(){
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!arrayCourse.isEmpty()){
                    myRef.child("course").child(uid).child(courseDTO.getTitle().toString().trim()).setValue(courseDTO);

                    finish();
                }
                else {
                    Toast.makeText(getApplicationContext(), "코스를 제작후 저장해 주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private double getAllDistance(){
        double distance = 0;

        for(int i = 0; i < arrayCourse.size() - 1; i++){
            distance += getDistance(arrayCourse.get(i), arrayCourse.get(i + 1));
        }

        return distance;
    }
    private double getDistance(LatLng a, LatLng b){
        double distance = 0;

        Location locationA = new Location("A");
        locationA.setLatitude(a.latitude);
        locationA.setLongitude(a.longitude);
        Location locationB = new Location("B");
        locationB.setLatitude(b.latitude);
        locationB.setLongitude(b.longitude);
        distance = locationA.distanceTo(locationB);

        return distance;
    }
}
