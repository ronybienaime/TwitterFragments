package com.codepath.apps.restclienttemplate.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.databinding.ItemTweetBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.utils.ParseRelativeDate;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by anushree on 9/27/2017.
 */

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {


    ArrayList<Tweet> list;
    Context mCtx;

    public TweetAdapter(Context context, ArrayList<Tweet> tweetList){
        list = tweetList;
        mCtx = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater li = LayoutInflater.from(mCtx);
        View view = li.inflate(R.layout.item_tweet,parent,false);

        ViewHolder holder = new ViewHolder(view, mCtx);
        return holder;

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Tweet tweet = list.get(position);
        holder.bind(tweet);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView profile_image;
        TextView name;
        TextView body;
        TextView post_time;
        TextView screen_name;
        Context ctx;
        android.widget.VideoView video_tweet;
        private ItemTweetBinding binding;




        public ViewHolder(View itemView, Context context) {
            super(itemView);
            binding = ItemTweetBinding.bind(itemView);
            ctx = context;
            profile_image = binding.profileImage;
            name = binding.name;
            body = binding.body;
            post_time = binding.postTime;
            video_tweet = binding.videoTweet;
            screen_name = binding.screenName;

        }


        public void bind(Tweet tweet){


            name.setText(tweet.getUser().getName());
            screen_name.setText("@"+tweet.getUser().getScreen_name());
            body.setText(tweet.getBody());
            Typeface font_name = Typeface.createFromAsset(ctx.getAssets(), "fonts/Aller_BdIt.ttf");
            Typeface font_body = Typeface.createFromAsset(ctx.getAssets(), "fonts/Pacifico.ttf");
            name.setTypeface(font_name);
            body.setTypeface(font_body);
            screen_name.setTypeface(font_name);
            post_time.setText(tweet.getCreated_at());
            Glide.with(ctx).load(tweet.getUser().getProfile_imageURL()).bitmapTransform(new RoundedCornersTransformation(ctx,4,1, RoundedCornersTransformation.CornerType.ALL)).into(profile_image);


            if(tweet.getEntities()!=null&& tweet.getEntities().getMedia().getMedia_type().equals("video")){
                video_tweet.setVisibility(View.VISIBLE);
                video_tweet.setVideoPath(tweet.getEntities().getMedia().getVideo_url());

                MediaController mediaController = new MediaController(ctx);
                mediaController.setAnchorView(video_tweet);
                mediaController.setVisibility(View.GONE);
                video_tweet.setMediaController(mediaController);
                video_tweet.requestFocus();
                video_tweet.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    // Close the progress bar and play the video
                    public void onPrepared(MediaPlayer mp) {
                        video_tweet.start();
                    }
                });

            }
            else {
                video_tweet.setVisibility(View.GONE);
            }

        }



    }


    public void clear() {
        list.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Tweet> pList) {
        list.addAll(pList);
        notifyDataSetChanged();
    }
}
