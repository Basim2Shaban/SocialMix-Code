package com.basm.socialmix;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class Chat extends Fragment {
    private DatabaseReference mFriendDatabase , dChat , userChatRef;
    private FirebaseUser mCureentUser ;
   // private String user_id ;

    private AdView mAdView;
    private InterstitialAd mInterstitialAd;
    private RecyclerView mUserslist;
    public Chat() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.chat_fragment, container, false);

        MobileAds.initialize(getContext(), "ca-app-pub-2166371604316477~1923513813");
        mInterstitialAd = new InterstitialAd(getContext());
        mInterstitialAd.setAdUnitId("ca-app-pub-2166371604316477/2085985712");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }

        });


        mAdView = (AdView)view.findViewById(R.id.ads_chat_banner);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        mCureentUser = FirebaseAuth.getInstance().getCurrentUser();
        String myId = mCureentUser.getUid();

        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends").child(myId).child("friends_user");
        dChat = FirebaseDatabase.getInstance().getReference().child("chat");





        mUserslist = (RecyclerView)view.findViewById(R.id.rec_friends_chat);
        mUserslist.setHasFixedSize(true);
        mUserslist.setLayoutManager(new LinearLayoutManager(getContext()));





        return view ;
    }

    @Override
    public void onStart() {
        super.onStart();

        Query query = mFriendDatabase.limitToLast(200);
        FirebaseRecyclerOptions<friends_user> options =
                new FirebaseRecyclerOptions.Builder()
                        .setQuery(query,friends_user.class).build();


        /*
           friends_user.class,
                R.layout.layout_friends_chat,
                MyHolder.class,
                mFriendDatabase
        * */
        FirebaseRecyclerAdapter<friends_user,MyHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<friends_user, MyHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final MyHolder holder, int position, @NonNull final friends_user model) {

                holder.setUserImage(model.getImage(),getActivity().getApplicationContext());
                holder.setDisplayName(model.getName());
                final String user_id = getRef(position).getKey();

                Get_Status_id(holder.textView_final_chat,user_id);

                holder.mview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mInterstitialAd.isLoaded()) {
                            mInterstitialAd.show();
                        }

                        Intent singlechat = new Intent(getActivity(),Activity_Single_Chat.class);
                        singlechat.putExtra("id_user",user_id);
                        singlechat.putExtra("image",model.getImage());
                        singlechat.putExtra("name",model.getName());
                        startActivity(singlechat);

                    }
                });

            }

            @Override
            public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_friends_chat, parent,false);
                return new MyHolder(view);
            }
        };
        mUserslist.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }



      public static  class MyHolder extends RecyclerView.ViewHolder {
            TextView textView_final_chat;
            View mview;

            public MyHolder(View itemView) {
                super(itemView);

                mview = itemView;
                textView_final_chat = (TextView) mview.findViewById(R.id.final_massage);


            }

            public void setUserImage(String thump_Image, Context ctx) {
                CircleImageView userimageView = (CircleImageView) mview.findViewById(R.id.image_user_chat);
                Picasso.with(ctx).load(thump_Image).placeholder(R.drawable.ava).into(userimageView);

            }

            public void setDisplayName(String name) {
                TextView usernameView = (TextView) mview.findViewById(R.id.name_user_chat);
                usernameView.setText(name);

            }


        }




    public void Get_Status_id(final TextView textView , final String user_id ){
            dChat.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.child(user_id).hasChild(mCureentUser.getUid())) {
                        userChatRef = dChat.child(user_id).child(mCureentUser.getUid());




                    } else {
                        userChatRef = dChat.child(mCureentUser.getUid()).child(user_id);

                    }
                    Query lastQuery = userChatRef.orderByKey().limitToLast(1);
                    lastQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String message;
                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                Massage_item m = child.getValue(Massage_item.class);
                                 message = m.getMassage();
                                    textView.setText(message);



                            }




                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            //Handle possible errors.
                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }

