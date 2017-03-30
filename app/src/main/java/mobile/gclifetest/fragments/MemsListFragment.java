package mobile.gclifetest.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonRequest;
import com.flurry.android.FlurryAgent;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import mobile.gclifetest.activity.HomeActivity;
import mobile.gclifetest.activity.R;
import mobile.gclifetest.activity.UserProfile;
import mobile.gclifetest.db.DatabaseHandler;
import mobile.gclifetest.http.MemsPost;
import mobile.gclifetest.materialDesign.ProgressBarCircularIndeterminate;
import mobile.gclifetest.pojoGson.FlatDetailsPojo;
import mobile.gclifetest.pojoGson.UserDetailsPojo;
import mobile.gclifetest.utils.Constants;
import mobile.gclifetest.utils.MyApplication;

/**
 * Created by MRaoKorni on 8/26/2016.
 */
public class MemsListFragment extends Fragment {
    Context context;
    ListView listviewMem;
    JSONObject veriJson;
    SharedPreferences userPref;
    String status, eventName = "MEMSVERIFI", statusReason = "";
    int memId, pos, recordId;
    JSONObject json;
    static Typeface typefaceLight;
    ProgressBarCircularIndeterminate pDialog;
    ProgressBarCircularIndeterminate pDialogPop, pDialogBtm;
    Dialog m_dialog, filter_dialog;
    UserModelAdapter dataTaskGrpAdapter;
    TextView submitTxt, loginWithEMailTxt, discriptionEdit;
    CheckBox checkboxApprove, rejectCheckBox, deleteCheckBox;
    SharedPreferences.Editor editor;
    List<UserDetailsPojo> userList;
    List<UserDetailsPojo> globalUsersList = new ArrayList<>();
    DatabaseHandler db;
    Gson gson;
    int limit = 10, currentPosition, offset = 0;
    View v;
    FloatingActionButton filterBtn;
    String filter = "Inactive";
    RadioGroup radioGroup;
    boolean isPagintation;
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(
                R.layout.list_members, container, false);
        context = getActivity();
        db = new DatabaseHandler(context);
        pDialog = (ProgressBarCircularIndeterminate)v.findViewById(R.id.pDialog);
        pDialogBtm = (ProgressBarCircularIndeterminate)v.findViewById(R.id.pDialogBtm);
        listviewMem = (ListView)v.findViewById(R.id.listViewMem);
        filterBtn = (FloatingActionButton) v.findViewById(R.id.filterBtn);
        userPref = context.getSharedPreferences("USER", context.MODE_PRIVATE);
        editor = userPref.edit();
        gson = new Gson();

        filter_dialog = new Dialog(context);
        filter_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        filter_dialog.setContentView(R.layout.mem_filter_popup);
        filter_dialog.getWindow().getAttributes().windowAnimations = R.style.popup_login_dialog_animation;

        listviewMem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent i = new Intent(context, UserProfile.class);
                Gson gsonn = new Gson();
                String json = gsonn.toJson(globalUsersList.get(position));
                editor.putString("activityName", "mems_activity");
                editor.apply();
                i.putExtra("EACH_USER_DET", json);
                startActivity(i);
            }
        });
        if (db.getEventNews(eventName) != "null") {
            Log.d("DB NOT NULL: " + eventName, db.getEventNews(eventName));
            pDialog.setVisibility(View.GONE);
            userList = gson.fromJson(db.getEventNews(eventName), new TypeToken<List<UserDetailsPojo>>() {
            }.getType());
            if (context != null) {
                dataTaskGrpAdapter = new UserModelAdapter(context, R.layout.member_row, userList);
                listviewMem.setAdapter(dataTaskGrpAdapter);
                callListMems(filter);
            }
        } else {
            callListMems(filter);
            Log.d("DB NULL: " + eventName, "");

        }
        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TextView cancellTxt = (TextView) filter_dialog
                        .findViewById(R.id.cancellTxt);
                TextView submitTxt = (TextView) filter_dialog
                        .findViewById(R.id.submitTxt);
                radioGroup = (RadioGroup) filter_dialog.findViewById(R.id.myRadioGroup);
                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        // TODO Auto-generated method stub
                        int pos = radioGroup.indexOfChild(filter_dialog.findViewById(checkedId));
                        switch (pos) {
                            case 0:
                                filter = "Inactive";
                                break;
                            case 1:
                                filter = "Approve";
                                break;
                            case 2:
                                filter = "Delete";
                                break;
                            case 3:
                                filter = "Reject";
                                break;
                            default:
                                filter = "Inactive";
                                break;
                        }
                    }
                });
                cancellTxt.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        filter_dialog.dismiss();
                    }
                });
                submitTxt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        globalUsersList = new ArrayList<UserDetailsPojo>();
                        db.clearTable(DatabaseHandler.TABLE_MEMSVERIFI);
                        // listviewMem.setAdapter(null);
                        callListMems(filter);
                        filter_dialog.dismiss();

                    }
                });
                filter_dialog.show();
            }
        });
        return v;
    }

    private void callListMems(final String filter) {
        JsonArrayRequest request = new JsonArrayRequest(JsonRequest.Method.GET, MyApplication.HOSTNAME + "get_registered_users.json?user_id=" +
                userPref.getString("USERID", "NV").replaceAll(" ", "%20") + "&limit=" + limit + "&offset=" + offset + "&filter_type=" + filter,
                (String) null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                //    pDialog.hide();
                Log.d("MEMS LIST REQUEST : ", MyApplication.HOSTNAME + "get_registered_users.json?user_id=" +
                        userPref.getString("USERID", "NV").replaceAll(" ", "%20") + "&limit=" + limit + "&offset=" + offset + "&filter_type=" + filter);
                Log.d("Response", response.toString());
                if (response != null) {
                    if (response.length() == 0
                            || response.toString() == "[]"
                            || response.toString() == ""
                            || response.toString().equals("")) {
                        pDialog.setVisibility(View.GONE);
                        pDialogBtm.setVisibility(View.INVISIBLE);
                        Constants.showToast(context, R.string.oops_no_members);
                        if (!isPagintation)
                            listviewMem.setAdapter(null);

                    } else {
                        userList = gson.fromJson(response.toString(), new TypeToken<List<UserDetailsPojo>>() {
                        }.getType());

                        globalUsersList.addAll(userList);
                        Log.d("SIZE", globalUsersList.size() + "");
                        currentPosition = listviewMem
                                .getLastVisiblePosition();
                        if (context != null) {
                            dataTaskGrpAdapter = new UserModelAdapter(context, R.layout.member_row, globalUsersList);

                            listviewMem.setAdapter(dataTaskGrpAdapter);

                            dataTaskGrpAdapter.notifyDataSetChanged();

                            pDialog.setVisibility(View.GONE);
                            pDialogBtm.setVisibility(View.INVISIBLE);
                            // Storing in DB
                            db.addEventNews(response, eventName);
                            //        //for updating new data
                            db.updateEventNews(response, eventName);
                            if (getActivity() != null) {
                                DisplayMetrics displayMetrics =
                                        getResources().getDisplayMetrics();
                                int height = displayMetrics.heightPixels;

                                listviewMem.setSelectionFromTop(
                                        currentPosition, height - 220);
                            }
                            listviewMem
                                    .setOnScrollListener(new AbsListView.OnScrollListener() {

                                        private int currentScrollState;
                                        private int currentFirstVisibleItem;
                                        private int currentVisibleItemCount;
                                        private int totalItemCount;
                                        private int mLastFirstVisibleItem;
                                        private boolean mIsScrollingUp;

                                        @Override
                                        public void onScrollStateChanged(
                                                AbsListView view, int scrollState) {
                                            // TODO Auto-generated method stub
                                            this.currentScrollState = scrollState;
                                            pDialogBtm.setVisibility(View.INVISIBLE);
                                            if (view.getId() == listviewMem
                                                    .getId()) {
                                                final int currentFirstVisibleItem = listviewMem
                                                        .getFirstVisiblePosition();

                                                if (currentFirstVisibleItem > mLastFirstVisibleItem) {
                                                    mIsScrollingUp = false;

                                                } else if (currentFirstVisibleItem < mLastFirstVisibleItem) {

                                                    mIsScrollingUp = true;
                                                }

                                                mLastFirstVisibleItem = currentFirstVisibleItem;
                                            }
                                            this.isScrollCompleted();
                                        }

                                        @Override
                                        public void onScroll(AbsListView view,
                                                             int firstVisibleItem,
                                                             int visibleItemCount,
                                                             int totalItemCount) {
                                            // TODO Auto-generated method stub

                                            this.currentFirstVisibleItem = firstVisibleItem;
                                            this.currentVisibleItemCount = visibleItemCount;
                                            this.totalItemCount = totalItemCount;

                                        }

                                        private void isScrollCompleted() {
                                            if (this.currentVisibleItemCount > 0
                                                    && this.currentScrollState == SCROLL_STATE_IDLE
                                                    && this.totalItemCount == (currentFirstVisibleItem + currentVisibleItemCount)) {
                                                offset = offset + 10;
                                                pDialogBtm.setVisibility(View.VISIBLE);
                                                isPagintation = true;
                                                callListMems(filter);

                                                //  pDialogBtm.setVisibility(View.GONE);
                                            }
                                        }
                                    });
                        }
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();

                Log.d("Error = ", volleyError.toString());

                pDialog.setVisibility(View.GONE);
                Constants.showToast(context, R.string.oops_no_members);
            }
        });
        MyApplication.queue.add(request);
    }
    private class UserModelAdapter extends ArrayAdapter<UserDetailsPojo> {

        List<UserDetailsPojo> usersList;
        LayoutInflater inflator;
        public UserModelAdapter(Context context, int textViewResourceId,
                                List<UserDetailsPojo> userList) {
            super(context, textViewResourceId, userList);
            this.usersList = userList;
            inflator = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public int updateList(List<UserDetailsPojo> useList) {
            notifyDataSetChanged();
            return useList.size();

        }


        @Override
        public int getCount() {
            return usersList.size();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            final ViewHolder holder;
            Log.v("ConvertView", String.valueOf(position));

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
            holder.usernameTxt.setText(usersList.get(position).getUsername());
            final List<FlatDetailsPojo> flatsList = usersList.get(position).getGclife_registration_flatdetails();
            for (int i = 0; i < flatsList.size(); i++) {
                holder.avenueNameTxt.setText(flatsList.get(i).getAvenue_name()+ " , " + flatsList.get(i).getBuildingid()+" , " + flatsList.get(i).getFlat_number());
            }
            holder.profileImg.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    Intent i = new Intent(context, UserProfile.class);
                    Gson gsonn = new Gson();
                    String json = gsonn.toJson(usersList.get(position));
                    i.putExtra("EACH_USER_DET", json);
                    startActivity(i);
                }
            });
            holder.settingsImg.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    m_dialog = new Dialog(context);
                    m_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    m_dialog.setContentView(R.layout.mem_approval_popup);
                    m_dialog.getWindow().getAttributes().windowAnimations = R.style.popup_login_dialog_animation;
                    TextView cancellTxt = (TextView) m_dialog
                            .findViewById(R.id.cancellTxt);
                    loginWithEMailTxt = (TextView) m_dialog
                            .findViewById(R.id.loginwithEmailTxt);
                    discriptionEdit = (TextView) m_dialog
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

                    if (usersList.get(position).getGclife_registration_flatdetails().get(0).getStatus().equalsIgnoreCase("Approve")) {
                        checkboxApprove.setChecked(true);
                        status = "Approve";
                    } else if (usersList.get(position).getGclife_registration_flatdetails().get(0).getStatus().equalsIgnoreCase("Delete")) {
                        deleteCheckBox.setChecked(true);
                        status = "Delete";
                    } else if (usersList.get(position).getGclife_registration_flatdetails().get(0).getStatus().equalsIgnoreCase("Reject")) {
                        rejectCheckBox.setChecked(true);
                        status = "Reject";
                    }
                    submitTxt.setTypeface(typefaceLight);
                    cancellTxt.setTypeface(typefaceLight);
                    discriptionEdit.setTypeface(typefaceLight);
                    loginWithEMailTxt.setTypeface(typefaceLight);
                    Log.d("MEMID", String.valueOf(usersList.get(position).getId()));
                    memId = usersList.get(position).getId();
                    recordId = flatsList.get(0).getId();
                    pos=position;
                    cancellTxt.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            m_dialog.dismiss();
                        }
                    });
                    submitTxt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            statusReason = discriptionEdit.getText().toString();
                            if (status == null || status == "null" || status == "" || status.equals("")) {
                                Constants.showSnack(v,
                                        "Select atleast one action!",
                                        "OK");
                            } else {

                                System.out.println(memId);
                                // to remove row
                                usersList.remove(position);

                                new ActivateMems().execute();
                            }
                        }
                    });
                    checkboxApprove.setOnClickListener(new View.OnClickListener() {

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
                    rejectCheckBox.setOnClickListener(new View.OnClickListener() {

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
                    deleteCheckBox.setOnClickListener(new View.OnClickListener() {

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
                Log.d("STATUS", status);
                veriJson = MemsPost.activateMem(MyApplication.HOSTNAME, memId, status, statusReason, recordId);
                Log.d("MEM RESPONSE", veriJson.toString());
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
            if (veriJson != null) {
                if (status == "Reject" || status.equals("Reject")) {
                    Constants.showToast(context, R.string.rejected);
                } else if (status == "Delete" || status.equals("status")) {
                    Constants.showToast(context, R.string.deleted);
                } else if (status == "Approve" || status.equals("Approve")) {
                    Constants.showToast(context, R.string.approved);
                } else {
                    Constants.showToast(context, R.string.went_wrong);
                }
                if (db.getEventNews(eventName) != "null") {
                    Log.d("DB NOT NULL: " + eventName, db.getEventNews(eventName));
                    userList = gson.fromJson(db.getEventNews(eventName), new TypeToken<List<UserDetailsPojo>>() {
                    }.getType());
                   // userList.remove(pos);
                    String afterEdit = gson.toJson(userList);
                    try {
                        JSONArray arr = new JSONArray(afterEdit);
                        db.updateEventNews(arr, eventName);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            } else {
                Constants.showToast(context, R.string.went_wrong);
            }

            dataTaskGrpAdapter.notifyDataSetChanged();
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
        ((HomeActivity) context).changeToolbarTitle(R.string.mem_ver);
    }

    @Override
    public void onStart() {
        super.onStart();
        FlurryAgent.onStartSession(context.getApplicationContext(), Constants.flurryApiKey);
    }

    @Override
    public void onStop() {
        super.onStop();
        FlurryAgent.onEndSession(context.getApplicationContext());
    }
}
