package de.android.fhwsapp;

import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Connect {

    private static List<ConnectionListener> listeners = new ArrayList<ConnectionListener>();

    public static void sendToAllListeners() {

        for (ConnectionListener l : listeners) {
            l.onChanged();
            Log.e("Connect", "listener onChanged");
        }
    }

    public static void addListener(ConnectionListener l) {
        listeners.clear();
        listeners.add(l);
        Log.e("Connect", l.toString());
    }
}
