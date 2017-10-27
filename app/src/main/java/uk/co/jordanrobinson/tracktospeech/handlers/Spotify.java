package uk.co.jordanrobinson.tracktospeech.handlers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import uk.co.jordanrobinson.tracktospeech.MainActivity;

public class Spotify extends PlayerHandler {

    private static final String PLAYBACK = "com.spotify.music.playbackstatechanged";
    private static final String METADATA_CHANGE = "com.spotify.music.metadatachanged";

    private static final String[] IDENTIFIERS = {PLAYBACK, METADATA_CHANGE};

    private Intent metadataIntent;
    private Intent playstateIntent;

    public Spotify(TextToSpeech tts, int initStatus, boolean playstate, String currentArtist, String currentTrack) {
        super(tts, initStatus, playstate, currentArtist, currentTrack);
        metadataIntent = null;
        playstateIntent = null;
    }

    @Override
    public void handle(Context context, Intent intent) {
        boolean handle = false;
        for (String IDENTIFIER : IDENTIFIERS) {
            if (IDENTIFIER.equals(intent.getAction())) {
                handle = true;
                break;
            }
        }

        if (handle) {
            super.handle(context, intent);

            String action = intent.getAction();
            Bundle bundle = intent.getExtras();

            if (METADATA_CHANGE.equals(action)) {
                metadataIntent = intent;
            }
            else if (PLAYBACK.equals(action) && metadataIntent != null) {
                playstate = (bundle.getBoolean("playstate") || bundle.getBoolean("playing"));
                Log.d("TrTS playstate", playstate + "");

                if (initStatus == TextToSpeech.SUCCESS && playstate) { //TTS is initialised, and we're actually playing
                    String command = metadataIntent.getStringExtra("command"); //so log what's going on
                    String artist = metadataIntent.getStringExtra("artist");
                    String track = metadataIntent.getStringExtra("track");
                    Log.d("TrTS track output", artist + " - " + track);
                    Log.d("TrTS action output", action + " -  " + command);

                    if (!artist.equals(currentArtist) || !track.equals(currentTrack)) { //if we haven't already
                        currentArtist = artist;
                        currentTrack = track;

                        //speak to the user
                        tts.speak(artist + ", " + track, TextToSpeech.QUEUE_FLUSH, null);
                        MainActivity.outputTextView.setText(artist + " - " + track);
                        MainActivity.history.add(artist + " - " + track);
                        Log.d("TrTS", "onRecieve success!");
                    }
                    else {
                        Log.d("TrTS", "onRecieve failed on artist comparison. Artist = "
                                + artist + " + " + currentArtist + " Track = " + track + " + " + currentTrack);
                    }
                }
                else {
                    Log.d("TrTS", "onRecieve failed on tts Success & playstate. Playstate = "
                            + playstate + " tts = " + (initStatus == TextToSpeech.SUCCESS));
                }
            }
        }
    }
}
