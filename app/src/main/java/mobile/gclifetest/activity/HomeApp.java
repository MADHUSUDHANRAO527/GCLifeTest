package mobile.gclifetest.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gc.materialdesign.widgets.SnackBar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.lang.reflect.Field;

import mobile.gclifetest.pojoGson.UserDetailsPojo;
import mobile.gclifetest.utils.InternetConnectionDetector;
import mobile.gclifetest.utils.MyApplication;
import mobile.gclifetest.db.DatabaseHandler;
import mobile.gclifetest.http.MemsPost;

public class HomeApp extends ActionBarActivity {
    android.support.v7.app.ActionBar actionBar;
    GridView gv;
    Context context;
    public static String[] prgmNameList = {"GC News", "Notice Board",
            "My Society", "Ideas", "Write", "Photos", "Videos", "Friends",
            "Imp Contacts", "Admin"};
    public static int[] prgmImages = {R.drawable.icon_news,
            R.drawable.icon_noticeboard, R.drawable.icon_society,
            R.drawable.icon_ideas, R.drawable.icon_write,
            R.drawable.icon_photos, R.drawable.icon_videos,
            R.drawable.icon_friends, R.drawable.icon_contacts, R.drawable.admin};
    static Typeface typefaceLight;
    JSONObject jsonLatestusers;
    SharedPreferences userPref;
    Editor editor;
    String userStatus = "";
    InternetConnectionDetector netConn;
    Boolean isInternetPresent = false;
    UserDetailsPojo user;
    RelativeLayout viewTouch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        gv = (GridView) findViewById(R.id.grid);
        gv.setAdapter(new CustomAdapter(HomeApp.this, prgmNameList, prgmImages));
        getOverflowMenu();
        context = this;
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setIcon(android.R.color.transparent);
        actionBar.setTitle("Home");
        actionBar.setBackgroundDrawable(new ColorDrawable(Color
                .parseColor(MyApplication.actiobarColor)));
        typefaceLight = Typeface.createFromAsset(getAssets(),
                "fonts/RobotoLight.ttf");
        netConn = new InternetConnectionDetector(this);
        isInternetPresent = netConn.isConnectingToInternet();

        viewTouch = (RelativeLayout) findViewById(R.id.view);

        userPref = getSharedPreferences("USER", MODE_PRIVATE);
        editor = userPref.edit();

        if (userPref.getString("USERID", "NV") == "NV"
                || userPref.getString("USERID", "NV").equals("NV")) {

        } else {
            if (isInternetPresent) {
                new LatestUserDetails().execute();
            } else {

                showSnack(HomeApp.this, "Please check network connection!",
                        "OK");

            }
        }
        viewTouch.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // show dialog here

                return false;
            }
        });

        gv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                Gson gson = new Gson();
                String jsonUser = userPref.getString("USER_DATA", "NV");
                user = gson.fromJson(jsonUser, UserDetailsPojo.class);

                System.out.println(userStatus
                        + "^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");

                userStatus = user.getActive();

                System.out.println(userStatus);
                InternetConnectionDetector netConn = new InternetConnectionDetector(
                        HomeApp.this);
                Boolean isInternetPresent = netConn.isConnectingToInternet();
                if (isInternetPresent) {
                    isInternetPresent = true;
                } else {
                    System.out.println(isInternetPresent
                            + " Please check your internet settings! ");
                    showSnack(HomeApp.this, "Please check network connection!",
                            "OK");

                }

                if (userStatus == "Approve" || userStatus.equals("Approve")) {


                    if (position == 0) {
                        Intent soc = new Intent(HomeApp.this,
                                IdeasList.class);
                        soc.putExtra("EventName", "News");
                        startActivity(soc);
                        overridePendingTransition(R.anim.slide_in_left,
                                R.anim.slide_out_left);
                    } else if (position == 1) {
                        Intent soc = new Intent(HomeApp.this,
                                IdeasList.class);
                        soc.putExtra("EventName", "Notice");
                        startActivity(soc);
                        overridePendingTransition(R.anim.slide_in_left,
                                R.anim.slide_out_left);
                    } else if (position == 2) {
                        Intent soc = new Intent(HomeApp.this,
                                MySociety.class);
                        startActivity(soc);
                        overridePendingTransition(R.anim.slide_in_left,
                                R.anim.slide_out_left);
                    } else if (position == 3) {
                        Intent dash = new Intent(HomeApp.this,
                                IdeasList.class);
                        dash.putExtra("EventName", "Ideas");
                        startActivity(dash);
                        overridePendingTransition(R.anim.slide_in_left,
                                R.anim.slide_out_left);
                    } else if (position == 4) {
                        Intent dash = new Intent(HomeApp.this, InBox.class);
                        startActivity(dash);
                        overridePendingTransition(R.anim.slide_in_left,
                                R.anim.slide_out_left);
                    } else if (position == 5) {
                        Intent dash = new Intent(HomeApp.this,
                                PhotosList.class);
                        dash.putExtra("EventName", "Photos");
                        startActivity(dash);
                        overridePendingTransition(R.anim.slide_in_left,
                                R.anim.slide_out_left);
                    } else if (position == 6) {
                        Intent dash = new Intent(HomeApp.this,
                                PhotosList.class);
                        dash.putExtra("EventName", "Videos");
                        startActivity(dash);
                        overridePendingTransition(R.anim.slide_in_left,
                                R.anim.slide_out_left);
                    } else if (position == 7) {
                        Intent dash = new Intent(HomeApp.this,
                                FrdsList.class);
                        startActivity(dash);
                        overridePendingTransition(R.anim.slide_in_left,
                                R.anim.slide_out_left);
                    } else if (position == 8) {
                        Intent dash = new Intent(HomeApp.this,
                                ImpContacts.class);
                        startActivity(dash);
                        overridePendingTransition(R.anim.slide_in_left,
                                R.anim.slide_out_left);
                    } else if (position == 9) {
                        Log.d("MEM TYPE", user.getGclife_registration_flatdetails().get(0).getMember_type());
                        if (user.getGclife_registration_flatdetails().get(0).getMember_type() == "Non_members" ||
                                user.getGclife_registration_flatdetails().get(0).getMember_type().equals("Non_members") || user.getGclife_registration_flatdetails().get(0).getMember_type() == "Member" ||
                                user.getGclife_registration_flatdetails().get(0).getMember_type().equals("Member")) {
                            showSnack(HomeApp.this, "You are not authorized person!", "");
                        } else {
                            Intent dash = new Intent(HomeApp.this,
                                    AdminGrid.class);
                            startActivity(dash);
                            overridePendingTransition(R.anim.slide_in_left,
                                    R.anim.slide_out_left);
                        }
                    } else {
                        showSnack(HomeApp.this, "Coming soon!", "OK");
                    }
                } else {
                    showSnack(HomeApp.this, "Please contact admin!", "OK");
                }

            }
        });
    }

    void getOverflowMenu() {

        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class
                    .getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class CustomAdapter extends BaseAdapter {

        String[] result;
        Context context;
        int[] imageId;
        LayoutInflater inflater = null;

        public CustomAdapter(HomeApp mainActivity, String[] prgmNameList,
                             int[] prgmImages) {
            // TODO Auto-generated constructor stub
            result = prgmNameList;
            context = mainActivity;
            imageId = prgmImages;
            inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return result.length;
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        public class Holder {
            TextView tv;
            ImageView img;
            LinearLayout rowLay;
        }

        @Override
        public View getView(int position, View convertView,
                            ViewGroup parent) {
            // TODO Auto-generated method stub
            Holder holder = new Holder();
            View rowView;

            rowView = inflater.inflate(R.layout.home_grid_row, null);
            holder.tv = (TextView) rowView.findViewById(R.id.textView1);
            holder.img = (ImageView) rowView.findViewById(R.id.imageView1);
            holder.rowLay = (LinearLayout) rowView.findViewById(R.id.rowLay);
            holder.tv.setTypeface(typefaceLight);
            holder.tv.setText(result[position]);
            holder.img.setImageResource(imageId[position]);
            if (position == 0) {
                holder.rowLay.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.homebg));
            } else if (position == 1) {
                holder.rowLay.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.homebgg));
            } else if (position == 2) {
                holder.rowLay.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.homebgg));
            } else if (position == 3) {
                holder.rowLay.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.homebg));
            } else if (position == 4) {
                holder.rowLay.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.homebg));
            } else if (position == 5) {
                holder.rowLay.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.homebgg));
            } else if (position == 6) {
                holder.rowLay.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.homebgg));
            } else if (position == 7) {
                holder.rowLay.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.homebg));
            } else if (position == 8) {
                holder.rowLay.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.homebg));
            } else if (position == 9) {
                holder.rowLay.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.homebgg));
            }
            return rowView;
        }
    }

    public class LatestUserDetails extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub
            try {
                jsonLatestusers = MemsPost.latestUsersDetails(MyApplication.HOSTNAME,
                        userPref.getString("USERID", "NV"));
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            System.out.println(jsonLatestusers + "      LATEST USERS     ");
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {

            if (jsonLatestusers != null) {
                Gson gson = new GsonBuilder().create();
                MyApplication.user = gson.fromJson(jsonLatestusers.toString(),
                        UserDetailsPojo.class);

                Gson gsonn = new Gson();
                String json = gsonn.toJson(MyApplication.user);

                editor.putString("USER_DATA", json);
                editor.commit();
            } else {

            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.logout_menu, menu);
        MenuItem itemLogout = menu.findItem(R.id.logOut);
        itemLogout.setVisible(true);

        return super.onCreateOptionsMenu(menu);
    }

    private static final int TIME_INTERVAL = 10000; // # milliseconds, desired
    private long mBackPressed;

    @Override
    public void onBackPressed() {
        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
        } else {
            showSnack(HomeApp.this, "Press again to close app!", "OK");
        }

        mBackPressed = System.currentTimeMillis();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.logOut:
                editor.clear();
                editor.commit();
                context.deleteDatabase(DatabaseHandler.DATABASE_NAME);
                showSnack(HomeApp.this, "You have been logged out!", "OK");
                Intent i = new Intent(HomeApp.this, Login.class);
                startActivity(i);
                break;
            case R.id.refreh:
                Intent intent = getIntent();
                finish();
                startActivity(intent);
                break;
            case R.id.profile:
                Intent ii = new Intent(HomeApp.this, UserProfile.class);
                ii.putExtra("EACH_USER_DET", "");
                startActivity(ii);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    void showSnack(HomeApp flats, String stringMsg, String ok) {
        new SnackBar(HomeApp.this, stringMsg, ok, new OnClickListener() {

            @Override
            public void onClick(View v) {
            }
        }).show();
    }
}
