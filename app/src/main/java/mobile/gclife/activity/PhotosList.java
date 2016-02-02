package mobile.gclife.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mobile.gclife.MaterialDesign.ProgressBarCircularIndeterminate;
import mobile.gclife.PojoGson.FlatDetailsPojo;
import mobile.gclife.PojoGson.MyApplication;
import mobile.gclife.PojoGson.UserDetailsPojo;
import mobile.gclife.application.InternetConnectionDetector;
import mobile.gclife.http.EvenstPost;
import mobile.gclife.http.Hostname;
import mobile.gclife.http.MemsPost;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gc.materialdesign.views.ButtonFloat;
import com.gc.materialdesign.widgets.SnackBar;
import com.google.gson.Gson;

public class PhotosList extends ActionBarActivity {
	android.support.v7.app.ActionBar actionBar;
	ButtonFloat addBtn;
	ProgressBarCircularIndeterminate pDialog;
	InternetConnectionDetector netConn;
	UserDetailsPojo user;
	Boolean isInternetPresent = false;
	String hostname, eventName, eid,deleteEveId;
	ListView listviewIdeas;
	JSONArray jsonResultArry;
	SharedPreferences userPref;
	List<FlatDetailsPojo> flatsList = new ArrayList<FlatDetailsPojo>();
	FlatDetailsPojo flats;
	Typeface typefaceLight;
	JSONObject jsonLike,jsonDelete;
	ListIdeasBaseAdapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ideas_list);
		actionBar = getSupportActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setIcon(android.R.color.transparent);
		actionBar.setBackgroundDrawable(new ColorDrawable(Color
				.parseColor(MyApplication.actiobarColor)));

		addBtn = (ButtonFloat) findViewById(R.id.addBtn);
		pDialog = (ProgressBarCircularIndeterminate) findViewById(R.id.pDialog);
		listviewIdeas = (ListView) findViewById(R.id.listview);
		typefaceLight = Typeface.createFromAsset(getAssets(),
				"fonts/RobotoLight.ttf");
		userPref = getSharedPreferences("USER", MODE_PRIVATE);
		Hostname host = new Hostname();
		hostname = host.globalVariable();

		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		eventName = bundle.getString("EventName");
		if (eventName == "Photos" || eventName.equals("Photos")) {
			actionBar.setTitle("Photos");
		} else {
			actionBar.setTitle("Videos");
		}

		addBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(PhotosList.this, PhotosCreate.class);
				i.putExtra("EventName", eventName);
				startActivity(i);
			}
		});
		Gson gson = new Gson();
		String jsonUser = userPref.getString("USER_DATA", "NV");
		user = gson.fromJson(jsonUser, UserDetailsPojo.class);
		flatsList = user.getGclife_registration_flatdetails();

		flats = flatsList.get(0);

		listviewIdeas.setOnItemClickListener(new OnItemClickListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				HashMap<String, String> o = (HashMap<String, String>) listviewIdeas
						.getItemAtPosition(position);
				Intent i = new Intent(PhotosList.this, IdeasDetail.class);
				i.putExtra("EventName", eventName);
				i.putExtra("id", o.get("id"));
				startActivity(i);
			}
		});

		new ListIdeas().execute();

	}

	public class ListIdeas extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			pDialog.setVisibility(View.VISIBLE);
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try {
				jsonResultArry = EvenstPost
						.callIdeasList(hostname,
								userPref.getString("USERID", "NV"),
								flats.getAvenue_name(), flats.getSocietyid(),
								eventName);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void unused) {

			System.out.println(jsonResultArry
					+ "LIST OF Ideas !!!!!!!!!!!!!!!!!!!!");
			if (jsonResultArry != null) {
				if (jsonResultArry.length() == 0
						|| jsonResultArry.toString() == "[]"
						|| jsonResultArry.toString() == ""
						|| jsonResultArry.toString().equals("")) {
					pDialog.setVisibility(View.GONE);
					if (eventName == "Photos" || eventName.equals("Photos")) {
						showSnack(PhotosList.this, "Oops! There is no photos!",
								"OK");
					} else if (eventName == "Videos"
							|| eventName.equals("Videos")) {
						showSnack(PhotosList.this, "Oops! There is no videos!",
								"OK");
					} else {

					}
				} else {

					ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

					for (int i = 0; i < jsonResultArry.length(); i++) {
						HashMap<String, String> map = new HashMap<String, String>();
						try {
							JSONObject jsonIdea = jsonResultArry
									.getJSONObject(i);
							JSONArray mediaArr = jsonIdea
									.getJSONArray("eventimages");
							JSONArray cmntArr = jsonIdea
									.getJSONArray("event_comments");
							JSONArray lieksArr = jsonIdea
									.getJSONArray("event_likes");
							int attchCount = mediaArr.length();
							int cmntCount = cmntArr.length();
							int likesCount = lieksArr.length();
							if (likesCount == 0) {
								map.put("LikeId", "");
							} else {
								for (int j = 0; j < lieksArr.length(); j++) {
									JSONObject jsonLikes = lieksArr
											.getJSONObject(j);
									map.put("LikeId", jsonLikes.getString("id"));
								}
							}
							map.put("id", jsonIdea.getString("id"));
							map.put("title", jsonIdea.getString("title"));
							map.put("sdesc", jsonIdea.getString("sdesc"));
							map.put("attchCount", String.valueOf(attchCount));
							map.put("cmntCount", String.valueOf(cmntCount));
							map.put("likesCount", String.valueOf(likesCount));
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						list.add(map);
					}
					 adapter = new ListIdeasBaseAdapter(
							PhotosList.this, list);
					listviewIdeas.setAdapter(adapter);
					pDialog.setVisibility(View.GONE);

				}
			} else {
				pDialog.setVisibility(View.GONE);
				showSnack(PhotosList.this,
						"Oops! Something went wrong. Please wait a moment!",
						"OK");
			}

		}
	}

	public class ListIdeasBaseAdapter extends BaseAdapter {
		ArrayList<HashMap<String, String>> list;
		private LayoutInflater inflator;
		private Context context;
		Boolean like = false;
		ArrayList<String> likeCheckArr = new ArrayList<String>();

		public ListIdeasBaseAdapter(Activity activity,
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

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub
			final ViewHolder holder;
			if (convertView == null) {
				convertView = inflator.inflate(R.layout.photo_list_row, parent,
						false);
				holder = new ViewHolder();
				holder.titleTxt = (TextView) convertView
						.findViewById(R.id.titleTxt);
				holder.attchCountTxt = (TextView) convertView
						.findViewById(R.id.attchcountTxt);
				holder.comntCountTxt = (TextView) convertView
						.findViewById(R.id.cmntcountTxt);
				holder.likesCountTxt = (TextView) convertView
						.findViewById(R.id.likecountTxt);
				holder.detailClick = (RelativeLayout) convertView
						.findViewById(R.id.detailClick);
				holder.shareImg = (ImageView) convertView
						.findViewById(R.id.shareImg);
				holder.likeImg = (ImageView) convertView
						.findViewById(R.id.likeImg);
				holder.deleteImg = (ImageView) convertView
						.findViewById(R.id.deleteImg);
				holder.titleTxt.setTypeface(typefaceLight);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.titleTxt.setText(list.get(position).get("title"));
			holder.attchCountTxt.setText(list.get(position).get("attchCount"));
			holder.comntCountTxt.setText(list.get(position).get("cmntCount"));
			holder.likesCountTxt.setText(list.get(position).get("likesCount"));
			eid = list.get(position).get("id");

			if (list.get(position).get("LikeId") == ""
					|| list.get(position).get("LikeId").equals("")) {

			} else {
				likeCheckArr.add(list.get(position).get("LikeId"));
			}

			if (likeCheckArr.contains(list.get(position).get("LikeId"))) {
				holder.likeImg.setImageResource(R.drawable.liked);
			} else {
				holder.likeImg.setImageResource(R.drawable.unlike);
			}
			if(list.get(position).get("id")==userPref.getString("USERID", "NV")||list.get(position).get("id").equals(userPref.getString("USERID", "NV"))){
				holder.deleteImg.setVisibility(View.VISIBLE);
			}else{
				holder.deleteImg.setVisibility(View.GONE);
			}
			holder.detailClick.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent i = new Intent(PhotosList.this, IdeasDetail.class);
					i.putExtra("EventName", eventName);
					i.putExtra("id", list.get(position).get("id"));
					startActivity(i);
				}
			});
			holder.deleteImg.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					// to remove row
					final Dialog m_dialog = new Dialog(PhotosList.this);
					m_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
					m_dialog.setContentView(R.layout.areyousuredelepopup);
					m_dialog.getWindow().getAttributes().windowAnimations = R.style.popup_login_dialog_animation;
					
					TextView noTxt=(TextView)m_dialog.findViewById(R.id.noTxt);
					TextView yesTxt=(TextView)m_dialog.findViewById(R.id.yesTxt);
					noTxt.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							m_dialog.dismiss();
						}
					});
					yesTxt.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							deleteEveId = list.get(position).get("id");
							new DeleteEvent().execute();
							list.remove(position);
						}
					});
					
					m_dialog.show();
					

				}
			});
			holder.shareImg.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent sharingIntent = new Intent(
							Intent.ACTION_SEND);
					sharingIntent.setType("text/plain");
					sharingIntent.putExtra(
							Intent.EXTRA_SUBJECT,
							list.get(position).get("title"));
					sharingIntent.putExtra(
							Intent.EXTRA_TEXT,
							list.get(position).get("sdesc") + "\n"
									+ list.get(position).get("bdesc"));
					startActivity(Intent.createChooser(sharingIntent,
							"Share via"));
				}
			});
			holder.likeImg.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					System.out.println(list);
					System.out
							.println(likeCheckArr + "  ******************** ");
					System.out.println(list.get(position).get("LikeId")
							+ "  ******************** ");
					if (likeCheckArr.contains(list.get(position).get("LikeId"))) {
						showSnack(PhotosList.this,
								"Oops! You already liked it!", "OK");
						// holder.likeImg.setImageResource(R.drawable.unlike);
					} else {
						eid = list.get(position).get("id");
						int likes = Integer.valueOf(list.get(position).get(
								"likesCount"));
						int lik = likes + 1;
						holder.likesCountTxt.setText(String.valueOf(lik));
						holder.likeImg.setImageResource(R.drawable.liked);
						LikeUnlike();
					}
				}
			});
			return convertView;
		}

		protected void LikeUnlike() {
			// TODO Auto-generated method stub
			new Like().execute();
		}

		public class ViewHolder {
			TextView titleTxt, sDiscTxt, attchCountTxt, comntCountTxt,
					likesCountTxt;
			ImageView shareImg, likeImg, deleteImg;
			RelativeLayout detailClick;
		}
	}

	private class Like extends AsyncTask<Void, Void, Void> {
		@SuppressLint("InlinedApi")
		protected void onPreExecute() {
			// pDialog.setVisibility(View.VISIBLE);

		}

		@Override
		protected Void doInBackground(Void... params) {
			JSONObject jsonLik = new JSONObject();
			try {
				JSONObject json = new JSONObject();
				json.put("event_id", eid);
				json.put("user_id", userPref.getString("USERID", "NV"));
				jsonLik.put("event_like", json);
				jsonLike = MemsPost.PostLike(jsonLik, hostname);
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
			System.out.println(jsonLike
					+ "^^^^^^^^^^^^ RESULT OF LIKES ^^^^^^^^^^^^^^");

			if (jsonLike != null) {
				if (jsonLike.has("errors")) {
					showSnack(PhotosList.this, "Oops! You already liked it!",
							"OK");
				}
			} else {
				showSnack(PhotosList.this,
						"Oops! Something went wrong. Please wait a moment!",
						"OK");
			}
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
	public class DeleteEvent extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
		//	pDialogPop.setVisibility(View.VISIBLE);
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try {
				System.out.println(deleteEveId + " **************");
				jsonDelete = EvenstPost.makeDelete(hostname, deleteEveId,"events");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void unused) {
		//	m_dialog.dismiss();
		//	pDialogPop.setVisibility(View.GONE);
		//	submitTxt.setVisibility(View.INVISIBLE);
			showSnack(PhotosList.this,
					"Deleted!",
					"OK");
			adapter.notifyDataSetChanged();
		}
	}

	public void onBackPressed() {
		Intent soc = new Intent(PhotosList.this, HomeApp.class);
		soc.putExtra("EventName", eventName);
		startActivity(soc);
	}

	void showSnack(PhotosList login, String stringMsg, String ok) {
		new SnackBar(PhotosList.this, stringMsg, ok, new OnClickListener() {

			@Override
			public void onClick(View v) {
			}
		}).show();
	}
}
