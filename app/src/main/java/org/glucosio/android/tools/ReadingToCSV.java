package org.glucosio.android.tools;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.util.Log;

import org.glucosio.android.R;
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

    public Uri createCSV(final ArrayList<GlucoseReading> readings, String um) {

        final File file = new File(context.getFilesDir(), "glucosio_exported_data.csv"); //Getting a file within the dir.
        file.delete();


        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            OutputStreamWriter osw = new OutputStreamWriter(fileOutputStream);

            osw.append(context.getResources().getString(R.string.dialog_add_concentration));
            osw.append(',');

            osw.append(context.getResources().getString(R.string.dialog_add_measured));
            osw.append(',');

            osw.append(context.getResources().getString(R.string.dialog_add_date));
            osw.append(',');

            osw.append(context.getResources().getString(R.string.dialog_add_time));
            osw.append('\n');

            FormatDateTime dateTool = new FormatDateTime(context);

            if (um.equals("mg/dL")) {
                for (int i = 0; i < readings.size(); i++) {

                    osw.append(readings.get(i).getReading() + "mg/dL");
                    osw.append(',');

                    osw.append(readings.get(i).getReading_type() + "");
                    osw.append(',');

                    osw.append(dateTool.convertRawDate(readings.get(i).getCreated() + ""));
                    osw.append(',');

                    osw.append(dateTool.convertRawTime(readings.get(i).getCreated() + ""));
                    osw.append('\n');
                }
            } else {
                GlucoseConverter converter = new GlucoseConverter();

                for (int i = 0; i < readings.size(); i++) {

                    osw.append(converter.glucoseToMmolL(readings.get(i).getReading())+ "mmol/L");
                    osw.append(',');

                    osw.append(dateTool.convertRawDate(readings.get(i).getCreated() + ""));
                    osw.append(',');

                    osw.append(dateTool.convertRawTime(readings.get(i).getCreated() + ""));
                    osw.append('\n');
                }
            }

            osw.close();
            Log.i("Glucosio", "Done exporting readings" );

        } catch (Exception e) {
            e.printStackTrace();
        }
        return FileProvider.getUriForFile(context, context.getPackageName() + ".provider.fileprovider", file);
    }
}
