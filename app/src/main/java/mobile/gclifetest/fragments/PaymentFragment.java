package mobile.gclifetest.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.flurry.android.FlurryAgent;

import mobile.gclifetest.activity.HomeActivity;
import mobile.gclifetest.activity.R;
import mobile.gclifetest.utils.Constants;
import mobile.gclifetest.utils.GclifeApplication;

/**
 * Created by MRaoKorni on 8/26/2016.
 */
public class PaymentFragment extends Fragment {
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
              //  Toast.makeText(context, GclifeApplication.HOSTNAME_PAYMENT+"GcLife/?BillId="+GclifeApplication.getInstance().getBillIdForPayment()+"&UserId="+ userPref.getString("USERID", "NV"), Toast.LENGTH_SHORT).show();
                webView.loadUrl(GclifeApplication.HOSTNAME_PAYMENT+"GcLife/?BillId="+GclifeApplication.getInstance().getBillIdForPayment()+"&UserId="+ userPref.getString("USERID", "NV"));

                Log.d("Payment link",GclifeApplication.HOSTNAME_PAYMENT+"GcLife/?BillId="+GclifeApplication.getInstance().getBillIdForPayment()+"&UserId="+ userPref.getString("USERID", "NV"));


            }
        });

        return v;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                    GclifeApplication.getInstance().setFromPaymentPage(true);
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
        ((HomeActivity) context).changeToolbarTitle(R.string.payment);
    }


    @Override
    public void onStart() {
        super.onStart();
        FlurryAgent.onStartSession(getActivity().getApplicationContext(), Constants.flurryApiKey);
    }



    @Override
    public void onStop() {
        super.onPause();
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
