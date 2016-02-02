package mobile.gclife.activity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import mobile.gclife.MaterialDesign.ProgressBarCircularIndeterminate;
import mobile.gclife.PojoGson.MyApplication;
import mobile.gclife.PojoGson.UserDetailsPojo;
import mobile.gclife.application.InternetConnectionDetector;
import mobile.gclife.application.ListViewUtils;
import mobile.gclife.http.EvenstPost;
import mobile.gclife.http.Hostname;
import mobile.gclife.http.MemsPost;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.gc.materialdesign.widgets.SnackBar;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class IdeasDetail extends ActionBarActivity {
	android.support.v7.app.ActionBar actionBar;
	String title, sdic, bdisc, societyName, associationName, memType,
			eventName, hostname, eid, commentDetail, comntCreated, userName;
	TextView titleTxt, sDiscTxt, bDiscTxt, photosCountTxt;
	public static List<String> imagesList;
	Gallery galleryArti;
	ImageLoader imageLoader;
	DisplayImageOptions options;
	JSONArray mediaArr;
	RelativeLayout imagesLay;
	LinearLayout sdescLay, bdescLay;
	Button sendBtn;
	ListView commentsListview;
	EditText comntEdit;
	InternetConnectionDetector netConn;
	Boolean isInternetPresent = false;
	Typeface typefaceLight;
	JSONObject jsonPushComment;
	SharedPreferences userPref;
	UserDetailsPojo user;
	ArrayList<HashMap<String, String>> commentsList = new ArrayList<HashMap<String, String>>();
	ProgressBarCircularIndeterminate pDialog;
	LinearLayout progrLay;
	JSONObject jsonDetails;
	ListCommentsBaseAdapter commentsAdapter;
	ScrollView scroll;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ideas_detail);
		actionBar = getSupportActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setIcon(android.R.color.transparent);
		actionBar.setBackgroundDrawable(new ColorDrawable(Color
				.parseColor(MyApplication.actiobarColor)));

		imageLoader = ImageLoader.getInstance();
		options = new DisplayImageOptions.Builder().cacheInMemory(true)
				.cacheOnDisc(true).resetViewBeforeLoading(true)
				.showImageForEmptyUri(R.drawable.no_media)
				.showImageOnFail(R.drawable.no_media)
				.showImageOnLoading(R.drawable.no_media).build();
		pDialog = (ProgressBarCircularIndeterminate) findViewById(R.id.pDialog);
		progrLay = (LinearLayout) findViewById(R.id.progrLay);
		titleTxt = (TextView) findViewById(R.id.titleNameTxt);
		sDiscTxt = (TextView) findViewById(R.id.sdescTxt);
		bDiscTxt = (TextView) findViewById(R.id.bdescNumTxt);
		photosCountTxt = (TextView) findViewById(R.id.photosCountTxt);
		galleryArti = (Gallery) findViewById(R.id.galleryArti);
		imagesLay = (RelativeLayout) findViewById(R.id.imagesLay);
		comntEdit = (EditText) findViewById(R.id.comentEdittext);
		sdescLay = (LinearLayout) findViewById(R.id.sdescLay);
		bdescLay = (LinearLayout) findViewById(R.id.bdescLay);
		sendBtn = (Button) findViewById(R.id.addCmntBtn);
		commentsListview = (ListView) findViewById(R.id.commentListview);
		scroll = (ScrollView) findViewById(R.id.scroll);
		scroll.smoothScrollTo(0, 0);

		typefaceLight = Typeface.createFromAsset(getAssets(),
				"fonts/RobotoLight.ttf");
		userPref = getSharedPreferences("USER", MODE_PRIVATE);
		String jsonUser = userPref.getString("USER_DATA", "NV");
		Gson gson = new Gson();
		user = gson.fromJson(jsonUser, UserDetailsPojo.class);
		Hostname host = new Hostname();
		hostname = host.globalVariable();
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		eventName = bundle.getString("EventName");
		if (eventName == "Ideas" || eventName.equals("Ideas")) {
			actionBar.setTitle("Ideas detail");
		} else if (eventName == "News" || eventName.equals("News")) {
			actionBar.setTitle("News detail");
		} else if (eventName == "Photos" || eventName.equals("Photos")) {
			actionBar.setTitle("Photos detail");
			sdescLay.setVisibility(View.GONE);
			bdescLay.setVisibility(View.GONE);
		} else if (eventName == "Videos" || eventName.equals("Videos")) {
			actionBar.setTitle("Videos detail");
			sdescLay.setVisibility(View.GONE);
			bdescLay.setVisibility(View.GONE);
		} else {
			actionBar.setTitle("NoticeBoard detail");
		}
		eid = bundle.getString("id");

		new EventDetails().execute();

		galleryArti.setOnItemClickListener(new OnItemClickListener() {
			@SuppressWarnings("unchecked")
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				HashMap<String, String> o = (HashMap<String, String>) galleryArti
						.getItemAtPosition(position);
				if (eventName == "News" || eventName.equals("News")
						|| eventName == "Notice" || eventName.equals("Notice")
						|| eventName == "Ideas" || eventName.equals("Ideas")
						|| eventName == "Photos" || eventName.equals("Photos")) {
					Intent i = new Intent(IdeasDetail.this,
							GalleryImageViewer.class);
					i.putExtra("position", position);
					i.putExtra("Images", mediaArr.toString());
					startActivity(i);
				} else if (eventName == "Videos" || eventName.equals("Videos")) {
					Intent i = new Intent(IdeasDetail.this, VideoPlay.class);

					try {
						System.out.println(mediaArr.getString(position)
								.toString());
						JSONObject json = mediaArr.getJSONObject(position);
						i.putExtra("Video", json.getString("image_url"));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					startActivity(i);
				}

			}
		});
		sendBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				netConn = new InternetConnectionDetector(IdeasDetail.this);
				isInternetPresent = netConn.isConnectingToInternet();

				if (comntEdit.getText().toString() == ""
						|| comntEdit.getText().toString().equals("")
						|| comntEdit.getText().toString().length() == 0
						|| comntEdit.getText().toString() == null) {
					showSnack(IdeasDetail.this, "Enter comment!", "OK");
				} else {
					if (isInternetPresent) {
						sendBtn.setText("Adding comment.....");
						new SendComment().execute();
					} else {
						showSnack(IdeasDetail.this,
								"Please check network connection!", "OK");
					}
				}

			}
		});
	}

	public class EventDetails extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			progrLay.setVisibility(View.VISIBLE);
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try {

				jsonDetails = EvenstPost.makeReqeventDetails(hostname, eid);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void unused) {
			if (jsonDetails != null) {
				try {
					titleTxt.setText(jsonDetails.getString("title"));

					if (jsonDetails.getString("sdesc") == null
							|| jsonDetails.getString("sdesc") == "null"
							|| jsonDetails.getString("bdesc") == null
							|| jsonDetails.getString("bdesc") == "null") {
						sdescLay.setVisibility(View.GONE);
						bdescLay.setVisibility(View.GONE);
					} else {
						sDiscTxt.setText(jsonDetails.getString("sdesc"));
						bDiscTxt.setText(jsonDetails.getString("bdesc"));
					}

					mediaArr = jsonDetails.getJSONArray("eventimages");
					if (mediaArr.length() == 0 || mediaArr == null
							|| mediaArr.toString() == "null") {
						imagesLay.setVisibility(View.GONE);
					} else {
						ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
						for (int i = 0; i < mediaArr.length(); i++) {
							HashMap<String, String> map = new HashMap<String, String>();

							JSONObject jsonImages = mediaArr.getJSONObject(i);

							String imgUrl = jsonImages.getString("image_url");

							imagesList = new ArrayList<String>();
							// Object object = mediaArr.get(i);
							// imagesList.add(mediaArr.getString(i));
							// System.out.println(String.valueOf(object));
							// String img = String.valueOf(object);

							map.put("ImagesList", imgUrl);
							list.add(map);
						}
						int photosCount = list.size();

						if (eventName == "Videos" || eventName.equals("Videos")) {
							photosCountTxt.setText(String.valueOf(photosCount)
									+ " " + "Videos");
						} else {
							photosCountTxt.setText(String.valueOf(photosCount)
									+ " " + "Photos");
						}

						System.out.println(imagesList);
						ImagesListBaseAdapter adapter = new ImagesListBaseAdapter(
								IdeasDetail.this, list);
						System.out.println(list);
						galleryArti.setAdapter(adapter);
						System.out.println(imagesList.size());
						if (photosCount > 1) {
							galleryArti.setSelection(1);
						}

						JSONArray cmntArr = jsonDetails
								.getJSONArray("event_comments");
						for (int i = 0; i < cmntArr.length(); i++) {
							HashMap<String, String> map = new HashMap<String, String>();
							JSONObject jsonComments = cmntArr.getJSONObject(i);

							commentDetail = jsonComments.getString("comment");

							comntCreated = jsonComments.getString("created_at");

							comntCreated = comntCreated.substring(0, 10);

							DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
							Date date;
							try {
								date = df.parse(comntCreated);
								System.out.println(date);
								comntCreated = date.toString();
								System.out.println(comntCreated.length());
								comntCreated = comntCreated.substring(4, 10);
								System.out.println(comntCreated);
								comntCreated.replaceAll("\\s+", " ");
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							userName = jsonComments.getString("username");

							map.put("DETAIL", commentDetail);
							map.put("NAME", userName);
							map.put("comntCreated", comntCreated);

							commentsList.add(map);
						}
						commentsAdapter = new ListCommentsBaseAdapter(
								IdeasDetail.this, commentsList);
						commentsListview.setAdapter(commentsAdapter);
						ListViewUtils.setDynamicHeight(commentsListview);
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				progrLay.setVisibility(View.GONE);
			} else {
				progrLay.setVisibility(View.GONE);
				showSnack(IdeasDetail.this,
						"Oops! Something went wrong. Please wait a moment!",
						"OK");
			}
		}

	}

	@SuppressLint("NewApi")
	public class ImagesListBaseAdapter extends BaseAdapter {
		ArrayList<HashMap<String, String>> list;
		private LayoutInflater inflator;
		private Context context;
		ViewHolder holder = null;
		ImageView imageView;

		public ImagesListBaseAdapter(IdeasDetail activity,
				ArrayList<HashMap<String, String>> listArticles) {
			// TODO Auto-generated constructor stub
			this.context = activity;
			this.list = listArticles;
			inflator = (LayoutInflater) activity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public Object getItem(int pos) {
			// TODO Auto-generated method stub
			return list.get(pos);
		}

		@Override
		public long getItemId(int pos) {
			// TODO Auto-generated method stub
			return pos;
		}

		@SuppressWarnings("deprecation")
		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub
			System.out.println(list);
			imageView = new ImageView(context);
			String imgUrl = list.get(position).get("ImagesList").toString();
			System.out.println(imgUrl);
			if (imgUrl == "[]" || imgUrl.equals("[]") || imgUrl == ""
					|| imgUrl.equals("")) {
				/*
				 * Picasso.with(context).load(R.drawable.noimage)
				 * .error(R.drawable.noimage).resize(250, 200) .into(imageView);
				 */

			} else {
				if (eventName == "Videos" || eventName.equals("Videos")) {
					Bitmap bitmap = null;
					MediaMetadataRetriever mediaMetadataRetriever = null;
					try {
						mediaMetadataRetriever = new MediaMetadataRetriever();
						if (Build.VERSION.SDK_INT >= 14)
							mediaMetadataRetriever.setDataSource(imgUrl,
									new HashMap<String, String>());
						else
							mediaMetadataRetriever.setDataSource(imgUrl);
						// mediaMetadataRetriever.setDataSource(videoPath);
						bitmap = mediaMetadataRetriever.getFrameAtTime();
						imageView.setImageBitmap(bitmap);
					} catch (Exception e) {
						e.printStackTrace();
						try {
							throw new Throwable(
									"Exception in retriveVideoFrameFromVideo(String videoPath)"
											+ e.getMessage());
						} catch (Throwable e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

					} finally {
						if (mediaMetadataRetriever != null) {
							mediaMetadataRetriever.release();
						}
					}
				} else {

					imageLoader.displayImage(imgUrl, imageView, options);
					imageView.setScaleType(ImageView.ScaleType.FIT_XY);
					imageView
							.setLayoutParams(new Gallery.LayoutParams(150, 150));
				}
			}

			return imageView;
		}

		public class ViewHolder {

		}

		class OnImageClickListener implements OnClickListener {

			int _postion;

			// constructor
			public OnImageClickListener(int position) {
				this._postion = position;
			}

			@Override
			public void onClick(View v) {
				// on selecting grid view image
				// launch full screen activity
				Intent i = new Intent(context, GalleryImageViewer.class);
				System.out.println(_postion + "CLICKED");
				i.putExtra("position", _postion);
				i.putExtra("Images", mediaArr.toString());
				context.startActivity(i);
			}

		}
	}

	private class SendComment extends AsyncTask<Void, Void, Void> {
		@SuppressLint("InlinedApi")
		protected void onPreExecute() {
			// pDialog.setVisibility(View.VISIBLE);

		}

		@Override
		protected Void doInBackground(Void... params) {
			JSONObject jsonComment = new JSONObject();
			try {
				JSONObject json = new JSONObject();
				json.put("event_id", eid);
				json.put("user_id", userPref.getString("USERID", "NV"));
				json.put("username", user.getUsername());
				json.put("comment", comntEdit.getText().toString());

				jsonComment.put("event_comment", json);
				jsonPushComment = MemsPost.PostComment(jsonComment, hostname);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void unused) {
			System.out.println(jsonPushComment
					+ "^^^^^^^^^^^^ RESULT OF PUSH COMMENT ^^^^^^^^^^^^^^");

			if (jsonPushComment != null) {
				HashMap<String, String> map = new HashMap<String, String>();
				try {
					commentDetail = jsonPushComment.getString("comment");

					comntCreated = jsonPushComment.getString("created_at");

					comntCreated = comntCreated.substring(0, 10);
					DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
					Date date;
					try {
						date = df.parse(comntCreated);
						System.out.println(date);
						comntCreated = date.toString();
						System.out.println(comntCreated.length());
						comntCreated = comntCreated.substring(4, 10);
						System.out.println(comntCreated);
						comntCreated.replaceAll("\\s+", " ");
						// commentImg = jsonCreatedBy.getString("image");
						userName = jsonPushComment.getString("username");
						// lastName = jsonCreatedBy.getString("last_name");

						map.put("DETAIL", commentDetail);
						map.put("NAME", userName);
						map.put("comntCreated", comntCreated);

						commentsList.add(map);
						commentsAdapter = new ListCommentsBaseAdapter(
								getApplicationContext(), commentsList);
						commentsListview.setAdapter(commentsAdapter);
						comntEdit.setText("");
						commentsAdapter.notifyDataSetChanged();
						ListViewUtils.setDynamicHeight(commentsListview);

					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				sendBtn.setText("ADD THIS COMMENT");
			} else {
				showSnack(IdeasDetail.this,
						"Oops! Something went wrong. Please wait a moment!",
						"OK");
			}
		}
	}

	@SuppressLint("NewApi")
	public class ListCommentsBaseAdapter extends BaseAdapter {
		ArrayList<HashMap<String, String>> listt;
		private LayoutInflater inflator;
		private Context context;

		public ListCommentsBaseAdapter(Context context2,
				ArrayList<HashMap<String, String>> listArticles) {
			// TODO Auto-generated constructor stub
			this.context = context2;
			this.listt = listArticles;
			inflator = (LayoutInflater) context2
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public boolean isEnabled(int position) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return listt.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO mylist_add_ind-generated method stub
			return listt.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return listt.get(position).hashCode();
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub
			final ViewHolder holder;
			if (convertView == null) {
				convertView = inflator.inflate(R.layout.comments_listrow, null);
				holder = new ViewHolder();
				holder.dpNameTxt = (TextView) convertView
						.findViewById(R.id.nameTxt);
				holder.comntDetailTxt = (TextView) convertView
						.findViewById(R.id.commentTxt);
				holder.timeTxt = (TextView) convertView
						.findViewById(R.id.timeTxt);
				holder.dpNameTxt.setTypeface(typefaceLight);
				holder.comntDetailTxt.setTypeface(typefaceLight);
				holder.timeTxt.setTypeface(typefaceLight);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.dpNameTxt.setTag(position);
			holder.comntDetailTxt.setTag(position);

			holder.dpNameTxt.setText(listt.get(position).get("NAME"));
			holder.comntDetailTxt.setText(Html.fromHtml(listt.get(position)
					.get("DETAIL")));
			holder.timeTxt.setText(Html.fromHtml(listt.get(position).get(
					"comntCreated")));

			return convertView;
		}

		public class ViewHolder {
			public TextView dpNameTxt, comntDetailTxt, timeTxt;
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

	public void onBackPressed() {

		if (eventName == "Photos" || eventName.equals("Photos")) {
			Intent soc = new Intent(IdeasDetail.this, PhotosList.class);
			soc.putExtra("EventName", eventName);
			startActivity(soc);
		} else if (eventName == "Videos" || eventName.equals("Videos")) {
			Intent soc = new Intent(IdeasDetail.this, PhotosList.class);
			soc.putExtra("EventName", eventName);
			startActivity(soc);
		} else {
			Intent soc = new Intent(IdeasDetail.this, IdeasList.class);
			soc.putExtra("EventName", eventName);
			startActivity(soc);
		}
	}

	void showSnack(IdeasDetail flats, String stringMsg, String ok) {
		new SnackBar(IdeasDetail.this, stringMsg, ok, new OnClickListener() {

			@Override
			public void onClick(View v) {
			}
		}).show();
	}
}
