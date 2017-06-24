package de.android.fhwsapp.Timetable;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.android.fhwsapp.R;

public class ContentFragment extends Fragment implements View.OnClickListener {

    private TextView tvDate;
    private String subjectText;
    private List<Subject> subjects;
    private LinearLayout layout;
    private TextView tvSubject;

    public static TextView markedTv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.time_table_content, container, false);

        instViews(rootView);

        setViews();

        return rootView;
    }

    private void instViews(ViewGroup rootView){
        tvDate = (TextView) rootView.findViewById(R.id.tvDate);
        //tvSubject = (TextView) rootView.findViewById(R.id.tvSubject);
        layout = (LinearLayout) rootView.findViewById(R.id.subjects);
        
    }

    public void loadData(ArrayList<Subject> subjects){
        this.subjects = subjects;
    }

    public void setViews(){

        if(subjects == null || subjects.size() == 0){
            return;
        }

        Timetable.floatingActionButton.setImageResource(android.R.drawable.ic_input_add);
        Timetable.floatingActionButton.setTag("plus");

        //muss noch geändert werden dass das Datum extern von subjects geschickt wird
        if(tvDate != null)
            tvDate.setText(subjects.get(0).getDate());

        for(int i = 0; i < subjects.size(); i++) {

            tvSubject = addSubject();

            subjectText = subjects.get(i).getTimeStart() + " - " + subjects.get(i).getTimeEnd() + " Uhr\n" + subjects.get(i).getType() +
                    " " + subjects.get(i).getSubjectName() + "\n" + subjects.get(i).getTeacher() + "\n" + subjects.get(i).getRoom() + "\n" + subjects.get(i).getInfo();


            if (tvSubject != null) {
                tvSubject.setText(subjectText);

                //bei info farbe ändern
                if(subjects.get(i).getInfo()!="") {
                    tvSubject.setBackgroundColor(0x99AA0000);
                    tvSubject.setHighlightColor(0x99AA0000);
                }
                //falls nur ein fach, größeres margin
                if(subjects.size() == 1)
                    setMargins(subjects.get(i),20);
                else
                    setMargins(subjects.get(i),8);
            }

        }
    }

    public void setMargins(Subject subject, int side){
        if(Timetable.oneHourMargin == 0)
            return;

        int sideMargin = side;
        float topMargin = 0, bottomMargin = 0;
        //top margin
        String[] startTime = subject.getTimeStart().split(":");
        int startHour = Integer.parseInt(startTime[0]) - 8;
        int startMin = Integer.parseInt(startTime[1]);

        topMargin = Timetable.oneHourMargin*startHour+ Timetable.oneHourMargin/60*startMin;

        //bottom margin
        startTime = subject.getTimeEnd().split(":");
        startHour = Integer.parseInt(startTime[0]) - 8;
        startMin = Integer.parseInt(startTime[1]);

        bottomMargin = Timetable.oneHourMargin *13 - Timetable.oneHourMargin*startHour- Timetable.oneHourMargin/60*startMin;

        //set margins
        ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) tvSubject.getLayoutParams();
        marginParams.setMargins(side, (int)topMargin, side, (int)bottomMargin);
    }

    private TextView addSubject(){
        layout.setWeightSum(layout.getWeightSum()+1);

        TextView tv = new TextView(getContext(),null);
        tv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f));

        tv.setGravity(Gravity.CENTER);
        tv.setTextColor(Color.WHITE);
        tv.setBackgroundColor(0x99000000);

        //make tv scrollbar
        tv.setMaxLines(10);
        tv.setVerticalScrollBarEnabled(true);
        tv.setMovementMethod(new ScrollingMovementMethod());
        tv.setHighlightColor(0x99000000);

        tv.setOnClickListener(this);

        layout.addView(tv);

        return tv;
    }


    @Override
    public void onClick(View v) {
        if(v instanceof TextView) {
            TextView tv = (TextView) v;

            //bei click auf gleichen tv
            if(markedTv != null)
                    if(markedTv.equals(tv)) {
                        markedTv = null;
                        tv.setBackgroundColor(tv.getHighlightColor());

                        if(markedTv == null)
                            if(Timetable.floatingActionButton != null) {
                                Timetable.floatingActionButton.setImageResource(android.R.drawable.ic_input_add);
                                Timetable.floatingActionButton.setTag("plus");
                            }

                        return;
                    }

            //bei click auf neuen tv
            if(Timetable.floatingActionButton != null){
                Timetable.floatingActionButton.setImageResource(R.drawable.minus);
                Timetable.floatingActionButton.setTag("minus");
            }

            if(markedTv != null)
                markedTv.setBackgroundColor(markedTv.getHighlightColor());

            markedTv = tv;
            tv.setBackgroundColor(0xFF00AA00);

            //klick weder auf Button, noch auf TV
        }else{
            if(markedTv != null) {

                markedTv.setBackgroundColor(markedTv.getHighlightColor());

                if (Timetable.floatingActionButton != null) {
                    Timetable.floatingActionButton.setImageResource(android.R.drawable.ic_input_add);
                    Timetable.floatingActionButton.setTag("plus");
                }
            }
        }
    }

}

