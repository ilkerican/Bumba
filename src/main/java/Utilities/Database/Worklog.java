package Utilities.Database;

/**
 * Created by ilkercan on 05/12/2017.
 */

import android.provider.BaseColumns;

/**
 * Created by FB005028 on 04/10/17.
 */

public final class Worklog {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.



    private Worklog() {}



    /* Inner class that defines the table contents */
    public static class WorklogEntry implements BaseColumns {

        // Field names :
        public static final String TABLE_NAME = "worklog";
        public static final String COLUMN_NAME_TASK_ID = "taskid";
        public static final String COLUMN_NAME_LOG_DATE = "logdate";
        public static final String COLUMN_NAME_POINT = "point";

        // Create table script
        public static final String SQL_CREATE_WORKLOG =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY," +
                        COLUMN_NAME_TASK_ID + " INTEGER," +
                        COLUMN_NAME_LOG_DATE + " TEXT," +
                        COLUMN_NAME_POINT + " INTEGER)";

        public static final String SQL_DELETE_WORKLOG =
                "DROP TABLE IF EXISTS " + TABLE_NAME;
    }


}
