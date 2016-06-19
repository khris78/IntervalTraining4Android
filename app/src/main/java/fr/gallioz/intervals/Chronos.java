/*
 *
 *   Copyright 2012 Pierre-Yves Le Dévéhat
 *
 *     This file is part of Chronos.
 *
 *
 *
 *   Chronos is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   Chronos is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with Chronos.  If not, see <http://www.gnu.org/licenses/>.
 */

package fr.gallioz.intervals;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ScrollView;
import android.widget.Spinner;
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

/**
 * Based initially on https://github.com/pyledevehat/chronos.
 */
public class Chronos extends Activity {

    private static final int STOPPED_STATE = 0;
    private static final int RUNNING_STATE = 1;
    private static final int SUSPENDED_STATE = 2;

    private int state = STOPPED_STATE;

    private static final int COUNT_STOPPED_COLOR = Color.parseColor("#5f9c97");
    private static final int LIST_BACKGROUND_SELECTED = Color.LTGRAY;
    private static final int LIST_BACKGROUND_UNSELECTED = Color.TRANSPARENT;

    private ScrollView svList;
    private LinearLayout llList;
    private TextView countText;
    private TextView countInvText;
    private Button startBtn;
    private CheckBox cbCountDown;
    private CheckBox cbFractions;

    private int curInterval = 0;
    private List<Long> dingBeforeIntervalEnd = new LinkedList<Long>();
    private long displayedTime = 0;
    private long startTime = 0;
    private long timeBeforeSuspension = 0;
    private boolean countDown = true;
    private boolean showFractions = true;

    private Timer t = new Timer();

    private List<IntervalDescription> intervals = null;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        AppliResources.initialize(this.getApplicationContext());
        SoundManager.initialize(this.getApplicationContext());

        startBtn = (Button) this.findViewById(R.id.startBtn);
        Button reinit = (Button) this.findViewById(R.id.reinitBtn);
        Button quit = (Button) this.findViewById(R.id.quitBtn);

        cbCountDown = (CheckBox) this.findViewById(R.id.checkBoxCountDown);
        cbFractions= (CheckBox) this.findViewById(R.id.checkBoxFractions);

        countText = (TextView) this.findViewById(R.id.count);
        countInvText = (TextView) this.findViewById(R.id.invertedCount);

        Spinner spinnerWeek = (Spinner) this.findViewById(R.id.spinnerWeek);
        initializeSpinnerWeek(spinnerWeek);
        spinnerWeek.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                intervals = IntervalManager.getWeekIntervals(position + 1);
                reinit();
                initializeIntervalsList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                intervals = null;
                reinit();
                initializeIntervalsList();
            }

        });

        svList = (ScrollView) this.findViewById(R.id.scrollView);
        llList = (LinearLayout) this.findViewById(R.id.listCount);

        intervals = IntervalManager.getWeekIntervals(17);

        initializeIntervalsList();

        launch();

        startBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                switch(state) {
                    case STOPPED_STATE:
                        if (intervals.size() > 0) {
                            state = RUNNING_STATE;
                            Chronos.this.startBtn.setText(R.string.stop);
                            setCurInterval(0);
                            Chronos.this.displayedTime = 0;
                            Chronos.this.startTime = System.currentTimeMillis();
                            Chronos.this.timeBeforeSuspension = 0;
                            llList.getChildAt(curInterval).setBackgroundColor(LIST_BACKGROUND_SELECTED);
                            setTextViewColorAccordingToType(null, intervals.get(0).getType());
                        } else {
                            Toast.makeText(getBaseContext(), "No interval to play", Toast.LENGTH_LONG).show();
                        }
                        break;
                    case RUNNING_STATE:
                        state = SUSPENDED_STATE;
                        Chronos.this.startBtn.setText(R.string.reStart);
                        Chronos.this.timeBeforeSuspension = (System.currentTimeMillis() - Chronos.this.startTime);
                        setTextColor(null, COUNT_STOPPED_COLOR);
                        break;
                    case SUSPENDED_STATE:
                        state = RUNNING_STATE;
                        Chronos.this.startBtn.setText(R.string.stop);
                        Chronos.this.startTime = System.currentTimeMillis() - Chronos.this.timeBeforeSuspension;
                        Chronos.this.timeBeforeSuspension = 0;
                        setTextColor(null, intervals.get(curInterval).getType().getColor());
                }
            }
        });

        reinit.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                reinit();
            }
        });

        quit.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                Chronos.this.finish();
            }
        });

        cbCountDown.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                countDown = b;
            }
        });

        cbFractions.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                showFractions = b;
                if (state == STOPPED_STATE) {
                    reinit();
                } else {
                    displayedTime = -1;
                }
            }
        });

    }



    private void initializeSpinnerWeek(Spinner spinnerWeek) {
        ArrayAdapter<String> adapter = new  ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerWeek.setAdapter(adapter);

        for (int i = 1; IntervalManager.getWeekIntervals(i).size() > 0 ; i++) {
            adapter.add(getString(R.string.week) + " " + i);
        }
        adapter.notifyDataSetChanged();
    }

    private void reinit() {
        state = STOPPED_STATE;
        Chronos.this.startBtn.setText(R.string.start);
        setTextColor(null, COUNT_STOPPED_COLOR);
        writeTimeToTextView(null, null, 0, null);
        setCurInterval(-1);
    }

    private void initializeIntervalsList() {
        llList.removeAllViews();

        if (intervals == null) {
            return;
        }

        long total = 0L;
        for (int i = 0 ; i < intervals.size() ; i++) {
            IntervalDescription iDesc = intervals.get(i);
            total += iDesc.getTimeLength();

            TextView tv = new TextView(Chronos.this);
            writeTimeToTextView(tv, i, iDesc.getTimeLength(), iDesc.getType().getLabel());
            setTextViewColorAccordingToType(tv, iDesc.getType());

            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
            tv.setTypeface(Typeface.DEFAULT_BOLD);

            Chronos.this.llList.addView(tv);
        }

        // Display the total
        TextView tv = new TextView(Chronos.this);
        writeTimeToTextView(tv, "T", total, AppliResources.getContext().getString(R.string.total));
        tv.setTextColor(Color.LTGRAY);

        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
        tv.setTypeface(Typeface.DEFAULT_BOLD);

        Chronos.this.llList.addView(tv);
    }

    private void setTextViewColorAccordingToType(TextView tv, IntervalType type) {
        if (type != null) {
            setTextColor(tv, type.getColor());
        } else {
            setTextColor(tv, Color.LTGRAY);
        }
    }

    private void setTextColor(TextView tv, int color) {
        if (tv == null) {
            countText.setTextColor(color);
            countInvText.setTextColor(color);
        } else {
            tv.setTextColor(color);
        }
    }

    private void setCurInterval(int newCurInterval) {
        if (curInterval >= 0 &&  curInterval < llList.getChildCount()) {
            llList.getChildAt(curInterval).setBackgroundColor(LIST_BACKGROUND_UNSELECTED);
        }

        curInterval = newCurInterval;

        if (curInterval >= 0 &&  curInterval < intervals.size()) {
            View curTime = llList.getChildAt(curInterval);
            curTime.setBackgroundColor(LIST_BACKGROUND_SELECTED);
            dingBeforeIntervalEnd.clear();
            IntervalDescription iDesc = intervals.get(curInterval);
            for (int i = 3 ; i > 0 ; i--) {
                long t = iDesc.getTimeLength() - i * 1500;
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

    private void writeTimeToTextView(TextView tv, Object index, long timeLength, String label) {
        long val = timeLength;
        long cs = (val % 1000) / 10;
        val = val /1000;
        long s = val % 60;
        val = val / 60;
        long m = val % 60;
        val = val / 60;
        long h = val;

        StringBuilder sb = new StringBuilder();
        if (index != null) {
            if (index instanceof Number) {
                sb.append(((Number) index).intValue() + 1);
            } else {
                sb.append(index);
            }
            sb.append(".   ");
        }
        if (h < 10) {
            sb.append('0');
        }
        sb.append(h).append(':');
        if (m < 10) {
            sb.append('0');
        }
        sb.append(m).append('\'');
        if (s < 10) {
            sb.append('0');
        }
        sb.append(s).append('"');
        if (showFractions) {
            if (cs < 10) {
                sb.append('0');
            }
            sb.append(cs);
        }

        if (label != null) {
            sb.append(" \u21D2 ").append(label);
        }

        if (tv == null) {
            String v = sb.toString();
            countText.setText(v);
            countInvText.setText(v);
        } else {
            tv.setText(sb.toString());
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        t.cancel();
    }

    private void launch() {
        t.schedule(new TimerTask(){
            @Override
            public void run() {

                Chronos.this.runOnUiThread(new Runnable() {
                    public void run() {

                        if (curInterval < 0 || curInterval >= intervals.size()) {
                            return;
                        }

                        long timeToBeDisplayed;
                        switch(state) {
                            case RUNNING_STATE :
                                timeToBeDisplayed = System.currentTimeMillis() - startTime;
                                break;
                            case SUSPENDED_STATE :
                                timeToBeDisplayed = Chronos.this.timeBeforeSuspension;
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
                        while (timeToBeDisplayed >= iDesc.getTimeLength()) {
                            timeToBeDisplayed -= iDesc.getTimeLength();
                            startTime += iDesc.getTimeLength();

                            setCurInterval(curInterval + 1);
                            intervalChanged = true;

                            if (curInterval < intervals.size()) {
                                iDesc = intervals.get(curInterval);
                            } else {
                                state = STOPPED_STATE;
                                startBtn.setText(R.string.reStart);
                                countText.setText(R.string.finished);
                                countInvText.setText(R.string.finished);
                                setTextColor(null, COUNT_STOPPED_COLOR);
                                displayedTime = 0;
                                setCurInterval(-1);
                                SoundManager.getInstance().playSound(R.raw.dingding);
                                return;
                            }
                        }
                        if (intervalChanged) {
                            setTextViewColorAccordingToType(null, iDesc.getType());
                            SoundManager.getInstance().playSound(R.raw.dingding);
                        }

                        // memorize the current time in order to avoid a refresh if unnecessary
                        displayedTime = timeToBeDisplayed;

                        if (! dingBeforeIntervalEnd.isEmpty() && timeToBeDisplayed > dingBeforeIntervalEnd.get(0)) {
                            dingBeforeIntervalEnd.remove(0);
                            SoundManager.getInstance().playSound(R.raw.ding);
                        }
                        if (countDown) {
                            timeToBeDisplayed = iDesc.getTimeLength() - timeToBeDisplayed;
                        }
                        writeTimeToTextView(null, null, timeToBeDisplayed, null);
                    }
                });
            }
        }, 0, 93);
    }

}
