package com.codepath.apps.restclienttemplate.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.apps.restclienttemplate.utils.EndlessRecyclerViewScrollListener;
import com.codepath.apps.restclienttemplate.utils.TwitterApplication;
import com.codepath.apps.restclienttemplate.utils.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;



public class MentionsTimelineFragment extends TweetListFragment {


    public static final String TAG = "MentionsFragment";
    private TwitterClient client;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TwitterApplication.getRestClient();


    }


    public void LoadMore(int page){
        Log.i(TAG,"OnLoad MentionTime");
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {



        return super.onCreateView(inflater, container, savedInstanceState);


    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dialog.show();
        prepareProgressDialog();
        scrollListener = new EndlessRecyclerViewScrollListener(lm) {

            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.i(TAG, "loadNextDataFromApi MentionTimeLine page"+page);
                loadNextDataFromApi(page);

            }
        };

        rvTweet.addOnScrollListener(scrollListener);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchMentionsTimeLine();
            }
        });

        populateMentionsTimeline();

    }


    private void fetchMentionsTimeLine(){

        if(!isOnline()){
            Log.i(TAG, " fetchMentionsTimeLine : Internet not available");
            swipeContainer.setRefreshing(false);
            Toast.makeText(mCtx,"Internet is not available", Toast.LENGTH_LONG).show();

        }
        else {
            client.getMentionsTimelines(new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    Log.i(TAG, response.toString());
                    addItemResponse(response);
                    /// Delete.table(Tweet.class);
                    //Delete.table(User.class);
                    // Delete.table(Entities.class);
                    // Delete.table(Media.class);

                    swipeContainer.setRefreshing(false);
                    //  fillDatabase();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    Log.i(TAG, "fetchMentionsTimeLine Error : " + errorResponse.toString());
                    swipeContainer.setRefreshing(false);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.i(TAG, "fetchMentionsTimeLine Error : " + responseString);
                    throwable.printStackTrace();
                    swipeContainer.setRefreshing(false);
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

            long id = getLastId();
            if(id==-1)
                return;
            client.getMoreMentionsTimelines(new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    Log.i(TAG, "loadNextDataFromApi" + response.toString());
                    addMoreItems(response);

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    Log.i(TAG, "loadNextDataFromApi onFailure" +errorResponse.toString() );
                    Toast.makeText(mCtx,"Cannot load more tweets",Toast.LENGTH_SHORT).show();
                }
            }, id);
        }
    }


    private void populateMentionsTimeline() {
        if(!isOnline()){
            Toast.makeText(mCtx,"Internet is not available", Toast.LENGTH_LONG).show();
            Log.i(TAG, " Internet not available");
            Log.i(TAG, "size "+tweetList.size());

        }

        else
        {
            client.getMentionsTimelines(new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    Log.i(TAG, response.toString());
                    addItemResponse(response);
                    dialog.dismiss();

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    Log.i(TAG, errorResponse.toString());
                    dialog.dismiss();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.i(TAG, responseString);
                    throwable.printStackTrace();
                    dialog.dismiss();
                }
            });
        }
    }


    @Override
    public void onRetweetClicked(Tweet tweet, final int position) {
        client.postReTweet(new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.i(TAG, response.toString());
                Log.i(TAG, "Retweet success");
                updateRetweetItemResponse(position);
                Toast.makeText(mCtx,"Retweet done",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.i(TAG, errorResponse.toString());
                Toast.makeText(mCtx,"You have already retweeted this tweet.",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.i(TAG, responseString);
                throwable.printStackTrace();
            }
        },tweet.gettweet_id());

    }

    @Override
    public void onFavClicked(Tweet tweet, int position) {
        Log.i(TAG,"Fav clicked "+tweet.getUser().getScreen_name());
        if(tweet.isFavorited()) {// it's already favorite, now remove the favorite : toggle it
            removeFavorite(tweet, position);
        }

        else{
            createFavorite(tweet, position);
        }

    }

    private void removeFavorite(Tweet tweet, final int position){
        client.postDestroyFavorite(new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.i(TAG, response.toString());
                Log.i(TAG, "Remove Favorite success");
                updateFavItemResponse(position,false);
                Toast.makeText(mCtx,"Remove Favorite success",Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.i(TAG, errorResponse.toString());
                Toast.makeText(mCtx,"Remove favorite failed",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.i(TAG, responseString);
                throwable.printStackTrace();
            }
        },tweet.gettweet_id());
    }

    private void createFavorite(Tweet tweet, final int position){

        client.postCreateFavorite(new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.i(TAG, response.toString());
                Log.i(TAG, "Add favorite success");
                updateFavItemResponse(position,true);
                Toast.makeText(mCtx,"Add favorite done",Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.i(TAG, errorResponse.toString());
                Toast.makeText(mCtx,"Add favorite failed",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.i(TAG, responseString);
                throwable.printStackTrace();
            }
        },tweet.gettweet_id());
    }

    @Override
    public void onUserNameClicked(String userName) {
        client.getOtherUserInfo(new JsonHttpResponseHandler() {
                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                        try {
                                            Log.i(TAG, response.toString());
                                            User user = getUserDetails(response);
                                            ProfileLoadListener listener = (ProfileLoadListener) getActivity();
                                            listener.onProfileLoad(user);

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                        Log.i(TAG, errorResponse.toString());
                                        Toast.makeText(mCtx,"Unable to get user details",Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                        Log.i(TAG, responseString);
                                        Toast.makeText(mCtx,"Unable to get user details",Toast.LENGTH_SHORT).show();
                                    }
                                }
                ,userName);
    }
}
