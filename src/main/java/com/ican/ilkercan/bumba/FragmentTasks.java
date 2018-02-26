package com.ican.ilkercan.bumba;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import Utilities.Database.DbHelper;
import Utilities.Database.Task;
import Utilities.Database.TaskRecord;
import Utilities.Database.TermRecord;
import Utilities.General.Constants;
import Utilities.General.UtilityFunctions;
import Utilities.Database.Term;

public class FragmentTasks extends Fragment {
    private OnFragmentInteractionListener mListener;

    protected List<TaskRecord> data;
    SwipeRefreshLayout swiperefresh;

    ProgressDialog progressDialog;
    ListView listView;



    public FragmentTasks() {

    }


    private void InitSwipeRefresh(final View rootView) {

        swiperefresh.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {

                        FillList(rootView);
                    }
                }
        );

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        progressDialog = UtilityFunctions.GetProgressDialog((AppCompatActivity) getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_tasks, container, false);


        swiperefresh = (SwipeRefreshLayout) rootView.findViewById(R.id.swiperefreshpage);

        listView = (ListView) rootView.findViewById(R.id.tasks_listview);

        InitToolBar();

        InitSwipeRefresh(rootView);

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

    private void CollectData(final View rootView)
    {
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
        String[] selectionArgs = { "0" };




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
        while(cursor.moveToNext()) {

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

    private void InitToolBar() {


        ActionBar mActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(true);
        mActionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(getActivity());
        View mCustomView = mInflater.inflate(R.layout.actionbar_other, null);
        TextView mTitleTextView = (TextView) mCustomView.findViewById(R.id.ab_design_home_title_text);
        mTitleTextView.setText(getString(R.string.a_tasks_page_title));
        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);

    }
    private void FillList(View rootView) {

        CollectData(rootView);

        ListView listView = (ListView) rootView.findViewById(R.id.tasks_listview);

        if(data != null) {

            if (data.size() > 0) {
                ArrayAdapter<TaskRecord> tasksArrayAdapter = new TaskArrayAdapter(getActivity(), 0, data);

                listView.setAdapter(tasksArrayAdapter);

                swiperefresh.setRefreshing(false);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        displayDetail(data.get(i));
                    }
                });
            } else {
                listView.setVisibility(View.GONE);
            }
        }
        else
        {
            listView.setVisibility(View.GONE);

        }
        UtilityFunctions.ShowProgressDialog(progressDialog, false);

    }

    private void displayDetail(TaskRecord taskRecord) {


        Intent intent = new Intent(getActivity(), TermDetailActivity.class);

        intent.putExtra(Constants.EXTRA_CODE_FOR_TERMDETAILID, taskRecord.getId());

        startActivityForResult(intent, Constants.ARC_TERMDETAIL_ACTIVITY);



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

            TaskRecord taskRecord = objects.get(position);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.task_list_item, null);

            TextView tvName = (TextView) view.findViewById(R.id.txtTaskName);
            tvName.setText(taskRecord.getName());

            TextView tvTaskDeadline = (TextView) view.findViewById(R.id.txtDeadline);
            tvTaskDeadline.setText(taskRecord.getDeadline());

            TextView tvTermName = (TextView) view.findViewById(R.id.txtTaskTermName);
            tvTermName.setText(String.valueOf(taskRecord.getTermid()));

            TextView tvPointValue = (TextView) view.findViewById(R.id.txtTaskPoint);
            tvPointValue.setText(String.valueOf(taskRecord.getPoint()));

            TextView tvPointsCollected = (TextView) view.findViewById(R.id.txtTaskCollectedPoints);
            tvPointsCollected.setText(String.valueOf(taskRecord.getTotalpoints()));

            return view;
        }
    }

}