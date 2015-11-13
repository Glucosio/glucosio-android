package org.glucosio.android.tools;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import org.glucosio.android.db.GlucoseReading;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class ReadingToCSV {

    Context context;

    public ReadingToCSV(Context mContext){
        this.context = mContext;
    }

    public Uri createCSV(final ArrayList<GlucoseReading> readings) {

        final File dir = context.getDir("Glucosio", Context.MODE_PRIVATE); //Creating an internal dir;
        final File file = new File(dir, "exported_data.csv"); //Getting a file within the dir.


        new Thread() {
            public void run() {
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    OutputStreamWriter osw = new OutputStreamWriter(fileOutputStream);

                    Log.e("Glucosio", readings.size() + "mg/dL");

                    for (int i=0; i<readings.size(); i++) {

                        osw.append(readings.get(i).getReading() + "mg/dL");
                        Log.e("", readings.get(i).getReading() + "mg/dL");
                        osw.append(',');

                        osw.append(readings.get(i).getReading_type() + "");
                        osw.append(',');

                        osw.append(readings.get(i).getCreated() + "");
                        osw.append('\n');
                    }

                    osw.close();
                    Log.e("Glucosio", "finished" );

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.run();
        return Uri.parse(file.toString());
    }
}
