package com.codepath.apps.restclienttemplate.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.databinding.ItemTweetBinding;
import com.codepath.apps.restclienttemplate.fragments.ReplyFragment;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.utils.ParseRelativeDate;
import com.codepath.apps.restclienttemplate.utils.PatternEditableBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by anushree on 9/27/2017.
 */

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {


    ArrayList<Tweet> list;
    Context mCtx;
    ProfileClickedListener mListener;


    public interface ProfileClickedListener{
            public void onProfileClicked(Tweet tweet);
            public void onRetweetClicked(Tweet tweet, int position);
            public void onReplyClicked(Tweet tweet);
            public void onFavClicked(Tweet tweet, int position);
            public void onUserNameClicked(String userName);

    }

    public TweetAdapter(Context context, ArrayList<Tweet> tweetList, ProfileClickedListener listener){
        list = tweetList;
        mCtx = context;
        mListener = listener;
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

    public class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView profile_image;
        TextView name;
        TextView body;
        TextView post_time;
        TextView screen_name;

        TextView reply_count;
        TextView retweet_count;
        TextView fav_count;

        ImageView reply_tweet;
        ImageView retweet;
        ImageView fav_tweet;
        ColorStateList oldColors;
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

            reply_count = binding.actionLayout.replyCountText;
            retweet_count = binding.actionLayout.retweetCountText;
            fav_count = binding.actionLayout.likeCountText;

            reply_tweet = binding.actionLayout.replyItemTweet;
            retweet = binding.actionLayout.retweetItemTweet;
            fav_tweet = binding.actionLayout.likeItemTweet;

            oldColors =  reply_count.getTextColors();
        }


        public void bind(Tweet tweet){
            name.setText(tweet.getUser().getName());
            screen_name.setText("@"+tweet.getUser().getScreen_name());
            body.setText(tweet.getBody());
            new PatternEditableBuilder().
                    addPattern(Pattern.compile("\\@(\\w+)"),ctx.getResources().getColor(R.color.twitter_logo_blue),
                            new PatternEditableBuilder.SpannableClickedListener() {
                                @Override
                                public void onSpanClicked(String text) {
                                    if(mListener!=null){
                                        mListener.onUserNameClicked(text);
                                    }
                                }
                            }).into(body);
            new PatternEditableBuilder().
                    addPattern(Pattern.compile("\\#(\\w+)"), ctx.getResources().getColor(R.color.twitter_logo_blue),
                    new PatternEditableBuilder.SpannableClickedListener() {
                        @Override
                        public void onSpanClicked(String text) {
                            Toast.makeText(ctx, "Clicked Hashtag: " + text,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }).into(body);
            post_time.setText(tweet.getCreated_at());
            Glide.with(ctx).load(tweet.getUser().getProfile_imageURL()).into(profile_image);
            setListeners(tweet);
            setVideoView(tweet);
            setFavoriteColor(tweet);
            setRetweetColor(tweet);
            reply_count.setText(""+tweet.getReply_count());
            retweet_count.setText(""+tweet.getRetweet_count());
            fav_count.setText(""+tweet.getFavorite_count());



        }

        private void setFavoriteColor(Tweet tweet){
            if(tweet.isFavorited()){
                //fav_tweet.setColorFilter(Color.MAGENTA, PorterDuff.Mode.SRC_IN);
                fav_tweet.setImageResource(R.drawable.ic_like1);
                fav_count.setTextColor(Color.MAGENTA);
            }
            else{
                fav_tweet.setImageResource(R.drawable.ic_twitter_like_outline);
                fav_count.setTextColor(oldColors);
            }
        }

        private void setRetweetColor(Tweet tweet){
            if(tweet.isRetweeted()){
                retweet.setColorFilter(ctx.getResources().getColor(R.color.twitter_retweet), PorterDuff.Mode.SRC_IN);
                retweet_count.setTextColor(ctx.getResources().getColor(R.color.twitter_retweet));
            }
            else{
                retweet.clearColorFilter();
                retweet_count.setTextColor(oldColors);
            }
        }

        private void setVideoView(Tweet tweet){
            if(tweet.getEntities()!=null&& tweet.getEntities().getMedia().getMedia_type().equals("video")){
                video_tweet.setVideoPath(tweet.getEntities().getMedia().getVideo_url());
                MediaController mediaController = new MediaController(ctx);
                mediaController.setAnchorView(video_tweet);
                mediaController.setVisibility(View.GONE);
                video_tweet.setMediaController(mediaController);
                video_tweet.setVisibility(View.VISIBLE);
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


        private void setListeners(final Tweet tweet){
            profile_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mListener!=null)
                        mListener.onProfileClicked(tweet);
                }
            });

            retweet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mListener!=null)
                        mListener.onRetweetClicked(tweet,getAdapterPosition());
                }
            });

            reply_tweet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mListener!=null)
                        mListener.onReplyClicked(tweet);
                }

            });


            fav_tweet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mListener!=null)
                        mListener.onFavClicked(tweet,getAdapterPosition());
                }

            });
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
