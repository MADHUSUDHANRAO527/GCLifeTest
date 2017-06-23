package mobile.gclifetest.Fcm;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import mobile.gclifetest.activity.HomeActivity;
import mobile.gclifetest.activity.R;
import mobile.gclifetest.fragments.InboxActivity;
import mobile.gclifetest.utils.Constants;

/**
 * Created by Madhu on 31/05/17.
 */

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    String message, title, category;
    Context mContext;
    String eid, notiId="100";
    SharedPreferences userPref;
    SharedPreferences.Editor editor;
    String msg = "", postedBy = "";
    private static final String TAG = FirebaseMessagingService.class.getSimpleName();

    private NotificationUtils notificationUtils;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.e(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage == null)
            return;

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
            handleNotification(remoteMessage.getNotification().getBody());
        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());

            try {
               // JSONObject json = new JSONObject(remoteMessage.getData().toString().trim());
                handleDataMessage(remoteMessage.getData());
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }
    private void handleNotification(String message) {
        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            // app is in foreground, broadcast the push message
            Intent pushNotification = new Intent("PUSH_NOTIFICATION");
            pushNotification.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            // play notification sound
            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            notificationUtils.playNotificationSound();
        }else{
            // If the app is in background, firebase itself handles the notification
        }
    }

    private void handleDataMessage(Map<String, String> json) {
        Log.e(TAG, "push json: " + json.toString());

        try {
           // JSONObject data = json.getJSONObject("data");

             title = json.get("tittle");
             message = json.get("message");
            category = json.get("category");
            eid = json.get("event");
            Log.e(TAG, "title: " + title);
            Log.e(TAG, "message: " + message);
            Log.e(TAG, "category: " + category);
            if (!message.contains("Mail") && message.contains("-") && message != null) {
                String[] totMsg = message.split("-");
                postedBy = totMsg[0];
                msg = totMsg[1];
            }else {
                msg = message;
            }



            Intent notificationIntent;
            if (category.equals("News") || category.equals("Notice") ||
                    category.equals("Ideas") || category.equals("Photos") || category.equals("Videos")) {
                notificationIntent = new Intent(getApplicationContext(), HomeActivity.class);
                notificationIntent.putExtra("EventName", category);
                notificationIntent.putExtra("id", eid);

            } else if (category == "Inbox" || category.equals("Inbox")) {
                notificationIntent = new Intent(getApplicationContext(), InboxActivity.class);
            } else {
                notificationIntent = new Intent(getApplicationContext(), HomeActivity.class);
            }
            notificationIntent.setAction(notiId);
            try {
                if (!msg.contains("Mail"))
                    generateNotification(getApplicationContext(), title, postedBy + "-" + Constants.decodeString(msg), notiId, notificationIntent);
                else
                    generateNotification(getApplicationContext(), title, msg, notiId, notificationIntent);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

         /*   if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                // app is in foreground, broadcast the push message
                Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                pushNotification.putExtra("message", message);
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                // play notification sound
                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                notificationUtils.playNotificationSound();
            } else {
                // app is in background, show the notification in notification tray
                Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
                resultIntent.putExtra("message", message);

                if (mContext != null)
                    userPref = mContext.getSharedPreferences("USER", MODE_PRIVATE);
                notiId = json.getString("notId");
                String eventId = userPref.getString("EventId", "NV");
                if (eventId.equals(json.getString("event"))) {
                    System.out.println(" SAME EVENT ID !!!!!!!! !!!!" + notiId);
                } else {
                    editor = userPref.edit();
                    editor.putString("EventId", eid);
                    editor.apply();
                }



                    showNotificationMessage(getApplicationContext(), title, message, resultIntent);

                // check for image attachment
               *//* if (TextUtils.isEmpty(imageUrl)) {
                    showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent);
                } else {
                    // image is present, show notification with image
                    showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, resultIntent, imageUrl);
                }*//*
            }*/
        }  catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, intent);
    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, intent);
    }




      /*  mContext = context;
        category = intent.getExtras().getString("category");
        title = intent.getExtras().getString("tittle");
        message = intent.getExtras().getString("message");
        if (!message.contains("Mail") && message.contains("-") && message != null) {
            String[] totMsg = message.split("-");
            postedBy = totMsg[0];
            msg = totMsg[1];
        }else {
            msg = message;
        }
        eid = intent.getExtras().getString("event");
        if (mContext != null)
            userPref = mContext.getSharedPreferences("USER", MODE_PRIVATE);
        notiId = intent.getExtras().getString("notId");
        String eventId = userPref.getString("EventId", "NV");
        if (eventId.equals(intent.getExtras().getString("event"))) {
            System.out.println(" SAME EVENT ID !!!!!!!! !!!!" + notiId);
        } else {
            editor = userPref.edit();
            editor.putString("EventId", eid);
            editor.apply();
            //   System.out.println(eid + " ID: " + category + " CAT: " + title + " TIT: " + message + " : MSG " + " " + " NOTI ID" + notiId);
            Intent notificationIntent;
            if (category.equals("News") || category.equals("Notice") ||
                    category.equals("Ideas") || category.equals("Photos") || category.equals("Videos")) {
                notificationIntent = new Intent(context, HomeActivity.class);
                notificationIntent.putExtra("EventName", category);
                notificationIntent.putExtra("id", eid);

            } else if (category == "Inbox" || category.equals("Inbox")) {
                notificationIntent = new Intent(context, InboxActivity.class);
            } else {
                notificationIntent = new Intent(context, HomeActivity.class);
            }
            notificationIntent.setAction(notiId);
            try {
                if (!msg.contains("Mail"))
                    generateNotification(context, title, postedBy + "-" + Constants.decodeString(msg), notiId, notificationIntent);
                else
                    generateNotification(context, title, msg, notiId, notificationIntent);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            ComponentName comp = new ComponentName(context.getPackageName(),
                    GcmMessagerHandler.class.getName());

            // Start the service, keeping the device awake while it is launching.
            startWakefulService(context, (intent.setComponent(comp)));
            setResultCode(Activity.RESULT_OK);
        }*/



    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @SuppressWarnings("deprecation")
    public void generateNotification(Context context, String title, String message, String notiId,
                                     Intent notificationIntent) throws UnsupportedEncodingException {
        // TODO Auto-generated method stub
        int icon = R.drawable.app_icon;
        PendingIntent intent = PendingIntent.getActivity(context, Integer.parseInt(notiId),
                notificationIntent, 0);
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        Resources res = context.getResources();
        Notification.Builder builder = new Notification.Builder(context);
        builder.setAutoCancel(true);
        builder.setContentTitle(title.replace("-", ""));
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
        if (!isScreenOn) {
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "MyLock");
            wl.acquire(10000);
            PowerManager.WakeLock wl_cpu = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyCpuLock");
            wl_cpu.acquire(10000);
        }
    }
}