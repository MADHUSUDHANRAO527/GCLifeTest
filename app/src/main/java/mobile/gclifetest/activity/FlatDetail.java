package mobile.gclifetest.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import mobile.gclifetest.pojoGson.FlatDetailsPojo;
import mobile.gclifetest.pojoGson.UserDetailsPojo;
import mobile.gclifetest.utils.Constants;
import mobile.gclifetest.utils.GclifeApplication;

public class FlatDetail extends BaseActivity {
    TextView avenueNameTxt, societyNameTxt, buidlingNameTxt, flatNumtxt,
            flatTypetxt, ownwerTypetxt,
            memTypeTxt, relationShipWithOwner, avenueHeadTxt,
            socityHeader, buildingHeader, flatNumHeader, flatTypeHeader,
            ownerTypeHeader, memTypeHeader, relationShipWithOwnerHeader, flatStatusTxt, flat_status_header, reset_txt, licenceEndDateHeader, licenceEndDateTxt;
    Typeface typefaceLight, typeMeduim;
    LinearLayout relationLay, switchLay, licenceEndDateLay;
    Switch pSwitch;
    UserDetailsPojo user;
    String swicthState;
    SharedPreferences userPref;
    RelativeLayout snackLay;
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
        relationShipWithOwner = (TextView) findViewById(R.id.relation_ship_with_owner);
        reset_txt = (TextView) findViewById(R.id.reset_txt);
        relationLay = (LinearLayout) findViewById(R.id.relationLay);
        licenceEndDateLay = (LinearLayout) findViewById(R.id.licence_end_date_lay);
        switchLay = (LinearLayout) findViewById(R.id.switchLay);
        avenueHeadTxt = (TextView) findViewById(R.id.avenueHeadTxt);
        socityHeader = (TextView) findViewById(R.id.socityHeader);
        buildingHeader = (TextView) findViewById(R.id.buildingHeader);
        flatNumHeader = (TextView) findViewById(R.id.flatNumHeader);
        flatStatusTxt = (TextView) findViewById(R.id.flatStatusTxt);
        flat_status_header = (TextView) findViewById(R.id.flat_status_header);
        flatTypeHeader = (TextView) findViewById(R.id.flatTypeHeader);
        memTypeHeader = (TextView) findViewById(R.id.memTypeHeader);
        ownerTypeHeader = (TextView) findViewById(R.id.ownerTypeHeader);
        relationShipWithOwnerHeader = (TextView) findViewById(R.id.relation_ship_with_ownerheader);
        licenceEndDateHeader = (TextView) findViewById(R.id.licence_end_date);
        licenceEndDateTxt = (TextView) findViewById(R.id.licence_end_date_txt);
        pSwitch = (Switch) findViewById(R.id.privacySwitch);
        snackLay = (RelativeLayout) findViewById(R.id.snackLay);
        avenueNameTxt.setTypeface(typefaceLight);
        societyNameTxt.setTypeface(typefaceLight);
        buidlingNameTxt.setTypeface(typefaceLight);
        flatNumtxt.setTypeface(typefaceLight);
        ownwerTypetxt.setTypeface(typefaceLight);
        flatTypetxt.setTypeface(typefaceLight);
        memTypeTxt.setTypeface(typefaceLight);
        relationShipWithOwner.setTypeface(typefaceLight);
        flatStatusTxt.setTypeface(typefaceLight);
        licenceEndDateTxt.setTypeface(typefaceLight);

        reset_txt.setTypeface(typeMeduim);
        avenueHeadTxt.setTypeface(typeMeduim);
        socityHeader.setTypeface(typeMeduim);
        buildingHeader.setTypeface(typeMeduim);
        licenceEndDateHeader.setTypeface(typeMeduim);
        flatNumHeader.setTypeface(typeMeduim);
        ownerTypeHeader.setTypeface(typeMeduim);
        flatTypeHeader.setTypeface(typeMeduim);
        memTypeHeader.setTypeface(typeMeduim);
        relationShipWithOwnerHeader.setTypeface(typeMeduim);
        flat_status_header.setTypeface(typeMeduim);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String flatDetails = bundle.getString("DETAILS");
        Gson gson = new GsonBuilder().create();
        final FlatDetailsPojo flats = gson.fromJson(flatDetails,
                FlatDetailsPojo.class);

        userPref = getSharedPreferences("USER", MODE_PRIVATE);
        String jsonUser = userPref.getString("USER_DATA", "NV");
        user = gson.fromJson(jsonUser, UserDetailsPojo.class);

        avenueNameTxt.setText(flats.getAvenue_name());
        societyNameTxt.setText(flats.getSocietyid());
        buidlingNameTxt.setText(flats.getBuildingid());
        flatNumtxt.setText(flats.getFlat_number());
        if (flats.getRelationshipid() == null || flats.getRelationshipid().equals("") || flats.getRelationshipid().equals("null")) {
            relationLay.setVisibility(View.GONE);
        } else {
            relationLay.setVisibility(View.VISIBLE);
            relationShipWithOwner.setText(flats.getRelationshipid());
        }
     //   Log.d("END DATE", flats.getTenurestart());
        if (flats.getTenurestart() == null || flats.getTenurestart().equals("")||flats.getTenurestart().equals("2015-12-12")) {
            licenceEndDateLay.setVisibility(View.GONE);
        } else {
            licenceEndDateLay.setVisibility(View.VISIBLE);
            licenceEndDateTxt.setText(flats.getTenurestart());
        }
        ownwerTypetxt.setText(flats.getOwnertypeid());
        flatTypetxt.setText(flats.getFlat_type());
        if (flats.getMember_type() == "Non_members" || flats.getMember_type().equals("Non_members")) {
            memTypeTxt.setText("Non member");
        } else {
            memTypeTxt.setText(flats.getMember_type());
        }

        if (flats.getStatus().equals("Inactive")) {
            flatStatusTxt.setText("Approval Pending");
            pSwitch.setChecked(false);
        } else if (flats.getStatus().equalsIgnoreCase("Reject")) {
            flatStatusTxt.setText("Rejected / Suspended");
            pSwitch.setChecked(false);
        } else if (flats.getStatus().equalsIgnoreCase("Delete")) {
            flatStatusTxt.setText("Deleted");
            pSwitch.setChecked(false);
        } else {
            flatStatusTxt.setText("Approved");
            pSwitch.setChecked(true);
        }

        Log.d("EMAIL : ", user.getEmail());
        if (user.getEmail().equalsIgnoreCase("gclife@gmail.com")) {
            switchLay.setVisibility(View.GONE);
        } else {
            switchLay.setVisibility(View.GONE);
        }

        pSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    swicthState = "on";
                } else {
                    swicthState = "off";
                }
                activateUsers(flats.getId(), swicthState);
            }
        });
    }

    private void activateUsers(int userId, String swicthState) {
        String host = GclifeApplication.HOSTNAME + "reset_user.json?flat_id=" + userId + "&status=" + swicthState;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, host.replaceAll(" ", "%20"), (String) null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("SWITCH RESPONSE : ", response.toString());
                try {
                    if (response.getString("status").equals("Inactive")) {
                        flatStatusTxt.setText("Approval Pending");
                    } else if (response.getString("status").equalsIgnoreCase("Reject")) {
                        flatStatusTxt.setText("Rejected / Suspended");
                    } else if (response.getString("status").equalsIgnoreCase("Delete")) {
                        flatStatusTxt.setText("Deleted");
                    } else {
                        flatStatusTxt.setText("Approved");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                Log.d("Error = ", volleyError.toString());
                Constants.showSnack(snackLay,
                        "Oops! Something went wrong. Please check internet connection!",
                        "OK");
            }
        });
        GclifeApplication.queue.add(request);

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
