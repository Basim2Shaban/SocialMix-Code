package com.basm.socialmix;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Contacts;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    private TextView mDisplayName,mDisplayStatus,status_friend ;
    private ImageView mImageView ;
       private ImageView  mProfileImageLove , mProfileImageBroken , mImageCountLove ,mImageCountBroken ;
    private TextView textViewCountLove , textViewCountBroken ;
    private ImageView imageView_send_massage ,mDiclinebtn;


    private DatabaseReference mUserDatabase ;

    private ProgressDialog mprogressDialog ;

    private FirebaseUser mCurrentUser ;

    private DatabaseReference mFriendRequest ;
    private DatabaseReference mFriendDatabase , Var_Lover , Var_Broken ;
    private DatabaseReference mget ;


    private String mcurrent_state , UId , Status_Profile_B_L , Status_Me_for_this_user;
    private String name , status , image ,Uri ,cancel ,user_id;
    String Display_name ,Display_status ,Display_image  ;
    private int GetCountLove ; private int GetCountBroken ;
    private String N_S_B , N_S_Love;
    private int getCL , getCB ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Uri = "https://omhassangroup.000webhostapp.com/request_set.php";




        user_id = getIntent().getStringExtra("User_id");

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(user_id);
        mFriendRequest = FirebaseDatabase.getInstance().getReference().child("Friend_req");
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        mget = FirebaseDatabase.getInstance().getReference().child("users");
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        UId = mCurrentUser.getUid();
        Var_Lover = FirebaseDatabase.getInstance().getReference().child("lover").child(user_id);
        Var_Broken = FirebaseDatabase.getInstance().getReference().child("broken").child(user_id);




        cancel = "https://omhassangroup.000webhostapp.com/cancel_req.php?sender="+UId+"&senderto="+user_id;


        mget.child(mCurrentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                image = dataSnapshot.child("image").getValue().toString();
                name = dataSnapshot.child("name").getValue().toString();
                status = dataSnapshot.child("status").getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        mprogressDialog = new ProgressDialog(this);
        mprogressDialog.setTitle("Loading User Data");
        mprogressDialog.setMessage("please wait while we load the user data");
        mprogressDialog.setCanceledOnTouchOutside(false);
        mprogressDialog.show();

        if (!ProfileActivity.this.isFinishing() && mprogressDialog != null) {
            mprogressDialog.dismiss();
        }


        mImageView = (ImageView)findViewById(R.id.profile_display_image);
        mDisplayName = (TextView)findViewById(R.id.profile_display_name);
        mDisplayStatus = (TextView)findViewById(R.id.profile_display_status);
        imageView_send_massage = (ImageView)findViewById(R.id.btn_profile_send_massage);
        status_friend = (TextView)findViewById(R.id.status_req);
        mProfileImageLove = (ImageView)findViewById(R.id.profile_lover_picture);
        mProfileImageBroken = (ImageView)findViewById(R.id.profile_broken_picture);
        mImageCountLove = (ImageView)findViewById(R.id.picture_lover_count);
        mImageCountBroken = (ImageView)findViewById(R.id.picture_broken_count);
        textViewCountLove = (TextView)findViewById(R.id.text_count_lover);
        textViewCountBroken = (TextView)findViewById(R.id.text_count_broken);


        if (user_id.equalsIgnoreCase(mCurrentUser.getUid()) ){
            imageView_send_massage.setVisibility(View.INVISIBLE);
            status_friend.setVisibility(View.INVISIBLE);
            mProfileImageLove.setImageDrawable(getResources().getDrawable(R.drawable.profile_like_two));
            mProfileImageLove.setEnabled(false);
            mProfileImageBroken.setImageDrawable(getResources().getDrawable(R.drawable.broken_heart));
            mProfileImageBroken.setEnabled(false);

        }

    //   B_L_Status();
        Else_Status_Fell();


        mProfileImageLove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                if (N_S_B.equalsIgnoreCase("here")){
                    if (GetCountBroken >= 0 ){
                        getCB = GetCountBroken - 1 ;

                        final Map hashMap = new HashMap<>();
                        hashMap.put("countbroken",getCB);
                        Var_Broken.child(UId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Var_Broken.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                      //  Else_Status_Fell();
                                        Else_Status_Fell();
                                    }
                                });
                            }
                        });

                    }else {
                        getCB = 0 ;
                    }
                    A_DD_Love(GetCountLove);
                }else {
                    A_DD_Love(GetCountLove);
                }


            }
        });

        mProfileImageBroken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



            if (N_S_Love.equalsIgnoreCase("here")){
                if (GetCountLove >= 0 ) {
                    getCL = GetCountLove - 1;
                    final Map hashMap = new HashMap<>();
                    hashMap.put("countlove",getCL);
                    Var_Lover.child(UId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Var_Lover.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                //    Else_Status_Fell();
                                    Else_Status_Fell();
                                }
                            });
                        }
                    });

                }else {
                    getCL = 0 ;
                }
                A_DD_Broken(GetCountBroken);
            }else {
                A_DD_Broken(GetCountBroken);
            }

            }
        });





        mcurrent_state = "not_friend";


        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Display_name = dataSnapshot.child("name").getValue().toString();
                Display_status = dataSnapshot.child("status").getValue().toString();
                Display_image = dataSnapshot.child("image").getValue().toString();

                mDisplayName.setText(Display_name);
                mDisplayStatus.setText(Display_status);

                Picasso.with(ProfileActivity.this).load(Display_image).placeholder(R.drawable.prof).into(mImageView);




                mFriendRequest.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.hasChild(user_id)){
                            String req_type = dataSnapshot.child(user_id).child("type").getValue().toString();

                            if (req_type.equals("received")){

                                mcurrent_state = "req_received";
                                status_friend.setText(getString(R.string.AcceptRequest));
                            }else if (req_type.equals("sent")){
                                mcurrent_state="req_sent";
                                status_friend.setText(getString(R.string.CancelFriendRequest));

                            }


                            mprogressDialog.dismiss();


                        }else {
                            mFriendDatabase.child(mCurrentUser.getUid()).child("friends_user")
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.hasChild(user_id)){

                                                mcurrent_state = "friends";
                                                status_friend.setText(getString(R.string.YourFrinds));

                                               
                                            }

                                            mprogressDialog.dismiss();

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                        }




                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });







        imageView_send_massage.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        if (mcurrent_state.equals("not_friend")){
            Glide.with(ProfileActivity.this).load(R.drawable.resorsload).into(imageView_send_massage);
            SendRequest(Uri);

        }


        if (mcurrent_state.equals("req_sent")){
            Glide.with(ProfileActivity.this).load(R.drawable.resorsload).into(imageView_send_massage);
            CancelFrind(cancel);


        }


        if (mcurrent_state.equals("req_received")){
            HashMap<String,String> hash = new HashMap<String, String>();
            hash.put("name",Display_name);
            hash.put("image",Display_image);
            mFriendDatabase.child(mCurrentUser.getUid()).child("friends_user").child(user_id).setValue(hash).
                    addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    HashMap<String,String> map = new HashMap<String, String>();
                    map.put("name",name);
                    map.put("image",image);
                 mFriendDatabase.child(user_id).child("friends_user").child(mCurrentUser.getUid()).
                         setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                     @Override
                     public void onSuccess(Void aVoid) {
                         mFriendRequest.child(mCurrentUser.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                             @Override
                             public void onSuccess(Void aVoid) {
                                 mFriendRequest.child(user_id).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                     @Override
                                     public void onSuccess(Void aVoid) {
                                         imageView_send_massage.setEnabled(true);
                                         Glide.with(ProfileActivity.this).load(R.drawable.frinds).into(imageView_send_massage);
                                         mcurrent_state = "friends";
                                         status_friend.setText(getString(R.string.Friens));
                                     }
                                 }) ;

                                 }
                         });

                         }
                 });
                }
            });
        }

    }
});
}


    public void SendRequest(String uri){
    StringRequest request = new StringRequest(Request.Method.POST, uri, new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            if (response.equals("done")){
                mFriendRequest.child(mCurrentUser.getUid()).child(user_id).child("type").
                        setValue("sent").addOnCompleteListener(new
                                     OnCompleteListener<Void>() {
                                  @Override
                                      public void onComplete(@NonNull Task<Void> task) {
                                      Glide.with(ProfileActivity.this).load(R.drawable.cancel_friend).into(imageView_send_massage);
                                   mFriendRequest.child(user_id).child(mCurrentUser.getUid()).child("type")
                                          .setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                      public void onSuccess(Void aVoid) {

                                          mcurrent_state="req_sent";
                                            status_friend.setText(getString(R.string.CancelFriendRequest));


                                                    }
                                                       });
                                                      }
                                                });

            }else{
                Toast.makeText(ProfileActivity.this, "some error", Toast.LENGTH_SHORT).show();
            }

        }
    }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {

        }
    }){
        @Override
        protected Map<String, String> getParams() throws AuthFailureError {
             super.getParams();
            HashMap<String,String> hashMap = new HashMap<>();
            hashMap.put("sender",UId);
            hashMap.put("senderto",user_id);
            hashMap.put("name",name);
            hashMap.put("image",image);
            return hashMap ;
        }
    };
    Volley.newRequestQueue(this).add(request);

}


    public void CancelFrind(String can){
    StringRequest stringrequest  = new StringRequest(Request.Method.GET, can, new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {

            if (response.equals("cancel")){
                mFriendRequest.child(mCurrentUser.getUid()).child(user_id).removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {


                                mFriendRequest.child(user_id).child(mCurrentUser.getUid()).removeValue()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Glide.with(ProfileActivity.this).load(R.drawable.add_friend).into(imageView_send_massage);
                                                imageView_send_massage.setEnabled(true);
                                                mcurrent_state = "not_friend";
                                                status_friend.setText(getString(R.string.SendFriendRequest));



                                            }
                                        });
                            }
                        });

            }else{
                Toast.makeText(ProfileActivity.this, "error in connect", Toast.LENGTH_SHORT).show();
            }

        }
    }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Toast.makeText(ProfileActivity.this, "error in connect", Toast.LENGTH_SHORT).show();

        }
    });
Volley.newRequestQueue(this).add(stringrequest);
}


    public void Else_Status_Fell(){
    Var_Broken.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            if (dataSnapshot.hasChild("countbroken")){
                GetCountBroken = Integer.parseInt(dataSnapshot.child("countbroken").getValue().toString());
                if (GetCountBroken <= 0){
                    GetCountBroken = 0 ;
                }
                textViewCountBroken.setVisibility(View.VISIBLE);
                mImageCountBroken.setVisibility(View.VISIBLE);
                textViewCountBroken.setText("+"+GetCountBroken);
                if (GetCountBroken == 0){
                    textViewCountBroken.setVisibility(View.INVISIBLE);
                    mImageCountBroken.setVisibility(View.INVISIBLE);
                }
            }else {
                GetCountBroken  = 0;
            }


            if (dataSnapshot.hasChild(UId)){
                N_S_B = "here" ;
                mProfileImageBroken.setImageDrawable(getResources().getDrawable(R.drawable.broken_heart));
                mProfileImageBroken.setEnabled(false);
            }else {
                N_S_B = "not" ;
                mProfileImageBroken.setImageDrawable(getResources().getDrawable(R.drawable.broken_one));
                mProfileImageBroken.setEnabled(true);

            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    });

        Var_Lover.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot.hasChild("countlove")){
                GetCountLove = Integer.parseInt(dataSnapshot.child("countlove").getValue().toString());
                if (GetCountLove <= 0){
                    GetCountLove = 0 ;
                }
                textViewCountLove.setVisibility(View.VISIBLE);
                mImageCountLove.setVisibility(View.VISIBLE);
                textViewCountLove.setText(GetCountLove+"+");

                if (GetCountLove == 0){
                    textViewCountLove.setVisibility(View.INVISIBLE);
                    mImageCountLove.setVisibility(View.INVISIBLE);
                }
            }else {
                GetCountLove = 0 ;


            }
            if (dataSnapshot.hasChild(UId)){
                N_S_Love = "here" ;
                mProfileImageLove.setImageDrawable(getResources().getDrawable(R.drawable.profile_like_two));
                mProfileImageLove.setEnabled(false);
            }else {
                N_S_Love = "not" ;
                mProfileImageLove.setImageDrawable(getResources().getDrawable(R.drawable.profile_un_like));
                mProfileImageLove.setEnabled(true);
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    });
}


    public void A_DD_Broken(final int Count){

    final Map hashMap = new HashMap<>();
    hashMap.put("countbroken",Count+1);

    Var_Broken.child(UId).child("me").setValue(UId).addOnCompleteListener(new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull Task<Void> task) {

            Var_Broken.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    mProfileImageBroken.setImageDrawable(getResources().getDrawable(R.drawable.broken_heart));
                }
            });
        }
    });
}


    public void A_DD_Love(int Count){

        final Map hashMap = new HashMap<>();
        hashMap.put("countlove",Count+1);
        Var_Lover.child(UId).child("me").setValue(UId).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Var_Lover.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        mProfileImageLove.setImageDrawable(getResources().getDrawable(R.drawable.profile_like_two));
                    }
                });
            }
        });
    }


}
