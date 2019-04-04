package com.joshuahalvorson.journal;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.RemoteInput;

public class NotificationScheduleReceiver extends BroadcastReceiver {

    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;

        showNotification();
    }

    private void showNotification(){
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "Journal Notification";
            String description = "Notification for journal entry";
            int importance = NotificationManager.IMPORTANCE_LOW;

            NotificationChannel notificationChannel = new NotificationChannel(JournalListActivity.channelId, name, importance);
            notificationChannel.setDescription(description);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        android.support.v4.app.RemoteInput remoteInput = new RemoteInput.Builder(JournalListActivity.NEW_ENTRY_ACTION)
                .setLabel("Enter your entry text")
                .build();

        Intent inputIntent = new Intent(context, NotificationResponseReceiver.class);
        PendingIntent resultPendingIntent = PendingIntent.getBroadcast
                (
                        context,
                        JournalListActivity.INPUT_INTENT_REQUEST_CODE,
                        inputIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        NotificationCompat.Action inputAction = new NotificationCompat.Action.Builder(
                android.R.drawable.ic_menu_edit, "Entry", resultPendingIntent)
                .addRemoteInput(remoteInput)
                .setAllowGeneratedReplies(true)
                .build();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, JournalListActivity.channelId)
                .setPriority(NotificationManager.IMPORTANCE_LOW)
                .setContentTitle("Journal Entry")
                .setContentText("Create a journal entry")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .addAction(inputAction)
                .setColor(context.getResources().getColor(R.color.colorAccentGrey));

        notificationManager.notify(JournalListActivity.NOTIFICATION_ID, builder.build());
    }

}
