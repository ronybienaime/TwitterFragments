package com.codepath.apps.restclienttemplate.activities;

import android.databinding.DataBindingUtil;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.databinding.ActivityFriendsBinding;
import com.codepath.apps.restclienttemplate.databinding.FriendsListFragmentBinding;
import com.codepath.apps.restclienttemplate.fragments.FollowersFragment;
import com.codepath.apps.restclienttemplate.fragments.FollowingFragment;
import com.codepath.apps.restclienttemplate.models.Friends;

public class FriendsActivity extends AppCompatActivity {

    public static final int FOLLOW = 1;
    public static final int FOLLOWING = 2;
    public static final String FOLLOWITEM = "follow_item";
    public static final String USERID = "id";

    ActivityFriendsBinding binding;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_friends);
        toolbar = binding.appBar.toolbar;
        toolbar.setBackgroundColor(getResources().getColor(R.color.twitter_logo_blue));
        toolbar.setLogo(R.drawable.ic_twittericon);


        long ID = getIntent().getExtras().getLong(USERID);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if(getIntent().getExtras().getInt(FOLLOWITEM)==FOLLOW){
            toolbar.setTitle("   Followers");
            FollowersFragment frag = FollowersFragment.newInstance(ID);
            ft.replace(R.id.friendFrameContainer,frag).commit();

        }
        else if(getIntent().getExtras().getInt(FOLLOWITEM)==FOLLOWING){
            toolbar.setTitle("   Following");
            FollowingFragment frag = FollowingFragment.newInstance(ID);
            ft.replace(R.id.friendFrameContainer,frag).commit();

        }
        else
            toolbar.setTitle("   Followers");

        setSupportActionBar(toolbar);
    }
}
