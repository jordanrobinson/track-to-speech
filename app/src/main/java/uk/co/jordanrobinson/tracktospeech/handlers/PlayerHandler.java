package uk.co.jordanrobinson.tracktospeech.handlers;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Iterator;
import java.util.Set;

public abstract class PlayerHandler {

    public static final String[] IDENTIFIERS = {};
    public static final boolean DEBUG = true;

    protected TextToSpeech tts;
    protected int initStatus;
    protected boolean playstate;
    protected String currentArtist = null;
    protected String currentTrack = null;

    public PlayerHandler(TextToSpeech tts, int initStatus, boolean playstate,
                           String currentArtist, String currentTrack) {
        this.tts = tts;
        this.initStatus = initStatus;
        this.playstate = playstate;
        this.currentArtist = currentArtist;
        this.currentTrack = currentTrack;
    }

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
