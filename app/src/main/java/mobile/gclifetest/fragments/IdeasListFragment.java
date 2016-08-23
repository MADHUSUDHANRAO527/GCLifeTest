package mobile.gclifetest.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonRequest;
import com.gc.materialdesign.views.ButtonFloat;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import mobile.gclifetest.MaterialDesign.ProgressBarCircularIndeterminate;
import mobile.gclifetest.PojoGson.EventsPojo;
import mobile.gclifetest.PojoGson.FlatDetailsPojo;
import mobile.gclifetest.PojoGson.UserDetailsPojo;
import mobile.gclifetest.Utils.Constants;
import mobile.gclifetest.Utils.InternetConnectionDetector;
import mobile.gclifetest.Utils.MyApplication;
import mobile.gclifetest.activity.HomeActivity;
import mobile.gclifetest.activity.R;
import mobile.gclifetest.adapters.ListIdeasAdapter;
import mobile.gclifetest.db.DatabaseHandler;

/**
 * Created by MRaoKorni on 8/1/2016.
 */
public class IdeasListFragment extends Fragment {
    Context context;
    ButtonFloat addBtn;
    InternetConnectionDetector netConn;
    UserDetailsPojo user;
    Boolean isInternetPresent = false;
    String eventName, eid, deleteEveId;
    ListView listviewIdeas;
    JSONArray jsonResultArry;
    SharedPreferences userPref;
    List<FlatDetailsPojo> flatsList = new ArrayList<FlatDetailsPojo>();
    List<EventsPojo> eventsPojo;
    FlatDetailsPojo flats;
    Typeface typefaceLight;
    JSONObject jsonLike, jsonDelete;
    ListIdeasAdapter adapter;
    Dialog m_dialog;
    Runnable run;
    RelativeLayout searchLay;
    EditText searchEdit;
    Gson gson;
    SwipeRefreshLayout mSwipeRefreshLayout;
    boolean progressShow = true;
    ProgressBar progressBar;
    String searchStr = "";
    ImageView clearImg;
    DatabaseHandler db;
    List<EventsPojo> globalEventsPojo = new ArrayList<>();
    int limit = 10, currentPosition, offset = 0;
    ProgressBarCircularIndeterminate pDialog, pDialogBtm;
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(
                R.layout.ideas_list, container, false);
        context = getActivity();
        addBtn = (ButtonFloat) v.findViewById(R.id.addBtn);
        pDialog = (ProgressBarCircularIndeterminate) v.findViewById(R.id.pDialog);
        listviewIdeas = (ListView) v.findViewById(R.id.listview);
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.activity_main_swipe_refresh_layout);
        searchEdit = (EditText) v.findViewById(R.id.searchEdit);
        progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        searchLay = (RelativeLayout) v.findViewById(R.id.searchLay);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.orange,
                R.color.green, R.color.blue);
        clearImg = (ImageView) v.findViewById(R.id.clearImg);
        pDialogBtm = (ProgressBarCircularIndeterminate)v.findViewById(R.id.pDialogBtm);
        userPref = context.getSharedPreferences("USER", context.MODE_PRIVATE);
        Bundle bundle = this.getArguments();
        eventName = bundle.getString("EventName");
        gson = new Gson();
        String jsonUser = userPref.getString("USER_DATA", "NV");
        user = gson.fromJson(jsonUser, UserDetailsPojo.class);
        flatsList = user.getGclife_registration_flatdetails();
        flats = flatsList.get(0);
        db = new DatabaseHandler(getActivity());
        if (db.getEventNews(eventName) != "null") {
            Log.d("DB NOT NULL: " + eventName, db.getEventNews(eventName));
            pDialog.setVisibility(View.GONE);
            eventsPojo = gson.fromJson(db.getEventNews(eventName), new TypeToken<List<EventsPojo>>() {
            }.getType());
            adapter = new ListIdeasAdapter(
                    context, eventsPojo, flats,eventName);
            listviewIdeas.setAdapter(adapter);
            progressShow = false;
            callEventsList(searchStr);
        } else {
            callEventsList(searchStr);
            Log.d("DB NULL: " + eventName, "");

        }
        listviewIdeas.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @SuppressWarnings("unchecked")
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                /*Intent i = new Intent(context, IdeasDetail.class);
                i.putExtra("EventName", eventName);
                i.putExtra("id", String.valueOf(eventsPojo.get(position).getId()));
                startActivity(i);*/

                IdeasDetailFragment fragment = new IdeasDetailFragment();
                Bundle bundle = new Bundle();
                bundle.putString("EventName", eventName);
                bundle.putString("id", String.valueOf(eventsPojo.get(position).getId()));
                fragment.setArguments(bundle);
                ((HomeActivity) context).addFragment(fragment);
            }
        });
        addBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                /*Intent i = new Intent(context, IdeasCreate.class);
                i.putExtra("EventName", eventName);
                startActivity(i);*/


                IdeasCreateFragment fragment = new IdeasCreateFragment();
                Bundle bundle = new Bundle();
                bundle.putString("EventName", eventName);
                fragment.setArguments(bundle);
                ((HomeActivity) context).addFragment(fragment);
            }
        });
        mSwipeRefreshLayout
                .setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                offset=0;
                                globalEventsPojo = new ArrayList<EventsPojo>();
                                callEventsList(searchStr);
                                getActivity().runOnUiThread(run);
                                mSwipeRefreshLayout
                                        .setRefreshing(false);
                            }
                        }, 2500);
                    }
                });
        run = new Runnable() {
            public void run() {
                if (eventsPojo.toString() == "[]" || eventsPojo.size() == 0) {

                } else {
                    adapter.notifyDataSetChanged();
                    listviewIdeas.invalidateViews();
                }
            }
        };
        searchEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    globalEventsPojo = new ArrayList<EventsPojo>();
                    offset = 0;
                    adapter.notifyDataSetChanged();
                    searchStr = searchEdit.getText().toString();
                    progressShow = false;
                    callEventsList(searchStr);
                    InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(searchEdit.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });
        clearImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                globalEventsPojo = new ArrayList<EventsPojo>();
                offset = 0;
                callEventsList("");
                searchEdit.setText("");
            }
        });
        return v;
    }

    private void callEventsList(final String searchStr) {

        //get method
        if (progressShow == true) {
            pDialog.setVisibility(View.VISIBLE);
        } else {
            pDialog.setVisibility(View.GONE);
        }
        String host = MyApplication.HOSTNAME + "events.json?user_id=" + userPref.getString("USERID", "NV")
                + "&event_type=" + eventName + "&society_master_name="
                + flats.getSocietyid() + "&association_name=" + flats.getAvenue_name() + "&limit=" + limit + "&offset=" + offset + "&search_text=" + searchStr;
        Log.d("HOSTNAME :", host.replaceAll(" ", "%20"));
        JsonArrayRequest request = new JsonArrayRequest(JsonRequest.Method.GET, host.replaceAll(" ", "%20"),
                (String) null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                //    pDialog.hide();
                Log.d("Response", response.toString());
                if (response != null) {
                    eventsPojo = gson.fromJson(response.toString(), new TypeToken<List<EventsPojo>>() {
                    }.getType());
                    globalEventsPojo.addAll(eventsPojo);
                    Log.d("SIZE", globalEventsPojo.size() + "");
                    currentPosition = listviewIdeas
                            .getLastVisiblePosition();
                    adapter = new ListIdeasAdapter(
                            context, eventsPojo, flats,eventName);
                    listviewIdeas.setAdapter(adapter);
                    pDialog.setVisibility(View.GONE);
                    pDialogBtm.setVisibility(View.GONE);
                    // Storing in DB
                    db.addEventNews(response, eventName);
                    //for updating new data
                    db.updateEventNews(response, eventName);


                    DisplayMetrics displayMetrics =
                            getResources().getDisplayMetrics();
                    int height = displayMetrics.heightPixels;

                    listviewIdeas.setSelectionFromTop(
                            currentPosition + 1, height - 220);

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

                                }

                                private void isScrollCompleted() {
                                    pDialogBtm.setVisibility(View.VISIBLE);
                                    if (this.currentVisibleItemCount > 0
                                            && this.currentScrollState == SCROLL_STATE_IDLE
                                            && this.totalItemCount == (currentFirstVisibleItem + currentVisibleItemCount)) {
                                        offset = offset + 10;
                                        Log.d("Offset :", offset + "");
                                        callEventsList(searchStr);
                                        pDialogBtm.setVisibility(View.GONE);
                                    }
                                }
                            });

                }
                if (response.toString() == "[]" || response.length() == 0) {
                    Constants.showSnack(context,
                            "Oops! There is no " + eventName + "!", "");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();

                Log.d("Error = ", volleyError.toString());

                pDialog.setVisibility(View.GONE);
                Constants.showSnack(context,
                        "Oops! Something went wrong. Please check internet connection!",
                        "OK");
            }
        });
        MyApplication.queue.add(request);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                ((HomeActivity) context).onBackpressed();
                return true;
            case R.id.search:
                searchLay.setVisibility(View.VISIBLE);
                searchEdit.requestFocus();
                searchEdit.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 0, 0, 0));
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
        ((HomeActivity) context).changeToolbarTitle(eventName);
    }
}
