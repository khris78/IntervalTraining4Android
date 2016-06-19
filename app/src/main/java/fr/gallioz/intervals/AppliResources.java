package fr.gallioz.intervals;

import android.content.Context;

public class AppliResources {

    private static Context context;


    public static void initialize(Context ctx) {
        context = ctx;
    }

    public static Context getContext() {
        return context;
    }

}
