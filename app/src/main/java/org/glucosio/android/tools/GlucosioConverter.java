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

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class GlucosioConverter {
    private GlucosioConverter(){}

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static int glucoseToMgDl(double mmolL) {
        double converted = mmolL * 18;
        return (int) converted;
    }

    public static double glucoseToMmolL(double mgDl) {
        return round(mgDl / 18.0, 1);
    }

    public static double glucoseToA1C(double mgDl) {
        // A1C = (Average glucose + 46.7) / 28.7
        return round((mgDl + 46.7) / 28.7, 2);
    }

    public static double a1cToGlucose(double a1c) {
        // Average glucose = (A1C * 28.7) -46.7
        return round((a1c * 28.7) - 46.7, 2);
    }

    public static int kgToLb(int kg) {
        Double d = kg * 2.20462;
        return d.intValue();
    }

    public static int lbToKg(int lb) {
        Double d = lb / 2.20462;
        return d.intValue();
    }

    public static double a1cNgspToIfcc(double ngsp) {
        // percentage to mmol/mol
        // [NGSP - 2.152] / 0.09148
        return round((ngsp - 2.152) / 0.09148, 2);
    }

    public static double a1cIfccToNgsp(double ifcc) {
        // mmol/mol to percentage
        // [0.09148 * IFCC] + 2.152
        return round((0.09148 * ifcc) + 2.152, 2);
    }
}