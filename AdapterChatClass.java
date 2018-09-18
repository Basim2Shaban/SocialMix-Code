package com.basm.socialmix;

import android.content.ClipData;
import android.content.Context;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Basim on 16/02/2018.
 */

public class AdapterChatClass extends RecyclerView.Adapter<AdapterChatClass.ViewRecycler>{


    String myname ;
    private List<Massage_item> arrayList ;
    Context context ;

    public AdapterChatClass(List<Massage_item> arrayList, Context context , String myname) {
        this.arrayList = arrayList;
        this.context = context;
        this.myname = myname ;
    }

    @Override
    public ViewRecycler onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == 0) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.massage_item, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.user_layout_massage, parent, false);
        }

        return new ViewRecycler(view);
    }

    @Override
    public void onBindViewHolder(ViewRecycler holder, int position) {
        Log.e("value","this : "+ arrayList.get(position).getImage());
        Picasso.with(context).load(arrayList.get(position).getImage()).into(holder.image_massage);

        holder.massage.setText(arrayList.get(position).getMassage());

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
    @Override
    public int getItemViewType(int position) {
        String name = arrayList.get(position).getName();
        if (name == null) {
            return 0;
        }
        if (arrayList.get(position).getName().equals(myname)) {
            return 0;
        } else {
            return 1;
        }

    }

    public class ViewRecycler extends RecyclerView.ViewHolder{
        private TextView massage, you;
        private ImageView image_massage, view_you;

        public ViewRecycler(View itemView) {
            super(itemView);

            massage = (TextView) itemView.findViewById(R.id.text_massage);
            //    you = (TextView) itemView.findViewById(R.id.text_user);
            //  view_me = (ImageView) itemView.findViewById(R.id.image_me);
            image_massage = (ImageView) itemView.findViewById(R.id.image_massage);
        }
    }


}
