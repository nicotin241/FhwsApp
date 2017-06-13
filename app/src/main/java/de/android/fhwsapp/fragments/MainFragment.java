package de.android.fhwsapp.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.android.fhwsapp.R;
import de.android.fhwsapp.Timetable.Timetable;

public class MainFragment extends Fragment implements View.OnClickListener {

    private CardView timeTable;

    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout =  inflater.inflate(R.layout.fragment_main, container, false);

        timeTable = (CardView) layout.findViewById(R.id.timeTable);
        timeTable.setOnClickListener(this);

        return layout;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.timeTable:
                Intent intent = new Intent(this.getActivity(), Timetable.class);
                startActivity(intent);
                break;
        }
    }
}
