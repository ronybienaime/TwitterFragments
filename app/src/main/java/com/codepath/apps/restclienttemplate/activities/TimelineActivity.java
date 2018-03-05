package com.codepath.apps.restclienttemplate.activities;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.adapter.TimeLinePagerAdapter;
import com.codepath.apps.restclienttemplate.fragments.DirectMessageFragment;
import com.codepath.apps.restclienttemplate.fragments.HomeTimelineFragment;
import com.codepath.apps.restclienttemplate.fragments.NavHeaderFragment;
import com.codepath.apps.restclienttemplate.fragments.NewDirectMessageFragment;
import com.codepath.apps.restclienttemplate.fragments.NewTweetFragment;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.fragments.ReplyFragment;
import com.codepath.apps.restclienttemplate.fragments.TweetListFragment;
import com.codepath.apps.restclienttemplate.databinding.ActivityTimelineBinding;
import com.codepath.apps.restclienttemplate.models.Friends;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class TimelineActivity extends AppCompatActivity implements NewTweetFragment.postTweetListener, TweetListFragment.ProfileLoadListener,NavHeaderFragment.followListener,
        ReplyFragment.ReplyTweetListener,TweetListFragment.tweetPostedListener, NewDirectMessageFragment.postDirectMessageListener {
    public static final String TAG = "TimelineActivity";
    public static final String FOLLOWITEM = "follow_item";
    public static final String USERID = "id";
    public static final String SEARCH = "SEARCH";
    public static final String NEWTWEET = "NEWTWEET";

    String user_name;
    String screen_name;
    String my_profile_image;

    Context mCtx;
    FloatingActionButton floatTweet;
    private ActivityTimelineBinding binding;
    Toolbar toolbar;
    TabLayout tab;
    ViewPager vpager;

    int previous_tab_position;

    FragmentManager fm;
    TimeLinePagerAdapter pagerAdp;
    DrawerLayout drawer;
    NavigationView navigateView;
    private String profile_url;

    int[] iconIntArray = {R.drawable.ic_twitter_newtweet,R.drawable.ic_twitter_newmessage};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_timeline);
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        if(intent.hasExtra("Profile_url"))
            profile_url= intent.getStringExtra("Profile_url");
        loadViews();
        toolbar.setTitle("      Home");
        setSupportActionBar(toolbar);
        previous_tab_position = 0;
        createCustomTabs();
        setupDrawerContent(navigateView);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(Gravity.LEFT);
            }
        });

        if(SEARCH.equals(action)){
            vpager.setCurrentItem(1);
        }
        else if(NEWTWEET.equals(action)){
            launchNewTweet();
        }
        else if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                // Make sure to check whether returned data will be null.
                String titleOfPage = intent.getStringExtra(Intent.EXTRA_SUBJECT);
                String urlOfPage = intent.getStringExtra(Intent.EXTRA_TEXT);
                getUserInformationAndLaunchNewTweet(titleOfPage,urlOfPage);
                Log.i(TAG,"Received Intent Twitter "+titleOfPage);
            }
        }

        setLogofromprofileUrl();
    }

    private void loadViews(){
        toolbar = binding.toolbar;
        tab = binding.slidingTabs;
        vpager = binding.viewpager;
        pagerAdp = new TimeLinePagerAdapter(getSupportFragmentManager(),this);
        vpager.setAdapter(pagerAdp);
        tab.setupWithViewPager(vpager);
        vpager.setOffscreenPageLimit(2); // to set pager pages keeping alive
        drawer = binding.drawerLayout;
        navigateView = binding.nvView;
        mCtx = TimelineActivity.this;
        fm = getSupportFragmentManager();
        floatTweet = binding.ftTweet;
        floatTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchNewTweet();
            }
        });
    }


    protected void animateFab(final int position) {
        floatTweet.clearAnimation();
        // Scale down animation
        ScaleAnimation shrink = new ScaleAnimation(1f, 0.2f, 1f, 0.2f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        shrink.setDuration(150);     // animation duration in milliseconds
        shrink.setInterpolator(new DecelerateInterpolator());
        shrink.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Change FAB icon
                floatTweet.setImageDrawable(getResources().getDrawable(iconIntArray[position], null));

                // Scale up animation
                ScaleAnimation expand = new ScaleAnimation(0.2f, 1f, 0.2f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                expand.setDuration(100);     // animation duration in milliseconds
                expand.setInterpolator(new AccelerateInterpolator());
                floatTweet.startAnimation(expand);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        floatTweet.startAnimation(shrink);
    }

    private void setLogofromprofileUrl(){
        if(profile_url!=null){
            Log.i(TAG, "profile_url is not null");
            Target target = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    Log.i(TAG, "onBitmapLoaded");
                    Bitmap b = Bitmap.createScaledBitmap(bitmap, 120, 120, false);
                    BitmapDrawable icon = new BitmapDrawable(toolbar.getResources(), b);
                    toolbar.setNavigationIcon(icon);
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                    Log.i(TAG, "onBitmapFailed");
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                    Log.i(TAG, "onPrepareLoad");
                }

            };

            Picasso.with(toolbar.getContext()).load(profile_url).into(target);
        }
        else {
            Log.i(TAG, "profile_url is null");
            toolbar.setNavigationIcon(R.drawable.ic_twittericon);
        }
    }


    private void setLogo(){

        if(NavHeaderFragment.getCurrentUser()!=null)
        {
            Log.i(TAG, "Current user is not null");
            Target target = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    Log.i(TAG, "onBitmapLoaded");
                    Bitmap b = Bitmap.createScaledBitmap(bitmap, 120, 120, false);
                    BitmapDrawable icon = new BitmapDrawable(toolbar.getResources(), b);
                    toolbar.setNavigationIcon(icon);
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                    Log.i(TAG, "onBitmapFailed");
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                    Log.i(TAG, "onPrepareLoad");
                }

            };

            Picasso.with(toolbar.getContext()).load(NavHeaderFragment.getCurrentUser().getProfile_imageURL()).into(target);
        }
        else {
            Log.i(TAG, "Current user is null");
            toolbar.setNavigationIcon(R.drawable.ic_twittericon);
        }


    }


    public void createCustomTabs(){
        tab.getTabAt(0).setIcon(R.drawable.ic_twitter_home);
        tab.getTabAt(1).setIcon(R.drawable.ic_twitter_search);
        tab.getTabAt(2).setIcon(R.drawable.ic_mention);
        tab.getTabAt(3).setIcon(R.drawable.ic_twitter_message);

        tab.getTabAt(tab.getSelectedTabPosition()).getIcon().setColorFilter(getResources().getColor(R.color.twitter_logo_blue), PorterDuff.Mode.SRC_IN);

        tab.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {


                tab.getIcon().setColorFilter(getResources().getColor(R.color.twitter_logo_blue), PorterDuff.Mode.SRC_IN);
                switch(tab.getPosition()){
                    case 0 : toolbar.setTitle("      Home");
                            break;
                    case 1 : toolbar.setTitle("");
                        break;
                    case 2 : toolbar.setTitle("      Mentions");
                        break;
                    case 3 : toolbar.setTitle("      Messages");
                        break;
                    default : toolbar.setTitle("      Home");
                }

                if(previous_tab_position==3 && tab.getPosition()!=3){
                    previous_tab_position = tab.getPosition();
                    animateFab(0);
                }
                else if (previous_tab_position!=3 &&  tab.getPosition()==3){
                    previous_tab_position = tab.getPosition();
                    animateFab(1);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }




    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        selectDrawerItem(menuItem);
                        return false;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {

        switch(menuItem.getItemId()) {
            case R.id.profile_fragment:
                menuItem.setChecked(false);
                Intent intent = new Intent();
                intent.setClass(this,ProfileActivity.class);
                Bundle bundle = new Bundle();
                User user = NavHeaderFragment.getCurrentUser();
                bundle.putParcelable("currentUser",user);
                intent.putExtra("mybundle",bundle);
                startActivity(intent);
                break;
           /* case R.id.nav_second_fragment:
                fragmentClass = SecondFragment.class;
                break;
            case R.id.nav_third_fragment:
                fragmentClass = ThirdFragment.class;
                break; */
            default:
                Intent intent1 = new Intent();
                intent1.setClass(this,ProfileActivity.class);
                startActivity(intent1);
        }

        try {

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Close the navigation drawer

        drawer.closeDrawers();
    }


    @Override
    public void onProfileLoad(User user) {

        Log.i(TAG,"tweet username ="+user.getScreen_name());
        Intent intent = new Intent();
        intent.setClass(this,ProfileActivity.class);

        Bundle bundle = new Bundle();
        bundle.putParcelable("currentUser",user);
        intent.putExtra("mybundle",bundle);
        intent.putExtra("user_type",2);
        startActivity(intent);
    }

    @Override
    public void onReply(Tweet tweet) {
        ReplyFragment reply = new ReplyFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("Tweet",tweet);
        reply.setArguments(bundle);
        reply.show(fm,"reply_frag");
    }




    @Override
    public void onReplyTweet() {
        Toast.makeText(mCtx,"Tweet Replied",Toast.LENGTH_SHORT).show();
    }



    private void launchNewTweet(){

        if(tab.getSelectedTabPosition()==3){
            NewDirectMessageFragment msgfrag = new NewDirectMessageFragment();
            msgfrag.show(fm, "newmessage");
        }
        else {

            NewTweetFragment tweetfrag = new NewTweetFragment();
            Bundle bundle = new Bundle();
            User currentUser = NavHeaderFragment.getCurrentUser();
            if (currentUser != null) {
                bundle.putString("name", currentUser.getName());
                bundle.putString("screen_name", currentUser.getScreen_name());
                bundle.putString("my_profile_image", currentUser.getProfile_imageURL());
                tweetfrag.setArguments(bundle);
                tweetfrag.show(fm, "newTweet");
            }
        }


    }


    private void getUserInformationAndLaunchNewTweet(final String titleOfPage,final String urlOfPage) {

        NewTweetFragment tweetfrag = new NewTweetFragment();
        Bundle bundle = new Bundle();
        User currentUser = NavHeaderFragment.getCurrentUser();
        if (currentUser != null) {
            bundle.putString("name", currentUser.getName());
            bundle.putString("screen_name", currentUser.getScreen_name());
            bundle.putString("my_profile_image", currentUser.getProfile_imageURL());
            bundle.putString("intent_title", titleOfPage);
            bundle.putString("intent_url", urlOfPage);
            tweetfrag.setArguments(bundle);
            tweetfrag.show(fm, "newTweet");
            Log.i(TAG, "Received Intent Twitter " + titleOfPage);
        }
    }



    @Override
    public void postTweet(String status) {
        //vpager.setCurrentItem(0,true);
        HomeTimelineFragment frag = (HomeTimelineFragment)pagerAdp.getRegisteredFragment(0);
        Log.i(TAG, "Tweet ="+status);
        frag.UpdateNewTweet(status);

    }

    @Override
    public void OnUserProfileLoaded() {
        setLogo();
    }

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            pagerAdp.getRegisteredFragment(tab.getSelectedTabPosition()).onActivityResult(requestCode,resultCode,data);
    }

    @Override
    public void onTweetPosted() {
        vpager.setCurrentItem(0,true);
    }

    @Override
    public void postDirectMessage(String name, String message) {
        DirectMessageFragment frag = (DirectMessageFragment)pagerAdp.getRegisteredFragment(3);
        Log.i(TAG, "Message ="+name+" "+message);
        frag.postNewDirectMessage(name, message);
    }
}
