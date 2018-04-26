package com.ashwingk.treasurehunt

import android.app.NotificationManager
import android.content.Context
import android.support.v4.app.NotificationCompat
import android.util.Log

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class NotificationService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "From: " + remoteMessage!!.from)
        Log.d(TAG, "Notification Message Body: " + remoteMessage.data)
        val mBuilder = NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(remoteMessage.data["title"])
                .setContentText(remoteMessage.data["text"])
        // Sets an ID for the notification
        val mNotificationId = 1
        // Gets an instance of the NotificationManager service
        val mNotifyMgr = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build())
    }

    companion object {
        private val TAG = "NotificationService"
    }


}
