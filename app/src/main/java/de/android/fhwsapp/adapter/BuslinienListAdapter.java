package de.android.fhwsapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import de.android.fhwsapp.R;


public class BuslinienListAdapter extends BaseAdapter {

    private Context context;
    private String[] map;

    public static int loadingView = -1;

    private static final String LINIE6 = "Stadtmitte - Gartenstadt Keesburg";
    private static final String LINIE114 = "Busbahnhof - Frauenland / Wittelsbacherplatz - Hubland / Unizentrum";
    private static final String LINIE14 = "Würzburg - Gerbrunn";
    private static final String LINIE214 = "Busbahnhof - Hubland / Mensa - FHWS (Sanderheinrichsleitenweg)";
    private static final String LINIE10 = "Sanderring - Hubland (Uni-Zentrum) - Campus Nord";

    public BuslinienListAdapter(Context context, String[] map) {

        this.context = context;
        this.map = map;

        loadingView = -1;

    }

    @Override
    public int getCount() {
        return map.length;
    }

    @Override
    public Object getItem(int position) {
        return map[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    class ViewHolder {

        private TextView bus_name;
        private TextView bus_description;
        private ImageView downloadImage;
        private ProgressBar downloadProgress;

    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        ViewHolder viewHolder = null;

        if (view == null) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.bus_list_item, null);

            viewHolder = new ViewHolder();

            viewHolder.bus_name = (TextView) view.findViewById(R.id.bus_name);
            viewHolder.bus_description = (TextView) view.findViewById(R.id.bus_description);
            viewHolder.downloadImage = (ImageView) view.findViewById(R.id.ivBusItem);
            viewHolder.downloadProgress = (ProgressBar) view.findViewById(R.id.pbBusItem);

            view.setTag(viewHolder);

        } else {

            viewHolder = (ViewHolder) view.getTag();

        }

        if (loadingView == position) {
            viewHolder.downloadProgress.setVisibility(View.VISIBLE);
            viewHolder.downloadImage.setVisibility(View.GONE);
        } else {
            viewHolder.downloadProgress.setVisibility(View.GONE);
            viewHolder.downloadImage.setVisibility(View.VISIBLE);
        }

        viewHolder.bus_name.setText("Linie " + map[position]);

        switch (map[position]) {
            case "6":
                viewHolder.bus_description.setText(LINIE6);
                break;
            case "114":
                viewHolder.bus_description.setText(LINIE114);
                break;
            case "14":
                viewHolder.bus_description.setText(LINIE14);
                break;
            case "214":
                viewHolder.bus_description.setText(LINIE214);
                break;
            case "10":
                viewHolder.bus_description.setText(LINIE10);
                break;
        }


        return view;
    }

}
