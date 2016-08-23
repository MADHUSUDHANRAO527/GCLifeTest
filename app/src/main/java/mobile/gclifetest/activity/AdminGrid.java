package mobile.gclifetest.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.gc.materialdesign.widgets.SnackBar;
import com.google.gson.Gson;

import mobile.gclifetest.PojoGson.UserDetailsPojo;

public class AdminGrid extends BaseActivity {
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_detail);
        gv = (GridView) findViewById(R.id.grid);
        gv.setAdapter(new CustommAdapter(AdminGrid.this, prgmNameList,
                prgmImages));
        setUpActionBar("Admin");
        typefaceLight = Typeface.createFromAsset(getAssets(),
                "fonts/RobotoLight.ttf");
        userPref = getSharedPreferences("USER", MODE_PRIVATE);
        String jsonUser = userPref.getString("USER_DATA", "NV");
        user = gson.fromJson(jsonUser, UserDetailsPojo.class);
        gv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                if (position == 0) {
                    Intent mems = new Intent(AdminGrid.this, MemsList.class);
                    startActivity(mems);
                    overridePendingTransition(R.anim.slide_in_left,
                            R.anim.slide_out_left);
                } else {
                    if (user.getGclife_registration_flatdetails().get(0).getMember_type() == "Non_members" ||
                            user.getGclife_registration_flatdetails().get(0).getMember_type().equals("Non_members") || user.getGclife_registration_flatdetails().get(0).getMember_type() == "Member" ||
                            user.getGclife_registration_flatdetails().get(0).getMember_type().equals("Member")) {
                        showSnack(AdminGrid.this, "You are not authorized person!", "");
                    } else {
                        Intent mems = new Intent(AdminGrid.this, AdminBillManagement.class);
                        startActivity(mems);
                        overridePendingTransition(R.anim.slide_in_left,
                                R.anim.slide_out_left);
                    }

                }
            }
        });
    }

    public static class CustommAdapter extends BaseAdapter {

        String[] result;
        Context context;
        int[] imageId;
        private static LayoutInflater inflater = null;

        public CustommAdapter(AdminGrid adminGrid, String[] prgmNameList,
                              int[] prgmImages) {
            // TODO Auto-generated constructor stub
            result = prgmNameList;
            context = adminGrid;
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
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            // TODO Auto-generated method stub
            Holder holder = new Holder();
            View rowView;

            rowView = inflater.inflate(R.layout.home_grid_row, null);
            holder.tv = (TextView) rowView.findViewById(R.id.textView1);
            holder.img = (ImageView) rowView.findViewById(R.id.imageView1);
            holder.tv.setTypeface(typefaceLight);
            holder.tv.setText(result[position]);
            holder.img.setImageResource(imageId[position]);

            return rowView;
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                overridePendingTransition(R.anim.slide_right_in,
                        R.anim.slide_out_right);
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    void showSnack(AdminGrid flats, String stringMsg, String ok) {
        new SnackBar(AdminGrid.this, stringMsg, ok, new View.OnClickListener() {

            @Override
            public void onClick(View v) {
            }
        }).show();
    }
}
