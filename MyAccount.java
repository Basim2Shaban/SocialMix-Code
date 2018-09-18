package com.basm.socialmix;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class MyAccount extends AppCompatActivity {

    private ProgressDialog mprogressdialogg;

    private DatabaseReference mUserDatabase;
    private StorageReference mStorageRef;
    private FirebaseUser mCurrentUser;
    private Button BtnEName, BtnEStatus, ChangeImage;
    private TextView GetName, GetStatus;

    private FirebaseUser UserF;
    private DatabaseReference mDataBase;
    private CircleImageView imageView;

    private static final int galory_pick = 1;
    private byte[] datab ;
    private String userEmail , image , name;
    private DatabaseReference PUserData;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

        UserF = FirebaseAuth.getInstance().getCurrentUser();
        final String UId = UserF.getUid();



        userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(UId);






        imageView = (CircleImageView) findViewById(R.id.image_my_account);
        ChangeImage = (Button) findViewById(R.id.bt_change_image);


        GetName = (TextView) findViewById(R.id.text_view_name);
        GetStatus = (TextView) findViewById(R.id.text_display_status);



        mDataBase = FirebaseDatabase.getInstance().getReference().child("users").child(UId);
        mDataBase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name = dataSnapshot.child("name").getValue().toString().trim();
                String status = dataSnapshot.child("status").getValue().toString().trim();
                image = dataSnapshot.child("image").getValue().toString().trim();

                String opo = "https://omhassangroup.000webhostapp.com/updateimage.php";

                UpdateImage(opo);

                GetName.setText(name);
                GetStatus.setText(status);


                if( !image.equals("default")){
                    Picasso.with(MyAccount.this).load(image).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.ava).into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(MyAccount.this).load(image).placeholder(R.drawable.ava).into(imageView);


                        }
                    });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        BtnEName = (Button) findViewById(R.id.bt_edit_name);
        BtnEName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Name = GetName.getText().toString().trim();
                Intent intentName = new Intent(MyAccount.this, EditName.class);
                intentName.putExtra("Name", Name);
                startActivity(intentName);
            }
        });

        BtnEStatus = (Button) findViewById(R.id.bt_edit_status);
        BtnEStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Status = GetStatus.getText().toString().trim();
                Intent intentName = new Intent(MyAccount.this, EditStatus.class);
                intentName.putExtra("Status", Status);
                startActivity(intentName);
            }
        });

        ChangeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryint = new Intent();
                galleryint.setType("image/*");
                galleryint.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryint, "SELECT IMAGE"), galory_pick);




            }


        });

    }

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == galory_pick || resultCode == RESULT_OK) {

                        Uri uri = data.getData();
            CropImage.activity(uri)
                    // .setAspectRatio(1, 1)
                    //.setMinCropWindowSize(200,200)
                    .start(this);


            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

                final CropImage.ActivityResult result = CropImage.getActivityResult(data);

                if (resultCode == RESULT_OK) {

                    mprogressdialogg = new ProgressDialog(MyAccount.this);
                    mprogressdialogg.setTitle(R.string.uploading_image);
                    mprogressdialogg.setMessage(getString(R.string.MassageUpLoadImage));
                    mprogressdialogg.setCanceledOnTouchOutside(false);
                    mprogressdialogg.show();

                    Uri resulturi = result.getUri();

                    // String current_user_id = mCurrentUser.getUid();


                    final File thump_filepatch = new File(resulturi.getPath());//111111111

                    String current_user_id = mCurrentUser.getUid();


                    Bitmap tump_bitmap = new Compressor(this) //00000000000
                            .setMaxWidth(200)
                            .setMaxHeight(200)
                            .setQuality(75)
                            .compressToBitmap(thump_filepatch);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    tump_bitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos);
                    final byte[] thump_byte = baos.toByteArray();



                    // StorageReference filepatch = mStorageRef.child("profile_images").child(current_user_id+".jpg");
                    final StorageReference thump_filepath =mStorageRef.child("profile_images").child(current_user_id+".jpg");


                    thump_filepath.putFile(resulturi).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {


                            if (task.isSuccessful()) {
                                final String dowanload_uri = task.getResult().getDownloadUrl().toString();
                                UploadTask uploadTask = thump_filepath.putBytes(thump_byte); //000000000000
                                uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thump_task) {

                                        String thump_downloadUrl = thump_task.getResult().getDownloadUrl().toString();



                                        if (thump_task.isSuccessful()){






                                            Map updateHashMap = new HashMap();
                                            //  updateHashMap.put("image",dowanload_uri);
                                            updateHashMap.put("image",thump_downloadUrl);

                                            mUserDatabase.updateChildren(updateHashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    mprogressdialogg.dismiss();
                                                    Toast.makeText(MyAccount.this, R.string.Success_Uploading, Toast.LENGTH_SHORT).show();
                                                }

                                            });
                                        }else {
                                            Toast.makeText(MyAccount.this,R.string.ErrorUpLoading, Toast.LENGTH_SHORT).show();
                                            mprogressdialogg.dismiss();
                                        }
                                    }
                                });


                            }else {
                                Toast.makeText(MyAccount.this, "Error in Uploading", Toast.LENGTH_SHORT).show();
                                mprogressdialogg.dismiss();
                            }
                        }
                    });
                }else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                }
            }


        }
    }

    public void UpdateImage(final String uopo){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, uopo, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> updet = new HashMap<>();
                updet.put("emailu",userEmail);
                updet.put("uname",name);
                updet.put("link",image);

                return updet ;
            }
        };
        Volley.newRequestQueue(this).add(stringRequest);
    }
}
