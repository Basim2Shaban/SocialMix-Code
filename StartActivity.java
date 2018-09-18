package com.basm.socialmix;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Button createAccount = (Button)findViewById(R.id.bt_create_account);
        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentCreate = new Intent(StartActivity.this,RegiesterAccount.class);
                startActivity(intentCreate);
            }
        });

        Button login_start_btn = (Button)findViewById(R.id.bt_loging);
        login_start_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent logen = new Intent(StartActivity.this,LoginActivity.class);
                startActivity(logen);
            }
        });
    }
}
