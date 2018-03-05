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
import com.codepath.apps.restclienttemplate.databinding.ItemFriendBinding;
import com.codepath.apps.restclienttemplate.models.Friends;
import com.codepath.apps.restclienttemplate.models.Tweet;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;



public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder>  {


    ArrayList<Friends> list;
    Context mCtx;


    public interface ProfileClickedListener{
        public void onProfileClicked(Tweet tweet);
        public void onRetweetClicked(Tweet tweet);
        public void onReplyClicked(Tweet tweet);
        public void onFavClicked(Tweet tweet);

    }

    public FriendsAdapter(Context context, ArrayList<Friends> friendList/*, TweetAdapter.ProfileClickedListener listener*/){
        list = friendList;
        mCtx = context;
        //mListener = listener;
    }

    @Override
    public FriendsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater li = LayoutInflater.from(mCtx);
        View view = li.inflate(R.layout.item_friend,parent,false);

        FriendsAdapter.ViewHolder holder = new FriendsAdapter.ViewHolder(view, mCtx);
        return holder;

    }

    @Override
    public void onBindViewHolder(FriendsAdapter.ViewHolder holder, int position) {

        Friends friend = list.get(position);
        holder.bind(friend);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{


        private ItemFriendBinding binding;
        TextView name;
        TextView body;
        TextView screen_name;
        Context mCtx;
        CircleImageView profile_image;
        Button follow;
        Button following;



        public ViewHolder(View itemView, Context context) {
            super(itemView);
            binding = ItemFriendBinding.bind(itemView);
            mCtx = context;
            name = binding.friendName;
            screen_name = binding.friendScreenName;
            body = binding.friendDesc;
            profile_image = binding.friendProfileImage;
            follow = binding.followBtn;
            following = binding.followingBtn;

        }


        public void bind(Friends friend){

            name.setText(friend.getName());
            screen_name.setText("@"+friend.getScreen_name());
            body.setText(friend.getDescription());
            Glide.with(mCtx).load(friend.getProfile_imageURL()).into(profile_image);
            setFollow(friend);

        }

        private void setFollow(Friends friend){
            if(friend.isFollowing())
            {
                following.setVisibility(View.VISIBLE);
                follow.setVisibility(View.GONE);
            }
            else{
                following.setVisibility(View.GONE);
                follow.setVisibility(View.VISIBLE);
            }
        }

    }


    public void clear() {
        list.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Friends> pList) {
        list.addAll(pList);
        notifyDataSetChanged();
    }
}
