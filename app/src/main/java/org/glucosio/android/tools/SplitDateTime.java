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

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.DateFormat;
import java.util.Date;

public class SplitDateTime {

    private DateTime origDateTime;
    private DateFormat inputFormat;

    public SplitDateTime(Date origDatetime) {
        this.origDateTime = new DateTime(origDatetime);
    }

    public String getHour() {
        DateTimeFormatter houreFormatter = DateTimeFormat.forPattern("HH");
        return houreFormatter.print(origDateTime);
    }

    public String getMinute() {
        DateTimeFormatter minuteFormatter = DateTimeFormat.forPattern("mm");
        return minuteFormatter.print(origDateTime);
    }

    public String getYear() {
        DateTimeFormatter yearFormatter =  DateTimeFormat.forPattern("yyyy");
        return yearFormatter.print(origDateTime);
    }

    public String getMonth() {
        DateTimeFormatter mouthFormatter = DateTimeFormat.forPattern("MM");
        return mouthFormatter.print(origDateTime);
    }

    public String getDay() {
        DateTimeFormatter dayFormatter = DateTimeFormat.forPattern("dd");
        return dayFormatter.print(origDateTime);
    }
}
