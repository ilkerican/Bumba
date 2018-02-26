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
        import Utilities.Database.TermRecord;
        import Utilities.General.Constants;
        import Utilities.General.UtilityFunctions;
        import Utilities.Database.Term;

public class FragmentTerms extends Fragment {
    private OnFragmentInteractionListener mListener;

    protected List<TermRecord> data;
    SwipeRefreshLayout swiperefresh;
    int minId = 0;
    int maxId = 0;
    int direction = 0;
    ProgressDialog progressDialog;
    ListView listView;



    public FragmentTerms() {

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


        View rootView = inflater.inflate(R.layout.fragment_terms, container, false);


        swiperefresh = (SwipeRefreshLayout) rootView.findViewById(R.id.swiperefreshpage);

        listView = (ListView) rootView.findViewById(R.id.terms_listview);
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

        data = new ArrayList<TermRecord>();
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
        mTitleTextView.setText(getString(R.string.a_terms_page_title));
        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);

    }
    private void FillList(View rootView) {

        CollectData(rootView);

        ListView listView = (ListView) rootView.findViewById(R.id.terms_listview);

        if(data != null) {

            if (data.size() > 0) {
                ArrayAdapter<TermRecord> newsArrayAdapter = new TermArrayAdapter(getActivity(), 0, data);

                listView.setAdapter(newsArrayAdapter);

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

    private void displayDetail(TermRecord term) {

        Intent intent = new Intent(getActivity(), TermDetailActivity.class);

        intent.putExtra(Constants.EXTRA_CODE_FOR_TERMDETAILID, term.getId());

        startActivityForResult(intent, Constants.ARC_TERMDETAIL_ACTIVITY);

    }


    class TermArrayAdapter extends ArrayAdapter<TermRecord> {

        Context context;
        List<TermRecord> objects;

        public TermArrayAdapter(Context context, int resource, List<TermRecord> objects) {
            super(context, resource, objects);

            this.context = context;
            this.objects = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            TermRecord termRecord = objects.get(position);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.term_list_item, null);

            TextView tvName = (TextView) view.findViewById(R.id.txtName);
            tvName.setText(termRecord.getName());

            TextView tvStartDate = (TextView) view.findViewById(R.id.txtStartDate);
            tvStartDate.setText(termRecord.getStartdate());

            TextView tvEndDate = (TextView) view.findViewById(R.id.txtEndDate);
            tvEndDate.setText(termRecord.getEnddate());

            return view;
        }
    }

}