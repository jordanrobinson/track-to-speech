package uk.co.jordanrobinson.tracktospeech;

import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.Menu;


public class MainActivity extends Activity implements OnInitListener {

	private TextToSpeech tts;
	
	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			String command = intent.getStringExtra("command");
			Log.d("TrTS action output", action + " - " + command);
			String artist = intent.getStringExtra("artist");
			String track = intent.getStringExtra("track");
			Log.d("TrTS track output", artist + " - " + track);
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		IntentFilter intentFilter = new IntentFilter();
		
		intentFilter.addAction("com.android.music.metachanged");
		intentFilter.addAction("com.android.music.playstatechanged");
		intentFilter.addAction("com.android.music.playbackcomplete");
		intentFilter.addAction("com.android.music.queuechanged");
		
		registerReceiver(broadcastReceiver, intentFilter);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		tts = new TextToSpeech(this, this);
		return true;
	}
	
	@Override
	public void onInit(int initStatus) {
		if (initStatus == TextToSpeech.SUCCESS) {
			tts.speak("Hello world", TextToSpeech.QUEUE_FLUSH, null);
		}
		
	}

}
