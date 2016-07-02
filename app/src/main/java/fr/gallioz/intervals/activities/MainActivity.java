/*
 *
 *   Copyright 2016 Christian Gallioz
 *
 *     This file is part of IntervalTraining4Android.
 *
 *
 *
 *   IntervalTraining4Android is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   IntervalTraining4Android is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with IntervalTraining4Android. If not, see <http://www.gnu.org/licenses/>.
 *
 *   This file, despite a lot of modifications, may still contain some code
 *   from Chronos (by Pierre-Yves Le Dévéhat), which is released under the same licence.
 *   See https://github.com/pyledevehat/chronos
 */

package fr.gallioz.intervals.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.view.View.OnClickListener;
import android.graphics.Color;
import android.graphics.Typeface;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import fr.gallioz.intervals.beans.TrainingPlanDescription;
import fr.gallioz.intervals.beans.TrainingSessionDescription;
import fr.gallioz.intervals.utils.AppliResources;
import fr.gallioz.intervals.R;
import fr.gallioz.intervals.utils.DisplayTime;
import fr.gallioz.intervals.utils.SoundManager;
import fr.gallioz.intervals.beans.IntervalDescription;
import fr.gallioz.intervals.beans.IntervalTypeDescription;
import fr.gallioz.intervals.db.DbAccess;

import static fr.gallioz.intervals.utils.AppliResources.*;
import static fr.gallioz.intervals.utils.Constants.*;

/**
 * This is the main activity, which contains the 2 chronometers.
 */
public class MainActivity extends Activity {

    private ScrollView svList;
    private LinearLayout llList;
    private TextView countText;
    private TextView countInvText;
    private Button startBtn;
    private CheckBox countDownCb;
    private CheckBox fractionsCb;

    private List<Long> dingBeforeIntervalEnd = new LinkedList<Long>();

    private long curTrainingPlan = 0;
    private long curSession = 0;
    private int curInterval = 0;
    private int state = STOPPED_STATE;
    private long displayedTime = 0L;
    private long startTime = 0;
    private long timeBeforeSuspension = 0;
    private boolean countDown = true;
    boolean showFractions = false;

    private Timer t = null;
    DisplayTime displayTimeUtil = new DisplayTime();

    private List<IntervalDescription> intervals = null;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Log.d(LOG_TAG, ">> MainActivity.onCreate");
        AppliResources.initialize(this.getApplicationContext());
        SoundManager.initialize(this.getApplicationContext());

        DbAccess dbAccess = getDbAccess();
        if (dbAccess == null) {
            dbAccess = new DbAccess(getApplicationContext());
            setDbAccess(dbAccess);
        }

        svList = (ScrollView) this.findViewById(R.id.scrollView);
        llList = (LinearLayout) this.findViewById(R.id.listCount);

        startBtn = (Button) this.findViewById(R.id.startBtn);
        Button reinitBtn = (Button) this.findViewById(R.id.reinitBtn);
        Button quitBtn = (Button) this.findViewById(R.id.quitBtn);
        Button selSessionBtn = (Button) this.findViewById(R.id.selSessionBtn);

        countDownCb = (CheckBox) this.findViewById(R.id.checkBoxCountDown);
        fractionsCb = (CheckBox) this.findViewById(R.id.checkBoxFractions);

        countText = (TextView) this.findViewById(R.id.count);
        countInvText = (TextView) this.findViewById(R.id.invertedCount);

        startBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                Log.d(LOG_TAG, "!! MainActivity.startBtn.click");

                switch(state) {
                    case STOPPED_STATE:
                        if (intervals.size() > 0) {
                            state = RUNNING_STATE;
                            MainActivity.this.startBtn.setText(R.string.stop);
                            setCurInterval(0);

                            MainActivity.this.displayedTime = 0L;
                            MainActivity.this.startTime = System.currentTimeMillis();
                            MainActivity.this.timeBeforeSuspension = 0;
                            llList.getChildAt(curInterval).setBackgroundColor(LIST_BACKGROUND_SELECTED);
                            setChronoColorAccordingToType(intervals.get(0).getType());
                        } else {
                            Toast.makeText(getBaseContext(), "No interval to play", Toast.LENGTH_LONG).show();
                        }
                        break;
                    case RUNNING_STATE:
                        state = SUSPENDED_STATE;
                        MainActivity.this.startBtn.setText(R.string.reStart);
                        MainActivity.this.timeBeforeSuspension = (System.currentTimeMillis() - MainActivity.this.startTime);
                        setChronoColor(COUNT_STOPPED_COLOR);
                        break;
                    case SUSPENDED_STATE:
                        state = RUNNING_STATE;
                        MainActivity.this.startBtn.setText(R.string.stop);
                        MainActivity.this.startTime = System.currentTimeMillis() - MainActivity.this.timeBeforeSuspension;
                        MainActivity.this.timeBeforeSuspension = 0;
                        setChronoColor(intervals.get(curInterval).getType().getColor());
                }
                getDbAccess().setParameterInteger("state", state);
                getDbAccess().setParameterLong("startTime", startTime);
                getDbAccess().setParameterLong("timeBeforeSuspension", timeBeforeSuspension);
            }
        });

        reinitBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                Log.d(LOG_TAG, "!! MainActivity.reinitBtn.click");
                reinit();
            }
        });

        quitBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                Log.d(LOG_TAG, "!! MainActivity.quitBtn.click");
                MainActivity.this.finish();
            }
        });

        selSessionBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                Log.d(LOG_TAG, "!! MainActivity.selSessionBtn.click");
                Intent intent = new Intent(MainActivity.this, PlanEditorActivity.class);
                startActivity(intent);
            }
        });

        countDownCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Log.d(LOG_TAG, "!! MainActivity.countDownCb.change");

                countDown = b;
                getDbAccess().setParameterBoolean("countDown", countDown);
            }
        });
        countDownCb.setChecked(countDown);

        fractionsCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Log.d(LOG_TAG, "!! MainActivity.fractionsCb.change");

                displayTimeUtil.setShowMillisec(b);
                getDbAccess().setParameterBoolean("showFractions", b);

                if (state == STOPPED_STATE) {
                    reinit();
                } else {
                    displayedTime = -1L;
                }
            }
        });
        fractionsCb.setChecked(showFractions);
        displayTimeUtil.setShowMillisec(showFractions);
        Log.d(LOG_TAG, "<< MainActivity.onCreate");
    }

    @Override
    public void onResume() {
        Log.d(LOG_TAG, "MainActivity.onResume");
        super.onResume();

        // Retrieves the saved values from the provided bundle
        DbAccess dbAccess = getDbAccess();
        curTrainingPlan = dbAccess.getParameterLong("curTrainingPlan", curTrainingPlan);
        curSession = dbAccess.getParameterLong("curSession", curSession);
        curInterval = dbAccess.getParameterInt("curInterval", curInterval);

        state = dbAccess.getParameterInt("state", state);
        displayedTime = 0L;
        startTime = dbAccess.getParameterLong("startTime", startTime);
        timeBeforeSuspension = dbAccess.getParameterLong("timeBeforeSuspension", timeBeforeSuspension);
        countDown = dbAccess.getParameterBoolean("countDown", countDown);
        showFractions = dbAccess.getParameterBoolean("showFractions", showFractions);

        intervals = dbAccess.retrieveIntervalsForSession(curSession);
        if (curInterval >= intervals.size()) {
            setCurInterval(0);
            state = STOPPED_STATE;
            dbAccess.setParameterInteger("state", state);
        }

        Utils.initializeIntervalsList(llList, intervals);

        switch(state) {
            case STOPPED_STATE :
                writeTimeToTextView(0L);
            case SUSPENDED_STATE :
                setChronoColor(COUNT_STOPPED_COLOR);
                break;
        }

        countDownCb.setChecked(countDown);
        fractionsCb.setChecked(showFractions);
        displayTimeUtil.setShowMillisec(showFractions);

        TrainingPlanDescription aPlan = getDbAccess().getPlan(curTrainingPlan);
        if (aPlan != null) {
            TextView planLbl = (TextView) this.findViewById(R.id.planLbl);
            planLbl.setText(aPlan.getName());

            TrainingSessionDescription aSession = getDbAccess().getSession(curSession);
            if (aSession != null) {
                TextView sessionLbl = (TextView) this.findViewById(R.id.sessionLbl);
                sessionLbl.setText(aSession.getName());
            }
        }

        launch();
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstance) {
        Log.d(LOG_TAG, "MainActivity.onRestoreInstanceState");

        super.onRestoreInstanceState(savedInstance);

        onResume();
    }

    private void reinit() {
        state = STOPPED_STATE;
        startBtn.setText(R.string.start);
        setChronoColor(COUNT_STOPPED_COLOR);
        writeTimeToTextView(0);
        setCurInterval(-1);
    }


    private void setChronoColorAccordingToType(IntervalTypeDescription type) {
        int color;
        if (type != null) {
            color = type.getColor();
        } else {
            color = Color.LTGRAY;
        }
        setChronoColor(color);
    }

    private void setChronoColor(int color) {
        countText.setTextColor(color);
        countInvText.setTextColor(color);
    }

    private void setCurInterval(int newCurInterval) {
        if (curInterval >= 0 &&  curInterval < llList.getChildCount()) {
            llList.getChildAt(curInterval).setBackgroundColor(LIST_BACKGROUND_UNSELECTED);
        }

        curInterval = newCurInterval;
        getDbAccess().setParameterInteger("curInterval", curInterval);

        if (curInterval >= 0 &&  curInterval < intervals.size()) {
            View curTime = llList.getChildAt(curInterval);
            curTime.setBackgroundColor(LIST_BACKGROUND_SELECTED);
            dingBeforeIntervalEnd.clear();
            IntervalDescription iDesc = intervals.get(curInterval);
            for (int i = 3 ; i > 0 ; i--) {
                long t = iDesc.getMillisec() - i * 1500;
                if (t > 2500) {
                    dingBeforeIntervalEnd.add(t);
                }
            }
            View visibleTarget =
                    (curInterval + 1 < llList.getChildCount()
                            ? llList.getChildAt(curInterval + 1)
                            : llList.getChildAt(curInterval));
            Rect rect = new Rect();
            visibleTarget.getHitRect(rect);
            svList.requestChildRectangleOnScreen(llList, rect, false);
            curTime.getHitRect(rect);
            svList.requestChildRectangleOnScreen(llList, rect, false);
        }
    }

    private void writeTimeToTextView(long timeLength) {

        displayTimeUtil.setTime(timeLength);

        if (displayTimeUtil.hasChanged()) {
            String v = displayTimeUtil.getTimeRepresentation();
            countText.setText(v);
            countInvText.setText(v);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        t.cancel();
    }

    private void launch() {
        if (t != null) {
            return;
        }

        t = new Timer();

        t.schedule(new TimerTask(){
            @Override
            public void run() {

                MainActivity.this.runOnUiThread(new Runnable() {
                    public void run() {

                        if (curInterval < 0 || curInterval >= intervals.size()) {
                            Toast.makeText(getApplicationContext(), R.string.msgNoInterval, Toast.LENGTH_LONG);
                            return;
                        }

                        long timeToBeDisplayed;
                        switch(state) {
                            case RUNNING_STATE :
                                timeToBeDisplayed = System.currentTimeMillis() - startTime;
                                break;
                            case SUSPENDED_STATE :
                                timeToBeDisplayed = MainActivity.this.timeBeforeSuspension;
                                break;
                            default :
                                timeToBeDisplayed = 0;
                        }

                        // If no modification, it's finished
                        if (displayedTime == timeToBeDisplayed) {
                            return;
                        }

                        // chrono is running

                        // check the end of the current interval
                        IntervalDescription iDesc = intervals.get(curInterval);
                        boolean intervalChanged = false;
                        while (timeToBeDisplayed >= iDesc.getMillisec()) {
                            timeToBeDisplayed -= iDesc.getMillisec();
                            startTime += iDesc.getMillisec();

                            setCurInterval(curInterval + 1);
                            intervalChanged = true;

                            if (curInterval < intervals.size()) {
                                iDesc = intervals.get(curInterval);
                            } else {
                                state = STOPPED_STATE;
                                startBtn.setText(R.string.reStart);
                                countText.setText(R.string.finished);
                                countInvText.setText(R.string.finished);
                                setChronoColor(COUNT_STOPPED_COLOR);
                                displayedTime = 0;
                                setCurInterval(-1);
                                SoundManager.getInstance().playSound(R.raw.dingding);
                                return;
                            }
                        }
                        if (intervalChanged) {
                            setChronoColorAccordingToType(iDesc.getType());
                            SoundManager.getInstance().playSound(R.raw.dingding);
                        }

                        // memorize the current time in order to avoid a refresh if unnecessary
                        displayedTime = timeToBeDisplayed;

                        if (! dingBeforeIntervalEnd.isEmpty() && timeToBeDisplayed > dingBeforeIntervalEnd.get(0)) {
                            dingBeforeIntervalEnd.remove(0);
                            SoundManager.getInstance().playSound(R.raw.ding);
                        }
                        if (countDown) {
                            timeToBeDisplayed = iDesc.getMillisec() - timeToBeDisplayed;
                        }
                        writeTimeToTextView(timeToBeDisplayed);
                    }
                });
            }
        }, 0, 93);
    }

}
