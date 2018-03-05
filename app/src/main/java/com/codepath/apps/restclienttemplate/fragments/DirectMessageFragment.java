package com.codepath.apps.restclienttemplate.fragments;


import android.content.Context;
import android.databinding.DataBindingUtil;
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
import com.codepath.apps.restclienttemplate.adapter.DirectMessageAdapter;
import com.codepath.apps.restclienttemplate.adapter.FriendsAdapter;
import com.codepath.apps.restclienttemplate.databinding.FragmentDirectMessageBinding;
import com.codepath.apps.restclienttemplate.databinding.FriendsListFragmentBinding;
import com.codepath.apps.restclienttemplate.models.DirectMessage;
import com.codepath.apps.restclienttemplate.models.Friends;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.utils.CheckOnline;
import com.codepath.apps.restclienttemplate.utils.EndlessRecyclerViewScrollListener;
import com.codepath.apps.restclienttemplate.utils.TwitterApplication;
import com.codepath.apps.restclienttemplate.utils.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;


public class DirectMessageFragment extends Fragment {

    FragmentDirectMessageBinding binding;


    public DirectMessageFragment() {
        // Required empty public constructor
    }

    public static final String TAG = "DirectMessageFragment";
    Context mCtx;
    private TwitterClient client;
    RecyclerView rvDirect;

    SwipeRefreshLayout swipeContainer;
    ArrayList<DirectMessage> messageList;
    DirectMessageAdapter msgAdp;
    EndlessRecyclerViewScrollListener scrollListener;
    LinearLayoutManager lm;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCtx = context;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TwitterApplication.getRestClient();
        populateDirectMessageList();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_direct_message,container,false);
        View view = binding.getRoot();

        rvDirect = binding.rvDirectMessage;
        swipeContainer = binding.swipeContainer;

        lm = new LinearLayoutManager(mCtx,LinearLayoutManager.VERTICAL,false);
        rvDirect.setLayoutManager(lm );
        messageList = new ArrayList<>();
        msgAdp = new DirectMessageAdapter(mCtx,messageList);
        rvDirect.setAdapter(msgAdp);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchDirectMessages();
            }
        });

        scrollListener = new EndlessRecyclerViewScrollListener(lm) {

            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.i(TAG, "loadNextDataFromApi HomeTimeLine page"+page);
                loadNextDataFromApi(page);

            }
        };

        rvDirect.addOnScrollListener(scrollListener);
        populateDirectMessageList();


        return view;
    }


    public void loadNextDataFromApi(int page) {

        if (!CheckOnline.isOnline()) {
            Log.i(TAG, " loadNextDataFromApi : Internet not available");
            Toast.makeText(mCtx,"Internet is not available", Toast.LENGTH_LONG).show();
        }
        else
        {


            Log.i(TAG, "loadNextDataFromApi page = " + page);

            long id = messageList.get(messageList.size()-1).getDirectMsgId();
            client.getMoreTimelines(new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    Log.i(TAG, "loadNextDataFromApi" + response.toString());
                    try {
                        addMoreItems(response);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    Log.i(TAG, "loadNextDataFromApi onFailure" +errorResponse.toString() );
                    Toast.makeText(mCtx,"Cannot load more Messages",Toast.LENGTH_SHORT).show();
                }
            }, id);
        }
    }

    private void fetchDirectMessages(){
        if(!CheckOnline.isOnline()){
            Log.i(TAG, " fetchHomeTimeLine : Internet not available");
            swipeContainer.setRefreshing(false);
            Toast.makeText(mCtx,"Internet is not available", Toast.LENGTH_LONG).show();

        }
        else{
            client.getDirectMessages(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    Log.i(TAG, response.toString());
                    try {
                        addMessageItems(response);
                        swipeContainer.setRefreshing(false);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    swipeContainer.setRefreshing(false);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    swipeContainer.setRefreshing(false);

                }

            });
        }


    }

    private void populateDirectMessageList() {
        client.getDirectMessages(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.i(TAG, response.toString());
                try {
                    addMessageItems(response);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.i(TAG, "populateDirectMessageList onFailure" +errorResponse.toString() );
                Toast.makeText(mCtx,"Cannot load more Messages",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.i(TAG, "populateDirectMessageList onFailure" + responseString );
                Toast.makeText(mCtx,"Cannot load more Messages",Toast.LENGTH_SHORT).show();

            }

        });
    }

    public void addMessageItems(JSONArray response) throws JSONException {
        messageList.clear();
        msgAdp.clear();
        messageList.addAll(DirectMessage.fromJSON(response));
        msgAdp.notifyItemRangeInserted(0, messageList.size() - 1);

    }

    public void addNewMessage(JSONObject response) throws JSONException {
        DirectMessage message = DirectMessage.fromJSON(response);
        messageList.add(0,message);
        msgAdp.notifyItemInserted(0);
        rvDirect.scrollToPosition(0);
    }


    public void addMoreItems(JSONArray response) throws JSONException{
        int curSize = messageList.size();
        messageList.addAll(DirectMessage.fromJSON(response));
        msgAdp.notifyItemRangeInserted(curSize,messageList.size() - 1);
    }



    public void postNewDirectMessage(String name, String message){

        if (!CheckOnline.isOnline()) {
            Toast.makeText(mCtx,"Internet not available. Cannot post", Toast.LENGTH_LONG).show();
        }
        else {

            client.postDirectMessage(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.i(TAG, "post Direct Message : Onsuccess");
                    try {
                        addNewMessage(response);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.i(TAG, errorResponse.toString());
                    Toast.makeText(mCtx, "Direct Message sending failed", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.i(TAG, responseString);
                    Toast.makeText(mCtx, "Direct Message sending failed", Toast.LENGTH_LONG).show();
                }
            }, name, message);
        }
    }


}
