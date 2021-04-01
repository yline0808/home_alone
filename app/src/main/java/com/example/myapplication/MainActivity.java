package com.example.myapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("android");
    ArrayList<String> friendList = new ArrayList<String>();
    AdapterFriend mAdapter = null;

    String uid;

    ScrollView scrollView;
    ImageButton imgbtnGoal;
    ImageButton imgbtnMyPage;

    LinearLayout llWaySelect;
    LinearLayout llWayMake;
    LinearLayout llCommunity;

    ProgressBar progressBar;
    TextView textViewPg;

    TextView textViewWalk1;
    TextView textViewWalk2;

    ListView listView;

    ProgressBar[] progressBarWeek = new ProgressBar[7];
    TextView[] textViewWeek1 = new TextView[7];
    TextView[] textViewWeek2 = new TextView[7];

    Intent intent;

    SensorManager sensorManager;
    Sensor stepCountSensor;

    UserDTO me;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        uid = (String)getIntent().getSerializableExtra("uid");
        friendList = (ArrayList<String>)getIntent().getSerializableExtra("friendList");

        for(int i = 0; i < friendList.size(); i++){
            if(uid.equals(friendList.get(i))){
                friendList.remove(i);
                myRef.child("friend").child(uid).child("0").setValue(null);
                break;
            }
        }

        findId();
        holdListView();
        conUserDB();

        imgbtnGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingGoal();
            }
        });
        llWaySelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getApplicationContext(), CourseSelActivity.class);
                intent.putExtra("uid", uid);
                startActivity(intent);
            }
        });
        llWayMake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getApplicationContext(), MakeCourseActivity.class);
                intent.putExtra("uid", uid);
                startActivity(intent);
            }
        });
        llCommunity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getApplicationContext(), CommunityActivity.class);
                intent.putExtra("uid", uid);
                startActivity(intent);
            }
        });
        imgbtnMyPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getApplicationContext(), MyPageActivity.class);
                intent.putExtra("uid", uid);
                startActivity(intent);
            }
        });
    }


    private void conUserDB(){
        myRef.child("user").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                for(int i = 0; i < friendList.size(); i++){
                    if(friendList.get(i).equals(dataSnapshot.getKey())){
                        UserDTO userDTO = dataSnapshot.getValue(UserDTO.class);
                        FriendDTO friendDTO = new FriendDTO(userDTO.getName(), userDTO.getLocation(), userDTO.getWalk(), userDTO.getUid());
                        mAdapter.add(friendDTO);
                        listView.smoothScrollByOffset(mAdapter.getCount());
                        break;
                    }
                }

                UserDTO userDTO = dataSnapshot.getValue(UserDTO.class);
                if(uid.equals(userDTO.getUid())){
                    me = userDTO;
                    readWalkDB();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String key = dataSnapshot.getKey();
                for(int i = 0; i < mAdapter.getCount(); i++){
                    if(mAdapter.getItem(i).getFuid().equals(key)){
                        mAdapter.remove(mAdapter.getItem(i));
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

    //사용자정보 읽기
    private void readWalkDB(){
        myRef.child("walk").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if(uid.equals(dataSnapshot.getKey())){
                    progressBar.setMax(dataSnapshot.getValue(WalkDTO.class).getGoal());
                    progressBar.setProgress(dataSnapshot.getValue(WalkDTO.class).getNow());
                    textViewPg.setText(String.format("%.2f", (100.0 * dataSnapshot.getValue(WalkDTO.class).getNow() / dataSnapshot.getValue(WalkDTO.class).getGoal())) + "%");
                    textViewWalk1.setText(dataSnapshot.getValue(WalkDTO.class).getNow() + " / " + dataSnapshot.getValue(WalkDTO.class).getGoal());

                    //칼로리 계산
                    double kcal_1 = getWalkToKcal();
                    double km_1 = getWalkKm();

                    textViewWalk2.setText(String.format("(%.2f kcal, %.2f km)", dataSnapshot.getValue(WalkDTO.class).getNow() * kcal_1, dataSnapshot.getValue(WalkDTO.class).getNow() * km_1) );

                    for(int i = 0; i < 7; i++){
                        progressBarWeek[i].setProgress(dataSnapshot.getValue(WalkDTO.class).getWeek().get(i));
                        progressBarWeek[i].setMax((int)dataSnapshot.getValue(WalkDTO.class).getGoal());

                        if(dataSnapshot.getValue(WalkDTO.class).getWeek().get(i) == 0){
                            textViewWeek1[i].setText(dataSnapshot.getValue(WalkDTO.class).getWeek().get(i) + " 걸음(" + "0 kcal, " + "0 km)");
                            textViewWeek2[i].setText("0%");
                        }
                        else{
                            textViewWeek1[i].setText(String.format("%d 걸음(%.2f kcal, %.2f km)", dataSnapshot.getValue(WalkDTO.class).getWeek().get(i), (float)dataSnapshot.getValue(WalkDTO.class).getWeek().get(i) * kcal_1, (float)dataSnapshot.getValue(WalkDTO.class).getWeek().get(i) * km_1));
                            textViewWeek2[i].setText(String.format("%.2f", (100.0 * dataSnapshot.getValue(WalkDTO.class).getWeek().get(i) / dataSnapshot.getValue(WalkDTO.class).getGoal())) + "%");
                        }
                    }
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

    //목표걸음 설정
    private void settingGoal(){
        final EditText editTextGoal = new EditText(this);

        editTextGoal.setInputType(InputType.TYPE_CLASS_NUMBER);
        editTextGoal.setHint("현재 목표 : " + progressBar.getMax() + " 보");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("목표 걸음");
        builder.setMessage("일일 목표 걸음수를 설정 하세요!");
        builder.setView(editTextGoal);
        builder.setPositiveButton("저장",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        myRef.child("walk").child(uid).child("goal").setValue(Integer.parseInt(editTextGoal.getText().toString().trim()));
                        Toast.makeText(getApplicationContext(), "설정 완료", Toast.LENGTH_SHORT).show();
                    }
                });
        builder.setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.show();
    }

    //스크롤뷰와 리스트뷰 고정
    private void holdListView() {
        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                scrollView.requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
    }
    //변수아이디잡기
    private void findId(){
        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        stepCountSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        scrollView = (ScrollView)findViewById(R.id.scrollView);
        imgbtnGoal = (ImageButton)findViewById(R.id.imageButtonGoal);
        imgbtnMyPage = (ImageButton)findViewById(R.id.imageButtonMyPage);

        llWaySelect = (LinearLayout)findViewById(R.id.waySelect);
        llWayMake = (LinearLayout)findViewById(R.id.wayMake);
        llCommunity = (LinearLayout)findViewById(R.id.community);

        progressBar = (ProgressBar)findViewById(R.id.progressBarDay);
        textViewPg = (TextView)findViewById(R.id.textViewPg);
        textViewWalk1 = (TextView)findViewById(R.id.textViewWalk1);
        textViewWalk2 = (TextView)findViewById(R.id.textViewWalk2);

        listView = (ListView)findViewById(R.id.listViewRanking);

        progressBarWeek[0] = (ProgressBar)findViewById(R.id.progressBarWeek0);
        progressBarWeek[1] = (ProgressBar)findViewById(R.id.progressBarWeek1);
        progressBarWeek[2] = (ProgressBar)findViewById(R.id.progressBarWeek2);
        progressBarWeek[3] = (ProgressBar)findViewById(R.id.progressBarWeek3);
        progressBarWeek[4] = (ProgressBar)findViewById(R.id.progressBarWeek4);
        progressBarWeek[5] = (ProgressBar)findViewById(R.id.progressBarWeek5);
        progressBarWeek[6] = (ProgressBar)findViewById(R.id.progressBarWeek6);

        textViewWeek1[0] = (TextView)findViewById(R.id.textViewWalkInfo0);
        textViewWeek1[1] = (TextView)findViewById(R.id.textViewWalkInfo1);
        textViewWeek1[2] = (TextView)findViewById(R.id.textViewWalkInfo2);
        textViewWeek1[3] = (TextView)findViewById(R.id.textViewWalkInfo3);
        textViewWeek1[4] = (TextView)findViewById(R.id.textViewWalkInfo4);
        textViewWeek1[5] = (TextView)findViewById(R.id.textViewWalkInfo5);
        textViewWeek1[6] = (TextView)findViewById(R.id.textViewWalkInfo6);

        textViewWeek2[0] = (TextView)findViewById(R.id.textViewWalkP0);
        textViewWeek2[1] = (TextView)findViewById(R.id.textViewWalkP1);
        textViewWeek2[2] = (TextView)findViewById(R.id.textViewWalkP2);
        textViewWeek2[3] = (TextView)findViewById(R.id.textViewWalkP3);
        textViewWeek2[4] = (TextView)findViewById(R.id.textViewWalkP4);
        textViewWeek2[5] = (TextView)findViewById(R.id.textViewWalkP5);
        textViewWeek2[6] = (TextView)findViewById(R.id.textViewWalkP6);

        mAdapter = new AdapterFriend(this, 0);
        listView.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, stepCountSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_STEP_COUNTER){
            Date currentTime = Calendar.getInstance().getTime();
            SimpleDateFormat weekdayFormat = new SimpleDateFormat("EE", Locale.getDefault());
            String weekDay = weekdayFormat.format(currentTime);
            switch (weekDay){
                case "월":
                    myRef.child("walk").child(uid).child("week").child("0").setValue((int)event.values[0]);
                    break;
                case "화":
                    myRef.child("walk").child(uid).child("week").child("1").setValue((int)event.values[0]);
                    break;
                case "수":
                    myRef.child("walk").child(uid).child("week").child("2").setValue((int)event.values[0]);
                    break;
                case "목":
                    myRef.child("walk").child(uid).child("week").child("3").setValue((int)event.values[0]);
                    break;
                case "금":
                    myRef.child("walk").child(uid).child("week").child("4").setValue((int)event.values[0]);
                    break;
                case "토":
                    myRef.child("walk").child(uid).child("week").child("5").setValue((int)event.values[0]);
                    break;
                case "일":
                    myRef.child("walk").child(uid).child("week").child("6").setValue((int)event.values[0]);
                    break;
            }

            myRef.child("walk").child(uid).child("now").setValue((int)event.values[0]);
            myRef.child("user").child(uid).child("walk").setValue((int)event.values[0]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
