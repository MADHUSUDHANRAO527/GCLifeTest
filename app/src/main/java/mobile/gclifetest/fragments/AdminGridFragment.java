package mobile.gclifetest.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.google.gson.Gson;

import mobile.gclifetest.pojoGson.UserDetailsPojo;
import mobile.gclifetest.utils.Constants;
import mobile.gclifetest.activity.HomeActivity;
import mobile.gclifetest.activity.R;
import mobile.gclifetest.adapters.MySocietyAdapter;

/**
 * Created by MRaoKorni on 8/26/2016.
 */
public class AdminGridFragment extends Fragment {
    GridView gv;
    Context context;
    // ArrayList prgmName;
    public static String[] prgmNameList = {"     Member " + "\n" + "    verification", "Bill"};

    public static int[] prgmImages = {R.drawable.icon_news,
            R.drawable.icon_noticeboard};
    static Typeface typefaceLight;
    SharedPreferences userPref;
    UserDetailsPojo user;
    Gson gson = new Gson();

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(
                R.layout.admin_detail, container, false);
        context = getActivity();
        gv = (GridView) v.findViewById(R.id.grid);
        gv.setAdapter(new MySocietyAdapter(getActivity(), prgmNameList,
                prgmImages));
        userPref = context.getSharedPreferences("USER", context.MODE_PRIVATE);
        String jsonUser = userPref.getString("USER_DATA", "NV");
        user = gson.fromJson(jsonUser, UserDetailsPojo.class);
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                if (position == 0) {
                    ((HomeActivity) context).addFragment(new MemsListFragment());
                } else {
                    boolean allowAdmin=true;
                    for (int i = 0; i < user.getGclife_registration_flatdetails().size(); i++) {
                        if (user.getGclife_registration_flatdetails().get(i).getMember_type() == "Non_members" ||
                                user.getGclife_registration_flatdetails().get(i).getMember_type().equals("Non_members") || user.getGclife_registration_flatdetails().get(i).getMember_type() == "Member" ||
                                user.getGclife_registration_flatdetails().get(i).getMember_type().equals("Member")) {

                            allowAdmin=false;
                        } else {
                            allowAdmin=true;
                            //((HomeActivity) context).addFragment(new AdminGridFragment());
                        }
                    }
                    if(allowAdmin=true){
                        ((HomeActivity) context).addFragment(new AdminBillManagementFragment());
                    }else {
                        Constants.showSnack(v, "You are not authorized person!", "");
                    }

                }
            }
        });
        return v;
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
        ((HomeActivity) context).changeToolbarTitle(R.string.admin);
    }
}