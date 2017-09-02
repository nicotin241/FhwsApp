package de.android.fhwsapp.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import de.android.fhwsapp.R;
import de.android.fhwsapp.adapter.QuestionListAdapter;
import de.android.fhwsapp.objects.SpoQuestionObject;

public class ScreenSlidePageFragment extends Fragment {

    String title;
    ArrayList<SpoQuestionObject> questions;

    private static final String ARG_TITLE = "title";
    private static final String ARG_QUESTION_ARRAY = "questions";

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        this.title = args.getString(ARG_TITLE);
        this.questions = args.getParcelableArrayList(ARG_QUESTION_ARRAY);
    }


    public static ScreenSlidePageFragment newInstance(String title, ArrayList<SpoQuestionObject> questions) {

        ScreenSlidePageFragment fragment = new ScreenSlidePageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putParcelableArrayList(ARG_QUESTION_ARRAY, questions);
        fragment.setArguments(args);
        return fragment;
    }

    public ScreenSlidePageFragment() {

    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.spo_screen_slide_page, container, false);

        TextView title_thema = (TextView) rootView.findViewById(R.id.title_thema);
        ListView question_list = (ListView) rootView.findViewById(R.id.question_list);
        question_list.setAdapter(new QuestionListAdapter(getContext(), questions));

        title_thema.setText(title);

        return rootView;
    }
}
