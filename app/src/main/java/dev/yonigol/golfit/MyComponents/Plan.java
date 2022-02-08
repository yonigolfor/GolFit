package dev.yonigol.golfit.MyComponents;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Plan implements Serializable {
    private String planTitle;
    private List<String> exNames;
    private List<List<List<Integer>>> resultsHistory; // [ [ [5,2], [6,3] ] , [ [5,3], [7,3] ] ]
    private int generalSets;// not in use yet
    private int generalReps; // not in use yet
    private List<Integer> volumeHistory; // 1,2,5,5,5,7



    public Plan() {
    }

    public Plan(String planTitle, List<String> exNames){
        this.planTitle = planTitle;
        this.exNames = exNames;
        this.resultsHistory = new ArrayList<>();


    }

    protected Plan(Parcel in) {
        planTitle = in.readString();
        exNames = in.createStringArrayList();
        generalSets = in.readInt();
        generalReps = in.readInt();
    }

//    public static final Creator<Plan> CREATOR = new Creator<Plan>() {
//        @Override
//        public Plan createFromParcel(Parcel in) {
//            return new Plan(in);
//        }
//
//        @Override
//        public Plan[] newArray(int size) {
//            return new Plan[size];
//        }
//    };

    public List<Integer> getVolumeHistory() {
        return volumeHistory;
    }

    public void setVolumeHistory(List<Integer> volumeHistory) {
        this.volumeHistory = volumeHistory;
    }

    public String getTitle() {
        return planTitle;
    }

    public void setTitle(String planTitle) {
        this.planTitle = planTitle;
    }

    public void exNamesList(List<String> exNames) {
        this.exNames = exNames;
    }

    public List<List<List<Integer>>> getResultsHistory() {
        return resultsHistory;
    }

    public void setResultsHistory(List<List<List<Integer>>> resultsHistory) {
        this.resultsHistory = resultsHistory;
    }

    public void addTodaysResults(List<List<Integer>> todaysResults) {
        this.resultsHistory.add(todaysResults);
    }


    public int getGeneralSets() {
        return generalSets;
    }

    public void setgeneralSets(int generalSets) {
        this.generalSets = generalSets;
    }

    public int getGeneralReps() {
        return generalReps;
    }

    public void setgeneralReps(int generalReps) {
        this.generalReps = generalReps;
    }

    public List<String> getExNames(){
        return exNames;
    }

    public String toString(){
        String resHist = "";
        if (resultsHistory == null);
        else resHist = resultsHistory.toString();

        return "Plan title: " + planTitle + "\n, ex Names: " + exNames.toString() + "\n" +
                "resultsHistory: " + resHist +"\n" ;
    }

//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel parcel, int i) {
//        parcel.writeString(planTitle);
//        parcel.writeStringList(exNames);
//        parcel.writeInt(generalSets);
//        parcel.writeInt(generalReps);
//    }
}
