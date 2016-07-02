package fr.gallioz.intervals.beans;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A description of intervals.
 * <ul>
 *     <li>type (warm-up / run / rest)</li>
 *     <li>time length (millisec)</li>
 * </ul>
 */
public class IntervalDescription {
    private long id = -1;
    private IntervalTypeDescription type;
    private int order;
    private long millisec;

    public IntervalDescription(long id, IntervalTypeDescription type, int order, long millisec) {
        this.id = id;
        this.type = type;
        this.order = order;
        this.millisec = millisec;
    }

    public IntervalDescription(IntervalTypeDescription type, int order, long millisec) {
        this.type = type;
        this.order = order;
        this.millisec = millisec;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject ret = new JSONObject();
        ret.put("type", type.getLabel());
        ret.put("order", order);
        ret.put("millisec", millisec);

        return ret;
    }

    @Override
    public String toString() {
        return "IntervalDescription{" +
                "type=" + type +
                ", order=" + order +
                ", millisec=" + millisec +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IntervalDescription that = (IntervalDescription) o;

        if (order != that.order) return false;
        if (millisec != that.millisec) return false;
        return type != null ? type.equals(that.type) : that.type == null;
    }

    @Override
    public int hashCode() {
        int result = order;
        result = 31 * result + (int) (millisec ^ (millisec >>> 32));
        return result;
    }

    public void setType(IntervalTypeDescription type) {
        this.type = type;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public void setMillisec(long millisec) {
        this.millisec = millisec;
    }

    public IntervalTypeDescription getType() {
        return type;
    }

    public long getMillisec() {
        return millisec;
    }
}
