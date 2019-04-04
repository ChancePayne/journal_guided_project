package com.joshuahalvorson.journal;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.RemoteInput;

public class NotificationResponseReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String entryText = processResponse(intent, context);
        if(entryText != null){
            JournalEntrySharedPrefsRepository repository = new JournalEntrySharedPrefsRepository(context);
            JournalEntry entry = new JournalEntry(JournalEntry.INVALID_ID, entryText);
            repository.createEntry(entry);
        }
    }

    private String processResponse(Intent intent, Context context){
        Bundle input = RemoteInput.getResultsFromIntent(intent);
        if(input != null){
            String entryText = input.getCharSequence(JournalListActivity.NEW_ENTRY_ACTION).toString();

            NotificationCompat.Builder successNotification = new NotificationCompat.Builder(
                    context, JournalListActivity.channelId)
                    .setSmallIcon(android.R.drawable.ic_menu_save)
                    .setContentText("New Entry Created");

            NotificationManager notificationManager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(JournalListActivity.NOTIFICATION_ID, successNotification.build());

            return entryText;
        }
        return null;
    }

}
