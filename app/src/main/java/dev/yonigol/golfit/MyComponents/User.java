package dev.yonigol.golfit.MyComponents;


import java.util.ArrayList;
import java.util.List;

public class User {

    private int userId;
    private List<Plan> plansList;

    public User(int userId) {

        this.userId = userId;
        this.plansList = new ArrayList<>();
    }

    public void addPlan(Plan plan) {
        plansList.add(plan);
    }

    public boolean removePlan(Plan plan) {
        if(plansList.contains(plan)){
            plansList.remove(plan);
            return true;
        }
        return false;

    }


    public int getUserId() {
        return userId;
    }

    public List<Plan> getPlansList() {
        return plansList;
    }
}
