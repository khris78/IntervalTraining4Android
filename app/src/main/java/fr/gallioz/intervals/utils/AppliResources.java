package fr.gallioz.intervals.utils;

import android.content.Context;

import fr.gallioz.intervals.db.DbAccess;

public abstract class AppliResources {

    public static final String LOG_TAG = "IntervalTraining";

    private static Context context;

    private static DbAccess dbAccess = null;

    public static void initialize(Context ctx) {
        context = ctx;
    }

    public static Context getContext() {
        return context;
    }

    public static DbAccess getDbAccess() {
        return dbAccess;
    }

    public static void setDbAccess(DbAccess dbAccess) {
        AppliResources.dbAccess = dbAccess;
    }
}
