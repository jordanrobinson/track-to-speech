package uk.co.jordanrobinson.tracktospeech;

import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class PrefsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

	@SuppressWarnings("deprecation") //TODO: update to something that works this time
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (getString(R.string.pref_notify_key).equals(key)) {
			if (MainActivity.showNotifier) {
				NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
				notificationManager.cancel(0);
				MainActivity.showNotifier = false;
			}
			else {
				MainActivity.showNotifier = true;
			}
		}
        else if (getString(R.string.pref_pattern_key).equals(key)) {
            //TODO: pattern logic
        }
	}
}
