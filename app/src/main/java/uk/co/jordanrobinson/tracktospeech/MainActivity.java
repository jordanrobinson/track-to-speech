package uk.co.jordanrobinson.tracktospeech;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;


public class MainActivity extends FragmentActivity {

    protected static boolean showNotifier = true;

    private int numMessages;
    private boolean enabled = true;
    public static TextView outputTextView;

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
            Intent intent = new Intent(this, PrefsActivity.class);
            startActivity(intent);
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Intent intent = new Intent(this, TrackToSpeechService.class);

        startService(intent);

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
                    Log.d("TrTS", "Starting service");
                    startService(intent);

                    outputTextView.setText("Waiting For Track");

                    Log.d("TrTS", "Current notifier state: " + showNotifier);

                    mNotifyBuilder.setContentText("Service is currently running")
                            .setNumber(++numMessages);

                    enabled = true;
                    if (showNotifier) {
                        displayNotifier();
                    }
                } else {
                    Log.d("TrTS", "Stopping service");
                    stopService(intent);
                    outputTextView.setText("Service Turned Off");

                    mNotifyBuilder.setContentText("Service is not running")
                            .setNumber(++numMessages);
                    mNotificationManager.notify(0, mNotifyBuilder.build());

                    enabled = false;
                }
            }
        });

        displayNotifier();
    }

    private boolean isServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (TrackToSpeechService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
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

    protected void displayNotifier() {

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
        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        notificationManager.notify(0, notification);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("TrTS", "onDestroy called...");
    }
}
