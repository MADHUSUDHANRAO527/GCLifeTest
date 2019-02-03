package mobile.gclifetest.http;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;

import mobile.gclifetest.utils.GclifeApplication;

public class SocietyBillPost {
    @SuppressWarnings("deprecation")
    public static JSONObject makeBillPost(String userId, String societyName,
                                          String month, String date, File file, String hostname)
            throws Exception {

        // String textFile = Environment.getExternalStorageDirectory()+
        // "/bill_data.xlsx";
        String path = file.getPath();

        // the URL where the file will be posted
        String postReceiverUrl = hostname + "bill_statuses/import.json";

        // new HttpClient
        HttpClient httpClient = new DefaultHttpClient();

        // post header
        HttpPost httpPost = new HttpPost(postReceiverUrl);

        file = new File(path);
        FileBody fileBody = new FileBody(file);

        MultipartEntity reqEntity = new MultipartEntity(
                HttpMultipartMode.BROWSER_COMPATIBLE);

        try {
            /*
			 * reqEntity.addPart("user_id",new StringBody("19"));
			 * reqEntity.addPart("society_master_id",new
			 * StringBody("Rustomjee Evershine Globalcity Avenue J 58 to 61 CHSL"
			 * )); reqEntity.addPart("month",new StringBody("Jan"));
			 * reqEntity.addPart("fyear",new StringBody("2011-2012"));
			 */

            reqEntity.addPart("user_id", new StringBody(userId));
            reqEntity.addPart("society_master_id", new StringBody(societyName));
            reqEntity.addPart("month", new StringBody(month));
            reqEntity.addPart("fyear", new StringBody(date));
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        reqEntity.addPart("file", fileBody);

        httpPost.setEntity(reqEntity);

        // execute HTTP post request
        HttpResponse response;
        String responseStr = null;
        try {
            response = httpClient.execute(httpPost);
            HttpEntity resEntity = response.getEntity();

            if (resEntity != null) {

                responseStr = EntityUtils.toString(resEntity).trim();
                System.out.println(responseStr + "   *******************");

            }
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // String output = EntityUtils.toString(response.getEntity());

        JSONObject jobj = new JSONObject(responseStr);

        return jobj;
    }

    public static JSONObject callBillView(String userId, String societyName,
                                          String month, String date, String hostname) throws Exception {
        SSLSocketFactory sslFactory = new SimpleSSLSocketFactory(null);
        sslFactory
                .setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        HttpParams params = new BasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
        SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http", PlainSocketFactory
                .getSocketFactory(), 80));
        registry.register(new Scheme("https", sslFactory, 443));
        ClientConnectionManager ccm = new ThreadSafeClientConnManager(params,
                registry);
        HttpClient client = new DefaultHttpClient(ccm, params);

        HttpGet httget = new HttpGet();
        // societyName=societyName.replaceAll("", "%20");
        societyName = societyName.replaceAll(" ", "%20");
        String host = hostname + "view_bill.json?user_id=" + userId
                + "&society_master_id=" + societyName + "&month=" + month
                + "&fyear=" + date;
        System.out.println(host + "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        httget.setURI(new URI(host));
        httget.setHeader("Accept", "*/*");
        httget.setHeader("Accept-Encoding", "identity");
        httget.addHeader("Connection", "keep-alive");
        httget.setHeader("User-Agent", "nnst");
        httget.addHeader("Accept-Language", "en-GB,en-US;q=0.8,en;q=0.6");
        httget.setHeader("Content-Type", "application/x-www-form-urlencoded");
        httget.setHeader("Accept-Charset", "utf-8");
        HttpResponse response = null;
        try {
            response = client.execute(httget);

        } catch (Exception e) {
            System.out.println(e);
        }
        if (response == null) {
            System.out.println("no data");
        }
        String output = EntityUtils.toString(response.getEntity());
        JSONObject jobj = new JSONObject(output.toString());
        return jobj;

    }

    public static JSONObject callSociMyBill(String userId, String societyName,
                                            String buildingName, String flatNum, String date, String hostname) throws Exception {
        SSLSocketFactory sslFactory = new SimpleSSLSocketFactory(null);
        sslFactory
                .setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        HttpParams params = new BasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
        SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http", PlainSocketFactory
                .getSocketFactory(), 80));
        registry.register(new Scheme("https", sslFactory, 443));
        ClientConnectionManager ccm = new ThreadSafeClientConnManager(params,
                registry);
        HttpClient client = new DefaultHttpClient(ccm, params);

        HttpGet httget = new HttpGet();
        // societyName=societyName.replaceAll("", "%20");
        societyName = societyName.replaceAll(" ", "%20");
        String host = hostname + "my_bill.json?user_id=" + userId
                + "&society_master_id=" + societyName + "&building_no="
                + buildingName + "&flat_no=" + flatNum + "&fyear=" + date;
        System.out.println(host + "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        httget.setURI(new URI(host));
        httget.setHeader("Accept", "*/*");
        httget.setHeader("Accept-Encoding", "identity");
        httget.addHeader("Connection", "keep-alive");
        httget.setHeader("User-Agent", "nnst");
        httget.addHeader("Accept-Language", "en-GB,en-US;q=0.8,en;q=0.6");
        httget.setHeader("Content-Type", "application/x-www-form-urlencoded");
        httget.setHeader("Accept-Charset", "utf-8");
        HttpResponse response = null;
        try {
            response = client.execute(httget);

        } catch (Exception e) {
            System.out.println(e);
        }
        if (response == null) {
            System.out.println("no data");
        }
        String output = EntityUtils.toString(response.getEntity());
        JSONObject jobj = new JSONObject(output.toString());
        return jobj;

    }

    public static JSONObject updateStatusBill(String hostname, String billId,
                                              String paymentMode, String refNum, String confirmed_status, String billAmountPaid)
            throws Exception {
        SSLSocketFactory sslFactory = new SimpleSSLSocketFactory(null);
        sslFactory
                .setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        HttpParams params = new BasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
        SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http", PlainSocketFactory
                .getSocketFactory(), 80));
        registry.register(new Scheme("https", sslFactory, 443));
        ClientConnectionManager ccm = new ThreadSafeClientConnManager(params,
                registry);
        HttpClient client = new DefaultHttpClient(ccm, params);

        HttpGet httget = new HttpGet();
        confirmed_status = confirmed_status.replaceAll(" ", "%20");
        String host = hostname + "confirm_bill.json?bill_id=" + billId
                + "&payment_mode=" + paymentMode + "&ref_no=" + refNum
                + "&confirmed_status=" + confirmed_status + "&bill_amount_paid=" + billAmountPaid;
        ;
        System.out.println(host + "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        httget.setURI(new URI(host));
        httget.setHeader("Accept", "*/*");
        httget.setHeader("Accept-Encoding", "identity");
        httget.addHeader("Connection", "keep-alive");
        httget.setHeader("User-Agent", "nnst");
        httget.addHeader("Accept-Language", "en-GB,en-US;q=0.8,en;q=0.6");
        httget.setHeader("Content-Type", "application/x-www-form-urlencoded");
        httget.setHeader("Accept-Charset", "utf-8");
        HttpResponse response = null;
        try {
            response = client.execute(httget);

        } catch (Exception e) {
            System.out.println(e);
        }
        if (response == null) {
            System.out.println("no data");
        }
        String output = EntityUtils.toString(response.getEntity());
        JSONObject jobj = new JSONObject(output.toString());
        return jobj;

    }

    public static JSONObject updateMyBill(String hostname, String billId,
                                          String paymentMode, String refNum, String billAmountPaid) throws Exception {
        SSLSocketFactory sslFactory = new SimpleSSLSocketFactory(null);
        sslFactory
                .setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        HttpParams params = new BasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
        SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http", PlainSocketFactory
                .getSocketFactory(), 80));
        registry.register(new Scheme("https", sslFactory, 443));
        ClientConnectionManager ccm = new ThreadSafeClientConnManager(params,
                registry);
        HttpClient client = new DefaultHttpClient(ccm, params);

        HttpGet httget = new HttpGet();
        String host = hostname + "my_bill_confirmation.json?bill_id=" + billId
                + "&payment_mode=" + paymentMode + "&ref_no=" + refNum + "&bill_amount_paid=" + billAmountPaid;
        System.out.println(host + "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        host = host.replaceAll(" ", "%20");
        httget.setURI(new URI(host));
        httget.setHeader("Accept", "*/*");
        httget.setHeader("Accept-Encoding", "identity");
        httget.addHeader("Connection", "keep-alive");
        httget.setHeader("User-Agent", "nnst");
        httget.addHeader("Accept-Language", "en-GB,en-US;q=0.8,en;q=0.6");
        httget.setHeader("Content-Type", "application/x-www-form-urlencoded");
        httget.setHeader("Accept-Charset", "utf-8");
        HttpResponse response = null;
        try {
            response = client.execute(httget);

        } catch (Exception e) {
            System.out.println(e);
        }
        if (response == null) {
            System.out.println("no data");
        }
        String output = EntityUtils.toString(response.getEntity());
        JSONObject jobj = new JSONObject(output.toString());
        return jobj;

    }


    public static void afterUploadBill_API(Context context) throws Exception {
        String host = GclifeApplication.HOSTNAME_PAYMENT + "TransferInvoice.php?BillId=&UserId=&CallFromApp=";

        RequestQueue queue = Volley.newRequestQueue(context);

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, host,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        System.out.println(response + "!!!!!!!!!!SUCCESS!!!!!!!!!!!!");

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("That didn't work! !!!!! afterUploadBill_API !!!!!");

            }
        });

// Add the request to the RequestQueue.
        queue.add(stringRequest);

    }
}
