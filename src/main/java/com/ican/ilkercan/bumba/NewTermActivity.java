package com.ican.ilkercan.bumba;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.support.v4.app.DialogFragment;
import android.widget.Toast;

import Utilities.Database.DbHelper;
import Utilities.Database.Term;
import Utilities.General.UtilityFunctions;

public class NewTermActivity extends AppCompatActivity {

    private static int selectedYear;
    private static int selectedMonth;
    private static int selectedDay;

    private static String ENUMSTARTDATE = "1";
    private static String ENUMENDDATE = "2";
    private static String dateSelected = "0";

    private static TextView txtStartDate;
    private static TextView txtEndDate;
    private static TextView txtTermName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_term);

        InitButtons();

        txtStartDate = (TextView)findViewById(R.id.txtStartDate);
        txtEndDate = (TextView)findViewById(R.id.txtEndDate);
        txtTermName = (TextView)findViewById(R.id.txtName);
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

             if(dateSelected.equals(ENUMSTARTDATE)) {
                 txtStartDate.setText(UtilityFunctions.GetDate(selectedYear, selectedMonth, selectedDay, false));
             }
             else
             {
                 txtEndDate.setText(UtilityFunctions.GetDate(selectedYear, selectedMonth, selectedDay, false));
             }

        }

    }

    private void InitButtons()
    {
        ImageButton btnSelectStartDate = (ImageButton)findViewById(R.id.btnSelectStartDate);
        btnSelectStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateSelected = ENUMSTARTDATE;
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });


        ImageButton btnSelectEndDate = (ImageButton)findViewById(R.id.btnSelectEndDate);
        btnSelectEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateSelected = ENUMENDDATE;
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

        Button btnSaveTerm = findViewById(R.id.btnSaveNewTerm);
        btnSaveTerm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DbHelper mDbHelper = new DbHelper(NewTermActivity.this);

                SQLiteDatabase db = mDbHelper.getWritableDatabase();

                ContentValues values = new ContentValues();

                values.put(Term.TermEntry.COLUMN_NAME_NAME, txtTermName.getText().toString());
                values.put(Term.TermEntry.COLUMN_NAME_STARTDATE, txtStartDate.getText().toString());
                values.put(Term.TermEntry.COLUMN_NAME_ENDDATE, txtEndDate.getText().toString());

                long newRowId = db.insert(Term.TermEntry.TABLE_NAME, null, values);

                Toast.makeText(NewTermActivity.this,String.valueOf(newRowId), Toast.LENGTH_SHORT).show();

            }
        });
    }

}
