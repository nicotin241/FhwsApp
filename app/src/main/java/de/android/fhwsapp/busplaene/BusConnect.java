package de.android.fhwsapp.busplaene;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import de.android.fhwsapp.ConnectionListener;

/**
 * Created by admin on 14.09.17.
 */

public class BusConnect {

    private static List<ConnectionListener> listeners = new ArrayList<ConnectionListener>();

    public static void sendToAllListeners() {

        for (ConnectionListener l : listeners) {
            l.onChanged();
            Log.e("BusConnect", "listener onChanged");
        }
    }

    public static void addListener(ConnectionListener l) {
        listeners.clear();
        listeners.add(l);
        Log.e("BusConnect", l.toString());
    }
}
