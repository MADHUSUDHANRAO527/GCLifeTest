package mobile.gclife.activity;

import java.util.ArrayList;

import mobile.gclife.PojoGson.MyApplication;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class GalleryImageViewer extends ActionBarActivity {
	private FullScreenImageAdapter adapter;
	private ViewPager viewPager;
	ImageLoader imageLoader;
	DisplayImageOptions options;
	android.support.v7.app.ActionBar actionBar;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.galleryimageviewer);
		// setting Action Bar
		actionBar = getSupportActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("Photos");
		actionBar.setBackgroundDrawable(new ColorDrawable(Color
				.parseColor(MyApplication.actiobarColor)));
		viewPager = (ViewPager) findViewById(R.id.pager);

		// image loading
		imageLoader = ImageLoader.getInstance();
		options = new DisplayImageOptions.Builder().cacheInMemory(true)
				.cacheOnDisc(true).resetViewBeforeLoading(true)
				.showImageForEmptyUri(R.drawable.no_media)
				.showImageOnFail(R.drawable.no_media)
				.showImageOnLoading(R.drawable.no_media).build();

		Intent ii = getIntent();
		ArrayList<String> arrayList = new ArrayList<String>();
		int position = ii.getIntExtra("position", 0);
		String images = ii.getStringExtra("Images");
		System.out.println(images + "------------------------");
		JSONArray jsonArray;
		try {
			jsonArray = new JSONArray(images);
			if (jsonArray != null) {
				int len = jsonArray.length();
				for (int i = 0; i < len; i++) {
					arrayList.add(jsonArray.getJSONObject(i).getString("image_url"));
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(arrayList + "ARRAY OF IMAGES !!!!!!!!!!!!!!!!!!!!!");
		adapter = new FullScreenImageAdapter(GalleryImageViewer.this, arrayList);
		viewPager.setAdapter(adapter);

		// displaying selected image first
		viewPager.setCurrentItem(position);
	}

	public class FullScreenImageAdapter extends PagerAdapter {

		private Activity _activity;
		private ArrayList<String> _imagePaths;
		private LayoutInflater inflater;

		// constructor
		public FullScreenImageAdapter(Activity activity,
				ArrayList<String> imagePaths) {
			this._activity = activity;
			this._imagePaths = imagePaths;
		}

		@Override
		public int getCount() {
			return this._imagePaths.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == ((RelativeLayout) object);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			TouchImageView imgDisplay;
			System.out.println(_imagePaths);
			inflater = (LayoutInflater) _activity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View viewLayout = inflater.inflate(
					R.layout.layout_fullscreen_image, container, false);

			imgDisplay = (TouchImageView) viewLayout
					.findViewById(R.id.imgDisplay);

			String imgUrl = null;
			for (int i = 0; i < _imagePaths.size(); i++) {
				imgUrl = _imagePaths.get(position);
				imageLoader.displayImage(imgUrl, imgDisplay, options);
			}

			((ViewPager) container).addView(viewLayout);

			return viewLayout;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager) container).removeView((RelativeLayout) object);

		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case android.R.id.home:
			onBackPressed();
			return true;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}
