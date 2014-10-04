package uk.co.jordanrobinson.tracktospeech;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import uk.co.jordanrobinson.tracktospeech.handlers.GooglePlayMusic;
import uk.co.jordanrobinson.tracktospeech.handlers.PlayerHandler;

public class TrackToSpeechService extends Service implements OnInitListener {

    private TextToSpeech tts;
    private int initStatus;
    private boolean playstate;
    private static String currentArtist = null;
    private static String currentTrack = null;

    private static final String[] PLAYER_INTENTS = {"com.android.music.metachanged",
            "com.android.music.playstatechanged",
            "com.android.music.metadatachanged",
            "com.spotify.music.metadatachanged",
            "com.dp.ezfolderplayer.free.musicservicecommand",
            "com.dp.ezfolderplayer.free.musicservicecommand.togglepause",
            "com.dp.ezfolderplayer.free.musicservicecommand.pause",
            "com.dp.ezfolderplayer.free.musicservicecommand.next",
            "com.dp.ezfolderplayer.free.musicservicecommand.previous",
            "com.dp.ezfolderplayer.free.musicservicecommand.cyclerepeat",
            "com.dp.ezfolderplayer.free.musicservicecommand.notificationtogglepause",
            "com.dp.ezfolderplayer.free.metachanged",
            "com.dp.ezfolderplayer.free.playstatechanged"
    };

    private static ArrayList<PlayerHandler> handlers;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("TrTS", "onRecieve Called...");
            for (int i = 0; i < handlers.size(); i++) {
                handlers.get(i).handle(intent);
            }
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("TrTS", "Hitting service onStartCommand");
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("TrTS", "Starting up...");

        //setup of background components
        final IntentFilter intentFilter = new IntentFilter();

        for (int i = 0; i < PLAYER_INTENTS.length; i++) {
            intentFilter.addAction(PLAYER_INTENTS[i]);
        }

        tts = new TextToSpeech(this, this);

        handlers = new ArrayList<PlayerHandler>();

        GooglePlayMusic googlePlayMusic = new GooglePlayMusic(tts, initStatus, playstate, currentArtist, currentTrack);
        handlers.add(googlePlayMusic);

        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        Log.d("TrTS", "Hitting service onDestroy");
        unregisterReceiver(broadcastReceiver);

        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }

        super.onDestroy();
    }

    @Override
    public void onInit(int initStatus) {
        this.initStatus = initStatus;
    }

    /**
     * Simple getter for the static fields representing the artist and track currently playing.
     * @return A String describing the current artist and track in the format 'artist\ntrack'
     */
    public String getCurrentPlaying() {
        return currentArtist + "\n" + currentTrack;
    }

}
