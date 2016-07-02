package fr.gallioz.intervals.db;


import android.provider.BaseColumns;

/** Contract for the storage of the database.
 * See https://developer.android.com/training/basics/data-storage/databases.html */
public abstract class DbContract {

    /** Table to store some properties of the application, for instance the current state of
     * the chronometer. */
    public static abstract class Parameters implements BaseColumns {
        public static final String TABLE_NAME = "parameters";
        public static final String COLUMN_NAME_PARAM_NAME = "name";
        public static final String COLUMN_NAME_PARAM_VALUE = "value";
    }

    public static final String CREATE_PARAMETERS_TABLE = "CREATE TABLE " + Parameters.TABLE_NAME + "("
            + Parameters.COLUMN_NAME_PARAM_NAME + " VARCHAR(200) PRIMARY KEY,"
            + Parameters.COLUMN_NAME_PARAM_VALUE + " VARCHAR(1000) "
            + ")";


    /** Table to store the plan names, and some other informations. */
    public static abstract class TrainingPlan implements BaseColumns {
        public static final String TABLE_NAME = "training_plan";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_SOURCE = "source";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
    }

    public static final String CREATE_TRAINING_PLAN_TABLE = "CREATE TABLE " + TrainingPlan.TABLE_NAME + "("
            + TrainingPlan.COLUMN_NAME_ID + " INTEGER PRIMARY KEY,"
            + TrainingPlan.COLUMN_NAME_NAME + " VARCHAR(1000), "
            + TrainingPlan.COLUMN_NAME_SOURCE + " VARCHAR(2000), "
            + TrainingPlan.COLUMN_NAME_DESCRIPTION + " VARCHAR(10000) "
            + ")";

    /** Table to store each training session in a plan. */
    public static abstract class TrainingSession implements BaseColumns {
        public static final String TABLE_NAME = "training_session";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_PLAN_ID = "plan_id";
        public static final String COLUMN_NAME_SESSION_ORDER = "session_order";
        public static final String COLUMN_NAME_NAME = "name";
    }

    public static final String CREATE_TRAINING_SESSION_TABLE = "CREATE TABLE " + TrainingSession.TABLE_NAME + "("
            + TrainingSession.COLUMN_NAME_ID + " INTEGER PRIMARY KEY,"
            + TrainingSession.COLUMN_NAME_PLAN_ID + " INTEGER, "
            + TrainingSession.COLUMN_NAME_SESSION_ORDER + " INTEGER, "
            + TrainingSession.COLUMN_NAME_NAME + " VARCHAR(2000) "
            + ")";


    /** Table to store the plan names, and some other informations. */
    public static abstract class IntervalType implements BaseColumns {
        public static final String TABLE_NAME = "interval_type";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_LABEL = "label";
        public static final String COLUMN_NAME_COLOR = "color";
        public static final String COLUMN_NAME_START_SND = "start_snd";
        public static final String COLUMN_NAME_END_SND = "end_snd";
    }

    public static final String CREATE_INTERVAL_TYPE_TABLE = "CREATE TABLE " + IntervalType.TABLE_NAME + "("
            + IntervalType.COLUMN_NAME_ID + " INTEGER PRIMARY KEY,"
            + IntervalType.COLUMN_NAME_LABEL + " VARCHAR(1000), "
            + IntervalType.COLUMN_NAME_COLOR + " VARCHAR(9), "
            + IntervalType.COLUMN_NAME_START_SND + " VARCHAR(100), "
            + IntervalType.COLUMN_NAME_END_SND + " VARCHAR(100) "
            + ")";

    /** Table to store the interval descriptions. */
    public static abstract class Interval implements BaseColumns {
        public static final String TABLE_NAME = "interval";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_SESSION_ID = "session_id";
        public static final String COLUMN_NAME_INTERVAL_ORDER = "interval_order";
        public static final String COLUMN_NAME_TYPE_ID = "type_id";
        public static final String COLUMN_NAME_MILLISEC = "millisec";
    }

    public static final String CREATE_INTERVAL_TABLE = "CREATE TABLE " + Interval.TABLE_NAME + "("
            + Interval.COLUMN_NAME_ID + " INTEGER PRIMARY KEY,"
            + Interval.COLUMN_NAME_SESSION_ID + " INTEGER,"
            + Interval.COLUMN_NAME_INTERVAL_ORDER + " INTEGER, "
            + Interval.COLUMN_NAME_TYPE_ID + " INTEGER, "
            + Interval.COLUMN_NAME_MILLISEC + " INTEGER "
            + ")";

}
