package mobile.gclifetest.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import mobile.gclifetest.materialDesign.PagerSlidingTabStrip;
import mobile.gclifetest.pojoGson.FlatDetailsPojo;
import mobile.gclifetest.pojoGson.UserDetailsPojo;

public class FrdsDetail extends BaseActivity {
    String hostname;
    PagerSlidingTabStrip tabs;
    ViewPager pager;
    MyPagerAdapter adapter;
    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frds_detail_tabstrip);
        setUpActionBar("Friends Detail");
        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        pager = (ViewPager) findViewById(R.id.pager);
        adapter = new MyPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        final int pageMargin = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                        .getDisplayMetrics());
        pager.setPageMargin(pageMargin);
        tabs.setViewPager(pager);

    }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        private final String[] TITLES = {"Personal Details", "Flat Details"};

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public Fragment getItem(int position) {
            return FrdsDetailTabs.newInstance(position);
        }

    }

    public static class FrdsDetailTabs extends Fragment {
        private int position, actionBarHeight;
        private static final String ARG_POSITION = "position";
        TextView unameTxt, emailTxt, mobileNumTxt, emrNumTxt, occupationtxt,
                dobTxt, genderTxt;
        Typeface typefaceLight, typeMeduim;
        LinearLayout layUserDet, flatDetailsLay, privacyLay;
        SharedPreferences userPref;
        List<FlatDetailsPojo> flatsList = new ArrayList<FlatDetailsPojo>();
        ListView listviewFlats;
        ImageView imageProfile, mobileNumImg, mobileSmsImg;
        FloatingActionButton addFlats;
        UserDetailsPojo user;
        String memDet, userStatus;
        static ImageLoader imageLoader;
        static DisplayImageOptions options;
        de.hdodenhof.circleimageview.CircleImageView userImg;

        public static FrdsDetailTabs newInstance(int position) {
            FrdsDetailTabs f = new FrdsDetailTabs();
            Bundle b = new Bundle();
            b.putInt(ARG_POSITION, position);
            f.setArguments(b);
            imageLoader = ImageLoader.getInstance();
            options = new DisplayImageOptions.Builder().cacheInMemory(true)
                    .cacheOnDisc(true).resetViewBeforeLoading(true)
                    .showImageForEmptyUri(R.drawable.no_media)
                    .showImageOnFail(R.drawable.no_media)
                    .showImageOnLoading(R.drawable.no_media).build();
            return f;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setHasOptionsMenu(true);
            position = getArguments().getInt(ARG_POSITION);
        }

        @Override
        public View onCreateView(final LayoutInflater infaltor,
                                 final ViewGroup container, Bundle savedInstanceState) {
            View v = null;
            Bundle b = getActivity().getIntent().getExtras();
            v = infaltor.inflate(R.layout.profile, container, false);
            userPref = getActivity().getSharedPreferences("USER", MODE_PRIVATE);

            emailTxt = (TextView) v.findViewById(R.id.emailTxt);

            unameTxt = (TextView) v.findViewById(R.id.unameTxt);
            mobileNumTxt = (TextView) v.findViewById(R.id.mobileNumTxt);
            listviewFlats = (ListView) v.findViewById(R.id.flatsList);
            addFlats = (FloatingActionButton) v.findViewById(R.id.addFlats);
            layUserDet = (LinearLayout) v.findViewById(R.id.layUserDet);
            flatDetailsLay = (LinearLayout) v.findViewById(R.id.flatDetailsLay);
            emrNumTxt = (TextView) v.findViewById(R.id.emeryNumTxt);
            imageProfile = (ImageView) v.findViewById(R.id.imageProfile);
            mobileNumImg= (ImageView) v.findViewById(R.id.mobileNumImg);
            mobileSmsImg= (ImageView) v.findViewById(R.id.mobileSmsImg);
            privacyLay = (LinearLayout) v.findViewById(R.id.privacy_lay);
            occupationtxt = (TextView) v.findViewById(R.id.occuTxt);
            genderTxt = (TextView) v.findViewById(R.id.gender_txt);
            dobTxt = (TextView) v.findViewById(R.id.dobTxt);
            userImg = (de.hdodenhof.circleimageview.CircleImageView) v.findViewById(R.id.imageProfile);
            typefaceLight = Typeface.createFromAsset(getActivity().getAssets(),
                    "fonts/RobotoLight.ttf");
            typeMeduim = Typeface.createFromAsset(getActivity().getAssets(),
                    "fonts/RobotoMedium.ttf");
            emailTxt.setTypeface(typefaceLight);
            mobileNumTxt.setTypeface(typefaceLight);
            unameTxt.setTypeface(typefaceLight);

            emrNumTxt.setTypeface(typefaceLight);
            genderTxt.setTypeface(typefaceLight);
            occupationtxt.setTypeface(typefaceLight);
            dobTxt.setTypeface(typefaceLight);

            Intent intent = getActivity().getIntent();
            Bundle bundle = intent.getExtras();
            memDet = bundle.getString("jsonDetails");
            Gson gson = new Gson();

            user = gson.fromJson(memDet, UserDetailsPojo.class);

            unameTxt.setText(user.getUsername());
            emailTxt.setText(user.getEmail());
            mobileNumTxt.setText(user.getMobile());
            genderTxt.setText(user.getGender());
            if (user.getPrivacy() == true) {
                privacyLay.setVisibility(View.GONE);
            } else {
                privacyLay.setVisibility(View.VISIBLE);
            }

            emrNumTxt.setText(user.getEmeNum());
            occupationtxt.setText(user.getOccupation());
            dobTxt.setText(user.getDob());
            if (user.getProfile_url() != null) {
                imageLoader.displayImage(user.getProfile_url(), userImg, options);
            }
            // list flat details
            flatsList = user.getGclife_registration_flatdetails();

            FlatModellAdapter dataTaskGrpAdapter = new FlatModellAdapter(
                    getActivity(), R.layout.flat_list_row, flatsList);

            listviewFlats.setAdapter(dataTaskGrpAdapter);

            listviewFlats.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    FlatDetailsPojo flats = flatsList.get(position);
                    userStatus = flats.getStatus();
                    Intent flatDetail = new Intent(getActivity(),
                            FlatDetail.class);
                    Gson gsonn = new Gson();
                    String jsonFlats = gsonn.toJson(flats);
                    flatDetail.putExtra("DETAILS", jsonFlats);
                    startActivity(flatDetail);
                    getActivity().overridePendingTransition(R.anim.slide_in_left,
                            R.anim.slide_out_left);
                }
            });
            addFlats.setVisibility(View.GONE);
            if (position == 0) {
                addFlats.setVisibility(View.GONE);
                flatDetailsLay.setVisibility(View.GONE);
                layUserDet.setVisibility(View.VISIBLE);
            } else {
                userImg.setVisibility(View.GONE);
                flatDetailsLay.setVisibility(View.VISIBLE);
                layUserDet.setVisibility(View.GONE);
                listviewFlats.setVisibility(View.VISIBLE);
            }


            TypedValue tv = new TypedValue();

            if (getActivity().getTheme().resolveAttribute(R.attr.actionBarSize,
                    tv, true)) {
                actionBarHeight = TypedValue.complexToDimensionPixelSize(
                        tv.data, getResources().getDisplayMetrics());
            }
            mobileNumTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + user.getMobile()));
                    startActivity(intent);
                }
            });
            emailTxt.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO,
                            Uri.fromParts("mailto", user.getEmail(), null));
                    startActivity(Intent.createChooser(emailIntent, "Send email..."));
                }
            });
            mobileNumImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + user.getMobile()));
                    startActivity(intent);
                }
            });
            mobileSmsImg.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO,
                            Uri.fromParts("mailto", user.getEmail(), null));
                    startActivity(Intent.createChooser(emailIntent, "Send email..."));
                }
            });
            return v;

        }

        private class FlatModellAdapter extends ArrayAdapter<FlatDetailsPojo> {

            ArrayList<FlatDetailsPojo> flatsList;
            LayoutInflater inflator;

            public FlatModellAdapter(Context context, int textViewResourceId,
                                     List<FlatDetailsPojo> flatsList) {
                super(context, textViewResourceId, flatsList);
                this.flatsList = new ArrayList<FlatDetailsPojo>();
                this.flatsList.addAll(flatsList);
                inflator = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }

            @Override
            public int getCount() {
                return flatsList.size();
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                final ViewHolder holder;
                Log.v("ConvertView", String.valueOf(position));
                final FlatDetailsPojo flats = flatsList.get(position);
                if (convertView == null) {
                    convertView = inflator
                            .inflate(R.layout.flat_list_row, null);

                    holder = new ViewHolder();
                    holder.avenueNameTxt = (TextView) convertView
                            .findViewById(R.id.avenueNameTxt);
                    holder.flatNumTxt = (TextView) convertView
                            .findViewById(R.id.flatNumTxt);

                    holder.avenueNameTxt.setTypeface(typeMeduim);
                    holder.flatNumTxt.setTypeface(typefaceLight);

                    convertView.setTag(holder);

                } else {
                    holder = (ViewHolder) convertView.getTag();
                }

                holder.avenueNameTxt.setText(flats.getAvenue_name());
                holder.flatNumTxt.setText(flats.getBuildingid() + "," + flats.getFlat_number());

                return convertView;
            }

            private class ViewHolder {
                public TextView avenueNameTxt, flatNumTxt;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                onBackPressed();
                overridePendingTransition(R.anim.slide_right_in,
                        R.anim.slide_out_right);
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
