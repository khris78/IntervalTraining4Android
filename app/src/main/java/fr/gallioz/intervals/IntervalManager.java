package fr.gallioz.intervals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fr.gallioz.intervals.IntervalType.RUN;
import static fr.gallioz.intervals.IntervalType.WALK;
import static fr.gallioz.intervals.IntervalType.WARM_UP;

public class IntervalManager {

    private static final long MINUTE = 60000L;
    private static final long SECOND = 1000L;

    private static Map<Integer, List<IntervalDescription>> mapWeeks = new HashMap<Integer, List<IntervalDescription>>();

    static {
        Number[][] lstWeeks = new Number[][] {
                new Integer[] {  2,  2,  2,  2,  2,  2,  2,  3 } ,
                new Integer[] {  2,  2,  2,  2,  2,  2,  2,  2,  1,  3 } ,
                new Integer[] {  3,  2,  3,  2,  3,  2,  3,  3 } ,
                new Integer[] {  4,  2,  3,  2,  3,  2,  4,  3 } ,
                new Integer[] {  5,  2,  3,  2,  4,  2,  4,  3 } ,
                new Integer[] {  5,  2,  3,  2,  5,  2,  5,  3 } ,
                new Integer[] {  5,  2,  7,  2,  5,  2,  5,  3 } ,
                new Integer[] {  6,  2, 10,  2,  5,  1,  4,  3 } ,
                new Integer[] {  6,  2, 12,  2,  6,  2,  4,  3 } ,
                new Integer[] {  9,  1, 10,  2,  6,  2,  8,  3 } ,
                new Integer[] { 10,  2, 10,  2, 10,  2,  5,  3  } ,
                new Integer[] { 12,  2, 12,  2, 12,  2,  5,  3  } ,
                new Integer[] { 15,  2, 15,  2, 16,  3 } ,
                new Integer[] { 20,  2, 20,  2, 10,  3 } ,
                new Integer[] { 25,  2, 25,  2,  2,  3 } ,
                new Integer[] { 28,  4, 26,  3  },
                new Integer[] { 60,  3  },
                new Number [] {  0.1, 0.05, 0.1, 0.02, 0.1, 0.05, 0.1, 0.05, 0.1, 0.05,  0.1  } // for tests
        };
        IntervalType types[] = new IntervalType[] { RUN, WALK };
        for (int s = 0 ; s < lstWeeks.length ; s++) {
            Number[] week = lstWeeks[s];
            List<IntervalDescription> lst = new ArrayList<IntervalDescription>(week.length + 1);
            lst.add(new IntervalDescription(WARM_UP, (long) (10.0 * SECOND)));
            int type = 0;
            for (int i = 0 ; i < week.length ; i++) {
                lst.add(new IntervalDescription(types[type], (long) (week[i].doubleValue() * MINUTE)));
                type = 1 - type;
            }
            mapWeeks.put(s + 1, lst);
        }
    }

    public static List<IntervalDescription> getWeekIntervals(int i) {
        List<IntervalDescription>  ret = mapWeeks.get(i);
        if (ret == null) {
            ret = new ArrayList<IntervalDescription>(0);
        }
        return ret;
    }
}
