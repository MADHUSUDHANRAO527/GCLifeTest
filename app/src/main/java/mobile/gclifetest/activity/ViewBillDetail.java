package mobile.gclifetest.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.gc.materialdesign.widgets.SnackBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import mobile.gclifetest.materialDesign.ProgressBarCircularIndeterminate;
import mobile.gclifetest.utils.Constants;
import mobile.gclifetest.utils.MyApplication;
import mobile.gclifetest.utils.NothingSelectedSpinnerAdapter1;
import mobile.gclifetest.http.SocietyBillPost;

public class ViewBillDetail extends BaseActivity {
    JSONArray totDataArr;
    ListView listview;
    String narration = "", spnrVal, billId, billAmountPaid, paymentType;
    ProgressBarCircularIndeterminate pDialog;
    JSONObject jsonConfrm;
    Dialog m_dialog;
    ViewHolder holder;
    int pos, cash = 1, cheque = 2, dd = 3;
    EditText amountEdit, narrationEdit, billAmountPaidEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_bill_detail);
        listview = (ListView) findViewById(R.id.viewBillDetailListview);

        setUpActionBar("View Bill");
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        String totData = bundle.getString("TOT_DATA");
        try {
            totDataArr = new JSONArray(totData);
            ArrayList<HashMap<String, String>> totDataList = new ArrayList<HashMap<String, String>>();

            for (int i = 0; i < totDataArr.length(); i++) {
                HashMap<String, String> map = new HashMap<String, String>();
                JSONObject jsonData = totDataArr.getJSONObject(i);
                map.put("FLATID", jsonData.getString("flat_id") + " , " + jsonData.getString("building_master_id"));
                map.put("AMOUNT", jsonData.getString("bill_amt"));
                map.put("STATUS", jsonData.getString("confirmed_status"));
                map.put("ID", jsonData.getString("id"));
                map.put("PAYMENT_MODE", jsonData.getString("payment_mode"));
                if (jsonData.isNull("ref_no")) {
                    map.put("REF_NO", "");
                } else {
                    map.put("REF_NO", jsonData.getString("ref_no"));
                }

                if (jsonData.isNull("bill_amount_paid")) {
                    map.put("bill_amount_paid", "");
                } else {
                    map.put("bill_amount_paid", jsonData.getString("bill_amount_paid"));
                }

                totDataList.add(map);
            }
            ListTotDataAdapter adapterFiles = new ListTotDataAdapter(ViewBillDetail.this,
                    totDataList);
            listview.setAdapter(adapterFiles);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @SuppressLint("NewApi")
    public class ListTotDataAdapter extends BaseAdapter {
        ArrayList<HashMap<String, String>> listt;
        private LayoutInflater inflator;
        private Context context;

        public ListTotDataAdapter(Context projectActivity,
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

            if (convertView == null) {
                convertView = inflator.inflate(R.layout.view_bill_detail_row,
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

            holder.flatNumTxt.setText(listt.get(positionn).get("FLATID"));
            holder.amountTxt.setText(listt.get(positionn).get("AMOUNT"));


            if (listt.get(positionn).get("STATUS") == null
                    || listt.get(positionn).get("STATUS").equals("null")) {
                holder.statusTxt.setVisibility(View.VISIBLE);
                holder.statusTxt.setText("Confirm");
            } else {
                holder.statusTxt.setVisibility(View.VISIBLE);
                holder.statusTxt.setText(listt.get(positionn).get("STATUS"));
            }
            holder.statusImg.setTag(positionn);
            holder.statusImg.setOnClickListener(new OnClickListener() {

                                                    @Override
                                                    public void onClick(View v) {
                                                        // TODO Auto-generated method stub

                                                        pos = positionn;
                                                        int position = (Integer) v.getTag();
                                                        m_dialog = new Dialog(context);
                                                        m_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                                        m_dialog.setContentView(R.layout.admin_bill_approval_popup);
                                                        m_dialog.getWindow().getAttributes().windowAnimations = R.style.popup_login_dialog_animation;

                                                        amountEdit = (EditText) m_dialog
                                                                .findViewById(R.id.amountEdit);
                                                        final Spinner paymentTypeSpin = (Spinner) m_dialog
                                                                .findViewById(R.id.paymentTypeSpin);

                                                        narrationEdit = (EditText) m_dialog
                                                                .findViewById(R.id.narrationEdit);
                                                        final Spinner actionTypeSpiner = (Spinner) m_dialog
                                                                .findViewById(R.id.actionTypeSpin);
                                                        billAmountPaidEdit = (EditText) m_dialog
                                                                .findViewById(R.id.billAmountPaidEdit);
                                                        TextView submitTxt = (TextView) m_dialog
                                                                .findViewById(R.id.submitTxt);
                                                        pDialog = (ProgressBarCircularIndeterminate) m_dialog
                                                                .findViewById(R.id.progressBarCircularIndetermininate);

                                                        final ArrayAdapter<CharSequence> actionTypeAdapter = ArrayAdapter
                                                                .createFromResource(context,
                                                                        R.array.statusBillArray, R.layout.spinr_txt);
                                                        actionTypeAdapter
                                                                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                                                        actionTypeSpiner
                                                                .setAdapter(new NothingSelectedSpinnerAdapter1(
                                                                        actionTypeAdapter,
                                                                        R.layout.admin_bill_spnr_txt, context));
                                                        billAmountPaid = billAmountPaidEdit.getText().toString();
                                                        amountEdit.setText(listt.get(positionn).get("AMOUNT"));
                                                        if (listt.get(positionn).get("REF_NO") == null || listt.get(positionn).get("REF_NO").equalsIgnoreCase("null")) {
                                                            narrationEdit.setText("");
                                                        }

                                                        if (listt.get(positionn).get("PAYMENT_MODE") == null || listt.get(positionn).get("PAYMENT_MODE").equalsIgnoreCase("null")) {
                                                            billAmountPaidEdit.setText("");
                                                        } else {
                                                            narrationEdit.setText(listt.get(positionn).get("REF_NO"));
                                                            if (listt.get(positionn).get("bill_amount_paid") != null || !listt.get(positionn).get("bill_amount_paid").equalsIgnoreCase(null)) {
                                                                billAmountPaidEdit.setText(listt.get(positionn).get("bill_amount_paid"));

                                                            } else {
                                                                billAmountPaidEdit.setText("");

                                                            }
                                                        }

                                                        final ArrayAdapter<String> paymentTypeAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, Constants.paymentTypes);
                                                        paymentTypeAdapter
                                                                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                                                        paymentTypeSpin
                                                                .setAdapter(new NothingSelectedSpinnerAdapter1(
                                                                        paymentTypeAdapter,
                                                                        R.layout.payment_type_spnr_txt, context));


                                                        paymentTypeSpin.setSelection(paymentTypeAdapter.getPosition(listt.get(positionn).get("PAYMENT_MODE")) + 1);
                                                        actionTypeSpiner.setSelection(actionTypeAdapter.getPosition(listt.get(positionn).get("STATUS")) + 1);


                                                        paymentTypeSpin.setOnItemSelectedListener(new OnItemSelectedListener() {

                                                            @Override
                                                            public void onItemSelected(AdapterView<?> arg0,
                                                                                       View arg1, int arg2, long arg3) {
                                                                // TODO Auto-generated method stub
                                                                paymentType = String.valueOf(paymentTypeSpin
                                                                        .getSelectedItem());
                                                                paymentType = paymentType.replaceAll(" ", "%20");
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
                                                                        System.out.println(spnrVal
                                                                                + " !!!!!!!!!!!!!!!!!!!!!!!!!!! ");
                                                                    }

                                                                    @Override
                                                                    public void onNothingSelected(
                                                                            AdapterView<?> arg0) {
                                                                        // TODO Auto-generated method stub

                                                                    }
                                                                });
                                                      /*  if(paymentType!=null){
                                                            paymentTypeSpin.setSelection(paymentTypeAdapter.getPosition(paymentType) + 1);
                                                        }
                                                        if(spnrVal!=null){
                                                            actionTypeSpiner.setSelection(actionTypeAdapter.getPosition(spnrVal) + 1);
                                                        }
                                                        if(narration!=null){
                                                            narrationEdit.setText(narration);
                                                        }*/

                                                        submitTxt.setOnClickListener(new OnClickListener() {

                                                            @Override
                                                            public void onClick(View v) {
                                                                // TODO Auto-generated method stub
                                                                //paymentMode = paymentModeEdit.getText().toString();
                                                                narration = narrationEdit.getText().toString();
                                                                billAmountPaid = billAmountPaidEdit.getText().toString();
                                                                billId = listt.get(positionn).get("ID");

                                                                if (paymentType == null || paymentType == "null"
                                                                        || paymentType == ""
                                                                        || paymentType.equals("")) {
                                                                    showSnack(ViewBillDetail.this,
                                                                            "Please enter payment mode!", "OK");
                                                                } else if (spnrVal == null || spnrVal == "null"
                                                                        || spnrVal == "" || spnrVal.equals("")) {
                                                                    showSnack(ViewBillDetail.this,
                                                                            "Please select paid/not paid!", "OK");
                                                                } else {
                                                                    billAmountPaidEdit.setText(billAmountPaidEdit.getText().toString());
                                                                    narration = narration.replaceAll(" ", "%20");

                                                                    new UpdateBillStatus().execute();
                                                                }

                                                            }
                                                        });

                                                        m_dialog.show();
                                                    }
                                                }

            );

            return convertView;
        }

    }

    public class UpdateBillStatus extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            pDialog.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub
            try {
                jsonConfrm = SocietyBillPost.updateStatusBill(
                        MyApplication.HOSTNAME, billId, paymentType, narration,
                        spnrVal, billAmountPaidEdit.getText().toString());
                System.out.println(billAmountPaid + " !!!!!!!!!!!!!!!!!!!!!!!! " + jsonConfrm);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            if (jsonConfrm != null) {

                // holder.statusImg.setVisibility(View.GONE);
                // holder.statusTxt.setVisibility(View.VISIBLE);
                try {

                    System.out.println(jsonConfrm.getString("status")
                            + " !!!!!!!!!!!!!!!!!!!!!!! ");

                    // holder.statusTxt.setText(jsonConfrm.getString("status"));

                    ((JSONObject) totDataArr.get(pos)).put("status",
                            jsonConfrm.get("confirmed_status"));
                    ((JSONObject) totDataArr.get(pos)).put("bill_amount_paid",
                            billAmountPaidEdit.getText().toString());
                    System.out.println(totDataArr + " !!*******************!");
                    /*
                     * Intent intent = getIntent(); finish();
					 * startActivity(intent);
					 */
                    ArrayList<HashMap<String, String>> totDataList = new ArrayList<HashMap<String, String>>();

                    for (int i = 0; i < totDataArr.length(); i++) {
                        HashMap<String, String> map = new HashMap<String, String>();
                        JSONObject jsonData = totDataArr.getJSONObject(i);
                        map.put("FLATID", jsonData.getString("flat_id") + " , " + jsonData.getString("building_master_id"));
                        map.put("AMOUNT", jsonData.getString("bill_amt"));
                        map.put("STATUS", jsonData.getString("status"));
                        map.put("ID", jsonData.getString("id"));
                        map.put("PAYMENT_MODE", jsonData.getString("payment_mode"));
                        map.put("REF_NO", jsonData.getString("ref_no"));
                        map.put("bill_amount_paid", jsonData.getString("bill_amount_paid"));
                        totDataList.add(map);
                    }
                    ListTotDataAdapter adapterFiles = new ListTotDataAdapter(ViewBillDetail.this,
                            totDataList);
                    listview.setAdapter(adapterFiles);
                    adapterFiles.notifyDataSetChanged();
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                pDialog.setVisibility(View.GONE);
                m_dialog.dismiss();

            } else {
                pDialog.setVisibility(View.GONE);
                m_dialog.dismiss();
                showSnack(ViewBillDetail.this,
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

    public class ViewHolder {
        public TextView flatNumTxt, amountTxt, statusTxt;
        ImageView statusImg;
    }

    void showSnack(ViewBillDetail login, String stringMsg, String ok) {
        new SnackBar(ViewBillDetail.this, stringMsg, ok, new OnClickListener() {

            @Override
            public void onClick(View v) {
            }
        }).show();
    }
}
