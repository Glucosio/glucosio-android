/*
 * Copyright (C) 2016 Glucosio Foundation
 *
 * This file is part of Glucosio.
 *
 * Glucosio is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * Glucosio is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Glucosio.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

package org.glucosio.android.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import org.glucosio.android.R;
import org.glucosio.android.fragment.AssistantFragment;
import org.glucosio.android.fragment.HistoryFragment;
import org.glucosio.android.fragment.OverviewFragment;

public class HomePagerAdapter extends FragmentPagerAdapter {

    private Context mContext;
    private OverviewFragment overviewFragment;
    private HistoryFragment historyFragment;
    private AssistantFragment assistantFragment;


    public HomePagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.mContext = context;
        overviewFragment = OverviewFragment.newInstance();
        historyFragment = HistoryFragment.newInstance();
        assistantFragment = new AssistantFragment();
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return overviewFragment;
            case 1:
                return historyFragment;
            default:
                return assistantFragment;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    // Workaround to refresh views with notifyDataSetChanged()
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return mContext.getString(R.string.tab_overview);
            case 1:
                return mContext.getString(R.string.tab_history);
            default:
                return mContext.getString(R.string.assistant);
        }
    }


    public OverviewFragment getOverviewFragment() {
        return overviewFragment;
    }

    public void setOverviewFragment(OverviewFragment overviewFragment) {
        this.overviewFragment = overviewFragment;
    }

    public HistoryFragment getHistoryFragment() {
        return historyFragment;
    }

    public void setHistoryFragment(HistoryFragment historyFragment) {
        this.historyFragment = historyFragment;
    }

    public AssistantFragment getAssistantFragment() {
        return assistantFragment;
    }

    public void setAssistantFragment(AssistantFragment assistantFragment) {
        this.assistantFragment = assistantFragment;
    }
}