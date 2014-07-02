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

import java.util.Iterator;
import java.util.Set;

public class TrackToSpeechService extends Service implements OnInitListener {
	
	private TextToSpeech tts;
	private int initStatus;
	private boolean playstate;
	private static String currentArtist = null;
	private static String currentTrack = null;
	
	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d("TrTS", "onRecieve Called...");

			String action = intent.getAction();

			Bundle bundle = intent.getExtras();
			if (bundle != null) {
				Set<String> keys = bundle.keySet();
				Iterator<String> it = keys.iterator();
				Log.d("TrTS bundle output", "Dumping Intent start");
				while (it.hasNext()) {
					String key = it.next();
					Log.d("TrTS bundle output", "[" + key + "=" + bundle.get(key)+ "]");
				}
				Log.d("TrTS bundle output", "Dumping Intent end");

				//for our use, these are essentially the same
				playstate = (bundle.getBoolean("playstate") || bundle.getBoolean("playing"));				
				Log.d("TrTS playstate", playstate + "");
			}

			if (initStatus == TextToSpeech.SUCCESS && playstate) { //TTS is initialised, and we're actually playing
				String command = intent.getStringExtra("command"); //so log what's going on
				String artist = intent.getStringExtra("artist");
				String track = intent.getStringExtra("track");
				Log.d("TrTS track output", artist + " - " + track);
				Log.d("TrTS action output", action + " -  " + command);

				if (!artist.equals(currentArtist) || !track.equals(currentTrack)) { //if we haven't already
					currentArtist = artist;
					currentTrack = track;

					//speak to the user
					tts.speak(artist + ", " + track, TextToSpeech.QUEUE_FLUSH, null);
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
	};

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
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

		intentFilter.addAction("com.android.music.metachanged");
		intentFilter.addAction("com.android.music.playstatechanged");

		registerReceiver(broadcastReceiver, intentFilter);

		tts = new TextToSpeech(this, this);

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
