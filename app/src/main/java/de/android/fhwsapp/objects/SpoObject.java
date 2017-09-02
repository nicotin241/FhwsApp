package de.android.fhwsapp.objects;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by alex on 02.09.17.
 */

public class SpoObject {

    @SerializedName("Studienstruktur")
    private ArrayList<SpoQuestionObject> studienstruktur;

    @SerializedName("Praxismodul")
    private ArrayList<SpoQuestionObject> praxismodul;

    @SerializedName("Anrechnung von Leistungen")
    private ArrayList<SpoQuestionObject> anrechungVonLeistungen;

    @SerializedName("Prüfungsleistungen")
    private ArrayList<SpoQuestionObject> pruefungsleistungen;

    @SerializedName("Termine und Fristen")
    private ArrayList<SpoQuestionObject> termineUndFristen;

    @SerializedName("Bachelorarbeit")
    private ArrayList<SpoQuestionObject> bachelorarbeit;

    @SerializedName("Projektarbeit")
    private ArrayList<SpoQuestionObject> projektarbeit;

    @SerializedName("Dokumente")
    private ArrayList<SpoQuestionObject> dokumente;

    @SerializedName("Gremien")
    private ArrayList<SpoQuestionObject> gremien;

    @SerializedName("Sonstiges")
    private ArrayList<SpoQuestionObject> sonstiges;

    public SpoObject() {

    }

    public ArrayList<SpoQuestionObject> getStudienstruktur() {
        return studienstruktur;
    }

    public void setStudienstruktur(ArrayList<SpoQuestionObject> studienstruktur) {
        this.studienstruktur = studienstruktur;
    }

    public ArrayList<SpoQuestionObject> getPraxismodul() {
        return praxismodul;
    }

    public void setPraxismodul(ArrayList<SpoQuestionObject> praxismodul) {
        this.praxismodul = praxismodul;
    }

    public ArrayList<SpoQuestionObject> getAnrechungVonLeistungen() {
        return anrechungVonLeistungen;
    }

    public void setAnrechungVonLeistungen(ArrayList<SpoQuestionObject> anrechungVonLeistungen) {
        this.anrechungVonLeistungen = anrechungVonLeistungen;
    }

    public ArrayList<SpoQuestionObject> getPruefungsleistungen() {
        return pruefungsleistungen;
    }

    public void setPruefungsleistungen(ArrayList<SpoQuestionObject> pruefungsleistungen) {
        this.pruefungsleistungen = pruefungsleistungen;
    }

    public ArrayList<SpoQuestionObject> getTermineUndFristen() {
        return termineUndFristen;
    }

    public void setTermineUndFristen(ArrayList<SpoQuestionObject> termineUndFristen) {
        this.termineUndFristen = termineUndFristen;
    }

    public ArrayList<SpoQuestionObject> getBachelorarbeit() {
        return bachelorarbeit;
    }

    public void setBachelorarbeit(ArrayList<SpoQuestionObject> bachelorarbeit) {
        this.bachelorarbeit = bachelorarbeit;
    }

    public ArrayList<SpoQuestionObject> getProjektarbeit() {
        return projektarbeit;
    }

    public void setProjektarbeit(ArrayList<SpoQuestionObject> projektarbeit) {
        this.projektarbeit = projektarbeit;
    }

    public ArrayList<SpoQuestionObject> getDokumente() {
        return dokumente;
    }

    public void setDokumente(ArrayList<SpoQuestionObject> dokumente) {
        this.dokumente = dokumente;
    }

    public ArrayList<SpoQuestionObject> getGremien() {
        return gremien;
    }

    public void setGremien(ArrayList<SpoQuestionObject> gremien) {
        this.gremien = gremien;
    }

    public ArrayList<SpoQuestionObject> getSonstiges() {
        return sonstiges;
    }

    public void setSonstiges(ArrayList<SpoQuestionObject> sonstiges) {
        this.sonstiges = sonstiges;
    }
}
