package fr.gallioz.intervals.activities;

import android.graphics.Color;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import fr.gallioz.intervals.R;
import fr.gallioz.intervals.beans.IntervalDescription;
import fr.gallioz.intervals.beans.IntervalTypeDescription;
import fr.gallioz.intervals.utils.AppliResources;
import fr.gallioz.intervals.utils.DisplayTime;

public abstract class Utils {

    public static void initializeIntervalsList(LinearLayout intervalLinearLayout, List<IntervalDescription> someIntervals) {
        intervalLinearLayout.removeAllViews();

        if (someIntervals == null) {
            return;
        }

        long total = 0L;
        for (int i = 0 ; i < someIntervals.size() ; i++) {
            IntervalDescription iDesc = someIntervals.get(i);
            total += iDesc.getMillisec();

            TextView tv = new TextView(intervalLinearLayout.getContext());
            writeTimeToTextView(tv, i, iDesc.getMillisec(), iDesc.getType().getLabel());
            setTextViewColorAccordingToType(tv, iDesc.getType());

            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
            tv.setTypeface(Typeface.DEFAULT_BOLD);

            intervalLinearLayout.addView(tv);
        }

        // Display the total
        TextView tv = new TextView(intervalLinearLayout.getContext());
        writeTimeToTextView(tv, "T", total, AppliResources.getContext().getString(R.string.total));
        tv.setTextColor(Color.LTGRAY);

        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
        tv.setTypeface(Typeface.DEFAULT_BOLD);

        intervalLinearLayout.addView(tv);
    }

    public static void writeTimeToTextView(TextView tv, Object index, long timeLength, String label) {

        DisplayTime displayTimeUtil = new DisplayTime();

        displayTimeUtil.setTime(timeLength);
        String v = displayTimeUtil.getTimeRepresentation();

        if (index != null) {
            v = index + ". " + v;
        }
        if (label != null) {
            v += " \u21D2 " + label;
        }

        tv.setText(v);
    }

    public static void setTextViewColorAccordingToType(TextView tv, IntervalTypeDescription type) {
        int color;
        if (type != null) {
            color = type.getColor();
        } else {
            color = Color.LTGRAY;
        }
        tv.setTextColor(color);
    }
}
