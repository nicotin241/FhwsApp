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
import de.android.fhwsapp.objects.Mensa;

public class MensaListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Mensa> data = Mensa.getAllMensas();

    public MensaListAdapter(Context context) {

        this.context = context;

    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    class ViewHolder {

        private ImageView mensa_image;
        private TextView mensa_name;
        private TextView mensa_zeiten;

    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        ViewHolder viewHolder = null;

        if (view == null) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.mensa_list_item, null);

            viewHolder = new ViewHolder();

            viewHolder.mensa_image = (ImageView) view.findViewById(R.id.mensa_pic);
            viewHolder.mensa_name = (TextView) view.findViewById(R.id.mensa_name);
            viewHolder.mensa_zeiten = (TextView) view.findViewById(R.id.mensa_zeiten);

            view.setTag(viewHolder);

        } else {

            viewHolder = (ViewHolder) view.getTag();

        }

        Mensa mensa = data.get(position);

        viewHolder.mensa_name.setText(mensa.getName());
        viewHolder.mensa_zeiten.setText(mensa.getZeiten());
        viewHolder.mensa_image.setImageResource(Mensa.getMensaPic(mensa.getMensaId()));

        return view;
    }

}
