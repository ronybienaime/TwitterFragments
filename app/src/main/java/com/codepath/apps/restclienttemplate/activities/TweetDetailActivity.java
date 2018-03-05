package com.codepath.apps.restclienttemplate.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.utils.PatternEditableBuilder;
import com.codepath.apps.restclienttemplate.utils.TwitterApplication;
import com.codepath.apps.restclienttemplate.utils.TwitterClient;
import com.codepath.apps.restclienttemplate.databinding.ActivityTweetDetailBinding;
import com.codepath.apps.restclienttemplate.fragments.ReplyFragment;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class TweetDetailActivity extends AppCompatActivity implements ReplyFragment.ReplyTweetListener {

    public static final String TAG = "TweetDetailActivity";
    public static final String TWEET = "tweet";
    public static final String POSITION = "position";
    ImageView photo;
    TextView name;
    TextView body;
    Context mctx;
    ImageView Reply;
    FragmentManager fm;
    ImageView image_embed;
    private TwitterClient client;

    TextView reply_count;
    TextView retweet_count;
    TextView fav_count;

    ImageView retweet;
    ImageView fav;
    Tweet tweet;
    ColorStateList oldColors;
    int position;


    Toolbar toolbar;
    private ActivityTweetDetailBinding binding;
    Long tweet_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_tweet_detail);
        mctx = TweetDetailActivity.this;
        tweet = getIntent().getParcelableExtra(TWEET);
        position = getIntent().getIntExtra(POSITION,0);
        toolbar = binding.appbar.toolbar;
        setSupportActionBar(toolbar);
        setUpViews();
        client = TwitterApplication.getRestClient();
        tweet_id = tweet.gettweet_id();
        name.setText(tweet.getUser().getName()+"\n@"+tweet.getUser().getScreen_name());
        body.setText(tweet.getBody());
        oldColors =  reply_count.getTextColors();
        setupPatternListener();
        Glide.with(mctx).load(tweet.getUser().getProfile_imageURL()).bitmapTransform(new RoundedCornersTransformation(mctx,4,1, RoundedCornersTransformation.CornerType.ALL)).into(photo);

        fm = getSupportFragmentManager();

        setListeners();

        if(tweet.getEntities()==null){
            image_embed.setVisibility(View.GONE);
        }
        else{
            image_embed.setVisibility(View.VISIBLE);
            Glide.with(mctx).load(tweet.getEntities().getMedia().getMedia_url()).into(image_embed);

        }
        loadCountViews();
        setUpRetweetAndFavViews();

    }

    private void setupPatternListener(){
        new PatternEditableBuilder().
                addPattern(Pattern.compile("\\@(\\w+)"), TweetDetailActivity.this.getResources().getColor(R.color.twitter_logo_blue),
                        new PatternEditableBuilder.SpannableClickedListener() {
                            @Override
                            public void onSpanClicked(String text) {
                                Toast.makeText(TweetDetailActivity.this, "Clicked username: " + text,
                                        Toast.LENGTH_SHORT).show();
                            }
                        }).into(body);
        new PatternEditableBuilder().
                addPattern(Pattern.compile("\\#(\\w+)"), TweetDetailActivity.this.getResources().getColor(R.color.twitter_logo_blue),
                        new PatternEditableBuilder.SpannableClickedListener() {
                            @Override
                            public void onSpanClicked(String text) {
                                Toast.makeText(TweetDetailActivity.this, "Clicked Hashtag: " + text,
                                        Toast.LENGTH_SHORT).show();
                            }
                        }).into(body);

    }

    private void loadCountViews(){
        reply_count.setText(""+tweet.getReply_count());
        retweet_count.setText(""+tweet.getRetweet_count());
        fav_count.setText(""+tweet.getFavorite_count());
    }

    private void setUpRetweetAndFavViews(){
        if(tweet.isRetweeted()){
            retweet.setColorFilter(mctx.getResources().getColor(R.color.twitter_retweet), PorterDuff.Mode.SRC_IN);
            retweet_count.setTextColor(mctx.getResources().getColor(R.color.twitter_retweet));
        }
        if(tweet.isFavorited()){
            fav.setImageResource(R.drawable.ic_like1);
            fav_count.setTextColor(Color.MAGENTA);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(TWEET,tweet);
        intent.putExtra(POSITION,position);
        setResult(RESULT_OK,intent);
        finish();
    }


    private void setUpViews(){
        photo = binding.profileImageDetail;
        name = binding.nameDetail;
        body = binding.bodyDetail;
        Reply = binding.actionLayout.replyItemTweet;
        retweet = binding.actionLayout.retweetItemTweet;
        fav = binding.actionLayout.likeItemTweet;
        image_embed = binding.imageEmbed;
        reply_count = binding.actionLayout.replyCountText;
        retweet_count = binding.actionLayout.retweetCountText;
        fav_count = binding.actionLayout.likeCountText;
    }


    private void setListeners(){
        Reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ReplyFragment reply = new ReplyFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelable("Tweet",tweet);
                reply.setArguments(bundle);
                reply.show(fm,"reply_frag");
            }
        });

        retweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                client.postReTweet(new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Log.i(TAG, response.toString());
                        Log.i(TAG, "Retweet success");
                        Toast.makeText(mctx,"Retweet done",Toast.LENGTH_SHORT).show();
                        retweet.setColorFilter(mctx.getResources().getColor(R.color.twitter_retweet), PorterDuff.Mode.SRC_IN);
                        retweet_count.setText(""+(tweet.getRetweet_count()+1));
                        tweet.setRetweet_count(tweet.getRetweet_count()+1);
                        tweet.setRetweeted(true);
                        retweet_count.setTextColor(mctx.getResources().getColor(R.color.twitter_retweet));

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Log.i(TAG, errorResponse.toString());
                        Toast.makeText(mctx,"You have already retweeted this tweet.",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Log.i(TAG, responseString);
                        throwable.printStackTrace();
                    }
                },tweet.gettweet_id());
            }
        });

        fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG,"Fav clicked "+tweet.getUser().getScreen_name());
                if(tweet.isFavorited()) {// it's already favorite, now remove the favorite : toggle it
                    removeFavorite(tweet);
                }

                else{
                    createFavorite(tweet);
                }
            }
        });
    }

    private void removeFavorite(final Tweet tweet){
        client.postDestroyFavorite(new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.i(TAG, response.toString());
                Log.i(TAG, "Remove Favorite success");
                fav.setImageResource(R.drawable.ic_twitter_like_outline);
                fav_count.setText(""+(tweet.getFavorite_count()-1));
                fav_count.setTextColor(oldColors);
                setFavStatus(false);
                Toast.makeText(mctx,"Remove Favorite success",Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.i(TAG, errorResponse.toString());
                Toast.makeText(mctx,"Remove favorite failed",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.i(TAG, responseString);
                throwable.printStackTrace();
            }
        },tweet.gettweet_id());
    }

    private void createFavorite(final Tweet tweet){

        client.postCreateFavorite(new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.i(TAG, response.toString());
                Log.i(TAG, "Add favorite success");
                fav.setImageResource(R.drawable.ic_like1);
                fav_count.setText(""+(tweet.getFavorite_count()+1));
                fav_count.setTextColor(Color.MAGENTA);
                setFavStatus(true);
                Toast.makeText(mctx,"Add favorite done",Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.i(TAG, errorResponse.toString());
                Toast.makeText(mctx,"Add favorite failed",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.i(TAG, responseString);
                throwable.printStackTrace();
            }
        },tweet.gettweet_id());
    }

    private void setFavStatus(boolean status){
        tweet.setFavorited(status);
        if(status)
            tweet.setFavorite_count(tweet.getFavorite_count()+1);
        else
            tweet.setFavorite_count(tweet.getFavorite_count()-1);
    }


    @Override
    public void onReplyTweet() {
        Toast.makeText(mctx,"Tweet Replied",Toast.LENGTH_SHORT).show();
    }
}
