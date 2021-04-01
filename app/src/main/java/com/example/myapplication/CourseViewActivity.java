package com.example.myapplication;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class CourseViewActivity extends AppCompatActivity {
    SupportMapFragment mapFragment;
    GoogleMap map;
    ImageButton imgbtnGet;
    CourseDTO course;
    String uid;
    ArrayList<LatLng> arrayCourse = new ArrayList<LatLng>();

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("android");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_view);

        mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        imgbtnGet = (ImageButton)findViewById(R.id.imgbtnGet);
        course = (CourseDTO) getIntent().getSerializableExtra("selCourse");
        uid = (String) getIntent().getSerializableExtra("uid");
        convertCourse();        //문자 경로를 위경도로 변환
        setMapPermission();     //권한을 얻기 위함
        mapFragmentGetMapAsync();//경로지정

        imgbtnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef.child("course").child(uid).child(course.getTitle()).setValue(course);
                finish();
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
                        .color(getResources().getColor(R.color.main)));

                map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        Toast.makeText(getApplicationContext(), marker.getSnippet(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
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
}
