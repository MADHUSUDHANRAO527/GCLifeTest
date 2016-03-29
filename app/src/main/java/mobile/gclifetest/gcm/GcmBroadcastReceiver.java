package mobile.gclifetest.gcm;

import mobile.gclifetest.activity.HomeApp;
import mobile.gclifetest.activity.IdeasDetail;
import mobile.gclifetest.activity.InBox;
import mobile.gclifetest.activity.R;

import org.json.JSONObject;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.PowerManager;
import android.support.v4.content.WakefulBroadcastReceiver;

public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {
    String message, title, category;
    JSONObject jsonCont;
    String uName, image_url;
    SharedPreferences uTuconatactsPref;
    Context mContext;
    String userNam, myId, eid;
    JSONObject json;
    String frdId;
    String countMsg, frdid;
    JSONObject jsonn;
    Intent notificationIntent;
    Notification myNotication;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        // Explicitly specify that GcmMessageHandler will handle the intent.

        category = intent.getExtras().getString("category");
        title = intent.getExtras().getString("tittle");
        message = intent.getExtras().getString("message");
        eid = intent.getExtras().getString("event");
        System.out.println(eid + " ID: " + category + " CAT: " + title + " TIT: " + message + " : MSG ");
        if (category == "News" || category.equals("News")) {
            notificationIntent = new Intent(context, IdeasDetail.class);
            notificationIntent.putExtra("EventName", category);
            notificationIntent.putExtra("id", eid);
        } else if (category == "Notice" || category.equals("Notice")) {
            notificationIntent = new Intent(context, IdeasDetail.class);
            notificationIntent.putExtra("id", eid);
            notificationIntent.putExtra("EventName", category);
        } else if (category == "Ideas" || category.equals("Ideas")) {
            notificationIntent = new Intent(context, IdeasDetail.class);
            notificationIntent.putExtra("id", eid);
            notificationIntent.putExtra("EventName", category);
        } else if (category == "Photos" || category.equals("Photos")) {
            notificationIntent = new Intent(context, IdeasDetail.class);
            notificationIntent.putExtra("id", eid);
            notificationIntent.putExtra("EventName", category);
        } else if (category == "Videos" || category.equals("Videos")) {
            notificationIntent = new Intent(context, IdeasDetail.class);
            notificationIntent.putExtra("id", eid);
            notificationIntent.putExtra("EventName", category);
        } else if (category == "Inbox" || category.equals("Inbox")) {
            notificationIntent = new Intent(context, InBox.class);
        } else {
            notificationIntent = new Intent(context, HomeApp.class);
        }

        generateNotification(context, "HELLLO", notificationIntent);

        ComponentName comp = new ComponentName(context.getPackageName(),
                GcmMessagerHandler.class.getName());

        // Start the service, keeping the device awake while it is launching.
        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
    }

    @SuppressWarnings("deprecation")
    private void generateNotification(Context context, String string,
                                      Intent notificationIntent) {
        // TODO Auto-generated method stub
        int icon = R.drawable.app_icon;
        //long when = System.currentTimeMillis();

		/*Notification notification = new Notification(icon, title, when);
        // set intent so it does not start a new activity
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent intent = PendingIntent.getActivity(context, 0,
				notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		notification.setLatestEventInfo(context, title, message, intent);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notificationManager.notify(0, notification);*/

        PendingIntent intent = PendingIntent.getActivity(context, 0,
                notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        Resources res = context.getResources();
        Notification.Builder builder = new Notification.Builder(context);
        builder.setAutoCancel(true);
        //	builder.setTicker("this is ticker text");
        builder.setContentTitle(title);
        builder.setContentText(message);
        builder.setSmallIcon(icon);
        builder.setContentIntent(intent);
        //  builder.setLights(Color.BLUE, 500, 500);
        builder.setOngoing(false);
        //  builder.setDefaults(Notification.DEFAULT_SOUND);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(alarmSound);
        builder.setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.app_icon));
        //	builder.setSubText("This is subtext...");   //API level 16
        builder.setWhen(System.currentTimeMillis());
        builder.build();
        myNotication = builder.getNotification();
        notificationManager.notify(0, myNotication);
        //wake up screen
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = pm.isScreenOn();
        if (isScreenOn == false) {
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "MyLock");
            wl.acquire(10000);
            PowerManager.WakeLock wl_cpu = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyCpuLock");
            wl_cpu.acquire(10000);
        }
    }
}