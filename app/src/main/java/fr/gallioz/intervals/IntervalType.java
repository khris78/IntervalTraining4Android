package fr.gallioz.intervals;

import android.content.res.Resources;
import android.graphics.Color;

public class IntervalType {

    public static final IntervalType RUN =
            new IntervalType(AppliResources.getContext().getString(R.string.run),
                    Color.parseColor("#ba1b53"), null, R.raw.dingding);

    public static final IntervalType WALK =
            new IntervalType(AppliResources.getContext().getString(R.string.walk),
                    Color.parseColor("#439e43"), null, R.raw.dingding);

    public static final IntervalType WARM_UP =
            new IntervalType(AppliResources.getContext().getString(R.string.warmup),
                    Color.parseColor("#5f9c97"), null, R.raw.dingding);

    private String label;
    private int color;
    private Integer startSoundResId;
    private Integer endSoundResId;

    public IntervalType(String label, int color, Integer startSoundResId, Integer endSoundResId) {
        this.label = label;
        this.color = color;
        this.startSoundResId = startSoundResId;
        this.endSoundResId = endSoundResId;
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
