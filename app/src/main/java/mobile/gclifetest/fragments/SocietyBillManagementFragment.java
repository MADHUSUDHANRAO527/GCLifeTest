package mobile.gclifetest.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import mobile.gclifetest.activity.HomeActivity;
import mobile.gclifetest.activity.R;
import mobile.gclifetest.event.AddIdeasEvent;
import mobile.gclifetest.http.SocietyBillPost;
import mobile.gclifetest.materialDesign.ProgressBarCircularIndeterminate;
import mobile.gclifetest.pojoGson.FlatDetailsPojo;
import mobile.gclifetest.pojoGson.UserDetailsPojo;
import mobile.gclifetest.utils.Constants;
import mobile.gclifetest.utils.FilePicker;
import mobile.gclifetest.utils.MyApplication;
import mobile.gclifetest.utils.NothingSelectedSpinnerAdapter1;

/**
 * Created by MRaoKorni on 8/26/2016.
 */
public class SocietyBillManagementFragment extends Fragment {
    Spinner societyNameSpinner, chooseyrSpiner, monthsSpinner,
            actionTypeSpiner;
    String actionType, fileSelectedPath;
    private static final int REQUEST_PICK_FILE = 1;
    LinearLayout viewLay, totBilAmountLay, dueStatusLay, paidStatusCountLay,
            confrmLay;
    String societyName = "", financialYr = "", monthName = "";
    JSONArray jsonResultArry;
    JSONObject jsonBill, jsonViewBill;
    SharedPreferences userPref;
    UserDetailsPojo user;
    List<FlatDetailsPojo> flatsList = new ArrayList<FlatDetailsPojo>();
    TextView submitTxt, totBilAmountTxt, dueStatusTxt, paidStatusCountTxt,
            dueAmountTxt, confrmdAmount, balnceAmountTxt, comfrmedCountTxt, fileNameTxt;
    private File selectedFile;
    ProgressBarCircularIndeterminate pDialog;
    JSONObject societyBillManagementDataJson;
    JSONArray totalDataArr, paidStatusDataArr, confirmedStatusDataArr,
            dueStatusDataArr;
    boolean action = false;
    Context context;
    View v;
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(
                R.layout.societybillmanagement, container, false);
        context = getActivity();
        societyNameSpinner = (Spinner) v.findViewById(R.id.societySpin);
        chooseyrSpiner = (Spinner) v.findViewById(R.id.yrSpin);
        monthsSpinner = (Spinner) v.findViewById(R.id.monthSpin);
        actionTypeSpiner = (Spinner) v.findViewById(R.id.actionTypeSpin);
        viewLay = (LinearLayout) v.findViewById(R.id.viewLay);
        totBilAmountLay = (LinearLayout) v.findViewById(R.id.totBilAmountLay);
        dueStatusLay = (LinearLayout) v.findViewById(R.id.dueStatusLay);
        paidStatusCountLay = (LinearLayout) v.findViewById(R.id.paidStatusCountLay);
        confrmLay = (LinearLayout) v.findViewById(R.id.confrmLay);
        submitTxt = (TextView) v.findViewById(R.id.submitTxt);

        totBilAmountTxt = (TextView) v.findViewById(R.id.totBilAmountTxt);
        dueStatusTxt = (TextView) v.findViewById(R.id.dueStatusTxt);
        comfrmedCountTxt = (TextView) v.findViewById(R.id.comfrmedCountTxt);
        paidStatusCountTxt = (TextView) v.findViewById(R.id.paidStatusCountTxt);
        dueAmountTxt = (TextView) v.findViewById(R.id.dueAmountTxt);
        confrmdAmount = (TextView) v.findViewById(R.id.confrmdAmount);
        balnceAmountTxt = (TextView) v.findViewById(R.id.balnceAmountTxt);
        fileNameTxt = (TextView) v.findViewById(R.id.fileName);
        pDialog = (ProgressBarCircularIndeterminate) v.findViewById(R.id.progressBarCircularIndetermininate);

        userPref = context.getSharedPreferences("USER", context.MODE_PRIVATE);

        Gson gson = new Gson();
        String jsonUser = userPref.getString("USER_DATA", "NV");
        user = gson.fromJson(jsonUser, UserDetailsPojo.class);

        // list flat details
        List<FlatDetailsPojo> flatsList = user
                .getGclife_registration_flatdetails();

        System.out
                .println(flatsList.size() + " ******************************");

        final List<String> sociList = new ArrayList<String>();
        for (int i = 0; i < flatsList.size(); i++) {
            FlatDetailsPojo flatsListt = user
                    .getGclife_registration_flatdetails().get(i);
            societyName = flatsListt.getSocietyid();
            System.out.println(societyName + "!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            if (!flatsListt.getMember_type().equals("Non_members") &&
                    !flatsListt.getMember_type().equals("Member")) {
                if (flatsListt.getStatus().equals("Approve"))
                    if (!sociList.contains(societyName))
                        sociList.add(societyName);
            }
        }

        System.out.println(sociList + "  ---------------------------------");

        ArrayAdapter<String> societyAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.spinr_txt, sociList);

        societyAdapter
                .setDropDownViewResource(R.layout.spnr_item);

        societyNameSpinner.setAdapter(new NothingSelectedSpinnerAdapter1(
                societyAdapter, R.layout.society_spinner_nothing_selected,
                getActivity()));

        ArrayAdapter<CharSequence> yrAdapter = ArrayAdapter.createFromResource(
                getActivity(), R.array.finaYr, R.layout.spinr_txt);
        yrAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        chooseyrSpiner.setAdapter(new NothingSelectedSpinnerAdapter1(yrAdapter,
                R.layout.year_spinner_nothing_selected, getActivity()));

        ArrayAdapter<CharSequence> monthsAdapter = ArrayAdapter
                .createFromResource(getActivity(), R.array.monthsArr, R.layout.spinr_txt);
        monthsAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        monthsSpinner.setAdapter(new NothingSelectedSpinnerAdapter1(
                monthsAdapter, R.layout.month_spinner_nothing_selected, getActivity()));

        ArrayAdapter<CharSequence> actionTypeAdapter = ArrayAdapter
                .createFromResource(getActivity(), R.array.actionTypeArray,
                        R.layout.spinr_txt);
        actionTypeAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        actionTypeSpiner.setAdapter(new NothingSelectedSpinnerAdapter1(
                actionTypeAdapter,
                R.layout.actiontype_spinner_nothing_selected, getActivity()));

        societyNameSpinner
                .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

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
        chooseyrSpiner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

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
        monthsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

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
                .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1,
                                               int arg2, long arg3) {
                        // TODO Auto-generated method stub
                        actionType = String.valueOf(actionTypeSpiner
                                .getSelectedItem());
                        if (actionType == "Upload"
                                || actionType.equals("Upload")) {
                            viewLay.setVisibility(View.GONE);
                            action = true;
                            fileNameTxt.setVisibility(View.GONE);
                            doOpen();
                        } else if (actionType == "View"
                                || actionType.equals("View")) {
                            action = false;
                            fileNameTxt.setVisibility(View.GONE);
                            submitTxt.setEnabled(true);
                        }
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
                if (societyName == "" || societyName.equals("")
                        || societyName == "null" || societyName == null || sociList.isEmpty() || sociList.size() == 0) {
                    Constants.showSnack(v,
                            "Select society!",
                            "OK");
                } else if (financialYr == "" || financialYr.equals("")
                        || financialYr == null || financialYr == "null") {
                    Constants.showSnack(v,
                            "Select financial year!",
                            "OK");
                } else if (monthName == "" || monthName.equals("")
                        || monthName == null || monthName == "null") {
                    Constants.showSnack(v,
                            "Select month!",
                            "OK");
                } else if (actionType == "" || actionType.equals("")
                        || actionType == null || actionType == "null") {
                    Constants.showSnack(v,
                            "Select View/Upload!",
                            "OK");
                } else {

                    if (actionType == "Upload" || actionType.equals("Upload")) {
                        if (selectedFile == null || selectedFile.toString() == "null") {
                            Constants.showSnack(v,
                                    "Select a file to upload!",
                                    "OK");
                        } else {
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
        totBilAmountLay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                ViewBillDetailFragment fragment = new ViewBillDetailFragment();
                Bundle bundle = new Bundle();
                bundle.putString("TOT_DATA", totalDataArr.toString());
                fragment.setArguments(bundle);
                ((HomeActivity) context).addFragment(fragment);
            }
        });
        dueStatusLay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                ViewBillDetailFragment fragment = new ViewBillDetailFragment();
                Bundle bundle = new Bundle();
                bundle.putString("TOT_DATA", dueStatusDataArr.toString());
                fragment.setArguments(bundle);
                ((HomeActivity) context).addFragment(fragment);
            }
        });
        paidStatusCountLay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                ViewBillDetailFragment fragment = new ViewBillDetailFragment();
                Bundle bundle = new Bundle();
                bundle.putString("TOT_DATA", paidStatusDataArr.toString());
                fragment.setArguments(bundle);
                ((HomeActivity) context).addFragment(fragment);
            }
        });
        paidStatusCountLay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                ViewBillDetailFragment fragment = new ViewBillDetailFragment();
                Bundle bundle = new Bundle();
                bundle.putString("TOT_DATA", paidStatusDataArr.toString());
                fragment.setArguments(bundle);
                ((HomeActivity) context).addFragment(fragment);
            }
        });
        confrmLay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                ViewBillDetailFragment fragment = new ViewBillDetailFragment();
                Bundle bundle = new Bundle();
                bundle.putString("TOT_DATA", confirmedStatusDataArr.toString());
                fragment.setArguments(bundle);
                ((HomeActivity) context).addFragment(fragment);
            }
        });
        return v;
    }

    private void doOpen() {
        Intent intent = new Intent(getActivity(), FilePicker.class);
        startActivityForResult(intent, REQUEST_PICK_FILE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == getActivity().RESULT_OK) {

            switch (requestCode) {

                case REQUEST_PICK_FILE:

                    if (data.hasExtra(FilePicker.EXTRA_FILE_PATH)) {

                        selectedFile = new File(
                                data.getStringExtra(FilePicker.EXTRA_FILE_PATH));
                        Constants.showSnack(v,
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
                ((HomeActivity) context).onBackpressed();
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
                        monthName, financialYr, selectedFile, MyApplication.HOSTNAME);

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            if (jsonBill == null || jsonBill.toString() == "null"
                    || jsonBill.equals("null")) {
                Constants.showSnack(v,
                        "Oops! Something went wrong. Please wait a moment!",
                        "OK");
                pDialog.setVisibility(View.GONE);
                fileNameTxt.setVisibility(View.GONE);
            } else {
                Constants.showSnack(v,
                        "Uploaded file!",
                        "OK");
                pDialog.setVisibility(View.GONE);
                fileNameTxt.setVisibility(View.GONE);
                submitTxt.setEnabled(false);
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
                        monthName, financialYr, MyApplication.HOSTNAME);
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
                Constants.showSnack(v,
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
                    confrmdAmount.setText("Rs." + json.getString("confirmed_amount") + ".00");
                    balnceAmountTxt.setText("Rs." + json.getString("balanced_amount") + ".00");
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
    public void onResume() {
        super.onResume();
        setHasOptionsMenu(true);
        ((HomeActivity) context).setHomeAsEnabled(true);
        ((HomeActivity) context).changeToolbarTitle(R.string.sbmanagement);
    }
    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        FlurryAgent.onStartSession(getActivity().getApplicationContext(), Constants.flurryApiKey);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(AddIdeasEvent event) {
        if (event.success) {
            new ViewBill().execute();
        }
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        FlurryAgent.onEndSession(getActivity().getApplicationContext());
        super.onPause();
    }
}
