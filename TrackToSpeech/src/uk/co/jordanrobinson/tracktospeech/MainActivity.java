package uk.co.jordanrobinson.tracktospeech;

import java.util.Iterator;
import java.util.Set;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Menu;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;


public class MainActivity extends Activity implements OnInitListener {

	private TextToSpeech tts;
	private int initStatus;
	private boolean enabled = true;
	private boolean playstate;
	TextView outputTextView;

	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			Bundle bundle = intent.getExtras();
			if (bundle != null) {
				Set<String> keys = bundle.keySet();
				Iterator<String> it = keys.iterator();
				Log.d("bundle output", "Dumping Intent start");
				while (it.hasNext()) {
					String key = it.next();
					Log.d("bundle output", "[" + key + "=" + bundle.get(key)+"]");
				}
				Log.d("bundle output", "Dumping Intent end");
				
				playstate = bundle.getBoolean("playstate");
				
				Log.d("playstate", playstate + "");
			}


			String command = intent.getStringExtra("command");
			Log.d("TrTS action output", action + " -  " + command);
			String artist = intent.getStringExtra("artist");
			String track = intent.getStringExtra("track");
			Log.d("TrTS track output", artist + " - " + track);

			outputTextView.setText(artist + "\n" + track);
			if (initStatus == TextToSpeech.SUCCESS && playstate) {			
				tts.speak(artist + ", " + track, TextToSpeech.QUEUE_FLUSH, null);
			}
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

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
					enabled = true;
				} else {
					unregisterReceiver(broadcastReceiver);
					outputTextView.setText("Service Turned Off");
					enabled = false;
				}
			}
		});

		displayNotifier();
	}

	private void displayNotifier() {

		String contentText = "Service is currently running";

		if (!enabled) {
			contentText = "Service is not running";
		}

		NotificationCompat.Builder mBuilder =
				new NotificationCompat.Builder(this)
		.setSmallIcon(R.drawable.ic_launcher)
		.setContentTitle("Track to Speech")
		.setContentText(contentText);

		NotificationManager mNotificationManager =
				(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(0, mBuilder.build());
	}

	@Override
	public void onInit(int initStatus) {
		this.initStatus = initStatus;
	}
}
