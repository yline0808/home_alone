package com.example.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
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

import java.util.ArrayList;

public class MyPageActivity extends AppCompatActivity {
    String uid;

    ImageButton imgbtnBack;
    ImageButton imgbtnFriend;
    TextView txtvName;
    TextView txtvUid;
    TextView txtvSex;
    TextView txtvAge;
    TextView txtvHeight;
    TextView txtvWeight;
    ListView lvFriend;

    ArrayList<String> friendList = new ArrayList<String>();
    AdapterFriend mAdapter;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("android");
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);

        uid = (String)getIntent().getSerializableExtra("uid");

        findId();
        conFriendDB();
        saveFriendDB();

        addFriendBtn();
        backBtn();
        lvSel();
    }
    private void backBtn(){
        imgbtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    //친구 추가
    private void addFriendBtn(){
        imgbtnFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFriend();
            }
        });
    }
    //친구 추가
    private void addFriend(){
        final EditText editText = new EditText(this);

        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        editText.setHint("친구의 UID를 입력하세요.");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(editText);
        builder.setTitle("친구 추가");
        builder.setMessage("찾을 친구의 UID를 입력해주세요!");
        builder.setPositiveButton("추가", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                myRef.child("friend").child(uid).push().setValue(editText.getText().toString().trim());
                Toast.makeText(getApplicationContext(), "친구가 추가 되었습니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        builder.show();
    }
    private void saveFriendDB(){
        myRef.child("user").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                for(int i = 0; i < friendList.size(); i++){
                    if(friendList.get(i).equals(dataSnapshot.getKey())){
                        UserDTO userDTO = dataSnapshot.getValue(UserDTO.class);
                        FriendDTO friendDTO = new FriendDTO(userDTO.getName(), userDTO.getLocation(), userDTO.getWalk(), userDTO.getUid());
                        mAdapter.add(friendDTO);
                        lvFriend.smoothScrollByOffset(mAdapter.getCount());
                        break;
                    }
                }
                if(dataSnapshot.getKey().equals(uid)){
                    UserDTO me = dataSnapshot.getValue(UserDTO.class);
                    txtvName.setText(me.getName());
                    txtvUid.setText(uid);
                    txtvSex.setText(me.getSex());
                    txtvAge.setText(""+me.getAge());
                    txtvHeight.setText(""+me.getHeight());
                    txtvWeight.setText(""+me.getWeight());
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
    //리스트 뷰 선택
    private void lvSel(){
        lvFriend.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                intent = new Intent(getApplicationContext(), GetCourseActivity.class);
                intent.putExtra("fuid", mAdapter.getItem(position).getFuid());
                intent.putExtra("uid", uid);
                startActivity(intent);
            }
        });
    }
    //아이디찾기
    private void findId(){
        imgbtnBack = (ImageButton)findViewById(R.id.imgbtnBack);
        imgbtnFriend = (ImageButton)findViewById(R.id.imgbtnAddFriend);
        txtvName = (TextView)findViewById(R.id.txtvName);
        txtvUid = (TextView)findViewById(R.id.txtvUid);
        txtvSex = (TextView)findViewById(R.id.txtvSex);
        txtvAge = (TextView)findViewById(R.id.txtvAge);
        txtvHeight = (TextView)findViewById(R.id.txtvHeight);
        txtvWeight = (TextView)findViewById(R.id.txtvWeight);
        lvFriend = (ListView)findViewById(R.id.lvFriend);

        mAdapter = new AdapterFriend(this, 0);
        lvFriend.setAdapter(mAdapter);
    }
}
