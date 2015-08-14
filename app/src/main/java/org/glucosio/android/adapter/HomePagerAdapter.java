package org.glucosio.android.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import org.glucosio.android.R;
import org.glucosio.android.fragment.HistoryFragment;
import org.glucosio.android.fragment.OverviewFragment;
import org.glucosio.android.fragment.TipsFragment;

/**
 * Created by paolo on 13/08/15.
 */
public class HomePagerAdapter extends FragmentPagerAdapter {

    Context mContext;

    public HomePagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new OverviewFragment();
            case 1:
                return new HistoryFragment();
            default:
                return new TipsFragment();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return mContext.getString(R.string.tab_overview);
            case 1:
                return mContext.getString(R.string.tab_history);
            default:
                return mContext.getString(R.string.tab_tips);
        }
    }
}