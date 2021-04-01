package com.example.myapplication;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class WriteActivity extends AppCompatActivity {
    ImageButton imgbtnClose;
    Button btnSave;
    EditText editxtTitle;
    EditText editxtContent;
    CheckBox cbUid;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("android");

    NoticeDTO noticeDTO;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        uid = (String)getIntent().getSerializableExtra("uid");

        findId();
        btnClose();
        btnSave();
    }
    //취소 버튼
    private void btnClose(){
        imgbtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    //저장 버튼
    private void btnSave(){
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(editxtTitle.getText().toString()) || TextUtils.isEmpty(editxtContent.getText().toString())){
                    Toast.makeText(getApplicationContext(), "항목을 모두 입력해 주세요.", Toast.LENGTH_SHORT).show();
                }
                else{
                    long now = System.currentTimeMillis();
                    Date date = new Date(now);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String getTime = sdf.format(date);

                    if(cbUid.isChecked()){
                        noticeDTO = new NoticeDTO(editxtTitle.getText().toString(), "uid : " + uid + "\n" + editxtContent.getText().toString(), getTime, uid);
                    }else{
                        noticeDTO = new NoticeDTO(editxtTitle.getText().toString(), editxtContent.getText().toString(), getTime);
                    }
                    myRef.child("community").child(noticeDTO.getTime()).setValue(noticeDTO);

                    finish();
                }
            }
        });
    }
    //아이디 찾기
    private void findId(){
        imgbtnClose = (ImageButton)findViewById(R.id.imgbtnClose);
        btnSave = (Button)findViewById(R.id.btnSave);
        editxtContent = (EditText)findViewById(R.id.editxtContent);
        editxtTitle = (EditText)findViewById(R.id.editxtTitle);
        cbUid = (CheckBox)findViewById(R.id.cbUid);
    }
}
