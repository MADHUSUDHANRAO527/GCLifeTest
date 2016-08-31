package mobile.gclifetest.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.gc.materialdesign.widgets.SnackBar;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mobile.gclifetest.materialDesign.ProgressBarCircularIndeterminate;
import mobile.gclifetest.pojoGson.FlatDetailsPojo;
import mobile.gclifetest.pojoGson.UserDetailsPojo;
import mobile.gclifetest.utils.ListViewUtils;
import mobile.gclifetest.utils.MyApplication;
import mobile.gclifetest.utils.NothingSelectedSpinnerAdapter1;
import mobile.gclifetest.http.SocietyBillPost;

public class MySocietyBill extends BaseActivity {
    Spinner societyNameSpinner, buildingSpinner, finaYrTypeSpinner;
    SharedPreferences userPref;
    String societyName, buildingName, financialYr;
    UserDetailsPojo user;
    TextView totBilAmountTxt, paidAmountTxt, balnceAmountTxt, submitTxt;
    ProgressBarCircularIndeterminate pDialog;
    JSONObject jsonViewBill, jsonConfrm;
    ListView detailsListview;
    LinearLayout bill_sumryLay;
    String refNum = "", spnrVal, billId, billAmountPaid, paymentType;
    Dialog m_dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mysocietybill);
        societyNameSpinner = (Spinner) findViewById(R.id.societySpin);
        buildingSpinner = (Spinner) findViewById(R.id.buildingSpin);
        finaYrTypeSpinner = (Spinner) findViewById(R.id.finaYrTypeSpinner);
        submitTxt = (TextView) findViewById(R.id.submitTxt);
        pDialog = (ProgressBarCircularIndeterminate) findViewById(R.id.progressBarCircularIndetermininate);
        totBilAmountTxt = (TextView) findViewById(R.id.totBillTxt);
        paidAmountTxt = (TextView) findViewById(R.id.paidTxt);
        balnceAmountTxt = (TextView) findViewById(R.id.balDueTxt);
        detailsListview = (ListView) findViewById(R.id.listViewBilldetail);
        bill_sumryLay = (LinearLayout) findViewById(R.id.bill_sumryLay);


        setUpActionBar("My Society Bill");

        userPref = getSharedPreferences("USER", MODE_PRIVATE);

        Gson gson = new Gson();
        String jsonUser = userPref.getString("USER_DATA", "NV");
        user = gson.fromJson(jsonUser, UserDetailsPojo.class);

        // list flat details
        List<FlatDetailsPojo> flatsList = user
                .getGclife_registration_flatdetails();

        List<String> sociList = new ArrayList<String>();
        List<String> buildingList = new ArrayList<String>();

        for (int i = 0; i < flatsList.size(); i++) {
            FlatDetailsPojo flatsListt = user
                    .getGclife_registration_flatdetails().get(i);
            societyName = flatsListt.getSocietyid();
            if (!sociList.contains(societyName)) {
                sociList.add(societyName);
            }
        }
        for (int i = 0; i < flatsList.size(); i++) {
            FlatDetailsPojo flatsListt = user
                    .getGclife_registration_flatdetails().get(i);
            buildingName = flatsListt.getBuildingid();
            if (!buildingList.contains(buildingName)) {
                buildingList.add(buildingName + " , " + flatsListt.getFlat_number());
            }
        }

        System.out.println(sociList + "  ---------------------------------");
        // society spinner
        ArrayAdapter<String> societyAdapter = new ArrayAdapter<String>(this,
                R.layout.spinr_txt, sociList);

        societyAdapter
                .setDropDownViewResource(R.layout.spnr_item);

        societyNameSpinner.setAdapter(new NothingSelectedSpinnerAdapter1(
                societyAdapter, R.layout.society_spinner_nothing_selected,
                MySocietyBill.this));
        // building spinner
        ArrayAdapter<String> buildingAdapter = new ArrayAdapter<String>(this,
                R.layout.spinr_txt, buildingList);

        buildingAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        buildingSpinner.setAdapter(new NothingSelectedSpinnerAdapter1(
                buildingAdapter, R.layout.building_spinner_nothing_selected,
                MySocietyBill.this));

        ArrayAdapter<CharSequence> yrAdapter = ArrayAdapter.createFromResource(
                this, R.array.finaYr, R.layout.spinr_txt);
        yrAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        finaYrTypeSpinner.setAdapter(new NothingSelectedSpinnerAdapter1(
                yrAdapter, R.layout.year_spinner_nothing_selected, this));

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
        finaYrTypeSpinner
                .setOnItemSelectedListener(new OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1,
                                               int pos, long arg3) {
                        // TODO Auto-generated method stub
                        financialYr = String.valueOf(finaYrTypeSpinner
                                .getSelectedItem());

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                        // TODO Auto-generated method stub

                    }
                });
        buildingSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int pos,
                                       long arg3) {
                // TODO Auto-generated method stub
                buildingName = String.valueOf(buildingSpinner.getSelectedItem());
                buildingName = buildingName.split(" , ")[0];
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
                    showSnack(MySocietyBill.this,
                            "Select society!",
                            "OK");
                } else if (buildingName == "" || buildingName.equals("")
                        || buildingName == null || buildingName == "null") {
                    showSnack(MySocietyBill.this,
                            "Select Building Name!",
                            "OK");
                } else if (financialYr == "" || financialYr.equals("")
                        || financialYr == null || financialYr == "null") {
                    showSnack(MySocietyBill.this,
                            "Select financial year!",
                            "OK");

                } else {

                    new ViewMySociBill().execute();

                }

            }
        });

    }

    public class ViewMySociBill extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            pDialog.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub
            try {

                jsonViewBill = SocietyBillPost.callSociMyBill(
                        userPref.getString("USERID", "NV"), societyName,
                        buildingName, financialYr, MyApplication.HOSTNAME);
                System.out
                        .println(jsonViewBill + " ******************************");
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            if (jsonViewBill == null || jsonViewBill.toString() == "null"
                    || jsonViewBill.equals("null")) {
                showSnack(MySocietyBill.this,
                        "Oops! Something went wrong. Please wait a moment!",
                        "OK");
                pDialog.setVisibility(View.GONE);
            } else {
                try {
                    JSONObject jsonSumry = jsonViewBill
                            .getJSONObject("bill_summary");

                    totBilAmountTxt.setText(jsonSumry.getString("total_amt"));
                    paidAmountTxt.setText(jsonSumry.getString("paid_amt"));
                    balnceAmountTxt.setText(jsonSumry.getString("due_amt"));

                    JSONArray totDetailArr = jsonViewBill
                            .getJSONArray("bill_detail");

                    ArrayList<HashMap<String, String>> totDataList = new ArrayList<HashMap<String, String>>();

                    for (int i = 0; i < totDetailArr.length(); i++) {
                        HashMap<String, String> map = new HashMap<String, String>();
                        JSONObject jsonData = totDetailArr.getJSONObject(i);
                        map.put("ID", jsonData.getString("id"));
                        map.put("MONTH", jsonData.getString("month"));
                        map.put("YEAR", jsonData.getString("fy"));
                        map.put("PAYMENT_MODE", jsonData.getString("payment_mode"));
                        if (jsonData.isNull("ref_no")) {
                            map.put("REF_NO", "");
                        } else {
                            map.put("REF_NO", jsonData.getString("ref_no"));
                        }


                        map.put("bill_amount_paid", jsonData.getString("bill_amount_paid"));
                        map.put("AMOUNT", jsonData.getString("bill_amt"));
                        map.put("STATUS", jsonData.getString("status"));
                        totDataList.add(map);
                    }
                    ListDetailAdapter adapterFiles = new ListDetailAdapter(
                            MySocietyBill.this, totDataList);
                    detailsListview.setAdapter(adapterFiles);
                    ListViewUtils.setDynamicHeight(detailsListview);
                    pDialog.setVisibility(View.GONE);
                    bill_sumryLay.setVisibility(View.VISIBLE);

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        }
    }

    @SuppressLint("NewApi")
    public class ListDetailAdapter extends BaseAdapter {
        ArrayList<HashMap<String, String>> listt;
        private LayoutInflater inflator;
        private Context context;

        public ListDetailAdapter(Context projectActivity,
                                 ArrayList<HashMap<String, String>> listArticles) {
            // TODO Auto-generated constructor stub
            this.context = projectActivity;
            this.listt = listArticles;
            inflator = (LayoutInflater) projectActivity
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
        public View getView(final int positionn, View convertView,
                            ViewGroup parent) {
            // TODO Auto-generated method stub
            final ViewHolder holder;
            if (convertView == null) {
                convertView = inflator.inflate(R.layout.myview_bill_detail_row,
                        null);
                holder = new ViewHolder();
                holder.flatNumTxt = (TextView) convertView
                        .findViewById(R.id.flatNumTxt);
                holder.amountTxt = (TextView) convertView
                        .findViewById(R.id.amountTxt);
                holder.statusTxt = (TextView) convertView
                        .findViewById(R.id.statusTxt);
                holder.statusImg = (ImageView) convertView
                        .findViewById(R.id.statusImg);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            String yr = listt
                    .get(positionn)
                    .get("YEAR")
                    .substring(2, 4);
            holder.flatNumTxt.setText(listt.get(positionn).get("MONTH") + ","
                    + yr);
            holder.amountTxt.setText(listt.get(positionn).get("AMOUNT"));
            holder.statusTxt.setText(listt.get(positionn).get("STATUS"));

            holder.statusImg.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    m_dialog = new Dialog(context);
                    m_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    m_dialog.setContentView(R.layout.admin_bill_approval_popup);
                    m_dialog.getWindow().getAttributes().windowAnimations = R.style.popup_login_dialog_animation;

                    EditText amountEdit = (EditText) m_dialog
                            .findViewById(R.id.amountEdit);

                    final EditText referenseNumEdit = (EditText) m_dialog
                            .findViewById(R.id.narrationEdit);
                    final EditText billAmountPaidEdit = (EditText) m_dialog
                            .findViewById(R.id.billAmountPaidEdit);
                    final com.wrapp.floatlabelededittext.FloatLabeledEditText amountEditt = (com.wrapp.floatlabelededittext.FloatLabeledEditText) m_dialog
                            .findViewById(R.id.amounttEdit);

                    final Spinner actionTypeSpiner = (Spinner) m_dialog
                            .findViewById(R.id.actionTypeSpin);
                    final Spinner paymentTypeSpin = (Spinner) m_dialog
                            .findViewById(R.id.paymentTypeSpin);

                    TextView submitTxt = (TextView) m_dialog
                            .findViewById(R.id.submitTxt);
                    pDialog = (ProgressBarCircularIndeterminate) m_dialog
                            .findViewById(R.id.progressBarCircularIndetermininate);

                    holder.line = (RelativeLayout) m_dialog
                            .findViewById(R.id.line);
                    holder.line1 = (RelativeLayout) m_dialog
                            .findViewById(R.id.line2);

                    holder.line.setVisibility(View.GONE);
                    holder.line1.setVisibility(View.GONE);

                    amountEdit.setVisibility(View.GONE);
                    actionTypeSpiner.setVisibility(View.GONE);
                    amountEditt.setVisibility(View.GONE);
                    if (listt.get(positionn).get("REF_NO") == null || listt.get(positionn).get("REF_NO").equalsIgnoreCase("null")) {
                        referenseNumEdit.setText("");
                    } else {
                        referenseNumEdit.setText(listt.get(positionn).get("REF_NO"));
                    }

                    if (listt.get(positionn).get("bill_amount_paid") == "" || listt.get(positionn).get("bill_amount_paid").equals("")
                            || listt.get(positionn).get("bill_amount_paid") == null || listt.get(positionn).get("bill_amount_paid").equalsIgnoreCase("null")) {
                        billAmountPaidEdit.setText(listt.get(positionn).get("AMOUNT"));
                    } else {
                        billAmountPaidEdit.setText(listt.get(positionn).get("bill_amount_paid"));
                    }

                    ArrayAdapter<CharSequence> paymentTypeAdapter = ArrayAdapter
                            .createFromResource(context,
                                    R.array.paymentTypeArray, R.layout.spinr_txt);
                    paymentTypeAdapter
                            .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    paymentTypeSpin
                            .setAdapter(new NothingSelectedSpinnerAdapter1(
                                    paymentTypeAdapter,
                                    R.layout.payment_type_spnr_txt, context));


                    ArrayAdapter<CharSequence> actionTypeAdapter = ArrayAdapter
                            .createFromResource(context,
                                    R.array.statusBillArray, R.layout.spinr_txt);
                    actionTypeAdapter
                            .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    actionTypeSpiner
                            .setAdapter(new NothingSelectedSpinnerAdapter1(
                                    actionTypeAdapter,
                                    R.layout.admin_bill_spnr_txt, context));
                    paymentTypeSpin.setSelection(paymentTypeAdapter.getPosition(listt.get(positionn).get("PAYMENT_MODE")) + 1);

                    amountEdit.setText(listt.get(positionn).get("AMOUNT"));

                    paymentTypeSpin.setOnItemSelectedListener(new OnItemSelectedListener() {

                        @Override
                        public void onItemSelected(AdapterView<?> arg0,
                                                   View arg1, int arg2, long arg3) {
                            // TODO Auto-generated method stub
                            paymentType = String.valueOf(paymentTypeSpin
                                    .getSelectedItem());

                        }

                        @Override
                        public void onNothingSelected(
                                AdapterView<?> arg0) {
                            // TODO Auto-generated method stub

                        }
                    });

                    actionTypeSpiner
                            .setOnItemSelectedListener(new OnItemSelectedListener() {

                                @Override
                                public void onItemSelected(AdapterView<?> arg0,
                                                           View arg1, int arg2, long arg3) {
                                    // TODO Auto-generated method stub
                                    spnrVal = String.valueOf(actionTypeSpiner
                                            .getSelectedItem());

                                }

                                @Override
                                public void onNothingSelected(
                                        AdapterView<?> arg0) {
                                    // TODO Auto-generated method stub

                                }
                            });

                    submitTxt.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            refNum = referenseNumEdit.getText().toString();
                            billAmountPaid = billAmountPaidEdit.getText().toString();
                            billId = listt.get(positionn).get("ID");
                            if (paymentType == null || paymentType == "null"
                                    || paymentType == ""
                                    || paymentType.equals("")) {
                                showSnack(MySocietyBill.this,
                                        "Please enter payment mode!",
                                        "OK");
                            } /*else if (refNum == null || refNum == "null"
                                    || refNum == "" || refNum.equals("")) {
                                showSnack(MySocietyBill.this,
                                        "Please enter reference number!",
                                        "OK");
                            }*/ else {
                                new UpdateMyBillStatus().execute();
                            }


                        }
                    });
                    m_dialog.show();
                }
            });

            return convertView;
        }

        public class ViewHolder {
            public TextView flatNumTxt, amountTxt, statusTxt;
            ImageView statusImg;
            RelativeLayout line, line1;
        }
    }

    public class UpdateMyBillStatus extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            pDialog.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub
            try {
                jsonConfrm = SocietyBillPost.updateMyBill(MyApplication.HOSTNAME, billId,
                        paymentType, refNum, billAmountPaid);
                new ViewMySociBill().execute();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            if (jsonConfrm != null) {
                pDialog.setVisibility(View.GONE);
                m_dialog.dismiss();
            } else {
                pDialog.setVisibility(View.GONE);
                m_dialog.dismiss();
                showSnack(MySocietyBill.this,
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

    void showSnack(MySocietyBill login, String stringMsg, String ok) {
        new SnackBar(MySocietyBill.this, stringMsg, ok, new OnClickListener() {

            @Override
            public void onClick(View v) {
            }
        }).show();
    }
}
