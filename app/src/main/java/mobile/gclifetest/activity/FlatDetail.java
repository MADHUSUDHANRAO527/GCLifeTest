package mobile.gclifetest.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import mobile.gclifetest.PojoGson.FlatDetailsPojo;

public class FlatDetail extends BaseActivity {
    TextView avenueNameTxt, societyNameTxt, buidlingNameTxt, flatNumtxt,
            loginDetailsTxt, flatDteailTxt, flatTypetxt, ownwerTypetxt,
            memTypeTxt, dateFrmTypetxt, avenueHeadTxt,
            socityHeader, buildingHeader, flatNumHeader, flatTypeHeader,
            ownerTypeHeader, memTypeHeader, relationSDateHeader, flatStatusTxt, flat_status_header;
    Typeface typefaceLight, typeMeduim;
    LinearLayout relationStartLay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.flat_detail_layout);
        setUpActionBar("Flat details");

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
        relationStartLay = (LinearLayout) findViewById(R.id.relationStartLay);
        avenueHeadTxt = (TextView) findViewById(R.id.avenueHeadTxt);
        socityHeader = (TextView) findViewById(R.id.socityHeader);
        buildingHeader = (TextView) findViewById(R.id.buildingHeader);
        flatNumHeader = (TextView) findViewById(R.id.flatNumHeader);
        flatStatusTxt = (TextView) findViewById(R.id.flatStatusTxt);
        flat_status_header = (TextView) findViewById(R.id.flat_status_header);
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
        flatStatusTxt.setTypeface(typefaceLight);

        avenueHeadTxt.setTypeface(typeMeduim);
        socityHeader.setTypeface(typeMeduim);
        buildingHeader.setTypeface(typeMeduim);
        flatNumHeader.setTypeface(typeMeduim);
        ownerTypeHeader.setTypeface(typeMeduim);
        flatTypeHeader.setTypeface(typeMeduim);
        memTypeHeader.setTypeface(typeMeduim);
        relationSDateHeader.setTypeface(typeMeduim);
        flat_status_header.setTypeface(typeMeduim);

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
        if (flats.getOwnertypeid() == "Licensee Owner" || flats.getOwnertypeid().equals("Licensee Owner")) {
            relationStartLay.setVisibility(View.VISIBLE);
            dateFrmTypetxt.setText(flats.getTenurestart());
        } else {
            relationStartLay.setVisibility(View.GONE);
        }
        ownwerTypetxt.setText(flats.getOwnertypeid());
        flatTypetxt.setText(flats.getFlat_type());
        if (flats.getMember_type() == "Non_members" || flats.getMember_type().equals("Non_members")) {
            memTypeTxt.setText("Non member");
        } else {
            memTypeTxt.setText(flats.getMember_type());
        }
        if (flats.getStatus() == "Inactive" || flats.getStatus().equals("Inactive")) {
            flatStatusTxt.setText("Approval Pending");
        } else if (flats.getStatus().equalsIgnoreCase("Reject")) {
            flatStatusTxt.setText("Rejected / Suspended");
        } else if (flats.getStatus().equalsIgnoreCase("Delete")) {
            flatStatusTxt.setText("Deleted");
        } else {
            flatStatusTxt.setText("Approved");
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
