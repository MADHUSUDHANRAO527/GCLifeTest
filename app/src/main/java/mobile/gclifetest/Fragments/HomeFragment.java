package mobile.gclifetest.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import mobile.gclifetest.activity.HomeActivity;
import mobile.gclifetest.activity.Login;
import mobile.gclifetest.activity.R;
import mobile.gclifetest.activity.UserProfile;
import mobile.gclifetest.adapters.HomeAdapter;
import mobile.gclifetest.db.DatabaseHandler;
import mobile.gclifetest.http.MemsPost;
import mobile.gclifetest.pojoGson.UserDetailsPojo;
import mobile.gclifetest.utils.Constants;
import mobile.gclifetest.utils.InternetConnectionDetector;
import mobile.gclifetest.utils.MyApplication;

/**
 * Created by MRaoKorni on 8/1/2016.
 */
public class HomeFragment extends Fragment {
    GridView gv;
    Context context;
    SharedPreferences userPref;
    UserDetailsPojo user;
    String userStatus = "";
    SharedPreferences.Editor editor;
    InternetConnectionDetector netConn;
    Boolean isInternetPresent = false;
    JSONObject jsonLatestusers;
    View v;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(
                R.layout.home, container, false);
        context = getActivity();
        gv = (GridView) v.findViewById(R.id.grid);
        gv.setAdapter(new HomeAdapter(context, Constants.homeMenuList, Constants.homeMenuImages));
        netConn = new InternetConnectionDetector(getActivity());
        isInternetPresent = netConn.isConnectingToInternet();
        userPref = context.getSharedPreferences("USER", context.MODE_PRIVATE);
        editor = userPref.edit();

        if (userPref.getString("USERID", "NV") == "NV"
                || userPref.getString("USERID", "NV").equals("NV")) {

        } else {
            if (isInternetPresent) {
                new LatestUserDetails().execute();
            } else {

                Constants.showSnack(v, "Please check network connection!",
                        "OK");

            }
        }


        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                Gson gson = new Gson();
                String jsonUser = userPref.getString("USER_DATA", "NV");
                user = gson.fromJson(jsonUser, UserDetailsPojo.class);

                System.out.println(userStatus
                        + "^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");

                userStatus = user.getActive();

                InternetConnectionDetector netConn = new InternetConnectionDetector(
                        getActivity());
                Boolean isInternetPresent = netConn.isConnectingToInternet();
                if (isInternetPresent) {
                    isInternetPresent = true;
                } else {

                    Constants.showSnack(v, "Please check network connection!",
                            "OK");

                }
                if (userStatus == "Approve" || userStatus.equals("Approve")) {
                    if (position == 0) {
                        IdeasListFragment fragment = new IdeasListFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("EventName", "News");
                        fragment.setArguments(bundle);
                        ((HomeActivity) context).addFragment(fragment);
                    } else if (position == 1) {
                        IdeasListFragment fragment = new IdeasListFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("EventName", "Notice");
                        fragment.setArguments(bundle);
                        ((HomeActivity) context).addFragment(fragment);
                    } else if (position == 2) {
                        ((HomeActivity) context).addFragment(new MySocietyFragment());
                    } else if (position == 3) {
                        IdeasListFragment fragment = new IdeasListFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("EventName", "Ideas");
                        fragment.setArguments(bundle);
                        ((HomeActivity) context).addFragment(fragment);
                    } else if (position == 4) {
                        Intent dash = new Intent(getActivity(), InboxActivity.class);
                        startActivity(dash);
                       /* getActivity().overridePendingTransition(R.anim.slide_in_left,
                                R.anim.slide_out_left);*/
                    } else if (position == 5) {
                        PhotosVideosListFragment fragment = new PhotosVideosListFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("EventName", "Photos");
                        fragment.setArguments(bundle);
                        ((HomeActivity) context).addFragment(fragment);
                    } else if (position == 6) {
                        PhotosVideosListFragment fragment = new PhotosVideosListFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("EventName", "Videos");
                        fragment.setArguments(bundle);
                        ((HomeActivity) context).addFragment(fragment);
                    } else if (position == 7) {
                        ((HomeActivity) context).addFragment(new FrdsListFragment());
                    } else if (position == 8) {
                        ((HomeActivity) context).addFragment(new ImpContactsFragment());
                    } else if (position == 9) {
                        Log.d("MEM TYPE", user.getGclife_registration_flatdetails().get(0).getMember_type());
                        boolean allowAdmin=true;
                        for (int i = 0; i < user.getGclife_registration_flatdetails().size(); i++) {
                            if (user.getGclife_registration_flatdetails().get(i).getMember_type() == "Non_members" ||
                                    user.getGclife_registration_flatdetails().get(i).getMember_type().equals("Non_members") || user.getGclife_registration_flatdetails().get(i).getMember_type() == "Member" ||
                                    user.getGclife_registration_flatdetails().get(i).getMember_type().equals("Member")) {

                                allowAdmin=false;
                            } else {
                                allowAdmin=true;
                                break;
                            }
                            Log.d("MEMBER_TYPE", user.getGclife_registration_flatdetails().get(i).getMember_type()+" *****");

                        }
                        if (allowAdmin) {
                            ((HomeActivity) context).addFragment(new AdminGridFragment());
                        }else {
                            Constants.showSnack(v, "You are not authorized person!", "");
                        }
                    } else {
                        Constants.showSnack(v, "Coming soon!", "OK");
                    }
                } else {
                    Constants.showSnack(v, "Please contact admin!", "OK");
                }

            }
        });

        return v;
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
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here
        inflater.inflate(R.menu.logout_menu, menu);
      /*  inflater.inflate(R.menu.logout_menu, menu);
        MenuItem itemLogout = menu.findItem(R.id.logOut);
        itemLogout.setVisible(true);*/
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.logOut:
                editor.clear();
                editor.commit();
                context.deleteDatabase(DatabaseHandler.DATABASE_NAME);
                Constants.showSnack(v, "You have been logged out!", "OK");
                Intent i = new Intent(getActivity(), Login.class);
                startActivity(i);
                break;
            case R.id.refreh:
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.detach(this).attach(this).commit();
                break;
            case R.id.profile:
                Intent ii = new Intent(getActivity(), UserProfile.class);
                ii.putExtra("EACH_USER_DET", "");
                startActivity(ii);
                break;
            default:
                break;
        }
        return false;
    }
    @Override
    public void onResume() {
        super.onResume();
        ((HomeActivity) context).setHomeAsEnabled(false);
        ((HomeActivity) context).changeToolbarTitle(R.string.home);
    }
}
