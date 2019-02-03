package mobile.gclifetest.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import mobile.gclifetest.activity.HomeActivity;
import mobile.gclifetest.activity.R;
import mobile.gclifetest.event.AthidiRefreshEvent;
import mobile.gclifetest.utils.Constants;

/**
 * Created by MRaoKorni on 8/26/2016.
 */
public class AthidiFragment extends Fragment {
    WebView webView;
    Context context;
    SharedPreferences userPref;

    @SuppressLint("AddJavascriptInterface")
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(
                R.layout.athidi_webview_fragment, container, false);
        context = getActivity();
        webView = (WebView) v.findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewController());
        userPref = context.getSharedPreferences("USER", Context.MODE_PRIVATE);

        webView.post(new Runnable() {
            @Override
            public void run() {
                //Log.d(AthidiFragment.class.getName(), "http://gcsuvidhatest.globalcityflatowners.org/GcLife/index.html?ResidentId=" + userPref.getString("USERID", "NV"));
             //   webView.loadUrl("http://35.166.172.142/GcLife/index.html?ResidentId="+ userPref.getString("USERID", "NV"));
                //  webView.loadUrl("http://gcsuvidhatest.globalcityflatowners.org/GcLife/index.html?ResidentId=" + userPref.getString("USERID", "NV"));

               webView.loadUrl("http://meljol.tech/GcLife/index.html?ResidentId=" + userPref.getString("USERID", "NV"));


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
        ((HomeActivity) context).changeToolbarTitle(R.string.athidi);
    }


    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        FlurryAgent.onStartSession(getActivity().getApplicationContext(), Constants.flurryApiKey);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(AthidiRefreshEvent event) {
        if (event.success) {
            Toast.makeText(context, "refreshing Atithi screen", Toast.LENGTH_SHORT).show();
            webView.loadUrl("http://meljol.tech/GcLife/index.html?ResidentId=" + userPref.getString("USERID", "NV"));
           // webView.loadUrl("http://35.166.172.142/GcLife/index.html?ResidentId="+ userPref.getString("USERID", "NV"));
        }
    }

    @Override
    public void onStop() {
        super.onPause();
        EventBus.getDefault().unregister(this);
        FlurryAgent.onEndSession(getActivity().getApplicationContext());
    }

    public class WebViewController extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}
