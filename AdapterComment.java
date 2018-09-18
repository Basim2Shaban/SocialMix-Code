package com.basm.socialmix;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by basm on 06/01/2018.
 */

public class AdapterComment extends RecyclerView.Adapter<AdapterComment.Comment> {
    ArrayList<List_item> comments = new ArrayList<>();
    Context context ;

    public AdapterComment(ArrayList<List_item> comments, Context context) {
        this.comments = comments;
        this.context = context;
    }

    @Override
    public Comment onCreateViewHolder(ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_comment,parent,false);
       Comment coo = new Comment(view);
        return coo ;
    }

    @Override
    public void onBindViewHolder(Comment holder, int position) {
        holder.textView_username.setText(comments.get(position).getUsername());
        Glide.with(context).load(comments.get(position).getUserimage()).into(holder.imageView_user);
        holder.textView_usercomment.setText(comments.get(position).getCommenttext());
        Glide.with(context).load(comments.get(position).getCommentimage()).into(holder.imageView_comment);
        holder.textView_time.setText(comments.get(position).getDatatime());


    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class Comment extends RecyclerView.ViewHolder{
        CircleImageView imageView_user ;
        ImageView imageView_comment ;
        TextView textView_username , textView_usercomment , textView_time ;

        public Comment(View itemView) {
            super(itemView);
        imageView_user = (CircleImageView) itemView.findViewById(R.id.image_user_comment);
            imageView_comment = (ImageView)itemView.findViewById(R.id.image_comment);
            textView_username = (TextView)itemView.findViewById(R.id.text_name_user_comment);
            textView_usercomment = (TextView)itemView.findViewById(R.id.text_comment);
            textView_time = (TextView)itemView.findViewById(R.id.text_time);

        }
    }
}
