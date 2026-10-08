package be.vives.loic.shopandcook.activities;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import be.vives.loic.shopandcook.R;

/**
 * Posts a "time to cook" notification for a planned recipe, even when the app is closed.
 * Alarms are inexact (no SCHEDULE_EXACT_ALARM permission needed) and are not restored after
 * a device reboot.
 */
public class ReminderReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "cooking_reminders";
    private static final String EXTRA_ID = "recipe_id";
    private static final String EXTRA_TITLE = "recipe_title";

    public static void schedule(Context context, String recipeId, String title, long triggerAtMillis) {
        Intent intent = new Intent(context, ReminderReceiver.class)
                .putExtra(EXTRA_ID, recipeId)
                .putExtra(EXTRA_TITLE, title);
        PendingIntent pending = PendingIntent.getBroadcast(context, recipeId.hashCode() ^ (int) triggerAtMillis,
                intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pending);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Build.VERSION.SDK_INT >= 33 && ContextCompat.checkSelfPermission(context,
                Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        String recipeId = intent.getStringExtra(EXTRA_ID);
        String title = intent.getStringExtra(EXTRA_TITLE);

        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    context.getString(R.string.reminder_channel), NotificationManager.IMPORTANCE_DEFAULT);
            context.getSystemService(NotificationManager.class).createNotificationChannel(channel);
        }

        Intent open = new Intent(context, RecipeDetailActivity.class)
                .putExtra("recipe_id", recipeId)
                .putExtra("recipe_title", title);
        PendingIntent contentIntent = PendingIntent.getActivity(context, recipeId.hashCode(), open,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_kitchen)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(context.getString(R.string.reminder_text, title))
                .setContentIntent(contentIntent)
                .setAutoCancel(true);
        NotificationManagerCompat.from(context).notify(recipeId.hashCode(), builder.build());
    }
}
