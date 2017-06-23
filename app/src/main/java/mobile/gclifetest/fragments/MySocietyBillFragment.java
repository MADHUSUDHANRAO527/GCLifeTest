package mobile.gclifetest.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mobile.gclifetest.activity.HomeActivity;
import mobile.gclifetest.activity.R;
import mobile.gclifetest.http.SocietyBillPost;
import mobile.gclifetest.materialDesign.ProgressBarCircularIndeterminate;
import mobile.gclifetest.pojoGson.FlatDetailsPojo;
import mobile.gclifetest.pojoGson.UserDetailsPojo;
import mobile.gclifetest.utils.Constants;
import mobile.gclifetest.utils.ListViewUtils;
import mobile.gclifetest.utils.GclifeApplication;
import mobile.gclifetest.utils.NothingSelectedSpinnerAdapter1;

/**
 * Created by MRaoKorni on 8/26/2016.
 */
public class MySocietyBillFragment extends Fragment {
    Context context;
    Spinner societyNameSpinner, buildingSpinner, finaYrTypeSpinner;
    SharedPreferences userPref;
    String societyName, buildingName, flatNum, financialYr;
    UserDetailsPojo user;
    TextView totBilAmountTxt, paidAmountTxt, balnceAmountTxt, submitTxt;
    ProgressBarCircularIndeterminate pDialog;
    JSONObject jsonViewBill, jsonConfrm;
    ListView detailsListview;
    LinearLayout bill_sumryLay;
    String refNum = "", spnrVal, billId, billAmountPaid, paymentType;
    Dialog m_dialog;
    View v;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(
                R.layout.mysocietybill, container, false);
        context = getActivity();
        societyNameSpinner = (Spinner) v.findViewById(R.id.societySpin);
        buildingSpinner = (Spinner) v.findViewById(R.id.buildingSpin);
        finaYrTypeSpinner = (Spinner) v.findViewById(R.id.finaYrTypeSpinner);
        submitTxt = (TextView) v.findViewById(R.id.submitTxt);
        pDialog = (ProgressBarCircularIndeterminate) v.findViewById(R.id.progressBarCircularIndetermininate);
        totBilAmountTxt = (TextView) v.findViewById(R.id.totBillTxt);
        paidAmountTxt = (TextView) v.findViewById(R.id.paidTxt);
        balnceAmountTxt = (TextView) v.findViewById(R.id.balDueTxt);
        detailsListview = (ListView) v.findViewById(R.id.listViewBilldetail);
        bill_sumryLay = (LinearLayout) v.findViewById(R.id.bill_sumryLay);

        userPref = context.getSharedPreferences("USER", context.MODE_PRIVATE);

        Gson gson = new Gson();
        String jsonUser = userPref.getString("USER_DATA", "NV");
        user = gson.fromJson(jsonUser, UserDetailsPojo.class);

        // list flat details
        List<FlatDetailsPojo> flatsList = user
                .getGclife_registration_flatdetails();

        List<String> sociList = new ArrayList<String>();
        //  ArrayList<String> listSociety = new ArrayList<String>();
        final Map<String, ArrayList<String>> listSocietyBuildingFlats = new HashMap<>();
        // final Map<String, String> societyMap = new HashMap<String, String>();
        for (int i = 0; i < flatsList.size(); i++) {
            ArrayList<String> buildingFlatList = new ArrayList<>();
            FlatDetailsPojo flatsListt = user
                    .getGclife_registration_flatdetails().get(i);
            societyName = flatsListt.getSocietyid();
            buildingName = flatsListt.getBuildingid();
            flatNum = flatsListt.getFlat_number();
            if (flatsListt.getStatus().equals("Approve")) {
                if (!sociList.contains(societyName)) {
                    sociList.add(societyName);
                    // listSociety.add(societyName);
                    buildingFlatList.add(buildingName + " , " + flatNum);
                } else {
                    ArrayList<String> newArraylIst = listSocietyBuildingFlats.get(societyName);
                    newArraylIst.add(buildingName + " , " + flatNum);
                    buildingFlatList = newArraylIst;
                }

                listSocietyBuildingFlats.put(societyName, buildingFlatList);

                //  buildingListMap.add(societyMap);
            }
        }

        System.out.println(sociList + "  ---------------------------------");
        System.out.println(listSocietyBuildingFlats + "  ---------------------------------");
        //   System.out.println(buildingListMap + "******************************");
        // society spinner
        ArrayAdapter<String> societyAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.spinr_txt, sociList);

        societyAdapter
                .setDropDownViewResource(R.layout.spnr_item);

        societyNameSpinner.setAdapter(new NothingSelectedSpinnerAdapter1(
                societyAdapter, R.layout.society_spinner_nothing_selected,
                getActivity()));

        ArrayList<String> buildingList = new ArrayList<String>();
        buildingList.add("Building Number");
        ArrayAdapter<String> buildingAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.spinr_txt, buildingList);

        buildingAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        buildingSpinner.setAdapter(new NothingSelectedSpinnerAdapter1(
                buildingAdapter, R.layout.building_spinner_nothing_selected,
                getActivity()));

        ArrayAdapter<CharSequence> yrAdapter = ArrayAdapter.createFromResource(
                getActivity(), R.array.finaYr, R.layout.spinr_txt);
        yrAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        finaYrTypeSpinner.setAdapter(new NothingSelectedSpinnerAdapter1(
                yrAdapter, R.layout.year_spinner_nothing_selected, getActivity()));

        societyNameSpinner
                .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1,
                                               int pos, long arg3) {
                        // TODO Auto-generated method stub
                        societyName = String.valueOf(societyNameSpinner
                                .getSelectedItem());
                        ArrayList<String> buildingList = new ArrayList<String>();
                        if (societyName == null || societyName.equalsIgnoreCase("null")) {
                            System.out.println(buildingList);
                        } else {
                            if (!buildingList.contains(buildingName)) {
                                buildingList = listSocietyBuildingFlats.get(societyName);
                            }
                            System.out.println(buildingList);

                            // building spinner
                            ArrayAdapter<String> buildingAdapter = new ArrayAdapter<String>(getActivity(),
                                    R.layout.spinr_txt, buildingList);

                            buildingAdapter
                                    .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            buildingSpinner.setAdapter(new NothingSelectedSpinnerAdapter1(
                                    buildingAdapter, R.layout.building_spinner_nothing_selected,
                                    getActivity()));

                        }

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                        // TODO Auto-generated method stub

                    }
                });
        finaYrTypeSpinner
                .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

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
        buildingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int pos,
                                       long arg3) {
                // TODO Auto-generated method stub
                buildingName = String.valueOf(buildingSpinner.getSelectedItem());
                flatNum = buildingName.substring(buildingName.lastIndexOf(",") + 1);
                flatNum = flatNum.replace(" ", "");
                buildingName = buildingName.split(" , ")[0];
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

        submitTxt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                System.out.println(societyName + "!!!!!!!!!!!!!!!!!!!!!!");
                System.out.println(buildingName + "-" + flatNum + "-" + financialYr + " ############!!!*");
                if (societyName == "" || societyName.equals("")
                        || societyName == "null" || societyName == null) {
                    Constants.showSnack(v, "Select society!", "OK");
                } else if (buildingName == "" || buildingName.equals("")
                        || buildingName == null || buildingName == "null" || buildingName.equalsIgnoreCase("Building Number")) {
                    Constants.showSnack(v, "Select Building Name!", "OK");
                } else if (financialYr == "" || financialYr.equals("")
                        || financialYr == null || financialYr == "null") {
                    Constants.showSnack(v, "Select financial year!", "OK");
                } else {

                    new ViewMySociBill().execute();

                }

            }
        });
        return v;
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
                        buildingName, flatNum, financialYr, GclifeApplication.HOSTNAME);
                System.out.println(jsonViewBill + " !!!!!!!!!!!!!!!!!!!!*");
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
                Constants.showToast(context, R.string.went_wrong);
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
                    if (totDetailArr.length() > 0) {
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
                        if (getActivity() != null) {
                            ListDetailAdapter adapterFiles = new ListDetailAdapter(
                                    getActivity(), totDataList);
                            detailsListview.setAdapter(adapterFiles);
                            ListViewUtils.setDynamicHeight(detailsListview);
                            pDialog.setVisibility(View.GONE);
                            bill_sumryLay.setVisibility(View.VISIBLE);
                        }
                    } else {
                        ArrayList<HashMap<String, String>> emptyArr = new ArrayList<>();
                        Constants.showSnack(v, "No data found!", "");
                        ListDetailAdapter adapterFiles = new ListDetailAdapter(
                                getActivity(), emptyArr);
                        detailsListview.setAdapter(adapterFiles);
                        ListViewUtils.setDynamicHeight(detailsListview);
                        pDialog.setVisibility(View.GONE);
                        bill_sumryLay.setVisibility(View.GONE);
                    }


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

        public ListDetailAdapter(Context mContext,
                                 ArrayList<HashMap<String, String>> listArticles) {
            // TODO Auto-generated constructor stub
            context = mContext;
            listt = listArticles;
            inflator = (LayoutInflater) mContext
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
                holder.viewImg = (ImageView) convertView
                        .findViewById(R.id.view_img);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            String yr = listt
                    .get(positionn)
                    .get("YEAR")
                    .substring(2, 4);
            holder.flatNumTxt.setText(listt.get(positionn).get("MONTH"));
            holder.amountTxt.setText(listt.get(positionn).get("AMOUNT"));
            holder.statusTxt.setText(listt.get(positionn).get("STATUS"));
            if (listt.get(positionn).get("STATUS").equalsIgnoreCase("Confirmed")) {
                holder.statusImg.setVisibility(View.GONE);
                holder.viewImg.setVisibility(View.VISIBLE);
            } else {
                holder.statusImg.setVisibility(View.VISIBLE);
                holder.viewImg.setVisibility(View.GONE);
            }

            holder.statusImg.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub

                    popUp(holder, positionn, true);
                }
            });
            holder.viewImg.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub

                    popUp(holder, positionn, false);
                }
            });
            return convertView;
        }

        private void popUp(ViewHolder holder, final int positionn, boolean isEditable) {
            m_dialog = new Dialog(context);
            m_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            m_dialog.setContentView(R.layout.admin_bill_approval_popup);
            m_dialog.getWindow().getAttributes().windowAnimations = R.style.popup_login_dialog_animation;

            EditText amountEdit = (EditText) m_dialog
                    .findViewById(R.id.amountEdit);
            amountEdit.setEnabled(isEditable);

            final EditText referenseNumEdit = (EditText) m_dialog
                    .findViewById(R.id.narrationEdit);

            referenseNumEdit.setEnabled(isEditable);
            final EditText billAmountPaidEdit = (EditText) m_dialog
                    .findViewById(R.id.billAmountPaidEdit);
            final TextInputLayout amountEditt = (TextInputLayout) m_dialog
                    .findViewById(R.id.amounttEdit);

            billAmountPaidEdit.setEnabled(isEditable);
            amountEditt.setEnabled(isEditable);


            final Spinner actionTypeSpiner = (Spinner) m_dialog
                    .findViewById(R.id.actionTypeSpin);
            final Spinner paymentTypeSpin = (Spinner) m_dialog
                    .findViewById(R.id.paymentTypeSpin);

            actionTypeSpiner.setEnabled(isEditable);
            paymentTypeSpin.setEnabled(isEditable);

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

            submitTxt.setEnabled(isEditable);

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

            paymentTypeSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> arg0,
                                           View arg1, int arg2, long arg3) {
                    // TODO Auto-generated method stub
                    paymentType = String.valueOf(paymentTypeSpin
                            .getSelectedItem());
                    // paymentType=paymentType.replaceAll("", "%20");
                }

                @Override
                public void onNothingSelected(
                        AdapterView<?> arg0) {
                    // TODO Auto-generated method stub

                }
            });

            actionTypeSpiner
                    .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

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

            submitTxt.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    refNum = referenseNumEdit.getText().toString();
                    billAmountPaid = billAmountPaidEdit.getText().toString();
                    billId = listt.get(positionn).get("ID");
                    if (paymentType == null || paymentType == "null"
                            || paymentType == ""
                            || paymentType.equals("")) {
                        Constants.showSnack(v,
                                "Please enter payment mode!",
                                "OK");
                    } else {
                        new UpdateMyBillStatus().execute();
                    }
                }
            });
            m_dialog.show();
        }

        public class ViewHolder {
            public TextView flatNumTxt, amountTxt, statusTxt;
            ImageView statusImg, viewImg;
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
                jsonConfrm = SocietyBillPost.updateMyBill(GclifeApplication.HOSTNAME, billId,
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
                Constants.showToast(context, R.string.went_wrong);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                ((HomeActivity) context).onBackpressed();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        setHasOptionsMenu(true);
        ((HomeActivity) context).setHomeAsEnabled(true);
        ((HomeActivity) context).changeToolbarTitle(R.string.my_Society_bill);
    }

    @Override
    public void onStart() {
        super.onStart();
        FlurryAgent.onStartSession(getActivity().getApplicationContext(), Constants.flurryApiKey);
    }

    @Override
    public void onStop() {
        super.onStop();
        FlurryAgent.onEndSession(getActivity().getApplicationContext());
    }
}
