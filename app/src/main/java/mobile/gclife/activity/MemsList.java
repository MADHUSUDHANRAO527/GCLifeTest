package mobile.gclife.activity;

import java.util.ArrayList;

import mobile.gclife.MaterialDesign.ProgressBarCircularIndeterminate;
import mobile.gclife.PojoGson.FlatDetailsPojo;
import mobile.gclife.PojoGson.MyApplication;
import mobile.gclife.PojoGson.UserDetailsPojo;
import mobile.gclife.http.Hostname;
import mobile.gclife.http.MemsPost;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.gc.materialdesign.widgets.SnackBar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class MemsList extends ActionBarActivity {
	android.support.v7.app.ActionBar actionBar;
	ListView listviewMem;
	JSONArray jsonResultArry;
	JSONObject veriJson;
	SharedPreferences userPref;
	String hostname, email, status;
	int memId;
	JSONObject json;
	static Typeface typefaceLight;
	ProgressBarCircularIndeterminate pDialog;
	ProgressBarCircularIndeterminate pDialogPop;
	Dialog m_dialog;
	UserModelAdapter dataTaskGrpAdapter;
	TextView submitTxt,loginWithEMailTxt,discriptionEdit;
	CheckBox checkboxApprove,rejectCheckBox,deleteCheckBox;
	SharedPreferences.Editor editor;
	ArrayList<UserDetailsPojo> userList = new ArrayList<UserDetailsPojo>();
	UserDetailsPojo users;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_members);

		pDialog = (ProgressBarCircularIndeterminate) findViewById(R.id.pDialog);

		listviewMem = (ListView) findViewById(R.id.listViewMem);
		actionBar = getSupportActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setIcon(android.R.color.transparent);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("GC Member Verification");
		actionBar.setBackgroundDrawable(new ColorDrawable(Color
				.parseColor(MyApplication.actiobarColor)));
		Hostname host = new Hostname();
		hostname = host.globalVariable();
		userPref = getSharedPreferences("USER", MODE_PRIVATE);
		editor = userPref.edit();
		typefaceLight = Typeface.createFromAsset(getAssets(),
				"fonts/RobotoLight.ttf");
		listviewMem.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent i = new Intent(MemsList.this, UserProfile.class);

				Gson gsonn = new Gson();
				String json = gsonn.toJson(userList.get(position));
				
				i.putExtra("EACH_USER_DET", json);
				startActivity(i);
				overridePendingTransition(R.anim.slide_in_left,
						R.anim.slide_out_left);
			}
		});
		new ListMems().execute();
	}

	public class ListMems extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			pDialog.setVisibility(View.VISIBLE);
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try {
				jsonResultArry = MemsPost.callMemsList(hostname,
						userPref.getString("USERID", "NV"));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void unused) {

			System.out.println(jsonResultArry
					+ "LIST OF MEMS !!!!!!!!!!!!!!!!!!!!");
			if (jsonResultArry != null) {
				if (jsonResultArry.length() == 0
						|| jsonResultArry.toString() == "[]"
						|| jsonResultArry.toString() == ""
						|| jsonResultArry.toString().equals("")) {
					pDialog.setVisibility(View.GONE);
					showSnack(MemsList.this,
							"Oops! There is no members!",
							"OK");
				} else {
					for (int i = 0; i < jsonResultArry.length(); i++) {
						try {
							JSONObject jsonObj = jsonResultArry.getJSONObject(i);
							Gson gson = new GsonBuilder().create();
							 users = gson.fromJson(
									jsonObj.toString(), UserDetailsPojo.class);
							 userList.add(users);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
					 dataTaskGrpAdapter = new UserModelAdapter(
							MemsList.this, R.layout.member_row, userList);
					listviewMem.setAdapter(dataTaskGrpAdapter);
					pDialog.setVisibility(View.GONE);
				}
			} else {
				pDialog.setVisibility(View.VISIBLE);
				showSnack(MemsList.this,
						"Oops! Something went wrong. Please wait a moment!",
						"OK");
			}

		}
	}
	private class UserModelAdapter extends ArrayAdapter<UserDetailsPojo> {

		ArrayList<UserDetailsPojo> usersList;
		LayoutInflater inflator;
		UserDetailsPojo users;

		public UserModelAdapter(Context context, int textViewResourceId,
				ArrayList<UserDetailsPojo> usersList) {
			super(context, textViewResourceId, usersList);
			this.usersList = new ArrayList<UserDetailsPojo>();
			this.usersList.addAll(usersList);
			inflator = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return usersList.size();
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {

			final ViewHolder holder;
			Log.v("ConvertView", String.valueOf(position));
			  users = usersList.get(position);
			if (convertView == null) {
				convertView = inflator.inflate(R.layout.member_row, parent,
						false);
				holder = new ViewHolder();
				holder.usernameTxt = (TextView) convertView
						.findViewById(R.id.usernameTxt);
				holder.avenueNameTxt = (TextView) convertView
						.findViewById(R.id.avenueNameTxt);
				holder.profileImg = (ImageView) convertView
						.findViewById(R.id.profileImg);
				holder.settingsImg = (ImageView) convertView
						.findViewById(R.id.settingImg);

				holder.usernameTxt.setTypeface(typefaceLight);
				holder.avenueNameTxt.setTypeface(typefaceLight);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			System.out.println(users.getGclife_registration_flatdetails().size()-1+" ************************** ");
			
			holder.usernameTxt.setText(users.getUsername());
			
			if(users.getGclife_registration_flatdetails().size()==0){
				
			}else{
				FlatDetailsPojo flatsList = users.getGclife_registration_flatdetails().get(users.getGclife_registration_flatdetails().size()-1);
				holder.avenueNameTxt.setText(flatsList.getAvenue_name()+" , "+flatsList.getFlat_number());
			}
			
						
			

			holder.profileImg.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent i = new Intent(MemsList.this, UserProfile.class);
					System.out.println(position + " ^^^^^^^^^^^^^^^ ");
					Gson gsonn = new Gson();
					String json = gsonn.toJson(usersList.get(position));
					i.putExtra("EACH_USER_DET", json);
					startActivity(i);
					overridePendingTransition(R.anim.slide_in_left,
							R.anim.slide_out_left);
				}
			});
		
			
			holder.settingsImg.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
					System.out.println(usersList.get(position).getUsername() + "     "+position + " POSITION !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
					
					m_dialog = new Dialog(MemsList.this);
					m_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
					m_dialog.setContentView(R.layout.mem_approval_popup);
					m_dialog.getWindow().getAttributes().windowAnimations = R.style.popup_login_dialog_animation;
					TextView cancellTxt = (TextView) m_dialog
							.findViewById(R.id.cancellTxt);
					loginWithEMailTxt= (TextView) m_dialog
							.findViewById(R.id.loginwithEmailTxt);
					discriptionEdit= (TextView) m_dialog
							.findViewById(R.id.discription);
					submitTxt = (TextView) m_dialog
							.findViewById(R.id.submitTxt);
					pDialogPop = (ProgressBarCircularIndeterminate) m_dialog
							.findViewById(R.id.pDialog);
					  checkboxApprove = (CheckBox) m_dialog
							.findViewById(R.id.approveCheckBox);
					  rejectCheckBox = (CheckBox) m_dialog
							.findViewById(R.id.rejectCheckBox);
					  deleteCheckBox = (CheckBox) m_dialog
							.findViewById(R.id.deleteCheckBox);
					// MaterialDesign.ProgressBarCircularIndeterminate
					// popUpDialog=(ProgressBarCircularIndeterminate)findViewById(R.id.popUpDialog);

					submitTxt.setTypeface(typefaceLight);
					cancellTxt.setTypeface(typefaceLight);
					discriptionEdit.setTypeface(typefaceLight);
					loginWithEMailTxt.setTypeface(typefaceLight);

					cancellTxt.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							m_dialog.dismiss();
						}
					});
					submitTxt.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {

							if (status==null||status=="null"||status == "" || status.equals("")) {
								showSnack(MemsList.this,
										"Select atleast one action!",
										"OK");
							} else {
								memId = usersList.get(position).getId();
								System.out.println(memId);
								// to remove row
								usersList.remove(position);
								
								new ActivateMems().execute();
							}

						}
					});
					checkboxApprove.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							if (((CheckBox) v).isChecked()) {
								System.out.println("CHECKED");
								rejectCheckBox.setChecked(false);
								deleteCheckBox.setChecked(false);
								status = "Approve";
							} else {
								System.out.println("UN CHECKED");
								status = "";
								// checkboxAll.setChecked(false);
							}
						}
					});
					rejectCheckBox.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							if (((CheckBox) v).isChecked()) {
								System.out.println("CHECKED");
								checkboxApprove.setChecked(false);
								deleteCheckBox.setChecked(false);
								status = "Reject";
							} else {
								System.out.println("UN CHECKED");
								// checkboxAll.setChecked(false);
								status = "";
							}
						}
					});
					deleteCheckBox.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							if (((CheckBox) v).isChecked()) {
								status = "Delete";
								System.out.println("CHECKED");
								checkboxApprove.setChecked(false);
								rejectCheckBox.setChecked(false);
							} else {
								System.out.println("UN CHECKED");
								// checkboxAll.setChecked(false);
								status = "";
							}
						}
					});

					m_dialog.show();
				}
			});

			return convertView;
		}

		public class ViewHolder {
			TextView usernameTxt, avenueNameTxt;
			ImageView profileImg, settingsImg;
		}
	}

	public class ActivateMems extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			submitTxt.setVisibility(View.INVISIBLE);
			pDialogPop.setVisibility(View.VISIBLE);
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try {
				veriJson = MemsPost.activateMem(hostname, memId, status);
				System.out.println(veriJson + "*************************");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void unused) {
			m_dialog.dismiss();
			pDialogPop.setVisibility(View.GONE);
			submitTxt.setVisibility(View.INVISIBLE);
			showSnack(MemsList.this,
					"Approved!",
					"OK");
			dataTaskGrpAdapter.notifyDataSetChanged();
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
	void showSnack(MemsList login, String stringMsg, String ok) {
		new SnackBar(MemsList.this, stringMsg, ok, new OnClickListener() {

			@Override
			public void onClick(View v) {
			}
		}).show();
	}
}
