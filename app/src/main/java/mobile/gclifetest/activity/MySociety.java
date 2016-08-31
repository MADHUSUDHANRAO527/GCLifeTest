package mobile.gclifetest.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.gc.materialdesign.widgets.SnackBar;
import com.google.gson.Gson;

import mobile.gclifetest.pojoGson.UserDetailsPojo;

public class MySociety extends BaseActivity {
	GridView gv;
	Context context;
	public static String[] prgmNameList = { "Kys", "My bill", "Facilitator" };
	public static int[] prgmImages = { R.drawable.icon_news,
			R.drawable.icon_noticeboard, R.drawable.icon_society};
	static Typeface typefaceLight;
	SharedPreferences userPref;
	Gson gson=new Gson();
	UserDetailsPojo user;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.admin_detail);
		gv = (GridView) findViewById(R.id.grid);
		gv.setAdapter(new CustomAdapter(this, prgmNameList, prgmImages));
		userPref = getSharedPreferences("USER", MODE_PRIVATE);
		String jsonUser = userPref.getString("USER_DATA", "NV");
		user = gson.fromJson(jsonUser, UserDetailsPojo.class);
		setUpActionBar("My Society");
		typefaceLight = Typeface.createFromAsset(getAssets(),
				"fonts/RobotoLight.ttf");
		gv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				System.out.println(position + "^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
				if (position == 1) {
					if (user.getGclife_registration_flatdetails().get(0).getMember_type() == "Non_members" ||
							user.getGclife_registration_flatdetails().get(0).getMember_type().equals("Non_members")){
						showSnack(MySociety.this, "You are not authorized person!", "");
					}else{
						Intent soc = new Intent(MySociety.this, MySocietyBill.class);
						startActivity(soc);
						overridePendingTransition(R.anim.slide_in_left,
								R.anim.slide_out_left);
					}

				} else {
					showSnack(MySociety.this,
							"Coming soon!",
							"OK");
				}
			}
		});

	}

	public static class CustomAdapter extends BaseAdapter {

		String[] result;
		Context context;
		int[] imageId;
		private static LayoutInflater inflater = null;

		public CustomAdapter(MySociety mainActivity, String[] prgmNameList,
				int[] prgmImages) {
			// TODO Auto-generated constructor stub
			result = prgmNameList;
			context = mainActivity;
			imageId = prgmImages;
			inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return result.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		public class Holder {
			TextView tv;
			ImageView img;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub
			Holder holder = new Holder();
			View rowView;

			rowView = inflater.inflate(R.layout.home_grid_row, null);
			holder.tv = (TextView) rowView.findViewById(R.id.textView1);
			holder.img = (ImageView) rowView.findViewById(R.id.imageView1);
			holder.tv.setTypeface(typefaceLight);
			holder.tv.setText(result[position]);
			holder.img.setImageResource(imageId[position]);



			return rowView;
		}

	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case android.R.id.home:
			onBackPressed();
			overridePendingTransition(R.anim.slide_right_in,
					R.anim.slide_out_right);
			return true;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	void showSnack(MySociety login, String stringMsg, String ok) {
		new SnackBar(MySociety.this, stringMsg, ok, new OnClickListener() {

			@Override
			public void onClick(View v) {
			}
		}).show();
	}
}
