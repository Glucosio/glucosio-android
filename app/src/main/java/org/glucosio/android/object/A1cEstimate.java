package org.glucosio.android.object;

import org.glucosio.android.tools.GlucosioConverter;

import java.text.NumberFormat;

public class A1cEstimate {
    private double value;
    private String month;

    public A1cEstimate(double value, String month) {
        this.value = value;
        this.month = month;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getGlucoseAverage() {
        return NumberFormat.getInstance().format(GlucosioConverter.a1cToGlucose(value));
    }
}
