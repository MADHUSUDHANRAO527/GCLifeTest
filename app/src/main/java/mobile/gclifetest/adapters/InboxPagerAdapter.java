package mobile.gclifetest.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import mobile.gclifetest.fragments.InboxFragment;

/**
 * Created by MRaoKorni on 8/26/2016.
 */
public class InboxPagerAdapter extends FragmentPagerAdapter {

    private final String[] TITLES = {"     Write     ", "     Inbox    ",
            "    Sent   "};

    public InboxPagerAdapter(FragmentManager fm) {
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
        return InboxFragment.newInstance(position);
    }

}
