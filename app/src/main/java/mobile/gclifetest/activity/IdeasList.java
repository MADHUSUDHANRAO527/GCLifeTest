package mobile.gclifetest.activity;

import android.annotation.SuppressLint;
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
import android.widget.BaseAdapter;
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
import com.gc.materialdesign.widgets.SnackBar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import mobile.gclifetest.materialDesign.ProgressBarCircularIndeterminate;
import mobile.gclifetest.pojoGson.EventsPojo;
import mobile.gclifetest.pojoGson.FlatDetailsPojo;
import mobile.gclifetest.pojoGson.UserDetailsPojo;
import mobile.gclifetest.utils.InternetConnectionDetector;
import mobile.gclifetest.utils.MyApplication;
import mobile.gclifetest.db.DatabaseHandler;
import mobile.gclifetest.http.EvenstPost;
import mobile.gclifetest.http.MemsPost;

public class IdeasList extends BaseActivity {
    ButtonFloat addBtn;
    ProgressBarCircularIndeterminate pDialog, pDialogBtm;
    InternetConnectionDetector netConn;
    UserDetailsPojo user;
    Boolean isInternetPresent = false;
    String eventName, eid, deleteEveId;
    ListView listviewIdeas;
    JSONArray jsonResultArry;
    SharedPreferences userPref;
    List<FlatDetailsPojo> flatsList = new ArrayList<FlatDetailsPojo>();
    List<EventsPojo> eventsPojo;
    List<EventsPojo> globalEventsPojo = new ArrayList<>();
    FlatDetailsPojo flats;
    Typeface typefaceLight;
    JSONObject jsonLike, jsonDelete;
    ListIdeasBaseAdapter adapter;
    Dialog m_dialog;
    Runnable run;
    RelativeLayout searchLay;
    EditText searchEdit;
    Gson gson;
    DatabaseHandler db = new DatabaseHandler(this);
    SwipeRefreshLayout mSwipeRefreshLayout;
    boolean progressShow = true;
    ProgressBar progressBar;
    String searchStr = "";
    ImageView clearImg;
    int limit = 10, currentPosition, offset = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ideas_list);
        addBtn = (ButtonFloat) findViewById(R.id.addBtn);
        pDialog = (ProgressBarCircularIndeterminate) findViewById(R.id.pDialog);
        pDialogBtm = (ProgressBarCircularIndeterminate) findViewById(R.id.pDialogBtm);
        listviewIdeas = (ListView) findViewById(R.id.listview);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
        searchEdit = (EditText) findViewById(R.id.searchEdit);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        searchLay = (RelativeLayout) findViewById(R.id.searchLay);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.orange,
                R.color.green, R.color.blue);
        clearImg = (ImageView) findViewById(R.id.clearImg);
        typefaceLight = Typeface.createFromAsset(getAssets(),
                "fonts/RobotoLight.ttf");
        userPref = getSharedPreferences("USER", MODE_PRIVATE);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        eventName = bundle.getString("EventName");
        if (eventName == "Ideas" || eventName.equals("Ideas")) {
            setUpActionBar("Ideas");
        } else if (eventName == "News" || eventName.equals("News")) {
            setUpActionBar("News");
        } else {
            setUpActionBar("NoticeBoard");
        }
        gson = new Gson();
        String jsonUser = userPref.getString("USER_DATA", "NV");
        user = gson.fromJson(jsonUser, UserDetailsPojo.class);
        flatsList = user.getGclife_registration_flatdetails();
        flats = flatsList.get(0);
        if (db.getEventNews(eventName) != "null") {
            Log.d("DB NOT NULL: " + eventName, db.getEventNews(eventName));
            pDialog.setVisibility(View.GONE);
            eventsPojo = gson.fromJson(db.getEventNews(eventName), new TypeToken<List<EventsPojo>>() {
            }.getType());
            adapter = new ListIdeasBaseAdapter(
                    IdeasList.this, eventsPojo);
            listviewIdeas.setAdapter(adapter);
            progressShow = false;
            callEventsList(searchStr);
        } else {
            callEventsList(searchStr);
            Log.d("DB NULL: " + eventName, "");
        }
        listviewIdeas.setOnItemClickListener(new OnItemClickListener() {

            @SuppressWarnings("unchecked")
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                Intent i = new Intent(IdeasList.this, IdeasDetail.class);
                i.putExtra("EventName", eventName);
                i.putExtra("id", String.valueOf(eventsPojo.get(position).getId()));
                startActivity(i);
            }
        });
        addBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent i = new Intent(IdeasList.this, IdeasCreate.class);
                i.putExtra("EventName", eventName);
                startActivity(i);
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
                                runOnUiThread(run);
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
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(searchEdit.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });
        clearImg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                globalEventsPojo = new ArrayList<EventsPojo>();
                offset = 0;
                callEventsList("");
                searchEdit.setText("");
            }
        });
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
                    adapter = new ListIdeasBaseAdapter(
                            IdeasList.this, globalEventsPojo);
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
                    showSnack(IdeasList.this,
                            "Oops! There is no " + eventName + "!", "");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();

                Log.d("Error = ", volleyError.toString());

                pDialog.setVisibility(View.GONE);
                showSnack(IdeasList.this,
                        "Oops! Something went wrong. Please check internet connection!",
                        "OK");
            }
        });
        MyApplication.queue.add(request);


    }


    public class ListIdeasBaseAdapter extends BaseAdapter {
        List<EventsPojo> eventsPojos = new ArrayList<EventsPojo>();
        private LayoutInflater inflator;
        private Context context;
        ArrayList<String> likeCheckArr = new ArrayList<String>();

        public ListIdeasBaseAdapter(Activity activity,
                                    List<EventsPojo> eventsPojo) {
            // TODO Auto-generated constructor stub
            this.context = activity;
            this.eventsPojos = eventsPojo;
            inflator = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return eventsPojos.size();
        }

        @Override
        public Object getItem(int pos) {
            // TODO Auto-generated method stub
            return eventsPojos.get(pos);
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
            final ViewHolder holder;
            if (convertView == null) {
                convertView = inflator.inflate(R.layout.idea_list_row, parent,
                        false);
                holder = new ViewHolder();
                holder.titleTxt = (TextView) convertView
                        .findViewById(R.id.titleTxt);
                holder.sDiscTxt = (TextView) convertView
                        .findViewById(R.id.sDiscTxt);
                holder.attchCountTxt = (TextView) convertView
                        .findViewById(R.id.attchcountTxt);
                holder.comntCountTxt = (TextView) convertView
                        .findViewById(R.id.cmntcountTxt);
                holder.likesCountTxt = (TextView) convertView
                        .findViewById(R.id.likecountTxt);
                holder.detailClick = (RelativeLayout) convertView
                        .findViewById(R.id.detailClick);
                holder.shareImg = (ImageView) convertView
                        .findViewById(R.id.shareImg);
                holder.likeImg = (ImageView) convertView
                        .findViewById(R.id.likeImg);
                holder.deleteImg = (ImageView) convertView
                        .findViewById(R.id.deleteImg);

                holder.titleTxt.setTypeface(typefaceLight);
                holder.sDiscTxt.setTypeface(typefaceLight);
                holder.attchCountTxt.setTypeface(typefaceLight);
                holder.comntCountTxt.setTypeface(typefaceLight);
                holder.likesCountTxt.setTypeface(typefaceLight);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.titleTxt.setText(eventsPojos.get(position).getTitle());
            holder.sDiscTxt.setText(eventsPojos.get(position).getSdesc());
            if (eventsPojos.get(position).getEvent_images().size() > 0) {
                holder.attchCountTxt.setText(String.valueOf(eventsPojos.get(position).getEvent_images().size()));
            }
            if (eventsPojos.get(position).getEvent_comments().size() > 0) {
                holder.comntCountTxt.setText(String.valueOf(eventsPojos.get(position).getEvent_comments().size()));
            }
            eid = String.valueOf(eventsPojos.get(position).getId());
            if (eventsPojos.get(position).getEvent_likes().size() > 0) {
                holder.likesCountTxt.setText(String.valueOf(eventsPojos.get(position).getEvent_likes().size()));
                likeCheckArr.add(String.valueOf(eventsPojos.get(position).getEvent_likes().get(0).getId()));
            }

            holder.detailClick.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    Intent i = new Intent(IdeasList.this, IdeasDetail.class);
                    i.putExtra("EventName", eventName);
                    i.putExtra("id", String.valueOf(eventsPojos.get(position).getId()));
                    startActivity(i);
                }
            });
            if (String.valueOf(eventsPojos.get(position).getUser_id()) == userPref.getString("USERID", "NV") || String.valueOf(eventsPojos.get(position).getUser_id()).equals(userPref.getString("USERID", "NV"))) {
                holder.deleteImg.setVisibility(View.VISIBLE);
            } else if (flats.getMember_type() == "Treasurer" || flats.getMember_type().equals("Treasurer")) {
                holder.deleteImg.setVisibility(View.VISIBLE);
            } else {
                holder.deleteImg.setVisibility(View.GONE);
            }
            holder.deleteImg.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    // to remove row

                    m_dialog = new Dialog(context);
                    m_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    m_dialog.setContentView(R.layout.areyousuredelepopup);
                    m_dialog.getWindow().getAttributes().windowAnimations = R.style.popup_login_dialog_animation;

                    TextView noTxt = (TextView) m_dialog.findViewById(R.id.noTxt);
                    TextView yesTxt = (TextView) m_dialog.findViewById(R.id.yesTxt);
                    noTxt.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            m_dialog.dismiss();
                        }
                    });
                    yesTxt.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            deleteEveId = String.valueOf(eventsPojos.get(position).getId());
                            System.out.println(deleteEveId + " !!!!!!!!!!!!!!!!!!!" + String.valueOf(eventsPojos.get(position).getId()));
                            new DeleteEvent().execute();
                            eventsPojos.remove(position);
                            m_dialog.dismiss();
                        }
                    });

                    m_dialog.show();
                }
            });
            holder.shareImg.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    Log.d("SHARE CONTENT", eventsPojos.get(position).getTitle() + " " + eventsPojos.get(position).getSdesc() + "\n"
                            + eventsPojos.get(position).getBdesc());
                    Intent sharingIntent = new Intent(
                            Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    sharingIntent.putExtra(
                            Intent.EXTRA_SUBJECT,
                            eventsPojos.get(position).getTitle());
                    sharingIntent.putExtra(
                            Intent.EXTRA_TEXT,
                            eventsPojos.get(position).getTitle() + "\n" + eventsPojos.get(position).getSdesc() + "\n"
                                    + eventsPojos.get(position).getBdesc());
                    startActivity(Intent.createChooser(sharingIntent,
                            "Share via"));
                }
            });
            if (eventsPojos.get(position).getEvent_likes().size() > 0) {
                if (likeCheckArr.contains(String.valueOf(eventsPojos.get(position).getEvent_likes().get(0).getId()))) {
                    holder.likeImg.setImageResource(R.drawable.liked);
                } else {
                    holder.likeImg.setImageResource(R.drawable.unlike);
                }
            }

            holder.likeImg.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub

                    if (eventsPojos.get(position).getEvent_likes().isEmpty() || eventsPojos.get(position).getEvent_likes().toString() == "[]") {
                        eid = String.valueOf(eventsPojos.get(position).getId());
                        int likes = Integer.valueOf(String.valueOf(eventsPojos.get(position).getEvent_likes().size()));
                        int lik = likes + 1;
                        holder.likesCountTxt.setText(String.valueOf(lik));
                        holder.likeImg.setImageResource(R.drawable.liked);
                        LikeUnlike();
                    } else {
                        if (likeCheckArr.contains(String.valueOf(eventsPojos.get(position).getEvent_likes().get(0).getId()))) {
                            showSnack(IdeasList.this, "Oops! You already liked it!",
                                    "OK");
                        }
                    }

                }
            });
            return convertView;
        }


        protected void LikeUnlike() {
            // TODO Auto-generated method stub
            new Like().execute();
        }

        public class ViewHolder {
            TextView titleTxt, sDiscTxt, attchCountTxt, comntCountTxt,
                    likesCountTxt;
            ImageView shareImg, likeImg, deleteImg;
            RelativeLayout detailClick;
        }
    }

    private class Like extends AsyncTask<Void, Void, Void> {
        @SuppressLint("InlinedApi")
        protected void onPreExecute() {
            // pDialog.setVisibility(View.VISIBLE);

        }

        @Override
        protected Void doInBackground(Void... params) {
            JSONObject jsonLik = new JSONObject();
            try {
                JSONObject json = new JSONObject();
                json.put("event_id", eid);
                json.put("user_id", userPref.getString("USERID", "NV"));
                jsonLik.put("event_like", json);
                jsonLike = MemsPost.PostLike(jsonLik, MyApplication.HOSTNAME);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            System.out.println(jsonLike
                    + "^^^^^^^^^^^^ RESULT OF LIKES ^^^^^^^^^^^^^^");

            if (jsonLike != null) {

            } else {
                showSnack(IdeasList.this,
                        "Oops! Something went wrong. Please check internet connection!",
                        "OK");
            }
        }
    }

    public class DeleteEvent extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            //	pDialogPop.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub
            try {
                System.out.println(deleteEveId + " **************");
                jsonDelete = EvenstPost.makeDelete(MyApplication.HOSTNAME, deleteEveId, "events");
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            showSnack(IdeasList.this,
                    "Deleted!",
                    "OK");
            adapter.notifyDataSetChanged();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.search:
                offset = 0;
                globalEventsPojo = new ArrayList<>();
                searchLay.setVisibility(View.VISIBLE);
                searchEdit.requestFocus();
                searchEdit.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 0, 0, 0));
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed() {
        Intent soc = new Intent(IdeasList.this, HomeApp.class);
        soc.putExtra("EventName", eventName);
        startActivity(soc);
    }

    void showSnack(IdeasList flats, String stringMsg, String ok) {
        new SnackBar(IdeasList.this, stringMsg, ok, new OnClickListener() {

            @Override
            public void onClick(View v) {
            }
        }).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search, menu);

        return true;
    }

}
