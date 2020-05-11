package com.dawoodamir.tibgodoc.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.dawoodamir.tibgodoc.R
import com.dawoodamir.tibgodoc.ui.SignUpActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.io.IOException
import java.net.URL

class MyFirebaseMessagingService : FirebaseMessagingService() {



    companion object{
        const val  SHARED_PREFS ="androidDeviceTokenSharedPref"
        const val KEY = "deviceToken"
        const val CHANNEL_ID ="0001010"
        const val CHANNEL_NAME ="Tib GO Doc Notifications"
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val notification = remoteMessage.notification
        val data = remoteMessage.data
        assert(notification != null)
        sendNotification(notification, data)
    }
    /**
     * Create and show a custom notification containing the received FCM message.
     *
     * @param notification FCM notification payload received.
     * @param data FCM data payload received.
     */
    private fun sendNotification(
        notification: RemoteMessage.Notification?,
        data: Map<String, String>
    ) {
        val icon = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
        val intent = Intent(this, SignUpActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent =
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        val notificationBuilder =
            NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(notification!!.title)
                .setContentText(notification.body)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pendingIntent)
                .setContentInfo(notification.title)
                .setLargeIcon(icon)
                .setColor(Color.GREEN)
                .setLights(Color.GREEN, 1000, 300)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setSmallIcon(R.drawable.ic_notifications)
        try {
            val pictureUrl = data["picture_url"]
            if (pictureUrl != null && "" != pictureUrl) {
                val url = URL(pictureUrl)
                val bigPicture =
                    BitmapFactory.decodeStream(url.openConnection().getInputStream())
                notificationBuilder.setStyle(
                    NotificationCompat.BigPictureStyle().bigPicture(bigPicture).setSummaryText(
                        notification.body
                    )
                )
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // Notification Channel is required for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = "channel description"
            channel.setShowBadge(true)
            channel.canShowBadge()
            channel.enableLights(true)
            channel.lightColor = Color.RED
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500)
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(0, notificationBuilder.build())
    }


/*
//For picture pattern

{
 "to" : "d-TSJDgyCRA:APA91bFQZUv-tgjQ4GZzORajxvFV_0V96a_DpnFEbhfMHjpKDCw3AJkAivQsZ-lPs6WL8gOkuFwlbCq0LpS6qdbtiLX2XC-9MuE1td5OoxQZFmUjzxzL6OkLW4YPAp3exIRpOxlKbUjx",
 "collapse_key" : "type_a",
 "notification" : {
      "body" : "Make sure to check the order first :-)",
      "title": "Delivery guy is on your delivery address"

 },
 "data" :  {
 	"picture_url" : "https://tpc.googlesyndication.com/simgad/17151761623747917053",
     "body" : "Body of Your Notification in Data",
     "title": "Title of Your Notification in Title",
     "key_1" : "Value for key_1",
     "key_2" : "Value for key_2"
 }
}

*
*
*
* */
    ///////////////////////////////////////////////////////////////

    override fun onNewToken(token: String) {
        Log.d("new Token", "Refreshed token: $token")

        savingNewTokenToSharedPref(token)
    }


    private fun savingNewTokenToSharedPref(token: String) {
        val sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(KEY,token).apply()

    }


}