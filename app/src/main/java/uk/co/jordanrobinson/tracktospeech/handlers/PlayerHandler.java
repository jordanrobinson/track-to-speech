package uk.co.jordanrobinson.tracktospeech.handlers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import uk.co.jordanrobinson.tracktospeech.MainActivity;
import uk.co.jordanrobinson.tracktospeech.R;

import static uk.co.jordanrobinson.tracktospeech.MainActivity.history;
import static uk.co.jordanrobinson.tracktospeech.MainActivity.listView;

public abstract class PlayerHandler {

    public static final String[] IDENTIFIERS = {};
    private static final boolean DEBUG = true;
    public static List<String> History;

    TextToSpeech tts;
    int initStatus;
    boolean playstate;
    String currentArtist = null;
    String currentTrack = null;

    PlayerHandler(TextToSpeech tts,
                  int initStatus,
                  boolean playstate,
                  String currentArtist,
                  String currentTrack) {
        this.tts = tts;
        this.initStatus = initStatus;
        this.playstate = playstate;
        this.currentArtist = currentArtist;
        this.currentTrack = currentTrack;
    }

    public void handle(Context context, Intent intent) {

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

    void updateHistory(Context context) {
        if (MainActivity.history.size() > 5) {
            MainActivity.history.remove(0);
        }

        ListView listView = MainActivity.listView;
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                context,
                android.R.layout.simple_list_item_1,
                history);

        listView.setAdapter(arrayAdapter);
        listView.invalidate();
    }
}
