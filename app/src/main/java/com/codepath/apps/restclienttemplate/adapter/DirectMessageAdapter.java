package com.codepath.apps.restclienttemplate.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.databinding.ItemDirectMessageBinding;
import com.codepath.apps.restclienttemplate.databinding.ItemFriendBinding;
import com.codepath.apps.restclienttemplate.models.DirectMessage;
import com.codepath.apps.restclienttemplate.models.Friends;
import com.codepath.apps.restclienttemplate.models.Tweet;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;



public class DirectMessageAdapter extends RecyclerView.Adapter<DirectMessageAdapter.ViewHolder>  {


    ArrayList<DirectMessage> list;
    Context mCtx;
    //TweetAdapter.ProfileClickedListener mListener;

    public DirectMessageAdapter(Context context, ArrayList<DirectMessage> msgList/*, TweetAdapter.ProfileClickedListener listener*/){
        list = msgList;
        mCtx = context;
        //mListener = listener;
    }

    @Override
    public DirectMessageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater li = LayoutInflater.from(mCtx);
        View view = li.inflate(R.layout.item_direct_message,parent,false);

        DirectMessageAdapter.ViewHolder holder = new DirectMessageAdapter.ViewHolder(view, mCtx);
        return holder;

    }

    @Override
    public void onBindViewHolder(DirectMessageAdapter.ViewHolder holder, int position) {

        DirectMessage message = list.get(position);
        holder.bind(message);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{


        private ItemDirectMessageBinding binding;
        TextView name;
        TextView text;
        TextView screen_name;
        Context mCtx;
        CircleImageView profile_image;
        TextView created;



        public ViewHolder(View itemView, Context context) {
            super(itemView);
            binding = ItemDirectMessageBinding.bind(itemView);
            mCtx = context;
            name = binding.recipientName;
            screen_name = binding.recipientScreenName;
            text = binding.messageText;
            profile_image = binding.recipientProfileImage;
            created = binding.created;

        }


        public void bind(DirectMessage message){

            name.setText(message.getRecipient_name());
            screen_name.setText("@"+message.getRecipient_screen_name());
            text.setText(message.getText());
            created.setText(message.getCreated_at());
            Glide.with(mCtx).load(message.getRecipient_profile_url()).into(profile_image);
        }

    }


    public void clear() {
        list.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<DirectMessage> pList) {
        list.addAll(pList);
        notifyDataSetChanged();
    }
}
