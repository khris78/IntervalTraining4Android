package fr.gallioz.intervals.utils;

public class IdAndLabel {
    private long id;
    private String label;

    public IdAndLabel(long id, String label) {
        this.id = id;
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
