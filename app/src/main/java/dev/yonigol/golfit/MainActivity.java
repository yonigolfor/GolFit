package dev.yonigol.golfit;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import dev.yonigol.golfit.MyComponents.Plan;
import dev.yonigol.golfit.MyComponents.User;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
//    private Array userPrograms;
    private TextView userTV;

    private FirebaseDatabase dbRoot;
    private DatabaseReference myRef;

    private Button createProgramBtn;
    private MaterialButton mbTrack;

//    private Button plusBtn;

    private Button planABtn;
    private Button planBBtn;
    private Button planCBtn;
    private Button planDBtn;

    private ArrayList<Button> plansBtns;

    private Dialog removePlanDialog;


    private List<Plan> plansList;
    private String userId = null;
    private int numOfPlans = 0;

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private MaterialButton btnPopupRemove;
    private MaterialButton btnPopupKeep;

    private TextView popupTv;
    private String titleForPopup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            createSignInIntent();
            //userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            //addNewUserToDB(userId);
        }
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            getProgramsInfo();
        }

    }



    private void findViews() {
        setLoadingView();

        userTV = findViewById(R.id.user);
        dbRoot = FirebaseDatabase.getInstance();
        myRef = dbRoot.getReference("users");
        plansList = new ArrayList<>();
        createProgramBtn = findViewById(R.id.create_plan_btn);
        createProgramBtn.setOnClickListener(v-> goCreatePlanScreen());
//        plusBtn = findViewById(R.id.plus_btn); // same functionality
//        plusBtn.setOnClickListener(v-> goCreatePlanScreen());

        mbTrack = findViewById(R.id.btn_track);
        mbTrack.setOnClickListener(v-> goTrackScreen());






        setPlanBtns();





    }

    private void setLoadingView() {
        dialogBuilder = new AlertDialog.Builder(this);
        final View loadingView = getLayoutInflater().inflate(R.layout.activity_splash, null);
        dialogBuilder.setView(loadingView);
        dialog=dialogBuilder.create();
        dialog.show();

    }

    private void setPlanBtns() {
        plansBtns = new ArrayList<>();

        planABtn = findViewById(R.id.planA_btn);
        planBBtn = findViewById(R.id.planB_btn);
        planCBtn = findViewById(R.id.planC_btn);
        planDBtn = findViewById(R.id.planD_btn);

        plansBtns.add(planABtn);
        plansBtns.add(planBBtn);
        plansBtns.add(planCBtn);
        plansBtns.add(planDBtn);


        //invisible all
        for (int i = 0; i < plansBtns.size() ; i++) {
            plansBtns.get(i).setVisibility(View.INVISIBLE);
        }

    }

    private void addNewUserToDB(String uid) {
        myRef.child(userId).setValue(uid);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (FirebaseAuth.getInstance().getCurrentUser() != null && userId == null) {
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            addNewUserToDB(userId);
            getProgramsInfo();
        }
    }

//    private void checkUserHasPrograms() {
//        getProgramsInfo();

//        Log.e("num of plans 2", ""+numOfPlans);
//        if (numOfPlans > 0) {
//            // no programs exist => move to create program screen
//            goCreatePlanScreen();
//
//        } else {
//            // has programs => stay in the screen + show select program button
//        }
//    }



    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            new ActivityResultCallback<FirebaseAuthUIAuthenticationResult>() {
                @Override
                public void onActivityResult(FirebaseAuthUIAuthenticationResult result) {
                    onSignInResult(result);
                }
            }
    );

    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        IdpResponse response = result.getIdpResponse();
        if (result.getResultCode() == RESULT_OK) {
            // Successfully signed in
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            userTV.setText(user.getEmail());
            // ...
        } else {
            userTV.setText("Not Connected");
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
        }
    }

    public void createSignInIntent() {
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(

                new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.FacebookBuilder().build());

        // Create and launch sign-in intent
        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build();
        signInLauncher.launch(signInIntent);
    }

    private void getProgramsInfo() {
        //check in db for user's programs
        // Read from the database

        myRef.child(userId).child("plans").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                numOfPlans = (int)dataSnapshot.getChildrenCount();

                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {

                    Plan p = snapshot.getValue(Plan.class);
                    plansList.add(p);
                    Log.e("plan: ", p.toString());
                }


                if (numOfPlans == 0)
                    goCreatePlanScreen();

                else {
                    // show plans btn ui
                    showPlansBtns();

                    if (plansList.size() == 4)
                        createProgramBtn.setVisibility(View.INVISIBLE);
                    else
                        createProgramBtn.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("value Cancel", "Failed to read value.", error.toException());
            }
        });

        dialog.cancel();

    }

    private void showPlansBtns() {
        for (int i = 0; i < plansList.size() && i < plansBtns.size(); i++) {
            Log.e("i:", ""+i);
            plansBtns.get(i).setVisibility(View.VISIBLE);
            int finalI = i;
            plansBtns.get(i).setOnClickListener(v-> goEnterResultsScreen(plansList.get(finalI)));
            int finalI1 = i;
            plansBtns.get(i).setOnLongClickListener(v->popUpRemovePlan(plansList.get(finalI1).getTitle()));
            plansBtns.get(i).setText(plansList.get(i).getTitle());


        }
    }

    private boolean popUpRemovePlan(String title) {
        Log.e("long click", "just occured");
        titleForPopup = title;

        // shake btn

        // show popup

        final View popupView = getLayoutInflater().inflate(R.layout.popup_remove_plan, null);



        //set dialog options
        btnPopupRemove = popupView.findViewById(R.id.btn_popup_remove);
        btnPopupKeep = popupView.findViewById(R.id.btn_popup_keep);
        popupTv = popupView.findViewById(R.id.popup_title_tv);

        popupTv.setText("Remove " + title + " ?");
        setPopupClick();

        dialogBuilder.setView(popupView);
        dialog=dialogBuilder.create();
        dialog.show();

        return true;
    }

    private void setPopupClick() {
        btnPopupRemove.setOnClickListener(view -> removePlan());
        btnPopupKeep.setOnClickListener(view -> dialog.cancel());
    }

    private void removePlan() {
        Log.e("remove", "removing plan " + titleForPopup + "...");

        // remove from plans btns
        updateplansBtnsUi();

        // find the plan from plan list by the title
        for (int i = 0; i < plansList.size(); i++)
            if(plansList.get(i).getTitle() == titleForPopup)
                plansList.remove(i);

        // update db
        myRef.child(userId).child("plans").setValue(plansList);

        dialog.cancel();

    }

    private void updateplansBtnsUi() {
        for (int i = 0; i < plansBtns.size(); i++) {
            if (plansBtns.get(i).getText().toString() == titleForPopup)
                plansBtns.get(i).setVisibility(View.INVISIBLE);
        }
    }

    private List<List<Integer>> getPlansVolumes() {
        List<List<Integer>> plansVolumes = new ArrayList<>();

        for (int i = 0; i < plansList.size(); i++) {
            if (plansList.get(i).getVolumeHistory() != null)
                plansVolumes.add(plansList.get(i).getVolumeHistory());
        }

        return plansVolumes;
    }

    private void goEnterResultsScreen(Plan plan) {
        Log.e("planToSend:" , plan.toString());
        Intent intent = new Intent(this, EnterResultsActivity.class);
        intent.putExtra("plan", plan);
        intent.putExtra("userId", userId);
        startActivity(intent);
        finish();
    }

    private void goTrackScreen() {
        List<List<Integer>> plansVolumes = getPlansVolumes();

        Intent intent = new Intent(this, TrackActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("plansList", (Serializable) plansList);
        intent.putExtra("plansVolumes", (Serializable) plansVolumes);

        startActivity(intent);
        finish();
    }



    private void goCreatePlanScreen() {
        Intent intent = new Intent(this, CreatePlanActivity.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
        finish();
    }
}