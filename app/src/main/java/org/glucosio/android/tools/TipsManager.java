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

package org.glucosio.android.tools;

import android.content.Context;

import org.glucosio.android.R;

import java.util.ArrayList;
import java.util.Collections;

public class TipsManager {
    private Context mContext;
    private int userAge;

    public TipsManager(Context mContext, int age) {
        this.mContext = mContext;
        this.userAge = age;
    }


    public ArrayList<String> getTips() {
        ArrayList<String> finalTips = new ArrayList<>();
        String[] allTips = mContext.getResources().getStringArray(R.array.tips_all);
        String[] plus40Tips = mContext.getResources().getStringArray(R.array.tips_all_age_plus_40);
        if (userAge >= 40) {
            Collections.addAll(finalTips, allTips);
            Collections.addAll(finalTips, plus40Tips);
        } else {
            Collections.addAll(finalTips, allTips);
        }
        return finalTips;
    }
}
