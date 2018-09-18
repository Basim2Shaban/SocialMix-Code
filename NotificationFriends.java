package com.basm.socialmix;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.basm.socialmix.Util.SoicalMixApi;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationFriends extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView mUserslist;
    private DatabaseReference muserdatabase;
    private FirebaseUser mCurrentUser;
    ArrayList<GetReq> arrayList = new ArrayList<>();
    RecyclerView recyclerView ;
    private DatabaseReference mFriendRequest ;
    private DatabaseReference mFriendDatabase ;
    private DatabaseReference mUserDatabase ;
    String statu  , myname , myimage , guname , guimage;
    private boolean sts ;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_friends);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int heighet = dm.heightPixels;

        getWindow().setLayout((width), (heighet));


        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        mFriendRequest = FirebaseDatabase.getInstance().getReference().child("Friend_req");
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String Uid = mCurrentUser.getUid();
        String uri = "https://omhassangroup.000webhostapp.com/get_user_requests.php?sender_to="+Uid;


        mUserDatabase.child(Uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                myname = dataSnapshot.child("name").getValue().toString();
                myimage = dataSnapshot.child("image").getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        toolbar = (Toolbar) findViewById(R.id.Requests);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Requests");


        recyclerView = (RecyclerView)findViewById(R.id.recycler_Requests);
        recyclerView.setHasFixedSize(true);
        final GridLayoutManager manager = new GridLayoutManager(this,1);
        recyclerView.setLayoutManager(manager);
        recyclerView.setItemAnimator(null);


     //   GetRequsted(uri,this);

        AdapterReq adapterReq = new AdapterReq(SoicalMixApi.getInstance().arrayList,this);
        recyclerView.setAdapter(adapterReq);
        adapterReq.notifyDataSetChanged();
    }








    //____________________________________________//adapter//________________________________________//
    public class AdapterReq extends RecyclerView.Adapter<AdapterReq.Requs>{
    ArrayList<GetReq> arrayList = new ArrayList<>();
        Context context ;

        public AdapterReq(ArrayList<GetReq> arrayList, Context context) {
            this.arrayList = arrayList;
            this.context = context;
        }

        @Override
        public Requs onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.requests_row_item,parent,false);
            Requs requs = new Requs(view);
            return requs ;
        }

        @Override
        public void onBindViewHolder(final Requs holder, final int position) {
            final String sender = arrayList.get(position).getSender();
            final String senderto = arrayList.get(position).getSender_to();
            holder.name.setText(arrayList.get(position).getName());
            Picasso.with(context).load(arrayList.get(position).getImage()).into(holder.image);





            mUserDatabase.child(sender).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    guname = dataSnapshot.child("name").getValue().toString();
                    guimage = dataSnapshot.child("image").getValue().toString();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            holder.accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                String conn = "https://omhassangroup.000webhostapp.com/connect_delete_request.php?senderto="+senderto+"&sender="+sender;
                       boolean m = Connect_Delete_Request(conn);
                    if (m == true){
                        holder.status_frind_req.setVisibility(View.INVISIBLE);
                        accept(sender);
                        SoicalMixApi.getInstance().arrayList.remove(position);
                        holder.accept.setVisibility(View.INVISIBLE);
                        holder.delete.setVisibility(View.INVISIBLE);
                        holder.status_frind_req.setVisibility(View.VISIBLE);
                        holder.status_frind_req.setText("you are friends now");
                    }

                }
            });

            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String conn = "https://omhassangroup.000webhostapp.com/connect_delete_request.php?senderto="+senderto+"&sender="+sender;
                    boolean m = Connect_Delete_Request(conn);
                    if (m == true){
                        holder.status_frind_req.setVisibility(View.INVISIBLE);
                            mFriendRequest.child(mCurrentUser.getUid()).child(sender).removeValue()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            mFriendRequest.child(sender).child(mCurrentUser.getUid()).removeValue()
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            SoicalMixApi.getInstance().arrayList.remove(position);
                                                            holder.accept.setVisibility(View.INVISIBLE);
                                                            holder.delete.setVisibility(View.INVISIBLE);
                                                            holder.status_frind_req.setVisibility(View.VISIBLE);
                                                            holder.status_frind_req.setText("Request deleted");
                                                        }
                                                    });
                                        }
                                    });
                    }
                }
            });
        }


        @Override
        public int getItemCount() {
            return arrayList.size();
        }

        public class Requs extends RecyclerView.ViewHolder{
            CircleImageView image ;
            TextView name , status_frind_req ;
            Button accept , delete ;

         public Requs(View itemView) {
             super(itemView);
             image = (CircleImageView) itemView.findViewById(R.id.user_req_image);
             name = (TextView)itemView.findViewById(R.id.user_req_name);
             status_frind_req = (TextView)itemView.findViewById(R.id.show_status_friend);
             accept = (Button)itemView.findViewById(R.id.btn_accept_req);
             delete = (Button)itemView.findViewById(R.id.btn_delete_req);

         }
     }
    }




    public void accept (final String send) {
        mFriendRequest.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(send)) {
                    String req_type = dataSnapshot.child(send).child("type").getValue().toString();
                    if (req_type.equals("received")){
                        HashMap<String,String> hash = new HashMap<String, String>();
                        hash.put("name",guname);
                        hash.put("image",guimage);
                        mFriendDatabase.child(mCurrentUser.getUid()).child("friends_user").child(send).setValue(hash).
                                addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        HashMap<String,String> map = new HashMap<String, String>();
                                        map.put("name",myname);
                                        map.put("image",myimage);
                                        mFriendDatabase.child(send).child("friends_user").child(mCurrentUser.getUid()).
                                                setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                mFriendRequest.child(mCurrentUser.getUid()).child(send).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        mFriendRequest.child(send).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {



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
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public boolean Connect_Delete_Request(String connect){

        StringRequest delete = new StringRequest(Request.Method.GET, connect, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        Volley.newRequestQueue(NotificationFriends.this).add(delete);
        return true ;
    }


  public boolean StatusNotification(MenuItem item){
     if (!arrayList.isEmpty()){
          return true ;
     }else {
         return false;
     }



  }




}