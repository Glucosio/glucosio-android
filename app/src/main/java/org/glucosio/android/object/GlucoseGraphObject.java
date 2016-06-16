package org.glucosio.android.object;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Date;

public class GlucoseGraphObject {
    private DateTime created;
    private int reading;

    public GlucoseGraphObject(DateTime created, int reading){
        this.created = created;
        this.reading = reading;
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

    public void setReading(int reading) {
        this.reading = reading;
    }
}
