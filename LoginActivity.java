package com.basm.socialmix;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private Toolbar ltoolbar;
    private Button login_btn;

    private TextInputLayout e_email, e_password;
    private ProgressDialog progresLogin;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);


        mAuth = FirebaseAuth.getInstance();
        progresLogin = new ProgressDialog(this);

        ltoolbar = (Toolbar) findViewById(R.id.app_bar_login);
        setSupportActionBar(ltoolbar);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        e_email = (TextInputLayout) findViewById(R.id.input_logen_email);
        e_password = (TextInputLayout) findViewById(R.id.input_logen_password);
        login_btn = (Button) findViewById(R.id.btn_login);
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = e_email.getEditText().getText().toString();
                String password = e_password.getEditText().getText().toString();
                if ((TextUtils.isEmpty(email) | !TextUtils.isEmpty(password))) {
                    progresLogin.setTitle(R.string.loging_in);
                    progresLogin.setMessage(getString(R.string.MassageLoging));
                    progresLogin.setCanceledOnTouchOutside(false);
                    progresLogin.show();
                    Login_user(email, password);
                }
            }
        });
    }

    private void Login_user(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    progresLogin.dismiss();
                    Intent IntentMain = new Intent(LoginActivity.this, MainActivity.class);
                    IntentMain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(IntentMain);
                } else {
                    progresLogin.hide();
                    Toast.makeText(LoginActivity.this, "cannot sign in please check the form and try again", Toast.LENGTH_SHORT).show();

                }
            }
        });

    }
}

