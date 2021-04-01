package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
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

public class CommunityActivity extends AppCompatActivity {
    Intent intent;

    ImageButton imgbtnWrite;
    ImageButton imgbtnBack;
    ListView lvCommunity;
    CheckBox cbn;

    String uid;
    String name;
    AdapterNotice mAdapter;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("android");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);

        uid = (String)getIntent().getSerializableExtra("uid");

        findId();

        mAdapter = new AdapterNotice(this, 0);
        lvCommunity.setAdapter(mAdapter);
        conNoticeDB();
        lvSel();
        btnWrite();
        btnBack();
    }
    private void lvSel(){
        lvCommunity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                intent = new Intent(getApplicationContext(), CommentActivity.class);
                intent.putExtra("notice", mAdapter.getItem(position));
                intent.putExtra("uid", uid);
                intent.putExtra("name", name);
                startActivity(intent);
            }
        });
    }

    private void conNoticeDB(){
        myRef.child("community").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                NoticeDTO noticeDTO = dataSnapshot.getValue(NoticeDTO.class);
                mAdapter.add(noticeDTO);
                lvCommunity.smoothScrollByOffset(mAdapter.getCount());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String  time = dataSnapshot.getKey();
                for (int i = 0; i < mAdapter.getCount(); i++){
                    if(mAdapter.getItem(i).getTime().equals(time)){
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
    //쓰기버튼
    private void btnWrite(){
        imgbtnWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getApplicationContext(), WriteActivity.class);
                intent.putExtra("uid", uid);
                startActivity(intent);
            }
        });
    }
    //뒤로가기 버튼
    private void btnBack(){
        imgbtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    //아이디 찾기
    private void findId(){
        imgbtnWrite = (ImageButton)findViewById(R.id.imgbtnWrite);
        imgbtnBack = (ImageButton)findViewById(R.id.imgbtnBack);
        lvCommunity = (ListView)findViewById(R.id.lvCommunity);
        cbn = (CheckBox)findViewById(R.id.cbn);
    }
}
