package mobile.gclifetest.fragments;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.cloudinary.utils.ObjectUtils;
import com.flurry.android.FlurryAgent;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.AccountPicker;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.googleapis.media.MediaHttpUploaderProgressListener;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeScopes;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoSnippet;
import com.google.api.services.youtube.model.VideoStatus;
import com.google.common.collect.Lists;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mobile.gclifetest.activity.HomeActivity;
import mobile.gclifetest.activity.R;
import mobile.gclifetest.custom.CustomGallery;
import mobile.gclifetest.custom.CustomImgGalleryActivity;
import mobile.gclifetest.custom.CustomVideoGalleryActivity;
import mobile.gclifetest.custom.GalleryImgAdapter;
import mobile.gclifetest.event.AddIdeasEvent;
import mobile.gclifetest.http.EvenstPost;
import mobile.gclifetest.http.SocietyNameGet;
import mobile.gclifetest.materialDesign.ProgressBarCircularIndeterminate;
import mobile.gclifetest.pojoGson.AvenuesFilter;
import mobile.gclifetest.utils.Constants;
import mobile.gclifetest.utils.MyApplication;

import static mobile.gclifetest.utils.Constants.isFileSizeAcceptable;
import static mobile.gclifetest.youtube.IntentRequestCode.REQUEST_AUTHORIZATION;
import static mobile.gclifetest.youtube.util.SharedPreferenceUtil.selectedGoogleAccount;

/**
 * Created by MRaoKorni on 8/19/2016.
 */
public class PhotosCreateFragment extends Fragment {
    Context context;
    FloatingActionButton addBtn;
    EditText titleEdit;
    JSONObject jsonResult;
    String ideaTitle, ext, fileName, mediaUrl = "", eventName, selectedAven = "", selectedSoci = "", selectedMem = "";
    TextView finishTxt, selectedMediaTxt;
    SharedPreferences userPref;
    Map<String, ArrayList<String>> societyMap = new HashMap<String, ArrayList<String>>();
    JSONArray jsonResultArry;
    ProgressBarCircularIndeterminate pDialog, pDialogImg;
    ImageView attachImg;
    int SELECT_FILE1 = 1;
    byte[] bytes;
    ViewSwitcher viewSwitcher;
    GalleryImgAdapter adapter;
    RelativeLayout avenueLay, sociLay, memLay;
    JSONArray eventImages = new JSONArray();
    Dialog dialogAvenue, dialogSoci, dialogMems;
    Typeface typefaceLight;
    ListAvenBaseAdapter aveAdapter;
    ArrayList<String> selectedAveNames = new ArrayList<String>();
    ArrayList<String> selectedSociNames = new ArrayList<String>();
    ArrayList<String> selectedMemsNames = new ArrayList<String>();
    boolean mems = false;
    String token;
    List<AvenuesFilter> societiesPojo = new ArrayList<AvenuesFilter>();
    ListView listviewAvenue, listviewSoci, listviewMem;
    ListSociBaseAdapter sociAdapter;
    SparseBooleanArray mChecked = new SparseBooleanArray();
    private static boolean isNotAdded = true;
    private CheckBox checkBox_header;
    static final int REQUEST_CODE_PICK_ACCOUNT = 1000;
    public static final String scope = "oauth2:https://www.googleapis.com/auth/youtube";
    private Video youtubeVideo = null;
    View v;
    String[] videoPathsArr = new String[0];
    String[] allPhotopaths;
    File filePhoto;
    SharedPreferences.Editor editor;
    ProgressBar mProgressBar;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(
                R.layout.photos_create, container, false);
        context = getActivity();

        userPref = context.getSharedPreferences("USER", context.MODE_PRIVATE);
        Log.d("EMAIL YOUTUBE ", userPref.getString("youtube_name", "NV"));
        editor = userPref.edit();
        addBtn = (FloatingActionButton) v.findViewById(R.id.addBtn);
        titleEdit = (EditText) v.findViewById(R.id.titleEdit);
        selectedMediaTxt = (TextView) v.findViewById(R.id.selectedMedia);
        avenueLay = (RelativeLayout) v.findViewById(R.id.avenueLay);
        sociLay = (RelativeLayout) v.findViewById(R.id.societyLay);
        memLay = (RelativeLayout) v.findViewById(R.id.memberLay);
        pDialog = (ProgressBarCircularIndeterminate) v.findViewById(R.id.pDialog);
        pDialogImg = (ProgressBarCircularIndeterminate) v.findViewById(R.id.pDialogImg);
        finishTxt = (TextView) v.findViewById(R.id.finishTxt);
        attachImg = (ImageView) v.findViewById(R.id.attachImg);
        mProgressBar = (ProgressBar) v.findViewById(R.id.upload_progress);
        Bundle bundle = getArguments();
        eventName = bundle.getString("EventName");

        dialogAvenue = new Dialog(getActivity());
        dialogAvenue.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogAvenue.setContentView(R.layout.avenues_listview_check);
        dialogAvenue.getWindow().getAttributes().windowAnimations = R.style.popup_login_dialog_animation;
        listviewAvenue = (ListView) dialogAvenue
                .findViewById(R.id.listviewAven);

        dialogSoci = new Dialog(getActivity());
        dialogSoci.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSoci.setContentView(R.layout.soci_listview_check);
        dialogSoci.getWindow().getAttributes().windowAnimations = R.style.popup_login_dialog_animation;
        listviewSoci = (ListView) dialogSoci.findViewById(R.id.listviewSoci);

        final View headerView = getActivity().getLayoutInflater().inflate(R.layout.custom_list_view_header,
                listviewSoci, false);

        checkBox_header = (CheckBox) headerView.findViewById(
                R.id.checkBox_header);

        checkBox_header.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (checkBox_header.isChecked()) {
                    for (int i = 0; i < societiesPojo.size(); i++) {
                        mChecked.put(i, checkBox_header.isChecked());
                        selectedSociNames.add(societiesPojo.get(i).getSocityName());
                    }
                } else {
                    for (int i = 0; i < societiesPojo.size(); i++) {
                        mChecked.delete(i);
                    }
                    selectedSociNames = new ArrayList<String>();
                }
                sociAdapter.notifyDataSetChanged();
            }
        });

            /*
             * Add Header to ListView
             */
        listviewSoci.addHeaderView(headerView);

        isNotAdded = false;


        dialogMems = new Dialog(getActivity());
        dialogMems.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogMems.setContentView(R.layout.mems_listview_check);
        dialogMems.getWindow().getAttributes().windowAnimations = R.style.popup_login_dialog_animation;
        listviewMem = (ListView) dialogMems.findViewById(R.id.listviewAven);

        ArrayList<String> membsList = new ArrayList<String>();
        membsList.add("All");
        membsList.add("Member");
        membsList.add("Committee member");
        membsList.add("Secretary");
        membsList.add("Chairman");
        membsList.add("Treasurer");
        membsList.add("Non member");

        ListMemsBaseAdapter memsAdapter = new ListMemsBaseAdapter(
                getActivity(), membsList, listviewMem);
        listviewMem.setAdapter(memsAdapter);

        TextView noTxtave = (TextView) dialogAvenue.findViewById(R.id.noTxt);
        TextView yesTxtave = (TextView) dialogAvenue.findViewById(R.id.yesTxt);

        TextView noTxtSoc = (TextView) dialogSoci.findViewById(R.id.noTxt);
        TextView yesTxtSoc = (TextView) dialogSoci.findViewById(R.id.yesTxt);

        TextView noTxtmem = (TextView) dialogMems.findViewById(R.id.noTxt);
        TextView yesTxtmem = (TextView) dialogMems.findViewById(R.id.yesTxt);
        noTxtave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialogAvenue.dismiss();
            }
        });
        yesTxtave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                selectedAven = TextUtils.join(",", selectedAveNames);
                System.out.println(selectedAven);
                dialogAvenue.dismiss();
            }
        });
        noTxtSoc.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialogSoci.dismiss();
            }
        });
        yesTxtSoc.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialogSoci.dismiss();
                //remove duplicates i used HashSet
                Set<String> hs = new HashSet<>();
                hs.addAll(selectedSociNames);
                selectedSociNames.clear();
                selectedSociNames.addAll(hs);
                selectedSoci = TextUtils.join(",", selectedSociNames);
                System.out.println(selectedSociNames.size());

            }
        });
        noTxtmem.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialogMems.dismiss();
            }
        });
        yesTxtmem.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialogMems.dismiss();

                if (selectedMemsNames.contains("Committee member")) {
                    selectedMemsNames.remove("Committee member");
                    selectedMemsNames.add("Committee_member");
                }
                if (selectedMemsNames.contains("Non member")) {
                    selectedMemsNames.remove("Non member");
                    selectedMemsNames.add("Non_members");
                }

                selectedMem = TextUtils.join(",", selectedMemsNames);
            }
        });
        avenueLay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialogAvenue.show();
            }
        });
        sociLay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialogSoci.show();
            }
        });
        memLay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialogMems.show();
            }
        });

        new SocietyNames().execute();

        finishTxt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                ideaTitle = titleEdit.getText().toString();
                if (ideaTitle == null || ideaTitle == "null" || ideaTitle == ""
                        || ideaTitle.length() == 0) {
                    Constants.showSnack(v,
                            "Please enter title!",
                            "OK");
                } else if (eventName.equals("Videos") &&(videoPathsArr == null && videoPathsArr.length == 0)) {
                    Constants.showSnack(v,
                            "Please select atleast one video!",
                            "OK");
                } else if (eventName.equals("Photos") && (eventImages == null ||
                        eventImages.length() == 0)) {
                        Constants.showSnack(v,
                                "Please select atleast one image!",
                                "OK");
                } else if (selectedAven == null || selectedAven == "null"
                        || selectedAven == "" || selectedAven.length() == 0) {
                    Constants.showSnack(v,
                            "Please select avenue name!", "OK");
                } else if (selectedSoci == null || selectedSoci == "null"
                        || selectedSoci == "" || selectedSoci.length() == 0) {
                    Constants.showSnack(v,
                            "Please select society name!", "OK");
                } else if (selectedMem == null || selectedMem == "null"
                        || selectedMem == "" || selectedMem.length() == 0) {
                    Constants.showSnack(v,
                            "Please select member type!", "OK");
                } else if (eventName.equals("Videos")) {
                    uploaddToServer();
                } else {
                    new CreatePhotosVideos().execute();
                }
            }
        });

        attachImg.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                // openGallery(SELECT_FILE1);
                if (eventName == "Photos" || eventName.equals("Photos")) {
                    Intent i = new Intent(getActivity(), CustomImgGalleryActivity.class);
                    startActivityForResult(i, 200);
                } else {
                    Intent i = new Intent(getActivity(), CustomVideoGalleryActivity.class);
                    startActivityForResult(i, 200);
                }
            }
        });
        return v;
    }

    public class CreatePhotosVideos extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            pDialog.setVisibility(View.VISIBLE);
            finishTxt.setVisibility(View.INVISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub
            JSONObject jsonIdeas = new JSONObject();
            try {
                jsonIdeas.put("event_type", eventName);
                jsonIdeas.put("title", Constants.encodeString(ideaTitle));
                jsonIdeas.put("user_id", userPref.getString("USERID", "NV"));
                jsonIdeas.put("association_list", selectedAven);
                jsonIdeas.put("society_list", selectedSoci);
                jsonIdeas.put("member_type_list", selectedMem);

                System.out.println(eventImages + "!!!!!!!!!!!!!!!!!!!!!!!");

                if (eventImages.toString() == "[]"
                        || eventImages.toString().equals("[]")
                        || eventImages.length() == 0) {
                    Toast.makeText(getActivity(), "Media is empty!", Toast.LENGTH_SHORT).show();
                } else {
                    jsonIdeas.put("EventImages", eventImages);
                }

                JSONObject eventJson = new JSONObject();
                eventJson.put("event", jsonIdeas);
                try {
                    jsonResult = EvenstPost.makeRequest(eventJson, MyApplication.HOSTNAME);
                    System.out.println("RESPONSE :::::::::::::::::::::"
                            + jsonResult);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            if (jsonResult != null) {
                if (jsonResult.has("id")) {
                    pDialog.setVisibility(View.GONE);
                    EventBus.getDefault().post(new AddIdeasEvent(true));
                    ((HomeActivity) context).onBackpressed();
                }
            } else {
                pDialog.setVisibility(View.GONE);
                finishTxt.setVisibility(View.VISIBLE);
                Constants.showToast(getActivity(),R.string.went_wrong);
            }
        }
    }

    public class SocietyNames extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            // pDialog.setVisibility(View.VISIBLE);
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
                    listAssociation.add("All");
                    for (int i = 0; i < jsonResultArry.length(); i++) {
                        ArrayList<String> listSociety = new ArrayList<String>();
                        //	listSociety.add("All");
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

                        }
                        ArrayList<String> associationList = new ArrayList<String>(
                                listSociety);
                        societyMap.put(associationName, associationList);

                        System.out.println(societyMap);

                    }
                    if (getActivity() != null) {
                        aveAdapter = new ListAvenBaseAdapter(getActivity(),
                                listAssociation, listviewAvenue);
                        listviewAvenue.setAdapter(aveAdapter);


                        ArrayList<String> societylist = societyMap
                                .get(listAssociation.get(1));
                        aveAdapter = new ListAvenBaseAdapter(getActivity(),
                                listAssociation, listviewAvenue);
                        listviewAvenue.setAdapter(aveAdapter);
                        Log.d("AVENUES : ", listAssociation.toString());
                    }


                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            } else {
                Constants.showToast(getActivity(),R.string.went_wrong);
            }
        }
    }
    // gallery

    public void openGallery(int req_code) {

        Intent intent = new Intent();
        intent.setType("video/*, image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(intent, "Select Picture"),
                1);

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 200 && resultCode == Activity.RESULT_OK) {
            final String[] all_path = data.getStringArrayExtra("all_path");
            videoPathsArr = data.getStringArrayExtra("all_path");
            if (all_path.length > 0) {

                if (eventName == "Photos" || eventName.equals("Photos")) {
                    pDialogImg.setVisibility(View.VISIBLE);
                    selectedMediaTxt.setText("Attaching images....");
                } else {
                    //  selectedMediaTxt.setText("Attaching videos....");
                }
                //    finishTxt.setEnabled(false);
            }
            System.out.println(all_path);
            if (eventName == "Photos" || eventName.equals("Photos")) {
                Toast.makeText(context, "You have Selected " + all_path.length
                        + " images", Toast.LENGTH_SHORT).show();

                for (String selectedPath1 : all_path) {
                    CustomGallery item = new CustomGallery();
                    item.sdcardPath = selectedPath1;
                    ext = selectedPath1
                            .substring(selectedPath1.lastIndexOf(".") + 1);
                    fileName = selectedPath1.replaceFirst(".*/(\\w+).*", "$1");

                    File filee = new File(selectedPath1);
                    int size = (int) filee.length();
                    bytes = new byte[size];
                    try {
                        BufferedInputStream buf = new BufferedInputStream(
                                new FileInputStream(filee));
                        buf.read(bytes, 0, bytes.length);
                        buf.close();
                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    System.out.println(bytes + " !!!!!!!!!!!!!!!!!!!!");

                    ext = ext.toLowerCase();
                    System.out.println("File name : " + fileName + "   "
                            + "Image extension : " + ext);

                    new LoadImages(all_path, filee).execute();
                }
            } else {
                selectedMediaTxt.setText("You have Selected " + all_path.length + " videos");

                if (!userPref.getString("youtube_name", "NV").equals("NV")) {
                    new FetchYouTubeTokenTask().execute();
                } else {
                    pickUserAccount();
                }
            }
            // adapter.addAll(dataT);
        } else {

            if (requestCode == REQUEST_CODE_PICK_ACCOUNT && resultCode == Activity.RESULT_OK) {
                if (data != null && data.getExtras() != null) {
                    String accountName = data.getExtras().getString(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        selectedGoogleAccount = accountName;
                        //     userNameTxt.setText("Email : " + selectedGoogleAccount);
                        editor.putString("youtube_name", accountName);
                        editor.commit();
                        editor.apply();
                        new FetchYouTubeTokenTask().execute();

                    } else {
                        //   DialogUtil.showExceptionAlertDialog(this, getString(R.string.googleAccountNotSelected), getString(R.string.googleAccountNotSupported));
                    }
                }
            } else if (requestCode == REQUEST_AUTHORIZATION && resultCode == Activity.RESULT_OK) {
                // Account has been chosen and permissions have been granted. You can upload video
                Toast.makeText(getActivity(), "APP AUTHORIZED!", Toast.LENGTH_LONG).show();
                new FetchYouTubeTokenTask().execute();
            }
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                ((HomeActivity) context).onBackpressed();
                return true;
               /* overridePendingTransition(R.anim.slide_right_in,
                        R.anim.slide_out_right);*/
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed() {
        Intent soc = new Intent(getActivity(), PhotosVideosListFragment.class);
        soc.putExtra("EventName", eventName);
        startActivity(soc);
    }


    public class ListAvenBaseAdapter extends BaseAdapter {
        ArrayList<String> list;
        private LayoutInflater inflator;
        private Context context;
        ArrayList<String> societylist = new ArrayList<String>();
        ListView avenuesListview;

        public ListAvenBaseAdapter(Activity activity,
                                   ArrayList<String> listArticles, ListView avenuesListvie) {
            // TODO Auto-generated constructor stub
            this.context = activity;
            this.list = listArticles;
            avenuesListview = avenuesListvie;
            inflator = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            societylist.add("All");
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
            final ViewHolder holder;
            if (convertView == null) {
                convertView = inflator.inflate(R.layout.avenues_adapter_row,
                        parent, false);
                holder = new ViewHolder();
                holder.titleTxt = (TextView) convertView
                        .findViewById(R.id.titleTxt);

                holder.checkBox = (CheckBox) convertView
                        .findViewById(R.id.checked);

                holder.checkBox.setVisibility(View.VISIBLE);
                holder.titleTxt.setTypeface(typefaceLight);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.titleTxt.setText(list.get(position));
            holder.checkBox.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (holder.checkBox.isChecked()) {
                        if (list.get(position) == "All" || list.get(position).equals("All")) {
                            societiesPojo = new ArrayList<AvenuesFilter>();
                            for (int i = 0; i < avenuesListview.getChildCount(); i++) {
                                RelativeLayout itemLayout = (RelativeLayout) avenuesListview.getChildAt(i);
                                CheckBox cb = (CheckBox) itemLayout.findViewById(R.id.checked);
                                cb.setChecked(true);
                            }
                            //    selectedAveNames.add("All");

                            for (int j = 1; j <= list.size()-1; j++) {
                                societylist = societyMap
                                        .get(list.get(j));
                                selectedAveNames.add(list.get(j).toString());
                                for (int i = 0; i < societylist.size(); i++) {
                                    Log.d("SOCIETY NAME :", societylist.get(i) + "");
                                    AvenuesFilter filter = new AvenuesFilter();
                                    filter.setSocityName(societylist.get(i));
                                    filter.setChecked(false);
                                    societiesPojo.add(filter);
                                }
                            }
                        } else {
                            societylist = societyMap
                                    .get(list.get(position));

                            selectedAveNames.add(list.get(position).toString());
                            Log.d("LIST SIZE  : ", societylist.toString() + "");
                            for (int i = 0; i < societylist.size(); i++) {
                                Log.d("SOCIETY NAME :", societylist.get(i) + "");
                                AvenuesFilter filter = new AvenuesFilter();
                                filter.setSocityName(societylist.get(i));
                                filter.setChecked(false);
                                societiesPojo.add(filter);
                            }
                            //    selectedAveNames.add(list.get(position));
                        }
                    } else {

                        if (list.get(position) == "All" || list.get(position).equals("All")) {
                            for (int i = 0; i < avenuesListview.getChildCount(); i++) {
                                RelativeLayout itemLayout = (RelativeLayout) avenuesListview.getChildAt(i);
                                CheckBox cb = (CheckBox) itemLayout.findViewById(R.id.checked);
                                cb.setChecked(false);
                            }
                            selectedAveNames = new ArrayList<String>();
                            //     selectedAveNames.remove("All");
                            societiesPojo = new ArrayList<AvenuesFilter>();
                        } else {
                            RelativeLayout itemLayout = (RelativeLayout) avenuesListview.getChildAt(0);
                            CheckBox cb = (CheckBox) itemLayout.findViewById(R.id.checked);
                            cb.setChecked(false);
                            selectedAveNames.remove(list.get(position).toString());
                            societylist = societyMap
                                    .get(list.get(position));
                            Log.d("MAP  : ", societyMap.toString() + "");
                            for (int i = 0; i < societylist.size(); i++) {


                                for (int j = 0; j < societiesPojo.size(); j++) {
                                    AvenuesFilter filter = societiesPojo.get(j);
                                    System.out.println(filter.getSocityName() + " " + societylist.get(i));
                                    Log.d("CHECKING  : ", filter.getSocityName() + " " + societylist.get(i) + "");
                                    if (filter.getSocityName() == societylist.get(i).toString() || filter.getSocityName().equals(societylist.get(i).toString())) {
                                        if (j <= societiesPojo.size() - 1) {
                                            societiesPojo.remove(j);
                                            break;
                                        }

                                    }
                                /*    filter.setSocityName(societylist.get(i));
                                    Log.d("ARRAY ITEM : ", societylist.get(i) + "");
                                    Log.d("SIZE  : ", societylist.size() + "");*/

                                }


                                Log.d("POJO SIZE  : ", societiesPojo.size() + "");
                            }
                        }
                    }

                    if (selectedAveNames.contains("All")) {
                        selectedAveNames.remove("All");
                    }
                    System.out.println(selectedAveNames);
                    Log.d("SIZE  POJO: ", societiesPojo.size() + "");
                    sociAdapter = new ListSociBaseAdapter(
                            getActivity(), societiesPojo, listviewSoci);
                    listviewSoci.setAdapter(sociAdapter);
                }
            });

            return convertView;
        }

        public class ViewHolder {
            TextView titleTxt;
            CheckBox checkBox;
        }
    }

    public class ListSociBaseAdapter extends BaseAdapter {
        List<AvenuesFilter> societiesPojo;
        private LayoutInflater inflator;
        private Context context;
        boolean[] checkBoxState;
        ListView listviewSoci;

        public ListSociBaseAdapter(Activity activity,
                                   List<AvenuesFilter> societiesPoj, ListView listviewSociety) {
            // TODO Auto-generated constructor stub
            this.context = activity;
            this.societiesPojo = societiesPoj;
            listviewSoci = listviewSociety;
            inflator = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //   this.societiesPojo.get(0).setSocityName("All");
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return societiesPojo.size();
        }

        @Override
        public Object getItem(int pos) {
            // TODO Auto-generated method stub
            return societiesPojo.get(pos);
        }

        @Override
        public long getItemId(int pos) {
            // TODO Auto-generated method stub
            return pos;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            View mView = convertView;

            if (mView == null) {
                final LayoutInflater sInflater = (LayoutInflater) context.getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);

                mView = sInflater.inflate(R.layout.avenues_adapter_row, null, false);
            }
            final TextView titleTxt = (TextView) mView.findViewById(R.id.titleTxt);
            //      final ImageView sIMG = (ImageView) mView.findViewById(R.id.imageView);
            final CheckBox mCheckBox = (CheckBox) mView.findViewById(
                    R.id.checked);
            titleTxt.setText(societiesPojo.get(position).getSocityName());
            mCheckBox.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (mCheckBox.isChecked()) {
                        mChecked.put(position, mCheckBox.isChecked());

                        if (isAllValuesChecked()) {
                            checkBox_header.setChecked(mCheckBox.isChecked());
                        }
                        selectedSociNames.add(societiesPojo.get(position).getSocityName());
                    } else {
                        mChecked.delete(position);
                        checkBox_header.setChecked(mCheckBox.isChecked());
                        selectedSociNames.remove(societiesPojo.get(position).getSocityName());
                    }
                }
            });
            mCheckBox.setChecked((mChecked.get(position) == true ? true : false));

            return mView;
        }

        /*
         * Find if all values are checked.
         */
        protected boolean isAllValuesChecked() {

            for (int i = 0; i < societiesPojo.size(); i++) {
                if (!mChecked.get(i)) {
                    return false;
                }
            }

            return true;
        }

        public class ViewHolder {
            TextView titleTxt;
            CheckBox checkBox;
        }
    }

    public class ListMemsBaseAdapter extends BaseAdapter {
        ArrayList<String> list;
        private LayoutInflater inflator;
        private Context context;
        ListView listviewMemadapter;

        public ListMemsBaseAdapter(Activity activity,
                                   ArrayList<String> listArticles, ListView listviewMemss) {
            // TODO Auto-generated constructor stub
            this.context = activity;
            this.list = listArticles;
            this.listviewMemadapter = listviewMemss;
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
            final ViewHolder holder;
            if (convertView == null) {
                convertView = inflator.inflate(R.layout.avenues_adapter_row,
                        parent, false);
                holder = new ViewHolder();
                holder.titleTxt = (TextView) convertView
                        .findViewById(R.id.titleTxt);
                holder.checkBox = (CheckBox) convertView
                        .findViewById(R.id.checked);
                holder.checkBox.setVisibility(View.VISIBLE);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.titleTxt.setTypeface(typefaceLight);


            holder.checkBox.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    if (holder.checkBox.isChecked()) {
                        if (list.get(position) == "All" || list.get(position).equals("All")) {
                            for (int i = 0; i < listviewMemadapter.getChildCount(); i++) {
                                RelativeLayout itemLayout = (RelativeLayout) listviewMemadapter.getChildAt(i);
                                CheckBox cb = (CheckBox) itemLayout.findViewById(R.id.checked);
                                cb.setChecked(true);
                                selectedMemsNames.add(list.get(i).trim());
                            }
                        } else {
                            selectedMemsNames.add(list.get(position).trim());
                        }
                    } else {
                        if (list.get(position) == "All" || list.get(position).equals("All")) {
                            for (int i = 0; i < listviewMemadapter.getChildCount(); i++) {
                                RelativeLayout itemLayout = (RelativeLayout) listviewMemadapter.getChildAt(i);
                                CheckBox cb = (CheckBox) itemLayout.findViewById(R.id.checked);
                                cb.setChecked(false);
                            }
                            selectedMemsNames = new ArrayList<String>();
                            //  selectedMemsNames.remove("All");
                        } else {
                            RelativeLayout itemLayout = (RelativeLayout) listviewMemadapter.getChildAt(0);
                            CheckBox cb = (CheckBox) itemLayout.findViewById(R.id.checked);
                            cb.setChecked(false);
                            selectedMemsNames.remove(list.get(position));
                        }
                    }

                    if (selectedMemsNames.contains("All")) {
                        selectedMemsNames.remove("All");
                    }
                    System.out.println(selectedMemsNames);
                }
            });
            holder.checkBox.setTag(position);
            holder.titleTxt.setText(list.get(position));
            return convertView;
        }

        public class ViewHolder {
            TextView titleTxt;
            CheckBox checkBox;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setHasOptionsMenu(true);
        ((HomeActivity) context).setHomeAsEnabled(true);
        if (eventName == "Photos" || eventName.equals("Photos")) {
            ((HomeActivity) context).changeToolbarTitle(R.string.create_photos);
        } else
            ((HomeActivity) context).changeToolbarTitle(R.string.create_videos);
    }

    private void pickUserAccount() {
        String[] accountTypes = new String[]{"com.google"};
        Intent intent = AccountPicker.newChooseAccountIntent(null, null, accountTypes, false, null, null, null, null);
        startActivityForResult(intent, REQUEST_CODE_PICK_ACCOUNT);
    }

    public class FetchYouTubeTokenTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                token = GoogleAuthUtil.getToken(getActivity(), userPref.getString("youtube_name", "NV"), scope);
            } catch (UserRecoverableAuthException userAuthEx) {
                // In case Android complains that Access not Configured, refer to comment of this class for how to configure OAuth client ID for this app.
                startActivityForResult(userAuthEx.getIntent(), REQUEST_AUTHORIZATION);
            } catch (GoogleAuthException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (token != null) {
                //  uploaddToServer();
            } else {
                Toast.makeText(getActivity(), "Couldn't able to fetch token from youtube!Try again!", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void uploaddToServer() {
        selectedMediaTxt.setText("Attaching videos....");

        //finishTxt.setEnabled(false);
        for (int i = 0; i < videoPathsArr.length; i++) {
            uploadVideo(videoPathsArr[i], i);
        }
    }

    private void uploadVideo(String videoPathvideo, int i) {
        if (!isFileSizeAcceptable(videoPathvideo)) {
            Constants.showSnack(v,
                    "File size too large! Maximum 80Mb allowed!",
                    "");
        } else {
            pDialog.setVisibility(View.VISIBLE);
            finishTxt.setVisibility(View.INVISIBLE);
            new YouTubeUploadTask(videoPathvideo, i).execute();
        }
    }


    public class YouTubeUploadTask extends AsyncTask<Void, Integer, Void> {
        String videoPathvideo;
        private static final String VIDEO_FILE_FORMAT = "video/*";
        int fileNumber;
        public static final String scope = "oauth2:https://www.googleapis.com/auth/youtube";
        private final List<String> scopes = Lists.newArrayList(YouTubeScopes.YOUTUBE);

        //  private String videoFileName = "/storage/sdcard1/DCIM/Camera/VID_20160905_125329.mp4";
        private String appName = "GCLife Test";
        private String title = titleEdit.getText().toString();
        private String description = "";

        private HttpTransport transport = AndroidHttp.newCompatibleTransport();
        private com.google.api.client.json.JsonFactory jsonFactory = new GsonFactory();

        private MediaHttpUploader uploader = null;


        private String errorMessage = null;

        public YouTubeUploadTask(String videoPathvide, int mFileNum) {
            videoPathvideo = videoPathvide;
            fileNumber = mFileNum;
        }

        @Override
        protected void onPreExecute() {
            MyApplication.getInstance().setUploadingMedia(true);
        }

        public YouTube.Videos.Insert prepareUpload() {
            try {


                File videoFile = new File(videoPathvideo);

                // Add extra information to the video before uploading
                Video videoObjectDefiningMetadata = new Video();

                // Set the video to public (default)
                VideoStatus status = new VideoStatus();
                status.setPrivacyStatus("public");
                videoObjectDefiningMetadata.setStatus(status);

                // Set metadata with the VideoSnippet object
                VideoSnippet snippet = new VideoSnippet();

                // Video title and description
                snippet.setTitle(title);
                snippet.setDescription(description);

                // Set keywords
                List<String> tags = new ArrayList<String>();
                tags.add(appName);
                snippet.setTags(tags);

                // Set completed snippet to the video object
                videoObjectDefiningMetadata.setSnippet(snippet);

                byte[] bytes = new byte[(int) videoFile.length()];
                try {
                    new FileInputStream(videoFile).read(bytes);
                } catch (Exception e) {
                }
                byte[] payload = bytes;

                int totalSize = payload.length;
                Log.d("payload size : ", totalSize + "");
                int bytesTransferred = 0;
                int chunkSize = 2000;
                System.out.println(fileNumber);
                int n = fileNumber++;
           /*     selectedMediaTxt.setText("Attaching videos.... "
                        + String.valueOf(fileNumber));*/

                while (bytesTransferred < totalSize) {
                    int nextChunkSize = totalSize - bytesTransferred;
                    if (nextChunkSize > chunkSize) {
                        nextChunkSize = chunkSize;
                    }
                    //   outputStream.write(payload, bytesTransferred, nextChunkSize); // TODO check outcome!
                    bytesTransferred += nextChunkSize;

                    float progressPercentage = (bytesTransferred * 1.0f) / totalSize * 100;
                    Log.d("Bytes transferred", bytesTransferred + " " + (int) progressPercentage);
                    //   mProgressBar.setProgress((int) progressPercentage);
                    publishProgress((int) progressPercentage);
                }
                InputStreamContent mediaContent = new InputStreamContent(VIDEO_FILE_FORMAT, new BufferedInputStream(new FileInputStream(videoFile)));
                mediaContent.setLength(bytesTransferred);


                GoogleAccountCredential credential = buildGoogleAccountCredential();
                YouTube youtube = new YouTube.Builder(transport, jsonFactory, credential).setApplicationName(appName).build();

                YouTube.Videos.Insert videoInsert = youtube.videos().insert("snippet,statistics,status", videoObjectDefiningMetadata, mediaContent);

                uploader = videoInsert.getMediaHttpUploader();

            /*
             * Sets whether direct media upload is enabled or disabled. True = whole media content is
             * uploaded in a single request. False (default) = resumable media upload protocol to upload
             * in data chunks.
             */
                uploader.setDirectUploadEnabled(true);
                MediaHttpUploaderProgressListener progressListener = new MediaHttpUploaderProgressListener() {
                    public void progressChanged(MediaHttpUploader uploader) throws IOException {
                        switch (uploader.getUploadState()) {
                            case INITIATION_STARTED:
                                System.out.println("Initiation Started");
//                                Toast.makeText(MainActivity.this, "Initiation Started", Toast.LENGTH_LONG).show();

                                break;
                            case INITIATION_COMPLETE:
                                System.out.println("Initiation Completed");
//                                Toast.makeText(MainActivity.this, "Initiation Completed", Toast.LENGTH_LONG).show();
                                break;
                            case MEDIA_IN_PROGRESS:
                                System.out.println("Upload in progress");
                                System.out.println("Upload percentage: " + uploader.getProgress());
//                                Toast.makeText(MainActivity.this, "Upload percentage:", Toast.LENGTH_LONG).show();
                                //      if (youtubeUploadTask != null) {
                                try {
                                    int progress = (int) (getUploader().getProgress() * 100);
                                    if (progress < 10) {
                                        //   activity.getTextViewProgress().setText(" 0" + progress + "%");
                                        Log.d("PROGRESS :", " 0" + progress + "%");
                                    } else if (progress < 100) {
                                        //   activity.getTextViewProgress().setText(" " + progress + "%");
                                        Log.d("PROGRESS :", " " + progress + "%");
                                    } else {
                                        //  activity.getTextViewProgress().setText(progress + "%");
                                        Log.d("PROGRESS :", progress + "%");
                                    }
                                    Log.d("PROGRESS :", progress + "");
                                    //   activity.getProgressBarUploadVideo().setProgress(progress);
                                } catch (IOException e) {

                                }
                                //    }
                                break;
                            case MEDIA_COMPLETE:
                                System.out.println("Upload Completed!");
//                                Toast.makeText(MainActivity.this, "Upload Completed!", Toast.LENGTH_LONG).show();
//                                Toast.makeText(YoutubeUpload.this, "videoUploadCompleted", Toast.LENGTH_LONG).show();
                                break;
                            case NOT_STARTED:
                                System.out.println("Upload Not Started!");
                                break;
                        }
                    }
                };
                uploader.setProgressListener(progressListener);
                // Set chunk size. See http://stackoverflow.com/questions/13580109/check-progress-for-upload-download-google-drive-api-for-android-or-java
                uploader.setChunkSize(MediaHttpUploader.MINIMUM_CHUNK_SIZE * 2);

                return videoInsert;
            } catch (GoogleJsonResponseException e) {
                System.err.println("GoogleJsonResponseException code: " + e.getDetails().getCode() + " : "
                        + e.getDetails().getMessage());
                e.printStackTrace();
            } catch (IOException e) {
                System.err.println("IOException: " + e.getMessage());
                e.printStackTrace();
            } catch (Throwable t) {
                System.err.println("Throwable: " + t.getMessage());
                t.printStackTrace();

            }
            return null;
        }

        /**
         * Build the credential to authorize the installed application to access user's protected data.
         */
        private GoogleAccountCredential buildGoogleAccountCredential() throws Exception {
            GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(getActivity(), scopes);
            credential.setBackOff(new ExponentialBackOff());
            credential.setSelectedAccountName(userPref.getString("youtube_name", "NV"));
            return credential;
        }

        @Override
        protected Void doInBackground(Void... params) {

            YouTube.Videos.Insert videoInsert = prepareUpload();
            if (videoInsert != null) {
                try {
                    youtubeVideo = videoInsert.execute();
                } catch (IOException e) {
                }
            }
            return null;

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            mProgressBar.setIndeterminate(false);
            mProgressBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Void result) {
            if (errorMessage != null) {
                //  DialogUtil.showExceptionAlertDialog(activity, "Exception", errorMessage);
            } else if (youtubeVideo != null) {
                System.out.println("\n================== Returned Video ==================\n");
                System.out.println("  - Id: " + youtubeVideo.getId());
                System.out.println("  - Title: " + youtubeVideo.getSnippet().getTitle());
                System.out.println("  - Tags: " + youtubeVideo.getSnippet().getTags());
                System.out.println("  - Privacy Status: " + youtubeVideo.getStatus().getPrivacyStatus());
                System.out.println("  - Video Count: " + youtubeVideo.getStatistics().getViewCount());
                //
                JSONObject jsonMedia = new JSONObject();
                try {
                    jsonMedia.put("image_type", "image");
                    if (mediaUrl != null) {
                        jsonMedia.put("image_url", "https://www.youtube.com/watch?v=" + youtubeVideo.getId());
                        System.out.println(jsonMedia
                                + " VIDEO JSON ");
                        eventImages.put(jsonMedia);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                System.out.println("VIDEO LENGTH :" + videoPathsArr + "__" + eventImages.length());
                if (videoPathsArr.length == eventImages.length()) {
                    //     pDialogImg.setVisibility(View.GONE);
                    selectedMediaTxt.setText("Attached "
                            + videoPathsArr.length + " videos");
                    pDialogImg.setVisibility(View.GONE);
                    //     finishTxt.setEnabled(true);
                    MyApplication.getInstance().setUploadingMedia(false);
                    new CreatePhotosVideos().execute();
                } else {
                    pDialogImg.setVisibility(View.VISIBLE);
                    selectedMediaTxt.setText("Attaching videos...."
                            + (String.valueOf(eventImages.length())));
                    //   finishTxt.setEnabled(false);
                }
            }
        }

        public Video getUploadedVideo() {
            return youtubeVideo;
        }

        MediaHttpUploader getUploader() {
            return uploader;
        }
    }

    public class LoadImages extends AsyncTask<Void, Void, Void> {
        String[] allPhotopaths;
        File filePhoto;

        LoadImages(String[] all_path, File filee) {
            this.allPhotopaths = all_path;
            this.filePhoto = filee;
        }

        @Override
        protected void onPreExecute() {
            MyApplication.getInstance().setUploadingMedia(true);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Map response = MyApplication.cloudinary.uploader().upload(filePhoto, ObjectUtils.emptyMap());
                System.out.println(response + "  IMAGE  RESULT ");

                JSONObject jsonMedia = new JSONObject();
                jsonMedia.put("image_type", "image");
                jsonMedia.put("image_url", response.get("url").toString().trim());
                System.out.println(jsonMedia
                        + " +++++++++++++++++++++++ ");
                eventImages.put(jsonMedia);
                System.out.println(eventImages
                        + " ******************* ");

                //  eventImages.put(jsonMedia);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            if (allPhotopaths.length == eventImages.length()) {
                selectedMediaTxt.setText("Attached "
                        + allPhotopaths.length + " images");
                pDialogImg.setVisibility(View.GONE);
                //     finishTxt.setEnabled(true);
                MyApplication.getInstance().setUploadingMedia(false);
            } else {
                selectedMediaTxt.setText("Attaching images...."
                        + (String.valueOf(eventImages.length())));

            }
        }
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
