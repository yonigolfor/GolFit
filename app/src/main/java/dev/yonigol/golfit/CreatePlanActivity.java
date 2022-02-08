package dev.yonigol.golfit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.DatabaseReference;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import androidx.core.content.res.ResourcesCompat;
import dev.yonigol.golfit.MyComponents.Plan;

public class CreatePlanActivity extends AppCompatActivity {

    private MaterialButton addExBtn;
    private MaterialButton generatePlanBtn;

    private int numOfExerciseOnUI = 6;
    private List<EditText> exNames = new ArrayList<>();
    private EditText planTitle;

    private LinearLayout exercisesLinearLayout;

    private TextView errMsgEx;
    private TextView errMsgTitle;

    private ScrollView scrollView;

    private FirebaseDatabase rootNode;
    private DatabaseReference reference;

    private String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_plan);

        findViews();

    }

    @Override
    public void onBackPressed() {
        goMainScreen();
    }


    private void findViews() {
        scrollView = findViewById(R.id.create_plan_sv);
        getUserId();

        exNames.add(findViewById(R.id.ex1_name_et));
        exNames.add(findViewById(R.id.ex2_name_et));
        exNames.add(findViewById(R.id.ex3_name_et));
        exNames.add(findViewById(R.id.ex4_name_et));
        exNames.add(findViewById(R.id.ex5_name_et));
        exNames.add(findViewById(R.id.ex6_name_et));

        planTitle = findViewById(R.id.plan_title_et);

        exercisesLinearLayout = (LinearLayout) findViewById(R.id.plan_exercises_layout);

        errMsgEx = findViewById(R.id.err_msg_ex);
        errMsgTitle = findViewById(R.id.err_msg_title);



        addExBtn = findViewById(R.id.add_ex_btn);
        addExBtn.setOnClickListener(view -> {
            // add exercise to the xml file
            addExerciseInputUI();
            if (numOfExerciseOnUI == 12)
                addExBtn.setVisibility(View.INVISIBLE);
        });

        generatePlanBtn = findViewById(R.id.generate_plan_btn);
        generatePlanBtn.setOnClickListener(view->{
            cleanErrMsg();
            // check if there are at least 2 exercises and title
            if(checkFill()){ // filled => Generate the new plan
                // add plan to user db
                getPlanData();
                //send user to main activity screen
                goHomePage();
            }
            else{
                // send user to top of the screen.
                scrollView.setSmoothScrollingEnabled(true); // optional
                scrollView.fullScroll(scrollView.FOCUS_UP);
            }

        });
    }

    private void goHomePage() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void getUserId() {
        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
    }

    private void getPlanData() {
        exNames = exNames.stream()
                .filter(en -> en.getText().toString() != null && !en.getText().toString().trim().isEmpty()).collect(Collectors.toList());
        List<String> exNamesList = exNames.stream().map(en->en.getText().toString()).collect(Collectors.toList());

        // update db
        addPlanToDb(exNamesList);
    }


    private void addPlanToDb(List<String> exNamesList) {

        String title = planTitle.getText().toString();
        Plan plan = new Plan(title, exNamesList);
        plan.setgeneralReps(10);
        plan.setgeneralSets(4);


        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference("users");
        reference.child(userId).child("plans").child(title).setValue(plan);



    }

    private void addExerciseInputUI() {
        LinearLayout newExlinearLayout = new LinearLayout(this);
        numOfExerciseOnUI++;

        newExlinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams llparams = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        llparams.topMargin = dp2px(40);
        newExlinearLayout.setLayoutParams(llparams);

        TextView tv = new TextView(this);
        LinearLayout.LayoutParams tvParams = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tvParams.leftMargin = dp2px(10);
        tvParams.weight = 1;
        tv.setTextSize(dp2px(6));
        tv.setText("Ex"+numOfExerciseOnUI+":");
        tv.setLayoutParams(tvParams);


        EditText et = new EditText(this);
        et.setId(numOfExerciseOnUI - 1);
        LinearLayout.LayoutParams etParams = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        etParams.weight = 1;
        et.setEms(8);
        et.setTextSize(dp2px(6));
        et.setPadding(8,8,8,8);
        etParams.setMarginEnd(dp2px(40));
        et.setHint(numOfExerciseOnUI + "th ex name");
        et.setBackgroundColor(Color.parseColor("#e7e9eb"));
        et.setLayoutParams(etParams);

        exNames.add(et);

        // inert new tv + et to layout
        newExlinearLayout.addView(tv);
        newExlinearLayout.addView(et);

        exercisesLinearLayout.addView(newExlinearLayout);
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

    private void cleanErrMsg() {
        errMsgTitle.setText("");
        errMsgEx.setText("");
    }

    private Boolean checkFill() {
        int numOfEx = 0;
        String exName, planName;

        planName = planTitle.getText().toString();

        if(planName != null && !planName.trim().isEmpty()){
            for (int i = 0; i < exNames.size(); i++){
                exName = exNames.get(i).getText().toString();
                if (exName != null && !exName.trim().isEmpty())
                    numOfEx++;
                if (numOfEx == 2)
                    return true;
            }
            errMsgEx.setText("Must have 2 exercises at least!");
            return false;
        }
        errMsgTitle.setText("Please enter plan name!");
        return false;
    }


    private void goMainScreen() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}