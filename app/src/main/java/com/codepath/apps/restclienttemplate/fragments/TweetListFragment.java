package com.codepath.apps.restclienttemplate.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.activities.TweetDetailActivity;
import com.codepath.apps.restclienttemplate.adapter.TweetAdapter;
import com.codepath.apps.restclienttemplate.databinding.TweetListFragmentBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.apps.restclienttemplate.utils.EndlessRecyclerViewScrollListener;
import com.codepath.apps.restclienttemplate.utils.ItemClickSupport;
import com.codepath.apps.restclienttemplate.utils.TwitterApplication;
import com.codepath.apps.restclienttemplate.utils.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;




public class TweetListFragment extends Fragment implements TweetAdapter.ProfileClickedListener {

    public static final String TAG = "TweetListFragment";
    public static final String TWEET = "tweet";
    public static final String POSITION = "position";
    public static final int REQUEST_CODE = 1;

    public interface tweetPostedListener{
        public void onTweetPosted();
    }


    public interface ProfileLoadListener{
        public void onProfileLoad(User user);
        public void onReply(Tweet tweet);
    }

    TweetListFragmentBinding binding;
    RecyclerView rvTweet;
    SwipeRefreshLayout swipeContainer;
    ArrayList<Tweet> tweetList;
    TweetAdapter tweetAdp;
    Context mCtx;
    private TwitterClient client;
    EndlessRecyclerViewScrollListener scrollListener;
    LinearLayoutManager lm;
    ProgressDialog dialog;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.mCtx = context;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate TweetListFragment = ");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.tweet_list_fragment,container,false);
        View view = binding.getRoot();

        rvTweet = binding.rvTweet;
        swipeContainer = binding.swipeContainer;

        lm = new LinearLayoutManager(mCtx,LinearLayoutManager.VERTICAL,false);
        rvTweet.setLayoutManager(lm );
        tweetList = new ArrayList<>();
        tweetAdp = new TweetAdapter(mCtx,tweetList,this);
        rvTweet.setAdapter(tweetAdp);
        dialog = new ProgressDialog(mCtx,R.style.TwitterDialogStyle);


        Log.i(TAG, "onCreateView TweetListFragment = ");
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.i(TAG, "onViewCreated page =TweetList ");
        ItemClickSupport.addTo(rvTweet).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                Tweet tweet = tweetList.get(position);
                Intent intent = new Intent();
                intent.setClass(mCtx,TweetDetailActivity.class);
                intent.putExtra(TWEET,tweet);
                intent.putExtra(POSITION,position);
                startActivityForResult(intent,REQUEST_CODE);
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode == Activity.RESULT_OK){
            Log.i(TAG,"OnActivityResult");
            int position = data.getIntExtra(POSITION,0);
            tweetList.remove(position);
            Tweet tweet = data.getParcelableExtra(TWEET);
            tweetList.add(position,tweet);
            tweetAdp.notifyItemChanged(position);

        }
    }

    protected void prepareProgressDialog(){
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.progressdialog);
    }

    public static boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }
        return false;
    }


    public long getLastId(){
        if(tweetList ==null || tweetList.size()==0 )
            return -1;

        return (tweetList.get(tweetList.size() - 1).gettweet_id()) - 1;
    }


    public void addItemResponse(JSONArray response){
        tweetList.clear();
        tweetAdp.clear();
        tweetList.addAll(Tweet.getTweets(response));
        tweetAdp.notifyItemRangeInserted(0, tweetList.size() - 1);
    }


    public void updateRetweetItemResponse(int position){
        Tweet tweet = tweetList.get(position);
        tweet.setRetweeted(true);
        tweet.setRetweet_count(tweet.getRetweet_count()+1);
        tweetList.remove(position);
        tweetList.add(position,tweet);
        tweetAdp.notifyItemChanged(position);
    }

    public void updateFavItemResponse(int position, boolean fav){
        Tweet tweet = tweetList.get(position);
        tweet.setFavorited(fav);
        if(fav)
            tweet.setFavorite_count(tweet.getFavorite_count()+1);
        else
            tweet.setFavorite_count(tweet.getFavorite_count()-1);
        tweetList.remove(position);
        tweetList.add(position,tweet);
        tweetAdp.notifyItemChanged(position);
    }

    public void addMoreItems(JSONArray response){
        int curSize = tweetList.size();
        tweetList.addAll(Tweet.getTweets(response));
        tweetAdp.notifyItemRangeInserted(curSize,tweetList.size() - 1);
    }


    public void addNewTweet(JSONObject response) throws JSONException {
        Tweet tweet = Tweet.fromJSON(response);
        tweetList.add(0,tweet);
        tweetAdp.notifyItemInserted(0);
        rvTweet.scrollToPosition(0);
    }


    @Override
    public void onProfileClicked(Tweet tweet) {
        Log.i(TAG,"User profile clicked "+tweet.getUser().getScreen_name());
        ProfileLoadListener listener = (ProfileLoadListener) getActivity();
        listener.onProfileLoad(tweet.getUser());

    }

    @Override
    public void onReplyClicked(Tweet tweet) {
        Log.i(TAG,"Reply  clicked "+tweet.getUser().getScreen_name());
        ProfileLoadListener listener = (ProfileLoadListener) getActivity();
        listener.onReply(tweet);

    }

    @Override
    public void onRetweetClicked(Tweet tweet, int position) {
        Log.e(TAG,"This method should never be called.If called, something is Wrong ! ");
        Log.e(TAG,"Child fragment's OnRetweet Click to be called");


    }



    @Override
    public void onFavClicked(Tweet tweet, int position) {
        Log.e(TAG,"This method should never be called.If called, something is Wrong ! ");
        Log.e(TAG,"Child fragment's onFavClicked to be called");
    }

    @Override
    public void onUserNameClicked(String userName) {
        Log.e(TAG,"This method should never be called.If called, something is Wrong ! ");
        Log.e(TAG,"Child fragment's onUserNameClicked to be called");
    }

    public User getUserDetails(JSONObject object) throws JSONException {

        User user = User.fromJSON(object);
        return user;

    }
}
