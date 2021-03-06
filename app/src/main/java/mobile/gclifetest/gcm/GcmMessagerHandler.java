package mobile.gclifetest.gcm;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class GcmMessagerHandler extends IntentService {

     String mes;
     private Handler handler;
    public GcmMessagerHandler() {
        super("GcmMessageHandler");
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        handler = new Handler();
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();

        /*GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);
*/
       mes = extras.getString("msg");
  //     showToast();
//       Log.i("GCM", "Received : (" +messageType+")  "+extras.getString("title"));

        GcmBroadcastReceiver.completeWakefulIntent(intent);

    }

}