package com.example.gongduck;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class EmergencyInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.emergency_info);
        //뒤로가기 이벤트
        Button back_btn=findViewById(R.id.btn_back_h);
        back_btn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                finish();
            }
        });


        Button hot=findViewById(R.id.e_hot);
        hot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomDialog customDialog = new CustomDialog(EmergencyInfo.this);
                customDialog.callFunction("폭염");
            }
        });

        Button cold=findViewById(R.id.e_cold);
        cold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomDialog customDialog = new CustomDialog(EmergencyInfo.this);
                customDialog.callFunction("한파");
            }
        });

        Button fire=findViewById(R.id.e_fire);
        fire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomDialog customDialog = new CustomDialog(EmergencyInfo.this);
                customDialog.callFunction("화재");
            }
        });

        Button earth=findViewById(R.id.e_earthq);
        earth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomDialog customDialog = new CustomDialog(EmergencyInfo.this);
                customDialog.callFunction("지진");
            }
        });


    }
}
