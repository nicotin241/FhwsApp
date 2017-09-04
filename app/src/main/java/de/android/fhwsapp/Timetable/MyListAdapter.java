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
        if(objects.size() < 5)
            return 5;
        return objects.size();
    }

    @Override
    public Object getItem(int position) {
        if(position+1 > objects.size())
            return null;
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView==null){

            convertView = LayoutInflater.from(context).inflate(R.layout.activity_list_adapter,parent,false);

            TextView tvTime = (TextView) convertView.findViewById(R.id.tvTime);
            tvTime.setSelected(true);
            TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
            tvName.setSelected(true);
            TextView tvRoom = (TextView) convertView.findViewById(R.id.tvRoom);
            tvRoom.setSelected(true);
            TextView tvTeacher = (TextView) convertView.findViewById(R.id.tvTeacher);
            tvTeacher.setSelected(true);

            if(position+1 <= objects.size()) {

                Subject subject = objects.get(position);

                tvTime.setText(subject.getTimeStart() + " - " + subject.getTimeEnd());
                tvName.setText(subject.getType() + " " + subject.getSubjectName());
                if(subject.getStudiengang() != null && !subject.getStudiengang().equals(""))
                    tvName.setText(tvName.getText()+ " [" + subject.getStudiengang() + "]");

                tvRoom.setText(subject.getRoom());
                tvTeacher.setText(subject.getTeacher());
            }

            if(position == 0 || position % 2 == 0)
                convertView.setBackgroundColor(Color.parseColor("#AAAAAA"));

        }
        return convertView;
    }
}
