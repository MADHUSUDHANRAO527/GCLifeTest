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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import mobile.gclifetest.MaterialDesign.ProgressBarCircularIndeterminate;
import mobile.gclifetest.PojoGson.EventComments;
import mobile.gclifetest.PojoGson.EventImages;
import mobile.gclifetest.PojoGson.EventsPojo;
import mobile.gclifetest.PojoGson.UserDetailsPojo;
import mobile.gclifetest.Utils.Constants;
import mobile.gclifetest.Utils.InternetConnectionDetector;
import mobile.gclifetest.Utils.ListViewUtils;
import mobile.gclifetest.Utils.MyApplication;
import mobile.gclifetest.activity.GalleryImageViewer;
import mobile.gclifetest.activity.HomeActivity;
import mobile.gclifetest.activity.IdeasDetail;
import mobile.gclifetest.activity.R;
import mobile.gclifetest.activity.VideoPlay;
import mobile.gclifetest.adapters.EventsCommentsAdapter;
import mobile.gclifetest.adapters.EventsImagesAdapter;
import mobile.gclifetest.db.DatabaseHandler;
import mobile.gclifetest.http.EvenstPost;
import mobile.gclifetest.http.MemsPost;

/**
 * Created by MRaoKorni on 8/2/2016.
 */
public class IdeasDetailFragment extends Fragment {
    Context context;
    String title, sdic, bdisc, societyName, associationName, memType,
            eventName, eid, videoPath, comntCreated, userName, jsonImages;
    TextView titleTxt, sDiscTxt, bDiscTxt, photosCountTxt;
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
    ProgressBarCircularIndeterminate pDialog;
    LinearLayout progrLay;
    JSONObject jsonDetails;
    EventsCommentsAdapter commentsAdapter;
    ScrollView scroll;
    List<EventsPojo> eventsPojo;
    List<EventImages> eventImagesPojo;
    List<EventComments> eventComntsPojo;
    Gson gson = new Gson();
    DatabaseHandler db;
    IdeasDetail.ImagesListBaseAdapter adapterImages;
    ImageView thumbnailImg;
    ProgressBar thumbPbar;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(
                R.layout.ideas_detail, container, false);
        context = getActivity();
        db = new DatabaseHandler(getActivity());
        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).resetViewBeforeLoading(true)
                .showImageForEmptyUri(R.drawable.no_media)
                .showImageOnFail(R.drawable.no_media)
                .showImageOnLoading(R.drawable.no_media).build();
        pDialog = (ProgressBarCircularIndeterminate) v.findViewById(R.id.pDialog);
        progrLay = (LinearLayout) v.findViewById(R.id.progrLay);
        titleTxt = (TextView) v.findViewById(R.id.titleNameTxt);
        sDiscTxt = (TextView) v.findViewById(R.id.sdescTxt);
        bDiscTxt = (TextView) v.findViewById(R.id.bdescNumTxt);
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
        if (eventName == "Ideas" || eventName.equals("Ideas")) {
            ((HomeActivity) context).changeToolbarTitle("Ideas detail");
            //  setUpActionBar("Ideas detail");
        } else if (eventName == "News" || eventName.equals("News")) {
            //  setUpActionBar("News detail");
            ((HomeActivity) context).changeToolbarTitle("News detail");
        } else if (eventName == "Photos" || eventName.equals("Photos")) {
            // setUpActionBar("Photos detail");
            ((HomeActivity) context).changeToolbarTitle("Photos detail");
            sdescLay.setVisibility(View.GONE);
            bdescLay.setVisibility(View.GONE);
        } else if (eventName == "Videos" || eventName.equals("Videos")) {
            //  setUpActionBar("Videos detail");
            ((HomeActivity) context).changeToolbarTitle("Videos detail");
            sdescLay.setVisibility(View.GONE);
            bdescLay.setVisibility(View.GONE);
        } else {
            //  actionBar.setTitle("NoticeBoard detail");
            ((HomeActivity) context).changeToolbarTitle("NoticeBoard detail");
        }

        eid = bundle.getString("id");

        //show offline
        if (db.getEventNews(eventName) != "null") {
            Log.d("DB NOT NULL: " + eventName, db.getEventNews(eventName));
            // pDialog.setVisibility(View.GONE);
            eventsPojo = gson.fromJson(db.getEventNews(eventName), new TypeToken<List<EventsPojo>>() {
            }.getType());

            for (int i = 0; i < eventsPojo.size(); i++) {
                if (eid.equals(String.valueOf(eventsPojo.get(i).getId()))) {
                    titleTxt.setText(eventsPojo.get(i).getTitle());
                    sDiscTxt.setText(eventsPojo.get(i).getSdesc());
                    bDiscTxt.setText(eventsPojo.get(i).getBdesc());
                    EventsImagesAdapter adapterImages = new EventsImagesAdapter(
                            context, eventsPojo.get(i).getEvent_images());//
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
                    Intent i = new Intent(context, VideoPlay.class);

                    try {
                        System.out.println(mediaArr.getString(position)
                                .toString());
                        JSONObject json = mediaArr.getJSONObject(position);
                        i.putExtra("Video", json.getString("image_url"));
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    startActivity(i);
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
                    Constants.showSnack(context, "Enter comment!", "OK");
                } else {
                    if (isInternetPresent) {
                        sendBtn.setText("Adding comment.....");
                        new SendComment().execute();
                    } else {
                        Constants.showSnack(context,
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

                jsonDetails = EvenstPost.makeReqeventDetails(MyApplication.HOSTNAME, eid);
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
                    titleTxt.setText(jsonDetails.getString("title"));

                    if (jsonDetails.getString("sdesc") == null
                            || jsonDetails.getString("sdesc") == "null"
                            || jsonDetails.getString("bdesc") == null
                            || jsonDetails.getString("bdesc") == "null") {
                        sdescLay.setVisibility(View.GONE);
                        bdescLay.setVisibility(View.GONE);
                    } else {
                        sDiscTxt.setText(jsonDetails.getString("sdesc"));
                        bDiscTxt.setText(jsonDetails.getString("bdesc"));
                    }

                    mediaArr = jsonDetails.getJSONArray("eventimages");
                    if (mediaArr.length() == 0 || mediaArr == null
                            || mediaArr.toString() == "null") {
                        imagesLay.setVisibility(View.GONE);
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
                                context, eventImagesPojo);
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
                }
                progrLay.setVisibility(View.GONE);
            } else {
                progrLay.setVisibility(View.GONE);
                Constants.showSnack(context,
                        "Oops! Something went wrong. Please check internet connection!",
                        "OK");
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
                json.put("comment", comntEdit.getText().toString());

                jsonComment.put("event_comment", json);
                jsonPushComment = MemsPost.PostComment(jsonComment, MyApplication.HOSTNAME);
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
            } else {
                Constants.showSnack(context,
                        "Oops! Something went wrong. Please check internet connection!",
                        "OK");
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
    }
}
