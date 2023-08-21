package com.example.alarm

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

const val notificationID = 1
const val channelID ="channel1"
const val titleExtra = "titleExtra"
const val messageExtra = "messageExtra"

class Notification() : BroadcastReceiver(){

    @SuppressLint("NotificationPermission")
    override fun onReceive(context: Context, intent: Intent) {
        val notification = NotificationCompat.Builder(context, channelID)
            .setSmallIcon(R.drawable.notification)
            .setContentTitle(intent.getStringExtra(titleExtra))
            .setContentText(intent.getStringExtra(messageExtra))
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationID, notification)
    }
}