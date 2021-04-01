package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class QrActivity extends AppCompatActivity {
    IntentIntegrator qrScan;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);

        qrScan = new IntentIntegrator(this); //스캔
        qrScan.setOrientationLocked(true); // default = 세로모드, 휴대폰 방향에 따라 가로, 세로로 자동 변경
        qrScan.setPrompt("QR CODE");
        qrScan.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        try {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(result.getContents()));
            startActivity(intent);
        }catch (Exception e){}
        finish();
    }
}
