package com.codepath.apps.restclienttemplate.activities;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.utils.TwitterApplication;
import com.codepath.apps.restclienttemplate.utils.TwitterClient;
import com.codepath.apps.restclienttemplate.databinding.ActivityTweetDetailBinding;
import com.codepath.apps.restclienttemplate.fragments.ReplyFragment;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class TweetDetailActivity extends AppCompatActivity implements ReplyFragment.ReplyTweetListener {

    public static final String TAG = "TweetDetailActivity";
    ImageView photo;
    TextView name;
    TextView body;
    Context mctx;
    ImageView Reply;
    FragmentManager fm;
    ImageView image_embed;
    private TwitterClient client;
    Toolbar toolbar;
    private ActivityTweetDetailBinding binding;
    Long tweet_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_tweet_detail);
        mctx = TweetDetailActivity.this;
        final Tweet tweet = getIntent().getParcelableExtra("Tweet");
        toolbar = binding.appbar.toolbar;
        setSupportActionBar(toolbar);

        tweet_id = tweet.gettweet_id();
        photo = binding.profileImageDetail;
        name = binding.nameDetail;
        body = binding.bodyDetail;
        Reply = binding.replyBtn;
        image_embed = binding.imageEmbed;
        client = TwitterApplication.getRestClient();
        Typeface font_name = Typeface.createFromAsset(mctx.getAssets(), "fonts/Aller_BdIt.ttf");
        Typeface font_body = Typeface.createFromAsset(mctx.getAssets(), "fonts/Pacifico.ttf");

        name.setText(tweet.getUser().getName()+"\n@"+tweet.getUser().getScreen_name());
        name.setTypeface(font_name);
        body.setText(tweet.getBody());
        body.setTypeface(font_body);
        Glide.with(mctx).load(tweet.getUser().getProfile_imageURL()).bitmapTransform(new RoundedCornersTransformation(mctx,4,1, RoundedCornersTransformation.CornerType.ALL)).into(photo);

        fm = getSupportFragmentManager();
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


        if(tweet.getEntities()==null){
            image_embed.setVisibility(View.GONE);
        }
        else{
            image_embed.setVisibility(View.VISIBLE);
            Glide.with(mctx).load(tweet.getEntities().getMedia().getMedia_url()).into(image_embed);

        }

    }

    @Override
    public void onReplyTweet(String reply) {
        client.replyTweet(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    Log.i(TAG,"onReplyTweet : Onsuccess");
                    Tweet tweetreply = Tweet.fromJSON(response);
                    Log.i(TAG,"onReplyTweet : tweet = "+tweetreply.getBody());

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        },reply,tweet_id);
    }
}
