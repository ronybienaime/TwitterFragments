package com.codepath.apps.restclienttemplate.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.restclienttemplate.fragments.DirectMessageFragment;
import com.codepath.apps.restclienttemplate.fragments.HomeTimelineFragment;
import com.codepath.apps.restclienttemplate.fragments.MentionsTimelineFragment;
import com.codepath.apps.restclienttemplate.fragments.SearchFragment;
import com.codepath.apps.restclienttemplate.fragments.TweetListFragment;



public class TimeLinePagerAdapter extends FragmentPagerAdapter {

    private SparseArray<Fragment> registeredFragments = new SparseArray<>();
    private final int NUM_OF_TABS = 4;

    Context mCtx;

    public TimeLinePagerAdapter(FragmentManager fm, Context ctx) {
        super(fm);
        mCtx = ctx;
    }

    @Override
    public Fragment getItem(int position) {
        if(position==0)
            return new HomeTimelineFragment();
        else if(position==1)
            return new SearchFragment();
        else if(position==2)
            return new MentionsTimelineFragment();
        else if(position==3)
            return new DirectMessageFragment();
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
