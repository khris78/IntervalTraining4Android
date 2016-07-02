package fr.gallioz.intervals.beans;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import static fr.gallioz.intervals.utils.AppliResources.*;
import fr.gallioz.intervals.utils.LockableArrayList;


public class TrainingPlanDescription {
    long id = -1L;
    private String name;
    private String source;
    private String description;

    private LockableArrayList<TrainingSessionDescription> sessions = null;
    private boolean isSessionsLoaded = false;

    public TrainingPlanDescription(long id, String name, String source, String description) {
        this.id = id;
        this.name = name;
        this.source = source;
        this.description = description;
        this.sessions = null;
        this.isSessionsLoaded = false;
    }

    public TrainingPlanDescription(String name, String source, String description) {
        this.name = name;
        this.source = source;
        this.description = description;
        this.sessions = null;
        this.isSessionsLoaded = false;
    }


    public JSONObject toJSON() throws JSONException {
        JSONObject ret = new JSONObject();
        ret.put("name", name);
        ret.put("source", source);
        ret.put("description", description);

        List<TrainingSessionDescription> theSessions = getSessions();
        JSONArray jsonSessions = new JSONArray();
        for (int i = 0 ; i < theSessions.size() ; i++) {
            jsonSessions.put(i, theSessions.get(i).toJSON());
        }
        ret.put("sessions", jsonSessions);

        return ret;
    }

    @Override
    public String toString() {
        return "TrainingPlanDescription{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", source='" + source + '\'' +
                ", description='" + description + '\'' +
                ", sessions=" + getSessions() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TrainingPlanDescription that = (TrainingPlanDescription) o;

        if (id != that.id) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (source != null ? !source.equals(that.source) : that.source != null) return false;
        return description != null ? description.equals(that.description) : that.description == null;

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (source != null ? source.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<TrainingSessionDescription> getSessions() {
        if (! isSessionsLoaded) {
            List<TrainingSessionDescription> theSessions = new LockableArrayList<>(getDbAccess().retrieveSessionsForPlan(id));
            try {
                sessions = (LockableArrayList<TrainingSessionDescription>) theSessions;
            } catch (ClassCastException e) {
                sessions = new LockableArrayList<TrainingSessionDescription>(theSessions);
            }
            isSessionsLoaded = true;
            sessions.lock();
        }
        return sessions;
    }

    public boolean isSessionsLoaded() {
        return isSessionsLoaded;
    }
}
