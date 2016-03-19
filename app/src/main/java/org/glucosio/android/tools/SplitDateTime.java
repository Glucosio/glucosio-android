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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SplitDateTime {

    private Date origDateTime; // Example "yyyy-MM-dd HH:mm"
    private DateFormat inputFormat;

    public SplitDateTime(Date origDatetime, DateFormat origDateFormat) {
        this.origDateTime = origDatetime;
        this.inputFormat = origDateFormat;
    }

    public String getHour() {
        DateFormat finalFormat = new SimpleDateFormat("HH");
        return finalFormat.format(origDateTime);
    }

    public String getMinute() {
        DateFormat finalFormat = new SimpleDateFormat("mm");
        return finalFormat.format(origDateTime);
    }

    public String getYear() {
        DateFormat finalFormat = new SimpleDateFormat("yyyy");
        return finalFormat.format(origDateTime);
    }

    public String getMonth() {
        DateFormat finalFormat = new SimpleDateFormat("MM");
        return finalFormat.format(origDateTime);
    }

    public String getDay() {
        DateFormat finalFormat = new SimpleDateFormat("dd");
        return finalFormat.format(origDateTime);
    }
}
