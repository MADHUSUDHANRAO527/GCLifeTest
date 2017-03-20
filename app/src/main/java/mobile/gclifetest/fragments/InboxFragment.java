package mobile.gclifetest.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mobile.gclifetest.activity.InboxDetail;
import mobile.gclifetest.activity.R;
import mobile.gclifetest.db.DatabaseHandler;
import mobile.gclifetest.http.EvenstPost;
import mobile.gclifetest.materialDesign.ProgressBarCircularIndeterminate;
import mobile.gclifetest.pojoGson.InboxPojo;
import mobile.gclifetest.utils.Constants;
import mobile.gclifetest.utils.MyApplication;

/**
 * Created by MRaoKorni on 8/26/2016.
 */
public class InboxFragment extends Fragment {
    private int position;
    private static final String ARG_POSITION = "position";
    EditText subEdit, discriptionEdit;
    AutoCompleteTextView toEdit;
    String toStr, subjectStr, discStr, unamesList, dbNameRcv = "MAILRECV", dbNameSent = "MAILSENT";
    TextView sendTxt;
    ImageView searchImg;
    ListView listviewUsernames;
    Runnable run;
    JSONArray userNamesArr, sentMailsJArr;
    JSONObject msgJObj;
    Typeface typefaceLight;
    String hostname, mailid;
    ArrayList<String> listUnames = new ArrayList<String>();
    SharedPreferences userPref;
    ProgressBarCircularIndeterminate pDialog, pDialog1;
    ArrayList<String> selectedUnameList = new ArrayList<String>();
    ArrayList<HashMap<String, String>> listSent = new ArrayList<HashMap<String, String>>();
    String msgType;
    ListUnamesBaseAdapter adapterUnames;
    ListView listviewSent;
    DatabaseHandler db;
    SwipeRefreshLayout mSwipeRefreshLayout;
    ListSendMailBaseAdapter adapterSentRcv;
    AutoCompleteAdapter adapter;
    ArrayList<String> strngArr = new ArrayList<String>();
    JSONObject jsonDelete;
    List<InboxPojo> inboxPojo;
    Context mContext;
    Gson gson;
    View v;
    int limit = 10, currentPosition, offset = 0;

    public static InboxFragment newInstance(int position) {
        InboxFragment f = new InboxFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        position = getArguments().getInt(ARG_POSITION);
        mContext = getActivity();
    }

    @Override
    public View onCreateView(final LayoutInflater infaltor,
                             final ViewGroup container, Bundle savedInstanceState) {
        typefaceLight = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/RobotoLight.ttf");

        userPref = mContext.getSharedPreferences("USER", 0);
        gson = new Gson();
        db = new DatabaseHandler(mContext);


        if (position == 0) {
            v = infaltor.inflate(R.layout.inbox_write, container, false);

            toEdit = (AutoCompleteTextView) v.findViewById(R.id.toEdit);
            subEdit = (EditText) v.findViewById(R.id.fromEdit);
            discriptionEdit = (EditText) v.findViewById(R.id.discrpEdit);
            sendTxt = (TextView) v.findViewById(R.id.sendTxt);
            searchImg = (ImageView) v.findViewById(R.id.searchImg);
            pDialog1 = (ProgressBarCircularIndeterminate) v
                    .findViewById(R.id.pDialog);

            toEdit.setInputType(EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE);

            new ListUsernames().execute();

            sendTxt.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    toStr = toEdit.getText().toString();
                    System.out.println(toStr);
                    subjectStr = subEdit.getText().toString();
                    discStr = discriptionEdit.getText().toString();

                    if (toStr == "" || toStr.equals("") || toStr == null
                            || toStr == "null") {
                        Constants.showSnack(v, "Enter username!", "OK");
                    } else if (subjectStr == "" || subjectStr.equals("")
                            || subjectStr == null || subjectStr == "null") {
                        Constants.showSnack(v, "Enter subject!", "OK");
                    } else if (subjectStr.length() > 180) {
                        Constants.showSnack(v, "Subject length should not be more than 180!", "OK");
                    } else {

                        System.out
                                .println(selectedUnameList + "  " + toStr);
                        selectedUnameList.add(toStr);

                        List<String> myList = new ArrayList<String>(Arrays
                                .asList(toStr.split(",")));
                        System.out.println(myList.size()
                                + "    SIZE !!!!! ");
                        for (int i = 0; i < myList.size(); i++) {

                            String uname = myList.get(i);

                            if (listUnames.contains(uname)) {


                                //remove duplicates
                                // add elements to al, including duplicates
                                Set<String> hs = new HashSet<>();
                                hs.addAll(selectedUnameList);
                                selectedUnameList.clear();
                                selectedUnameList.addAll(hs);


                                System.out.println("AFTER REMOVING DUPLICATES : " + selectedUnameList);

                                unamesList = selectedUnameList.toString()
                                        .replaceAll("[\\[\\](){}]", "");
                                unamesList = unamesList.replaceAll("\\s",
                                        "");

                            } else {
                                Toast.makeText(mContext, uname
                                        + " is not registered!", Toast.LENGTH_SHORT).show();
                                break;
                            }
                            if (i < myList.size() - 1) {

                            } else {
                                new SendEmail().execute();
                            }

                        }
                    }
                }
            });
            toEdit.addTextChangedListener(new TextWatcher() {

                @Override
                public void afterTextChanged(Editable s) {
                    selectedUnameList.remove(s);
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start,
                                              int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start,
                                          int before, int count) {
                    if (s.length() == 0) {
                        strngArr = new ArrayList<String>();
                        selectedUnameList = new ArrayList<String>();
                    }
                }
            });

            toEdit.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    if (!strngArr.contains(adapter.getItem(position)
                            .toString())) {
                        strngArr.add(adapter.getItem(position).toString());
                    }
                    toEdit.setText(TextUtils.join(",", strngArr));
                    toEdit.setSelection(toEdit.getText().length());

                }
            });

            searchImg.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    final Dialog m_dialog = new Dialog(mContext);
                    m_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    m_dialog.setContentView(R.layout.imp_contacts_popup_listview);
                    m_dialog.getWindow().getAttributes().windowAnimations = R.style.popup_login_dialog_animation;
                    listviewUsernames = (ListView) m_dialog
                            .findViewById(R.id.listview);

                    adapterUnames = new ListUnamesBaseAdapter(
                            mContext, listUnames);
                    listviewUsernames.setAdapter(adapterUnames);

                    listviewUsernames
                            .setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                @SuppressWarnings("unchecked")
                                @Override
                                public void onItemClick(
                                        AdapterView<?> parent, View view,
                                        int position, long id) {
                                    // TODO Auto-generated method stub

                                    String uname = listUnames.get(position);

                                    if (!strngArr.contains(uname)) {
                                        strngArr.add(uname);
                                    }
                                    toEdit.setText(TextUtils.join(",",
                                            strngArr));
                                    toEdit.setSelection(toEdit.getText()
                                            .length());

                                    m_dialog.dismiss();
                                }
                            });

                    m_dialog.show();
                }
            });

        } else if (position == 1) {
            v = infaltor.inflate(R.layout.ideas_list, container, false);
            FloatingActionButton addBtn = (FloatingActionButton) v.findViewById(R.id.addBtn);
            pDialog = (ProgressBarCircularIndeterminate) v
                    .findViewById(R.id.pDialog);
            listviewSent = (ListView) v.findViewById(R.id.listview);
            mSwipeRefreshLayout = (SwipeRefreshLayout) v
                    .findViewById(R.id.activity_main_swipe_refresh_layout);

            mSwipeRefreshLayout.setColorSchemeResources(R.color.orange,
                    android.R.color.holo_green_dark, R.color.blue);

            listviewSent.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @SuppressWarnings("unchecked")
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    // TODO Auto-generated method stub

                    Intent i = new Intent(mContext, InboxDetail.class);
                    i.putExtra("position", position);
                    i.putExtra("mailType", msgType);
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

                                    listSent = new ArrayList<HashMap<String, String>>();
                                    callSentListMail();
                                    if (mContext != null) {
                                        getActivity().runOnUiThread(run);
                                        mSwipeRefreshLayout
                                                .setRefreshing(false);
                                    }
                                }
                            }, 2500);
                        }
                    });
            run = new Runnable() {
                public void run() {
                    // reload content
                    // list.clear();
                    if (listSent.toString() == "[]" || listSent.size() == 0) {

                    } else {
                        adapterSentRcv.notifyDataSetChanged();
                        listviewSent.invalidateViews();
                    }

                }
            };

            addBtn.setVisibility(View.GONE);
            msgType = "receive";

            if (db.getEventNews(dbNameRcv) != "null") {
                Log.d("DB NOT NULL: " + dbNameRcv, db.getEventNews(dbNameRcv));
                pDialog.setVisibility(View.GONE);
                inboxPojo = gson.fromJson(db.getEventNews(dbNameRcv), new TypeToken<List<InboxPojo>>() {
                }.getType());
                adapterSentRcv = new ListSendMailBaseAdapter(
                        mContext, inboxPojo);
                listviewSent.setAdapter(adapterSentRcv);
                pDialog.setVisibility(View.GONE);
                callSentListMail();

            } else {
                callSentListMail();
                Log.d("DB NULL: " + dbNameRcv, "");

            }


        } else {
            v = infaltor.inflate(R.layout.ideas_list, container, false);

            FloatingActionButton addBtn = (FloatingActionButton) v.findViewById(R.id.addBtn);
            pDialog = (ProgressBarCircularIndeterminate) v
                    .findViewById(R.id.pDialog);
            addBtn.setVisibility(View.GONE);
            listviewSent = (ListView) v.findViewById(R.id.listview);
            mSwipeRefreshLayout = (SwipeRefreshLayout) v
                    .findViewById(R.id.activity_main_swipe_refresh_layout);

            mSwipeRefreshLayout.setColorSchemeResources(R.color.orange,
                    android.R.color.holo_green_dark, R.color.blue);
            msgType = "sent";
            listviewSent.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @SuppressWarnings("unchecked")
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    // TODO Auto-generated method stub
                    Intent i = new Intent(mContext, InboxDetail.class);
                    i.putExtra("position", position);
                    i.putExtra("mailType", msgType);
                    startActivity(i);
                }
            });

            if (db.getEventNews(dbNameSent) != "null") {
                Log.d("DB NOT NULL: " + dbNameSent, db.getEventNews(dbNameSent));
                pDialog.setVisibility(View.GONE);
                inboxPojo = gson.fromJson(db.getEventNews(dbNameSent), new TypeToken<List<InboxPojo>>() {
                }.getType());
                adapterSentRcv = new ListSendMailBaseAdapter(
                        mContext, inboxPojo);
                listviewSent.setAdapter(adapterSentRcv);
                pDialog.setVisibility(View.GONE);
                //    progressShow = false;
                callSentListMail();

            } else {
                callSentListMail();
                Log.d("DB NULL: " + dbNameSent, "");

            }

            mSwipeRefreshLayout
                    .setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                        @Override
                        public void onRefresh() {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    listSent = new ArrayList<HashMap<String, String>>();
                                    callSentListMail();
                                    // setupAdapter();
                                    // adapter.notifyDataSetChanged();
                                    getActivity().runOnUiThread(run);
                                    mSwipeRefreshLayout
                                            .setRefreshing(false);
                                }
                            }, 2500);
                        }
                    });
            run = new Runnable() {
                public void run() {
                    // reload content
                    // list.clear();
                    if (listSent.toString() == "[]" || listSent.size() == 0) {

                    } else {
                        adapterSentRcv.notifyDataSetChanged();
                        listviewSent.invalidateViews();
                    }

                }
            };

        }

        return v;

    }

    public class ListUsernames extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                userNamesArr = EvenstPost.makeRequestUserNamesList(MyApplication.HOSTNAME);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onPostExecute(Void unused) {
            if (userNamesArr != null) {
                for (int i = 0; i < userNamesArr.length(); i++) {

                    try {
                        listUnames.add(userNamesArr.get(i).toString());
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
                if (mContext == null) {

                } else {
                    adapter = new AutoCompleteAdapter(mContext,
                            R.layout.auto_suggestion_row, R.id.autoSugg,
                            listUnames);
                    toEdit.setAdapter(adapter);// setting the adapter data
                    // into the
                    // AutoCompleteTextView
                    toEdit.setTextColor(Color.BLACK);
                    toEdit.setThreshold(1);
                }
            } else {
                Constants.showToast(mContext,
                        R.string.went_wrong);
            }
        }
    }

    public class AutoCompleteAdapter extends ArrayAdapter<String> implements
            Filterable {

        private ArrayList<String> fullList;
        private ArrayList<String> mOriginalValues;
        private ArrayFilter mFilter;

        public AutoCompleteAdapter(Context context, int resource,
                                   int autosugg, ArrayList<String> fullList) {

            super(context, resource, autosugg, fullList);
            this.fullList = fullList;
            mOriginalValues = new ArrayList<String>(fullList);

        }

        @Override
        public int getCount() {
            return fullList.size();
        }

        @Override
        public String getItem(int position) {
            return fullList.get(position);
        }

        @Override
        public Filter getFilter() {
            if (mFilter == null) {
                mFilter = new ArrayFilter();
            }
            return mFilter;
        }

        private class ArrayFilter extends Filter {
            private Object lock;

            @Override
            protected FilterResults performFiltering(CharSequence prefix) {
                String[] arr = prefix.toString().split(",");
                prefix = arr[arr.length - 1];

                FilterResults results = new FilterResults();

                if (mOriginalValues == null) {
                    synchronized (lock) {
                        mOriginalValues = new ArrayList<String>(fullList);
                    }
                }

                if (prefix == null || prefix.length() == 0) {
                    synchronized (lock) {
                        ArrayList<String> list = new ArrayList<String>(
                                mOriginalValues);
                        results.values = list;
                        results.count = list.size();
                    }
                } else {
                    final String prefixString = prefix.toString()
                            .toLowerCase();

                    ArrayList<String> values = mOriginalValues;
                    int count = values.size();

                    ArrayList<String> newValues = new ArrayList<String>(
                            count);

                    for (int i = 0; i < count; i++) {
                        String item = values.get(i);
                        if (item.toLowerCase().contains(prefixString)) {
                            newValues.add(item);
                        }

                    }

                    results.values = newValues;
                    results.count = newValues.size();
                }

                return results;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,
                                          FilterResults results) {

                if (results.values != null) {
                    fullList = (ArrayList<String>) results.values;
                } else {
                    fullList = new ArrayList<String>();
                }
                if (results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        }
    }

    public class ListUnamesBaseAdapter extends BaseAdapter {
        ArrayList<String> list;
        private LayoutInflater inflator;
        private Context context;

        public ListUnamesBaseAdapter(Context activity,
                                     ArrayList<String> listArticles) {
            // TODO Auto-generated constructor stub
            this.context = activity;
            this.list = listArticles;
            inflator = (LayoutInflater) activity
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
                convertView = inflator.inflate(R.layout.avenues_adapter_row,
                        parent, false);
                holder = new ViewHolder();
                holder.titleTxt = (TextView) convertView
                        .findViewById(R.id.titleTxt);
                holder.titleTxt.setTypeface(typefaceLight);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.titleTxt.setText(list.get(position));

            return convertView;
        }

        public class ViewHolder {
            TextView titleTxt;
        }
    }

    public class SendEmail extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            pDialog1.setVisibility(View.VISIBLE);
            sendTxt.setVisibility(View.INVISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            JSONObject jsonMsg = new JSONObject();
            try {
                JSONObject jsonMsgs = new JSONObject();

                jsonMsg.put("from_user_id",
                        userPref.getString("USERID", "NV"));
                jsonMsg.put("to_user_id", "2");
                jsonMsg.put("subject", Constants.encodeString(subjectStr));
                jsonMsg.put("message", Constants.encodeString(discStr));
                jsonMsg.put("read", "NO");

                jsonMsgs.put("message", jsonMsg);
                jsonMsgs.put("names", unamesList);
                msgJObj = EvenstPost.makeRequestForPostMsg(jsonMsgs,
                        MyApplication.HOSTNAME);

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
            if (msgJObj != null) {
                Constants.showToast(mContext,
                        R.string.mail_has_sent);
                subEdit.setText("");
                discriptionEdit.setText("");
                toEdit.setText("");
                pDialog1.setVisibility(View.GONE);
                sendTxt.setVisibility(View.VISIBLE);
            } else {
                sendTxt.setVisibility(View.VISIBLE);
                pDialog1.setVisibility(View.GONE);
                Constants.showToast(mContext,
                        R.string.went_wrong);
            }
        }
    }

    private void callSentListMail() {
        String hostt = MyApplication.HOSTNAME + "messages.json?user_id=" + userPref.getString("USERID", "NV") + "&type="
                + msgType + "&limit=" + limit + "&offset=" + offset;

        JsonArrayRequest request = new JsonArrayRequest(JsonRequest.Method.GET, hostt.replaceAll(" ", "%20"),
                (String) null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                //    pDialog.hide();

                Log.d("Reponse", response.toString());
                sentMailsJArr = response;
                if (response != null) {
                    inboxPojo = gson.fromJson(sentMailsJArr.toString(), new TypeToken<List<InboxPojo>>() {
                    }.getType());
                    if (sentMailsJArr.length() == 0
                            || sentMailsJArr.toString() == "[]"
                            || sentMailsJArr.toString() == ""
                            || sentMailsJArr.toString().equals("")) {
                        pDialog.setVisibility(View.GONE);

                    } else {
                        adapterSentRcv = new ListSendMailBaseAdapter(
                                mContext, inboxPojo);
                        listviewSent.setAdapter(adapterSentRcv);
                        pDialog.setVisibility(View.GONE);
                        if (msgType == "receive") {
                            db.addEventNews(sentMailsJArr, dbNameRcv);
                            //for updating new data
                            db.updateEventNews(response, dbNameRcv);
                        } else {
                            db.addEventNews(sentMailsJArr, dbNameSent);
                            //for updating new data
                            db.updateEventNews(response, dbNameSent);
                        }
                        DisplayMetrics displayMetrics =
                                getResources().getDisplayMetrics();
                        int height = displayMetrics.heightPixels;

                        listviewSent.setSelectionFromTop(
                                currentPosition + 1, height - 220);

                        listviewSent
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
                                        //      pDialogBtm.setVisibility(View.GONE);
                                        pDialog.setVisibility(View.GONE);
                                        if (view.getId() == listviewSent
                                                .getId()) {
                                            final int currentFirstVisibleItem = listviewSent
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
                                            Log.d("Offset :", offset + "");
                                            //   pDialogBtm.setVisibility(View.VISIBLE);
                                            callSentListMail();
                                        }
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
            }
        });
        MyApplication.queue.add(request);
    }

    public class ListSendMailBaseAdapter extends BaseAdapter {
        List<InboxPojo> inboxPojos;
        private LayoutInflater inflator;
        private Context context;

        public ListSendMailBaseAdapter(Context context2,
                                       List<InboxPojo> inboxPojo) {
            // TODO Auto-generated constructor stub
            this.context = context2;
            this.inboxPojos = inboxPojo;
            inflator = (LayoutInflater) context2
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return inboxPojos.size();
        }

        @Override
        public Object getItem(int pos) {
            // TODO Auto-generated method stub
            return inboxPojos.get(pos);
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
                convertView = inflator.inflate(R.layout.inbox_list, parent,
                        false);
                holder = new ViewHolder();
                holder.unameTxt = (TextView) convertView
                        .findViewById(R.id.unameTxt);
                holder.subjectTxt = (TextView) convertView
                        .findViewById(R.id.subjectTxt);
                holder.recvdDateTxt = (TextView) convertView
                        .findViewById(R.id.recvdDateTxt);
                holder.deleteImg = (ImageView) convertView
                        .findViewById(R.id.deleteImg);
                holder.fromTxt = (TextView) convertView
                        .findViewById(R.id.fromTxt);
                holder.unameTxt.setTypeface(typefaceLight);
                holder.subjectTxt.setTypeface(typefaceLight);
                holder.recvdDateTxt.setTypeface(typefaceLight);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            try {
                holder.subjectTxt.setText(Constants.decodeString(inboxPojos.get(position).getSubject()));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if (msgType == "sent" || msgType.equals("sent")) {
                holder.fromTxt.setText("To");
                holder.unameTxt.setText(inboxPojos.get(position).getReceiver_name());
            } else {
                holder.fromTxt.setText("From");
                holder.unameTxt.setText(inboxPojos.get(position).getSender_name());
            }
            holder.deleteImg.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    // to remove row

                    final Dialog m_dialog = new Dialog(context);
                    m_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    m_dialog.setContentView(R.layout.areyousuredelepopup);
                    m_dialog.getWindow().getAttributes().windowAnimations = R.style.popup_login_dialog_animation;

                    TextView noTxt = (TextView) m_dialog.findViewById(R.id.noTxt);
                    TextView yesTxt = (TextView) m_dialog.findViewById(R.id.yesTxt);
                    noTxt.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            m_dialog.dismiss();
                        }
                    });
                    yesTxt.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            mailid = String.valueOf(inboxPojos.get(position).getId());
                            new DeleteEvent().execute();
                            inboxPojos.remove(position);
                            m_dialog.dismiss();
                        }
                    });

                    m_dialog.show();
                }
            });
            String createdAt = inboxPojos.get(position)
                    .getCreated_at();
            MyApplication app = new MyApplication();
            createdAt = app.convertDateEmail(createdAt);
            String time = inboxPojos.get(position)
                    .getCreated_at().substring(11, 19);
            time = app.convertTimeEmail(time);

            holder.recvdDateTxt.setText(createdAt + "," + time);

            return convertView;
        }

        public class ViewHolder {
            TextView unameTxt, subjectTxt, recvdDateTxt, fromTxt;
            ImageView deleteImg;
        }
    }

    public class DeleteEvent extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub
            try {
                jsonDelete = EvenstPost.makeDeleteInbox(MyApplication.HOSTNAME, mailid, "messages", userPref.getString("USERID", "NV"));
                Log.d("DELETE RESULT : ", jsonDelete + "");
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.d("DELETE EXCEPTION : ", e + "");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            Constants.showToast(mContext,
                    R.string.deleted);
            adapterSentRcv.notifyDataSetChanged();
            callSentListMail();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        FlurryAgent.onStartSession(mContext.getApplicationContext(), Constants.flurryApiKey);
    }

    @Override
    public void onStop() {
        super.onStop();
        FlurryAgent.onEndSession(mContext.getApplicationContext());
    }
}
