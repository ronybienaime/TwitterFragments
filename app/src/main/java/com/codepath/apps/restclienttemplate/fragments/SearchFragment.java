package com.codepath.apps.restclienttemplate.fragments;


import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.activities.TimelineActivity;
import com.codepath.apps.restclienttemplate.utils.EndlessRecyclerViewScrollListener;
import com.codepath.apps.restclienttemplate.utils.TwitterApplication;
import com.codepath.apps.restclienttemplate.utils.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


public class SearchFragment extends TweetListFragment{

    public static final String TAG = "SearchFragment";
    private TwitterClient client;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TwitterApplication.getRestClient();

       // populateTimeline();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return super.onCreateView(inflater, container, savedInstanceState);


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_search, menu);

    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        MenuItem searchViewMenuItem = menu.findItem(R.id.action_search);
        SearchView  sv = (SearchView) searchViewMenuItem.getActionView();
        int searchImgId = android.support.v7.appcompat.R.id.search_button;
        ImageView v = (ImageView) sv.findViewById(searchImgId);
        v.setImageResource(R.drawable.button);

        EditText searchBox=((EditText) sv.findViewById (android.support.v7.appcompat.R.id.search_src_text));
        searchBox.setHint("Search Twitter");


        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchQuery(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                System.out.println("tap");
                return false;
            }
        });
    }


    private void searchQuery(String query){
        dialog.setMessage("");
        prepareProgressDialog();
        client.getSearchResult(new JsonHttpResponseHandler(){
                                   @Override
                                   public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                       Log.i(TAG, response.toString());
                                       try {
                                           addItemResponse(response.getJSONArray("statuses"));
                                            dialog.dismiss();
                                       } catch (JSONException e) {
                                           e.printStackTrace();
                                       }
                                   }

                                   @Override
                                   public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                       Log.i(TAG, errorResponse.toString());
                                       dialog.dismiss();
                                       Toast.makeText(mCtx,"Couldn't find anything",Toast.LENGTH_SHORT).show();

                                   }

                                   @Override
                                   public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                       Log.i(TAG, responseString);
                                       dialog.dismiss();
                                   }
                               }
                ,query);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        scrollListener = new EndlessRecyclerViewScrollListener(lm) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.i(TAG, "loadNextDataFromApi HomeTimeLine page"+page);
               // loadNextDataFromApi(page);

            }
        };

        rvTweet.addOnScrollListener(scrollListener);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //fetchHomeTimeLine();
            }
        });

        //populateTimeline();
    }

}
