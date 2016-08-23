package mobile.gclifetest.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.List;
import java.util.Stack;

import mobile.gclifetest.db.DatabaseHandler;
import mobile.gclifetest.fragments.HomeFragment;

/**
 * Created by MRaoKorni on 8/1/2016.
 */
public class HomeActivity extends BaseActivity {
    FrameLayout mFrame;
    private Toolbar mToolbar;
    private static Stack<android.support.v4.app.Fragment> mFragmentStack = null;
    android.support.v4.app.FragmentManager mFragmentManager;
    Context context;
    private boolean doubleBackToExitPressedOnce;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        mFragmentManager = getSupportFragmentManager();
        mFrame = (FrameLayout) findViewById(R.id.layout_frame_content);
        context = this;
        initToolbar();
        getOverflowMenu();
        replaceFragment(new HomeFragment());
    }

    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.action_bar);
        mToolbar.setTitle("Home");
        setSupportActionBar(mToolbar);
    }

    public void changeToolbarTitle(String title) {
        mToolbar.setTitle(title);
    }

    public void setHomeAsEnabled(boolean status) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(status);
    }

    public void toolbarVisible(boolean status) {
        if (status) {
            mToolbar.setVisibility(View.GONE);
        } else {
            mToolbar.setVisibility(View.VISIBLE);
        }
    }

    public void replaceFragment(Fragment fragment) {
        if (mFragmentStack != null) {
            Log.e("Size", mFragmentStack.size() + "");
        }
        mFragmentStack = new Stack<>();
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.layout_frame_content, fragment);
        mFragmentStack.push(fragment);
        transaction.commitAllowingStateLoss();
    }

    public void addFragment(android.support.v4.app.Fragment fragment) {
        android.support.v4.app.FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.add(R.id.layout_frame_content, fragment);
        mFragmentStack.lastElement().onPause();
        transaction.hide(mFragmentStack.lastElement());
        mFragmentStack.push(fragment);
        transaction.commit();
    }


    private static final int TIME_INTERVAL = 10000; // # milliseconds, desired
    private long mBackPressed;

    public void onBackpressed() {
        if (mFragmentStack.size() >= 2) {
            FragmentTransaction ft = mFragmentManager.beginTransaction();
            mFragmentStack.lastElement().onPause();
            ft.remove(mFragmentStack.pop());
            mFragmentStack.lastElement().onResume();
            ft.show(mFragmentStack.lastElement());
            ft.commitAllowingStateLoss();
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please Tap BACK again to exit", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.logOut:
                // editor.clear();
                //  editor.commit();
                context.deleteDatabase(DatabaseHandler.DATABASE_NAME);
                //  showSnack(HomeApp.this, "You have been logged out!", "OK");
                //  Intent i = new Intent(HomeApp.this, Login.class);
                //  startActivity(i);
                break;
            case R.id.refreh:
                Intent intent = getIntent();
                finish();
                startActivity(intent);
                break;
            case R.id.profile:
                //  Intent ii = new Intent(HomeApp.this, UserProfile.class);
                // ii.putExtra("EACH_USER_DET", "");
                // startActivity(ii);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

            handleButtonBarBackKey();

    }
    private void handleButtonBarBackKey() {
        List<Fragment> frags = getSupportFragmentManager().getFragments();
        Fragment lastFrag = getLastNotNull(frags);
        if (!(lastFrag instanceof BackKeyListener)) {
            onBackpressed();
        } else {
            if (!((BackKeyListener) lastFrag).handleBackKeyPress())
                onBackpressed();
        }
    }
    private Fragment getLastNotNull(List<Fragment> list) {
        for (int i = list.size() - 1; i >= 0; i--) {
            Fragment frag = list.get(i);
            if (frag != null) {
                return frag;
            }
        }
        return null;
    }
    public interface BackKeyListener {
        boolean handleBackKeyPress();
    }
}
