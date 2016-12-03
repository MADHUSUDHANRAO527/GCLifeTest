package mobile.gclifetest.gcm;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.support.v4.content.WakefulBroadcastReceiver;

import mobile.gclifetest.activity.HomeActivity;
import mobile.gclifetest.activity.R;
import mobile.gclifetest.fragments.InboxActivity;

public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {
    String message, title, category;
    Context mContext;
    String eid, notiId;
    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        category = intent.getExtras().getString("category");
        title = intent.getExtras().getString("tittle");
        message = intent.getExtras().getString("message");
        eid = intent.getExtras().getString("event");
        notiId = intent.getExtras().getString("notId");

        System.out.println(eid + " ID: " + category + " CAT: " + title + " TIT: " + message + " : MSG " + " " + " NOTI ID" + notiId);
        Intent notificationIntent;
        if (category.equals("News") || category.equals("Notice") ||
                category.equals("Ideas") ||  category.equals("Photos") ||category.equals("Videos")) {
            notificationIntent = new Intent(context, HomeActivity.class);
            notificationIntent.putExtra("EventName", category);
            notificationIntent.putExtra("id", eid);

        } else if (category == "Inbox" || category.equals("Inbox")) {
            notificationIntent = new Intent(context, InboxActivity.class);
        } else {
            notificationIntent = new Intent(context, HomeActivity.class);
        }
        notificationIntent.setAction(notiId);
        generateNotification(context, title, message, notiId, notificationIntent);
        ComponentName comp = new ComponentName(context.getPackageName(),
                GcmMessagerHandler.class.getName());

        // Start the service, keeping the device awake while it is launching.
        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @SuppressWarnings("deprecation")
    public void generateNotification(Context context, String title, String message, String notiId,
                                     Intent notificationIntent) {
        // TODO Auto-generated method stub
        int icon = R.drawable.app_icon;
        PendingIntent intent = PendingIntent.getActivity(context, Integer.parseInt(notiId),
                notificationIntent, 0);
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        Resources res = context.getResources();
        Notification.Builder builder = new Notification.Builder(context);
        builder.setAutoCancel(true);
        builder.setContentTitle(title);
        builder.setContentText(message);
        builder.setSmallIcon(icon);
        builder.setContentIntent(intent);
        builder.setOngoing(false);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(alarmSound);
        builder.setLargeIcon(BitmapFactory.decodeResource(res, icon));
        builder.setWhen(System.currentTimeMillis());
        builder.build();
        Notification myNotication = builder.getNotification();
        myNotication.contentIntent = intent;
        myNotication.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(Integer.parseInt(notiId), myNotication);
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