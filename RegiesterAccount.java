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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class RegiesterAccount extends AppCompatActivity {
    private TextInputLayout mDesplayname ;
    private TextInputLayout mdesplayemail ;
    private TextInputLayout mDesplaypassword;

    private FirebaseAuth auth ;
    private Toolbar mtoolbaropo ;
    private ProgressDialog progressDialog ;
    private DatabaseReference mdatabase ;
    public Button MSignUp ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.regiester_account);

        mtoolbaropo= (Toolbar)findViewById(R.id.app_bar_regiester);
        setSupportActionBar(mtoolbaropo);
        getSupportActionBar().setTitle(getString(R.string.CreateAccount));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this);


        auth = FirebaseAuth.getInstance();

        mDesplayname = (TextInputLayout)findViewById(R.id.create_user_name);
        mdesplayemail = (TextInputLayout)findViewById(R.id.create_user_email);
        mDesplaypassword = (TextInputLayout)findViewById(R.id.create_user_password);
        MSignUp = (Button)findViewById(R.id.bt_signup);

        MSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = mDesplayname.getEditText().getText().toString();
                String email = mdesplayemail.getEditText().getText().toString();
                String password = mDesplaypassword.getEditText().getText().toString();

                if (!TextUtils.isEmpty(name) || !TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)){
                    progressDialog.setTitle("Registering User");
                    progressDialog.setMessage("please wait while we create your Account");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

                    Register_user(name,email,password);
                }


            }
        });


    }

    private void Register_user(final String name, String email, String password) {

        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){
                    FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                    String uid = current_user.getUid();
                    mdatabase = FirebaseDatabase.getInstance().getReference().child("users").child(uid);

                    HashMap<String,String> user_map = new HashMap<>();
                    user_map.put("name",name);
                    user_map.put("status","hi there am using Social Mix");
                    user_map.put("image","default");

                    mdatabase.setValue(user_map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                progressDialog.dismiss();
                                Intent mainactive = new Intent(RegiesterAccount.this,MainActivity.class);
                                mainactive.putExtra("name",name);
                                mainactive.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(mainactive);
                                finish();
                            }
                        }
                    });


                }else{
                    progressDialog.hide();
                    Toast.makeText(RegiesterAccount.this,R.string.Status_register, Toast.LENGTH_SHORT).show();
                }
            }
        });

      //  StorageReference filepatch = mStorageRef.child("Photos").child(uri.getLastPathSegment());
       // filepatch.putBytes(datab).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
         //   @Override
           // public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
             //   Toast.makeText(MyAccount.this, "done", Toast.LENGTH_SHORT).show();
          //  }
       // });

    }

}
