package mobile.gclifetest.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import mobile.gclifetest.PojoGson.UserDetailsPojo;
import mobile.gclifetest.Utils.Constants;
import mobile.gclifetest.Utils.InternetConnectionDetector;
import mobile.gclifetest.Utils.MyApplication;
import mobile.gclifetest.activity.AdminGrid;
import mobile.gclifetest.activity.FrdsList;
import mobile.gclifetest.activity.HomeActivity;
import mobile.gclifetest.activity.ImpContacts;
import mobile.gclifetest.activity.InBox;
import mobile.gclifetest.activity.MySociety;
import mobile.gclifetest.activity.R;
import mobile.gclifetest.adapters.HomeAdapter;
import mobile.gclifetest.http.MemsPost;

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

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(
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

                Constants.showSnack(getActivity(), "Please check network connection!",
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

                System.out.println(userStatus);
                InternetConnectionDetector netConn = new InternetConnectionDetector(
                        getActivity());
                Boolean isInternetPresent = netConn.isConnectingToInternet();
                if (isInternetPresent) {
                    isInternetPresent = true;
                } else {

                    Constants.showSnack(getActivity(), "Please check network connection!",
                            "OK");

                }

                if (userStatus == "Approve" || userStatus.equals("Approve")) {

                    if (position == 0) {
                      //  ((BaseActivity) context).replaceFragment(new IdeasListFragment());
                        IdeasListFragment fragment = new IdeasListFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("EventName", "News");
                        fragment.setArguments(bundle);
                        ((HomeActivity) context).addFragment(fragment);
                       /* Intent soc = new Intent(getActivity(),
                                IdeasList.class);
                        soc.putExtra("EventName", "News");
                        startActivity(soc);
                        getActivity().overridePendingTransition(R.anim.slide_in_left,
                                R.anim.slide_out_left);*/
                    } else if (position == 1) {
                       /* Intent soc = new Intent(getActivity(),
                                IdeasList.class);
                        soc.putExtra("EventName", "Notice");
                        startActivity(soc);
                        getActivity().overridePendingTransition(R.anim.slide_in_left,
                                R.anim.slide_out_left);*/
                        IdeasListFragment fragment = new IdeasListFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("EventName", "Notice");
                        fragment.setArguments(bundle);
                        ((HomeActivity) context).addFragment(fragment);
                    } else if (position == 2) {
                        Intent soc = new Intent(getActivity(),
                                MySociety.class);
                        startActivity(soc);
                        getActivity().overridePendingTransition(R.anim.slide_in_left,
                                R.anim.slide_out_left);
                    } else if (position == 3) {
                        /*Intent dash = new Intent(getActivity(),
                                IdeasList.class);
                        dash.putExtra("EventName", "Ideas");
                        startActivity(dash);
                        getActivity().overridePendingTransition(R.anim.slide_in_left,
                                R.anim.slide_out_left);*/

                        IdeasListFragment fragment = new IdeasListFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("EventName", "Ideas");
                        fragment.setArguments(bundle);
                        ((HomeActivity) context).addFragment(fragment);
                    } else if (position == 4) {
                        Intent dash = new Intent(getActivity(), InBox.class);
                        startActivity(dash);
                        getActivity().overridePendingTransition(R.anim.slide_in_left,
                                R.anim.slide_out_left);
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
                        Intent dash = new Intent(getActivity(),
                                FrdsList.class);
                        startActivity(dash);
                        getActivity().overridePendingTransition(R.anim.slide_in_left,
                                R.anim.slide_out_left);
                    } else if (position == 8) {
                        Intent dash = new Intent(getActivity(),
                                ImpContacts.class);
                        startActivity(dash);
                        getActivity().overridePendingTransition(R.anim.slide_in_left,
                                R.anim.slide_out_left);
                    } else if (position == 9) {
                        Log.d("MEM TYPE", user.getGclife_registration_flatdetails().get(0).getMember_type());
                        if (user.getGclife_registration_flatdetails().get(0).getMember_type() == "Non_members" ||
                                user.getGclife_registration_flatdetails().get(0).getMember_type().equals("Non_members") || user.getGclife_registration_flatdetails().get(0).getMember_type() == "Member" ||
                                user.getGclife_registration_flatdetails().get(0).getMember_type().equals("Member")) {
                            Constants.showSnack(getActivity(), "You are not authorized person!", "");
                        } else {
                            Intent dash = new Intent(getActivity(),
                                    AdminGrid.class);
                            startActivity(dash);
                            getActivity().overridePendingTransition(R.anim.slide_in_left,
                                    R.anim.slide_out_left);
                        }
                    } else {
                        Constants.showSnack(getActivity(), "Coming soon!", "OK");
                    }
                } else {
                    Constants.showSnack(getActivity(), "Please contact admin!", "OK");
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
            } else {

            }

        }
    }
    @Override
    public void onResume() {
        super.onResume();
        setHasOptionsMenu(true);
        ((HomeActivity) context).setHomeAsEnabled(false);
        ((HomeActivity) context).changeToolbarTitle("Home");
    }
}
