package Utilities.Database;

/**
 * Created by ilkercan on 05/12/2017.
 */

import android.provider.BaseColumns;

/**
 * Created by FB005028 on 04/10/17.
 */

public final class Task {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.



    private Task() {}



    /* Inner class that defines the table contents */
    public static class TaskEntry implements BaseColumns {

        // Field names :
        public static final String TABLE_NAME = "task";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_TERM_ID = "termid";
        public static final String COLUMN_NAME_DEADLINE = "deadline";
        public static final String COLUMN_NAME_POINT = "point";
        public static final String COLUMN_NAME_TOTAL_POINTS = "totalpoints";

        // Create table script
        public static final String SQL_CREATE_TASK =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY," +
                        COLUMN_NAME_NAME + " TEXT," +
                        COLUMN_NAME_TERM_ID + " INTEGER," +
                        COLUMN_NAME_DEADLINE + " TEXT," +
                        COLUMN_NAME_POINT + " INTEGER," +
                        COLUMN_NAME_TOTAL_POINTS + " INTEGER)";

        public static final String SQL_DELETE_TASK =
                "DROP TABLE IF EXISTS " + TABLE_NAME;
    }


}
