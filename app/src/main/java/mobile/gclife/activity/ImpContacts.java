package mobile.gclife.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import mobile.gclife.MaterialDesign.ProgressBarCircularIndeterminate;
import mobile.gclife.PojoGson.MyApplication;
import mobile.gclife.http.EvenstPost;
import mobile.gclife.http.Hostname;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
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
import android.widget.TextView;

import com.gc.materialdesign.views.ButtonFloat;
import com.gc.materialdesign.widgets.SnackBar;

public class ImpContacts extends ActionBarActivity {
	android.support.v7.app.ActionBar actionBar;
	ButtonFloat addBtn;
	Hostname host = new Hostname();
	ProgressBarCircularIndeterminate pDialog;
	ListView listviewImp;
	JSONArray jsonResultArry;
	SharedPreferences userPref;
	Typeface typefaceLight;
	String name, phNo, email;
	ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
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
		actionBar.setTitle("Imp Contacts");

		addBtn = (ButtonFloat) findViewById(R.id.addBtn);
		pDialog = (ProgressBarCircularIndeterminate) findViewById(R.id.pDialog);
		listviewImp = (ListView) findViewById(R.id.listview);
		listviewImp.setClickable(false);
		typefaceLight = Typeface.createFromAsset(getAssets(),
				"fonts/RobotoLight.ttf");
		addBtn.setVisibility(View.GONE);
		userPref = getSharedPreferences("USER", MODE_PRIVATE);
		
		listviewImp
		.setOnItemClickListener(new OnItemClickListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void onItemClick(AdapterView<?> parent,
					View view, int position, long id) {
				// TODO Auto-generated method stub
				showPopup(list,position);
			}
		});

		
		new ImpContact().execute();
	}

	protected void showPopup(ArrayList<HashMap<String, String>> list, int position) {
		// TODO Auto-generated method stub
		Dialog m_dialog = new Dialog(ImpContacts.this);
		m_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		m_dialog.setContentView(R.layout.imp_contacts_popup_listview);
		m_dialog.getWindow().getAttributes().windowAnimations = R.style.popup_login_dialog_animation;
		ListView listviewConts = (ListView) m_dialog
				.findViewById(R.id.listview);

		phNo = list.get(position).get("phno");

		String[] phNoArray = phNo.split(",");
		// String[]
		// emailArr=list.get(position).get("email").split(",");

		System.out.println(phNoArray
				+ "!!!!!!!!!!!!!!!!!!!!!! PH NO's");
		List<String> phList = Arrays.asList(phNoArray);
		// List<String> emailList = Arrays.asList(emailArr);
		ArrayList<HashMap<String, String>> listConts = new ArrayList<HashMap<String, String>>();
		for (int i = 0; i < phNoArray.length; i++) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("PHNO", phList.get(i));
			map.put("NAME", list.get(i).get("title"));
			map.put("EMAIL", list.get(i).get("email"));
			listConts.add(map);
		}
		ListImpPopupBaseAdapter adapter = new ListImpPopupBaseAdapter(
				ImpContacts.this, listConts);
		listviewConts.setAdapter(adapter);

		m_dialog.show();
	}

	public class ImpContact extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			pDialog.setVisibility(View.VISIBLE);
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try {
				jsonResultArry = EvenstPost.makeRequestImpContList(host
						.globalVariable());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void unused) {

			System.out.println(jsonResultArry
					+ "LIST OF IMP CONTS !!!!!!!!!!!!!!!!!!!!");
			if (jsonResultArry != null) {
				if (jsonResultArry.length() == 0
						|| jsonResultArry.toString() == "[]"
						|| jsonResultArry.toString() == ""
						|| jsonResultArry.toString().equals("")) {
					pDialog.setVisibility(View.GONE);
					showSnack(ImpContacts.this,
							"Oops! There is no contacts!",
							"OK");
				} else {
					
					for (int i = 0; i < jsonResultArry.length(); i++) {
						HashMap<String, String> map = new HashMap<String, String>();
						try {
							JSONObject jsonContacts = jsonResultArry
									.getJSONObject(i);
							map.put("title", jsonContacts.getString("name"));
							map.put("phno", jsonContacts.getString("phno"));
							map.put("email", jsonContacts.getString("email"));

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						list.add(map);
					}
					ListImpContsBaseAdapter adapter = new ListImpContsBaseAdapter(
							ImpContacts.this, list);
					listviewImp.setAdapter(adapter);
					pDialog.setVisibility(View.GONE);
				}
			} else {
				pDialog.setVisibility(View.GONE);
				showSnack(ImpContacts.this,
						"Oops! Something went wrong. Please wait a moment!",
						"OK");
			}

		}
	}

	public class ListImpContsBaseAdapter extends BaseAdapter {
		ArrayList<HashMap<String, String>> list;
		private LayoutInflater inflator;
		private Context context;

		public ListImpContsBaseAdapter(Activity activity,
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
			ViewHolder holder;
			if (convertView == null) {
				convertView = inflator.inflate(R.layout.imp_contacts_row,
						parent, false);
				holder = new ViewHolder();
				holder.titleTxt = (TextView) convertView
						.findViewById(R.id.titleTxt);
				holder.optionsImg = (ImageView) convertView
						.findViewById(R.id.optionsImg);

				holder.titleTxt.setTypeface(typefaceLight);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.optionsImg.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

				showPopup(list,position);
				}
			});
			holder.titleTxt.setText(list.get(position).get("title"));

			return convertView;
		}

		public class ViewHolder {
			TextView titleTxt;
			ImageView optionsImg;
		}
	}

	public class ListImpPopupBaseAdapter extends BaseAdapter {
		ArrayList<HashMap<String, String>> list;
		private LayoutInflater inflator;
		private Context context;

		public ListImpPopupBaseAdapter(Context context2,
				ArrayList<HashMap<String, String>> listArticles) {
			// TODO Auto-generated constructor stub
			this.context = context2;
			this.list = listArticles;
			inflator = (LayoutInflater) context2
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
			ViewHolder holder;
			if (convertView == null) {
				convertView = inflator.inflate(R.layout.imp_contacts_popup,
						parent, false);
				holder = new ViewHolder();
				holder.titleTxt = (TextView) convertView
						.findViewById(R.id.titleTxt);
				holder.phNumTxt = (TextView) convertView
						.findViewById(R.id.phNumTxt);
				holder.emailTxt = (TextView) convertView
						.findViewById(R.id.emailTxt);
				holder.callImg = (ImageView) convertView
						.findViewById(R.id.phoneImg);
				holder.smsImg = (ImageView) convertView
						.findViewById(R.id.smsImg);
				holder.emailImg = (ImageView) convertView
						.findViewById(R.id.emailImg);
				holder.titleTxt.setTypeface(typefaceLight);
				holder.phNumTxt.setTypeface(typefaceLight);
				holder.emailTxt.setTypeface(typefaceLight);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.callImg.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(Intent.ACTION_DIAL);

					intent.setData(Uri.parse("tel:" + list.get(position).get("PHNO")));
					context.startActivity(intent);
				}
			});
			holder.smsImg.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent smsVIntent = new Intent(Intent.ACTION_VIEW);
					smsVIntent.setType("vnd.android-dir/mms-sms");
					smsVIntent.putExtra("address", list.get(position).get("PHNO"));
					try {
						startActivity(smsVIntent);
					} catch (Exception ex) {
						showSnack(ImpContacts.this,
								"Your sms has failed...!",
								"OK");
						ex.printStackTrace();
					}
				}
			});
			holder.emailImg.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent emailIntent = new Intent(Intent.ACTION_SENDTO,
							Uri.fromParts("mailto", list.get(position).get("EMAIL"), null));
					startActivity(Intent.createChooser(emailIntent, "Send email..."));
				}
			});
			holder.titleTxt.setText(list.get(position).get("NAME"));
			holder.phNumTxt.setText(list.get(position).get("PHNO"));
			holder.emailTxt.setText(list.get(position).get("EMAIL"));

			return convertView;
		}

		public class ViewHolder {
			TextView titleTxt, phNumTxt, emailTxt;
			ImageView callImg, smsImg, emailImg;
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
	void showSnack(ImpContacts flats, String stringMsg, String ok) {
		new SnackBar(ImpContacts.this, stringMsg, ok, new OnClickListener() {

			@Override
			public void onClick(View v) {
			}
		}).show();
	}
}