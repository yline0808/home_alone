package com.example.myapplication;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class SigninActivity extends AppCompatActivity {
    EditText id;
    EditText pw;
    EditText pwc;
    EditText name;
    EditText age;
    RadioButton mb;
    RadioButton fb;
    EditText height;
    EditText weight;
    Button signin;

    ProgressDialog progressDialog;

    FirebaseAuth firebaseAuth;
    FirebaseUser fuser;

    ArrayList<CourseDTO> courseDTOS = new ArrayList<CourseDTO>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        id = (EditText)findViewById(R.id.editTextId);
        pw = (EditText)findViewById(R.id.editTextPw);
        pwc = (EditText)findViewById(R.id.editTextPwCheck);
        name = (EditText)findViewById(R.id.editTextName);
        age = (EditText)findViewById(R.id.editTextAge);
        mb = (RadioButton)findViewById(R.id.radiobuttonm);
        fb = (RadioButton)findViewById(R.id.radiobuttonf);
        height = (EditText)findViewById(R.id.editTextHeight);
        weight = (EditText)findViewById(R.id.editTextWeight);
        signin = (Button)findViewById(R.id.buttonSignin);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(allSigncheck() == false){
                    return;
                }
                progressDialog.setMessage("등록중입니다. 기다려 주세요...");
                progressDialog.show();

                firebaseAuth.createUserWithEmailAndPassword(id.getText().toString().trim(), pw.getText().toString().trim())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getApplicationContext(), "가입되었습다.", Toast.LENGTH_SHORT).show();
                            courseSet();
                            dbUpdate();
                            finish();
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "이메일 형식이 잘못 되었거나 이미 있는 이메일 입니다.", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            id.setText("");
                            return;
                        }
                        progressDialog.dismiss();
                    }
                });
            }
        });
    }
    public void courseSet(){
        ArrayList<String> course1 = new ArrayList<String>();
        ArrayList<String> course2 = new ArrayList<String>();
        ArrayList<String> course3 = new ArrayList<String>();

        //초기화
        course1.add("37.885985,127.738311");
        course1.add("37.884643,127.738416");
        course1.add("37.883745,127.737649");
        course1.add("37.885087,127.736560");
        course1.add("37.885007,127.736308");
        course1.add("37.885837,127.735546");
        course1.add("37.885977,127.735805");
        course1.add("37.886860,127.737613");
        course1.add("37.887029,127.738624");
        course1.add("37.886758,127.739236");
        course1.add("37.885985,127.738311");
        courseDTOS.add(new CourseDTO("한림대 기본 A코스", 1.0, true, course1));

        course2.add("37.885985,127.738311");
        course2.add("37.885406,127.737168");
        course2.add("37.885007,127.736308");
        course2.add("37.885837,127.735546");
        course2.add("37.885977,127.735805");
        course2.add("37.886881,127.737632");
        course2.add("37.887364,127.737208");
        course2.add("37.887906,127.737932");
        course2.add("37.888439,127.737481");
        course2.add("37.888575,127.739222");
        course2.add("37.887724,127.740450");
        course2.add("37.887316,127.740650");
        course2.add("37.886558,127.739276");
        course2.add("37.885985,127.738311");
        courseDTOS.add(new CourseDTO("한림대 기본 B코스", 1.5, true, course2));

        course3.add("37.885985,127.738311");
        course3.add("37.884677,127.738428");
        course3.add("37.883911,127.737490");
        course3.add("37.884233,127.737265");
        course3.add("37.883996,127.736750");
        course3.add("37.884258,127.736600");
        course3.add("37.884407,127.736864");
        course3.add("37.884817,127.736546");
        course3.add("37.884774,127.736460");
        course3.add("37.884875,127.736321");
        course3.add("37.884715,127.735988");
        course3.add("37.885634,127.735129");
        course3.add("37.885977,127.735805");
        course3.add("37.886881,127.737632");
        course3.add("37.887364,127.737208");
        course3.add("37.887906,127.737932");
        course3.add("37.888439,127.737481");
        course3.add("37.888575,127.739222");
        course3.add("37.887724,127.740450");
        course3.add("37.887316,127.740650");
        course3.add("37.887316,127.740650");
        course3.add("37.886036,127.740512");
        course3.add("37.885968,127.739715");
        course3.add("37.886233,127.738704");
        course3.add("37.885985,127.738311");
        courseDTOS.add(new CourseDTO("한림대 기본 C코스", 2.0, true, course3));
    }
    public void dbUpdate(){
        fuser = firebaseAuth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference myRef = database.getReference("android").child("user");
        UserDTO user = new UserDTO(fuser.getUid(), name.getText().toString().trim(), Integer.parseInt(age.getText().toString().trim()), (mb.isChecked() ? "남자" : "여자"), Integer.parseInt(height.getText().toString().trim()), Integer.parseInt(weight.getText().toString().trim()), "0,0", 0);
        myRef.child(user.getUid()).setValue(user);

        myRef = database.getReference("android").child("walk");
        WalkDTO walkDTO = new WalkDTO(10000, 0);
        myRef.child(user.getUid()).setValue(walkDTO);

        myRef = database.getReference("android").child("course");
        myRef.child(user.getUid()).child(courseDTOS.get(0).getTitle()).setValue(courseDTOS.get(0));
        myRef.child(user.getUid()).child(courseDTOS.get(1).getTitle()).setValue(courseDTOS.get(1));
        myRef.child(user.getUid()).child(courseDTOS.get(2).getTitle()).setValue(courseDTOS.get(2));

        myRef = database.getReference("android").child("friend");
        myRef.child(fuser.getUid()).child("0").setValue(fuser.getUid());
    }
    public boolean signCheck(){
        return ( TextUtils.isEmpty(id.getText()) || TextUtils.isEmpty(pw.getText()) ||
                TextUtils.isEmpty(pwc.getText()) || TextUtils.isEmpty(name.getText()) ||
                TextUtils.isEmpty(age.getText()) || (!mb.isChecked() == !fb.isChecked()) ||
                TextUtils.isEmpty(height.getText()) || TextUtils.isEmpty(weight.getText()));
    }

    public boolean allSigncheck(){
        if(signCheck()){
            Toast.makeText(getApplicationContext(), "모든 항목을 입력해 주세요.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!TextUtils.equals(pw.getText(), pwc.getText())){
            Toast.makeText(getApplicationContext(), "두개의 비밀번호가 다릅니다.", Toast.LENGTH_SHORT).show();
            pwc.setText("");
            return false;
        }
        if(pw.getText().length() < 6 || pw.getText().length() > 12){
            Toast.makeText(getApplicationContext(), "비밀번호의 길이를 6~12 자리로 설정하세요.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if((Integer.parseInt(age.getText().toString()) < 1) || (Integer.parseInt(height.getText().toString()) < 1) || (Integer.parseInt(weight.getText().toString()) < 1)){
            Toast.makeText(getApplicationContext(), "나이, 키, 몸무게 항목중에 음수또는 0이 포함 되었습니다.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
