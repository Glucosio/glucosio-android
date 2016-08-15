package org.glucosio.android.object;

import org.joda.time.DateTime;

public class DoubleGraphObject {
    private DateTime created;
    private double reading;

    public DoubleGraphObject(DateTime created, double reading) {
        this.created = created;
        this.reading = reading;
    }

    public DateTime getCreated() {
        return created;
    }

    public void setCreated(DateTime created) {
        this.created = created;
    }

    public double getReading() {
        return reading;
    }

    public void setReading(int reading) {
        this.reading = reading;
    }
}
