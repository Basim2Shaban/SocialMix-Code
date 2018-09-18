package com.basm.socialmix;

import android.app.Notification;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.basm.socialmix.Util.SoicalMixApi;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.security.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    private Toolbar toolbar;

    private ViewPager viewPager;
    private ClassPigerAdepter classPigerAdepter;
    private TabLayout tabLayout;
    private boolean men;
    private FirebaseUser mCurrentUser;
    private DatabaseReference Connect, DisConnect;
    private String mydate;

    MenuItem mFrindsRequstsItem;
    Menu optionsMenu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        try {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        } catch (Exception e) {
        }

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();


        mAuth = FirebaseAuth.getInstance();
        toolbar = (Toolbar) findViewById(R.id.main_app_bar);
        setSupportActionBar(toolbar);
        //م  getSupportActionBar().setTitle("Social Mix");
        //   toolbar.setTitleTextColor(R.color.COLOR_TEXT);

        FirebaseUser mAuthCurrentUser = mAuth.getCurrentUser();

        if (mAuthCurrentUser != null) {
            SoicalMixApi.getInstance().mUserid = mCurrentUser.getUid();
            SoicalMixApi.getInstance().GetRequsted(this);

            //  mydate = java.text.DateFormat.getTimeInstance().format(Calendar.getInstance().getTime());
            //   DateFormat df = new SimpleDateFormat("hh:mm");
            // String strTime = df.format(new Date());
/*
            DateFormat dateFormat = new SimpleDateFormat("hh:mm a");
            Calendar cal = Calendar.getInstance();
            mydate = dateFormat.format(cal.getTime());
            */
            Calendar rightNow = Calendar.getInstance();

            Connect = FirebaseDatabase.getInstance().getReference().child("connect").child(mCurrentUser.getUid());
            Connect.child("status").onDisconnect().setValue(rightNow.getTimeInMillis());
            Connect.child("status").setValue(-1);
        }


        SoicalMixApi.getInstance().setOnFriendListRequstListener(new SoicalMixApi.OnFriendListRequstListener() {
            @Override
            public void OnRequstFinished() {

                if (SoicalMixApi.getInstance().arrayList.size() == 0) {
                    mFrindsRequstsItem.setIcon(getResources().getDrawable(R.drawable.ic_supervisor_account_black_24dp));
                } else {
                    mFrindsRequstsItem.setIcon(getResources().getDrawable(R.drawable.req_frien));

                }

            }
        });

        viewPager = (ViewPager) findViewById(R.id.main_tab_piger);
        classPigerAdepter = new ClassPigerAdepter(getSupportFragmentManager());
        viewPager.setAdapter(classPigerAdepter);
        tabLayout = (TabLayout) findViewById(R.id.main_tabs);
        tabLayout.setupWithViewPager(viewPager);


        viewPager.setOffscreenPageLimit(3);


    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser mAuthCurrentUser = mAuth.getCurrentUser();

        if (mAuthCurrentUser == null) {
            SendToStart();

        }


    }


    private void SendToStart() {
        Intent startIntent = new Intent(MainActivity.this, StartActivity.class);
        startActivity(startIntent);
        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_home, menu);

        mFrindsRequstsItem = menu.findItem(R.id.btn_menu_notification);


        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);


        if (item.getItemId() == R.id.btn_menu_logout) {
            Connect.child("status").setValue(mydate);
            FirebaseAuth.getInstance().signOut();
            SendToStart();
        }
        if (item.getItemId() == R.id.btn_menu_my_account) {
            Intent intentMyAccount = new Intent(MainActivity.this, MyAccount.class);
            startActivity(intentMyAccount);
        }
        if (item.getItemId() == R.id.btn_menu_notification) {
            Intent noti = new Intent(MainActivity.this, NotificationFriends.class);
            startActivity(noti);
        }


        return true;
    }
}
