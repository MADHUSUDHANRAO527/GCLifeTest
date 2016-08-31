package mobile.gclifetest.gcm;

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

import java.util.Random;

import mobile.gclifetest.activity.HomeActivity;
import mobile.gclifetest.activity.InBox;
import mobile.gclifetest.activity.R;

public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {
    String message, title, category;
    Context mContext;
    String  eid;
    Intent notificationIntent;
    Notification myNotication;
    Random random = new Random();
    SharedPreferences notificationPref;
    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        // Explicitly specify that GcmMessageHandler will handle the intent.
        notificationPref = context.getSharedPreferences("NOTIFICATION", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=notificationPref.edit();
        category = intent.getExtras().getString("category");
        title = intent.getExtras().getString("tittle");
        message = intent.getExtras().getString("message");
        eid = intent.getExtras().getString("event");
        System.out.println(eid + " ID: " + category + " CAT: " + title + " TIT: " + message + " : MSG ");

        if (category.equals("News") || category.equals("Notice") ||
                category.equals("Ideas") ||  category.equals("Photos") ||category.equals("Videos")) {
            notificationIntent = new Intent(context, HomeActivity.class);
            notificationIntent.putExtra("EventName", category);
            notificationIntent.putExtra("id", eid);

            editor.putString("notification","+ve");
            editor.commit();

        } else if (category == "Inbox" || category.equals("Inbox")) {
            notificationIntent = new Intent(context, InBox.class);
        } else {
            notificationIntent = new Intent(context, HomeActivity.class);
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
        builder.setLargeIcon(BitmapFactory.decodeResource(res, icon));
        //	builder.setSubText("This is subtext...");   //API level 16
        builder.setWhen(System.currentTimeMillis());
        builder.build();
        myNotication = builder.getNotification();
        //for multiple notification // i put some unique number to generate multiple notifications
        int n = random.nextInt(9999 - 1000) + 1000;
        notificationManager.notify(n, myNotication);
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