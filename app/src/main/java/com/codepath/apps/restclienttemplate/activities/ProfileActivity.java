package com.codepath.apps.restclienttemplate.activities;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcelable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.adapter.ProfilePagerAdapter;
import com.codepath.apps.restclienttemplate.adapter.TimeLinePagerAdapter;
import com.codepath.apps.restclienttemplate.databinding.ActivityProfileBinding;
import com.codepath.apps.restclienttemplate.fragments.NavHeaderFragment;
import com.codepath.apps.restclienttemplate.fragments.ReplyFragment;
import com.codepath.apps.restclienttemplate.fragments.TweetListFragment;
import com.codepath.apps.restclienttemplate.fragments.UserTimelineFragment;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;

import org.parceler.Parcel;

public class ProfileActivity extends AppCompatActivity implements NavHeaderFragment.followListener, TweetListFragment.ProfileLoadListener, ReplyFragment.ReplyTweetListener {

    private static final String USER = "currentUser";
    private static final String USER_TYPE = "user_type";
    public static final String FOLLOWITEM = "follow_item";
    public static final String USERID = "id";

    User user;
    int userType = 1; // By default user profile

    Context context;
    TabLayout tab;
    ViewPager vpager;
    RelativeLayout frame;

    ProfilePagerAdapter pagerAdp;
    private ActivityProfileBinding binding;


    @Override
    public void onFollowClicked(long ID) {
        Intent intent = new Intent();
        intent.putExtra(FOLLOWITEM,1);
        intent.putExtra(USERID,ID);
        intent.setClass(this,FriendsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onFollowingClicked(long ID) {
        Intent intent = new Intent();
        intent.putExtra(FOLLOWITEM,2);
        intent.putExtra(USERID,ID);
        intent.setClass(this,FriendsActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile);

        frame = binding.backgroundProfile;
        context = ProfileActivity.this;
        Bundle bundle = getIntent().getBundleExtra("mybundle");
        user = bundle.getParcelable(USER);
            if(getIntent().hasExtra(USER_TYPE))
                userType = getIntent().getExtras().getInt(USER_TYPE);

        if(!user.getProfile_background_url().isEmpty()){
            Glide.with(this).load(user.getProfile_background_url()).asBitmap().into(new SimpleTarget<Bitmap>(200, 200) {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    Drawable drawable = new BitmapDrawable(resource);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        frame.setBackground(drawable);
                    }
                }
            });
        }

        NavHeaderFragment userinfoFrag = NavHeaderFragment.newInstance(userType,user);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frame1,userinfoFrag);
        ft.commit();


        tab = binding.profileTabs;
        vpager = binding.profileViewpager;
        pagerAdp = new ProfilePagerAdapter(getSupportFragmentManager(),this,user.getScreen_name());
        vpager.setAdapter(pagerAdp);
        tab.setupWithViewPager(vpager);
        vpager.setOffscreenPageLimit(1); // to set pager pages keeping alive
        createCustomTabs();

    }

    public void createCustomTabs(){
        tab.getTabAt(0).setText("TWEETS");
        tab.getTabAt(1).setText("FAVORITES");

        tab.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }


    @Override
    public void onProfileLoad(User user) {
        //Do nothing
    }

    @Override
    public void onReply(Tweet tweet) {
        ReplyFragment reply = new ReplyFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("Tweet",tweet);
        reply.setArguments(bundle);
        reply.show(getSupportFragmentManager(),"reply_frag");
    }

    @Override
    public void onReplyTweet() {
        Toast.makeText(context,"Tweet Replied",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void OnUserProfileLoaded() {
        // Don't do anything
    }
}
