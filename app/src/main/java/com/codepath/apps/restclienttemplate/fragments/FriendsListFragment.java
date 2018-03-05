package com.codepath.apps.restclienttemplate.fragments;

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

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.adapter.FriendsAdapter;
import com.codepath.apps.restclienttemplate.databinding.FriendsListFragmentBinding;
import com.codepath.apps.restclienttemplate.models.DirectMessage;
import com.codepath.apps.restclienttemplate.models.Friends;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.utils.EndlessRecyclerViewScrollListener;
import com.codepath.apps.restclienttemplate.utils.ItemClickSupport;
import com.codepath.apps.restclienttemplate.utils.TwitterClient;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;



public class FriendsListFragment extends Fragment {

    public static final String TAG = "FriendsListFragment";

    FriendsListFragmentBinding binding;
    RecyclerView rvFriends;
    SwipeRefreshLayout swipeContainer;
    ArrayList<Friends> friendList;
    FriendsAdapter friendAdp;
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
        binding = DataBindingUtil.inflate(inflater, R.layout.friends_list_fragment,container,false);
        View view = binding.getRoot();

        rvFriends = binding.rvfriends;
        swipeContainer = binding.swipeContainer;

        lm = new LinearLayoutManager(mCtx,LinearLayoutManager.VERTICAL,false);
        rvFriends.setLayoutManager(lm );
        friendList = new ArrayList<>();
        friendAdp = new FriendsAdapter(mCtx,friendList);
        rvFriends.setAdapter(friendAdp);
        dialog = new ProgressDialog(mCtx,R.style.TwitterDialogStyle);

        Log.i(TAG, "onCreateView TweetListFragment = ");
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.i(TAG, "onViewCreated page =TweetList ");
        ItemClickSupport.addTo(rvFriends).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                Friends friend = friendList.get(position);
            }
        });


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

    protected void prepareProgressDialog(){
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.progressdialog);
    }


    public void addFriendsItems(JSONArray response) throws JSONException {
        friendList.clear();
        friendAdp.clear();
        friendList.addAll(Friends.fromJSON(response));
        friendAdp.notifyItemRangeInserted(0, friendList.size() - 1);

    }

    public void addMoreFriendsItems(JSONArray response) throws JSONException{
        int curSize = friendList.size();
        friendList.addAll(Friends.fromJSON(response));
        friendAdp.notifyItemRangeInserted(curSize,friendList.size() - 1);
    }
}
