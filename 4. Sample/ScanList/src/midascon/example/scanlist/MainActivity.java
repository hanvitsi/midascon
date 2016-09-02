package midascon.example.scanlist;

import com.hanvitsi.midascon.Beacon;
import com.hanvitsi.midascon.BeaconCallback;
import com.hanvitsi.midascon.MidasApplication;
import com.hanvitsi.midascon.manager.ContextManager;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.ListView;

public class MainActivity extends Activity implements BeaconCallback, Runnable {

	private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 100;
	private ContextManager contextManager;
	private BeaconListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);


		contextManager = getMidasApplication().getContextManager();
		contextManager.getBeaconSettings().setMidasScanMode(false);
		
		adapter = new BeaconListAdapter(getBaseContext());

		ListView listView = (ListView) findViewById(R.id.listView);
		listView.setAdapter(adapter);
	}

	@Override
	public void onBeaconCallback(int status, Beacon beacon) {
		switch (status) {
		case STATUS_CODE_ENTER:
		case STATUS_CODE_UPDATE:
			if (adapter != null)
				adapter.addBeacon(beacon);
			break;

		case STATUS_CODE_EXIT:
			if (adapter != null)
				adapter.removeBeacon(beacon);
			break;

		default:
			break;
		}

		runOnUiThread(this);

	}

	@Override
	public void run() {
		if (adapter != null)
			adapter.notifyDataSetChanged();
	}

	// AndroidManifest.xml에 설정된 name 클래스 호출
	public MidasApplication getMidasApplication() {
		return (MidasApplication) getApplication();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
				ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
			} else {
				ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
			}
		} else {
			if (BluetoothAdapter.getDefaultAdapter().isEnabled()) {
				// 콜백 등록
				contextManager.setBeaconCallback(this);
				contextManager.startLeScan();
			} else {
				contextManager.stopLeScan();

				Intent settingsIntent = new Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
				startActivity(settingsIntent);
			}
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		contextManager.stopLeScan();
	}

}
