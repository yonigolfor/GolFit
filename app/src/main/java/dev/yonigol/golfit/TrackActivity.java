package dev.yonigol.golfit;

import androidx.appcompat.app.AppCompatActivity;
import dev.yonigol.golfit.MyComponents.Plan;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class TrackActivity extends AppCompatActivity {

    private LineChart lineChart;
    private List<LineDataSet> lineDataSets;

    private List<Plan> plansList;
    private List<List<Integer>> volumeResults;
    private String[] titles;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);

        findViews();
    }

    @Override
    public void onBackPressed() {
        goMainScreen();
    }

    private void goMainScreen() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void findViews() {
        volumeResults = new ArrayList<>();

        plansList = new ArrayList<>();
        lineDataSets = new ArrayList<>();




        getVolumeHistory();
        titles = new String[volumeResults.size()];

//        setTitles();
        lineChart = (LineChart) findViewById(R.id.line_chart);

        setGraph();
    }


//    private void setTitles() {
//        int index = 0;
//        for (int i = 0; i < plansList.size(); i++) {
//            if (plansList.get(i).getResultsHistory() != null){
//                titles[index] = plansList.get(i).getTitle();
//                index++;
//            }
//        }
//    }

    private void setGraph() {
        String title;
        // set line data set + title
        for (int i = 0; i < plansList.size(); i++) {
            if (plansList.get(i).getVolumeHistory() != null){
                Log.e("reach", i +" = "+ plansList.get(i).getVolumeHistory().toString());
                title = plansList.get(i).getTitle();
                LineDataSet lds = new LineDataSet(getEntry(plansList.get(i).getVolumeHistory()), title);
                lds = designLine(lds, i);
                lineDataSets.add(lds);
            }
        }

        setLineChartData();
    }

    private LineDataSet designLine(LineDataSet lds, int index) {
        lds.setLineWidth(7);
        lds.setColor(getLineColor(index));
        lds.setValueTextSize(10);
        lds.setCircleColor(getLineColor(index)); //Color.GRAY
        lds.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        lds.setDrawValues(false); // remove data on the line
        lds.setDrawCircles(false); // show or not the circles

        return lds;
    }

    private int getLineColor(int index) {
        switch (index){
            case 0:
                return Color.parseColor("#dab800"); // yellow
            case 1:
                return Color.parseColor("#04648f"); // blue
            case 2:
                return Color.parseColor("#ff6766"); // pink
            default:
                return Color.parseColor("#ff9933");
        }
    }

    private List<Entry> getEntry(List<Integer> volumeHistory) {
        List<Entry> entries = new ArrayList<>();
        int counter = 0;
            for (int data: volumeHistory) {
                entries.add(new Entry(counter, data)); // 1,1 2,2 3,3 4,4 5,5
                counter++;
        }
            return entries;
    }

//    private void handleTrainingResults() {
//        List<Entry> entries = new ArrayList<>();
//        int counter, data;
//        for (int i = 0; i < volumeResults.size(); i++) {
//            counter = 0;
//            for (int j = 0; j < volumeResults.get(i).size(); j++) {
//                data = volumeResults.get(i).get(j);
//                entries.add(new Entry(counter, data));
//
//                counter++;
//            }
//            trainingResults.add(entries);
//        }
//    }

    private void getVolumeHistory() {
        plansList = (List<Plan>) getIntent().getSerializableExtra("plansList");
        volumeResults = (List<List<Integer>>) getIntent().getSerializableExtra("plansVolumes");
        Log.e("volume:", volumeResults.toString());
        Log.e("volume:", plansList.toString());

    }

//    private void getPlanData() {
//        userId = getIntent().getStringExtra("userId");
//
//        plan = (Plan) getIntent().getSerializableExtra("plan");
//
//        // retrieve plan from db
//
//        planTitle = plan.getTitle();
//
//        exNames = plan.getExNames();
//
//
//
//        showUI();
//    }



    private void setLineChartData() {
        
//       LineDataSet lineDataSet = new LineDataSet(entries, title);
//       lineDataSet.setLineWidth(5);
//       lineDataSet.setColor(Color.BLUE);
//       lineDataSet.setValueTextSize(10);
//       lineDataSet.setCircleColor(Color.GRAY);
//       lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
//        lineDataSet.setDrawValues(false); // remove data on the line
//
//
       ArrayList<ILineDataSet> dataSets = new ArrayList<>();
       dataSets = setDataSet(dataSets);

       LineData AllLinesData = new LineData(dataSets);

       lineChart.setData(AllLinesData);


       lineChart.getAxisLeft().setDrawGridLines(false);
       lineChart.getXAxis().setDrawGridLines(false); // clean inside lines
       lineChart.getXAxis().setEnabled(false); // delete the nums above the graph
//       lineChart.getAxisRight().setDrawGridLines(false); // delete all lines --- in the middle

//       lineChart.getAxisLeft().setEnabled(true); // remove left line axis מאונך, ציר וואי
        lineChart.getAxisRight().setEnabled(false); // remove right side of the same above

        lineChart.getLegend().setEnabled(true); // shows the names of the plans
        lineChart.animateXY(2500, 3000, Easing.EaseInCubic, Easing.EaseInOutCubic);



        lineChart.setPinchZoom(false); // מאפשר זום מגניב לגרף
        lineChart.setAutoScaleMinMaxEnabled(true);

       lineChart.invalidate();


    }

    private ArrayList<ILineDataSet> setDataSet(ArrayList<ILineDataSet> dataSets) {
        for (int i = 0; i < lineDataSets.size(); i++)
            dataSets.add(lineDataSets.get(i));

        return dataSets;
    }

//    private String[] getTitles() {
//        String [] titles= new String[volumeResults.size()];
//        for (int i = 0; i < plansList.size(); i++) {
//            if (plansList.get(i).getVolumeHistory() != null)
//
//
//        }
//    }


}