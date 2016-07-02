package fr.gallioz.intervals.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;

import org.json.JSONException;

import java.util.List;

import fr.gallioz.intervals.R;
import fr.gallioz.intervals.beans.TrainingPlanDescription;
import fr.gallioz.intervals.beans.TrainingSessionDescription;
import fr.gallioz.intervals.utils.IdAndLabel;

import static fr.gallioz.intervals.utils.AppliResources.*;
import static fr.gallioz.intervals.utils.Constants.*;

/** This activity is dedicated to the trainig plan edition */
public class PlanEditorActivity extends Activity {


    private Spinner trainingPlanSpinner;
    private Spinner sessionSpinner;
    private LinearLayout llList;
    private long curTrainingPlan = 0;
    private long curSession = 0;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plan_editor);

        curTrainingPlan = getDbAccess().getParameterLong("curTrainingPlan", curTrainingPlan);
        curSession = getDbAccess().getParameterLong("curSession", curSession);

        llList = (LinearLayout) this.findViewById(R.id.listIntervals);

        trainingPlanSpinner = (Spinner) this.findViewById(R.id.trainingPlanSpinner);
        ArrayAdapter<IdAndLabel> trainingPlanAdapter = new  ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_item);
        trainingPlanAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        trainingPlanSpinner.setAdapter(trainingPlanAdapter);

        List<TrainingPlanDescription>  plans = getDbAccess().getAllPlans();
        for (int i = 0; i < plans.size(); i++) {
            trainingPlanAdapter.add(new IdAndLabel(plans.get(i).getId(), plans.get(i).getName()));
        }
        trainingPlanAdapter.notifyDataSetChanged();

        sessionSpinner = (Spinner) this.findViewById(R.id.sessionSpinner);
        final ArrayAdapter<IdAndLabel> sessionAdapter = new  ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_item);
        sessionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sessionSpinner.setAdapter(sessionAdapter);


        trainingPlanSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        Log.d(LOG_TAG, ">> PlanEditorActivity.trainingPlanSpinner.onItemSelected");

                        IdAndLabel selected = (IdAndLabel) parent.getAdapter().getItem(position);

                        TrainingPlanDescription thePlan = getDbAccess().getPlan(selected.getId());

                        List<TrainingSessionDescription> lstSessions = thePlan.getSessions();

                        ArrayAdapter<IdAndLabel> sessionAdapter = (ArrayAdapter<IdAndLabel>) sessionSpinner.getAdapter();
                        sessionAdapter.clear();
                        for (int i = 0; i < lstSessions.size(); i++) {
                            sessionAdapter.add(new IdAndLabel(lstSessions.get(i).getId(), lstSessions.get(i).getName()));
                        }
                        sessionAdapter.notifyDataSetChanged();

                        if (curTrainingPlan != selected.getId()) {
                            curTrainingPlan = selected.getId();
                            getDbAccess().setParameterLong("curTrainingPlan", selected.getId());
                            sessionSpinner.setSelection(0);
                        } else {
                            ArrayAdapter<IdAndLabel> trainingSessionAdapter = (ArrayAdapter<IdAndLabel> ) sessionSpinner.getAdapter();
                            for (int i = 0; i < trainingSessionAdapter.getCount() ; i++) {
                                if (trainingSessionAdapter.getItem(i).getId() == curSession) {
                                    sessionSpinner.setSelection(i);
                                    break;
                                }
                            }
                        }

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                }
        );

        sessionSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        Log.d(LOG_TAG, ">> PlanEditorActivity.sessionSpinner.onItemSelected");

                        IdAndLabel selected = (IdAndLabel) parent.getAdapter().getItem(position);

                        if (selected.getId() != curSession) {
                            curSession = selected.getId();
                            getDbAccess().setParameterLong("curSession", curSession);
                            getDbAccess().setParameterInteger("state", STOPPED_STATE);
                            getDbAccess().setParameterLong("startTime", 0L);
                            getDbAccess().setParameterLong("timeBeforeSuspension", 0L);
                        }

                        TrainingSessionDescription session = getDbAccess().getSession(curSession);
                        Utils.initializeIntervalsList(llList, session.getIntervals());
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                }
        );


        Button exportBtn = (Button) this.findViewById(R.id.exportBtn);

        exportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LOG_TAG, ">> PlanEditorActivity.trainingPlanSpinner.export");
                TrainingPlanDescription plan = getDbAccess().getAllPlans().get(0);
                try {
                    Log.i(LOG_TAG, plan.toJSON().toString(2));
                } catch(JSONException e) {
                    Log.w(LOG_TAG, "JSONException", e);
                }
            }
        });
    }

    public void onResume() {
        super.onResume();

        curTrainingPlan = getDbAccess().getParameterLong("curTrainingPlan", curTrainingPlan);
        curSession = getDbAccess().getParameterLong("curSession", curSession);

        ArrayAdapter<IdAndLabel> trainingPlanAdapter = (ArrayAdapter<IdAndLabel> ) trainingPlanSpinner.getAdapter();
        for (int i = 0; i < trainingPlanAdapter.getCount() ; i++) {
            if (trainingPlanAdapter.getItem(i).getId() == curTrainingPlan) {
                trainingPlanSpinner.setSelection(i);
                break;
            }
        }

        ArrayAdapter<IdAndLabel> trainingSessionAdapter = (ArrayAdapter<IdAndLabel> ) sessionSpinner.getAdapter();
        for (int i = 0; i < trainingSessionAdapter.getCount() ; i++) {
            if (trainingSessionAdapter.getItem(i).getId() == curSession) {
                sessionSpinner.setSelection(i);
                break;
            }
        }
    }
}