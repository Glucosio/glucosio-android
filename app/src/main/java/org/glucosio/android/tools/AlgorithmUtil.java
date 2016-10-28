package org.glucosio.android.tools;

import android.content.Context;
import android.support.annotation.NonNull;

import org.glucosio.android.object.GlucoseData;
import org.glucosio.android.object.PredictionData;
import org.glucosio.android.object.ReadingData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AlgorithmUtil {

    private static final double TREND_UP_DOWN_LIMIT = 1;
    private static final double TREND_SLIGHT_UP_DOWN_LIMIT = 0.5;

    public enum TrendArrow {
        UNKNOWN,
        DOWN,
        SLIGHTLY_DOWN,
        FLAT,
        SLIGHTLY_UP,
        UP
    }

    public enum Danger {
        HIGH,
        LOW,
        NOTHING
    }

    private static final int MINUTE = 60000;

    // TODO: 15 a good value?
    private static final int PREDICTION_TIME = 15;

    private static final SimpleDateFormat mFormat = new SimpleDateFormat("HH:mm:ss");

    public static String format(Date date) {
        return mFormat.format(date);
    }

    private static int getGlucose(byte[] bytes) {
        return (int)Math.round(((256 * (bytes[0] & 0xFF) + (bytes[1] & 0xFF)) & 0x0FFF) / 8.5);
    }

    public interface AlertRule {
        enum AlertResult {
            INVALID,
            NO_ALERTS,
            NOTHING,
            ALERT_HIGH,
            ALERT_LOW,
        }

        AlertResult doFilter(Context context, GlucoseData prediction);
    }

    public static TrendArrow getTrendArrow(Context context, GlucoseData data) {
        if (data instanceof PredictionData) {
            PredictionData predictionData = (PredictionData) data;

            // TODO: Check what confidenceLimit is;
            float confidenceLimit = 1;
            if (predictionData.confidence > confidenceLimit) {
                return TrendArrow.UNKNOWN;
            } else {
                if (predictionData.trend > TREND_UP_DOWN_LIMIT) {
                    return TrendArrow.UP;
                } else if (predictionData.trend < -TREND_UP_DOWN_LIMIT) {
                    return TrendArrow.DOWN;
                } else if (predictionData.trend > TREND_SLIGHT_UP_DOWN_LIMIT) {
                    return TrendArrow.SLIGHTLY_UP;
                } else if (predictionData.trend < -TREND_SLIGHT_UP_DOWN_LIMIT) {
                    return TrendArrow.SLIGHTLY_DOWN;
                } else {
                    return TrendArrow.FLAT;
                }
            }
        } else {
            return TrendArrow.UNKNOWN;
        }
    }

    public static Danger danger(Context context, PredictionData data, List<AlertRule> rules) {
        if (data.glucoseLevel < 10) return Danger.NOTHING;

        Danger danger = Danger.NOTHING;
        for (AlertRule rule : rules) {
            AlertRule.AlertResult result = rule.doFilter(context, data);
            switch (result) {
                case ALERT_HIGH:
                    danger = Danger.HIGH;
                    break;
                case ALERT_LOW:
                    danger = Danger.LOW;
                    break;
                case NO_ALERTS:
                    return Danger.NOTHING;
            }
        }

        return danger;
    }

    public static ReadingData parseData(int attempt, String tagId, byte[] data) {
        long watchTime = System.currentTimeMillis();

        int indexTrend = data[26] & 0xFF;

        int indexHistory = data[27] & 0xFF;

        final int sensorTime = 256 * (data[317] & 0xFF) + (data[316] & 0xFF);

        long sensorStartTime = watchTime - sensorTime * MINUTE;

        ArrayList<GlucoseData> historyList = new ArrayList<>();

        // loads history values (ring buffer, starting at index_trent. byte 124-315)
        for (int index = 0; index < 32; index++) {
            int i = indexHistory - index - 1;
            if (i < 0) i += 32;
            GlucoseData glucoseData = new GlucoseData();
            glucoseData.glucoseLevel =
                    getGlucose(new byte[]{data[(i * 6 + 125)], data[(i * 6 + 124)]});

            int time = Math.max(0, Math.abs((sensorTime - 3) / 15) * 15 - index * 15);

            glucoseData.realDate = sensorStartTime + time * MINUTE;
            glucoseData.sensorId = tagId;
            glucoseData.sensorTime = time;
            historyList.add(glucoseData);
        }


        ArrayList<GlucoseData> trendList = new ArrayList<>();

        // loads trend values (ring buffer, starting at index_trent. byte 28-123)
        for (int index = 0; index < 16; index++) {
            int i = indexTrend - index - 1;
            if (i < 0) i += 16;
            GlucoseData glucoseData = new GlucoseData();
            glucoseData.glucoseLevel =
                    getGlucose(new byte[]{data[(i * 6 + 29)], data[(i * 6 + 28)]});
            int time = Math.max(0, sensorTime - index);

            glucoseData.realDate = sensorStartTime + time * MINUTE;
            glucoseData.sensorId = tagId;
            glucoseData.sensorTime = time;
            trendList.add(glucoseData);
        }

        PredictionData predictedGlucose = getPredictionData(attempt, tagId, trendList);
        return new ReadingData(predictedGlucose, trendList, historyList);
    }

    @NonNull
    private static PredictionData getPredictionData(int attempt, String tagId, ArrayList<GlucoseData> trendList) {
        PredictionData predictedGlucose = new PredictionData();
/*        SimpleRegression regression = new SimpleRegression();
        for (int i = 0; i < trendList.size(); i++) {
            regression.addData(trendList.size() - i, (trendList.get(i)).glucoseLevel);
        }
        predictedGlucose.glucoseLevel = (int)regression.predict(15 + PREDICTION_TIME);
        predictedGlucose.trend = regression.getSlope();
        predictedGlucose.confidence = regression.getSlopeConfidenceInterval();*/
        predictedGlucose.errorCode = PredictionData.Result.OK;
        predictedGlucose.realDate = trendList.get(0).realDate;
        predictedGlucose.sensorId = tagId;
        predictedGlucose.attempt = attempt;
        predictedGlucose.sensorTime = trendList.get(0).sensorTime;
        return predictedGlucose;
    }
}
