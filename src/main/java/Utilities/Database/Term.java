package Utilities.Database;

/**
 * Created by ilkercan on 01/12/2017.
 */

import android.provider.BaseColumns;

/**
 * Created by FB005028 on 04/10/17.
 */

public final class Term {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.



    private Term() {}



    /* Inner class that defines the table contents */
    public static class TermEntry implements BaseColumns {

        // Field names :
        public static final String TABLE_NAME = "term";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_STARTDATE = "startdate";
        public static final String COLUMN_NAME_ENDDATE = "enddate";

        // Create table script
        public static final String SQL_CREATE_TERM =
                "CREATE TABLE " + Term.TermEntry.TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY," +
                        COLUMN_NAME_NAME + " TEXT," +
                        COLUMN_NAME_STARTDATE + " TEXT," +
                        COLUMN_NAME_ENDDATE + " TEXT)";

        public static final String SQL_DELETE_TERM =
                "DROP TABLE IF EXISTS " + TABLE_NAME;
    }


}
