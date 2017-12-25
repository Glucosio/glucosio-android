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

package org.glucosio.android.db;

import android.content.Context;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;
import net.danlew.android.joda.JodaTimeAndroid;
import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.Weeks;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DatabaseHandler {

    private static RealmConfiguration mRealmConfig;
    private Context mContext;
    private Realm realm;

    public DatabaseHandler(Context context) {
        this.mContext = context;
        Realm.init(context);
        this.realm = getNewRealmInstance();
    }

    public Realm getNewRealmInstance() {
        if (mRealmConfig == null) {
            mRealmConfig = new RealmConfiguration.Builder()
                    .schemaVersion(5)
                    .migration(new Migration())
                    .build();
        }
        return Realm.getInstance(mRealmConfig); // Automatically run migration if needed
    }

    public Realm getRealmInstance() {
        return realm;
    }

    public void addUser(User user) {
        realm.beginTransaction();
        realm.copyToRealm(user);
        realm.commitTransaction();
    }

    public User getUser(long id) {
        return realm.where(User.class)
                .equalTo("id", id)
                .findFirst();
    }

    public User getUser(Realm realm, long id) {
        return realm.where(User.class)
                .equalTo("id", id)
                .findFirst();
    }

    public void updateUser(User user) {
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(user);
        realm.commitTransaction();
    }

    public boolean addReminder(Reminder reminder) {
        // Check for duplicates first
        if (getReminder(reminder.getId()) == null) {
            realm.beginTransaction();
            realm.copyToRealm(reminder);
            realm.commitTransaction();
            return true;
        }

        return false;
    }

    public void updateReminder(Reminder reminder) {
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(reminder);
        realm.commitTransaction();
    }

    private void deleteReminder(Reminder reminder) {
        realm.beginTransaction();
        reminder.deleteFromRealm();
        realm.commitTransaction();
    }

    public void deleteReminder(long id) {
        deleteReminder(getReminder(id));
    }

    public boolean areRemindersActive() {
        RealmResults<Reminder> activeRemindersList =
                realm.where(Reminder.class)
                        .equalTo("active", true)
                        .findAll();

        return activeRemindersList.size() > 0;
    }

    public Reminder getReminder(long id) {
        return realm.where(Reminder.class)
                .equalTo("id", id)
                .findFirst();
    }

    public List<Reminder> getReminders() {
        RealmResults<Reminder> results =
                realm.where(Reminder.class)
                        .findAllSorted("alarmTime", Sort.DESCENDING);
        List<Reminder> reminders = new ArrayList<>(results.size());
        for (int i = 0; i < results.size(); i++) {
            reminders.add(results.get(i));
        }
        return reminders;
    }

    public ArrayList<Date> getRemindersDatesAsArray() {
        List<Reminder> readings = getReminders();
        ArrayList<Date> datesArray = new ArrayList<Date>();
        int i;

        for (i = 0; i < readings.size(); i++) {
            Date reading;
            Reminder reminder = readings.get(i);
            reading = reminder.getAlarmTime();
            datesArray.add(reading);
        }
        return datesArray;
    }

    public ArrayList<String> getRemindersDatesStringAsArray() {
        List<Reminder> readings = getReminders();
        ArrayList<String> datesArray = new ArrayList<String>();
        int i;
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        for (i = 0; i < readings.size(); i++) {
            String reading;
            Reminder reminder = readings.get(i);
            reading = inputFormat.format(reminder.getAlarmTime());
            datesArray.add(reading);
        }

        return datesArray;
    }

    public boolean addGlucoseReading(GlucoseReading reading) {
        // generate record Id
        String id = generateIdFromDate(reading.getCreated(), reading.getId());

        // Check for duplicates
        if (getGlucoseReadingById(Long.parseLong(id)) != null) {
            return false;
        } else {
            realm.beginTransaction();
            reading.setId(Long.parseLong(id));
            realm.copyToRealm(reading);
            realm.commitTransaction();
            return true;
        }
    }

    private String generateIdFromDate(Date created, long readingId) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(created);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        return "" + year + month + day + hours + minutes + readingId;
    }

    public void addNGlucoseReadings(int n) {
        for (int i = 0; i < n; i++) {
            Calendar calendar = Calendar.getInstance();
            GlucoseReading gReading = new GlucoseReading(50 + i, "Debug reading", calendar.getTime(), "");

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int hours = calendar.get(Calendar.HOUR_OF_DAY);
            int minutes = calendar.get(Calendar.MINUTE);
            String id = "" + year + month + day + hours + minutes + gReading.getReading();

            // Check for duplicates
            if (getGlucoseReadingById(Long.parseLong(id)) == null) {
                realm.beginTransaction();
                gReading.setId(Long.parseLong(id));
                realm.copyToRealm(gReading);
                realm.commitTransaction();
            }
        }
    }

    public boolean editGlucoseReading(long oldId, GlucoseReading reading) {
        // First delete the old reading
        deleteGlucoseReading(getGlucoseReadingById(oldId));
        // then save the new one
        return addGlucoseReading(reading);
    }

    public void deleteGlucoseReading(GlucoseReading reading) {
        realm.beginTransaction();
        reading.deleteFromRealm();
        realm.commitTransaction();
    }

    public GlucoseReading getLastGlucoseReading() {
        RealmResults<GlucoseReading> results =
                realm.where(GlucoseReading.class)
                        .findAllSorted("created", Sort.DESCENDING);
        return results.get(0);
    }

    public List<GlucoseReading> getGlucoseReadings() {
        RealmResults<GlucoseReading> results =
                realm.where(GlucoseReading.class)
                        .findAllSorted("created", Sort.DESCENDING);
        ArrayList<GlucoseReading> readingList = new ArrayList<>();
        for (int i = 0; i < results.size(); i++) {
            readingList.add(results.get(i));
        }
        return readingList;
    }

    public List<GlucoseReading> getGlucoseReadings(Realm realm) {
        RealmResults<GlucoseReading> results =
                realm.where(GlucoseReading.class)
                        .findAllSorted("created", Sort.DESCENDING);
        return new ArrayList<>(results);
    }

    private ArrayList<GlucoseReading> getGlucoseReadings(Date from, Date to) {
        RealmResults<GlucoseReading> results =
                realm.where(GlucoseReading.class)
                        .between("created", from, to)
                        .findAllSorted("created", Sort.DESCENDING);
        return new ArrayList<>(results);
    }

    public List<GlucoseReading> getGlucoseReadings(Realm realm, Date from, Date to) {
        RealmResults<GlucoseReading> results =
                realm.where(GlucoseReading.class)
                        .between("created", from, to)
                        .findAllSorted("created", Sort.DESCENDING);
        return new ArrayList<>(results);
    }

    public GlucoseReading getGlucoseReadingById(long id) {
        return realm.where(GlucoseReading.class)
                .equalTo("id", id)
                .findFirst();
    }

    public List<Long> getGlucoseIdAsList() {
        List<GlucoseReading> glucoseReading = getGlucoseReadings();
        List<Long> idArray = new ArrayList<>(glucoseReading.size());

        for (GlucoseReading aGlucoseReading : glucoseReading) {
            long id = aGlucoseReading.getId();
            idArray.add(id);
        }

        return idArray;
    }

    public List<Double> getGlucoseReadingAsList() {
        List<GlucoseReading> glucoseReading = getGlucoseReadings();
        ArrayList<Double> readingArray = new ArrayList<>(glucoseReading.size());
        int i;

        for (i = 0; i < glucoseReading.size(); i++) {
            GlucoseReading singleReading = glucoseReading.get(i);
            double reading = singleReading.getReading();
            readingArray.add(reading);
        }

        return readingArray;
    }

    public List<String> getGlucoseTypeAsList() {
        List<GlucoseReading> glucoseReading = getGlucoseReadings();
        List<String> typeArray = new ArrayList<>(glucoseReading.size());

        for (GlucoseReading aGlucoseReading : glucoseReading) {
            String reading = aGlucoseReading.getReading_type();
            typeArray.add(reading);
        }

        return typeArray;
    }

    public List<String> getGlucoseNotesAsList() {
        List<GlucoseReading> glucoseReading = getGlucoseReadings();
        ArrayList<String> notesArray = new ArrayList<>(glucoseReading.size());

        for (GlucoseReading aGlucoseReading : glucoseReading) {
            String reading = aGlucoseReading.getNotes();
            notesArray.add(reading);
        }

        return notesArray;
    }

    public List<String> getGlucoseDateTimeAsList() {
        List<GlucoseReading> glucoseReading = getGlucoseReadings();
        ArrayList<String> datetimeArray = new ArrayList<>(glucoseReading.size());
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        for (GlucoseReading singleReading : glucoseReading) {
            String reading = inputFormat.format(singleReading.getCreated());
            datetimeArray.add(reading);
        }

        return datetimeArray;
    }

    public List<Double> getAverageGlucoseReadingsByWeek() {
        JodaTimeAndroid.init(mContext);

        DateTime maxDateTime = new DateTime(realm.where(GlucoseReading.class).maximumDate("created").getTime());
        DateTime minDateTime = new DateTime(realm.where(GlucoseReading.class).minimumDate("created").getTime());

        DateTime currentDateTime = minDateTime;
        DateTime newDateTime;

        List<Double> averageReadings = new ArrayList<>(Weeks.weeksBetween(maxDateTime, minDateTime).getWeeks());

        // The number of weeks is at least 1 since we do have average for the current week even if incomplete
        int weeksNumber = Weeks.weeksBetween(minDateTime, maxDateTime).getWeeks() + 1;

        for (int i = 0; i < weeksNumber; i++) {
            newDateTime = currentDateTime.plusWeeks(1);
            RealmResults<GlucoseReading> readings = realm.where(GlucoseReading.class)
                    .between("created", currentDateTime.toDate(), newDateTime.toDate())
                    .findAll();
            averageReadings.add(readings.average("reading"));
            currentDateTime = newDateTime;
        }
        return averageReadings;
    }

    public List<String> getGlucoseDatetimesByWeek() {
        JodaTimeAndroid.init(mContext);

        DateTime maxDateTime = new DateTime(realm.where(GlucoseReading.class).maximumDate("created").getTime());
        DateTime minDateTime = new DateTime(realm.where(GlucoseReading.class).minimumDate("created").getTime());

        DateTime currentDateTime = minDateTime;
        DateTime newDateTime;

        List<String> finalWeeks = new ArrayList<>();

        // The number of weeks is at least 1 since we do have average for the current week even if incomplete
        int weeksNumber = Weeks.weeksBetween(minDateTime, maxDateTime).getWeeks() + 1;

        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        for (int i = 0; i < weeksNumber; i++) {
            newDateTime = currentDateTime.plusWeeks(1);
            finalWeeks.add(inputFormat.format(newDateTime.toDate()));
            currentDateTime = newDateTime;
        }
        return finalWeeks;
    }

    public List<Double> getAverageGlucoseReadingsByMonth() {
        JodaTimeAndroid.init(mContext);

        DateTime maxDateTime = new DateTime(realm.where(GlucoseReading.class).maximumDate("created").getTime());
        DateTime minDateTime = new DateTime(realm.where(GlucoseReading.class).minimumDate("created").getTime());

        DateTime currentDateTime = minDateTime;
        DateTime newDateTime;

        List<Double> averageReadings = new ArrayList<>(Months.monthsBetween(maxDateTime, minDateTime).getMonths());

        // The number of months is at least 1 since we do have average for the current week even if incomplete
        int monthsNumber = Months.monthsBetween(minDateTime, maxDateTime).getMonths() + 1;

        for (int i = 0; i < monthsNumber; i++) {
            newDateTime = currentDateTime.plusMonths(1);
            RealmResults<GlucoseReading> readings = realm.where(GlucoseReading.class)
                    .between("created", currentDateTime.toDate(), newDateTime.toDate())
                    .findAll();
            averageReadings.add(readings.average("reading"));
            currentDateTime = newDateTime;
        }
        return averageReadings;
    }

    public List<String> getGlucoseDatetimesByMonth() {
        JodaTimeAndroid.init(mContext);

        DateTime maxDateTime = new DateTime(realm.where(GlucoseReading.class).maximumDate("created").getTime());
        DateTime minDateTime = new DateTime(realm.where(GlucoseReading.class).minimumDate("created").getTime());

        DateTime currentDateTime = minDateTime;
        DateTime newDateTime = minDateTime;

        ArrayList<String> finalMonths = new ArrayList<String>();

        // The number of months is at least 1 because current month is incomplete
        int monthsNumber = Months.monthsBetween(minDateTime, maxDateTime).getMonths() + 1;

        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        for (int i = 0; i < monthsNumber; i++) {
            newDateTime = currentDateTime.plusMonths(1);
            finalMonths.add(inputFormat.format(newDateTime.toDate()));
            currentDateTime = newDateTime;
        }
        return finalMonths;
    }

    public List<GlucoseReading> getLastMonthGlucoseReadings() {
        JodaTimeAndroid.init(mContext);

        DateTime todayDateTime = DateTime.now();
        DateTime minDateTime = DateTime.now().minusMonths(1).minusDays(15);

        return getGlucoseReadings(minDateTime.toDate(), todayDateTime.toDate());
    }

    public void addHB1ACReading(HB1ACReading reading) {
        realm.beginTransaction();
        reading.setId(getNextKey("hb1ac"));
        realm.copyToRealm(reading);
        realm.commitTransaction();
    }

    public void deleteHB1ACReading(HB1ACReading reading) {
        realm.beginTransaction();
        reading.deleteFromRealm();
        realm.commitTransaction();
    }

    public HB1ACReading getHB1ACReadingById(long id) {
        return realm.where(HB1ACReading.class)
                .equalTo("id", id)
                .findFirst();
    }

    public void editHB1ACReading(long oldId, HB1ACReading reading) {
        // First delete the old reading
        deleteHB1ACReading(getHB1ACReadingById(oldId));
        // then save the new one
        addHB1ACReading(reading);
    }

    public RealmResults<HB1ACReading> getrHB1ACRawReadings() {
        return realm.where(HB1ACReading.class)
                .findAllSorted("created", Sort.DESCENDING);
    }

    public ArrayList<HB1ACReading> getHB1ACReadings() {
        RealmResults<HB1ACReading> results =
                realm.where(HB1ACReading.class)
                        .findAllSorted("created", Sort.DESCENDING);
        ArrayList<HB1ACReading> readingList = new ArrayList<>();
        for (int i = 0; i < results.size(); i++) {
            readingList.add(results.get(i));
        }
        return readingList;
    }

    public ArrayList<Long> getHB1ACIdAsArray() {
        List<HB1ACReading> readings = getHB1ACReadings();
        ArrayList<Long> idArray = new ArrayList<Long>();
        int i;

        for (i = 0; i < readings.size(); i++) {
            long id;
            HB1ACReading singleReading = readings.get(i);
            id = singleReading.getId();
            idArray.add(id);
        }

        return idArray;
    }

    public ArrayList<Double> getHB1ACReadingAsArray() {
        List<HB1ACReading> readings = getHB1ACReadings();
        ArrayList<Double> readingArray = new ArrayList<Double>();
        int i;

        for (i = 0; i < readings.size(); i++) {
            double reading;
            HB1ACReading singleReading = readings.get(i);
            reading = singleReading.getReading();
            readingArray.add(reading);
        }

        return readingArray;
    }

    public ArrayList<String> getHB1ACDateTimeAsArray() {
        List<HB1ACReading> readings = getHB1ACReadings();
        ArrayList<String> datetimeArray = new ArrayList<String>();
        int i;
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        for (i = 0; i < readings.size(); i++) {
            String reading;
            HB1ACReading singleReading = readings.get(i);
            reading = inputFormat.format(singleReading.getCreated());
            datetimeArray.add(reading);
        }

        return datetimeArray;
    }

    public RealmResults<KetoneReading> getRawKetoneReadings() {
        return realm.where(KetoneReading.class)
                .findAllSorted("created", Sort.DESCENDING);
    }

    public void addKetoneReading(KetoneReading reading) {
        realm.beginTransaction();
        reading.setId(getNextKey("ketone"));
        realm.copyToRealm(reading);
        realm.commitTransaction();
    }

    public void editKetoneReading(long oldId, KetoneReading reading) {
        // First delete the old reading
        deleteKetoneReading(getKetoneReadingById(oldId));
        // then save the new one
        addKetoneReading(reading);
    }

    public KetoneReading getKetoneReadingById(long id) {
        return realm.where(KetoneReading.class)
                .equalTo("id", id)
                .findFirst();
    }

    public void deleteKetoneReading(KetoneReading reading) {
        realm.beginTransaction();
        reading.deleteFromRealm();
        realm.commitTransaction();
    }

    public ArrayList<KetoneReading> getKetoneReadings() {
        RealmResults<KetoneReading> results =
                realm.where(KetoneReading.class)
                        .findAllSorted("created", Sort.DESCENDING);
        ArrayList<KetoneReading> readingList = new ArrayList<>();
        for (int i = 0; i < results.size(); i++) {
            readingList.add(results.get(i));
        }
        return readingList;
    }

    public ArrayList<Long> getKetoneIdAsArray() {
        List<KetoneReading> readings = getKetoneReadings();
        ArrayList<Long> idArray = new ArrayList<Long>();
        int i;

        for (i = 0; i < readings.size(); i++) {
            long id;
            KetoneReading singleReading = readings.get(i);
            id = singleReading.getId();
            idArray.add(id);
        }

        return idArray;
    }

    public List<Double> getKetoneReadingAsArray() {
        List<KetoneReading> readings = getKetoneReadings();
        ArrayList<Double> readingArray = new ArrayList<>(readings.size());
        int i;

        for (i = 0; i < readings.size(); i++) {
            double reading;
            KetoneReading singleReading = readings.get(i);
            reading = singleReading.getReading();
            readingArray.add(reading);
        }

        return readingArray;
    }

    public ArrayList<String> getKetoneDateTimeAsArray() {
        List<KetoneReading> readings = getKetoneReadings();
        ArrayList<String> datetimeArray = new ArrayList<String>();
        int i;
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        for (i = 0; i < readings.size(); i++) {
            String reading;
            KetoneReading singleReading = readings.get(i);
            reading = inputFormat.format(singleReading.getCreated());
            datetimeArray.add(reading);
        }

        return datetimeArray;
    }

    public void addPressureReading(PressureReading reading) {
        realm.beginTransaction();
        reading.setId(getNextKey("pressure"));
        realm.copyToRealm(reading);
        realm.commitTransaction();
    }

    public PressureReading getPressureReading(long id) {
        return realm.where(PressureReading.class)
                .equalTo("id", id)
                .findFirst();
    }

    public void deletePressureReading(PressureReading reading) {
        realm.beginTransaction();
        reading.deleteFromRealm();
        realm.commitTransaction();
    }

    public void editPressureReading(long oldId, PressureReading reading) {
        // First delete the old reading
        deletePressureReading(getPressureReading(oldId));
        // then save the new one
        addPressureReading(reading);
    }

    public ArrayList<PressureReading> getPressureReadings() {
        RealmResults<PressureReading> results =
                realm.where(PressureReading.class)
                        .findAllSorted("created", Sort.DESCENDING);
        ArrayList<PressureReading> readingList = new ArrayList<>();
        for (int i = 0; i < results.size(); i++) {
            readingList.add(results.get(i));
        }
        return readingList;
    }

    public ArrayList<Long> getPressureIdAsArray() {
        List<PressureReading> readings = getPressureReadings();
        ArrayList<Long> idArray = new ArrayList<Long>();
        int i;

        for (i = 0; i < readings.size(); i++) {
            long id;
            PressureReading singleReading = readings.get(i);
            id = singleReading.getId();
            idArray.add(id);
        }

        return idArray;
    }

    public List<Double> getMinPressureReadingAsArray() {
        List<PressureReading> readings = getPressureReadings();
        ArrayList<Double> readingArray = new ArrayList<>(readings.size());
        int i;

        for (i = 0; i < readings.size(); i++) {
            PressureReading singleReading = readings.get(i);
            double reading = singleReading.getMinReading();
            readingArray.add(reading);
        }

        return readingArray;
    }

    public List<Double> getMaxPressureReadingAsArray() {
        List<PressureReading> readings = getPressureReadings();
        ArrayList<Double> readingArray = new ArrayList<>(readings.size());
        int i;

        for (i = 0; i < readings.size(); i++) {
            PressureReading singleReading = readings.get(i);
            double reading = singleReading.getMaxReading();
            readingArray.add(reading);
        }

        return readingArray;
    }

    public ArrayList<String> getPressureDateTimeAsArray() {
        List<PressureReading> readings = getPressureReadings();
        ArrayList<String> datetimeArray = new ArrayList<String>();
        int i;
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        for (i = 0; i < readings.size(); i++) {
            String reading;
            PressureReading singleReading = readings.get(i);
            reading = inputFormat.format(singleReading.getCreated());
            datetimeArray.add(reading);
        }

        return datetimeArray;
    }

    public void addWeightReading(WeightReading reading) {
        realm.beginTransaction();
        reading.setId(getNextKey("weight"));
        realm.copyToRealm(reading);
        realm.commitTransaction();
    }

    public void editWeightReading(long oldId, WeightReading reading) {
        // First delete the old reading
        deleteWeightReading(getWeightReadingById(oldId));
        // then save the new one
        addWeightReading(reading);
    }

    public WeightReading getWeightReadingById(long id) {
        return realm.where(WeightReading.class)
                .equalTo("id", id)
                .findFirst();
    }

    public void deleteWeightReading(WeightReading reading) {
        realm.beginTransaction();
        reading.deleteFromRealm();
        realm.commitTransaction();
    }

    public ArrayList<WeightReading> getWeightReadings() {
        RealmResults<WeightReading> results =
                realm.where(WeightReading.class)
                        .findAllSorted("created", Sort.DESCENDING);
        ArrayList<WeightReading> readingList = new ArrayList<>();
        for (int i = 0; i < results.size(); i++) {
            readingList.add(results.get(i));
        }
        return readingList;
    }

    public ArrayList<Long> getWeightIdAsArray() {
        List<WeightReading> readings = getWeightReadings();
        ArrayList<Long> idArray = new ArrayList<Long>();
        int i;

        for (i = 0; i < readings.size(); i++) {
            long id;
            WeightReading singleReading = readings.get(i);
            id = singleReading.getId();
            idArray.add(id);
        }

        return idArray;
    }

    public List<Double> getWeightReadingAsArray() {
        List<WeightReading> readings = getWeightReadings();
        ArrayList<Double> readingArray = new ArrayList<>(readings.size());
        int i;

        for (i = 0; i < readings.size(); i++) {
            WeightReading singleReading = readings.get(i);
            double reading = singleReading.getReading();
            readingArray.add(reading);
        }

        return readingArray;
    }

    public ArrayList<String> getWeightReadingDateTimeAsArray() {
        List<WeightReading> readings = getWeightReadings();
        ArrayList<String> datetimeArray = new ArrayList<String>();
        int i;
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        for (i = 0; i < readings.size(); i++) {
            String reading;
            WeightReading singleReading = readings.get(i);
            reading = inputFormat.format(singleReading.getCreated());
            datetimeArray.add(reading);
        }

        return datetimeArray;
    }

    public void addCholesterolReading(CholesterolReading reading) {
        realm.beginTransaction();
        reading.setId(getNextKey("cholesterol"));
        realm.copyToRealm(reading);
        realm.commitTransaction();
    }

    public void editCholesterolReading(long oldId, CholesterolReading reading) {
        // First delete the old reading
        deleteCholesterolReading(getCholesterolReading(oldId));
        // then save the new one
        addCholesterolReading(reading);
    }

    public CholesterolReading getCholesterolReading(long id) {
        return realm.where(CholesterolReading.class)
                .equalTo("id", id)
                .findFirst();
    }

    public void deleteCholesterolReading(CholesterolReading reading) {
        realm.beginTransaction();
        reading.deleteFromRealm();
        realm.commitTransaction();
    }

    public ArrayList<CholesterolReading> getCholesterolReadings() {
        RealmResults<CholesterolReading> results =
                realm.where(CholesterolReading.class)
                        .findAllSorted("created", Sort.DESCENDING);
        ArrayList<CholesterolReading> readingList = new ArrayList<>();
        for (int i = 0; i < results.size(); i++) {
            readingList.add(results.get(i));
        }
        return readingList;
    }

    public ArrayList<Long> getCholesterolIdAsArray() {
        List<CholesterolReading> readings = getCholesterolReadings();
        ArrayList<Long> idArray = new ArrayList<Long>();
        int i;

        for (i = 0; i < readings.size(); i++) {
            long id;
            CholesterolReading singleReading = readings.get(i);
            id = singleReading.getId();
            idArray.add(id);
        }

        return idArray;
    }

    public List<Double> getHDLCholesterolReadingAsArray() {
        List<CholesterolReading> readings = getCholesterolReadings();
        ArrayList<Double> readingArray = new ArrayList<>(readings.size());
        int i;

        for (i = 0; i < readings.size(); i++) {
            CholesterolReading singleReading = readings.get(i);
            double reading = singleReading.getHDLReading();
            readingArray.add(reading);
        }

        return readingArray;
    }

    public List<Double> getLDLCholesterolReadingAsArray() {
        List<CholesterolReading> readings = getCholesterolReadings();
        ArrayList<Double> readingArray = new ArrayList<>(readings.size());
        int i;

        for (i = 0; i < readings.size(); i++) {
            CholesterolReading singleReading = readings.get(i);
            double reading = singleReading.getLDLReading();
            readingArray.add(reading);
        }

        return readingArray;
    }

    public List<Double> getTotalCholesterolReadingAsArray() {
        List<CholesterolReading> readings = getCholesterolReadings();
        ArrayList<Double> readingArray = new ArrayList<>(readings.size());
        int i;

        for (i = 0; i < readings.size(); i++) {
            CholesterolReading singleReading = readings.get(i);
            double reading = singleReading.getTotalReading();
            readingArray.add(reading);
        }

        return readingArray;
    }

    public ArrayList<String> getCholesterolDateTimeAsArray() {
        List<CholesterolReading> readings = getCholesterolReadings();
        ArrayList<String> datetimeArray = new ArrayList<String>();
        int i;
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        for (i = 0; i < readings.size(); i++) {
            String reading;
            CholesterolReading singleReading = readings.get(i);
            reading = inputFormat.format(singleReading.getCreated());
            datetimeArray.add(reading);
        }

        return datetimeArray;
    }

    private long getNextKey(String where) {
        Number maxId = null;
        switch (where) {
            case "glucose":
                maxId = realm.where(GlucoseReading.class)
                        .max("id");
                break;
            case "weight":
                maxId = realm.where(WeightReading.class)
                        .max("id");
                break;
            case "hb1ac":
                maxId = realm.where(HB1ACReading.class)
                        .max("id");
                break;
            case "pressure":
                maxId = realm.where(PressureReading.class)
                        .max("id");
                break;
            case "ketone":
                maxId = realm.where(KetoneReading.class)
                        .max("id");
                break;
            case "cholesterol":
                maxId = realm.where(CholesterolReading.class)
                        .max("id");
                break;
        }
        if (maxId == null) {
            return 0;
        } else {
            return Long.parseLong(maxId.toString()) + 1;
        }
    }
}
