package mobile.gclife.activity;

import mobile.gclife.PojoGson.MyApplication;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class AdminBillManagement extends ActionBarActivity {
	android.support.v7.app.ActionBar actionBar;
	GridView gv;
	public static String[] prgmNameList = { "          Bill " + "\n"
			+ "    Management" };
	static Typeface typefaceLight;
	public static int[] prgmImages = { R.drawable.icon_news };
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		gv = (GridView) findViewById(R.id.grid);
		gv.setAdapter(new CustomAdapter(AdminBillManagement.this, prgmNameList,
				prgmImages));
		actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setIcon(android.R.color.transparent);
		actionBar.setTitle("Society Bill Management");
		actionBar.setBackgroundDrawable(new ColorDrawable(Color
				.parseColor(MyApplication.actiobarColor)));
		typefaceLight = Typeface.createFromAsset(getAssets(),
				"fonts/RobotoLight.ttf");
		gv.setOnItemClickListener(new OnItemClickListener() {
	        @Override
	        public void onItemClick(AdapterView<?> parent, View v,
	                int position, long id) {
	        	System.out.println(position+"^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
	        	if(position==0){
					Intent mems = new Intent(AdminBillManagement.this, SocietyBillManagement.class);
					startActivity(mems);
					overridePendingTransition(R.anim.slide_in_left,
							R.anim.slide_out_left);
				}
	        }
	    });
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

	public static class CustomAdapter extends BaseAdapter {

		String[] result;
		Context context;
		int[] imageId;
		LayoutInflater inflater = null;

		public CustomAdapter(AdminBillManagement adminBillManagement,
				String[] prgmNameList, int[] prgmImages) {
			// TODO Auto-generated constructor stub
			result = prgmNameList;
			context = adminBillManagement;
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

}