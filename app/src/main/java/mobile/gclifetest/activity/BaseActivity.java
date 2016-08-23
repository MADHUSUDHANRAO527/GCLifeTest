package mobile.gclifetest.activity;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewConfiguration;

import java.lang.reflect.Field;
import java.util.Stack;

import mobile.gclifetest.Utils.MyApplication;

/**
 * Created by goodworklabs on 04/03/2016.
 */
public class BaseActivity extends AppCompatActivity {
    android.support.v7.app.ActionBar actionBar;
    private static Stack<android.support.v4.app.Fragment> mFragmentStack = null;
    android.support.v4.app.FragmentManager mFragmentManager;
    Context mContext;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        mFragmentManager = getSupportFragmentManager();
    }
    public void setUpActionBar(String title) {
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setIcon(android.R.color.transparent);
        actionBar.setTitle(title);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color
                .parseColor(MyApplication.actiobarColor)));
    }

    void getOverflowMenu() {

        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class
                    .getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

  /*  public void replaceFragment(Fragment fragment) {
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
    }*/

    public void clearFragmentStack() {
        if (mFragmentStack.size() >= 2) {
            //   Log.i("remove frag" + i, " " + mFragmentStack.size());
            FragmentTransaction ft = mFragmentManager.beginTransaction();
            mFragmentStack.lastElement().onPause();
            ft.remove(mFragmentStack.pop());
            mFragmentStack.lastElement().onResume();
            ft.show(mFragmentStack.lastElement());
            ft.commitAllowingStateLoss();
        }
    }
   /* public void onBackpressed() {
        if (mFragmentStack.size() >= 1) {
            FragmentTransaction ft = mFragmentManager.beginTransaction();
            mFragmentStack.lastElement().onPause();
            ft.remove(mFragmentStack.pop());
            mFragmentStack.lastElement().onResume();
            ft.show(mFragmentStack.lastElement());
            ft.commitAllowingStateLoss();
        }*//* else {
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
        }*//*
    }*/
}
