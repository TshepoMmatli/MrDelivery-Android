package com.tshepommatli.mrdelivery;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;

public class ListViewAdapter extends BaseAdapter {
	public static final String PREFS = "prefFile";

	// Declare Variables
	Context context;
	LayoutInflater inflater;
	ArrayList<HashMap<String, String>> data;
	private static final String baseUrlForImage ="http://touristtravelguide.000webhostapp.com/mrdelivery/images/";
	HashMap<String, String> resultp = new HashMap<String, String>();

	public ListViewAdapter(Context context, ArrayList<HashMap<String, String>> arraylist) {
		this.context = context;
		data = arraylist;
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {

		// Declare Variables
		TextView restaurantName;
		TextView restaurantAddress;
	 	TextView restaurantCity;
	 	ImageView logo;

		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View itemView = inflater.inflate(R.layout.custom_user_main, parent, false);

		// Get the position
		resultp = data.get(position);

		restaurantName = (TextView) itemView.findViewById(R.id.restaurantNames);
		restaurantAddress = (TextView) itemView.findViewById(R.id.restaurantAddress);
		logo = (ImageView) itemView.findViewById(R.id.logo);
		restaurantCity = (TextView) itemView.findViewById(R.id.restaurantCity);

		// Capture position and set results to the TextViews
		restaurantName.setText(resultp.get("restaurant_name"));
		restaurantAddress.setText(resultp.get("restaurant_address"));
		restaurantCity.setText(resultp.get("restaurant_city"));

		final String urlForImage = baseUrlForImage + resultp.get("logo");

		new DownloadImageTask(logo).execute(urlForImage);

		// Capture ListView item click
		itemView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// Get the position
				resultp = data.get(position);
				Intent intent = new Intent(context, MenuActivity.class);

				//Save the selected restaurant in a file
				SharedPreferences preferences = context.getSharedPreferences(PREFS, 0);
				SharedPreferences.Editor editor = preferences.edit();
				editor.putString("selectedRestaurant", resultp.get("restaurant_name"));
				editor.commit();

				// Pass all data rank
				//intent.putExtra("name", resultp.get("name"));
				//intent.putExtra("email",resultp.get("email"));

				// Start SingleItemView Class
				context.startActivity(intent);
			}
		});
		return itemView;
	}
}
