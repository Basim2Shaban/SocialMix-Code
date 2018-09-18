package com.basm.socialmix;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.basm.socialmix.firebase.MyMethod;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.threeten.bp.Duration;
import org.threeten.bp.Instant;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class Activity_Single_Chat extends AppCompatActivity {
    private static final int NUM_OF_ITEMS_ON_FIRST_LOAD = 20;
    private static final int NUM_OF_ITEMS_ON_LOAD_MORE = 5;
    private TextView name , Status_Connect_user;
    private CircleImageView imageView_user;
    private DatabaseReference getinfo, getmyinfo;
    private DatabaseReference dChat, getChat;
    private StorageReference mStorageRef;
    private FirebaseUser mCurrentUser;
    private static String getimage, getname;
    public static String myname;
    private String user_id, status;
    private String thump_downloadUrl = "default";
    private Toolbar toolbar;
    private ImageView image_send, send_massage, display_image_send;
    private EditText editText_mass;
    private int number;
    private RecyclerView recyclerView_chat;
    boolean sts;
    byte[] edata, uridata;
    private int count;
    private LinearLayoutManager layoutManager;
    private FirebaseRecyclerAdapter<Massage_item, GetMassage> recyclerAdapter;
    private DatabaseReference mCurrentUserChatsRef , Connect;
    private boolean mCanLoadMore = true;




    long timeInMilliseconds ;


    FirebaseRecyclerOptions<Massage_item> options;
    Query query;
    List<Massage_item> list_chat = new LinkedList<>();
    AdapterChatClass chatClass;
    String firstItemKey;
    DatabaseReference userChatRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__single__chat);

        user_id = getIntent().getStringExtra("id_user");


        recyclerView_chat = (RecyclerView) findViewById(R.id.view_rec_chaat);

        recyclerView_chat.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView_chat.setLayoutManager(layoutManager);
        recyclerView_chat.setItemAnimator(new DefaultItemAnimator());


        send_massage = (ImageView) findViewById(R.id.send_single_chat);
        image_send = (ImageView) findViewById(R.id.load_image_single_chat);
        display_image_send = (ImageView) findViewById(R.id.display_image_send);
        editText_mass = (EditText) findViewById(R.id.edit_single_chat);
        name = (TextView) findViewById(R.id.name_single_chat);
        imageView_user = (CircleImageView) findViewById(R.id.image_single_chat);
        Status_Connect_user = (TextView)findViewById(R.id.Status_connect);
        toolbar = (Toolbar) findViewById(R.id.app_bar_chat);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");


        Connect = FirebaseDatabase.getInstance().getReference().child("connect");
        mStorageRef = FirebaseStorage.getInstance().getReference();
        getinfo = FirebaseDatabase.getInstance().getReference().child("users").child(user_id);
        dChat = FirebaseDatabase.getInstance().getReference().child("chat");
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        getChat = FirebaseDatabase.getInstance().getReference().child("chat");
        getmyinfo = FirebaseDatabase.getInstance().getReference().child("users").child(mCurrentUser.getUid());

        mCurrentUserChatsRef = getChat.child(mCurrentUser.getUid());

        GetMyInfo();
        StatusConnectUser();


        dChat.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.child(user_id).hasChild(mCurrentUser.getUid())) {
                    userChatRef = getChat.child(user_id).child(mCurrentUser.getUid());

                    sts = true;


                } else {
                    userChatRef = getChat.child(mCurrentUser.getUid()).child(user_id);
                    sts = false;

                }
               // queryLast.Query_about_FinalMassage(userChatRef,holder);
                fetchChatMesages(userChatRef.limitToLast(NUM_OF_ITEMS_ON_FIRST_LOAD));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        recyclerView_chat.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (layoutManager.findFirstCompletelyVisibleItemPosition() == 0 && mCanLoadMore
                        && list_chat.size() >= NUM_OF_ITEMS_ON_FIRST_LOAD) {
                    Query query = userChatRef.orderByKey().endAt(firstItemKey).limitToLast(NUM_OF_ITEMS_ON_LOAD_MORE + 1);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getChildrenCount() <= NUM_OF_ITEMS_ON_LOAD_MORE)
                                mCanLoadMore = false;
                            List<Massage_item> newScrolledItems = new ArrayList<>();
                            Iterator <DataSnapshot> itr = dataSnapshot.getChildren().iterator();

                           for (int i = 1; i < dataSnapshot.getChildrenCount() ; i++) {
                               DataSnapshot item = itr.next();
                               if (newScrolledItems.size() == 0) {
                                   firstItemKey = item.getKey();
                               }
                               newScrolledItems.add(item.getValue(Massage_item.class));
                           }

                            list_chat.addAll(0, newScrolledItems);
                            chatClass.notifyItemRangeInserted(0, newScrolledItems.size());


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        });


        GetInformitionUser();


        CheckStatus();


        if (editText_mass.getText().equals(null)) {
            send_massage.setEnabled(false);
        } else {
            send_massage.setEnabled(true);
        }

        send_massage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (status.equals("yes")) {
                    String getmassage = editText_mass.getText().toString().trim();
                    if (getmassage.isEmpty() && thump_downloadUrl == "default") {
                    } else {
                        SendOpject opj = new SendOpject(myname, getmassage, thump_downloadUrl);
                        dChat.child(user_id).child(mCurrentUser.getUid()).push().setValue(opj).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                editText_mass.setText("");
                                thump_downloadUrl = "default";
                                recyclerView_chat.smoothScrollToPosition(recyclerView_chat.getAdapter().getItemCount());
                                display_image_send.setImageDrawable(null);
                                // AdapterRec();
                                if (thump_downloadUrl != "default") {
                                    display_image_send.requestLayout();
                                    display_image_send.getLayoutParams().height = 0;
                                    display_image_send.getLayoutParams().width = 0;
                                }
                            }

                        });
                    }
                } else {
                    String getmassage = editText_mass.getText().toString().trim();
                    if (getmassage.isEmpty() && thump_downloadUrl == "default") {

                    } else {
                        SendOpject opj = new SendOpject(myname, getmassage, thump_downloadUrl);
                        String push = dChat.child(mCurrentUser.getUid()).child(user_id).push().getKey();
                        dChat.child(mCurrentUser.getUid()).child(user_id).child(push).setValue(opj).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                editText_mass.setText("");
                                thump_downloadUrl = "default";
                                recyclerView_chat.smoothScrollToPosition(recyclerView_chat.getAdapter().getItemCount());
                                display_image_send.setImageDrawable(null);
                                //       AdapterRec();
                                if (thump_downloadUrl != "default") {
                                    display_image_send.requestLayout();
                                    display_image_send.getLayoutParams().height = 0;
                                    display_image_send.getLayoutParams().width = 0;
                                }

                            }
                        });


                    }
                }


            }
        });

        image_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallory = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(gallory, 100);
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_user, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.btn_show_acc) {
            Intent intent = new Intent(Activity_Single_Chat.this, ProfileActivity.class);
            intent.putExtra("User_id", user_id);
            startActivity(intent);
        }


        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        GetInformitionUser();


    }


    public static class GetMassage extends RecyclerView.ViewHolder {
        private TextView massage, you;
        private ImageView image_massage, view_you;

        public GetMassage(View itemView) {
            super(itemView);
            massage = (TextView) itemView.findViewById(R.id.text_massage);
            //    you = (TextView) itemView.findViewById(R.id.text_user);
            //  view_me = (ImageView) itemView.findViewById(R.id.image_me);
            image_massage = (ImageView) itemView.findViewById(R.id.image_massage);
        }
    }


    public void CheckStatus() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                dChat.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(user_id).hasChild(mCurrentUser.getUid())) {
                            status = "yes";
                            getChat.keepSynced(true);


                        } else {
                            status = "no";
                            //  dChat.child(mCurrentUser.getUid()).child(user_id);
                            getChat.keepSynced(true);


                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };
        thread.start();
    }


    public void GetInformitionUser() {

        getinfo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                getimage = dataSnapshot.child("image").getValue().toString();
                getname = dataSnapshot.child("name").getValue().toString();

                Picasso.with(Activity_Single_Chat.this).load(getimage).into(imageView_user);
                name.setText(getname);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void GetMyInfo() {
        getmyinfo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                myname = dataSnapshot.child("name").getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 || resultCode == RESULT_OK) {

            Uri uri = data.getData();
            Glide.with(this).load(R.drawable.loding).into(display_image_send);


            java.util.Calendar calendar = java.util.Calendar.getInstance();
            calendar.setTime(new Date());
            int cl = (int) calendar.getTimeInMillis();

            Random r = new Random();
            int rf = r.nextInt(80 - 65) + 1000;

            number = cl + rf;


            try {
                ContentResolver cr = this.getContentResolver();
                InputStream inputStream = cr.openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                Bitmap res = Bitmap.createScaledBitmap(bitmap, 500, 650, false);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                res.compress(Bitmap.CompressFormat.JPEG, 60, baos);
                edata = baos.toByteArray();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }


            final StorageReference thump_filepath = mStorageRef.child("chat").child(mCurrentUser.getUid()).child(number + ".jpg");
            thump_filepath.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    UploadTask uploadTask = thump_filepath.putBytes(edata);
                    uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                thump_downloadUrl = task.getResult().getDownloadUrl().toString();
                                Glide.with(getApplicationContext()).load(thump_downloadUrl).into(display_image_send);
                                if (display_image_send.getDrawable() != null) {
                                    display_image_send.requestLayout();
                                    display_image_send.getLayoutParams().height = 200;
                                    display_image_send.getLayoutParams().width = 250;
                                }
                            }
                        }
                    });
                }
            });
        }
    }


    private void fetchChatMesages(Query query) {


        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (firstItemKey == null) {
                    firstItemKey = dataSnapshot.getKey();
                }
                onNewMessage(dataSnapshot.getValue(Massage_item.class));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void onNewMessage(Massage_item item) {
        list_chat.add(item);

        if (recyclerView_chat.getAdapter() == null) {
            chatClass = new AdapterChatClass(list_chat, Activity_Single_Chat.this, myname);
            recyclerView_chat.setAdapter(chatClass);
            // recyclerView_chat.scrollToPosition(list_chat.size());
            //  recyclerView_chat.smoothScrollToPosition(recyclerView_chat.getAdapter().getItemCount());

        } else {
            chatClass.notifyItemInserted(chatClass.getItemCount() - 1);
            //   recyclerView_chat.smoothScrollToPosition(recyclerView_chat.getAdapter().getItemCount());

        }


    }

    public void StatusConnectUser(){
        Connect.child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long lastSeenTime = dataSnapshot.child("status").getValue(Long.class);
                if (lastSeenTime == -1){
                    Status_Connect_user.setText("متصل ");
                }else{

                    Instant current = Instant.now();
                    Instant last = Instant.ofEpochMilli(lastSeenTime);
                    Duration duration = Duration.between(last, current);

                    if (duration.toMinutes() <= 60){

                        Status_Connect_user.setText( getString(R.string.Been_here_before)+duration.toMinutes() + " دقيقه");
                    }else {
                        if (duration.toHours() != 0) {
                            Status_Connect_user.setText(  getString(R.string.Been_here_before) + duration.toHours() + " ساعات");
                            if (duration.toHours() >= 24){
                                Date date = new Date(lastSeenTime);
                                SimpleDateFormat formatter= new SimpleDateFormat("dd/MM/yyyy");
                                String formatted = formatter.format(date );
                                Status_Connect_user.setText(getString(R.string.Last_seen) + formatted);
                            }
                        }
                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}



















/*
    public void NextAway(){
        getChat.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Add_chat(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Add_chat(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private String chat_msg , chat_msg_user ;

    private void Add_chat(DataSnapshot dataSnapshot){

        Iterator i = dataSnapshot.getChildren().iterator();
        editText_mass.setText("");
        while (i.hasNext()){

                        String image  = (String)((DataSnapshot) i.next()).getValue();
                        String massage  = (String)((DataSnapshot) i.next()).getValue();
                        String name  = (String)((DataSnapshot) i.next()).getValue();


            Items_chats items = new Items_chats(image,massage,name);
            list_chat.add(items);
            arrayAdapter.notifyDataSetChanged();
            recyclerView_chat.setSelection(list_chat.size());
        }


    }

    public class ListAdapter extends BaseAdapter{
        ArrayList<Items_chats> arrayList = new ArrayList<>();

        public ListAdapter(ArrayList<Items_chats> arrayList) {
            this.arrayList = arrayList;
        }

        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public Object getItem(int i) {
            return i;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View view1 = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.massage_item,viewGroup,false);

            TextView mymass = (TextView)view1.findViewById(R.id.text_user);
            ImageView myimaggem = (ImageView)view1.findViewById(R.id.image_user);
            TextView yourtext = (TextView)view1.findViewById(R.id.text_me);
            ImageView yourimage = (ImageView)view1.findViewById(R.id.image_me);

            String nam = arrayList.get(i).getName();

            if (nam.equalsIgnoreCase(myname)){
                mymass.setText(arrayList.get(i).getMassage());
            }else {
                yourtext.setText(arrayList.get(i).getMassage());
            }


            return view1;
        }
    }
*/


/*
*

* */
/*
*


 public void AdapterRec(){
        final FirebaseRecyclerAdapter<Massage_item, GetMassage> recyclerAdapter = new FirebaseRecyclerAdapter<Massage_item, GetMassage>(
                Massage_item.class,
                R.layout.massage_item,
                GetMassage.class,
                getChat
        ) {


            @Override
            protected void populateViewHolder(GetMassage viewHolder, Massage_item model, int position) {

                count = getItemCount();
                String nn = model.getName();
                if (nn.equals(myname)) {
                    Picasso.with(getApplicationContext()).load(model.getImage()).into(viewHolder.view_me);
                    viewHolder.me.setText(model.getMassage());
                } else {
                    Picasso.with(getApplicationContext()).load(model.getImage()).into(viewHolder.view_you);
                    viewHolder.you.setText(model.getMassage());
                }
            }
        };

    recyclerView_chat.addOnScrollListener(new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (layoutManager.findLastCompletelyVisibleItemPosition() == recyclerAdapter.getItemCount()-2){
                recyclerAdapter.cleanup();
            }
        }
    });
        recyclerAdapter.notifyDataSetChanged();
        recyclerView_chat.setAdapter(recyclerAdapter);
        recyclerView_chat.scrollToPosition(recyclerAdapter.getItemCount());
        recyclerView_chat.swapAdapter(recyclerAdapter,false);

    }


* */
/**/
/**/