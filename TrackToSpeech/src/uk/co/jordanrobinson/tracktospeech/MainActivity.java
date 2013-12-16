package uk.co.jordanrobinson.tracktospeech;

import android.os.Bundle;
import android.app.Activity;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.view.Menu;

public class MainActivity extends Activity implements OnInitListener {

	private TextToSpeech tts;	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
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
