package Utilities.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Bumba.db";


    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    private void CreateTables(SQLiteDatabase db)
    {

        db.execSQL(Term.TermEntry.SQL_CREATE_TERM);
        db.execSQL(Task.TaskEntry.SQL_CREATE_TASK);
        db.execSQL(Worklog.WorklogEntry.SQL_CREATE_WORKLOG);

    }

    private void DeleteTables(SQLiteDatabase db)
    {
        db.execSQL(Term.TermEntry.SQL_DELETE_TERM);
        db.execSQL(Task.TaskEntry.SQL_DELETE_TASK);
        db.execSQL(Worklog.WorklogEntry.SQL_DELETE_WORKLOG);
    }

    public void onCreate(SQLiteDatabase db) {
        CreateTables(db);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        DeleteTables(db);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
