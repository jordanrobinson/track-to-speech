package uk.co.jordanrobinson.tracktospeech;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
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

			
			if (!intent.getBooleanExtra("playing", false)) {
				Log.d("TrTS", "Track seems paused.");
			}
			else {
				outputTextView.setText(artist + "\n" + track);
				if (initStatus == TextToSpeech.SUCCESS) {			
					tts.speak(artist + ", " + track, TextToSpeech.QUEUE_FLUSH, null);
				}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//setup of background components
		final IntentFilter intentFilter = new IntentFilter();

		intentFilter.addAction("com.android.music.metachanged");
		intentFilter.addAction("com.android.music.playstatechanged");

		registerReceiver(broadcastReceiver, intentFilter);

		tts = new TextToSpeech(this, this);

		//setup of graphical components
		//set overall layout
		setContentView(R.layout.activity_main);

		//set text view to show track and artist
		outputTextView = (TextView) ((Activity) this).findViewById(R.id.track_view);

		//set toggle button for service
		ToggleButton toggleButton = (ToggleButton) this.findViewById(R.id.on_off_toggle);
		
		//set width to the longest text width it can use (stops it resizing for different text)
		toggleButton.setWidth((int) toggleButton.getPaint().measureText("Service Off") + 
				toggleButton.getPaddingLeft() + toggleButton.getPaddingRight());
		toggleButton.setChecked(true);

		//hook up button functionality
		toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					registerReceiver(broadcastReceiver, intentFilter); //we default to checked
					outputTextView.setText("Waiting For Track");
				} else {
					unregisterReceiver(broadcastReceiver);
					outputTextView.setText("Service Turned Off");
				}
			}
		});
	}

	@Override
	public void onInit(int initStatus) {
		this.initStatus = initStatus;
	}
}
