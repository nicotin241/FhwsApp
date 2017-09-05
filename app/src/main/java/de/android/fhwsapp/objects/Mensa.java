package de.android.fhwsapp.objects;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Mensa {

    private int mensaId;
    private String name;
    private String zeiten;

    public Mensa() {

    }

    public Mensa(int mensaId, String name, String zeiten) {

        this.mensaId = mensaId;
        this.name = name;
        this.zeiten = zeiten;

    }

    public int getMensaId() {
        return mensaId;
    }

    public void setMensaId(int mensaId) {
        this.mensaId = mensaId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getZeiten() {
        return zeiten;
    }

    public void setZeiten(String zeiten) {
        this.zeiten = zeiten;
    }

    public static ArrayList<Mensa> getAllMensas() {

        ArrayList<Mensa> allMensas = new ArrayList<>();

        allMensas.add(new Mensa(8, "Burse Würzburg", "Mo - Fr    11.00 - 14.15 Uhr\nMo - Do    15.00 - 18.30 Uhr\n(Nur in der Vorlesungszeit!)"));
        allMensas.add(new Mensa(3, "Mensateria Campus Nord", "Mo - Fr    11.00 - 14.15 Uhr"));
        allMensas.add(new Mensa(5, "Mensa am Hubland Würzburg", "Mo - Fr    11.00 - 14.15 Uhr \nMo - Do    15.30 - 19.00 Uhr\n(Essensausgabe über Frankenstube)"));
        allMensas.add(new Mensa(9, "Mensa Röntgenring Würzburg", "Mo - Fr    11.30 - 14.00 Uhr"));
        allMensas.add(new Mensa(10, "Mensa Josef-Schneider-Straße Würzburg", "Mo - Fr    11.30 - 14.00 Uhr"));
        allMensas.add(new Mensa(7, "Frankenstube Würzburg", "Mo - Do    15.30 - 19.00 Uhr\n(Nur in der Vorlesungszeit!)"));

        return allMensas;

    }

}
