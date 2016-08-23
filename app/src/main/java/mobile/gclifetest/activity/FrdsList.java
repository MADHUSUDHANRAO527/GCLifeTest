package mobile.gclifetest.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonRequest;
import com.gc.materialdesign.views.ButtonFloat;
import com.gc.materialdesign.widgets.SnackBar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mobile.gclifetest.MaterialDesign.ProgressBarCircularIndeterminate;
import mobile.gclifetest.PojoGson.FlatDetailsPojo;
import mobile.gclifetest.PojoGson.UserDetailsPojo;
import mobile.gclifetest.Utils.InternetConnectionDetector;
import mobile.gclifetest.Utils.MyApplication;
import mobile.gclifetest.Utils.NothingSelectedSpinnerAdapter1;
import mobile.gclifetest.db.DatabaseHandler;
import mobile.gclifetest.http.SocietyNameGet;

public class FrdsList extends BaseActivity {
    ButtonFloat addBtn;
    ProgressBarCircularIndeterminate pDialog,pDialogBtm;
    InternetConnectionDetector netConn;
    UserDetailsPojo user;
    Boolean isInternetPresent = false;
    String  eventName = "Friends", avenueName, societyName, buildingNum;
    ListView listviewIdeas;
    JSONArray jsonResultArry;
    SharedPreferences userPref;
    List<FlatDetailsPojo> flatsList = new ArrayList<FlatDetailsPojo>();
    FlatDetailsPojo flats;
    Typeface typefaceLight;
    Map<String, List<String>> societyMap = new HashMap<String, List<String>>();
    Map<String, List<String>> buildingMap = new HashMap<String, List<String>>();
    Spinner avenueSpinner, societyNameSpinner, buildingSpinner;
    EditText flatNoEdit;
    ArrayAdapter<String> associtationAdapter;
    JSONObject jsonDetails;
    DatabaseHandler db;
    List<UserDetailsPojo> userListPojo;
    List<UserDetailsPojo> globalUserListPojo=new ArrayList<>();
    ListFreindsBaseAdapter adapterfrds;
    SwipeRefreshLayout mSwipeRefreshLayout;
    Runnable run;
    Gson gson;
    RelativeLayout searchLay;
    EditText searchEdit;
    ProgressBar progressBar;
    String searchStr="";
    boolean progressShow=true;
    ImageView clearImg;
    int limit = 15, currentPosition,offset=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ideas_list);
        setUpActionBar("Friends");
        addBtn = (ButtonFloat) findViewById(R.id.addBtn);
        pDialog = (ProgressBarCircularIndeterminate) findViewById(R.id.pDialog);
        listviewIdeas = (ListView) findViewById(R.id.listview);
        searchLay=(RelativeLayout)findViewById(R.id.searchLay);
        avenueSpinner = (Spinner) findViewById(R.id.avenueSpin);
        pDialogBtm = (ProgressBarCircularIndeterminate) findViewById(R.id.pDialogBtm);
        societyNameSpinner = (Spinner) findViewById(R.id.societySpin);
        buildingSpinner = (Spinner) findViewById(R.id.buildingSpin);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
        searchEdit=(EditText)findViewById(R.id.searchEdit);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        clearImg=(ImageView)findViewById(R.id.clearImg);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.orange,
                R.color.green, R.color.blue);
        gson = new Gson();
        db = new DatabaseHandler(this);
        typefaceLight = Typeface.createFromAsset(getAssets(),
                "fonts/RobotoLight.ttf");
        userPref = getSharedPreferences("USER", MODE_PRIVATE);
        if (db.getEventNews(eventName) != "null") {
            Log.d("DB NOT NULL: " + eventName, db.getEventNews(eventName));
            pDialog.setVisibility(View.GONE);
            userListPojo = gson.fromJson(db.getEventNews(eventName), new TypeToken<List<UserDetailsPojo>>() {
            }.getType());
            globalUserListPojo.addAll(userListPojo);
            adapterfrds = new ListFreindsBaseAdapter(FrdsList.this, globalUserListPojo);
            listviewIdeas.setAdapter(adapterfrds);
            listviewIdeas.smoothScrollToPosition(0);
            progressShow=false;
            callFrdsList();

        } else {
            callFrdsList();
            Log.d("DB NULL: " + eventName, "");

        }
        mSwipeRefreshLayout
                .setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                offset=0;
                                globalUserListPojo = new ArrayList<UserDetailsPojo>();
                                callFrdsList();
                                runOnUiThread(run);
                                mSwipeRefreshLayout
                                        .setRefreshing(false);
                            }
                        }, 2500);
                    }
                });
        run = new Runnable() {
            public void run() {
                if (userListPojo.toString() == "[]" || userListPojo.size() == 0) {

                } else {
                    adapterfrds.notifyDataSetChanged();
                    listviewIdeas.invalidateViews();
                }
            }
        };
        searchEdit.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(searchStr.length()==2){
                    Toast.makeText(getApplicationContext(),"Type atleast three characters",Toast.LENGTH_LONG).show();
                }
            }
        });
        searchEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchStr = searchEdit.getText().toString();
                    System.out.println(searchStr);
                   if(searchStr.length()>=3){
                       globalUserListPojo=new ArrayList<UserDetailsPojo>();
                       offset=0;
                       adapterfrds.notifyDataSetChanged();
                        progressShow=false;
                        callFrdsList();
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(searchEdit.getWindowToken(), 0);
                   }else{
                       Toast.makeText(getApplicationContext(),"Type atleast three characters",Toast.LENGTH_LONG).show();
                   }

                    return true;
                }
                return false;
            }
        });
        addBtn.setVisibility(View.GONE);
        clearImg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                globalUserListPojo=new ArrayList<UserDetailsPojo>();
                offset=0;
                searchStr="";
                callFrdsList();
                searchEdit.setText("");
            }
        });
        addBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                if (jsonResultArry == null
                        || jsonResultArry.toString() == "null") {
                    showSnack(FrdsList.this,
                            "Please wait! Societies are loading...!", "OK");
                } else {
                    final Dialog m_dialog = new Dialog(FrdsList.this);
                    m_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    m_dialog.setContentView(R.layout.frds_filter);
                    m_dialog.getWindow().getAttributes().windowAnimations = R.style.popup_login_dialog_animation;

                    avenueSpinner = (Spinner) m_dialog
                            .findViewById(R.id.avenueSpin);
                    societyNameSpinner = (Spinner) m_dialog
                            .findViewById(R.id.societySpin);
                    flatNoEdit = (EditText) m_dialog
                            .findViewById(R.id.flatNoEdit);
                    buildingSpinner = (Spinner) m_dialog
                            .findViewById(R.id.buildingSpin);
                    TextView cancellTxt = (TextView) m_dialog
                            .findViewById(R.id.cancellTxt);
                    TextView submitTxt = (TextView) m_dialog
                            .findViewById(R.id.submitTxt);

                    cancellTxt.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            m_dialog.dismiss();
                        }
                    });
                    submitTxt.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }

                    });

                    ArrayAdapter<CharSequence> avenueAdapter = ArrayAdapter
                            .createFromResource(FrdsList.this,
                                    R.array.avenueArray, R.layout.spinr_txt);
                    ArrayAdapter<CharSequence> sociAdapter = ArrayAdapter
                            .createFromResource(FrdsList.this,
                                    R.array.societyNameArray,
                                    R.layout.spinr_txt);
                    ArrayAdapter<CharSequence> buildingAdapter = ArrayAdapter
                            .createFromResource(FrdsList.this,
                                    R.array.buildingNameArray,
                                    R.layout.spinr_txt);

                    avenueAdapter
                            .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    buildingAdapter
                            .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sociAdapter
                            .setDropDownViewResource(R.layout.multiline_spinner_dropdown_item);

                    societyNameSpinner
                            .setAdapter(new NothingSelectedSpinnerAdapter1(
                                    sociAdapter,
                                    R.layout.society_spinner_nothing_selected,
                                    FrdsList.this));

                    buildingSpinner
                            .setAdapter(new NothingSelectedSpinnerAdapter1(
                                    buildingAdapter,
                                    R.layout.building_spinner_nothing_selected,
                                    FrdsList.this));
                    // load data from server

                    avenueSpinner
                            .setAdapter(new NothingSelectedSpinnerAdapter1(
                                    associtationAdapter,
                                    R.layout.avenue_spinner_nothing_selected,
                                    FrdsList.this));
                    societyNameSpinner.setEnabled(true);

                    avenueSpinner
                            .setOnItemSelectedListener(new OnItemSelectedListener() {

                                @Override
                                public void onItemSelected(AdapterView<?> arg0,
                                                           View arg1, int arg2, long arg3) {
                                    // TODO Auto-generated method stub
                                    avenueName = String.valueOf(avenueSpinner
                                            .getSelectedItem());
                                    System.out
                                            .println(societyMap
                                                    + "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

                                    ((TextView) avenueSpinner.getSelectedView())
                                            .setTextColor(getResources()
                                                    .getColor(R.color.black));

                                    if (avenueName == null
                                            || avenueName.equals("null")) {
                                        System.out
                                                .println("1st TIME NULLLLLLLLLLLLLLLLLL");
                                        societyNameSpinner.setEnabled(false);
                                        buildingSpinner.setEnabled(false);

                                    } else {
                                        List<String> societylist = societyMap
                                                .get(avenueName);
                                        ArrayAdapter<String> sociAdapter = new ArrayAdapter<String>(
                                                FrdsList.this,
                                                android.R.layout.simple_list_item_1,
                                                societylist);

                                        sociAdapter
                                                .setDropDownViewResource(R.layout.multiline_spinner_dropdown_item);
                                        societyNameSpinner
                                                .setAdapter(new NothingSelectedSpinnerAdapter1(
                                                        sociAdapter,
                                                        R.layout.society_spinner_nothing_selected,
                                                        FrdsList.this));

                                        ArrayAdapter<CharSequence> buildingAdapter = ArrayAdapter
                                                .createFromResource(
                                                        FrdsList.this,
                                                        R.array.buildingNameArray,
                                                        R.layout.spinr_txt);

                                        buildingSpinner
                                                .setAdapter(new NothingSelectedSpinnerAdapter1(
                                                        buildingAdapter,
                                                        R.layout.building_spinner_nothing_selected,
                                                        FrdsList.this));

                                        societyNameSpinner.setEnabled(true);
                                    }

                                }

                                @Override
                                public void onNothingSelected(
                                        AdapterView<?> arg0) {
                                    // TODO Auto-generated method stub

                                }
                            });

                    societyNameSpinner
                            .setOnItemSelectedListener(new OnItemSelectedListener() {

                                @Override
                                public void onItemSelected(AdapterView<?> arg0,
                                                           View arg1, int pos, long arg3) {
                                    // TODO Auto-generated method stub
                                    societyName = String
                                            .valueOf(societyNameSpinner
                                                    .getSelectedItem());

                                    ((TextView) societyNameSpinner
                                            .getSelectedView())
                                            .setTextColor(getResources()
                                                    .getColor(R.color.black));
                                    System.out
                                            .println(buildingMap
                                                    + "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

                                    if (societyName == null
                                            || societyName.equals("null")) {
                                        System.out
                                                .println("1st TIME NULLLLLLLLLLLLLLLLLL");
                                    } else {
                                        List<String> buildinglist = buildingMap
                                                .get(societyName);
                                        ArrayAdapter<String> buildAdapter = new ArrayAdapter<String>(
                                                FrdsList.this,
                                                android.R.layout.simple_list_item_1,
                                                buildinglist);

                                        buildAdapter
                                                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        buildingSpinner
                                                .setAdapter(new NothingSelectedSpinnerAdapter1(
                                                        buildAdapter,
                                                        R.layout.building_spinner_nothing_selected,
                                                        FrdsList.this));
                                        buildingSpinner.setEnabled(true);
                                    }
                                    // System.out.println(buildingMap);

                                    System.out.println(societyMap);

                                    List<String> societyNames = societyMap
                                            .get(societyName);

                                }

                                @Override
                                public void onNothingSelected(
                                        AdapterView<?> arg0) {
                                    // TODO Auto-generated method stub

                                }
                            });
                    buildingSpinner
                            .setOnItemSelectedListener(new OnItemSelectedListener() {

                                @Override
                                public void onItemSelected(AdapterView<?> arg0,
                                                           View arg1, int arg2, long arg3) {
                                    // TODO Auto-generated method stub
                                    ((TextView) buildingSpinner
                                            .getSelectedView())
                                            .setTextColor(getResources()
                                                    .getColor(R.color.black));
                                    buildingNum = String
                                            .valueOf(buildingSpinner
                                                    .getSelectedItem());

                                    flatNoEdit.setCursorVisible(false);

                                }

                                @Override
                                public void onNothingSelected(
                                        AdapterView<?> arg0) {
                                    // TODO Auto-generated method stub
                                }
                            });
                    flatNoEdit.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            flatNoEdit.setCursorVisible(true);
                        }
                    });
                    m_dialog.show();
                }
            }
        });

        listviewIdeas.setOnItemClickListener(new OnItemClickListener() {

            @SuppressWarnings("unchecked")
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                Intent i = new Intent(FrdsList.this, FrdsDetail.class);
                i.putExtra("jsonDetails", gson.toJson(globalUserListPojo.get(position)));

                startActivity(i);
            }
        });

        //new ListFrds().execute();
        new SocietyNamees().execute();
    }
    private void callFrdsList() {
        String host = MyApplication.HOSTNAME + "search_users.json?limit=" +  "&limit=" + limit +"&offset="+offset+"&search_key="+searchStr;
        host = host.replaceAll(" ", "%20");
        JsonArrayRequest request = new JsonArrayRequest(JsonRequest.Method.GET, host,
                (String) null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                //    pDialog.hide();
                jsonResultArry=response;
                Log.d("Reponse", response.toString());

                if (jsonResultArry != null) {
                    if (jsonResultArry.length() == 0
                            || jsonResultArry.toString() == "[]"
                            || jsonResultArry.toString() == ""
                            || jsonResultArry.toString().equals("")) {
                        pDialog.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        showSnack(FrdsList.this, "Oops! There is no Friends!", "OK");
                        userListPojo.clear();
                        adapterfrds.notifyDataSetChanged();
                        listviewIdeas.setAdapter(adapterfrds);
                    } else {
                        userListPojo = gson.fromJson(jsonResultArry.toString(), new TypeToken<List<UserDetailsPojo>>() {
                        }.getType());

                        globalUserListPojo.addAll(userListPojo);
                        Log.d("SIZE", globalUserListPojo.size() + "");
                        currentPosition = listviewIdeas
                                .getLastVisiblePosition();

                        adapterfrds = new ListFreindsBaseAdapter(
                                FrdsList.this, globalUserListPojo);
                        listviewIdeas.setAdapter(adapterfrds);

                        pDialogBtm.setVisibility(View.GONE);

                        pDialog.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        db.addEventNews(jsonResultArry, eventName);
                        //for updating new data
                        db.updateEventNews(response, eventName);

                        DisplayMetrics displayMetrics =
                                getResources().getDisplayMetrics();
                        int height = displayMetrics.heightPixels;

                        listviewIdeas.setSelectionFromTop(
                                currentPosition + 1, height - 220);
                        listviewIdeas.smoothScrollToPosition(0);
                        listviewIdeas
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
                                        if (view.getId() == listviewIdeas
                                                .getId()) {
                                            final int currentFirstVisibleItem = listviewIdeas
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
                                        pDialogBtm.setVisibility(View.GONE);

                                    }

                                    private void isScrollCompleted() {
                                        pDialogBtm.setVisibility(View.VISIBLE);
                                        if (this.currentVisibleItemCount > 0
                                                && this.currentScrollState == SCROLL_STATE_IDLE
                                                && this.totalItemCount == (currentFirstVisibleItem + currentVisibleItemCount)) {
                                            offset = offset + 15;
                                            callFrdsList();

                                        }
                                        pDialogBtm.setVisibility(View.GONE);
                                    }
                                });
                    }
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();

                Log.d("Error = ", volleyError.toString());
                pDialog.setVisibility(View.GONE);
                showSnack(FrdsList.this,
                        "Oops! Something went wrong. Please check internet connection!",
                        "OK");
            }
        });
        MyApplication.queue.add(request);

    }


    public class ListFreindsBaseAdapter extends BaseAdapter {
        ArrayList<HashMap<String, String>> list;
        List<UserDetailsPojo> userListPojo;
        private LayoutInflater inflator;
        private Context context;

        public ListFreindsBaseAdapter(Activity activity, List<UserDetailsPojo> userListPojos) {
            // TODO Auto-generated constructor stub
            this.context = activity;
            this.userListPojo = userListPojos;
            inflator = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return userListPojo.size();
        }

        @Override
        public Object getItem(int pos) {
            // TODO Auto-generated method stub
            return userListPojo.get(pos);
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
                convertView = inflator.inflate(R.layout.friends_list_row,
                        parent, false);
                holder = new ViewHolder();
                holder.unameTxt = (TextView) convertView
                        .findViewById(R.id.titleTxt);
                holder.avaneuNameTxt = (TextView) convertView
                        .findViewById(R.id.avaneuNameTxt);

                holder.unameTxt.setTypeface(typefaceLight);
                holder.avaneuNameTxt.setTypeface(typefaceLight);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.unameTxt.setText(userListPojo.get(position).getUsername());
            Log.d("POS :",position+"");
            holder.avaneuNameTxt.setText(userListPojo.get(position).getGclife_registration_flatdetails().get(0).getAvenue_name()
                    + "," + userListPojo.get(position).getGclife_registration_flatdetails().get(0).getBuildingid() + ","
                    + userListPojo.get(position).getGclife_registration_flatdetails().get(0).getFlat_number());

            return convertView;
        }

        public class ViewHolder {
            TextView unameTxt, avaneuNameTxt;
        }
    }

    public class SocietyNamees extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub
            try {
                jsonResultArry = SocietyNameGet.callSocietyList(MyApplication.HOSTNAME);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {

            System.out.println(jsonResultArry
                    + "LIST OF MEMS !!!!!!!!!!!!!!!!!!!!");
            if (jsonResultArry != null) {
                try {
                    ArrayList<String> listAssociation = new ArrayList<String>();
                    for (int i = 0; i < jsonResultArry.length(); i++) {
                        ArrayList<String> listSociety = new ArrayList<String>();
                        JSONObject json = jsonResultArry.getJSONObject(i);

                        String associationName = json
                                .getString("associationname");

                        listAssociation.add(associationName);

                        JSONArray societyArray;
                        societyArray = json.getJSONArray("society_masters");
                        ArrayList<String> listSocietydata = new ArrayList<String>();
                        ArrayList<String> listbuilddata = null;

                        System.out.println(societyArray);
                        final int numberOfItemsInResp = societyArray.length();
                        for (int j = 0; j < numberOfItemsInResp; j++) {

                            JSONObject societyjson = societyArray
                                    .getJSONObject(j);

                            String societyName = societyjson
                                    .getString("societyname");

                            listSociety.add(societyName);
                            listSocietydata.add(societyArray.get(j).toString());

                            System.out.println(listSociety);

                            System.out.println(listSocietydata);

                            JSONArray buildingArray;
                            buildingArray = societyjson
                                    .getJSONArray("building_masters");
                            listbuilddata = new ArrayList<String>();
                            for (int k = 0; k < buildingArray.length(); k++) {
                                JSONObject jsonBuild = buildingArray
                                        .getJSONObject(k);
                                String buildingName = jsonBuild
                                        .getString("buildinname");
                                BigDecimal number = new BigDecimal(buildingName);
                                buildingName = number.stripTrailingZeros()
                                        .toPlainString();

                                listbuilddata.add(buildingName);

                                // listbuilddata.add(buildingArray.get(k).toString());
                            }
                            List<String> buildList = new ArrayList<String>(
                                    listbuilddata);
                            buildingMap.put(societyName, buildList);

                            System.out.println(buildingMap);
                        }
                        // ArrayList<String> societyList = new
                        // ArrayList<String>(listbuilddata);
                        List<String> associationList = new ArrayList<String>(
                                listSociety);
                        societyMap.put(associationName, associationList);

                    }
                    associtationAdapter = new ArrayAdapter<String>(
                            FrdsList.this, android.R.layout.simple_list_item_1,
                            listAssociation);

                    associtationAdapter
                            .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            } else {
                showSnack(FrdsList.this,
                        "Oops! Something went wrong. Please check internet connection!",
                        "OK");
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                overridePendingTransition(R.anim.slide_right_in,
                        R.anim.slide_out_right);
                return true;
            case R.id.search:
                Toast.makeText(getApplicationContext(),"Type atleast three characters",Toast.LENGTH_LONG).show();
                searchLay.setVisibility(View.VISIBLE);
                searchEdit.requestFocus();
                searchEdit.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 0, 0, 0));
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    void showSnack(FrdsList flats, String stringMsg, String ok) {
        new SnackBar(FrdsList.this, stringMsg, ok, new OnClickListener() {

            @Override
            public void onClick(View v) {
            }
        }).show();
    }



}