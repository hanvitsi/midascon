package midascon.example.scanlist;

import com.hanvitsi.midascon.Beacon;
import com.hanvitsi.midascon.manager.BeaconUtils;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class BeaconListAdapter extends BaseAdapter {

	private final LayoutInflater inflater;

	private final ArrayMap<String, Beacon> itemMap = new ArrayMap<String, Beacon>();

	private int count;

	private final int padding;

	public BeaconListAdapter(Context context) {
		super();
		padding = (int) context.getResources().getDimension(R.dimen.activity_vertical_margin);
		this.inflater = LayoutInflater.from(context);
	}

	public int addBeacon(Beacon beacon) {
		synchronized (itemMap) {
			itemMap.put(beacon.getMac(), beacon);
			count = itemMap.size();
			return count;
		}
	}

	public int removeBeacon(Beacon beacon) {
		synchronized (itemMap) {
			itemMap.remove(beacon.getMac());
			count = itemMap.size();
			return count;
		}
	}

	@Override
	public int getCount() {
		return count;
	}

	@Override
	public Beacon getItem(int position) {
		synchronized (itemMap) {
			return itemMap.valueAt(position);
		}

	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView textView = null;
		if (convertView == null) {
			convertView = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
			textView = (TextView) convertView.findViewById(android.R.id.text1);
			textView.setBackgroundColor(Color.WHITE);
			textView.setTextColor(Color.BLACK);
			textView.setPadding(padding, padding, padding, padding);
			convertView.setTag(textView);
		} else {
			textView = (TextView) convertView.getTag();
		}

		Beacon item = getItem(position);
		int[] values = BeaconUtils.getAccelerometer(item);
		textView.setText(String.format("[%s]\nMAC : %s\nRSSI : %d\nX : %d, Y : %d, Z : %d", item.getType() == Beacon.TYPE_MIDAS ? "Midascon" : "Beacon", item.getMac(), item.getRssi(), values[0],
				values[1], values[2]));

		return convertView;
	}

}
