package mobile.gclifetest.activity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBarActivity;
import android.view.ViewConfiguration;

import java.lang.reflect.Field;

import mobile.gclifetest.Utils.MyApplication;

/**
 * Created by goodworklabs on 04/03/2016.
 */
public class BaseActivity extends ActionBarActivity{
    android.support.v7.app.ActionBar actionBar;

    public void setUpActionBar(String title) {
        actionBar =getSupportActionBar();
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
}
