package de.android.fhwsapp.Timetable;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import de.android.fhwsapp.R;

public class MyListAdapter extends BaseAdapter {

    private Context context;
    private List<Subject> objects;

    public MyListAdapter(Context context, List<Subject> objects) {
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

        //if(convertView==null){

        convertView = LayoutInflater.from(context).inflate(R.layout.activity_list_adapter, parent, false);

        TextView tvTime = (TextView) convertView.findViewById(R.id.tvTime);
        TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
        TextView tvRoom = (TextView) convertView.findViewById(R.id.tvRoom);
        TextView tvTeacher = (TextView) convertView.findViewById(R.id.tvTeacher);

        tvTime.setSelected(true);
        tvName.setSelected(true);
        tvRoom.setSelected(true);
        tvTeacher.setSelected(true);

        Subject subject = objects.get(position);

        tvTime.setText(subject.getTimeStart() + " - " + subject.getTimeEnd());
        tvName.setText(subject.getType() + " " + subject.getSubjectName());
        if (subject.getStudiengang() != null && !subject.getStudiengang().equals(""))
            tvName.setText(tvName.getText() + " [" + subject.getStudiengang() + "]");

        tvRoom.setText(subject.getRoom());
        tvTeacher.setText(subject.getTeacher());


        if (position == 0 || position % 2 == 0)
            convertView.setBackgroundColor(Color.parseColor("#AAAAAA"));

        //}
        return convertView;
    }
}
