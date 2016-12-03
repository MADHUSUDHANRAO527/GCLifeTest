package mobile.gclifetest.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mobile.gclifetest.activity.BaseActivity;
import mobile.gclifetest.activity.HomeActivity;
import mobile.gclifetest.activity.R;
import mobile.gclifetest.db.DatabaseHandler;
import mobile.gclifetest.event.AddIdeasEvent;
import mobile.gclifetest.http.EvenstPost;
import mobile.gclifetest.http.MemsPost;
import mobile.gclifetest.materialDesign.ProgressBarCircularIndeterminate;
import mobile.gclifetest.pojoGson.EventsPojo;
import mobile.gclifetest.pojoGson.FlatDetailsPojo;
import mobile.gclifetest.pojoGson.UserDetailsPojo;
import mobile.gclifetest.utils.Constants;
import mobile.gclifetest.utils.InternetConnectionDetector;
import mobile.gclifetest.utils.MyApplication;

/**
 * Created by MRaoKorni on 8/19/2016.
 */
public class PhotosVideosListFragment extends Fragment {
    Context context;
    FloatingActionButton addBtn;
    ProgressBarCircularIndeterminate pDialog, pDialogBtm;
    InternetConnectionDetector netConn;
    UserDetailsPojo user;
    Boolean isInternetPresent = false;
    String eventName, eid, deleteEveId;
    ListView listviewIdeas;
    JSONArray jsonResultArry;
    SharedPreferences userPref;
    List<FlatDetailsPojo> flatsList = new ArrayList<FlatDetailsPojo>();
    FlatDetailsPojo flats;
    Typeface typefaceLight;
    JSONObject jsonLike, jsonDelete;
    ListIdeasBaseAdapter adapter;
    DatabaseHandler db;
    List<EventsPojo> eventsPojo;
    Dialog m_dialog;
    Gson gson;
    SwipeRefreshLayout mSwipeRefreshLayout;
    List<EventsPojo> globalEventsPojo = new ArrayList<>();
    Runnable run;
    RelativeLayout searchLay;
    ProgressBar progressBar;
    boolean progressShow = true, imPagination = true;
    EditText searchEdit;
    String searchStr = "";
    ImageView clearImg;
    ImageLoader imageLoader;
    DisplayImageOptions options;
    int limit = 10, currentPosition, offset = 0;
    View v;
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(
                R.layout.ideas_list, container, false);
        context = getActivity();
        db = new DatabaseHandler(getActivity());
        addBtn = (FloatingActionButton) v.findViewById(R.id.addBtn);
        pDialog = (ProgressBarCircularIndeterminate) v.findViewById(R.id.pDialog);
        listviewIdeas = (ListView) v.findViewById(R.id.listview);
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.activity_main_swipe_refresh_layout);
        searchEdit = (EditText) v.findViewById(R.id.searchEdit);
        progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        pDialogBtm = (ProgressBarCircularIndeterminate) v.findViewById(R.id.pDialogBtm);
        searchLay = (RelativeLayout) v.findViewById(R.id.searchLay);
        clearImg = (ImageView) v.findViewById(R.id.clearImg);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.orange,
                android.R.color.holo_green_dark, R.color.blue);
        typefaceLight = Typeface.createFromAsset(context.getAssets(),
                "fonts/RobotoLight.ttf");
        userPref = context.getSharedPreferences("USER", context.MODE_PRIVATE);
        Bundle bundle = this.getArguments();
        eventName = bundle.getString("EventName");

        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).resetViewBeforeLoading(true)
                .showImageForEmptyUri(R.drawable.no_media)
                .showImageOnFail(R.drawable.no_media)
                .showImageOnLoading(R.drawable.no_media).build();

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
               /* Intent i=new Intent(getActivity(),YoutubeUpload.class);
                startActivity(i);*/
                PhotosCreateFragment fragment = new PhotosCreateFragment();
                Bundle bundle = new Bundle();
                bundle.putString("EventName", eventName);
                fragment.setArguments(bundle);
                ((HomeActivity) context).addFragment(fragment);
            }
        });
        gson = new Gson();
        String jsonUser = userPref.getString("USER_DATA", "NV");
        user = gson.fromJson(jsonUser, UserDetailsPojo.class);
        flatsList = user.getGclife_registration_flatdetails();
        flats = flatsList.get(0);

        listviewIdeas.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @SuppressWarnings("unchecked")
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                IdeasDetailFragment fragment = new IdeasDetailFragment();
                Bundle bundle = new Bundle();
                bundle.putString("EventName", eventName);
                bundle.putString("id", String.valueOf(eventsPojo.get(position).getId()));
                fragment.setArguments(bundle);
                ((HomeActivity) context).addFragment(fragment);
            }
        });
        if (db.getEventNews(eventName) != "null") {
            Log.d("DB NOT NULL: " + eventName, db.getEventNews(eventName));
            pDialog.setVisibility(View.GONE);
            eventsPojo = gson.fromJson(db.getEventNews(eventName), new TypeToken<List<EventsPojo>>() {
            }.getType());
            adapter = new ListIdeasBaseAdapter(
                    getActivity(), eventsPojo);
            listviewIdeas.setAdapter(adapter);
            progressShow = false;
            callEventsList(searchStr);
        } else {
            callEventsList(searchStr);
            Log.d("DB NULL: " + eventName, "");

        }
        mSwipeRefreshLayout
                .setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                offset = 0;
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
        searchEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (searchStr.length() >= 3) {
                        globalEventsPojo = new ArrayList<EventsPojo>();
                        offset = 0;
                        adapter.notifyDataSetChanged();
                        searchStr = searchEdit.getText().toString();
                        progressShow = false;
                        callEventsList(searchStr);
                        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(searchEdit.getWindowToken(), 0);
                    } else {
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
        if (progressShow) {
            pDialog.setVisibility(View.VISIBLE);
        } else {
            pDialog.setVisibility(View.GONE);
        }
        String host = MyApplication.HOSTNAME + "events.json?user_id=" + userPref.getString("USERID", "NV")
                + "&event_type=" + eventName + "&society_master_name="
                + flats.getSocietyid() + "&association_name=" + flats.getAvenue_name() + "&limit=" + limit + "&offset=" + offset + "&search_text=" + searchStr;
        JsonArrayRequest request = new JsonArrayRequest(JsonRequest.Method.GET, host.replaceAll(" ", "%20"),
                (String) null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                //    pDialog.hide();
                Log.d("Response", response.toString());
                if (response.toString() == "[]" || response.length() == 0) {
                    if (searchStr.length() > 0) {
                        Constants.showSnack(v,
                                "Search result not found, please try with another search criteria!", "");
                    } else {
                        Constants.showSnack(v,
                                "No further " + eventName + " content is available!", "");
                    }

                    if (imPagination) {
                        listviewIdeas.setAdapter(null);
                    }
                    pDialog.setVisibility(View.GONE);
                    pDialogBtm.setVisibility(View.INVISIBLE);
                } else if (response != null) {
                    eventsPojo = gson.fromJson(response.toString(), new TypeToken<List<EventsPojo>>() {
                    }.getType());
                    globalEventsPojo.addAll(eventsPojo);
                    Log.d("SIZE", globalEventsPojo.size() + "");
                    currentPosition = listviewIdeas
                            .getLastVisiblePosition();
                    adapter = new ListIdeasBaseAdapter(
                            getActivity(), globalEventsPojo);
                    listviewIdeas.setAdapter(adapter);
                    pDialog.setVisibility(View.GONE);
                    pDialogBtm.setVisibility(View.INVISIBLE);
                    // Storing in DB
                    db.addEventNews(response, eventName);
                    //for updating new data
                    db.updateEventNews(response, eventName);


                    DisplayMetrics displayMetrics =
                            getResources().getDisplayMetrics();
                    int height = displayMetrics.heightPixels;

                    listviewIdeas.setSelectionFromTop(
                            currentPosition, height - 220);

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
                                    pDialogBtm.setVisibility(View.INVISIBLE);
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
                                    pDialogBtm.setVisibility(View.INVISIBLE);
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
                                        progressShow = false;
                                        imPagination = false;
                                        callEventsList(searchStr);
                                        pDialogBtm.setVisibility(View.INVISIBLE);
                                    }
                                }
                            });
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();

                Log.d("Error = ", volleyError.toString());

                pDialog.setVisibility(View.GONE);
                Constants.showSnack(v,
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
                convertView = inflator.inflate(R.layout.photo_list_row, parent,
                        false);
                holder = new ViewHolder();
                holder.titleTxt = (TextView) convertView
                        .findViewById(R.id.titleTxt);
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
                holder.shareeImg = (ImageView) convertView
                        .findViewById(R.id.shareeImg);
                holder.likeImg = (ImageView) convertView
                        .findViewById(R.id.likeImg);
                holder.deleteImg = (ImageView) convertView
                        .findViewById(R.id.deleteImg);

                holder.titleTxt.setTypeface(typefaceLight);
                holder.attchCountTxt.setTypeface(typefaceLight);
                holder.comntCountTxt.setTypeface(typefaceLight);
                holder.likesCountTxt.setTypeface(typefaceLight);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.titleTxt.setText(eventsPojos.get(position).getTitle());
            if (eventsPojos.get(position).getEvent_images().size() > 0) {
                holder.attchCountTxt.setText(String.valueOf(eventsPojos.get(position).getEvent_images().size()));
            }
            if (eventsPojos.get(position).getEvent_comments().size() > 0) {
                holder.comntCountTxt.setText(String.valueOf(eventsPojos.get(position).getEvent_comments().size()));
            }
            eid = String.valueOf(eventsPojos.get(position).getId());
            final ArrayList<String> likeCheckArr = new ArrayList<String>();
            if (eventsPojos.get(position).getEvent_likes().size() > 0) {
                holder.likesCountTxt.setText(String.valueOf(eventsPojos.get(position).getEvent_likes().size()));
                for (int i = 0; i < eventsPojos.get(position).getEvent_likes().size(); i++) {
                    likeCheckArr.add(String.valueOf(eventsPojos.get(position).getEvent_likes().get(i).getUser_id()));
                }

            }
            if (eventsPojos.get(position).getEvent_images().size() > 0) {
                imageLoader.displayImage(eventsPojos.get(position).getEvent_images().get(0).getImage_url(), holder.shareeImg, options);
            }

            holder.detailClick.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    IdeasDetailFragment fragment = new IdeasDetailFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("EventName", eventName);
                    bundle.putString("id", String.valueOf(globalEventsPojo.get(position).getId()));
                    fragment.setArguments(bundle);
                    ((HomeActivity) context).addFragment(fragment);
                }
            });
            if (String.valueOf(eventsPojos.get(position).getUser_id()) == userPref.getString("USERID", "NV") || String.valueOf(eventsPojos.get(position).getUser_id()).equals(userPref.getString("USERID", "NV"))) {
                holder.deleteImg.setVisibility(View.VISIBLE);
            } else {
                holder.deleteImg.setVisibility(View.GONE);
            }
            holder.deleteImg.setOnClickListener(new View.OnClickListener() {

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
            holder.shareImg.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    onShareItem(v, holder.shareeImg);
                }
            });
            System.out.println(eventsPojos.get(position).getEvent_likes() + " !!!!!!!!!SIZE!!!!!!!!!!" + likeCheckArr.toString());

            if (eventsPojos.get(position).getEvent_likes().size() > 0) {
                if (likeCheckArr.contains(userPref.getString("USERID", "NV"))) {
                    holder.likeImg.setImageResource(R.drawable.liked);
                } else {
                    holder.likeImg.setImageResource(R.drawable.unlike);
                }
            }

            holder.likeImg.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub

                    if (likeCheckArr.contains(userPref.getString("USERID", "NV"))) {
                        Constants.showSnack(v, "Oops! You already liked it!",
                                "OK");
                    } else {
                        eid = String.valueOf(eventsPojos.get(position).getId());
                        int likes = Integer.valueOf(String.valueOf(eventsPojos.get(position).getEvent_likes().size()));
                        int lik = likes + 1;
                        holder.likesCountTxt.setText(String.valueOf(lik));
                        holder.likeImg.setImageResource(R.drawable.liked);
                        LikeUnlike();
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
            TextView titleTxt, attchCountTxt, comntCountTxt,
                    likesCountTxt;
            ImageView shareImg, likeImg, deleteImg, shareeImg;
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

            if (jsonLike != null) {
                if (jsonLike.has("errors")) {
                    Constants.showSnack(v, "Oops! You already liked it!",
                            "OK");
                }
            } else {
                Constants.showSnack(v,
                        "Oops! Something went wrong. Please check internet connection!",
                        "OK");
            }
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
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
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
                jsonDelete = EvenstPost.makeDelete(MyApplication.HOSTNAME, deleteEveId, "events");
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            Constants.showSnack(v,
                    "Deleted!",
                    "OK");
            adapter.notifyDataSetChanged();
        }
    }




    // Can be triggered by a view event such as a button press
    public void onShareItem(View v, ImageView ivImage) {
        // Get access to bitmap image from view

        // Get access to the URI for the bitmap
        Uri bmpUri = getLocalBitmapUri(ivImage);
        if (bmpUri != null) {
            // Construct a ShareIntent with link to image
            Intent shareIntent = new Intent();
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Sample text");
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
            shareIntent.setType("image/*");
            // Launch sharing dialog for image
            startActivity(Intent.createChooser(shareIntent, "Share Image"));
        } else {
            // ...sharing failed, handle error
        }
    }

    // Returns the URI path to the Bitmap displayed in specified ImageView
    public Uri getLocalBitmapUri(ImageView imageView) {
        // Extract Bitmap from ImageView drawable
        Drawable drawable = imageView.getDrawable();
        Bitmap bmp = null;
        if (drawable instanceof BitmapDrawable) {
            bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        } else {
            return null;
        }
        // Store image to default external storage directory
        Uri bmpUri = null;
        try {
            // Use methods on Context to access package-specific directories on external storage.
            // This way, you don't need to request external read/write permission.
            // See https://youtu.be/5xVh-7ywKpE?t=25m25s
            File file = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

    @Override
    public void onResume() {
        super.onResume();
        setHasOptionsMenu(true);
        ((HomeActivity) context).setHomeAsEnabled(true);
        ((HomeActivity) context).changeToolbarTitle(eventName);
        searchEdit.setHint("Search your " + eventName.toLowerCase());
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(AddIdeasEvent event) {
        if (event.success) {
            pDialogBtm.setVisibility(View.INVISIBLE);
            globalEventsPojo.clear();
            callEventsList(searchStr);
        }
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }
}
