package org.glucosio.android.object;

import org.joda.time.DateTime;

public class GraphObject {
    private DateTime created;
    private int reading;
    private double doubleReading;

    public GraphObject(DateTime created, int reading){
        this.created = created;
        this.reading = reading;
    }

    public GraphObject(DateTime created, double reading){
        this.created = created;
        this.doubleReading = reading;
    }

    public DateTime getCreated() {
        return created;
    }

    public void setCreated(DateTime created) {
        this.created = created;
    }

    public int getReading() {
        return reading;
    }

    public double getDoubleReading() {
        return doubleReading;
    }

    public void setReading(int reading) {
        this.reading = reading;
    }
}
