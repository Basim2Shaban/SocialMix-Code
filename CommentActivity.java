package com.basm.socialmix;

import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class CommentActivity extends AppCompatActivity {

   private ImageButton btn_send_comment  ;
    private ImageView btn_load_image ;
   private EditText editText_comment ;
   private int pid , count_comment  ;
   private int number ;
    private byte[] edata ;
 private StorageReference mStorageComment ;
    private FirebaseUser CurrentUser ;
    private String final_uri  , User_id , uri_c , uri_get_comment ;
    private String user_email , username ,userimage , commenttext;
    private Bitmap res ;
    private DatabaseReference mDataBase , CoundComm;
    private RecyclerView recycler_Comment ;
    private ArrayList<List_item> comments = new ArrayList<>();
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int heighet = dm.heightPixels;

        getWindow().setLayout((width),(heighet));




        final_uri = "empty";


        Intent getdata = getIntent();
        pid = getdata.getExtras().getInt("pid");


        recycler_Comment = (RecyclerView)findViewById(R.id.list_comment);
        recycler_Comment.setHasFixedSize(true);
        final GridLayoutManager manager = new GridLayoutManager(CommentActivity.this,1);
        // manager.setStackFromEnd(true);
        recycler_Comment.setLayoutManager(manager);
        recycler_Comment.setItemAnimator(null);

        GetComments("p"+pid);




        btn_send_comment = (ImageButton)findViewById(R.id.btn_send_comment);
        btn_load_image = (ImageView)findViewById(R.id.btn_load_image);
        editText_comment= (EditText)findViewById(R.id.edit_text_comment);


        mStorageComment = FirebaseStorage.getInstance().getReference();
        CurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        CoundComm = FirebaseDatabase.getInstance().getReference();

        user_email = CurrentUser.getEmail();
        User_id = CurrentUser.getUid();



        mDataBase = FirebaseDatabase.getInstance().getReference().child("users").child(User_id);
        mDataBase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                username = dataSnapshot.child("name").getValue().toString().trim();
                userimage = dataSnapshot.child("image").getValue().toString().trim();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




        btn_load_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(intent, 10);
            }
        });



            btn_send_comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    commenttext = editText_comment.getText().toString().trim();
                    SendComments();

                }
            });





    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==10 && resultCode==RESULT_OK){
            Uri uri = data.getData();
            Glide.with(CommentActivity.this).load(R.drawable.loding).into(btn_load_image);




            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            int cl = (int) calendar.getTimeInMillis();

            Random r = new Random();
            int rf = r.nextInt(80 - 65) + 1000;

            number = cl + rf;



            try {
                InputStream input = this.getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(input);
                res = Bitmap.createScaledBitmap(bitmap,380,450,false);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                res.compress(Bitmap.CompressFormat.JPEG, 60, baos);
                edata = baos.toByteArray();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            final StorageReference thumb_file = mStorageComment.child("comments").child(User_id).child(number + ".jpg");
            thumb_file.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    UploadTask uploadTask = thumb_file.putBytes(edata);
                    uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            final_uri = task.getResult().getDownloadUrl().toString();
                            Glide.with(CommentActivity.this).load(final_uri).placeholder(R.drawable.loding).into(btn_load_image);
                        }
                    });
                }
            });

        }
    }

    public void SendComments() {
         uri_c = "https://omhassangroup.000webhostapp.com/insertcomment.php";
        if (commenttext.isEmpty() && final_uri.equalsIgnoreCase("empty")) {
            Toast.makeText(this, "can not post empty case", Toast.LENGTH_SHORT).show();
        } else {
            StringRequest req_q = new StringRequest(Request.Method.POST, uri_c, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Toast.makeText(CommentActivity.this,"تم التعليق", Toast.LENGTH_SHORT).show();
                    editText_comment.setText("");
                    btn_load_image.setImageDrawable(null);
                    final_uri = "empty";

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(CommentActivity.this,"p"+ pid, Toast.LENGTH_SHORT).show();

                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String,String>send_map = new HashMap();
                    send_map.put("pid","p"+pid);
                    send_map.put("useremail", user_email);
                    send_map.put("username", username);
                    send_map.put("userimage", userimage);
                    send_map.put("commenttext", commenttext);
                    send_map.put("commentimage", final_uri);
                    send_map.put("count",""+count_comment+"".trim());
                    return send_map;
                }
            };

            Volley.newRequestQueue(CommentActivity.this).add(req_q);

        }
    }

    public void GetComments(String idc){
        uri_get_comment = "https://omhassangroup.000webhostapp.com/getcomment.php?pid="+idc;
        requestQueue = Volley.newRequestQueue(CommentActivity.this) ;
        JsonObjectRequest request = new JsonObjectRequest(uri_get_comment, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("posts");
                            for (int i = 0 ; i <jsonArray.length();i++){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                int id = jsonObject.getInt("id");
                                String pid = jsonObject.getString("pid");
                                String useremail = jsonObject.getString("useremail");
                                String username = jsonObject.getString("username");
                                String userimage = jsonObject.getString("userimage");
                                String commenttext = jsonObject.getString("commenttext");
                                String commentimage = jsonObject.getString("commentimage");
                                String datatime = jsonObject.getString("datatime");

                                List_item list_item = new List_item(id,pid,useremail,username,userimage,commenttext,commentimage,datatime);
                                comments.add(list_item);
                                AdapterComment adapterComment = new AdapterComment(comments,CommentActivity.this);
                                recycler_Comment.setAdapter(adapterComment);
                                adapterComment.notifyDataSetChanged();

                                count_comment = comments.size() + 1 ;


                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VOLLEY", "ERROR");

            }
        });
        requestQueue.add(request);
    }




}
