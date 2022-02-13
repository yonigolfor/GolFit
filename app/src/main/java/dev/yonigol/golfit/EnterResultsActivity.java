package dev.yonigol.golfit;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;
import dev.yonigol.golfit.MyComponents.Plan;
import dev.yonigol.golfit.MyComponents.User;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class EnterResultsActivity extends AppCompatActivity {
    private LinearLayout planInputsLayout;

    private String userId;
    private Plan plan;

    private String planTitle;
    private List<String> exNames;

//    private List<Integer> todaysResults;

    private List<EditText> kgsVal;
    private List<List<EditText>> repsVal;
    private List<List<Integer>> planResults;

    private int workoutVolume = 0;

    private FirebaseDatabase rootNode;
    private DatabaseReference reference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_results);

        findViews();
        getPlanData();
//        showUI();
    }

    @Override
    public void onBackPressed() {
        goMainScreen();
    }

    private void findViews() {
        planInputsLayout = (LinearLayout) findViewById(R.id.plan_inputs_layout) ;
        planResults = new ArrayList<List<Integer>>();
        exNames = new ArrayList<>();
        repsVal = new ArrayList<List<EditText>>();
        kgsVal = new ArrayList<>();

        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference("users");

    }

    private void showUI() {
        LinearLayout newExlinearLayout = new LinearLayout(this);
        newExlinearLayout = setLayout(newExlinearLayout);
        newExlinearLayout = showTitle(newExlinearLayout);
        newExlinearLayout = showExercisesUI(newExlinearLayout);
        newExlinearLayout = addFinishBtn(newExlinearLayout);

        planInputsLayout.addView(newExlinearLayout);
    }

    private LinearLayout addFinishBtn(LinearLayout newExlinearLayout) {
//        planResults = new ArrayList<>();
//        List<Integer> exRes= new ArrayList<>();
        Button submitBtn = new Button(this);

        submitBtn.setOnClickListener(v->{
            Log.e("generate", "Getting results...");
            // check if all reps have only integer.


            //if true accept the results => add to plans resultsHistory and go to home page.


            // if not, add error msg.

            if(!checkAllInputsFilled()){
                //raise error message
                Log.e("must at least 2 and fill all kg input"," reps Err");
                return;
            }


            for (int i = 0; i < repsVal.size(); i++) {
                List<Integer> exReps = new ArrayList<>();
                for (int j = 0; j < repsVal.get(i).size(); j++) {

                    if (repsVal.get(i).get(j).getText().toString() != null &&
                            repsVal.get(i).get(j).getText().toString() != "" &&
                            !repsVal.get(i).get(j).getText().toString().isEmpty()){
                        Log.e(""+i+j, ""+ repsVal.get(i).get(j).getText().toString());


                        //fill the plan results
                        int res = Integer.parseInt(repsVal.get(i).get(j).getText().toString());
                        exReps.add(res);

                    }
                }
                planResults.add(exReps);
            }

            calculateWorkoutVolume();



            addResultToPlan();
            addVolumeToPlan();


            reference.child(userId).child("plans").child(planTitle).setValue(plan);


            // go back to main
            goMainScreen();

        });

        newExlinearLayout.addView(submitBtn);
        return newExlinearLayout;
    }

    private boolean checkAllInputsFilled() {
        int countEmpty = 0;
        for (int i = 0; i < repsVal.size(); i++) {
            // check if kg is empty somewhere
            if (kgsVal.get(i).getText().toString().isEmpty())
                return false;
            // check if at least 2 reps inserted by user
            for (int j = 0; j < repsVal.get(i).size(); j++) {
                if (repsVal.get(i).get(j).getText().toString().isEmpty())
                    countEmpty++;
                if(countEmpty == 4) // at least 2
                    return false;

            }

            countEmpty = 0;

        }
        return true;
    }

    private void addVolumeToPlan() {
        List<Integer> volList = new ArrayList<>();

        if(plan.getVolumeHistory() == null){
            volList.add(workoutVolume);
            plan.setVolumeHistory(volList);
        }
        else{
            volList = plan.getVolumeHistory();
            volList.add(workoutVolume);
            plan.setVolumeHistory(volList);



        }

    }

    private void addResultToPlan() {
        List<List<List<Integer>>> history = new ArrayList<>();

        if(plan.getResultsHistory() == null){ // empty

            history.add(planResults);
            plan.setResultsHistory(history);
        }
        else{

            history = plan.getResultsHistory();
            history.add(planResults);
            plan.setResultsHistory(history);
        }

    }


    private void goMainScreen() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void calculateWorkoutVolume() {
        int exSumNumOfReps;
        int kg = 1;
        for (int i = 0; i < planResults.size(); i++) {
            exSumNumOfReps = 0;
            for (int j = 0; j < planResults.get(i).size(); j++) {
                if (planResults.get(i).get(j) != null )
                    exSumNumOfReps += planResults.get(i).get(j);
            }
            // add  * kg
            kg = Integer.parseInt(kgsVal.get(i).getText().toString());
            if (kg <= 0)
                kg = 1;
            workoutVolume = workoutVolume + (exSumNumOfReps *  kg);

        }


    }

    private LinearLayout showTitle(LinearLayout newExlinearLayout) {
        TextView tv = new TextView(this);
        LinearLayout.LayoutParams tvParams = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setTv(planTitle);

        tvParams.topMargin = dp2px(6);
        tvParams.weight = 2;

        tv.setTextSize(dp2px(10));
//        tv.setBackgroundColor(Color.parseColor("#e7e9eb"));
        tv.setTextColor(Color.parseColor("#000000"));
        tv.setText(planTitle);


        tv.setLayoutParams(tvParams);



//        tv.setText(planTitle);
//        tv.setLayoutParams(tvParams);

        newExlinearLayout.addView(tv);

        newExlinearLayout = addUnderLine(newExlinearLayout);

        return newExlinearLayout;
    }

    private LinearLayout addUnderLine(LinearLayout newExlinearLayout) {
        View v = new View(this);
        LinearLayout.LayoutParams vParams = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        vParams.height = dp2px(1);
        v.setBackgroundColor(Color.parseColor("#000000"));
        v.setLayoutParams(vParams);
        newExlinearLayout.addView(v);
        return newExlinearLayout;
    }

    private LinearLayout showExercisesUI(LinearLayout newExlinearLayout) {
        int numOfSets = 5;


        for(int i = 0; i < exNames.size(); i++){

            // tv - ex name
            TextView tv = new TextView(this);
            tv = setTv(exNames.get(i));

            newExlinearLayout.addView(tv);

            //kgsVal
            TextView tvKg = new TextView(this);
            tvKg = setTv("Kg: ");
            newExlinearLayout.addView(tvKg);

            // add kg input:
            EditText etKg = new EditText(this);
            etKg = setEt(etKg, i);
            etKg.setInputType(InputType.TYPE_CLASS_NUMBER);

            kgsVal.add(etKg);
            newExlinearLayout.addView(etKg);


            // input reps
            TextView tvInput = new TextView(this);
            tvInput = setTv("Reps: ");

            LinearLayout InputRepslinearLayout = new LinearLayout(this);
            InputRepslinearLayout = setLayoutHorizontal(InputRepslinearLayout);
            newExlinearLayout.addView(tvInput);

            // fetch regular num of sets - for now numOfSets = 4
            InputRepslinearLayout = createRepsInputs(InputRepslinearLayout, numOfSets, i);

            newExlinearLayout.addView(InputRepslinearLayout);


        }


        return newExlinearLayout;
    }



    private LinearLayout createRepsInputs(LinearLayout inputRepslinearLayout, int numOfSets, int i) {
        List<EditText> repsForEx = new ArrayList<>();

        for (int j = 0; j < numOfSets ; j++) {
            EditText etReps = new EditText(this);
            etReps = setEt(etReps, i);
            etReps.setInputType(InputType.TYPE_CLASS_NUMBER);


            repsForEx.add(etReps);
            inputRepslinearLayout.addView(etReps);
        }
        repsVal.add(repsForEx);
        return inputRepslinearLayout;
    }

    private EditText setEt(EditText et, int id) {
        et.setId(id);
        LinearLayout.LayoutParams etParams = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        etParams.weight = 1;
        et.setEms(8);
        et.setTextSize(dp2px(6));
        et.setPadding(8,8,8,8);
        etParams.setMarginEnd(dp2px(40));
        et.setHint("");
        et.setBackgroundColor(Color.parseColor("#e7e9eb"));
        et.setLayoutParams(etParams);
        return et;
    }

    private TextView setTv(String text) {
        TextView tv = new TextView(this);
        LinearLayout.LayoutParams tvParams = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tvParams.leftMargin = dp2px(10);
        tvParams.weight = 1;
        tv.setTextSize(dp2px(6));
        tv.setText(text);
//        tv.setBackgroundColor(Color.parseColor("#e7e9eb"));
        tv.setLayoutParams(tvParams);
        return tv;
    }

    private LinearLayout setLayoutHorizontal(LinearLayout inputSetlinearLayout) {
        inputSetlinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams llparams = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        llparams.topMargin = dp2px(40);
        inputSetlinearLayout.setLayoutParams(llparams);
        return inputSetlinearLayout;
    }

    private LinearLayout setLayout(LinearLayout newExlinearLayout) {
        newExlinearLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams llparams = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        llparams.topMargin = dp2px(40);
        newExlinearLayout.setLayoutParams(llparams);
        return newExlinearLayout;
    }

    private void getPlanData() {
        userId = getIntent().getStringExtra("userId");

        plan = (Plan) getIntent().getSerializableExtra("plan");

        // retrieve plan from db

        planTitle = plan.getTitle();

        exNames = plan.getExNames();



        showUI();




    }

    private int dp2px(int dpVal){
        Resources r = this.getResources();
        int pxVal = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dpVal,
                r.getDisplayMetrics()
        );
        return pxVal;
    }
}