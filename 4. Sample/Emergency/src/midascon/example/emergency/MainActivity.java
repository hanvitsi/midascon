package midascon.example.emergency;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivity extends Activity implements OnClickListener {

	private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 100;
	private BroadcastReceiver receiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		findViewById(R.id.start).setOnClickListener(this);
		findViewById(R.id.stop).setOnClickListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			receiver = null;
			if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
				ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
			} else {
				ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
			}
		} else {
			checkButton();
			if (receiver == null)
				receiver = new BroadcastReceiver() {

					@Override
					public void onReceive(Context context, Intent intent) {
						checkButton();
					}
				};

			registerReceiver(receiver, new IntentFilter(EmergencyService.ACTION_STATUS));
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		if (receiver != null)
			unregisterReceiver(receiver);
	}

	private void checkButton() {
		if (EmergencyService.run) {
			findViewById(R.id.start).setEnabled(false);
			findViewById(R.id.stop).setEnabled(true);
		} else {
			findViewById(R.id.start).setEnabled(true);
			findViewById(R.id.stop).setEnabled(false);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.start:
			if (BluetoothAdapter.getDefaultAdapter().isEnabled() == false) {
				Intent settingsIntent = new Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
				startActivity(settingsIntent);
				return;
			}

			startService(new Intent(getApplicationContext(), EmergencyService.class));
			break;

		case R.id.stop:
			stopService(new Intent(getApplicationContext(), EmergencyService.class));
			break;

		default:
			break;
		}
		checkButton();
	}

}
