package com.codepath.apps.restclienttemplate.activities;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v4.app.FragmentManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.database.MyDatabase;
import com.codepath.apps.restclienttemplate.fragments.NewTweetFragment;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.utils.TwitterApplication;
import com.codepath.apps.restclienttemplate.utils.TwitterClient;
import com.codepath.apps.restclienttemplate.adapter.TweetAdapter;
import com.codepath.apps.restclienttemplate.databinding.ActivityTimelineBinding;
import com.codepath.apps.restclienttemplate.models.Entities;
import com.codepath.apps.restclienttemplate.models.Media;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.apps.restclienttemplate.utils.EndlessRecyclerViewScrollListener;
import com.codepath.apps.restclienttemplate.utils.ItemClickSupport;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ProcessModelTransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity implements NewTweetFragment.postTweetListener{
    public static final String TAG = "TimelineActivity";

    String user_name;
    String screen_name;
    String my_profile_image;
    private TwitterClient client;
    Context mCtx;
    ArrayList<Tweet> tweetList;
    TweetAdapter tweetAdp;
    FloatingActionButton floatTweet;
    EndlessRecyclerViewScrollListener scrollListener;
    SwipeRefreshLayout swipeContainer;
    private ActivityTimelineBinding binding;
    Toolbar toolbar;

    FragmentManager fm;
    RecyclerView rvTweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_timeline);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_timeline);
        client = TwitterApplication.getRestClient();

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        rvTweet = binding.rvTweet;
        toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        LinearLayoutManager lm= new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        rvTweet.setLayoutManager(lm );
        tweetList = new ArrayList<>();
        tweetAdp = new TweetAdapter(this,tweetList);
        scrollListener = new EndlessRecyclerViewScrollListener(lm) {

            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.i(TAG, "loadNextDataFromApi page"+page);
                loadNextDataFromApi(page);
            }
        };

        mCtx = TimelineActivity.this;
        rvTweet.setAdapter(tweetAdp);
        rvTweet.addOnScrollListener(scrollListener);
        fm = getSupportFragmentManager();
        swipeContainer = binding.swipeContainer;
        floatTweet = binding.ftTweet;
        floatTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(screen_name==null || user_name == null){
                    getUserInformation();
                }
                NewTweetFragment tweetfrag = new NewTweetFragment();
                Bundle bundle = new Bundle();

                bundle.putString("name",user_name);
                bundle.putString("screen_name",screen_name);
                bundle.putString("my_profile_image",my_profile_image);
                tweetfrag.setArguments(bundle);
                tweetfrag.show(fm,"newTweet");
            }
        });
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchTwitterTimeLine();
            }
        });

        ItemClickSupport.addTo(rvTweet).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                Tweet tweet = tweetList.get(position);
                Intent intent = new Intent();
                intent.setClass(mCtx,TweetDetailActivity.class);
                intent.putExtra("Tweet",tweet);
                startActivity(intent);
            }
        });

        populateTimeline();
        getUserInformation();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {

                // Make sure to check whether returned data will be null.
                String titleOfPage = intent.getStringExtra(Intent.EXTRA_SUBJECT);
                String urlOfPage = intent.getStringExtra(Intent.EXTRA_TEXT);

                getUserInformationAndLaunchNewTweet(titleOfPage,urlOfPage);
                Log.i(TAG,"Received Intent Twitter "+titleOfPage);
            }
        }


    }


    private void getUserInformationAndLaunchNewTweet(final String titleOfPage,final String urlOfPage){
        client.getUserInfo(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.i(TAG, response.toString());

                try {
                    user_name = response.getString("name");
                    screen_name = response.getString("screen_name");
                    my_profile_image = response.getString("profile_image_url");

                            NewTweetFragment tweetfrag = new NewTweetFragment();
                            Bundle bundle = new Bundle();
                            if(screen_name==null || user_name == null){
                                getUserInformation();
                            }
                            bundle.putString("name",user_name);
                            bundle.putString("screen_name",screen_name);
                            bundle.putString("my_profile_image",my_profile_image);
                            bundle.putString("intent_title",titleOfPage);
                            bundle.putString("intent_url",urlOfPage);
                            tweetfrag.setArguments(bundle);
                            tweetfrag.show(fm,"newTweet");
                            Log.i(TAG,"Received Intent Twitter "+titleOfPage);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }


    private void fetchTwitterTimeLine(){

        if(!isOnline()){
            Log.i(TAG, " fetchTwitterTimeLine : Internet not available");
            swipeContainer.setRefreshing(false);
            Toast.makeText(mCtx,"Internet is not available", Toast.LENGTH_LONG).show();

        }
        else {
            client.getTimelines(new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    Log.i(TAG, response.toString());
                    tweetList.clear();
                    tweetAdp.clear();

                    Delete.table(Tweet.class);
                    Delete.table(User.class);
                    Delete.table(Entities.class);
                    Delete.table(Media.class);
                    tweetList.addAll(Tweet.getTweets(response));
                    tweetAdp.notifyItemRangeInserted(0, tweetList.size() - 1);
                    swipeContainer.setRefreshing(false);
                    fillDatabase();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    Log.i(TAG, "FetchTime Line Error : " + errorResponse.toString());
                    swipeContainer.setRefreshing(false);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.i(TAG, "FetchTime Line Error : " + responseString);
                    throwable.printStackTrace();
                    swipeContainer.setRefreshing(false);
                }
            });
        }
    }

    public void getUserInformation() {

        if (!isOnline()) {
            Log.i(TAG, " getUserInformation : Internet not available");
        }
        else
        {
            client.getUserInfo(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.i(TAG, response.toString());

                    try {
                        user_name = response.getString("name");
                        screen_name = response.getString("screen_name");
                        my_profile_image = response.getString("profile_image_url");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                }
            });
        }
    }


    public void loadNextDataFromApi(int page) {

        if (!isOnline()) {
            Log.i(TAG, " loadNextDataFromApi : Internet not available");
            //Ask : Check if we need to load more tweets from database
        }
        else
        {

            Log.i(TAG, "loadNextDataFromApi page = " + page);
            long id = tweetList.get(tweetList.size() - 1).gettweet_id() - 1;
            client.getMoreTimelines(new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    Log.i(TAG, "loadNextDataFromApi" + response.toString());
                    int curSize = tweetList.size();
                    tweetList.addAll(Tweet.getTweets(response));
                    tweetAdp.notifyItemRangeInserted(curSize,tweetList.size() - 1);
                    fillDatabase(); //Since ID is unique, it will not duplicate
                    }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    Log.i(TAG, "loadNextDataFromApi onFailure" +errorResponse.toString() );
                    Toast.makeText(mCtx,"Cannot load more tweets",Toast.LENGTH_SHORT);
                }
            }, id);
        }
    }
    private void populateTimeline() {
        if(!isOnline()){
                Toast.makeText(mCtx,"Internet is not available", Toast.LENGTH_LONG).show();
                Log.i(TAG, " populateTimeline : Internet not available");
                floatTweet.setEnabled(false);
                tweetList.clear();
                tweetAdp.clear();
                tweetList.addAll(Tweet.loadRecentItemsfromDB());
                tweetAdp.notifyItemRangeInserted(0, tweetList.size() - 1);
                Log.i(TAG, " populateTimeline : size "+tweetList.size());

        }

        else
        {
            floatTweet.setEnabled(true);
            tweetList.clear();
            tweetAdp.clear();
            client.getTimelines(new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    Log.i(TAG, response.toString());
                    tweetList.addAll(Tweet.getTweets(response));
                    tweetAdp.notifyItemRangeInserted(0, tweetList.size() - 1);
                    fillDatabase();

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    Log.i(TAG, errorResponse.toString());
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.i(TAG, responseString);
                    throwable.printStackTrace();
                }
            });
        }
    }

    @Override
    public void postTweet(String status) {
        client.postTweet(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    Log.i(TAG,"postTweet : Onsuccess");
                    Tweet tweet = Tweet.fromJSON(response);
                    tweetList.add(0,tweet);
                    tweetAdp.notifyItemInserted(0);
                    rvTweet.scrollToPosition(0);
                    fillDatabase();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        },status);
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


    public void fillDatabase(){
        FlowManager.getDatabase(MyDatabase.class)
                .beginTransactionAsync(new ProcessModelTransaction.Builder<>(
                        new ProcessModelTransaction.ProcessModel<Tweet>() {
                            @Override
                            public void processModel(Tweet tweet, DatabaseWrapper wrapper) {
                                tweet.save();
                            }
                        }).addAll(tweetList).build())  // add elements (can also handle multiple)
                .error(new Transaction.Error() {
                    @Override
                    public void onError(Transaction transaction, Throwable error) {

                    }
                })
                .success(new Transaction.Success() {
                    @Override
                    public void onSuccess(Transaction transaction) {

                    }
                }).build().execute();
    }

}
