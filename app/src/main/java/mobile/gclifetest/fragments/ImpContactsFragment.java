package mobile.gclifetest.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonRequest;
import com.flurry.android.FlurryAgent;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import mobile.gclifetest.activity.BaseActivity;
import mobile.gclifetest.activity.HomeActivity;
import mobile.gclifetest.activity.R;
import mobile.gclifetest.db.DatabaseHandler;
import mobile.gclifetest.materialDesign.ProgressBarCircularIndeterminate;
import mobile.gclifetest.pojoGson.ImpContactsPojo;
import mobile.gclifetest.utils.Constants;
import mobile.gclifetest.utils.MyApplication;

/**
 * Created by MRaoKorni on 8/26/2016.
 */
public class ImpContactsFragment extends Fragment {
    Context context;
    FloatingActionButton addBtn;
    ProgressBarCircularIndeterminate pDialog,pDialogBtm;
    ListView listviewImp;
    SharedPreferences userPref;
    Typeface typefaceLight;
    String name, phNo, email;
    List<ImpContactsPojo> impContactPojo;
    List<ImpContactsPojo> globalImpContactPojo = new ArrayList<>();
    Gson gson = new Gson();
    SwipeRefreshLayout mSwipeRefreshLayout;
    Runnable run;
    DatabaseHandler db;
    ListImpContsBaseAdapter adapterConts;
    RelativeLayout searchLay;
    EditText searchEdit;
    ProgressBar progressBar;
    String searchStr = "";
    boolean progressShow = true;
    ImageView clearImg;
    int limit = 15, currentPosition, offset = 0;
    RelativeLayout snackLay;
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(
                R.layout.ideas_list, container, false);
        context = getActivity();
        addBtn = (FloatingActionButton) v.findViewById(R.id.addBtn);
        pDialog = (ProgressBarCircularIndeterminate) v.findViewById(R.id.pDialog);
        pDialogBtm = (ProgressBarCircularIndeterminate)v.findViewById(R.id.pDialogBtm);
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.activity_main_swipe_refresh_layout);
        searchEdit = (EditText) v.findViewById(R.id.searchEdit);
        progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        searchLay = (RelativeLayout) v.findViewById(R.id.searchLay);
        clearImg = (ImageView) v.findViewById(R.id.clearImg);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.orange,
                android.R.color.holo_green_dark, R.color.blue);
        listviewImp = (ListView) v.findViewById(R.id.listview);
        snackLay = (RelativeLayout) v.findViewById(R.id.snackLay);
        listviewImp.setClickable(false);
        addBtn.setVisibility(View.GONE);
        userPref = context.getSharedPreferences("USER", context.MODE_PRIVATE);
        db = new DatabaseHandler(context);
        mSwipeRefreshLayout
                .setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                offset = 0;
                                impContactPojo = new ArrayList<ImpContactsPojo>();
                                callImpContsList(searchStr);
                                getActivity().runOnUiThread(run);
                                mSwipeRefreshLayout
                                        .setRefreshing(false);
                            }
                        }, 2500);
                    }
                });
        run = new Runnable() {
            public void run() {
                if (impContactPojo.toString() == "[]" || impContactPojo.size() == 0) {

                } else {
                    adapterConts.notifyDataSetChanged();
                    listviewImp.invalidateViews();
                }
            }
        };
        listviewImp
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @SuppressWarnings("unchecked")
                    @Override
                    public void onItemClick(AdapterView<?> parent,
                                            View view, int position, long id) {
                        // TODO Auto-generated method stub
                        System.out.print(position + " ******** ");
                        showPopup(globalImpContactPojo, position);
                    }
                });
        searchEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (searchStr.length() >= 3) {
                        searchStr = searchEdit.getText().toString();
                        progressShow = false;
                        offset = 0;
                        callImpContsList(searchStr);
                        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(searchEdit.getWindowToken(), 0);
                    }else{
                        Toast.makeText(getActivity(), "Type atleast three characters", Toast.LENGTH_LONG).show();
                    }
                    return true;
                }
                return false;
            }
        });
        clearImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                globalImpContactPojo = new ArrayList<ImpContactsPojo>();
                offset = 0;
                searchStr = "";
                callImpContsList(searchStr);
                searchEdit.setText("");
                searchLay.setVisibility(View.GONE);
            }
        });
        searchEdit.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

                searchStr = searchEdit.getText().toString();
            }
        });
        if (db.getEventNews("ImpContacts") != "null") {
            Log.d("DB NOT NULL: " + "ImpContats", db.getEventNews("ImpContacts"));
            pDialog.setVisibility(View.GONE);
            impContactPojo = gson.fromJson(db.getEventNews("ImpContacts"), new TypeToken<List<ImpContactsPojo>>() {
            }.getType());
            adapterConts = new ListImpContsBaseAdapter(
                    getActivity(), impContactPojo);
            listviewImp.setAdapter(adapterConts);
            progressShow = false;
            callImpContsList(searchStr);
        } else {
            callImpContsList(searchStr);
            Log.d("DB NULL: " + "ImpContacts", "");
        }
        return v;
    }

    protected void showPopup(List<ImpContactsPojo> impContsPojo, int position) {
        // TODO Auto-generated method stub
        Dialog m_dialog = new Dialog(context);
        m_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        m_dialog.setContentView(R.layout.imp_contacts_popup_listview);
        m_dialog.getWindow().getAttributes().windowAnimations = R.style.popup_login_dialog_animation;
        ListView listviewConts = (ListView) m_dialog
                .findViewById(R.id.listview);
        if(impContsPojo.size()>0){
            phNo = impContsPojo.get(position).getPhno();

            String[] phNoArray = phNo.split(",");
            // String[]
            // emailArr=list.get(position).get("email").split(",");

            System.out.println(phNoArray
                    + "!!!!!!!!!!!!!!!!!!!!!! PH NO's");
            List<String> phList = Arrays.asList(phNoArray);
            // List<String> emailList = Arrays.asList(emailArr);
            ArrayList<HashMap<String, String>> listConts = new ArrayList<HashMap<String, String>>();
            for (int i = 0; i < phNoArray.length; i++) {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("PHNO", phList.get(i));
                map.put("NAME", impContsPojo.get(position).getName());
                map.put("EMAIL", impContsPojo.get(position).getEmail());
                listConts.add(map);
            }
            ListImpPopupBaseAdapter adapter = new ListImpPopupBaseAdapter(
                    getActivity(), listConts);
            listviewConts.setAdapter(adapter);

            m_dialog.show();
        }
    }

    private void callImpContsList(final String searchStr) {
        try {

            if (progressShow) {
                pDialog.setVisibility(View.VISIBLE);
            } else {
                pDialog.setVisibility(View.GONE);
            }
            String hostt = MyApplication.HOSTNAME + "important_contacts.json?limit=" + limit + "&offset=" + offset +"&search_key=" + searchStr;
            hostt = hostt.replace(" ", "%20");
            JsonArrayRequest request = new JsonArrayRequest(JsonRequest.Method.GET, hostt,
                    (String) null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    //    pDialog.hide();
                    if (response != null) {
                        impContactPojo = gson.fromJson(response.toString(), new TypeToken<List<ImpContactsPojo>>() {
                        }.getType());
                        Log.d("Reponse", response.toString());
                        if (response.length() == 0
                                || response.toString() == "[]"
                                || response.toString() == ""
                                || response.toString().equals("")) {
                            pDialog.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                            pDialogBtm.setVisibility(View.INVISIBLE);
                            if (searchStr.length() > 0) {
                                Constants.showSnack(snackLay,
                                        "Searched Important contact details are not found, please try with another search criteria!", "");
                            } else {
                                Constants.showSnack(snackLay,
                                        "No further Contacts content is available!",
                                        "OK");
                            }

                            impContactPojo.clear();

                            currentPosition = listviewImp
                                    .getLastVisiblePosition();
                            DisplayMetrics displayMetrics =
                                    getResources().getDisplayMetrics();
                            int height = displayMetrics.heightPixels;
                            listviewImp.setSelectionFromTop(
                                    currentPosition, height - 220);
                        } else {
                            globalImpContactPojo.addAll(impContactPojo);
                            Log.d("SIZE", globalImpContactPojo.size() + "");
                            currentPosition = listviewImp
                                    .getLastVisiblePosition();
                            if (getActivity() != null) {
                            adapterConts = new ListImpContsBaseAdapter(
                                    getActivity(), globalImpContactPojo);
                            listviewImp.setAdapter(adapterConts);
                            adapterConts.notifyDataSetChanged();
                            pDialog.setVisibility(View.GONE);
                            pDialogBtm.setVisibility(View.INVISIBLE);
                            progressBar.setVisibility(View.GONE);
                            db.addEventNews(response, "ImpContacts");
                            //for updating new data
                            db.updateEventNews(response, "ImpContacts");
                                DisplayMetrics displayMetrics =
                                        getResources().getDisplayMetrics();
                                int height = displayMetrics.heightPixels;

                                listviewImp.setSelectionFromTop(
                                        currentPosition + 1, height - 220);

                                listviewImp
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
                                                if (view.getId() == listviewImp
                                                        .getId()) {
                                                    final int currentFirstVisibleItem = listviewImp
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
                                                    offset = offset + 15;
                                                    Log.d("Offset :", offset + "");
                                                    pDialogBtm.setVisibility(View.VISIBLE);
                                                    progressShow = false;
                                                    callImpContsList(searchStr);
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

                    if (getActivity() != null) {
                        pDialog.setVisibility(View.GONE);
                        Constants.showSnack(snackLay,
                                "Oops! Something went wrong. Please check internet connection!",
                                "OK");
                    }
                    //   pDialog.hide();
                }
            });
            MyApplication.queue.add(request);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public class ListImpContsBaseAdapter extends BaseAdapter {
        List<ImpContactsPojo> impContsPojo;
        private LayoutInflater inflator;
        private Context context;

        public ListImpContsBaseAdapter(Activity activity,
                                       List<ImpContactsPojo> impContactsPojo) {
            // TODO Auto-generated constructor stub
            this.context = activity;
            this.impContsPojo = impContactsPojo;
            inflator = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return impContsPojo.size();
        }

        @Override
        public Object getItem(int pos) {
            // TODO Auto-generated method stub
            return impContsPojo.get(pos);
        }

        @Override
        public long getItemId(int pos) {
            // TODO Auto-generated method stub
            return pos;
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            // TODO Auto-generated method stub
            ViewHolder holder;
            if (convertView == null) {
                convertView = inflator.inflate(R.layout.imp_contacts_row,
                        parent, false);
                holder = new ViewHolder();
                holder.titleTxt = (TextView) convertView
                        .findViewById(R.id.titleTxt);

                holder.titleTxt.setTypeface(typefaceLight);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }


            holder.titleTxt.setText(impContsPojo.get(position).getName());

            return convertView;
        }

        public class ViewHolder {
            TextView titleTxt;
        }
    }

    public class ListImpPopupBaseAdapter extends BaseAdapter {
        ArrayList<HashMap<String, String>> list;
        private LayoutInflater inflator;
        private Context context;

        public ListImpPopupBaseAdapter(Context context2,
                                       ArrayList<HashMap<String, String>> listArticles) {
            // TODO Auto-generated constructor stub
            this.context = context2;
            this.list = listArticles;
            inflator = (LayoutInflater) context2
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return list.size();
        }

        @Override
        public Object getItem(int pos) {
            // TODO Auto-generated method stub
            return list.get(pos);
        }

        @Override
        public long getItemId(int pos) {
            // TODO Auto-generated method stub
            return pos;
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            // TODO Auto-generated method stub
            ViewHolder holder;
            if (convertView == null) {
                convertView = inflator.inflate(R.layout.imp_contacts_popup,
                        parent, false);
                holder = new ViewHolder();
                holder.titleTxt = (TextView) convertView
                        .findViewById(R.id.titleTxt);
                holder.phNumTxt = (TextView) convertView
                        .findViewById(R.id.phNumTxt);
                holder.emailTxt = (TextView) convertView
                        .findViewById(R.id.emailTxt);
                holder.callImg = (ImageView) convertView
                        .findViewById(R.id.phoneImg);
                holder.smsImg = (ImageView) convertView
                        .findViewById(R.id.smsImg);
                holder.emailImg = (ImageView) convertView
                        .findViewById(R.id.emailImg);
                holder.titleTxt.setTypeface(typefaceLight);
                holder.phNumTxt.setTypeface(typefaceLight);
                holder.emailTxt.setTypeface(typefaceLight);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.callImg.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    Intent intent = new Intent(Intent.ACTION_DIAL);

                    intent.setData(Uri.parse("tel:" + list.get(position).get("PHNO")));
                    context.startActivity(intent);
                }
            });
            holder.smsImg.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    Intent smsVIntent = new Intent(Intent.ACTION_VIEW);
                    smsVIntent.setType("vnd.android-dir/mms-sms");
                    smsVIntent.putExtra("address", list.get(position).get("PHNO").replaceAll("[.0]+$", ""));
                    try {
                        startActivity(smsVIntent);
                    } catch (Exception ex) {
                        Constants.showSnack(snackLay,
                                "Your sms has failed...!",
                                "OK");
                        ex.printStackTrace();
                    }
                }
            });
            holder.emailImg.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO,
                            Uri.fromParts("mailto", list.get(position).get("EMAIL"), null));
                    startActivity(Intent.createChooser(emailIntent, "Send email..."));
                }
            });
            holder.titleTxt.setText(list.get(position).get("NAME"));
            holder.phNumTxt.setText(list.get(position).get("PHNO").replaceAll("[.0]+$", ""));
            holder.emailTxt.setText(list.get(position).get("EMAIL"));
            if (list.get(position).get("EMAIL") == "" || list.get(position).get("EMAIL").equals("")) {
                holder.emailImg.setVisibility(View.GONE);
            }

            return convertView;
        }

        public class ViewHolder {
            TextView titleTxt, phNumTxt, emailTxt;
            ImageView callImg, smsImg, emailImg;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here
        inflater.inflate(R.menu.search, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                ((HomeActivity) context).onBackpressed();
                BaseActivity.closeSoftKeyboard(context, searchEdit);
                return true;
            case R.id.search:
                searchLay.setVisibility(View.VISIBLE);
                searchEdit.requestFocus();
                searchEdit.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 0, 0, 0));
                globalImpContactPojo = new ArrayList<>();
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
        ((HomeActivity) context).changeToolbarTitle(R.string.imp_contacts);
        searchEdit.setHint("Search your contacts");
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
