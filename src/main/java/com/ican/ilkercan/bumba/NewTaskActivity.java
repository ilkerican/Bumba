package com.ican.ilkercan.bumba;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import Utilities.Adapters.TermsAdapter;
import Utilities.Database.DbHelper;
import Utilities.Database.Task;
import Utilities.Database.TaskRecord;
import Utilities.Database.Term;
import Utilities.Database.TermRecord;
import Utilities.General.UtilityFunctions;

public class NewTaskActivity extends AppCompatActivity {

    private static int ZEROPOINTS = 0;
    private static String NODATE = "NONE";

    private static TextView txtName;
    private static TextView txtDeadline;
    private static TextView txtPoint;

    private static int selectedYear;
    private static int selectedMonth;
    private static int selectedDay;

    List<TermRecord> termRecordList;

    Spinner spTerms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);

        InitControls();

        InitTermsSpinner();

        InitButtons();
    }

    private void InitTermsSpinner()
    {
        DbHelper mDbHelper = new DbHelper(this);

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        //Select all fields in the table
        String[] projection = {
                Term.TermEntry._ID,
                Term.TermEntry.COLUMN_NAME_NAME,
                Term.TermEntry.COLUMN_NAME_STARTDATE,
                Term.TermEntry.COLUMN_NAME_ENDDATE
        };

        //Select all rows ID > 0, all rows :))
        String selection = Term.TermEntry._ID + " > ?";
        String[] selectionArgs = { "0" };

        String sortOrder = Term.TermEntry._ID + " ASC";

        Cursor cursor = db.query(
                Term.TermEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        termRecordList = new ArrayList<TermRecord>();
        while(cursor.moveToNext()) {

            int itemId = cursor.getInt(cursor.getColumnIndexOrThrow(Term.TermEntry._ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(Term.TermEntry.COLUMN_NAME_NAME));
            String startdate = cursor.getString(cursor.getColumnIndexOrThrow(Term.TermEntry.COLUMN_NAME_STARTDATE));
            String enddate = cursor.getString(cursor.getColumnIndexOrThrow(Term.TermEntry.COLUMN_NAME_ENDDATE));

            TermRecord rec = new TermRecord();

            rec.setId(itemId);
            rec.setName(name);
            rec.setStartdate(startdate);
            rec.setEnddate(enddate);

            termRecordList.add(rec);
        }
        cursor.close();

        if(termRecordList != null) {
            TermsAdapter adapter = new TermsAdapter(NewTaskActivity.this, 0, termRecordList);
            spTerms.setAdapter(adapter);
        }
    }

    private void InitControls()
    {
        txtName = (TextView)findViewById(R.id.txtTaskName);
        txtDeadline = (TextView)findViewById(R.id.txtDeadline);
        txtPoint = (TextView)findViewById(R.id.txtPoint);
        spTerms = (Spinner)findViewById(R.id.spTermsSpinner);
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            final java.util.Calendar c = java.util.Calendar.getInstance();
            int year = c.get(java.util.Calendar.YEAR);
            int month = c.get(java.util.Calendar.MONTH);
            int day = c.get(java.util.Calendar.DAY_OF_MONTH);


            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {

            // NOTE : date picker month returns 1 less than the selected month.
            // So add + 1 to get the correct month.

            selectedYear = year;
            selectedMonth = month + 1;
            selectedDay = day;

            txtDeadline.setText(UtilityFunctions.GetDate(selectedYear, selectedMonth, selectedDay, false));

        }

    }

    private void InitButtons()
    {
        ImageButton btnSelectDeadline = (ImageButton)findViewById(R.id.btnSelectDeadline);
        btnSelectDeadline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DialogFragment newFragment = new NewTaskActivity.DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });


        Button btnSaveTask = findViewById(R.id.btnSaveNewTask);
        btnSaveTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DbHelper mDbHelper = new DbHelper(NewTaskActivity.this);

                SQLiteDatabase db = mDbHelper.getWritableDatabase();

                ContentValues values = new ContentValues();

                values.put(Task.TaskEntry.COLUMN_NAME_NAME, txtName.getText().toString());
                values.put(Task.TaskEntry.COLUMN_NAME_DEADLINE, NODATE);
                values.put(Task.TaskEntry.COLUMN_NAME_POINT, Integer.valueOf(txtPoint.getText().toString()));
                values.put(Task.TaskEntry.COLUMN_NAME_TOTAL_POINTS, ZEROPOINTS); //No points collected initially
                values.put(Task.TaskEntry.COLUMN_NAME_TERM_ID, termRecordList.get(spTerms.getSelectedItemPosition()).getId());

                long newRowId = db.insert(Task.TaskEntry.TABLE_NAME, null, values);

                Toast.makeText(NewTaskActivity.this,String.valueOf(newRowId), Toast.LENGTH_SHORT).show();

            }
        });
    }
}
