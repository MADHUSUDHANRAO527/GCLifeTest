package mobile.gclifetest.utils;

import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

public class ListViewUtils {
	public static void setDynamicHeight(ListView mListView) {
		ListAdapter mListAdapter = mListView.getAdapter();
		if (mListAdapter == null) {
			// when adapter is null
			return;
		}
		int height = 0;
		int desiredWidth = MeasureSpec.makeMeasureSpec(mListView.getWidth(),
				MeasureSpec.UNSPECIFIED);
		for (int i = 0; i < mListAdapter.getCount(); i++) {
			View listItem = mListAdapter.getView(i, null, mListView);
			listItem.measure(desiredWidth, MeasureSpec.UNSPECIFIED);
			height += listItem.getMeasuredHeight();
		}
		ViewGroup.LayoutParams params = mListView.getLayoutParams();
		params.height = height
				+ (mListView.getDividerHeight() * (mListAdapter.getCount() - 1));
		mListView.setLayoutParams(params);
		mListView.requestLayout();
	}
}
