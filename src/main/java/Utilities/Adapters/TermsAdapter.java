package Utilities.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.ican.ilkercan.bumba.R;
import java.util.List;
import Utilities.Database.TermRecord;


public class TermsAdapter extends ArrayAdapter<TermRecord> {

    Context context;
    List<TermRecord> objects;

    public TermsAdapter(Context context, int resource, List<TermRecord> objects){
        super(context, resource, objects);

        this.context = context;
        this.objects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        TermRecord termRecord = objects.get(position);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.spinner_row, null);

        TextView tvItem = (TextView) view.findViewById(R.id.option);
        tvItem.setText(termRecord.getName());
        view.setTag(termRecord.getId());
        return view;
    }
    public View getDropDownView(int position, View convertView, ViewGroup parent) {

        TermRecord termRecord = objects.get(position);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.spinner_row, null);

        TextView tvItem = (TextView) view.findViewById(R.id.option);
        tvItem.setText(termRecord.getName());
        view.setTag(termRecord.getId());
        return view;
    }
}