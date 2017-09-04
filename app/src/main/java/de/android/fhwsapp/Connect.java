package de.android.fhwsapp;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class Connect {

    private static List<ConnectionListener> listeners = new ArrayList<ConnectionListener>();

    public static void sendToAllListeners() {

        for (ConnectionListener l : listeners) {
            l.onChanged();
        }
    }

    public static void addListener(ConnectionListener l) {
        listeners.clear();
        listeners.add(l);
    }
}
