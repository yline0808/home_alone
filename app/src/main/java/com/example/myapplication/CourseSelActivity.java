package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class CourseSelActivity extends AppCompatActivity {
    ListView lv;
    ImageButton imgbPlus;
    ImageButton imgbSearch;
    EditText editText;

    Intent intent;
    String uid;
    AdapterCourse mAdapter;
    ArrayList<CourseDTO> cal = new ArrayList<CourseDTO>();

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_sel);

        uid = (String)getIntent().getSerializableExtra("uid");

        lv = (ListView)findViewById(R.id.lvCourse);
        imgbPlus = (ImageButton)findViewById(R.id.imgbtnPlus);
        imgbSearch = (ImageButton)findViewById(R.id.imgbtnSearch);
        editText = (EditText)findViewById(R.id.editText);

        mAdapter = new AdapterCourse(this, 0);
        lv.setAdapter(mAdapter);
        initFirebaseDatabase();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                intent = new Intent(getApplicationContext(), MapCourseActivity.class);
                intent.putExtra("selCourse", cal.get(position));
                intent.putExtra("uid", uid);
                startActivity(intent);
            }
        });
        imgbPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getApplicationContext(), MakeCourseActivity.class);
                intent.putExtra("uid", uid);
                startActivity(intent);
            }
        });
    }
    private void initFirebaseDatabase(){
        myRef = database.getReference("android");
        myRef.child("course").child(uid).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                CourseDTO courseDTO = dataSnapshot.getValue(CourseDTO.class);
                mAdapter.add(courseDTO);
                lv.smoothScrollToPosition(mAdapter.getCount());
                initAdapterToArray();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String title = dataSnapshot.getKey();
                int count = mAdapter.getCount();
                for (int i = 0; i < count; i++) {
                    if (mAdapter.getItem(i).getTitle().equals(title)) {
                        mAdapter.remove(mAdapter.getItem(i));
                        onChildAdded(dataSnapshot, s);
                        break;
                    }
                }
                initAdapterToArray();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                String title = dataSnapshot.getKey();
                int count = mAdapter.getCount();
                for (int i = 0; i < count; i++) {
                    if (mAdapter.getItem(i).getTitle().equals(title)) {
                        mAdapter.remove(mAdapter.getItem(i));
                        break;
                    }
                }
                initAdapterToArray();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void initAdapterToArray(){
        cal.clear();
        int count = mAdapter.getCount();
        for (int i = 0; i < count; i++){
            cal.add(mAdapter.getItem(i));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        myRef.child("user").child(uid).child("location").setValue("0,0");
    }
}
