package de.android.fhwsapp.objects;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by alex on 02.09.17.
 */

public class SpoQuestionObject implements Parcelable{

    @SerializedName("frage")
    private String question;

    @SerializedName("antwort")
    private String answer;

    public SpoQuestionObject() {

    }

    protected SpoQuestionObject(Parcel in) {
        question = in.readString();
        answer = in.readString();
    }

    public static final Creator<SpoQuestionObject> CREATOR = new Creator<SpoQuestionObject>() {
        @Override
        public SpoQuestionObject createFromParcel(Parcel in) {
            return new SpoQuestionObject(in);
        }

        @Override
        public SpoQuestionObject[] newArray(int size) {
            return new SpoQuestionObject[size];
        }
    };

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(question);
        parcel.writeString(answer);
    }
}
