package com.ashwingk.treasurehunt

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.preference.PreferenceManager
import android.support.v4.app.NotificationCompat

class TimerReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val isreminder = intent.type == Constants.IS_REMINDER
        val notif_mgr = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        if(isreminder) {
            val notifBuilder = NotificationCompat.Builder(context)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Hurry up!")
                    .setContentText("You have only 10 minutes left, get to the extraction point fast!")
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
            notif_mgr.notify(1, notifBuilder.build())
        } else {
            val notifBuilder = NotificationCompat.Builder(context)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Time up!")
                    .setContentText("You have run out of time, get to the extraction point fast!")
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
            PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(Constants.TIME_UP_PREF, true).apply()
            notif_mgr.notify(2, notifBuilder.build())
        }
    }
}
