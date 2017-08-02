package mobile.gclifetest.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.List;

import mobile.gclifetest.activity.GalleryImageViewer;
import mobile.gclifetest.activity.HomeActivity;
import mobile.gclifetest.activity.R;
import mobile.gclifetest.activity.VideoPlay;
import mobile.gclifetest.adapters.EventsCommentsAdapter;
import mobile.gclifetest.adapters.EventsImagesAdapter;
import mobile.gclifetest.db.DatabaseHandler;
import mobile.gclifetest.event.AddIdeasEvent;
import mobile.gclifetest.http.EvenstPost;
import mobile.gclifetest.http.MemsPost;
import mobile.gclifetest.pojoGson.EventComments;
import mobile.gclifetest.pojoGson.EventImages;
import mobile.gclifetest.pojoGson.EventsPojo;
import mobile.gclifetest.pojoGson.UserDetailsPojo;
import mobile.gclifetest.utils.Constants;
import mobile.gclifetest.utils.GclifeApplication;
import mobile.gclifetest.utils.InternetConnectionDetector;
import mobile.gclifetest.utils.ListViewUtils;

import static mobile.gclifetest.activity.R.id.titleNameTxt;

/**
 * Created by MRaoKorni on 8/2/2016.
 */
public class IdeasDetailFragment extends Fragment {
    Context context;
    String title, sdic, bdisc, societyName, associationName, memType,
            eventName, eid, videoPath, comntCreated, userName, jsonImages;
    TextView titleTxt, sDiscTxt, bDiscTxt, photosCountTxt, postedByTxt;
    Bitmap bitmap = null;
    Gallery galleryArti;
    ImageLoader imageLoader;
    DisplayImageOptions options;
    JSONArray mediaArr;
    RelativeLayout imagesLay;
    LinearLayout sdescLay, bdescLay;
    Button sendBtn;
    ListView commentsListview;
    EditText comntEdit;
    InternetConnectionDetector netConn;
    Boolean isInternetPresent = false;
    Typeface typefaceLight;
    JSONObject jsonPushComment;
    SharedPreferences userPref;
    UserDetailsPojo user;
    mobile.gclifetest.materialDesign.ProgressBarCircularIndeterminate pDialog;
    LinearLayout progrLay, photosCountLay;
    JSONObject jsonDetails;
    EventsCommentsAdapter commentsAdapter;
    ScrollView scroll;
    List<EventsPojo> eventsPojo;
    List<EventImages> eventImagesPojo;
    List<EventComments> eventComntsPojo;
    Gson gson = new Gson();
    DatabaseHandler db;
    EventsImagesAdapter adapterImages;
    ImageView thumbnailImg;
    ProgressBar thumbPbar;
    View v;
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(
                R.layout.ideas_detail, container, false);
        context = getActivity();
        db = new DatabaseHandler(getActivity());
        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).resetViewBeforeLoading(true)
                .showImageForEmptyUri(R.drawable.no_media)
                .showImageOnFail(R.drawable.no_media)
                .showImageOnLoading(R.drawable.no_media).build();
        pDialog = (mobile.gclifetest.materialDesign.ProgressBarCircularIndeterminate) v.findViewById(R.id.pDialog);
        progrLay = (LinearLayout) v.findViewById(R.id.progrLay);
        photosCountLay = (LinearLayout) v.findViewById(R.id.photos_lay);
        titleTxt = (TextView) v.findViewById(titleNameTxt);
        sDiscTxt = (TextView) v.findViewById(R.id.sdescTxt);
        bDiscTxt = (TextView) v.findViewById(R.id.bdescNumTxt);
        postedByTxt = (TextView) v.findViewById(R.id.posted_by);
        photosCountTxt = (TextView) v.findViewById(R.id.photosCountTxt);
        galleryArti = (Gallery) v.findViewById(R.id.galleryArti);
        imagesLay = (RelativeLayout) v.findViewById(R.id.imagesLay);
        comntEdit = (EditText) v.findViewById(R.id.comentEdittext);
        sdescLay = (LinearLayout) v.findViewById(R.id.sdescLay);
        bdescLay = (LinearLayout) v.findViewById(R.id.bdescLay);
        sendBtn = (Button) v.findViewById(R.id.addCmntBtn);
        commentsListview = (ListView) v.findViewById(R.id.commentListview);
        thumbPbar = (ProgressBar) v.findViewById(R.id.thumbPbar);
        scroll = (ScrollView) v.findViewById(R.id.scroll);
        scroll.smoothScrollTo(0, 0);
        userPref = context.getSharedPreferences("USER", context.MODE_PRIVATE);
        String jsonUser = userPref.getString("USER_DATA", "NV");
        Gson gson = new Gson();
        user = gson.fromJson(jsonUser, UserDetailsPojo.class);
        Bundle bundle = this.getArguments();
        eventName = bundle.getString("EventName");

        eid = bundle.getString("id");

        //show offline
        if (db.getEventNews(eventName) != "null") {
            Log.d("DB NOT NULL: " + eventName, db.getEventNews(eventName));
            // pDialog.setVisibility(View.GONE);
            eventsPojo = gson.fromJson(db.getEventNews(eventName), new TypeToken<List<EventsPojo>>() {
            }.getType());

            for (int i = 0; i < eventsPojo.size(); i++) {
                if (eid.equals(String.valueOf(eventsPojo.get(i).getId()))) {
                    try {
                        titleTxt.setText(Constants.decodeString(eventsPojo.get(i).getTitle()));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    try {
                        if (eventName.equals("Photos") || eventName.equals("Videos")) {


                        } else {
                            sDiscTxt.setText(Constants.decodeString(eventsPojo.get(i).getSdesc()));
                            bDiscTxt.setText(Constants.decodeString(eventsPojo.get(i).getBdesc()));
                        }


                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    EventsImagesAdapter adapterImages = new EventsImagesAdapter(
                            context, eventsPojo.get(i).getEvent_images(),eventName,thumbPbar);//
                    jsonImages = gson.toJson(eventsPojo.get(i).getEvent_images());

                    try {
                        mediaArr = new JSONArray(jsonImages);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    galleryArti.setAdapter(adapterImages);
                    if (eventsPojo.get(i).getEvent_images().size() > 1) {
                        galleryArti.setSelection(1);
                    }
                    commentsAdapter = new EventsCommentsAdapter(
                            context, eventsPojo.get(i).getEvent_comments());
                    commentsListview.setAdapter(commentsAdapter);
                    ListViewUtils.setDynamicHeight(commentsListview);
                }
            }
        } else {
        }

        new EventDetails().execute();

        galleryArti.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressWarnings("unchecked")
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                if (eventName == "News" || eventName.equals("News")
                        || eventName == "Notice" || eventName.equals("Notice")
                        || eventName == "Ideas" || eventName.equals("Ideas")
                        || eventName == "Photos" || eventName.equals("Photos")) {
                    Intent i = new Intent(context,
                            GalleryImageViewer.class);
                    i.putExtra("position", position);
                    i.putExtra("Images", mediaArr.toString());
                    startActivity(i);
                } else if (eventName == "Videos" || eventName.equals("Videos")) {

                    Intent i = new Intent(getActivity(), VideoPlay.class);
                    try {
                        i.putExtra("Video", String.valueOf(mediaArr.getJSONObject(position)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    startActivity(i);

                  /*  try {
                        JSONObject json = mediaArr.getJSONObject(position);
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(json.getString("image_url"))));
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }*/
                }

            }
        });
        sendBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                netConn = new InternetConnectionDetector(context);
                isInternetPresent = netConn.isConnectingToInternet();

                if (comntEdit.getText().toString() == ""
                        || comntEdit.getText().toString().equals("")
                        || comntEdit.getText().toString().length() == 0
                        || comntEdit.getText().toString() == null) {
                    Constants.showSnack(v, "Enter comment!", "OK");
                } else {
                    if (isInternetPresent) {
                        sendBtn.setText("Adding comment.....");
                        new SendComment().execute();
                    } else {
                        Constants.showSnack(v,
                                "Please check network connection!", "OK");
                    }
                }

            }
        });
        return v;
    }

    public class EventDetails extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            progrLay.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub
            try {

                jsonDetails = EvenstPost.makeReqeventDetails(GclifeApplication.HOSTNAME, eid);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            if (jsonDetails != null) {
                try {
                    titleTxt.setText(Constants.decodeString(jsonDetails.getString("title").trim()));

                    if (jsonDetails.getString("sdesc") == null
                            || jsonDetails.getString("sdesc") == "null"
                            || jsonDetails.getString("bdesc") == null
                            || jsonDetails.getString("bdesc") == "null") {
                        sdescLay.setVisibility(View.GONE);
                        bdescLay.setVisibility(View.GONE);
                    } else {
                        if (eventName.equals("Photos") || eventName.equals("Videos")) {


                        } else {
                            sDiscTxt.setText(Constants.decodeString(jsonDetails.getString("sdesc")));
                            bDiscTxt.setText(Constants.decodeString(jsonDetails.getString("bdesc")));
                        }

                    }
                    String createdAt = GclifeApplication.convertDateEmail(jsonDetails.getString("created_at"));
                    String time = jsonDetails.getString("created_at").substring(11, 19);
                    time = GclifeApplication.convertTimeEmail(time);

                    if (jsonDetails.has("username")) {
                        if (jsonDetails.getString("username") != null) {
                            postedByTxt.setText("Posted by: " + jsonDetails.getString("username") + " - " + createdAt + "," + time);
                            //   postedByTxt.setText(Html.fromHtml("<font color=\"#c5c5c5\">" + "Posted by: " + "</font>" +"<font color=\"#000000\">"+ jsonDetails.getString("username") +  "</font>" +
                            //           "<font color=\"#000000\">"+createdAt + "," + time + "</font>"));
                        }
                    }
                    mediaArr = jsonDetails.getJSONArray("eventimages");
                    if (mediaArr.length() == 0 || mediaArr == null
                            || mediaArr.toString() == "null") {
                        imagesLay.setVisibility(View.GONE);
                        photosCountLay.setVisibility(View.GONE);
                    } else {
                        eventImagesPojo = gson.fromJson(mediaArr.toString(), new TypeToken<List<EventImages>>() {
                        }.getType());
                        if (eventName == "Videos" || eventName.equals("Videos")) {
                            photosCountTxt.setText(String.valueOf(eventImagesPojo.size())
                                    + " " + "Videos");
                        } else {
                            photosCountTxt.setText(String.valueOf(eventImagesPojo.size())
                                    + " " + "Photos");
                        }
                        EventsImagesAdapter adapterImages = new EventsImagesAdapter(
                                context, eventImagesPojo,eventName,thumbPbar);
                        galleryArti.setAdapter(adapterImages);
                        if (eventImagesPojo.size() > 1) {
                            galleryArti.setSelection(1);
                        }
                    }
                    JSONArray cmntArr = jsonDetails
                            .getJSONArray("event_comments");
                    eventComntsPojo = gson.fromJson(cmntArr.toString(), new TypeToken<List<EventComments>>() {
                    }.getType());

                    commentsAdapter = new EventsCommentsAdapter(
                            context, eventComntsPojo);
                    commentsListview.setAdapter(commentsAdapter);
                    ListViewUtils.setDynamicHeight(commentsListview);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                progrLay.setVisibility(View.GONE);
            } else {
                progrLay.setVisibility(View.GONE);
                Constants.showToast(context, R.string.went_wrong);
                ((HomeActivity) context).onBackpressed();
            }
        }
    }

    private class SendComment extends AsyncTask<Void, Void, Void> {
        @SuppressLint("InlinedApi")
        protected void onPreExecute() {
            // pDialog.setVisibility(View.VISIBLE);

        }

        @Override
        protected Void doInBackground(Void... params) {
            JSONObject jsonComment = new JSONObject();
            try {
                JSONObject json = new JSONObject();
                json.put("event_id", eid);
                json.put("user_id", userPref.getString("USERID", "NV"));
                json.put("username", user.getUsername());
                json.put("comment", Constants.encodeString(comntEdit.getText().toString()));

                jsonComment.put("event_comment", json);
                jsonPushComment = MemsPost.PostComment(jsonComment, GclifeApplication.HOSTNAME);
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
            System.out.println(jsonPushComment
                    + "^^^^^^^^^^^^ RESULT OF PUSH COMMENT ^^^^^^^^^^^^^^");

            if (jsonPushComment != null) {
                EventComments eventComntPojo = gson.fromJson(jsonPushComment.toString(), new TypeToken<EventComments>() {
                }.getType());
                eventComntsPojo.add(eventComntPojo);
                commentsAdapter = new EventsCommentsAdapter(context, eventComntsPojo);
                commentsListview.setAdapter(commentsAdapter);
                comntEdit.setText("");
                commentsAdapter.notifyDataSetChanged();
                ListViewUtils.setDynamicHeight(commentsListview);
                sendBtn.setText("ADD THIS COMMENT");
                EventBus.getDefault().post(new AddIdeasEvent(true));
            } else {
                Constants.showToast(context, R.string.went_wrong);
            }
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                EventBus.getDefault().post(new AddIdeasEvent(true));
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
        if (eventName == "Ideas" || eventName.equals("Ideas")) {
            ((HomeActivity) context).changeToolbarTitle(R.string.idea_detail);
        } else if (eventName == "News" || eventName.equals("News")) {
            ((HomeActivity) context).changeToolbarTitle(R.string.news_detail);
        } else if (eventName.equals("Photos")) {
            ((HomeActivity) context).changeToolbarTitle(R.string.photos_detail);
            sdescLay.setVisibility(View.GONE);
            bdescLay.setVisibility(View.GONE);
        } else if (eventName.equals("Videos")) {
            ((HomeActivity) context).changeToolbarTitle(R.string.videos_detail);
            sdescLay.setVisibility(View.GONE);
            bdescLay.setVisibility(View.GONE);
        } else {
            ((HomeActivity) context).changeToolbarTitle(R.string.nb_detail);
        }
        ((HomeActivity) context).setHomeAsEnabled(true);
       /* if(getView() == null){
            return;
        }

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){
                    // handle back button's click listener
                    EventBus.getDefault().post(new AddIdeasEvent(true));
                    return true;
                }
                return false;
            }
        });*/
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
