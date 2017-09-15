package de.android.fhwsapp.Timetable;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.android.fhwsapp.R;

public class ContentFragment extends Fragment {

    private TextView tvDate;
    private String subjectText;
    private List<Subject> subjects;
    private LinearLayout layout;
    private TextView tvSubject;

    Animation animShake;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.time_table_content, container, false);

        animShake = AnimationUtils.loadAnimation(getContext(), R.anim.shake);

        instViews(rootView);

        setViews();

        return rootView;
    }

    private void instViews(ViewGroup rootView) {
        tvDate = (TextView) rootView.findViewById(R.id.tvDate);
        layout = (LinearLayout) rootView.findViewById(R.id.subjects);
    }

    public void loadData(ArrayList<Subject> subjects) {
        this.subjects = subjects;
    }

    public void setViews() {

        if (subjects == null || subjects.size() == 0) {
            return;
        }


        if (tvDate != null) {
            String weekDay = subjects.get(0).getDateAsDateTime().dayOfWeek().getAsText(new Locale("de")); //.getAsShortText(new Locale("de"));
            tvDate.setText(weekDay + " " + subjects.get(0).getDate());

        }

        //falls leere Fächer
        if (subjects.get(0).getSubjectName() == null || subjects.get(0).getSubjectName().equals(""))
            return;

        for (int i = 0; i < subjects.size(); i++) {

            tvSubject = addSubject();

            subjectText = subjects.get(i).getTimeStart() + " - " + subjects.get(i).getTimeEnd() + " Uhr\n" + subjects.get(i).getType() +
                    " " + subjects.get(i).getSubjectName() + "\n" + subjects.get(i).getTeacher() + "\n" + subjects.get(i).getRoom() + "\n" + subjects.get(i).getInfo();


            if (tvSubject != null) {
                tvSubject.setText(subjectText);

                //falls nur ein fach, größeres margin
                if (subjects.size() == 1)
                    setMargins(subjects.get(i), 20);
                else
                    setMargins(subjects.get(i), 8);
            }

        }
    }

    public void setMargins(Subject subject, int side) {
        if (Timetable.oneHourMargin == 0 || subject.getTimeStart() == null)
            return;

        int sideMargin = side;
        float topMargin = 0, bottomMargin = 0;
        //top margin
        String[] startTime = subject.getTimeStart().split(":");
        int startHour = Integer.parseInt(startTime[0]) - 8;
        int startMin = Integer.parseInt(startTime[1]);

        topMargin = Timetable.oneHourMargin * startHour + Timetable.oneHourMargin / 60 * startMin;

        //bottom margin
        startTime = subject.getTimeEnd().split(":");
        startHour = Integer.parseInt(startTime[0]) - 8;
        startMin = Integer.parseInt(startTime[1]);

        bottomMargin = Timetable.oneHourMargin * 13 - Timetable.oneHourMargin * startHour - Timetable.oneHourMargin / 60 * startMin;

        //set margins
        ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) tvSubject.getLayoutParams();
        marginParams.setMargins(side, (int) topMargin, side, (int) bottomMargin);
    }

    private TextView addSubject() {

        layout.setWeightSum(layout.getWeightSum() + 1);

        TextView tv = new TextView(getContext(), null);
        tv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f));

        tv.setGravity(Gravity.CENTER);
        tv.setTextColor(Color.WHITE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tv.setLetterSpacing(0.05f);
        }
        tv.setBackgroundResource(R.drawable.timetable_item_bg);


        //make tv scrollbar
        tv.setMaxLines(10);
        tv.setVerticalScrollBarEnabled(true);
        tv.setMovementMethod(new ScrollingMovementMethod());

        layout.addView(tv);

        return tv;
    }

}

