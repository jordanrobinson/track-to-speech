package uk.co.jordanrobinson.tracktospeech;

import java.util.Iterator;
import java.util.Set;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;


public class MainActivity extends FragmentActivity implements OnInitListener {

	private TextToSpeech tts;
	private int initStatus;
	private boolean enabled = true;
	private boolean playstate;
	private static String currentArtist = null;
	private static String currentTrack = null;
	private TextView outputTextView;

	private int numMessages;
	
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

					outputTextView.setText(artist + "\n" + track); //set the text and speak to the user
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
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getTitle().equals("Settings")) {
			
			Log.d("TrTS", "attempting init Settings");
	        getFragmentManager().beginTransaction()
	        .replace(android.R.id.content, new PrefsFragment())
	        .commit();
		}
		return true;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("TrTS", "Starting up...");	
		
		numMessages = 0;

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

		final NotificationManager mNotificationManager =
				(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		final NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(this);

		//hook up button functionality
		toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					registerReceiver(broadcastReceiver, intentFilter); //we default to checked
					outputTextView.setText("Waiting For Track");

					mNotifyBuilder.setContentText("Service is currently running")
					.setNumber(++numMessages);
					mNotificationManager.notify(2, mNotifyBuilder.build());

					enabled = true;
				} else {
					unregisterReceiver(broadcastReceiver);
					outputTextView.setText("Service Turned Off");

					mNotifyBuilder.setContentText("Service is not running")
					.setNumber(++numMessages);
					mNotificationManager.notify(1, mNotifyBuilder.build());

					enabled = false;
				}
			}
		});

		displayNotifier();
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d("TrTS", "onResume called...");
		if (currentArtist != null && currentTrack != null) {
			outputTextView.setText(currentArtist + "\n" + currentTrack);
			Log.d("TrTS", "Setting text from last time");
		}
		else {
			outputTextView.setText("Waiting For Track");
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		Log.d("TrTS", "OnNewIntent called...");
		onResume();
	}

	private void displayNotifier() {

		String contentText = "Service is currently running";

		if (!enabled) {
			contentText = "Service is not running";
		}

		Intent resultIntent = new Intent(this, MainActivity.class);		

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
		.setSmallIcon(R.drawable.ic_stat_notify)
		.setContentTitle("Track to Speech")
		.setContentText(contentText);

		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		stackBuilder.addParentStack(NotificationActivity.class);
		stackBuilder.addNextIntent(resultIntent);

		PendingIntent resultPendingIntent = 
				PendingIntent.getActivity(
						this,
						0,
						resultIntent,
						PendingIntent.FLAG_UPDATE_CURRENT
						);

		mBuilder.setContentIntent(resultPendingIntent);

		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		Notification notification = mBuilder.build();
		notification.flags |= Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;
		notificationManager.notify(0, notification);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d("TrTS", "onDestroy called...");
	}

	@Override
	public void onInit(int initStatus) {
		this.initStatus = initStatus;
	}
}
