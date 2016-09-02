package midascon.example.emergency;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.BigTextStyle;

import com.hanvitsi.midascon.Beacon;
import com.hanvitsi.midascon.EmergencyCallback;
import com.hanvitsi.midascon.MidasApplication;
import com.hanvitsi.midascon.manager.ContextManager;

public class EmergencyService extends Service implements EmergencyCallback {

	public static final String TAG = EmergencyService.class.getSimpleName();
	public static final String ACTION_STATUS = TAG + ".ACTION_STATUS";

	static boolean run;

	// AndroidManifest.xml에 설정된 name 클래스 호출
	public MidasApplication getMidasApplication() {
		return (MidasApplication) getApplication();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private BluetoothAdapter adapter;
	private ContextManager contextManager;

	@Override
	public void onCreate() {
		super.onCreate();

		run = true;

		adapter = BluetoothAdapter.getDefaultAdapter();
		contextManager = getMidasApplication().getContextManager();

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		sendStatus(true);

		// 응급신호 콜백 등록
		contextManager.setEmergencyCallback(this);

		if (adapter.isEnabled()) {
			contextManager.stopLeScan();
			contextManager.startLeScan();

		} else {
			contextManager.stopLeScan();
		}
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (contextManager != null)
			contextManager.stopLeScan();
		run = false;

		sendStatus(false);
	}

	@Override
	public void onEmergencyCallback(int status, Beacon beacon) {
		showNotification(beacon);
	}

	private void sendStatus(boolean status) {
		Intent intent = new Intent(ACTION_STATUS);
		intent.putExtra("status", status);
		sendBroadcast(intent);
	}

	// 응급신호 알림
	private void showNotification(Beacon beacon) {
		if (beacon == null)
			return;

		int notify = beacon.getId().hashCode();
		Intent intent = new Intent(getApplicationContext(), MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		intent.putExtra("title", "응급 신호 발생");
		intent.putExtra("message", beacon.getId());
		intent.putExtra("notify", notify);

		NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext());
		builder.setContentIntent(PendingIntent.getActivity(getApplicationContext(), notify, intent, PendingIntent.FLAG_UPDATE_CURRENT));

		builder.setPriority(NotificationCompat.PRIORITY_HIGH);

		builder.setSmallIcon(R.drawable.ic_launcher);
		builder.setTicker("응급 신호 발생");
		builder.setContentTitle(beacon.getId());
		builder.setContentText(beacon.getId() + " 응급 신호 발생");

		builder.setAutoCancel(true);
		builder.setDefaults(NotificationCompat.DEFAULT_ALL);

		BigTextStyle style = new BigTextStyle(builder);
		style.bigText(beacon.getId() + " 응급 신호 발생");
		style.setBigContentTitle(" 응급 신호 발생");
		style.setSummaryText(getString(R.string.app_name));

		NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		manager.notify(notify, style.build());
	}

}
