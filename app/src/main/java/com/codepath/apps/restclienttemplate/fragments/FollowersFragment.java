package com.codepath.apps.restclienttemplate.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.utils.CheckOnline;
import com.codepath.apps.restclienttemplate.utils.EndlessRecyclerViewScrollListener;
import com.codepath.apps.restclienttemplate.utils.TwitterApplication;
import com.codepath.apps.restclienttemplate.utils.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;



public class FollowersFragment extends FriendsListFragment {

    public static final String USERID = "user_id";
    public static final String TAG = "FollowersFragment";
    long cursor;

    Long usrID;

    public static FollowersFragment newInstance(@NonNull long userID) {
        FollowersFragment fragment = new FollowersFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(USERID,userID);
        fragment.setArguments(bundle);
        return fragment;
    }

    private TwitterClient client;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TwitterApplication.getRestClient();
        usrID = getArguments().getLong(USERID);
        populateFollowersList();
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
                Log.i(TAG, "loadNextDataFromApi HomeTimeLine page"+page);
                loadNextDataFromApi(page);

            }
        };

        rvFriends.addOnScrollListener(scrollListener);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchFollowers();
            }
        });


    }


    private void fetchFollowers(){


        if(!isOnline()){
            Log.i(TAG, " fetchHomeTimeLine : Internet not available");
            swipeContainer.setRefreshing(false);
            Toast.makeText(mCtx,"Internet is not available", Toast.LENGTH_LONG).show();

        }
        else {
            client.getfollowersList(new JsonHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.i(TAG,response.toString());
                    try {
                        addFriendsItems(response.getJSONArray("users"));
                        cursor = response.getLong("next_cursor");
                        swipeContainer.setRefreshing(false);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    swipeContainer.setRefreshing(false);
                    Toast.makeText(mCtx,"Failed to fetch Followers List",Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    swipeContainer.setRefreshing(false);
                    Toast.makeText(mCtx,"Failed to fetch Followers List",Toast.LENGTH_SHORT).show();
                }
            },usrID);
        }
    }


    public void loadNextDataFromApi(int page) {
        if (!isOnline()) {
            Log.i(TAG, " loadNextDataFromApi : Internet not available");
            //Ask : Check if we need to load more tweets from database
        }

        else if(cursor!=0){
            client.getMorefollowersList(new JsonHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.i(TAG,response.toString());
                    try {
                        addMoreFriendsItems(response.getJSONArray("users"));
                        cursor = response.getLong("next_cursor");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    Toast.makeText(mCtx,"Fetching more followers failed",Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    Toast.makeText(mCtx,"Fetching more followers failed",Toast.LENGTH_SHORT).show();
                }
            },usrID,cursor);
        }
        else{
            Toast.makeText(mCtx,"End of Followers List",Toast.LENGTH_SHORT).show();
        }
    }

    private void populateFollowersList(){
        if (!CheckOnline.isOnline()) {
            Toast.makeText(mCtx,"Internet not available. Cannot load followers list ", Toast.LENGTH_LONG).show();
        }
        else {
            client.getfollowersList(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.i(TAG, response.toString());
                    try {
                        addFriendsItems(response.getJSONArray("users"));
                        cursor = response.getLong("next_cursor");
                        dialog.dismiss();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    Toast.makeText(mCtx, "Failed to fetch Followers List", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    Toast.makeText(mCtx, "Failed to fetch Followers List", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }, usrID);
        }
    }


}
