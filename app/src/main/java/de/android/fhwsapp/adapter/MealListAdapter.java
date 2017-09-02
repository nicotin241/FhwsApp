package de.android.fhwsapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import de.android.fhwsapp.R;
import de.android.fhwsapp.objects.Meal;



public class MealListAdapter extends BaseAdapter {

    Context context;
    ArrayList<Meal> listData;

    public MealListAdapter(Context context, ArrayList<Meal> listData) {

        this.context = context;
        this.listData = listData;

    }

    @Override
    public int getCount() {
        return listData.size();
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

        private ImageView foodtype_image;
        private TextView meal_name;
        private TextView meal_price;

    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        ViewHolder viewHolder = null;

        if (view == null) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.meal_list_item, null);

            viewHolder = new ViewHolder();

            viewHolder.foodtype_image = (ImageView) view.findViewById(R.id.foodtype_image);
            viewHolder.meal_name = (TextView) view.findViewById(R.id.meal_name);
            viewHolder.meal_price = (TextView) view.findViewById(R.id.meal_price);


            view.setTag(viewHolder);

        } else {

            viewHolder = (ViewHolder) view.getTag();

        }

        Meal meal = listData.get(position);

        viewHolder.meal_name.setText(meal.getName());
        viewHolder.meal_price.setText(meal.getPrice_students() + " €");
        switch (meal.getFoodtype()) {
            case "Schwein":
                viewHolder.foodtype_image.setImageResource(R.drawable.schwein);
                break;
            case "Rind":
                viewHolder.foodtype_image.setImageResource(R.drawable.rind);
                break;
            case "Gefluegel":
                viewHolder.foodtype_image.setImageResource(R.drawable.gefluegel);
                break;
            case "Fisch":
                viewHolder.foodtype_image.setImageResource(R.drawable.fisch);
                break;
            case "Fleischlos":
                viewHolder.foodtype_image.setImageResource(R.drawable.fleischlos);
                break;
            case "Vegan":
                viewHolder.foodtype_image.setImageResource(R.drawable.vegan);
                break;
            case "Kalb":
                viewHolder.foodtype_image.setImageResource(R.drawable.kalb);
                break;
            case "Lamm":
                viewHolder.foodtype_image.setImageResource(R.drawable.lamm);
                break;
            case "Vorderschinken":
                viewHolder.foodtype_image.setImageResource(R.drawable.vorderschinken);
                break;
            case "Wild":
                viewHolder.foodtype_image.setImageResource(R.drawable.wild);
                break;
        }



        return view;
    }

}
