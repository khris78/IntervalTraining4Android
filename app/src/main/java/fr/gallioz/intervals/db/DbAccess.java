package fr.gallioz.intervals.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.net.ParseException;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.gallioz.intervals.*;
import fr.gallioz.intervals.beans.IntervalDescription;
import fr.gallioz.intervals.beans.IntervalTypeDescription;
import fr.gallioz.intervals.beans.TrainingPlanDescription;
import fr.gallioz.intervals.beans.TrainingSessionDescription;
import static fr.gallioz.intervals.utils.AppliResources.*;
import fr.gallioz.intervals.utils.AppliResources;
import fr.gallioz.intervals.utils.LockableArrayList;

import static fr.gallioz.intervals.db.DbContract.*;

public class DbAccess extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 4;
    public static final String DATABASE_NAME = "intervalTraining.db";

    private static final long MINUTE = 60000L;
    private static final long SECOND = 1000L;

    private SQLiteDatabase database;

    public DbAccess(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PARAMETERS_TABLE);
        db.execSQL(CREATE_TRAINING_PLAN_TABLE);
        db.execSQL(CREATE_TRAINING_SESSION_TABLE);
        db.execSQL(CREATE_INTERVAL_TYPE_TABLE);
        db.execSQL(CREATE_INTERVAL_TABLE);

        initDefaultPlans(db);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The parameters can be reinitialized, whatever the version is
        db.execSQL("Drop table " + Parameters.TABLE_NAME);
        db.execSQL(CREATE_PARAMETERS_TABLE);

        // Version 4 : adding the INTERVAL_TYPE, TRAINING_PLAN, TRAINING_SESSION, and INTERVAL tables
        if (oldVersion < 4) {
            db.execSQL("Drop table " + TrainingPlan.TABLE_NAME);
            db.execSQL("Drop table " + TrainingSession.TABLE_NAME);
            db.execSQL("Drop table " + Interval.TABLE_NAME);
            db.execSQL("Drop table " + DbContract.IntervalType.TABLE_NAME);
            db.execSQL(CREATE_TRAINING_PLAN_TABLE);
            db.execSQL(CREATE_TRAINING_SESSION_TABLE);
            db.execSQL(CREATE_INTERVAL_TYPE_TABLE);
            db.execSQL(CREATE_INTERVAL_TABLE);

            initDefaultPlans(db);
        }
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private SQLiteDatabase getDatabase() {
        if (database == null) {
            database = getWritableDatabase();
        }
        return database;
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////////
    // TRAINING PLAN
    private void initDefaultPlans(SQLiteDatabase db) {
        // INTERVAL_TYPE
        ContentValues map = new ContentValues();
        map.put(DbContract.IntervalType.COLUMN_NAME_LABEL, AppliResources.getContext().getString(R.string.run));
        map.put(DbContract.IntervalType.COLUMN_NAME_COLOR, "#ba1b53");
        map.put(DbContract.IntervalType.COLUMN_NAME_END_SND, "dingding");

        long runType = db.insert(DbContract.IntervalType.TABLE_NAME, null, map);

        map.clear();

        map.put(DbContract.IntervalType.COLUMN_NAME_LABEL, AppliResources.getContext().getString(R.string.walk));
        map.put(DbContract.IntervalType.COLUMN_NAME_COLOR, "#439e43");
        map.put(DbContract.IntervalType.COLUMN_NAME_END_SND, "dingding");

        long walkType = db.insert(DbContract.IntervalType.TABLE_NAME, null, map);

        map.clear();

        map.put(DbContract.IntervalType.COLUMN_NAME_LABEL, AppliResources.getContext().getString(R.string.warmup));
        map.put(DbContract.IntervalType.COLUMN_NAME_COLOR, "#5f9c97");
        map.put(DbContract.IntervalType.COLUMN_NAME_END_SND, "dingding");

        long warmupType = db.insert(DbContract.IntervalType.TABLE_NAME, null, map);

        map.clear();

        // PLAN
        map.put(TrainingPlan.COLUMN_NAME_NAME, "Begginer Training Plan");
        map.put(TrainingPlan.COLUMN_NAME_DESCRIPTION, "A plan for people who want to start to run. " +
                "Yes, running an hour long without being breathless is possible. ");
        map.put(TrainingPlan.COLUMN_NAME_SOURCE, "http://www.jogging-running.com/article-323737.html");

        long planNum = db.insert(TrainingPlan.TABLE_NAME, null, map);

        map.clear();

        // SESSION and INTERVAL for beginner plan
        long[] type = new long[]{runType, walkType};

        Number[][] lstSessions = new Number[][]{
                new Integer[]{2, 2, 2, 2, 2, 2, 2, 3},
                new Integer[]{2, 2, 2, 2, 2, 2, 2, 2, 1, 3},
                new Integer[]{3, 2, 3, 2, 3, 2, 3, 3},
                new Integer[]{4, 2, 3, 2, 3, 2, 4, 3},
                new Integer[]{5, 2, 3, 2, 4, 2, 4, 3},
                new Integer[]{5, 2, 3, 2, 5, 2, 5, 3},
                new Integer[]{5, 2, 7, 2, 5, 2, 5, 3},
                new Integer[]{6, 2, 10, 2, 5, 1, 4, 3},
                new Integer[]{6, 2, 12, 2, 6, 2, 4, 3},
                new Integer[]{9, 1, 10, 2, 6, 2, 8, 3},
                new Integer[]{10, 2, 10, 2, 10, 2, 5, 3},
                new Integer[]{12, 2, 12, 2, 12, 2, 5, 3},
                new Integer[]{15, 2, 15, 2, 16, 3},
                new Integer[]{20, 2, 20, 2, 10, 3},
                new Integer[]{25, 2, 25, 2, 2, 3},
                new Integer[]{28, 4, 26, 3},
                new Integer[]{60, 3}
        };

        long types[] = new long[]{runType, walkType};
        insertSessions(db, planNum, warmupType, 10, types,
                lstSessions, AppliResources.getContext().getString(R.string.week));

        // SESSION and INTERVAL for test plan
        map.put(TrainingPlan.COLUMN_NAME_NAME, "Test Plan");
        map.put(TrainingPlan.COLUMN_NAME_DESCRIPTION, "Some tests");
        map.put(TrainingPlan.COLUMN_NAME_SOURCE, "");

        planNum = db.insert(TrainingPlan.TABLE_NAME, null, map);

        map.clear();

        lstSessions = new Number[][]{
                new Number[]{0.1, 0.05, 0.1, 0.02, 0.1, 0.05, 0.1, 0.05, 0.1, 0.05, 0.1}
        };

        insertSessions(db, planNum, warmupType, 0, types, lstSessions,
                AppliResources.getContext().getString(R.string.testPlan));
    }

    private void insertSessions(SQLiteDatabase db, long planNum, Long warmupType, Integer warmupSeconds, long[] types, Number[][] lstSessions, String labelPrefix) {
        ContentValues map = new ContentValues();
        for (int iSession = 0; iSession < lstSessions.length; iSession++) {

            // SESSION
            Number[] sessionDesc = lstSessions[iSession];

            map.put(TrainingSession.COLUMN_NAME_PLAN_ID, planNum);
            map.put(TrainingSession.COLUMN_NAME_NAME, labelPrefix + " " + (iSession + 1));
            map.put(TrainingSession.COLUMN_NAME_SESSION_ORDER, iSession + 1);

            long sessionId = db.insert(TrainingSession.TABLE_NAME, null, map);

            map.clear();

            // INTERVAL warm-up
            if (warmupType != null && warmupSeconds != null && warmupSeconds > 0) {
                map.put(Interval.COLUMN_NAME_SESSION_ID, sessionId);
                map.put(Interval.COLUMN_NAME_INTERVAL_ORDER, 0);
                map.put(Interval.COLUMN_NAME_MILLISEC, warmupSeconds * SECOND);
                map.put(Interval.COLUMN_NAME_TYPE_ID, warmupType);

                db.insert(Interval.TABLE_NAME, null, map);

                map.clear();
            }

            // INTERVAL
            int iType = 0;
            for (int i = 0; i < sessionDesc.length; i++) {
                if (sessionDesc[i].doubleValue() > 0.0d) {
                    map.put(Interval.COLUMN_NAME_SESSION_ID, sessionId);
                    map.put(Interval.COLUMN_NAME_INTERVAL_ORDER, i + 1);
                    map.put(Interval.COLUMN_NAME_MILLISEC, (long) (sessionDesc[i].doubleValue() * MINUTE));
                    map.put(Interval.COLUMN_NAME_TYPE_ID, types[iType]);

                    db.insert(Interval.TABLE_NAME, null, map);

                    map.clear();
                }
                iType = (iType + 1) % types.length;
            }
        }
    }

    public List<TrainingPlanDescription> getAllPlans() {
        List<TrainingPlanDescription> ret = null;

        Map<Long, IntervalTypeDescription> types = null;

        Cursor lst = getDatabase().rawQuery("SELECT "
                + TrainingPlan.COLUMN_NAME_ID + ", "
                + TrainingPlan.COLUMN_NAME_NAME + ", "
                + TrainingPlan.COLUMN_NAME_SOURCE + ", "
                + TrainingPlan.COLUMN_NAME_DESCRIPTION
                + " FROM " + TrainingPlan.TABLE_NAME, null);

        try {
            ret = new ArrayList<TrainingPlanDescription>(lst.getCount());
            boolean hasNext = lst.moveToFirst();
            while (hasNext) {
                TrainingPlanDescription plan = new TrainingPlanDescription(lst.getLong(0),
                        lst.getString(1),
                        lst.getString(2),
                        lst.getString(3));
                ret.add(plan);
                hasNext = lst.moveToNext();
            }
        } finally {
            lst.close();
        }

        return ret;
    }

    public TrainingPlanDescription getPlan(long planId) {
        TrainingPlanDescription ret = null;

        Cursor lst = getDatabase().rawQuery("SELECT "
                        + TrainingPlan.COLUMN_NAME_ID + ", "
                        + TrainingPlan.COLUMN_NAME_NAME + ", "
                        + TrainingPlan.COLUMN_NAME_SOURCE + ", "
                        + TrainingPlan.COLUMN_NAME_DESCRIPTION
                        + " FROM " + TrainingPlan.TABLE_NAME
                        + " WHERE " + TrainingPlan.COLUMN_NAME_ID + " = ?",
                new String[] {Long.toString(planId)} );

        try {
            if (lst.moveToFirst()) {
                ret = new TrainingPlanDescription(lst.getLong(0),
                        lst.getString(1),
                        lst.getString(2),
                        lst.getString(3));
            }
        } finally {
            lst.close();
        }

        return ret;
    }

    public TrainingSessionDescription getSession(long sessionId) {
        TrainingSessionDescription ret = null;

        Cursor lstSession = getDatabase().rawQuery("SELECT "
                        + TrainingSession.COLUMN_NAME_ID + ", "
                        + TrainingSession.COLUMN_NAME_SESSION_ORDER + ", "
                        + TrainingSession.COLUMN_NAME_NAME
                        + " FROM " + TrainingSession.TABLE_NAME
                        + " WHERE " + TrainingSession.COLUMN_NAME_ID + " = ?",
                new String[] {Long.toString(sessionId)} );

        try {
            if (lstSession.moveToFirst()) {
                ret = new TrainingSessionDescription(lstSession.getLong(0),
                        lstSession.getInt(1),
                        lstSession.getString(2));
            }
        } finally {
            lstSession.close();
        }

        return ret;
    }

    public List<TrainingSessionDescription> retrieveSessionsForPlan(long planId) {
        LockableArrayList<TrainingSessionDescription> ret = null;

        // Get the sessions and, for each the intervals
        Cursor lstSession = getDatabase().rawQuery("SELECT "
                        + TrainingSession.COLUMN_NAME_ID + ", "
                        + TrainingSession.COLUMN_NAME_SESSION_ORDER + ", "
                        + TrainingSession.COLUMN_NAME_NAME
                        + " FROM " + TrainingSession.TABLE_NAME
                        + " WHERE " + TrainingSession.COLUMN_NAME_PLAN_ID + " = ?"
                        + " ORDER BY " + TrainingSession.COLUMN_NAME_SESSION_ORDER,
                new String[] { Long.toString(planId) });

        try {
            ret = new LockableArrayList<TrainingSessionDescription>(lstSession.getCount());

            boolean hasNextSession = lstSession.moveToFirst();
            while (hasNextSession) {
                TrainingSessionDescription session =
                        new TrainingSessionDescription(lstSession.getLong(0),
                                lstSession.getInt(1),
                                lstSession.getString(2));
                ret.add(session);
                hasNextSession = lstSession.moveToNext();
            }
        } finally {
            lstSession.close();
        }

        return ret;
    }

    public List<IntervalDescription> retrieveIntervalsForSession(long sessionId) {
        Map<Long, IntervalTypeDescription> types = getIntervalTypeDescriptionMap();

        List<IntervalDescription> ret = new LockableArrayList<IntervalDescription>();

        Cursor lstInterval = getDatabase().rawQuery("SELECT "
                        + Interval.COLUMN_NAME_ID + ", "
                        + Interval.COLUMN_NAME_TYPE_ID + ", "
                        + Interval.COLUMN_NAME_INTERVAL_ORDER + ", "
                        + Interval.COLUMN_NAME_MILLISEC
                        + " FROM " + Interval.TABLE_NAME
                        + " WHERE " + Interval.COLUMN_NAME_SESSION_ID + " = ?",
                new String[] { Long.toString(sessionId) });

        boolean hasNextInterval = lstInterval.moveToFirst();
        while (hasNextInterval) {
            IntervalDescription interval =
                    new IntervalDescription(lstInterval.getLong(0),
                            types.get(lstInterval.getLong(1)),
                            lstInterval.getInt(2),
                            lstInterval.getLong(3));

            ret.add(interval);
            hasNextInterval = lstInterval.moveToNext();
        }
        lstInterval.close();

        return ret;
    }

    private Map<Long, IntervalTypeDescription> intervalTypesCache = null;

    private Map<Long, IntervalTypeDescription> getIntervalTypeDescriptionMap() {
        if (intervalTypesCache == null) {
            // Get the type descriptions
            Cursor lstTypes = getDatabase().rawQuery("SELECT "
                            + IntervalType.COLUMN_NAME_ID + ", "
                            + IntervalType.COLUMN_NAME_LABEL + ", "
                            + IntervalType.COLUMN_NAME_COLOR + ", "
                            + IntervalType.COLUMN_NAME_START_SND + ", "
                            + IntervalType.COLUMN_NAME_END_SND
                            + " FROM " + IntervalType.TABLE_NAME,
                    null);
            try {
                intervalTypesCache = new HashMap(lstTypes.getCount());

                boolean hasNextType = lstTypes.moveToFirst();
                while (hasNextType) {
                    IntervalTypeDescription type =
                            new IntervalTypeDescription(lstTypes.getLong(0), lstTypes.getString(1),
                                    Color.parseColor(lstTypes.getString(2)),
                                    lstTypes.getString(3) == null ? null
                                            : AppliResources.getContext().getResources().getIdentifier(lstTypes.getString(3), "raw", AppliResources.getContext().getPackageName()),
                                    lstTypes.getString(4) == null ? null
                                            : AppliResources.getContext().getResources().getIdentifier(lstTypes.getString(4), "raw", AppliResources.getContext().getPackageName()));

                    intervalTypesCache.put(type.getId(), type);

                    hasNextType = lstTypes.moveToNext();
                }
            } catch(RuntimeException e) {
                Log.e(LOG_TAG, "Exception while readin the interval types", e);
                intervalTypesCache = null;
            } finally {
                lstTypes.close();
            }
        }
        return intervalTypesCache;
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////:
    // PARAMETER

    public String getParameter(String paramName, String defaultVal) {
        Cursor c = getDatabase().rawQuery("SELECT " + Parameters.COLUMN_NAME_PARAM_VALUE
                + " FROM " + Parameters.TABLE_NAME
                + " WHERE " + Parameters.COLUMN_NAME_PARAM_NAME + " = ?",
                new String[] { paramName });

        String ret;
        if (c.moveToFirst()) {
            ret = c.getString(0);
        } else {
            ret = defaultVal;
        }

        return ret;
    }

    public void setParameter(String paramName, String val) {

        String prevVal = getParameter(paramName, null);
        if ((prevVal != null && prevVal.equals(val))
                || (prevVal == null && val == null)) {
            Log.d(AppliResources.LOG_TAG, "DB : Parameter " + paramName + " : No change [" + prevVal + "]");
            return;
        }

        SQLiteDatabase db = getDatabase();
        db.beginTransaction();

        Log.d(AppliResources.LOG_TAG, "DB : Parameter " + paramName + " : [" + prevVal + "] => [" + val + "]");

        try {
            if (val != null) {
                ContentValues map = new ContentValues();
                map.put(Parameters.COLUMN_NAME_PARAM_NAME, paramName);
                map.put(Parameters.COLUMN_NAME_PARAM_VALUE, val);
                if (prevVal == null) {
                    db.insert(Parameters.TABLE_NAME, null, map);
                } else {
                    db.update(Parameters.TABLE_NAME, map, Parameters.COLUMN_NAME_PARAM_NAME + " = ?", new String[]{paramName});
                }
            } else {
                db.delete(Parameters.TABLE_NAME, Parameters.COLUMN_NAME_PARAM_NAME + " = ?", new String[]{paramName});
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public Long getParameterLong(String paramName, Long defaultVal) {
        String val = getParameter(paramName, null);
        Long ret;
        if (val != null) {
            try {
                ret = Long.parseLong(val);
            } catch(ParseException e) {
                ret = defaultVal;
            }
        } else {
            ret = defaultVal;
        }
        return ret;
    }

    public void setParameterLong(String paramName, Long val) {
        if (val == null) {
            setParameter(paramName, null);
        } else {
            setParameter(paramName, Long.toString(val));
        }
    }

    public Integer getParameterInt(String paramName, Integer defaultVal) {
        String val = getParameter(paramName, null);
        Integer ret;
        if (val != null) {
            try {
                ret = Integer.parseInt(val);
            } catch(ParseException e) {
                ret = defaultVal;
            }
        } else {
            ret = defaultVal;
        }
        return ret;
    }

    public void setParameterInteger(String paramName, Integer val) {
        if (val == null) {
            setParameter(paramName, null);
        } else {
            setParameter(paramName, Integer.toString(val));
        }
    }

    public Boolean getParameterBoolean(String paramName, Boolean defaultVal) {
        String val = getParameter(paramName, null);
        Boolean ret;
        if (val != null) {
            try {
                ret = Boolean.parseBoolean(val);
            } catch(ParseException e) {
                ret = defaultVal;
            }
        } else {
            ret = defaultVal;
        }
        return ret;
    }

    public void setParameterBoolean(String paramName, Boolean val) {
        if (val == null) {
            setParameter(paramName, null);
        } else {
            setParameter(paramName, Boolean.toString(val));
        }
    }

}
