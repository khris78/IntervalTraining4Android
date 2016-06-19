package fr.gallioz.intervals;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import java.util.HashMap;
import java.util.Map;

public class SoundManager {

    private static SoundManager instance = null;

    private Context context;
    private SoundPool soundPool;
    private Map<Integer, Integer> soundIds = new HashMap<Integer, Integer>();

    private SoundManager() {
    }

    private SoundManager(Context context) {
        this.context = context;
        this.soundPool = new SoundPool(20, AudioManager.STREAM_MUSIC, 0);
    }

    public static final void initialize(Context context) {
        synchronized (SoundManager.class) {
            if (instance == null) {
                instance = new SoundManager(context);
                // Preload the sounds
                instance.getIdForSound(R.raw.ding);
                instance.getIdForSound(R.raw.dingding);
            }
        }
    }

    public static final SoundManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("The class SoundManager is not initialized. Call initialize()");
        }
        return instance;
    }

    private int getIdForSound(int resId) {
        Integer ret = soundIds.get(resId);
        if (ret == null) {
            ret = soundPool.load(context, resId, 1);
            soundIds.put(resId, ret);
        }
        return ret;
    }

    public void playSound(Integer soundResId) {
        if (soundResId == null) {
            return;
        }
        int soundId = getIdForSound(soundResId);
        soundPool.play(soundId, 1, 1, 0, 0, 1);
    }
}
