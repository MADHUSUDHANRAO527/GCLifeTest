package mobile.gclifetest.youtube;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.google.android.gms.common.AccountPicker;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mobile.gclifetest.activity.HomeActivity;
import mobile.gclifetest.activity.R;
import mobile.gclifetest.custom.CustomImgGalleryActivity;
import mobile.gclifetest.custom.GalleryImgAdapter;
import mobile.gclifetest.event.AddIdeasEvent;
import mobile.gclifetest.fragments.PhotosVideosListFragment;
import mobile.gclifetest.http.EvenstPost;
import mobile.gclifetest.http.SocietyNameGet;
import mobile.gclifetest.materialDesign.ProgressBarCircularIndeterminate;
import mobile.gclifetest.pojoGson.AvenuesFilter;
import mobile.gclifetest.utils.Constants;
import mobile.gclifetest.utils.GclifeApplication;
import mobile.gclifetest.youtube.dialog.ConfirmUploadVideoDialogBuilder;
import mobile.gclifetest.youtube.handler.FetchTokenHandler;
import mobile.gclifetest.youtube.handler.UploadProgressHandler;
import mobile.gclifetest.youtube.task.FetchYouTubeTokenTask;
import mobile.gclifetest.youtube.task.YouTubeUploadTask;
import mobile.gclifetest.youtube.util.DialogUtil;

/**
 * Created by MRaoKorni on 8/19/2016.
 */
public class PhotosCreateFrag extends Fragment {
    Context context;
    private String videoFileName = "";
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
    List<AvenuesFilter> societiesPojo = new ArrayList<AvenuesFilter>();
    ListView listviewAvenue, listviewSoci, listviewMem;
    ListSociBaseAdapter sociAdapter;
    SparseBooleanArray mChecked = new SparseBooleanArray();
    private static boolean isNotAdded = true;
    private CheckBox checkBox_header;
    private YouTubeUploadTask youtubeUploadTask = null;
    private String selectedGoogleAccount;
    private FetchTokenHandler fetchTokenHandler = null;
    private UploadProgressHandler uploadProgressHandler = null;
    View v;
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
         v = inflater.inflate(
                R.layout.photos_create, container, false);
        context = getActivity();

        userPref = context.getSharedPreferences("USER", Context.MODE_PRIVATE);
        addBtn = (FloatingActionButton) v.findViewById(R.id.addBtn);
        titleEdit = (EditText) v.findViewById(R.id.titleEdit);
        finishTxt = (TextView) v.findViewById(R.id.finishTxt);
        selectedMediaTxt = (TextView) v.findViewById(R.id.selectedMedia);
        avenueLay = (RelativeLayout) v.findViewById(R.id.avenueLay);
        sociLay = (RelativeLayout) v.findViewById(R.id.societyLay);
        memLay = (RelativeLayout) v.findViewById(R.id.memberLay);
        pDialog = (ProgressBarCircularIndeterminate) v.findViewById(R.id.pDialog);
        pDialogImg = (ProgressBarCircularIndeterminate) v.findViewById(R.id.pDialogImg);
        finishTxt = (TextView) v.findViewById(R.id.finishTxt);
        attachImg = (ImageView) v.findViewById(R.id.attachImg);

        Bundle bundle = getArguments();
        eventName = bundle.getString("EventName");

        //youtube
        uploadProgressHandler = new UploadProgressHandler(this);
        fetchTokenHandler = new FetchTokenHandler(this);

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

    /*
             * mListView >> (ListView) //DO NOT ADD `NULL` here.
             */

        final View headerView = getActivity().getLayoutInflater().inflate(R.layout.custom_list_view_header,
                listviewSoci, false);

        checkBox_header = (CheckBox) headerView.findViewById(
                R.id.checkBox_header);

            /*
             * Select All / None DO NOT USE "setOnCheckedChangeListener" here.
             */
        checkBox_header.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                    /*
                     * Set all the checkbox to True/False
                     */
                if (checkBox_header.isChecked()) {
                    for (int i = 0; i < societiesPojo.size(); i++) {
                        mChecked.put(i, checkBox_header.isChecked());
                        selectedSociNames.add(societiesPojo.get(i).getSocityName());

                    }
                } else {
                    for (int i = 0; i < societiesPojo.size(); i++) {
                        mChecked.delete(i);
                        //  selectedSociNames.remove(societiesPojo.get(i).getSocityName());

                    }
                    //   societiesPojo=new ArrayList<AvenuesFilter>();
                    selectedSociNames = new ArrayList<String>();
                }
                    /*
                     * Update View
                     */
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
                //    selectedSoci = selectedSoci.replaceAll("\\s", "");
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
                // selectedMem=selectedMem.trim();
                //         selectedMem = selectedMem.replaceAll("\\s", "");
                System.out.println(selectedMem);
            }
        });
        avenueLay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

				/*
                 * adapterUnames = new ListUnamesBaseAdapter( getActivity(),
				 * listUnames);
				 */
                // listviewUsernames.setAdapter(adapterUnames);
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
                new ConfirmUploadVideoDialogBuilder(context, uploadProgressHandler).create().show();
               /* if (ideaTitle == null || ideaTitle == "null" || ideaTitle == ""
                        || ideaTitle.length() == 0) {
                    Constants.showSnack(context,
                            "Please enter title!",
                            "OK");
                } else if (eventImages == null || eventImages.toString() == "null"
                        || eventImages.toString() == "" || eventImages.length() == 0) {
                    if (eventName == "Photos" || eventName.equals("Photos")) {
                        Constants.showSnack(context,
                                "Please select atleast one image!",
                                "OK");
                    } else {
                        Constants.showSnack(context,
                                "Please select atleast one video!",
                                "OK");
                    }

                } else if (selectedAven == null || selectedAven == "null"
                        || selectedAven == "" || selectedAven.length() == 0) {
                    Constants.showSnack(context,
                            "Please select avenue name!", "OK");
                } else if (selectedSoci == null || selectedSoci == "null"
                        || selectedSoci == "" || selectedSoci.length() == 0) {
                    Constants.showSnack(context,
                            "Please select society name!", "OK");
                } else if (selectedMem == null || selectedMem == "null"
                        || selectedMem == "" || selectedMem.length() == 0) {
                    Constants.showSnack(context,
                            "Please select member type!", "OK");
                } else {
                    new CreatePhotosVideos().execute();
                }*/
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
                   /* Intent i = new Intent(getActivity(), CustomVideoGalleryActivity.class);
                    startActivityForResult(i, 200);*/
                    startPickVideo();
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
                jsonIdeas.put("title", ideaTitle);
                jsonIdeas.put("user_id", userPref.getString("USERID", "NV"));
                jsonIdeas.put("association_list", selectedAven);
                jsonIdeas.put("society_list", selectedSoci);
                jsonIdeas.put("member_type_list", selectedMem);


                System.out.println(eventImages + "!!!!!!!!!!!!!!!!!!!!!!!");

                if (eventImages.toString() == "[]"
                        || eventImages.toString().equals("[]")
                        || eventImages.length() == 0) {
                    // jsonIdeas.put("EventImages", "[]");
                } else {
                    jsonIdeas.put("EventImages", eventImages);
                }

                JSONObject eventJson = new JSONObject();
                eventJson.put("event", jsonIdeas);
                try {
                    jsonResult = EvenstPost.makeRequest(eventJson, GclifeApplication.HOSTNAME);
                    System.out.println("RESPONSE :::::::::::::::::::::"
                            + jsonResult);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
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
                Constants.showSnack(v,
                        "Oops! Something went wrong. Please wait a moment!",
                        "OK");
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
                jsonResultArry = SocietyNameGet.callSocietyList(GclifeApplication.HOSTNAME);
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
                    aveAdapter = new ListAvenBaseAdapter(getActivity(),
                            listAssociation, listviewAvenue);
                    listviewAvenue.setAdapter(aveAdapter);


                    ArrayList<String> societylist = societyMap
                            .get(listAssociation.get(1));
                    aveAdapter = new ListAvenBaseAdapter(getActivity(),
                            listAssociation, listviewAvenue);
                    listviewAvenue.setAdapter(aveAdapter);
                    Log.d("AVENUES : ", listAssociation.toString());

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            } else {

                Constants.showSnack(v,
                        "Oops! Something went wrong. Please wait a moment!",
                        "OK");
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == IntentRequestCode.TAKE_VIDEO && resultCode == getActivity().RESULT_OK) {
            // videoFileName has been prepared for taking video
            File file = new File(videoFileName);
            // On Android 2.2, the file may not be created, therefore we need to check the returned URI.
            if (!file.exists()) {
                if (data.getData() != null) {
                    videoFileName = getRealPathFromURI(data.getData());

                }
                else {
                    videoFileName = null;
                    Toast.makeText(context, "videoNotAvailable", Toast.LENGTH_LONG).show();
                }
            }
            else {

            }
        }
        else if (requestCode == IntentRequestCode.PICK_UP_VIDEO && resultCode == getActivity().RESULT_OK) {
            Uri selectedVideo = data.getData();
             videoFileName = getRealPathFromURI(selectedVideo);
            if(videoFileName != null) {
               // onVideoReady();
            }
            else {
                Toast.makeText(context, "videoNotAvailable", Toast.LENGTH_LONG).show();
            }
        }
        else if (requestCode ==  IntentRequestCode.REQUEST_ACCOUNT_PICKER &&resultCode == Activity.RESULT_OK) {
            if(data != null	&& data.getExtras() != null) {
                String accountName = data.getExtras().getString(AccountManager.KEY_ACCOUNT_NAME);
                if (accountName != null) {
                    selectedGoogleAccount = accountName;
                    fetchToken();
                }
                else {
                    DialogUtil.showExceptionAlertDialog(context, getString(R.string.googleAccountNotSelected), getString(R.string.googleAccountNotSupported));
                }
            }
        }
        else if(requestCode == IntentRequestCode.REQUEST_AUTHORIZATION && resultCode == Activity.RESULT_OK) {
            // Account has been chosen and permissions have been granted. You can upload video
            Toast.makeText(context, getString(R.string.appAuthorized), Toast.LENGTH_LONG).show();
        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    public String getPath(Uri uri) {

        String[] projection = {MediaStore.Images.Media.DATA};

        Cursor cursor = getActivity().managedQuery(uri, projection, null, null, null);

        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        return cursor.getString(column_index);

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

                            for (int j = 1; j <= 2; j++) {
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

                /*
                 * LayoutInflater
                 */
                final LayoutInflater sInflater = (LayoutInflater) context.getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);

                /*
                 * Inflate Custom List View
                 */
                mView = sInflater.inflate(R.layout.avenues_adapter_row, null, false);

            }

            /* **************CUSTOM LISTVIEW OBJECTS**************** */

            /*
             * DO NOT MISS TO ADD "mView"
             */
            final TextView titleTxt = (TextView) mView.findViewById(R.id.titleTxt);
            //      final ImageView sIMG = (ImageView) mView.findViewById(R.id.imageView);
            final CheckBox mCheckBox = (CheckBox) mView.findViewById(
                    R.id.checked);

            /* **************CUSTOM LISTVIEW OBJECTS**************** */

            /* **************ADDING CONTENTS**************** */
            //   sTV1.setText(MainActivity.textviewContent[position]);
            //    sIMG.setImageResource(R.drawable.ic_launcher);
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

                                /*
                                 * Removed UnChecked Position
                                 */
                        mChecked.delete(position);

                                /*
                                 * Remove Checked in Header
                                 */
                        checkBox_header.setChecked(mCheckBox.isChecked());
                        selectedSociNames.remove(societiesPojo.get(position).getSocityName());
                    }
                }
            });


           /* mCheckBox.setOnCheckedChangeListener(
                    new CompoundButton.OnCheckedChangeListener() {

                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {

                                *//*
                                 * Saving Checked Position
                                 *//*
                                mChecked.put(position, isChecked);

                                if (isAllValuesChecked()) {
                                    checkBox_header.setChecked(isChecked);
                                }
                                selectedSociNames.add(societiesPojo.get(position).getSocityName());
                            } else {

                                *//*
                                 * Removed UnChecked Position
                                 *//*
                                mChecked.delete(position);

                                *//*
                                 * Remove Checked in Header
                                 *//*
                                checkBox_header.setChecked(isChecked);
                                selectedSociNames.remove(societiesPojo.get(position).getSocityName());
                            }

                        }
                    });*/

            /*
             * Set CheckBox "TRUE" or "FALSE" if mChecked == true
             */
            mCheckBox.setChecked((mChecked.get(position) == true ? true : false));

            /* **************ADDING CONTENTS**************** */

            /*
             * Return View here
             */
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
                            //  selectedMemsNames.add("All");
                            //  return;
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
        }else
            ((HomeActivity) context).changeToolbarTitle(R.string.create_videos);
    }

    public void uploadYouTubeVideo() {
        youtubeUploadTask = new YouTubeUploadTask(context, videoFileName, getString(R.string.app_name),  "Title",
                "disc", selectedGoogleAccount, uploadProgressHandler);
        youtubeUploadTask.execute();
    }

    public void chooseAccount() {
        String[] accountTypes = new String[]{"com.google"};
        Intent intent = AccountPicker.newChooseAccountIntent(null, null, accountTypes, false, null, null, null, null);
        startActivityForResult(intent, IntentRequestCode.REQUEST_ACCOUNT_PICKER);
    }

    public void setSelectedGoogleAccount(String account) {
        selectedGoogleAccount = account;
    }

    private void fetchToken() {
        new FetchYouTubeTokenTask(this, selectedGoogleAccount, fetchTokenHandler).execute();
    }
    private void startPickVideo() {

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        try {
            startActivityForResult(intent, IntentRequestCode.PICK_UP_VIDEO);
        }
        catch (ActivityNotFoundException e) {
            // On Andriod 2.2, the above method may cause exception due to not finding an activity to handle the intent. Use the method below instead.
            Intent mediaChooser = new Intent(Intent.ACTION_GET_CONTENT);
            mediaChooser.setType("video/*");
            startActivityForResult(mediaChooser, IntentRequestCode.PICK_UP_VIDEO);
        }
        catch (SecurityException e) {
            // When picking up videos, there may be an exception like:
            //  java.lang.SecurityException:
            //      Permission Denial:
            //      starting Intent { act=android.intent.action.PICK
            //      dat=content://media/external/video/media
            //      cmp=com.android.music/.VideoBrowserActivity } from ProcessRecord
            // Try another way to start the intent
            intent = new Intent(Intent.ACTION_PICK, null);
            intent.setType("video/*");
            try {
                startActivityForResult(intent, IntentRequestCode.PICK_UP_VIDEO);
            } catch (Exception ex) {
                //DialogUtil.showExceptionAlertDialog(UploadVideoActivity.this, getString(R.string.cannotPickUpVideo), getString(R.string.notSupportedOnDevice));
            }
        }
    }
    private String getRealPathFromURI(Uri contentUri) {
        String filePath = null;
        String[] projection = { MediaStore.Video.Media.DATA };
        Cursor cursor = context.getContentResolver().query(contentUri, projection, null, null, null);
        if(cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndexOrThrow(projection[0]);
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;
    }
}
