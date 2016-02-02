package mobile.gclife.activity;

import mobile.gclife.PojoGson.FlatDetailsPojo;
import mobile.gclife.PojoGson.MyApplication;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class FlatDetail extends ActionBarActivity {
	android.support.v7.app.ActionBar actionBar;
	TextView avenueNameTxt, societyNameTxt, buidlingNameTxt, flatNumtxt,
			loginDetailsTxt, flatDteailTxt, flatTypetxt, ownwerTypetxt,
			memTypeTxt, dateFrmTypetxt, avenueHeadTxt,
			socityHeader, buildingHeader, flatNumHeader, flatTypeHeader,
			ownerTypeHeader, memTypeHeader, relationSDateHeader;
	Typeface typefaceLight, typeMeduim;
	LinearLayout relationStartLay;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.flat_detail_layout);
		actionBar = getSupportActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setIcon(android.R.color.transparent);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("Flat details");
		actionBar.setBackgroundDrawable(new ColorDrawable(Color
				.parseColor(MyApplication.actiobarColor)));

		typefaceLight = Typeface.createFromAsset(getAssets(),
				"fonts/RobotoLight.ttf");
		typeMeduim = Typeface.createFromAsset(getAssets(),
				"fonts/RobotoMedium.ttf");
		avenueNameTxt = (TextView) findViewById(R.id.avenueNameTxt);
		societyNameTxt = (TextView) findViewById(R.id.societyTxt);
		buidlingNameTxt = (TextView) findViewById(R.id.buildingNumTxt);
		flatNumtxt = (TextView) findViewById(R.id.flatNumtxt);
		flatTypetxt = (TextView) findViewById(R.id.flatTypeTxt);
		memTypeTxt = (TextView) findViewById(R.id.memTypeTxt);
		ownwerTypetxt = (TextView) findViewById(R.id.ownerTypeTxt);
		dateFrmTypetxt = (TextView) findViewById(R.id.relationSDateTxt);
		relationStartLay=(LinearLayout)findViewById(R.id.relationStartLay);
		avenueHeadTxt = (TextView) findViewById(R.id.avenueHeadTxt);
		socityHeader = (TextView) findViewById(R.id.socityHeader);
		buildingHeader = (TextView) findViewById(R.id.buildingHeader);
		flatNumHeader = (TextView) findViewById(R.id.flatNumHeader);
		flatTypeHeader = (TextView) findViewById(R.id.flatTypeHeader);
		memTypeHeader = (TextView) findViewById(R.id.memTypeHeader);
		ownerTypeHeader = (TextView) findViewById(R.id.ownerTypeHeader);
		relationSDateHeader = (TextView) findViewById(R.id.relationSDateHeader);

		avenueNameTxt.setTypeface(typefaceLight);
		societyNameTxt.setTypeface(typefaceLight);
		buidlingNameTxt.setTypeface(typefaceLight);
		flatNumtxt.setTypeface(typefaceLight);
		ownwerTypetxt.setTypeface(typefaceLight);
		flatTypetxt.setTypeface(typefaceLight);
		memTypeTxt.setTypeface(typefaceLight);
		dateFrmTypetxt.setTypeface(typefaceLight);
		
		avenueHeadTxt.setTypeface(typeMeduim);
		socityHeader.setTypeface(typeMeduim);
		buildingHeader.setTypeface(typeMeduim);
		flatNumHeader.setTypeface(typeMeduim);
		ownerTypeHeader.setTypeface(typeMeduim);
		flatTypeHeader.setTypeface(typeMeduim);
		memTypeHeader.setTypeface(typeMeduim);
		relationSDateHeader.setTypeface(typeMeduim);

		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		String flatDetails = bundle.getString("DETAILS");
		Gson gson = new GsonBuilder().create();
		FlatDetailsPojo flats = gson.fromJson(flatDetails,
				FlatDetailsPojo.class);

		avenueNameTxt.setText(flats.getAvenue_name());
		societyNameTxt.setText(flats.getSocietyid());
		buidlingNameTxt.setText(flats.getBuildingid());
		flatNumtxt.setText(flats.getFlat_number());
		if(flats.getOwnertypeid()=="Licensee Owner"||flats.getOwnertypeid().equals("Licensee Owner")){
			relationStartLay.setVisibility(View.VISIBLE);
			dateFrmTypetxt.setText(flats.getTenurestart());
		}else{
			relationStartLay.setVisibility(View.GONE);
		}
		ownwerTypetxt.setText(flats.getOwnertypeid());
		flatTypetxt.setText(flats.getFlat_type());
		if(flats.getMember_type()=="Non_members"||flats.getMember_type().equals("Non_members")){
			memTypeTxt.setText("Non member");
		}else{
			memTypeTxt.setText(flats.getMember_type());
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
}
