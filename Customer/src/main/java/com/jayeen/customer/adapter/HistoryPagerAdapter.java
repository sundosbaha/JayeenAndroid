package com.jayeen.customer.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.jayeen.customer.HistoryActivityNew;
import com.jayeen.customer.R;
import com.jayeen.customer.fragments.UberHistoryFragment;
import com.jayeen.customer.fragments.UberScheduleFragment;

import java.util.ArrayList;


public class HistoryPagerAdapter extends FragmentStatePagerAdapter {

    //integer to count number of tabs
    int tabCount;
    Context context;

    ArrayList<String> tabTitles;

    public HistoryPagerAdapter(FragmentManager fm, int tabCount, Context con, ArrayList<String> tabTitles) {
        super(fm);
        context = con;
        //Initializing tab count
        this.tabCount = tabCount;
        this.tabTitles =tabTitles;
    }


    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles.get(position);
    }

    //Overriding method getItem
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return UberHistoryFragment.newInstance(position);
            case 1:
                return UberScheduleFragment.newInstance(position);
            default:
                return null;
        }
    }

    //Overriden method getCount to get the number of tabs
    @Override
    public int getCount() {
        return tabCount;
    }
}