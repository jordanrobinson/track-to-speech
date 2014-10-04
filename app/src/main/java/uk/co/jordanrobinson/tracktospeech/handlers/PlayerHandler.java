package uk.co.jordanrobinson.tracktospeech.handlers;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.Iterator;
import java.util.Set;

public abstract class PlayerHandler {

    public static final String[] IDENTIFIERS = {};
    public static final boolean DEBUG = false;

    public void handle(Intent intent) {
        if (DEBUG) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Set<String> keys = bundle.keySet();
                Iterator<String> it = keys.iterator();
                Log.d("TrTS bundle output", intent.getAction());
                Log.d("TrTS bundle output", "Dumping Intent start");
                while (it.hasNext()) {
                    String key = it.next();
                    Log.d("TrTS bundle output", "[" + key + "=" + bundle.get(key)+ "]");
                }
                Log.d("TrTS bundle output", "Dumping Intent end");
            }
        }
    }
}
