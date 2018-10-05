package com.app.sample.fchat;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.util.Map;


///**
// * Copyright Google Inc. All Rights Reserved.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// * http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */package com.app.sample.fchat;
//
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "message received";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Handle data payload of FCM messages.
        System.out.println("msg received");
        Log.d(TAG, "FCM Message Id: " + remoteMessage.getMessageId());
        Log.d(TAG, "FCM Notification Message: " + remoteMessage.getNotification());
        Log.d(TAG, "FCM Data Message: " + remoteMessage.getData());
        System.out.println("hm msg recieved"+remoteMessage.getFrom()+remoteMessage.getMessageType()+remoteMessage.getNotification());
    }
}
//
//import android.app.NotificationManager;
//        import android.app.PendingIntent;
//        import android.content.Intent;
//        import android.support.v4.app.NotificationCompat;
//import android.util.Log;
//
//import com.google.firebase.messaging.RemoteMessage;
//
//public class MyFirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService{
//
//    @Override
//    public void onMessageReceived(RemoteMessage remoteMessage) {
//        System.out.println("chat test :"+remoteMessage.getNotification());
//        showNotification(remoteMessage.getData().get("message"));
//    }
//    @Override
//    public void onNewToken(String s) {
//        Log.e("NEW_TOKEN", s);
//    }
//    private void showNotification(String message) {
//
//        Intent i = new Intent(this,MyFirebaseInstanceIdService.class);
//        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
//        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,i,PendingIntent.FLAG_UPDATE_CURRENT);
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
//                .setAutoCancel(true)
//                .setContentTitle("FCM Test")
//                .setContentText(message)
//                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
//                .setContentIntent(pendingIntent);
//
//        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//
//        manager.notify(0,builder.build());
//    }
//}
//public class MyFirebaseMessagingService extends FirebaseMessagingService {
//
//    @Override
//    public void onNewToken(String s) {
//        Log.e("NEW_TOKEN", s);
//    }
//
//    @Override
//    public void onMessageReceived(RemoteMessage remoteMessage) {
//
//        Map<String, String> params = remoteMessage.getData();
//        JSONObject object = new JSONObject(params);
//        Log.e("JSON_OBJECT", object.toString());
//
//        String NOTIFICATION_CHANNEL_ID = "Nilesh_channel";
//
//        long pattern[] = {0, 1000, 500, 1000};
//
//        NotificationManager mNotificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
////        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
////            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Your Notifications",
////                    NotificationManager.IMPORTANCE_HIGH);
////
////            notificationChannel.setDescription("");
////            notificationChannel.enableLights(true);
////            notificationChannel.setLightColor(Color.RED);
////            notificationChannel.setVibrationPattern(pattern);
////            notificationChannel.enableVibration(true);
////            mNotificationManager.createNotificationChannel(notificationChannel);
////        }
//
//        // to diaplay notification in DND Mode
////        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
////            NotificationChannel channel = mNotificationManager.getNotificationChannel(NOTIFICATION_CHANNEL_ID);
////            channel.canBypassDnd();
////        }
//
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
//
//        notificationBuilder.setAutoCancel(true)
//                .setColor(ContextCompat.getColor(this, R.color.colorAccent))
//                .setContentTitle(getString(R.string.app_name))
//                .setContentText(remoteMessage.getNotification().getBody())
//                .setDefaults(Notification.DEFAULT_ALL)
//                .setWhen(System.currentTimeMillis())
//                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
//                .setAutoCancel(true);
//
//
//        mNotificationManager.notify(1000, notificationBuilder.build());
//    }
//}