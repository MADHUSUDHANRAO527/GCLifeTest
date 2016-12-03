package mobile.gclifetest.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FrdsDetailTabs extends Fragment {
	private int position, actionBarHeight;
	private static final String ARG_POSITION = "position";
	TextView avenueNameTxt, societyNameTxt, buidlingNameTxt, flatNumtxt,
			loginDetailsTxt, flatDteailTxt, flatTypetxt, ownwerTypetxt,
			memTypeTxt;
	LinearLayout relationStartLay;
	public static FrdsDetailTabs newInstance(int position) {
		FrdsDetailTabs f = new FrdsDetailTabs();
		Bundle b = new Bundle();
		b.putInt(ARG_POSITION, position);
		f.setArguments(b);
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
		View v;
		Bundle b = getActivity().getIntent().getExtras();
		if (position == 0) {
			v = infaltor.inflate(R.layout.frds_detail, container, false);
			TextView unameTxt = (TextView) v.findViewById(R.id.unameTxt);
			TextView emailTxt = (TextView) v.findViewById(R.id.emailTxt);
			TextView mobileTxt = (TextView) v.findViewById(R.id.mobileNumTxt);
			unameTxt.setText(b.getString("uname"));
			emailTxt.setText(b.getString("email"));
			mobileTxt.setText(b.getString("mobile"));
		} else {
			v = infaltor.inflate(R.layout.flat_detail_layout, container, false);
			avenueNameTxt = (TextView) v.findViewById(R.id.avenueNameTxt);
			societyNameTxt = (TextView) v.findViewById(R.id.societyTxt);
			buidlingNameTxt = (TextView) v.findViewById(R.id.buildingNumTxt);
			flatNumtxt = (TextView) v.findViewById(R.id.flatNumtxt);
			flatTypetxt = (TextView) v.findViewById(R.id.flatTypeTxt);
			memTypeTxt = (TextView) v.findViewById(R.id.memTypeTxt);
			ownwerTypetxt = (TextView) v.findViewById(R.id.ownerTypeTxt);
			relationStartLay=(LinearLayout)v.findViewById(R.id.relationStartLay);
			relationStartLay.setVisibility(View.GONE);
			avenueNameTxt.setText(b.getString("avenue_name"));
			societyNameTxt.setText(b.getString("societyid"));
			buidlingNameTxt.setText(b.getString("buildingid"));
			flatNumtxt.setText(b.getString("flat_number"));
			flatTypetxt.setText(b.getString("flat_type"));
			memTypeTxt.setText(b.getString("member_type"));
			ownwerTypetxt.setText(b.getString("ownertypeid"));
		}

		TypedValue tv = new TypedValue();

		if (getActivity().getTheme().resolveAttribute(R.attr.actionBarSize, tv,
				true)) {
			actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,
					getResources().getDisplayMetrics());
		}
		return v;

	}
}
