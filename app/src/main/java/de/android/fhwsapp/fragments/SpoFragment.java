package de.android.fhwsapp.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStream;

import de.android.fhwsapp.R;
import de.android.fhwsapp.objects.SpoObject;


public class SpoFragment extends Fragment {

    private View view;

    private static final int NUM_PAGES = 10;
    private ViewPager mPager;
    private ScreenSlidePagerAdapter mPagerAdapter;


    public SpoFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view  = inflater.inflate(R.layout.fragment_spo, container, false);

        String spoFaq = loadJSONFromAsset();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        SpoObject spoObject = gson.fromJson(spoFaq, SpoObject.class);

        mPager = (ViewPager) view.findViewById(R.id.spo_pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getChildFragmentManager(), spoObject);
        mPager.setAdapter(mPagerAdapter);

        return view;
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getActivity().getAssets().open("spo_faq.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        SpoObject data;

        public ScreenSlidePagerAdapter(FragmentManager fm, SpoObject data) {

            super(fm);
            this.data = data;

        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0: return ScreenSlidePageFragment.newInstance("Studienstruktur", data.getStudienstruktur());
                case 1: return ScreenSlidePageFragment.newInstance("Praxismodul", data.getPraxismodul());
                case 2: return ScreenSlidePageFragment.newInstance("Anrechnung von Leistungen", data.getAnrechungVonLeistungen());
                case 3: return ScreenSlidePageFragment.newInstance("Prüfungsleistungen", data.getPruefungsleistungen());
                case 4: return ScreenSlidePageFragment.newInstance("Termine und Fristen", data.getTermineUndFristen());
                case 5: return ScreenSlidePageFragment.newInstance("Bachelorarbeit", data.getBachelorarbeit());
                case 6: return ScreenSlidePageFragment.newInstance("Projektarbeit", data.getProjektarbeit());
                case 7: return ScreenSlidePageFragment.newInstance("Dokumente", data.getDokumente());
                case 8: return ScreenSlidePageFragment.newInstance("Gremien", data.getGremien());
                case 9: return ScreenSlidePageFragment.newInstance("Sonstiges", data.getSonstiges());
                default: return null;
            }

        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }




}
