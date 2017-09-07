package de.android.fhwsapp.adapter;

import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import de.android.fhwsapp.R;
import de.android.fhwsapp.objects.SpoQuestionObject;


public class QuestionListAdapter extends BaseAdapter {

    Context context;
    ArrayList<SpoQuestionObject> listData;

    final int arrowUp = R.drawable.arrow_grey_top;
    final int arrowDown = R.drawable.arrow_grey_bottom;

    public QuestionListAdapter(Context context, ArrayList<SpoQuestionObject> listData) {

        this.context = context;
        this.listData = listData;

    }

    @Override
    public int getCount() {
        try {
            return listData.size();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }

    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    class ViewHolder {

        private ImageView arrow;
        private TextView question;
        private TextView answer;
        private LinearLayout element;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        ViewHolder viewHolder = null;

        if (view == null) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.spo_list_item, null);

            viewHolder = new ViewHolder();

            viewHolder.element = (LinearLayout) view.findViewById(R.id.element);
            viewHolder.arrow = (ImageView) view.findViewById(R.id.arrow);
            viewHolder.question = (TextView) view.findViewById(R.id.question);
            viewHolder.answer = (TextView) view.findViewById(R.id.answer);


            view.setTag(viewHolder);

        } else {

            viewHolder = (ViewHolder) view.getTag();

        }

        SpoQuestionObject question = listData.get(position);

        viewHolder.question.setText((position + 1) + ". " + question.getQuestion());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            viewHolder.answer.setText(Html.fromHtml(question.getAnswer(), Build.VERSION.SDK_INT));
        } else {
            viewHolder.answer.setText(Html.fromHtml(question.getAnswer()));
        }

        viewHolder.arrow.setImageResource(arrowDown);
        viewHolder.answer.setVisibility(View.GONE);
        final ViewHolder finalViewHolder = viewHolder;
        viewHolder.element.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(finalViewHolder.answer.getVisibility() == View.GONE) {

                    finalViewHolder.answer.setVisibility(View.VISIBLE);
                    finalViewHolder.arrow.setImageResource(arrowUp);

                } else {

                    finalViewHolder.answer.setVisibility(View.GONE);
                    finalViewHolder.arrow.setImageResource(arrowDown);

                }

            }
        });

        return view;
    }

}
