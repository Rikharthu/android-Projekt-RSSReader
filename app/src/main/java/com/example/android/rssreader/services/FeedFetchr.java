    //package com.example.android.rssreader.services;
//
//
//import android.app.Notification;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.app.Service;
//import android.content.Intent;
//import android.os.IBinder;
//import android.support.annotation.Nullable;
//import android.util.Log;
//
//import com.example.android.rssreader.R;
//import com.example.android.rssreader.model.RSSFeed;
//
//import java.util.Timer;
//import java.util.TimerTask;
//
//public class FeedFetchr extends Service {
//
//    private Timer timer;
//
//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        return START_STICKY;
//    }
//
//    private void startTimer() {
//        TimerTask task = new TimerTask() {
//
//            @Override
//            public void run() {
//                Log.d("News reader", "Timer task started");
//
//                io.downloadFile();
//                Log.d("News reader", "File downloaded");
//
//                RSSFeed newFeed = io.readFile();
//                Log.d("News reader", "File read");
//
//                // if new feed is newer than old feed
//                if (newFeed.getPubDateMillis() > app.getFeedMillis()) {
//                    Log.d("News reader", "Updated feed available.");
//
//                    // update app object
//                    // TODO budet li app suschestovatj vse eto vremja?
//                    app.setFeedMillis(newFeed.getPubDateMillis());
//
//                    // display notification
//                    sendNotification("Select to view updated feed.");
//
//                    // send broadcast
//                    // will trigger updateDisplay() in ItemsActivity due to intent filter
//                    Intent intent = new Intent(RSSFeed.NEW_FEED);
//                    FeedFetchr.this.sendBroadcast(intent);
//                }
//                else {
//                    Log.d("News reader", "Updated feed NOT available.");
//                }
//            }
//        };
//
//        timer = new Timer(true);
//        int delay = 1000 * 60 * 60;      // 1 hour
//        int interval = 1000 * 60 * 60;   // 1 hour
//        timer.schedule(task, delay, interval);
//    }
//
//    private void stopTimer() {
//        if (timer != null) {
//            timer.cancel();
//        }
//    }
//
//    private void sendNotification(String text) {
//
//        // create the intent for the notification
//        Intent notificationIntent = new Intent(this, ItemsActivity.class)
//                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //
//
//        // create the pending intent
//        // flag tells Android what to do if the pending intent with the same name exists
//        int flags = PendingIntent.FLAG_UPDATE_CURRENT; // keep current intent but update it's data
//        PendingIntent pendingIntent =
//                PendingIntent.getActivity(this, 0, notificationIntent, flags);
//
//        // create the variables for the notification
////        int icon = R.drawable.ic_launcher;
//        CharSequence tickerText = "Updated news feed is available";
//        CharSequence contentTitle = getText(R.string.app_name);
//        CharSequence contentText = text;
//
//        // create the notification and set its data
//        Notification notification =
//                new Notification.Builder(this)
////                        .setSmallIcon(icon)
//                        .setTicker(tickerText) // text when notification first appears
//                        .setContentTitle(contentTitle)
//                        .setContentText(contentText)
//                        .setContentIntent(pendingIntent) // pass intent that will be executed on click
//                        .setAutoCancel(true) //remove intent when notification selected
//                        .build(); // !!!
//
//        // display the notification
//        // get system notification service
//        NotificationManager manager = (NotificationManager)
//                getSystemService(NOTIFICATION_SERVICE);
//        // id will be used to identify notification (cancel it or replace with new one)
//        final int NOTIFICATION_ID = 1;
//        manager.notify(NOTIFICATION_ID, notification);
//    }
//
//}
