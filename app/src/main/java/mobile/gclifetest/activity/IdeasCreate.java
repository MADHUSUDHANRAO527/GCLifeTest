package mobile.gclifetest.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.gc.materialdesign.views.ButtonFloat;
import com.gc.materialdesign.widgets.SnackBar;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mobile.gclifetest.MaterialDesign.ProgressBarCircularIndeterminate;
import mobile.gclifetest.PojoGson.AvenuesFilter;
import mobile.gclifetest.Utils.MyApplication;
import mobile.gclifetest.custom.CustomGallery;
import mobile.gclifetest.custom.CustomImgGalleryActivity;
import mobile.gclifetest.custom.GalleryImgAdapter;
import mobile.gclifetest.http.EvenstPost;
import mobile.gclifetest.http.SocietyNameGet;

@SuppressWarnings("deprecation")
public class IdeasCreate extends BaseActivity {
    ButtonFloat addBtn;
    EditText titleEdit, shortDisEdit, briefDiscEdit;
    JSONObject jsonResult;
    String ideaTitle, ideaSDisc = "", ideaBdisc = "",
            avenueName = "All", societyName = "All", memberType = "All",
            selectedPath1, ext, fileName, mediaUrl = "", eventName,
            selectedAven = "", selectedSoci = "", selectedMem = "";
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
    RelativeLayout avenueLay, sociLay, memLay, societyLayy;
    JSONArray eventImages = new JSONArray();
    ListView listviewAvenue, listviewSoci, listviewMem;
    Dialog dialogAvenue, dialogSoci, dialogMems;
    Typeface typefaceLight;
    ListAvenBaseAdapter aveAdapter;
    ArrayList<String> selectedAveNames = new ArrayList<String>();
    ArrayList<String> selectedSociNames = new ArrayList<String>();
    ArrayList<String> selectedMemsNames = new ArrayList<String>();
    boolean mems = false;
    ArrayList<String> myFinalList = new ArrayList<String>();
    boolean apiProgress;
    List<AvenuesFilter> societiesPojo = new ArrayList<AvenuesFilter>();

    ListSociBaseAdapter sociAdapter;

    SparseBooleanArray mChecked = new SparseBooleanArray();
    private static boolean isNotAdded = true;
    private CheckBox checkBox_header;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ideas_create);
        userPref = getSharedPreferences("USER", MODE_PRIVATE);

        //  filter.setSocityName("All");
        addBtn = (ButtonFloat) findViewById(R.id.addBtn);
        titleEdit = (EditText) findViewById(R.id.titleEdit);
        shortDisEdit = (EditText) findViewById(R.id.shortDisEdit);
        briefDiscEdit = (EditText) findViewById(R.id.briefDiscEdit);
        finishTxt = (TextView) findViewById(R.id.finishTxt);
        selectedMediaTxt = (TextView) findViewById(R.id.selectedMedia);
        avenueLay = (RelativeLayout) findViewById(R.id.avenueLay);
        sociLay = (RelativeLayout) findViewById(R.id.societyLay);
        memLay = (RelativeLayout) findViewById(R.id.memberLay);

        pDialog = (ProgressBarCircularIndeterminate) findViewById(R.id.pDialog);
        pDialogImg = (ProgressBarCircularIndeterminate) findViewById(R.id.pDialogImg);
        finishTxt = (TextView) findViewById(R.id.finishTxt);
        attachImg = (ImageView) findViewById(R.id.attachImg);
        typefaceLight = Typeface.createFromAsset(getAssets(),
                "fonts/RobotoLight.ttf");
      /*  titleEdit.setTypeface(typefaceLight);
        shortDisEdit.setTypeface(typefaceLight);
        briefDiscEdit.setTypeface(typefaceLight);*/
        myFinalList.add("All");
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        eventName = bundle.getString("EventName");
        if (eventName == "Ideas" || eventName.equals("Ideas")) {
            setUpActionBar("Create idea");
        } else if (eventName == "News" || eventName.equals("News")) {
            setUpActionBar("Create News");
        } else {
            setUpActionBar("Create NoticeBoard");
        }

        dialogAvenue = new Dialog(IdeasCreate.this);
        dialogAvenue.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogAvenue.setContentView(R.layout.avenues_listview_check);
        dialogAvenue.getWindow().getAttributes().windowAnimations = R.style.popup_login_dialog_animation;
        listviewAvenue = (ListView) dialogAvenue
                .findViewById(R.id.listviewAven);

        dialogSoci = new Dialog(IdeasCreate.this);
        dialogSoci.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSoci.setContentView(R.layout.soci_listview_check);
        dialogSoci.getWindow().getAttributes().windowAnimations = R.style.popup_login_dialog_animation;
        listviewSoci = (ListView) dialogSoci.findViewById(R.id.listviewSoci);
        societyLayy = (RelativeLayout) dialogSoci.findViewById(R.id.societyLayy);

        //  if (isNotAdded) {

            /*
             * mListView >> (ListView) //DO NOT ADD `NULL` here.
             */

        final View headerView = getLayoutInflater().inflate(R.layout.custom_list_view_header,
                listviewSoci, false);

        checkBox_header = (CheckBox) headerView.findViewById(
                R.id.checkBox_header);

            /*
             * Select All / None DO NOT USE "setOnCheckedChangeListener" here.
             */
        checkBox_header.setOnClickListener(new OnClickListener() {

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


        dialogMems = new Dialog(IdeasCreate.this);
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
                IdeasCreate.this, membsList, listviewMem);
        listviewMem.setAdapter(memsAdapter);

        TextView noTxtave = (TextView) dialogAvenue.findViewById(R.id.noTxt);
        TextView yesTxtave = (TextView) dialogAvenue.findViewById(R.id.yesTxt);

        TextView noTxtSoc = (TextView) dialogSoci.findViewById(R.id.noTxt);
        TextView yesTxtSoc = (TextView) dialogSoci.findViewById(R.id.yesTxt);

        TextView noTxtmem = (TextView) dialogMems.findViewById(R.id.noTxt);
        TextView yesTxtmem = (TextView) dialogMems.findViewById(R.id.yesTxt);
        noTxtave.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialogAvenue.dismiss();
            }
        });
        yesTxtave.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (societiesPojo.size() < 3) {
                    societyLayy.getLayoutParams().height = 450;  // replace 100 with your dimensions
                    societyLayy.getLayoutParams().width = RelativeLayout.LayoutParams.MATCH_PARENT;
                } else {
                    societyLayy.getLayoutParams().height = RelativeLayout.LayoutParams.MATCH_PARENT;  // replace 100 with your dimensions
                    societyLayy.getLayoutParams().width = RelativeLayout.LayoutParams.MATCH_PARENT;
                }

                //        scrollpopap.setLayoutParams(rel_btn);
                //       scrollpopap.setScrollingEnabled(false);

                selectedAven = TextUtils.join(",", selectedAveNames);
                //   selectedAven = selectedAven.replaceAll("\\s", "");
                System.out.println(selectedAven);
                dialogAvenue.dismiss();
            }
        });
        noTxtSoc.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialogSoci.dismiss();
            }
        });
        yesTxtSoc.setOnClickListener(new OnClickListener() {

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
        noTxtmem.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialogMems.dismiss();
            }
        });
        yesTxtmem.setOnClickListener(new OnClickListener() {

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
        avenueLay.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                dialogAvenue.show();
            }
        });
        sociLay.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (selectedAveNames.size() > 0) {
                    dialogSoci.show();
                } else {
                    showSnack(IdeasCreate.this, "Select avenue name", "OK");
                }
            }
        });
        memLay.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialogMems.show();
            }
        });


        new SocietyNames().execute();

        finishTxt.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                ideaTitle = titleEdit.getText().toString();
                ideaSDisc = shortDisEdit.getText().toString();
                ideaBdisc = briefDiscEdit.getText().toString();

                if (ideaTitle == null || ideaTitle == "null" || ideaTitle == ""
                        || ideaTitle.length() == 0) {
                    showSnack(IdeasCreate.this, "Please enter title!", "OK");
                } else if (ideaSDisc == null || ideaSDisc == "null"
                        || ideaSDisc == "" || ideaSDisc.length() == 0) {
                    showSnack(IdeasCreate.this,
                            "Please enter short discription!", "OK");
                } else if (selectedAven == null || selectedAven == "null"
                        || selectedAven == "" || selectedAven.length() == 0) {
                    showSnack(IdeasCreate.this,
                            "Please select avenue name!", "OK");
                } else if (selectedSoci == null || selectedSoci == "null"
                        || selectedSoci == "" || selectedSoci.length() == 0) {
                    showSnack(IdeasCreate.this,
                            "Please select society name!", "OK");
                } else if (selectedMem == null || selectedMem == "null"
                        || selectedMem == "" || selectedMem.length() == 0) {
                    showSnack(IdeasCreate.this,
                            "Please select member type!", "OK");
                } else {
                    new CreateIdeas().execute();
                }
            }
        });

        attachImg.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                // openGallery(SELECT_FILE1);
                Intent i = new Intent(IdeasCreate.this,
                        CustomImgGalleryActivity.class);
                startActivityForResult(i, 200);
            }
        });
    }

    public class CreateIdeas extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            apiProgress = true;
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
                jsonIdeas.put("sdesc", ideaSDisc);
                jsonIdeas.put("bdesc", ideaBdisc);
                jsonIdeas.put("user_id", userPref.getString("USERID", "NV"));
                jsonIdeas.put("association_list", selectedAven);
                jsonIdeas.put("society_list", selectedSoci);
                jsonIdeas.put("member_type_list", selectedMem);

                if (eventImages.toString() == "[]"
                        || eventImages.toString().equals("[]")
                        || eventImages.length() == 0) {
                } else {
                    jsonIdeas.put("EventImages", eventImages);
                }

                JSONObject eventJson = new JSONObject();
                eventJson.put("event", jsonIdeas);
                try {
                    jsonResult = EvenstPost.makeRequest(eventJson, MyApplication.HOSTNAME);
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
                    Intent i = new Intent(IdeasCreate.this, IdeasList.class);
                    i.putExtra("EventName", eventName);
                    startActivity(i);
                    apiProgress = false;
                }
            } else {
                pDialog.setVisibility(View.GONE);
                finishTxt.setVisibility(View.VISIBLE);
                showSnack(IdeasCreate.this,
                        "Oops! Something went wrong. Please wait a moment!",
                        "OK");
                apiProgress = false;
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
                    aveAdapter = new ListAvenBaseAdapter(IdeasCreate.this,
                            listAssociation, listviewAvenue);
                    listviewAvenue.setAdapter(aveAdapter);
                    Log.d("AVENUES : ", listAssociation.toString());


                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            } else {

                showSnack(IdeasCreate.this,
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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 200 && resultCode == Activity.RESULT_OK) {
            final String[] all_path = data.getStringArrayExtra("all_path");
            if (all_path.length > 0) {
                pDialogImg.setVisibility(View.VISIBLE);
                selectedMediaTxt.setText("Attaching images....");
            }
            System.out.println(all_path);
            showSnack(IdeasCreate.this, "You have Selected " + all_path.length
                    + " images", "OK");

            ArrayList<CustomGallery> dataT = new ArrayList<CustomGallery>();

            for (String selectedPath1 : all_path) {
                CustomGallery item = new CustomGallery();
                item.sdcardPath = selectedPath1;

                dataT.add(item);

                // parsing
                ext = selectedPath1
                        .substring(selectedPath1.lastIndexOf(".") + 1);
                fileName = selectedPath1.replaceFirst(".*/(\\w+).*", "$1");

                // selectedMediaTxt.setText(fileName+"."+ext);

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
                final ParseFile file = new ParseFile(fileName + "." + ext,
                        bytes);
                file.saveInBackground(new SaveCallback() {

                    @Override
                    public void done(ParseException arg0) {
                        // TODO Auto-generated method stub
                        final ParseObject jobApplication = new ParseObject(
                                "Files");
                        if (ext == "png" || ext.equals("png")
                                || ext.equals("ico") || ext == "ico"
                                || ext == "jpg" || ext.equals("jpg")
                                || ext.equals("jpeg") || ext == "jpeg"
                                || ext == "gif" || ext.equals("gif")
                                || ext.equals("thm") || ext == "thm"
                                || ext == "tif" || ext.equals("tif")
                                || ext.equals("yuv") || ext == "yuv"
                                || ext.equals("bmp") || ext == "bmp") {
                            jobApplication.put("mediatype", "image");
                        } else {
                            jobApplication.put("mediatype", "video");
                        }
                        // jobApplication.put("ticklecreatedid","Mayura");
                        jobApplication.put("file", file);
                        jobApplication.saveInBackground();
                        // file = jobApplication.getParseFile(file);
                        mediaUrl = file.getUrl();// live url
                        System.err.println(mediaUrl
                                + "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

                        try {
                            JSONObject jsonMedia = new JSONObject();
                            jsonMedia.put("image_type", "image");
                            jsonMedia.put("image_url", mediaUrl);
                            System.out.println(jsonMedia
                                    + " +++++++++++++++++++++++ ");
                            eventImages.put(jsonMedia);
                            System.out.println(eventImages
                                    + " ******************* ");

                            if (all_path.length == eventImages.length()) {
                                pDialogImg.setVisibility(View.GONE);
                                selectedMediaTxt.setText("Attached "
                                        + all_path.length + " images");
                                finishTxt.setClickable(true);
                                finishTxt.setEnabled(true);
                            } else {
                                pDialogImg.setVisibility(View.VISIBLE);
                                selectedMediaTxt.setText("Attaching images...."
                                        + (String.valueOf(eventImages.length())));
                                finishTxt.setClickable(false);
                                finishTxt.setEnabled(false);
                            }

                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                });
            }

            // adapter.addAll(dataT);

        }
    }

    public String getPath(Uri uri) {

        String[] projection = {MediaStore.Images.Media.DATA};

        Cursor cursor = managedQuery(uri, projection, null, null, null);

        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        return cursor.getString(column_index);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (apiProgress == false) {
            Intent soc = new Intent(IdeasCreate.this, IdeasList.class);
            soc.putExtra("EventName", eventName);
            startActivity(soc);
        } else {
            Log.d("API PROGRESS", apiProgress + "");
        }
    }

    void showSnack(IdeasCreate flats, String stringMsg, String ok) {
        new SnackBar(IdeasCreate.this, stringMsg, ok, new OnClickListener() {

            @Override
            public void onClick(View v) {
            }
        }).show();
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
            holder.checkBox.setOnClickListener(new OnClickListener() {

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
                            IdeasCreate.this, societiesPojo, listviewSoci);
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


            mCheckBox.setOnClickListener(new OnClickListener() {
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


            holder.checkBox.setOnClickListener(new OnClickListener() {

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
}
