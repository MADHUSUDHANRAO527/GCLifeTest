package mobile.gclife.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import mobile.gclife.MaterialDesign.ProgressBarCircularIndeterminate;
import mobile.gclife.PojoGson.FlatDetailsPojo;
import mobile.gclife.PojoGson.MyApplication;
import mobile.gclife.PojoGson.UserDetailsPojo;
import mobile.gclife.application.FilePicker;
import mobile.gclife.application.NothingSelectedSpinnerAdapter1;
import mobile.gclife.http.Hostname;
import mobile.gclife.http.SocietyBillPost;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.gc.materialdesign.widgets.SnackBar;
import com.google.gson.Gson;

public class SocietyBillManagement extends ActionBarActivity {
	android.support.v7.app.ActionBar actionBar;
	Spinner societyNameSpinner, chooseyrSpiner, monthsSpinner,
			actionTypeSpiner;
	String actionType, fileSelectedPath;
	private static final int REQUEST_PICK_FILE = 1;
	LinearLayout viewLay, totBilAmountLay, dueStatusLay, paidStatusCountLay,
			confrmLay;
	String hostname, societyName = "", financialYr = "", monthName = "";
	JSONArray jsonResultArry;
	JSONObject jsonBill, jsonViewBill;
	SharedPreferences userPref;
	UserDetailsPojo user;
	List<FlatDetailsPojo> flatsList = new ArrayList<FlatDetailsPojo>();
	TextView submitTxt, totBilAmountTxt, dueStatusTxt, paidStatusCountTxt,
			dueAmountTxt, confrmdAmount, balnceAmountTxt, comfrmedCountTxt,fileNameTxt;
	private File selectedFile;
	ProgressBarCircularIndeterminate pDialog;
	JSONObject societyBillManagementDataJson;
	JSONArray totalDataArr, paidStatusDataArr, confirmedStatusDataArr,
			dueStatusDataArr;
	boolean action = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.societybillmanagement);

		societyNameSpinner = (Spinner) findViewById(R.id.societySpin);
		chooseyrSpiner = (Spinner) findViewById(R.id.yrSpin);
		monthsSpinner = (Spinner) findViewById(R.id.monthSpin);
		actionTypeSpiner = (Spinner) findViewById(R.id.actionTypeSpin);
		viewLay = (LinearLayout) findViewById(R.id.viewLay);
		totBilAmountLay = (LinearLayout) findViewById(R.id.totBilAmountLay);
		dueStatusLay = (LinearLayout) findViewById(R.id.dueStatusLay);
		paidStatusCountLay = (LinearLayout) findViewById(R.id.paidStatusCountLay);
		confrmLay = (LinearLayout) findViewById(R.id.confrmLay);
		submitTxt = (TextView) findViewById(R.id.submitTxt);

		totBilAmountTxt = (TextView) findViewById(R.id.totBilAmountTxt);
		dueStatusTxt = (TextView) findViewById(R.id.dueStatusTxt);
		comfrmedCountTxt = (TextView) findViewById(R.id.comfrmedCountTxt);
		paidStatusCountTxt = (TextView) findViewById(R.id.paidStatusCountTxt);
		dueAmountTxt = (TextView) findViewById(R.id.dueAmountTxt);
		confrmdAmount = (TextView) findViewById(R.id.confrmdAmount);
		balnceAmountTxt = (TextView) findViewById(R.id.balnceAmountTxt);
		fileNameTxt=(TextView) findViewById(R.id.fileName);
		pDialog = (ProgressBarCircularIndeterminate) findViewById(R.id.progressBarCircularIndetermininate);

		actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setIcon(android.R.color.transparent);
		actionBar.setTitle("Society Bill Management");
		actionBar.setBackgroundDrawable(new ColorDrawable(Color
				.parseColor(MyApplication.actiobarColor)));

		userPref = getSharedPreferences("USER", MODE_PRIVATE);

		Gson gson = new Gson();
		String jsonUser = userPref.getString("USER_DATA", "NV");
		user = gson.fromJson(jsonUser, UserDetailsPojo.class);

		// list flat details
		List<FlatDetailsPojo> flatsList = user
				.getGclife_registration_flatdetails();

		System.out
				.println(flatsList.size() + " ******************************");

		List<String> sociList = new ArrayList<String>();
		for (int i = 0; i < flatsList.size(); i++) {

			FlatDetailsPojo flatsListt = user
					.getGclife_registration_flatdetails().get(i);
			societyName = flatsListt.getSocietyid();
			System.out.println(societyName + "!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			sociList.add(societyName);
		}

		System.out.println(sociList + "  ---------------------------------");

		ArrayAdapter<String> societyAdapter = new ArrayAdapter<String>(this,
				R.layout.spinr_txt, sociList);

		societyAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		societyNameSpinner.setAdapter(new NothingSelectedSpinnerAdapter1(
				societyAdapter, R.layout.society_spinner_nothing_selected,
				SocietyBillManagement.this));

		Hostname host = new Hostname();
		hostname = host.globalVariable();

		ArrayAdapter<CharSequence> yrAdapter = ArrayAdapter.createFromResource(
				this, R.array.finaYr, R.layout.spinr_txt);
		yrAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		chooseyrSpiner.setAdapter(new NothingSelectedSpinnerAdapter1(yrAdapter,
				R.layout.year_spinner_nothing_selected, this));

		ArrayAdapter<CharSequence> monthsAdapter = ArrayAdapter
				.createFromResource(this, R.array.monthsArr, R.layout.spinr_txt);
		monthsAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		monthsSpinner.setAdapter(new NothingSelectedSpinnerAdapter1(
				monthsAdapter, R.layout.month_spinner_nothing_selected, this));

		ArrayAdapter<CharSequence> actionTypeAdapter = ArrayAdapter
				.createFromResource(this, R.array.actionTypeArray,
						R.layout.spinr_txt);
		actionTypeAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		actionTypeSpiner.setAdapter(new NothingSelectedSpinnerAdapter1(
				actionTypeAdapter,
				R.layout.actiontype_spinner_nothing_selected, this));

		societyNameSpinner
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int pos, long arg3) {
						// TODO Auto-generated method stub
						societyName = String.valueOf(societyNameSpinner
								.getSelectedItem());

					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						// TODO Auto-generated method stub

					}
				});
		chooseyrSpiner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {
				// TODO Auto-generated method stub
				financialYr = String.valueOf(chooseyrSpiner.getSelectedItem());

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});
		monthsSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {
				// TODO Auto-generated method stub
				monthName = String.valueOf(monthsSpinner.getSelectedItem());

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});

		actionTypeSpiner
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						// TODO Auto-generated method stub
						actionType = String.valueOf(actionTypeSpiner
								.getSelectedItem());
						if (actionType == "Upload"
								|| actionType.equals("Upload")) {
							viewLay.setVisibility(View.GONE);
							action=true;
							fileNameTxt.setVisibility(View.GONE);
							doOpen();
						} else if (actionType == "View"
								|| actionType.equals("View")) {
							action=false;
							fileNameTxt.setVisibility(View.GONE);
						}
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						// TODO Auto-generated method stub

					}
				});
		submitTxt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				System.out.println(societyName + "!!!!!!!!!!!!!!!!!!!!!!");
				if (societyName == "" || societyName.equals("")
						|| societyName == "null" || societyName == null) {
					showSnack(SocietyBillManagement.this,
							"Select society!",
							"OK");
				} else if (financialYr == "" || financialYr.equals("")
						|| financialYr == null || financialYr == "null") {
					showSnack(SocietyBillManagement.this,
							"Select financial year!",
							"OK");
				} else if (monthName == "" || monthName.equals("")
						|| monthName == null || monthName == "null") {
					showSnack(SocietyBillManagement.this,
							"Select month!",
							"OK");
				} else if (actionType == "" || actionType.equals("")
						|| actionType == null || actionType == "null") {
					showSnack(SocietyBillManagement.this,
							"Select View/Upload!",
							"OK");
				} else {

					if (actionType == "Upload" || actionType.equals("Upload")) {
						if(selectedFile==null||selectedFile.toString()=="null"){
							showSnack(SocietyBillManagement.this,
									"Select a file to upload!",
									"OK");
						}else{
							new PostSocietyBill().execute();
						}


					} else if (actionType == "View"
							|| actionType.equals("View")) {
						System.out.println(actionType
								+ " ^^^^^^^^^^^^^^^^^^^^^^^^");
						new ViewBill().execute();

					}
				}

			}
		});
		totBilAmountLay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(SocietyBillManagement.this,
						ViewBillDetail.class);
				i.putExtra("TOT_DATA", totalDataArr.toString());
				startActivity(i);
			}
		});
		dueStatusLay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(SocietyBillManagement.this,
						ViewBillDetail.class);
				i.putExtra("TOT_DATA", dueStatusDataArr.toString());
				startActivity(i);
			}
		});
		paidStatusCountLay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(SocietyBillManagement.this,
						ViewBillDetail.class);
				i.putExtra("TOT_DATA", paidStatusDataArr.toString());
				startActivity(i);
			}
		});
		paidStatusCountLay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(SocietyBillManagement.this,
						ViewBillDetail.class);
				i.putExtra("TOT_DATA", paidStatusDataArr.toString());
				startActivity(i);
			}
		});
		confrmLay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(SocietyBillManagement.this,
						ViewBillDetail.class);
				i.putExtra("TOT_DATA", confirmedStatusDataArr.toString());
				startActivity(i);
			}
		});
	}

	private void doOpen() {
		Intent intent = new Intent(this, FilePicker.class);
		startActivityForResult(intent, REQUEST_PICK_FILE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == RESULT_OK) {

			switch (requestCode) {

			case REQUEST_PICK_FILE:

				if (data.hasExtra(FilePicker.EXTRA_FILE_PATH)) {

					selectedFile = new File(
							data.getStringExtra(FilePicker.EXTRA_FILE_PATH));
					showSnack(SocietyBillManagement.this,
							selectedFile.getPath(),
							"OK");
					fileNameTxt.setVisibility(View.VISIBLE);
					fileNameTxt.setText(selectedFile.getPath().toString());
					// get path
					// String path=selectedFile.getPath()
				}
				break;
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

	public class PostSocietyBill extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			pDialog.setVisibility(View.VISIBLE);
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try {
				jsonBill = SocietyBillPost.makeBillPost(
						userPref.getString("USERID", "NV"), societyName,
						monthName, financialYr, selectedFile, hostname);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void unused) {
			System.out
					.println(jsonBill
							+ " !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!      RESPONSE");
			if (jsonBill == null || jsonBill.toString() == "null"
					|| jsonBill.equals("null")) {
				showSnack(SocietyBillManagement.this,
						"Oops! Something went wrong. Please wait a moment!",
						"OK");
				pDialog.setVisibility(View.GONE);
				fileNameTxt.setVisibility(View.GONE);
			} else {
				showSnack(SocietyBillManagement.this,
						"Uploaded file!",
						"OK");
				pDialog.setVisibility(View.GONE);
				fileNameTxt.setVisibility(View.GONE);
			}

		}
	}

	public class ViewBill extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			pDialog.setVisibility(View.VISIBLE);
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try {

				jsonViewBill = SocietyBillPost.callBillView(
						userPref.getString("USERID", "NV"), societyName,
						monthName, financialYr, hostname);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void unused) {
			System.out
					.println(jsonViewBill
							+ " !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!      RESPONSE");

			if (jsonViewBill == null || jsonViewBill.toString() == "null"
					|| jsonViewBill.equals("null")) {
				showSnack(SocietyBillManagement.this,
						"Oops! Something went wrong. Please wait a moment!",
						"OK");
				pDialog.setVisibility(View.GONE);
			} else {
				try {
					JSONObject json = jsonViewBill
							.getJSONObject("society_bill_management_count");

					societyBillManagementDataJson = jsonViewBill
							.getJSONObject("society_bill_management_data");

					totalDataArr = societyBillManagementDataJson
							.getJSONArray("total_data");
					paidStatusDataArr = societyBillManagementDataJson
							.getJSONArray("paid_status_data");
					confirmedStatusDataArr = societyBillManagementDataJson
							.getJSONArray("confirmed_status_data");
					dueStatusDataArr = societyBillManagementDataJson
							.getJSONArray("due_status_data");

					comfrmedCountTxt
							.setText(json.getString("confirmed_status"));
					totBilAmountTxt.setText(json.getString("total_count"));
					dueStatusTxt.setText(json.getString("due_status_count"));
					paidStatusCountTxt.setText(json
							.getString("paid_status_count"));
					dueAmountTxt.setText(json.getString("due_amount"));
					confrmdAmount.setText(json.getString("confirmed_amount"));
					balnceAmountTxt.setText(json.getString("balanced_amount"));
					viewLay.setVisibility(View.VISIBLE);
					pDialog.setVisibility(View.GONE);
					fileNameTxt.setVisibility(View.GONE);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		System.out.println(action + " ACTION ^^^^^^^^^^^^^^^^6");
		if(action==true){

		}else{
			if (financialYr == "" || financialYr.equals("")) {

			} else {
				new ViewBill().execute();
			}
		}
	}
	void showSnack(SocietyBillManagement login, String stringMsg, String ok) {
		new SnackBar(SocietyBillManagement.this, stringMsg, ok, new OnClickListener() {

			@Override
			public void onClick(View v) {
			}
		}).show();
	}
}
