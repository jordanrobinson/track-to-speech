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
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;


public class MainActivity extends Activity implements OnInitListener {

	private TextToSpeech tts;
	private int initStatus;
	TextView outputTextView;
	
	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			String command = intent.getStringExtra("command");
			Log.d("TrTS action output", action + " - " + command);
			String artist = intent.getStringExtra("artist");
			String track = intent.getStringExtra("track");
			Log.d("TrTS track output", artist + " - " + track);
			
			outputTextView.setText(artist + " \n " + track);
			if (initStatus == TextToSpeech.SUCCESS) {			
				tts.speak(artist + ", " + track, TextToSpeech.QUEUE_FLUSH, null);
			}			
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//set overall layout
		setContentView(R.layout.activity_main);
		
		//set text view to show track and artist
		outputTextView = (TextView) ((Activity) this).findViewById(R.id.track_view);
		
		//set toggle button for service
	    ToggleButton toggleButton = (ToggleButton) this.findViewById(R.id.on_off_toggle);
	    toggleButton.setWidth((int) toggleButton.getPaint().measureText("Service Off") + 
	    		toggleButton.getPaddingLeft() + toggleButton.getPaddingRight());
	    toggleButton.setChecked(true);
	    
	    
		
		final IntentFilter intentFilter = new IntentFilter();
		
		intentFilter.addAction("com.android.music.metachanged");
		intentFilter.addAction("com.android.music.playstatechanged");
		intentFilter.addAction("com.android.music.playbackcomplete");
		intentFilter.addAction("com.android.music.queuechanged");
		
		registerReceiver(broadcastReceiver, intentFilter);
		
		toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					registerReceiver(broadcastReceiver, intentFilter);
					outputTextView.setText("Waiting For Track");
				} else {
					unregisterReceiver(broadcastReceiver);
					outputTextView.setText("Service Turned Off");
				}
			}
		});
		
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
		this.initStatus = initStatus;		
	}

}
