package fr.gallioz.intervals;

/**
 * A description of intervals.
 * <ul>
 *     <li>type (warm-up / run / rest)</li>
 *     <li>time length (millisec)</li>
 * </ul>
 */
public class IntervalDescription {
    private IntervalType type;
    private long timeLength;

    public IntervalDescription(IntervalType type, long timeLength) {
        this.type = type;
        this.timeLength = timeLength;
    }

    @Override
    public String toString() {
        return "IntervalDescription{" +
                "type='" + type + '\'' +
                ", timeLength=" + timeLength +
                '}';
    }

    public IntervalType getType() {
        return type;
    }

    public long getTimeLength() {
        return timeLength;
    }
}
