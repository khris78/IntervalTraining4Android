package fr.gallioz.intervals.beans;

import android.graphics.Color;

import fr.gallioz.intervals.utils.AppliResources;
import fr.gallioz.intervals.R;

public class IntervalTypeDescription {

    public static final IntervalTypeDescription RUN =
            new IntervalTypeDescription(AppliResources.getContext().getString(R.string.run),
                    Color.parseColor("#ba1b53"), null, R.raw.dingding);

    public static final IntervalTypeDescription WALK =
            new IntervalTypeDescription(AppliResources.getContext().getString(R.string.walk),
                    Color.parseColor("#439e43"), null, R.raw.dingding);

    public static final IntervalTypeDescription WARM_UP =
            new IntervalTypeDescription(AppliResources.getContext().getString(R.string.warmup),
                    Color.parseColor("#5f9c97"), null, R.raw.dingding);

    private long id = -1;
    private String label;
    private int color;
    private Integer startSoundResId;
    private Integer endSoundResId;

    public IntervalTypeDescription(long id, String label, int color, Integer startSoundResId, Integer endSoundResId) {
        this.id = id;
        this.label = label;
        this.color = color;
        this.startSoundResId = startSoundResId;
        this.endSoundResId = endSoundResId;
    }

    public IntervalTypeDescription(String label, int color, Integer startSoundResId, Integer endSoundResId) {
        this.label = label;
        this.color = color;
        this.startSoundResId = startSoundResId;
        this.endSoundResId = endSoundResId;
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

    public int getColor() {
        return color;
    }

    public Integer getStartSoundResId() {
        return startSoundResId;
    }

    public Integer getEndSoundResId() {
        return endSoundResId;
    }
}
