package com.basm.socialmix;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;

import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class AllUsers extends Fragment {
    private RecyclerView mUserslist;
    private LinearLayoutManager layoutManager ;
    private DatabaseReference muserdatabase;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;



    public AllUsers() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_all_users, container, false);
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



        mAdView = (AdView)view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);




        muserdatabase = FirebaseDatabase.getInstance().getReference().child("users");


      layoutManager = new LinearLayoutManager(getContext());
        mUserslist = (RecyclerView) view.findViewById(R.id.list_users);
        mUserslist.setHasFixedSize(true);

        mUserslist.setLayoutManager(layoutManager);


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Query query = muserdatabase.limitToLast(50);
        FirebaseRecyclerOptions<Users> options =
                new FirebaseRecyclerOptions.Builder()
                        .setQuery(query,Users.class).build();

        FirebaseRecyclerAdapter<Users, UserViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, UserViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull Users model) {

                holder.setUserImage(model.getImage(), getActivity().getApplicationContext());
                holder.setDisplayName(model.getName());
                holder.setStatus(model.getStatus());
                // viewHolder.setUserImage(model.getThumb_image(),getActivity().getApplicationContext());

                final String user_id = getRef(position).getKey();

                holder.mview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mInterstitialAd.isLoaded()) {
                            mInterstitialAd.show();
                        }

                        Intent profileIntent = new Intent(getActivity(), ProfileActivity.class);
                        profileIntent.putExtra("User_id", user_id);
                        startActivity(profileIntent);
                    }
                });
            }

            @Override
            public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.user_single_layout, parent,false);
                return new UserViewHolder(view);
            }
        };





        mUserslist.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }


    public static class UserViewHolder extends RecyclerView.ViewHolder {

        View mview;

        public UserViewHolder(View itemView) {
            super(itemView);


            mview = itemView;
        }

        public void setDisplayName(String name) {
            TextView usernameView = (TextView) mview.findViewById(R.id.user_single_name);
            usernameView.setText(name);

        }

        public void setStatus(String status) {
            TextView statu = (TextView) mview.findViewById(R.id.user_single_status);
            statu.setText(status);
        }

        public void setUserImage(String thump_Image, Context ctx) {
            CircleImageView userimageView = (CircleImageView) mview.findViewById(R.id.user_single_image);
            Picasso.with(ctx).load(thump_Image).placeholder(R.drawable.ava).into(userimageView);

        }


    }
}

