package com.basm.socialmix;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
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
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;


/**
 * A simple {@link Fragment} subclass.
 */
public class Home extends Fragment {
    private static final String IS_LIST_LOADED = "is_loaded";
    private Button btn_post  , btn_like;
    private String bt_status = "des" ;
    private TextInputLayout writeTextPost;
    private ImageView postImage;
    private DatabaseReference mDataBase, mPosts;
    private FirebaseUser UserF;
    private String image, thump_downloadUrl, name, GetTextP;
    private ProgressDialog mprogressdialogg;
    private FirebaseUser mCurrentUser;
    private StorageReference mStorageRef;
    private FirebaseAuth mAuth;
    private View view;
    private String UriV;
    private String user;
    byte[] edata, uridata;
    int number ;
    String UserId;
    private RequestQueue requestQueue;
    private JsonObjectRequest jsonObjectRequest;
    private ArrayList<IItemWithAds> posts = new ArrayList<>();
    RecyclerView recyclerView;
    TextView xtex;
    private boolean mprogresslike = false ;
    private DatabaseReference PUserData ;
    private int countsl = 0;

    PostsRecycler postsRecycler;


    public Home() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRetainInstance(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(IS_LIST_LOADED,true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.home_fragment, container, false);


        MobileAds.initialize(getContext(), "ca-app-pub-2166371604316477~1923513813");


        UserF = FirebaseAuth.getInstance().getCurrentUser();
        final String UId = UserF.getUid();
        UserId = UserF.getUid();

        mprogressdialogg = new ProgressDialog(getContext());

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        user = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        thump_downloadUrl = "empty";


    //    FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        mPosts = FirebaseDatabase.getInstance().getReference().child("posts");
        PUserData = FirebaseDatabase.getInstance().getReference().child("likes");

        PUserData.keepSynced(true);

        btn_post = (Button) view.findViewById(R.id.bt_post);
        writeTextPost = (TextInputLayout) view.findViewById(R.id.edit_post);
        postImage = (ImageView) view.findViewById(R.id.image_post);



        recyclerView = (RecyclerView) view.findViewById(R.id.recclyerview);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setHasFixedSize(true);
        // final GridLayoutManager manager = new GridLayoutManager(getContext(),1);
       // manager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(null);




        postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(intent, 100);
            }
        });

        mDataBase = FirebaseDatabase.getInstance().getReference().child("users").child(UId);
        mDataBase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name = dataSnapshot.child("name").getValue().toString().trim();
                image = dataSnapshot.child("image").getValue().toString().trim();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        btn_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mprogressdialogg.setTitle(R.string.published);
                mprogressdialogg.setMessage(getString(R.string.PublishedYourpost));
                mprogressdialogg.setCanceledOnTouchOutside(false);
                mprogressdialogg.show();

                GetTextP = writeTextPost.getEditText().getText().toString().trim();

                SendPosts();

                DataGet(0);
            }
        });

        DataGet(0);


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (layoutManager.findLastCompletelyVisibleItemPosition()== posts.size()-1){
                    if (posts == null || posts.size() == 0) {
                        DataGet(0);
                        return;
                    }
                    int m = posts.size() / 15 + 1;
                    DataGet(m);
                }
            }
        });


        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 || resultCode == 100) {
            Uri uri = data.getData();
          //  postImage.setImageURI(uri);
            Picasso.with(getContext()).load(R.drawable.loding).into(postImage);

            String current_user_id = mCurrentUser.getUid();


            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            int cl = (int) calendar.getTimeInMillis();

            Random r = new Random();
            int rf = r.nextInt(80 - 65) + 1000;

            number = cl + rf;


            try {
                ContentResolver cr = getActivity().getContentResolver();
                InputStream inputStream = cr.openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                Bitmap res = Bitmap.createScaledBitmap(bitmap,850,900,false);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                res.compress(Bitmap.CompressFormat.JPEG, 60, baos);
                edata = baos.toByteArray();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            final StorageReference thump_filepath = mStorageRef.child("posts").child(current_user_id).child(number + ".jpg");
            thump_filepath.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    UploadTask uploadTask = thump_filepath.putBytes(edata);
                    uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                thump_downloadUrl = task.getResult().getDownloadUrl().toString();
                                Picasso.with(getContext()).load(thump_downloadUrl).into(postImage);
                            }
                        }
                    });
                }
            });


        }
    }


    public boolean SendPosts() {
        UriV = "https://omhassangroup.000webhostapp.com/insert_post.php";
        if (GetTextP.isEmpty() && thump_downloadUrl.equalsIgnoreCase("empty")) {
            Toast.makeText(getContext(),R.string.CanNotPost, Toast.LENGTH_SHORT).show();
            mprogressdialogg.hide();
        } else {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, UriV, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Toast.makeText(getContext(), response + user, Toast.LENGTH_SHORT).show();
                    posts.clear();
                    postsRecycler.notifyDataSetChanged();
                   int o = posts.size();
                    if (o <= 0){
                        DataGet(0);
                    }

                    postImage.setImageResource(R.drawable.img);
                    writeTextPost.getEditText().setText("");
                    thump_downloadUrl = "empty";
                    mprogressdialogg.dismiss();

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getContext(), "حدث خطا ", Toast.LENGTH_SHORT).show();
                    mprogressdialogg.hide();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    HashMap<String, String> posts = new HashMap<>();
                    posts.put("useremail", user);
                    posts.put("username", name);
                    posts.put("userimage", image);
                    posts.put("posttext", GetTextP);
                    posts.put("postimage", thump_downloadUrl);
                    return posts;

                }
            };
            Volley.newRequestQueue(getContext()).add(stringRequest);

        }
        return true;
    }

         public void DataGet(int m){
             Log.e("dataget", "starting th requst");
             String UriJson = "https://omhassangroup.000webhostapp.com/getdata.php?limet="+m ;
             requestQueue = Volley.newRequestQueue(getContext());
             jsonObjectRequest = new JsonObjectRequest(UriJson, null,
                     new Response.Listener<JSONObject>() {
                         @Override
                         public void onResponse(JSONObject responde) {
                             try {
                                 JSONArray jsonArray = responde.getJSONArray("posts");
                                 Log.e("dataget", "json array length: " +String.valueOf(jsonArray.length()) );
                                 List<IItemWithAds> tempNewItemsList = new ArrayList<>();
                                 for (int i = 0; i < jsonArray.length(); i++) {
                                     JSONObject respond = jsonArray.getJSONObject(i);
                                     int id = respond.getInt("id");
                                     String useremail = respond.getString("useremail");
                                     String username = respond.getString("username");
                                     String userimage = respond.getString("userimage");
                                     String posttext = respond.getString("posttext");
                                     String postimage = respond.getString("postimage");
                                     String datatime = respond.getString("datatime");
                                     AdapterList adapterList = new AdapterList(id, useremail, username, userimage, posttext, postimage, datatime);
                                     tempNewItemsList.add(adapterList);

                                     if (i % 3 == 0) {
                                         tempNewItemsList.add(new AdsItem());
                                     }
                                 }
                                 Log.e("dataget", "temp list size: " +String.valueOf(tempNewItemsList) );
                                 posts.addAll(tempNewItemsList);
                                 if (postsRecycler == null) {
                                     postsRecycler = new PostsRecycler(posts, getContext());
                                     recyclerView.setAdapter(postsRecycler);
                                     Log.e("dataget", "adapter set with items count: " +String.valueOf(postsRecycler.getItemCount()) );
                                     Log.e("dataget", "recycelr chiledren count: " +String.valueOf(recyclerView.getChildCount()) );
                                 } else {
                                     postsRecycler.notifyItemRangeInserted(postsRecycler.getItemCount()-1,tempNewItemsList.size());
                                 }
                             } catch (JSONException e) {
                                 e.printStackTrace();
                             }


                         }


                     },
                     new Response.ErrorListener() {
                         @Override
                         public void onErrorResponse(VolleyError volleyError) {
                             Log.e("dataget", "ERROR :" +volleyError.getMessage());

                         }
                     });
             requestQueue.add(jsonObjectRequest);
         }











    //****************************************************************************************?//
    public class PostsRecycler extends RecyclerView.Adapter< RecyclerView.ViewHolder> {
        ArrayList<IItemWithAds> posts = new ArrayList<>();
        Context context ;

        public PostsRecycler(ArrayList<IItemWithAds> posts, Context context) {
            this.posts = posts;
            this.context = context;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView;
            RecyclerView.ViewHolder viewHolder;
            LayoutInflater inflater =  LayoutInflater.from(parent.getContext());
            if (viewType == IItemWithAds.ITEM_TYPE_NORMAL) {
                itemView = inflater.inflate(R.layout.row_item, parent, false);
                viewHolder = new MyPostsView(itemView) ;
            } else {
                itemView = inflater.inflate(R.layout.layout_item_ads, parent, false);
                // virew holder for ads here
                viewHolder = new MyAdsView(itemView);
              //  viewHolder = new .....
            }



            return viewHolder ;
        }

        @Override
        public void onBindViewHolder( RecyclerView.ViewHolder viewHolder, int position) {

            if (getItemViewType(position) == IItemWithAds.ITEM_TYPE_NORMAL) {
                final MyPostsView postsViewHolder = (MyPostsView) viewHolder;
                AdapterList postItem = (AdapterList) posts.get(position);
                final int id_post = postItem.getUid();
                postsViewHolder.tName.setText(postItem.getUsername());
                postsViewHolder.tTime.setText(postItem.getDatatime());
                Picasso.with(context).load(postItem.getUserimage()).placeholder(R.drawable.ava).into(postsViewHolder.userImage);
                postsViewHolder.tPost.setText(postItem.getPosttext());
                Picasso.with(context).load(postItem.getPostimage()).into(postsViewHolder.postImage);

                //////
                PUserData.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(String.valueOf(id_post)).hasChild("count")) {
                            int count = Integer.parseInt(dataSnapshot.child(String.valueOf(id_post)).child("count").getValue().toString());
                            if (count > 1) {
                                postsViewHolder.count_like.setText("و" + count + "اعجبهم هذا ");
                            } else {
                                postsViewHolder.count_like.setVisibility(View.INVISIBLE);
                            }
                        } else {
                            postsViewHolder.count_like.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                //////

                PUserData.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.child(String.valueOf(id_post)).hasChild(UserId)) {
                            postsViewHolder.blike.setBackgroundResource(R.drawable.handb);
                        } else {
                            postsViewHolder.blike.setBackgroundResource(R.drawable.handa);
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                postsViewHolder.tLike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mprogresslike = true;

                        PUserData.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.child(String.valueOf(id_post)).hasChild("count")) {
                                    countsl = Integer.parseInt(dataSnapshot.child(String.valueOf(id_post)).child("count").getValue().toString());
                                } else {
                                    countsl = 0;
                                }

                                if (mprogresslike) {
                                    if (dataSnapshot.child(String.valueOf(id_post)).hasChild(UserId)) {
                                        PUserData.child(String.valueOf(id_post)).child("count").setValue(countsl - 1);
                                        PUserData.child(String.valueOf(id_post)).child(UserId).removeValue();
                                        countsl = 0;

                                        // Toast.makeText(context, "unlike", Toast.LENGTH_SHORT).show();
                                        mprogresslike = false;
                                        postsViewHolder.blike.setBackgroundResource(R.drawable.handa);
                                    } else {
                                        PUserData.child(String.valueOf(id_post)).child("count").setValue(countsl + 1);
                                        PUserData.child(String.valueOf(id_post)).child(UserId).setValue("RandomValue");
                                        MediaPlayer mediaPlayer = MediaPlayer.create(getContext(), R.raw.like);
                                        mediaPlayer.start();
                                        countsl = 0;

                                        //  Toast.makeText(context, "like", Toast.LENGTH_SHORT).show();
                                        mprogresslike = false;
                                        postsViewHolder.blike.setBackgroundResource(R.drawable.handb);
                                    }
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                });


                postsViewHolder.blike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mprogresslike = true;

                        PUserData.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.child(String.valueOf(id_post)).hasChild("count")) {
                                    countsl = Integer.parseInt(dataSnapshot.child(String.valueOf(id_post)).child("count").getValue().toString());
                                } else {
                                    countsl = 0;
                                }

                                if (mprogresslike) {
                                    if (dataSnapshot.child(String.valueOf(id_post)).hasChild(UserId)) {
                                        PUserData.child(String.valueOf(id_post)).child("count").setValue(countsl - 1);
                                        PUserData.child(String.valueOf(id_post)).child(UserId).removeValue();
                                        countsl = 0;

                                        // Toast.makeText(context, "unlike", Toast.LENGTH_SHORT).show();
                                        mprogresslike = false;
                                        postsViewHolder.blike.setBackgroundResource(R.drawable.handa);
                                    } else {
                                        PUserData.child(String.valueOf(id_post)).child("count").setValue(countsl + 1);
                                        PUserData.child(String.valueOf(id_post)).child(UserId).setValue("RandomValue");
                                        MediaPlayer mediaPlayer = MediaPlayer.create(getContext(), R.raw.like);
                                        mediaPlayer.start();
                                        countsl = 0;

                                        //  Toast.makeText(context, "like", Toast.LENGTH_SHORT).show();
                                        mprogresslike = false;
                                        postsViewHolder.blike.setBackgroundResource(R.drawable.handb);
                                    }
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                });

                postsViewHolder.bcomment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), CommentActivity.class);
                        intent.putExtra("pid", id_post);
                        startActivity(intent);
                    }
                });

                //   postsViewHolder.SetLike();


            } else {
                MyAdsView adsView = (MyAdsView) viewHolder ;
                AdRequest adRequest = new AdRequest.Builder().build();
                adsView.AdView.loadAd(adRequest);
               // ads logic here

            }
        }
        @Override
        public int getItemCount() {
            return posts.size();
        }

        @Override
        public int getItemViewType(int position) {
            return posts.get(position).getItemType();
        }

        public  class  MyPostsView extends RecyclerView.ViewHolder{
            TextView tName , tTime , tPost , tCountLike ,tLike , tComment ;
            CircleImageView userImage ;
            ImageView postImage ;
            Button blike , bcomment ;
            TextView count_comment , count_like ;

          //  public void SetLike(){


         //   }

            public MyPostsView(View itemView) {
                super(itemView);
                blike = (Button)itemView.findViewById(R.id.bt_image_like);
                bcomment = (Button)itemView.findViewById(R.id.bt_image_comment);
                tName =(TextView) itemView.findViewById(R.id.textview_name);
                tTime =(TextView) itemView.findViewById(R.id.textview_time);
                tPost =(TextView) itemView.findViewById(R.id.textview_post);
                tLike =(TextView) itemView.findViewById(R.id.text_like);
                tComment =(TextView) itemView.findViewById(R.id.text_comment);

                userImage = (CircleImageView)itemView.findViewById(R.id.imageuser_p);
                postImage = (ImageView)itemView.findViewById(R.id.imageView_post);

                count_like = (TextView)itemView.findViewById(R.id.textview_count_like);
                count_comment = (TextView)itemView.findViewById(R.id.comments_count);
            }
        }

    }






    //******************************************************************************************//

  public class MyAdsView extends RecyclerView.ViewHolder{
      AdView AdView;
      public MyAdsView(View itemView) {
          super(itemView);

          AdView = (AdView) itemView.findViewById(R.id.ads_view_layout);


      }
  }





}

