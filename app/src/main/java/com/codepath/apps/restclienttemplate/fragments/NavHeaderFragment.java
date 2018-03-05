package com.codepath.apps.restclienttemplate.fragments;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.activities.ProfileActivity;
import com.codepath.apps.restclienttemplate.activities.TimelineActivity;
import com.codepath.apps.restclienttemplate.databinding.FragmentNavHeaderBinding;
import com.codepath.apps.restclienttemplate.models.Friends;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.apps.restclienttemplate.utils.TwitterApplication;
import com.codepath.apps.restclienttemplate.utils.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;


public class NavHeaderFragment extends Fragment {

    public interface followListener{
        public void OnUserProfileLoaded();
        public void onFollowClicked(long ID);
        public void onFollowingClicked(long ID);
    }



    private TwitterClient client;
    public static final String TAG = "NavHeaderFragment";
    User mUser;
    Context mCtx;
    private static String SCREEN_NAME = "screen_name";
    private static String USER_TYPE = "user_type";
    private String username;
    static User currentUser;

    int usertype = 1;


    private FragmentNavHeaderBinding binding;
    CircleImageView image;
    TextView name;
    TextView screen_name;
    TextView following;
    TextView followers;

    ImageView imgLocation;
    TextView desc;
    TextView location;

    private static final int USER_CURRENT = 1;
    private static final int USER_OTHER = 2;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCtx = context;
    }

    //Current User
    public static NavHeaderFragment newInstance(@NonNull int userType) {
        NavHeaderFragment fragment = new NavHeaderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(USER_TYPE,userType);
        fragment.setArguments(bundle);
        return fragment;
    }

    //Another User
    public static NavHeaderFragment newInstance(@NonNull int userType, @NonNull User user) {
        NavHeaderFragment fragment = new NavHeaderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(USER_TYPE,userType);
        bundle.putParcelable(SCREEN_NAME,user);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TwitterApplication.getRestClient();

        if(getArguments()!=null) {
            if (getArguments().getInt(USER_TYPE) == USER_CURRENT) {
                getUserInformation();
                usertype = USER_CURRENT;
            }
            else if(getArguments().getInt(USER_TYPE) == USER_OTHER)
                usertype = USER_OTHER;
            }

        else
            getUserInformation();

    }


    public void loadOtherUserInformation(User user){
        loadViews(user);
    }


    public void loadViews(User user ) {
        Glide.with(mCtx).load(user.getProfile_imageURL()).into(image);
        name.setText(user.getName());
        screen_name.setText("@" + user.getScreen_name());
        username = user.getScreen_name();

        String normalText = " Following";
        String followersNormalText = " Followers";
        if (user.getFollowing() > 9999){
            String folboldText;
            int followingCount = user.getFollowing();
            if(followingCount>999999) {
                followingCount = followingCount / 1000000;
                folboldText = "" + followingCount+"M";
            }
            else {
                followingCount = followingCount / 1000;
                folboldText = "" + followingCount+"K";
            }


            SpannableStringBuilder str = new SpannableStringBuilder(folboldText + normalText);
            str.setSpan(new StyleSpan(Typeface.BOLD), 0, folboldText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            following.setText(str);
        }
        else{
            String folboldText = "" + user.getFollowing();
            SpannableStringBuilder str = new SpannableStringBuilder(folboldText + normalText);
            str.setSpan(new StyleSpan(Typeface.BOLD), 0, folboldText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            following.setText(str);
        }


        if(user.getFollowers()>9999){
            int followerCount = user.getFollowers();
            String followerBoldText;
            if(followerCount>999999) {
                followerCount = followerCount / 1000000;
                followerBoldText = followerCount+"M";
            }
            else{
                followerCount = followerCount / 1000;
                followerBoldText = followerCount+"K";
            }
            SpannableStringBuilder str1 = new SpannableStringBuilder(followerBoldText + followersNormalText);
            str1.setSpan(new StyleSpan(Typeface.BOLD), 0, followerBoldText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            followers.setText(str1);

        }

        else{
            String followerBoldText = "" + user.getFollowers();
            SpannableStringBuilder str1 = new SpannableStringBuilder(followerBoldText + followersNormalText);
            str1.setSpan(new StyleSpan(Typeface.BOLD), 0, followerBoldText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            followers.setText(str1);
        }


    }


    private void loadLocationAndDescriptionDetails(User user){
        if(mCtx instanceof TimelineActivity){
            desc.setVisibility(View.GONE);
            location.setVisibility(View.GONE);
            imgLocation.setVisibility(View.GONE);
        }
        else if(mCtx instanceof ProfileActivity){
            if(user.getDesc().isEmpty()) {
                desc.setVisibility(View.GONE);
            }
            else{
                desc.setVisibility(View.VISIBLE);
                desc.setText(user.getDesc());
            }
            if(user.getLocation().isEmpty()) {
                location.setVisibility(View.GONE);
                imgLocation.setVisibility(View.GONE);
            }
            else{
                location.setVisibility(View.VISIBLE);
                imgLocation.setVisibility(View.VISIBLE);
                location.setText(user.getLocation());
            }
        }
        else{
            desc.setVisibility(View.GONE);
            location.setVisibility(View.GONE);
            imgLocation.setVisibility(View.GONE);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_nav_header, container, false);

        View view = binding.getRoot();
        image = binding.nvProfileImage;
        name = binding.nvName;
        screen_name = binding.nvScreenName;
        following = binding.following;
        followers = binding.followers;

        imgLocation = binding.imageLoc;
        desc = binding.nvDescription;
        location = binding.nvLocation;

        if (usertype == 2)
        {
            mUser = getArguments().getParcelable(SCREEN_NAME);
            loadOtherUserInformation(mUser);
        }

        following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                followListener listener = (followListener) getActivity();
                if(usertype==USER_OTHER)
                    listener.onFollowingClicked(mUser.getUid());
                else
                    listener.onFollowingClicked(currentUser.getUid());
            }
        });

        followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                followListener listener = (followListener) getActivity();
                if(usertype==USER_OTHER)
                    listener.onFollowClicked(mUser.getUid());
                else
                    listener.onFollowClicked(currentUser.getUid());

            }
        });

        if(usertype==USER_OTHER)
            loadLocationAndDescriptionDetails(mUser);
        else
            loadLocationAndDescriptionDetails(currentUser);



        return view;



    }


    public void getUserInformation() {

        if (!TweetListFragment.isOnline()) {
            Log.i(TAG, " getUserInformation : Internet not available");
        }
        else
        {
            client.getUserInfo(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.i(TAG, response.toString());

                    try {
                        currentUser = User.fromJSON(response);
                        followListener listener = (followListener) getActivity();
                        listener.OnUserProfileLoaded();
                        loadViews(currentUser);


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

    public static User getCurrentUser(){
        return currentUser;
    }
}
