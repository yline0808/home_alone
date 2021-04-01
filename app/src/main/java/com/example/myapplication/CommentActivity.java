package com.example.myapplication;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CommentActivity extends AppCompatActivity {
    ImageButton imgbtnBack;
    ImageButton imgbtnSend;
    TextView txtvTitle;
    TextView txtvContent;
    TextView txtvTime;
    TextView txtvCommentCnt;
    EditText editTxtComment;
    ListView lvComment;
    CheckBox cbn;

    String uid;
    String name;
    NoticeDTO notice;
    CommentDTO comment;
    AdapterComment mAdapter;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("android");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        notice = (NoticeDTO) getIntent().getSerializableExtra("notice");
        uid = (String) getIntent().getSerializableExtra("uid");
        name = (String) getIntent().getSerializableExtra("name");

        conMyDB();
        conDB();
        findId();

        mAdapter = new AdapterComment(this, 0);
        lvComment.setAdapter(mAdapter);

        btnBack();
        btnSend();
    }
    private void conMyDB(){
        myRef.child("user").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.getKey().equals(uid)){
                    UserDTO me = dataSnapshot.getValue(UserDTO.class);
                    name = me.getName();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

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
    private void conDB(){
        myRef.child("community").child(notice.getTime()).child("comment").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                CommentDTO commentDTO = dataSnapshot.getValue(CommentDTO.class);
                mAdapter.add(commentDTO);
                lvComment.smoothScrollByOffset(mAdapter.getCount());
                myRef.child("community").child(notice.getTime()).child("commentCnt").setValue(mAdapter.getCount());
                txtvCommentCnt.setText("" + mAdapter.getCount());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String time = dataSnapshot.getKey();
                for(int i = 0; i < mAdapter.getCount(); i++){
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
    //뒤로 가기 버튼
    private void btnBack(){
        imgbtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    //보내기 버튼
    private void btnSend(){
        imgbtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(editTxtComment.getText().toString())){
                    Toast.makeText(getApplicationContext(), "댓글을 입력해 주세요.", Toast.LENGTH_SHORT).show();
                }else {
                    long now = System.currentTimeMillis();
                    Date date = new Date(now);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String getTime = sdf.format(date);

                    if(cbn.isChecked()){
                        comment = new CommentDTO("익명", editTxtComment.getText().toString(), getTime);
                    }else {
                        comment = new CommentDTO(name, editTxtComment.getText().toString(), getTime);
                    }
                    myRef.child("community").child(notice.getTime()).child("comment").child(getTime).setValue(comment);
                    editTxtComment.setText("");
                }
            }
        });
    }
    //아이디 찾기
    private void findId(){
        imgbtnBack = (ImageButton)findViewById(R.id.imgbtnBack);
        imgbtnSend = (ImageButton)findViewById(R.id.imgbtnSend);
        txtvTitle = (TextView)findViewById(R.id.txtvTitle);
        txtvContent = (TextView)findViewById(R.id.txtvContent);
        txtvTime = (TextView)findViewById(R.id.txtvTime);
        txtvCommentCnt = (TextView) findViewById(R.id.txtvCommentCnt);
        editTxtComment = (EditText)findViewById(R.id.edittxtComment);
        lvComment = (ListView)findViewById(R.id.lvComment);
        cbn = (CheckBox)findViewById(R.id.cbn);

        txtvTitle.setText(notice.getTitle());
        txtvContent.setText(notice.getContent());
        txtvTime.setText(notice.getTime());
    }
}
