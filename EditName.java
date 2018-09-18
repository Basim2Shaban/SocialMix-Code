package com.basm.socialmix;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class EditName extends AppCompatActivity {
    private TextInputLayout TextName ;
    private FirebaseAuth Auth ;
    private DatabaseReference DataBase ;

    private ProgressDialog MProgressDialog ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_name);


        MProgressDialog = new ProgressDialog(this);


        Intent Value = getIntent();
        String name = Value.getStringExtra("Name");

        TextName = (TextInputLayout)findViewById(R.id.put_name);

        TextName.getEditText().setText(name);



        Button savename = (Button)findViewById(R.id.btn_save);
        savename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MProgressDialog.setTitle(R.string.ProgressChanged_Name);
                MProgressDialog.setMessage(getString(R.string.MassageProgressChangedName));
                MProgressDialog.show();

                final String NameD = TextName.getEditText().getText().toString().trim();

                FirebaseUser CurrentUser = FirebaseAuth.getInstance().getCurrentUser();
                String Uid = CurrentUser.getUid();



                DataBase = FirebaseDatabase.getInstance().getReference().child("users").child(Uid);
                Map user_map = new HashMap<>();
                user_map.put("name",NameD);

                DataBase.updateChildren(user_map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            MProgressDialog.dismiss();
                            TextName.getEditText().setText(NameD);
                            Toast.makeText(EditName.this,R.string.StatusChangedName, Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(EditName.this, R.string.StatusErrorChangedName, Toast.LENGTH_SHORT).show();
                            MProgressDialog.hide();
                        }

                    }
                });

            }
        });





    }


}
