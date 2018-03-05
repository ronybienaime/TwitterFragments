package com.codepath.apps.restclienttemplate.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.codepath.apps.restclienttemplate.fragments.DirectMessageFragment;
import com.codepath.apps.restclienttemplate.fragments.FavoriteListFragment;
import com.codepath.apps.restclienttemplate.fragments.HomeTimelineFragment;
import com.codepath.apps.restclienttemplate.fragments.MentionsTimelineFragment;
import com.codepath.apps.restclienttemplate.fragments.SearchFragment;
import com.codepath.apps.restclienttemplate.fragments.UserTimelineFragment;



public class ProfilePagerAdapter extends FragmentPagerAdapter {

    private SparseArray<Fragment> registeredFragments = new SparseArray<>();
    private final int NUM_OF_TABS = 2;

    Context mCtx;
    String screen_name;

    public ProfilePagerAdapter(FragmentManager fm, Context ctx, String name) {
        super(fm);
        mCtx = ctx;
        screen_name = name;
    }

    @Override
    public Fragment getItem(int position) {
        if(position==0) {
            UserTimelineFragment frag = UserTimelineFragment.newInstance(screen_name);
            return frag;
        }
        else if(position==1){
            FavoriteListFragment frag = FavoriteListFragment.newInstance(screen_name);
            return frag;
        }

        else{
            return null;
        }
    }

    @Override
    public int getCount() {
        return NUM_OF_TABS;
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }
}
