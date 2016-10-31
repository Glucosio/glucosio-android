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

import android.content.Context;
import android.os.Environment;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import org.glucosio.android.db.GlucoseReading;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;

import io.realm.Realm;


public final class ReadingToPdf {

    public static String createPDFFile(Context context, Realm realm, final List<GlucoseReading> readings, String um) {
        try {
            final File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/glucosio", "glucosio_export_" + System.currentTimeMillis() / 1000 + ".pdf");
            final File sd = Environment.getExternalStorageDirectory();
            if (sd.canWrite()) {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                Document document = new Document();
                PdfWriter.getInstance(document, fileOutputStream);
                PdfPTable table = new PdfPTable(6);

                document.open();

                // CSV Structure
                // Date | Time | Concentration | Unit | Measured | Notes

                PdfPCell c1 = new PdfPCell(new Phrase("Date"));
                c1.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(c1);

                c1 = new PdfPCell(new Phrase("Time"));
                c1.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(c1);

                c1 = new PdfPCell(new Phrase("Concentration"));
                c1.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(c1);

                c1 = new PdfPCell(new Phrase("Unit"));
                c1.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(c1);

                c1 = new PdfPCell(new Phrase("Measured"));
                c1.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(c1);

                c1 = new PdfPCell(new Phrase("Notes"));
                c1.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(c1);

                FormatDateTime dateTool = new FormatDateTime(context);

                if ("mg/dL".equals(um)) {
                    for (int i = 0; i < readings.size(); i++) {
                        GlucoseReading reading = readings.get(i);
                        table.addCell(dateTool.convertRawDate(reading.getCreated().toString()));
                        table.addCell(dateTool.convertRawTime(reading.getCreated().toString()));
                        table.addCell(String.valueOf(reading.getReading()));
                        table.addCell("mg/dL");
                        table.addCell(String.valueOf(reading.getReading_type()));
                        table.addCell(reading.getNotes());
                    }
                } else {
                    GlucosioConverter converter = new GlucosioConverter();
                    for (int i = 0; i < readings.size(); i++) {
                        GlucoseReading reading = readings.get(i);
                        table.addCell(dateTool.convertRawDate(reading.getCreated().toString()));
                        table.addCell(dateTool.convertRawTime(reading.getCreated().toString()));
                        table.addCell(String.valueOf(converter.glucoseToMmolL(reading.getReading())));
                        table.addCell("mmol/L");
                        table.addCell(String.valueOf(reading.getReading_type()));
                        table.addCell(reading.getNotes());
                    }
                }
                document.add(table);
                document.close();
            }

            realm.close();
            return file.getPath();
        }
        catch (FileNotFoundException e) {
            realm.close();
            e.printStackTrace();
            return null;
        }
        catch (DocumentException e) {
            realm.close();
            e.printStackTrace();
            return null;
        }
    }
}
