package mobile.gclifetest.fragments;

import android.content.Context;
import android.content.SharedPreferences;
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
public class MySocietyFragment extends Fragment {
    GridView gv;
    Context context;
    public static String[] prgmNameList = {"Kys", "My bill", "Facilitator"};
    public static int[] prgmImages = {R.drawable.icon_news,
            R.drawable.icon_noticeboard, R.drawable.icon_society};
    SharedPreferences userPref;
    Gson gson = new Gson();
    UserDetailsPojo user;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(
                R.layout.admin_detail, container, false);
        context = getActivity();
        gv = (GridView) v.findViewById(R.id.grid);
        gv.setAdapter(new MySocietyAdapter(context, prgmNameList, prgmImages));
        userPref = context.getSharedPreferences("USER", context.MODE_PRIVATE);
        String jsonUser = userPref.getString("USER_DATA", "NV");
        user = gson.fromJson(jsonUser, UserDetailsPojo.class);
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                System.out.println(position + "^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
                if (position == 1) {
                    if (user.getGclife_registration_flatdetails().get(0).getMember_type() == "Non_members" ||
                            user.getGclife_registration_flatdetails().get(0).getMember_type().equals("Non_members")) {
                        Constants.showSnack(v, "You are not authorized person!", "OK");
                    } else {
                        ((HomeActivity) context).addFragment(new MySocietyBillFragment());
                    }
                } else {
                    Constants.showSnack(v, "Coming soon!", "OK");
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
        ((HomeActivity) context).changeToolbarTitle(R.string.my_society);
    }
}
