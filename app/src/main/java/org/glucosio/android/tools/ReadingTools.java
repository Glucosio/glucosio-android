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

public class ReadingTools {

    public ReadingTools() {
    }

    public int hourToSpinnerType(int hour) {

        if (hour > 23) {
            return 8;  //night
        } else if (hour > 20) {
            return 5; //after dinner
        } else if (hour > 17) {
            return 4; // before dinner
        } else if (hour > 13) {
            return 3; // after lunch
        } else if (hour > 11) {
            return 2; // before lunch
        } else if (hour > 7) {
            return 1; //after breakfast
        } else if (hour > 4) {
            return 0; // before breakfast
        } else {
            return 8; // night time
        }
    }
}
