package com.example.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {
    EditText id;
    EditText pw;
    Button login;
    Button signin;

    ProgressDialog progressDialog;

    FirebaseAuth mAuth;
    FirebaseUser fuser;
    ArrayList<String> friendList = new ArrayList<String>();
    ArrayList<String> resultList = new ArrayList<String>();

    boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

//        if(mAuth.getCurrentUser() != null){
//            finish();
//            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//            startActivityForResult(intent, 1003);
//        }

        id = (EditText)findViewById(R.id.editTextId);
        pw = (EditText)findViewById(R.id.editTextPw);
        login = (Button)findViewById(R.id.buttonLogin);
        signin = (Button)findViewById(R.id.textViewSignin);

        progressDialog = new ProgressDialog(this);

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SigninActivity.class);
                startActivityForResult(intent, 1001);
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                startActivityForResult(intent, 1002);

                if(TextUtils.isEmpty(id.getText()) || TextUtils.isEmpty(pw.getText())){
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "아이디와 비밀번호를 입력해 주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                progressDialog.setMessage("로그인중입니다. 기다려 주세요...");
                progressDialog.show();
                friendFun(id.getText().toString().trim(), pw.getText().toString().trim());
                loginFun(id.getText().toString().trim(), pw.getText().toString().trim());
            }
        });
    }

    //디비 연결
    private void conFriendDB(String uid){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("android");
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
    private void friendFun(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                conFriendDB(mAuth.getUid());
            }
        });
    }
    public void loginFun(final String email, final String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful() && !friendList.isEmpty()){
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            fuser = mAuth.getCurrentUser();
                            intent.putExtra("uid", fuser.getUid());

                            for(int i = 0; i < friendList.size(); i++){
                                if(!resultList.contains(friendList.get(i))){
                                    resultList.add(friendList.get(i));
                                }
                            }

                            intent.putExtra("friendList", resultList);

                            progressDialog.dismiss();
                            friendList.clear();
                            startActivityForResult(intent, 1002);
                        }
                        else if(!task.isSuccessful()){
                            Toast.makeText(getApplicationContext(), "계정을 확인하세요.", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            return;
                        }
                        else {
                            flag = true;
                            loginFun(email, password);
                        }
                    }
                });
    }
}
