package midascon.example.thermometer;

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
import android.widget.TextView;

public class MainActivity extends Activity implements BeaconCallback {

	private static final String TEXT = "Temperature :\n%s\n\nHumidity :\n%s";

	private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 100;

	private ContextManager contextManager;
	private TextView textView;

	// AndroidManifest.xml에 설정된 name 클래스 호출
	public MidasApplication getMidasApplication() {
		return (MidasApplication) getApplication();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		contextManager = getMidasApplication().getContextManager();
		// 맥주소로 수신할 비콘 설정
		contextManager.getBeaconSettings().setAllowBeaconMac(true);
		// 마이다스콘 맥 주소 등록
		// contextManager.getBeaconSettings().setAllowBeaconId("74-f0-7d-00-12-36",
		// true);

		textView = (TextView) findViewById(R.id.status);
	}

	@Override
	public void onBeaconCallback(int status, Beacon beacon) {
		switch (status) {
		case STATUS_CODE_ENTER:
		case STATUS_CODE_UPDATE:
			// 온도, 습도 데이터를 TextView에 표시
			final float temperature = beacon.getTemperature();
			final float humidity = beacon.getHumidity();
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					if (textView == null)
						return;

					textView.setText(String.format(TEXT, temperature + " ℃", humidity + " %"));
				}
			});
			break;

		default:
			break;
		}

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
