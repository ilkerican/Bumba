package com.ican.ilkercan.bumba;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import Utilities.Database.DbHelper;
import Utilities.Database.Task;
import Utilities.Database.TaskRecord;
import Utilities.Database.Worklog;
import Utilities.General.UtilityFunctions;


public class FragmentHome extends Fragment {
    private OnFragmentInteractionListener mListener;

    protected List<TaskRecord> data;

    ProgressDialog progressDialog;
    ListView listView;
    final int NOID_JUST_TO_REFRESH = 0;
    final int NO_WORKLOG_ID = 0;

    public FragmentHome() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        progressDialog = UtilityFunctions.GetProgressDialog((AppCompatActivity) getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_home, container, false);


        listView = (ListView) rootView.findViewById(R.id.tasks_listview);


        attemptGetNewsData(rootView);

        return rootView;


    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void attemptGetNewsData(View rootView) {

        UtilityFunctions.ShowProgressDialog(progressDialog, true);
        FillList(rootView);

    }



    private void CalculatePoints(int id, boolean isdeleted)
    {
        int sum = 0;
        int numpoints = 0;


        if(data != null)
        {
            if(data.size() > 0)
            {
                int len = data.size();
                for(int i = 0; i < len; i++)
                {
                    if(id != NOID_JUST_TO_REFRESH) {
                        if (((TaskRecord) data.get(i)).getId() == id) {
                            if (isdeleted) {
                                ((TaskRecord) data.get(i)).setWorklogpoints(0);
                            } else {
                                numpoints = ((TaskRecord) data.get(i)).getPoint();
                                ((TaskRecord) data.get(i)).setWorklogpoints(numpoints);
                            }
                        }
                    }

                    sum = sum + ((TaskRecord)data.get(i)).getWorklogpoints();
                }
            }
        }

        InitToolBar(sum);
    }


    private void ReadData(final View rootView) {
        DbHelper mDbHelper = new DbHelper(getActivity());

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String MY_QUERY = "SELECT T._ID, T.NAME, T.POINT, T.TERMID, T.DEADLINE, T.TOTALPOINTS, " +
                " ifnull((SELECT point FROM WORKLOG WHERE taskid = T._ID AND LOGDATE = ? ),0) AS COLLECTEDPOINTS," +
                " ifnull((SELECT _id FROM WORKLOG WHERE taskid = T._ID AND LOGDATE = ? ),0) AS LOGID  FROM TASK T";

        Cursor cursor = db.rawQuery(MY_QUERY, new String[]{UtilityFunctions.GetCurrentDate(false), UtilityFunctions.GetCurrentDate(false)});


        data = new ArrayList<TaskRecord>();

        while (cursor.moveToNext()) {

            int itemId = cursor.getInt(cursor.getColumnIndexOrThrow(Task.TaskEntry._ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(Task.TaskEntry.COLUMN_NAME_NAME));
            int termid = cursor.getInt(cursor.getColumnIndexOrThrow(Task.TaskEntry.COLUMN_NAME_TERM_ID));
            String deadline = cursor.getString(cursor.getColumnIndexOrThrow(Task.TaskEntry.COLUMN_NAME_DEADLINE));
            int point = cursor.getInt(cursor.getColumnIndexOrThrow(Task.TaskEntry.COLUMN_NAME_POINT));
            int totalpoints = cursor.getInt(cursor.getColumnIndexOrThrow(Task.TaskEntry.COLUMN_NAME_TOTAL_POINTS));

            int worklogid = cursor.getInt(cursor.getColumnIndexOrThrow("LOGID"));
            int worklogpoints = cursor.getInt(cursor.getColumnIndexOrThrow("COLLECTEDPOINTS"));

            TaskRecord rec = new TaskRecord();

            rec.setId(itemId);
            rec.setName(name);
            rec.setTermid(termid);
            rec.setDeadline(deadline);
            rec.setPoint(point);
            rec.setTotalpoints(totalpoints);

            rec.setWorklogid(worklogid);
            rec.setWorklogpoints(worklogpoints);

            data.add(rec);
        }

        cursor.close();
    }

    private void CollectData(final View rootView) {
        DbHelper mDbHelper = new DbHelper(getActivity());

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        //Select all fields in the table
        String[] projection = {
                Task.TaskEntry._ID,
                Task.TaskEntry.COLUMN_NAME_NAME,
                Task.TaskEntry.COLUMN_NAME_TERM_ID,
                Task.TaskEntry.COLUMN_NAME_DEADLINE,
                Task.TaskEntry.COLUMN_NAME_POINT,
                Task.TaskEntry.COLUMN_NAME_TOTAL_POINTS
        };

        //Select all rows ID > 0, all rows :))
        String selection = Task.TaskEntry._ID + " > ?";
        String[] selectionArgs = {"0"};


        String sortOrder = Task.TaskEntry._ID + " ASC";

        Cursor cursor = db.query(
                Task.TaskEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        data = new ArrayList<TaskRecord>();
        while (cursor.moveToNext()) {

            int itemId = cursor.getInt(cursor.getColumnIndexOrThrow(Task.TaskEntry._ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(Task.TaskEntry.COLUMN_NAME_NAME));
            int termid = cursor.getInt(cursor.getColumnIndexOrThrow(Task.TaskEntry.COLUMN_NAME_TERM_ID));
            String deadline = cursor.getString(cursor.getColumnIndexOrThrow(Task.TaskEntry.COLUMN_NAME_DEADLINE));
            int point = cursor.getInt(cursor.getColumnIndexOrThrow(Task.TaskEntry.COLUMN_NAME_POINT));
            int totalpoints = cursor.getInt(cursor.getColumnIndexOrThrow(Task.TaskEntry.COLUMN_NAME_TOTAL_POINTS));

            TaskRecord rec = new TaskRecord();

            rec.setId(itemId);
            rec.setName(name);
            rec.setTermid(termid);
            rec.setDeadline(deadline);
            rec.setPoint(point);
            rec.setTotalpoints(totalpoints);

            data.add(rec);
        }

        cursor.close();

    }


    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
    }

    private void InitToolBar(int sum) {


        ActionBar mActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(true);
        mActionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(getActivity());
        View mCustomView = mInflater.inflate(R.layout.actionbar_home, null);
        TextView mDate = (TextView) mCustomView.findViewById(R.id.ab_design_home_txtDate);
        mDate.setText(UtilityFunctions.GetCurrentDate(false));

        TextView mPoints = (TextView) mCustomView.findViewById(R.id.ab_design_home_txtPoints);
        mPoints.setText(String.valueOf(sum) + " points");

        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);

    }

    private void FillList(View rootView) {

        ReadData(rootView);

        ListView listView = (ListView) rootView.findViewById(R.id.lvActivities);

        if (data != null) {

            if (data.size() > 0) {
                ArrayAdapter<TaskRecord> tasksArrayAdapter = new TaskArrayAdapter(getActivity(), 0, data);

                listView.setAdapter(tasksArrayAdapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        displayDetail(data.get(i));
                    }
                });
            } else {
                listView.setVisibility(View.GONE);
            }
        } else {
            listView.setVisibility(View.GONE);

        }

        CalculatePoints(NOID_JUST_TO_REFRESH, false);

        UtilityFunctions.ShowProgressDialog(progressDialog, false);

    }

    private void displayDetail(TaskRecord taskRecord) {

        /*

        Intent intent = new Intent(getActivity(), TermDetailActivity.class);

        intent.putExtra(Constants.EXTRA_CODE_FOR_TERMDETAILID, taskRecord.getId());

        startActivityForResult(intent, Constants.ARC_TERMDETAIL_ACTIVITY);

        */

    }


    class TaskArrayAdapter extends ArrayAdapter<TaskRecord> {

        Context context;
        List<TaskRecord> objects;

        public TaskArrayAdapter(Context context, int resource, List<TaskRecord> objects) {
            super(context, resource, objects);

            this.context = context;
            this.objects = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final TaskRecord taskRecord = objects.get(position);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.activity_item, null);

            TextView tvName = (TextView) view.findViewById(R.id.txtActivity);
            tvName.setText(taskRecord.getName());

            Button btnComplete = (Button) view.findViewById(R.id.btnComplete);

            int logid = taskRecord.getWorklogid();

            btnComplete.setTag(logid);

            Drawable top = getResources().getDrawable(R.drawable.ic_done_black_24dp);

            if (Integer.valueOf(btnComplete.getTag().toString()) == NO_WORKLOG_ID) {
                top = getResources().getDrawable(R.drawable.ic_close_black_24dp);
            }

            btnComplete.setCompoundDrawablesWithIntrinsicBounds(null, top, null, null);

            btnComplete.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {


                    if (view.getTag() != null) {

                        String deleteId = String.valueOf(view.getTag());

                        // Clear tag..ok
                        view.setTag(null);

                        // Change button picture ..ok
                        Drawable top = getResources().getDrawable(R.drawable.ic_close_black_24dp);
                        ((Button) view).setCompoundDrawablesWithIntrinsicBounds(null, top, null, null);

                        // Implement delete here...ok

                        DbHelper mDbHelper = new DbHelper((AppCompatActivity) getActivity());

                        SQLiteDatabase db = mDbHelper.getWritableDatabase();

                        db.delete(Worklog.WorklogEntry.TABLE_NAME, "_ID" + "=" + deleteId, null);

                        CalculatePoints(taskRecord.getId(), true);


                    } else {
                        DbHelper mDbHelper = new DbHelper((AppCompatActivity) getActivity());

                        SQLiteDatabase db = mDbHelper.getWritableDatabase();

                        ContentValues values = new ContentValues();

                        values.put(Worklog.WorklogEntry.COLUMN_NAME_TASK_ID, taskRecord.getId());
                        values.put(Worklog.WorklogEntry.COLUMN_NAME_LOG_DATE, UtilityFunctions.GetCurrentDate(false));
                        values.put(Worklog.WorklogEntry.COLUMN_NAME_POINT, taskRecord.getPoint());

                        long newRowId = db.insert(Worklog.WorklogEntry.TABLE_NAME, null, values);

                        Drawable top = getResources().getDrawable(R.drawable.ic_done_black_24dp);
                        ((Button) view).setCompoundDrawablesWithIntrinsicBounds(null, top, null, null);

                        CalculatePoints(taskRecord.getId(), false);

                        view.setTag(newRowId);

                    }

                    return false;
                }
            });


            /*
            btnComplete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    UtilityFunctions.ShowMessage((AppCompatActivity) getActivity(), "Short clicked...");
                }
            });
            */

            /*

            TextView tvTaskDeadline = (TextView) view.findViewById(R.id.txtDeadline);
            tvTaskDeadline.setText(taskRecord.getDeadline());

            TextView tvTermName = (TextView) view.findViewById(R.id.txtTaskTermName);
            tvTermName.setText(String.valueOf(taskRecord.getTermid()));

            TextView tvPointValue = (TextView) view.findViewById(R.id.txtTaskPoint);
            tvPointValue.setText(String.valueOf(taskRecord.getPoint()));

            TextView tvPointsCollected = (TextView) view.findViewById(R.id.txtTaskCollectedPoints);
            tvPointsCollected.setText(String.valueOf(taskRecord.getTotalpoints()));
            */

            return view;
        }
    }

}