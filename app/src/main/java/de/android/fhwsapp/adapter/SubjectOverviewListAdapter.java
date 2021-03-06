package de.android.fhwsapp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import de.android.fhwsapp.R;
import de.android.fhwsapp.objects.Subject;


public class SubjectOverviewListAdapter extends BaseAdapter {

    private Context context;
    private List<Subject> objects;

    public SubjectOverviewListAdapter(Context context, List<Subject> objects) {
        this.context = context;
        this.objects = objects;
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = LayoutInflater.from(context).inflate(R.layout.subject_overview_list_adapter, parent, false);

        TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
        TextView tvRoom = (TextView) convertView.findViewById(R.id.tvRoom);
        TextView tvTeacher = (TextView) convertView.findViewById(R.id.tvTeacher);
        TextView tvType = (TextView) convertView.findViewById(R.id.tvType);

        LinearLayout llBackground = (LinearLayout) convertView.findViewById(R.id.llBackground);

        Subject subject = objects.get(position);

        tvName.setText(subject.getSubjectName());
        tvType.setText(subject.getType());
        tvRoom.setText("Raum: " + subject.getRoom());
        tvTeacher.setText("Dozent: " + subject.getTeacher());

        if (position == 0 || position % 2 == 0)
            llBackground.setBackgroundColor(Color.parseColor("#DDDDDD"));
        else
            llBackground.setBackgroundColor(Color.parseColor("#ED6E00"));


        return convertView;
    }
}

