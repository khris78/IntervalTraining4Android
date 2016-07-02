package fr.gallioz.intervals.beans;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import fr.gallioz.intervals.utils.LockableArrayList;
import static fr.gallioz.intervals.utils.AppliResources.*;


public class TrainingSessionDescription {
    private long id = -1;
    private int order;
    private String name;

    private LockableArrayList<IntervalDescription> intervals = null;
    private boolean isIntervalsLoaded = false;

    public TrainingSessionDescription(long id, int order, String name) {
        this.id = id;
        this.order = order;
        this.name = name;
    }

    public TrainingSessionDescription(int order, String name) {
        this.order = order;
        this.name = name;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject ret = new JSONObject();
        ret.put("name", name);
        ret.put("order", order);

        List<IntervalDescription> theIntervals = getIntervals();
        JSONArray jsonIntervals = new JSONArray();
        for (int i = 0 ; i < theIntervals.size() ; i++) {
            jsonIntervals.put(i, theIntervals.get(i).toJSON());
        }
        ret.put("intervals", jsonIntervals);

        return ret;
    }

    @Override
    public String toString() {
        return "TrainingSessionDescription{" +
                "id=" + id +
                ", order=" + order +
                ", name='" + name + '\'' +
                ", intervals=" + intervals +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TrainingSessionDescription that = (TrainingSessionDescription) o;

        if (id != that.id) return false;
        if (order != that.order) return false;
        return name != null ? name.equals(that.name) : that.name == null;

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + order;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<IntervalDescription> getIntervals() {
        if (! isIntervalsLoaded) {
            List<IntervalDescription> theIntervals = getDbAccess().retrieveIntervalsForSession(id);
            try {
                intervals = (LockableArrayList<IntervalDescription>) theIntervals;
            } catch(ClassCastException e) {
                intervals = new LockableArrayList<IntervalDescription>(theIntervals);
            }
            intervals.lock();
            isIntervalsLoaded = true;
        }
        return intervals;
    }
}
