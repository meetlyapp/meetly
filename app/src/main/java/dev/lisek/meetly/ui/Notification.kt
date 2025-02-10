package dev.lisek.meetly.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import dev.lisek.meetly.MainActivity
import dev.lisek.meetly.R
import kotlin.random.Random

@Composable
fun RequestNotificationPermission() {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (!isGranted) {
                Toast.makeText(context, "Notifications disabled 😢", Toast.LENGTH_SHORT).show()
            }
        }
    )

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}

fun showNotification(context: Context, title: String, message: String) {
    val channelId = "meetly_notifications"
    val notificationId = Random.nextInt()

    val channel = NotificationChannel(
        channelId,
        "Notifications",
        NotificationManager.IMPORTANCE_HIGH
    ).apply {
        description = "Notifications for meetly app"
    }

    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.createNotificationChannel(channel)

    val builder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.logo_fg)
        .setContentTitle(title)
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)

    with(NotificationManagerCompat.from(context)) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            notify(notificationId, builder.build())
        }
    }
}

fun listenForFriendRequests(context: Context, uid: String) {
    val db = Firebase.firestore
    val docRef = db.collection("users").document(uid)

    docRef.addSnapshotListener { snapshot, e ->
        if (e != null || snapshot == null || !snapshot.exists()) {
            Log.e("Firestore", "Listen failed.", e)
            return@addSnapshotListener
        }

        val newRequest = snapshot.get("incomingFriends") as? String
        if (!newRequest.isNullOrBlank()) {
            showFriendRequestNotification(context, newRequest)
        }
    }
}

fun showFriendRequestNotification(context: Context, senderName: String) {
    val channelId = "friend_request_channel"

    val intent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    val pendingIntent = PendingIntent.getActivity(
        context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val notification = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.logo_fg)
        .setContentTitle("New Friend Request")
        .setContentText("$senderName sent you a friend request!")
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)
        .setContentIntent(pendingIntent)
        .build()

    val notificationManager = ContextCompat.getSystemService(context, NotificationManager::class.java)
    notificationManager?.notify(1001, notification)
}

fun createNotificationChannel(context: Context) {
    val channel = NotificationChannel(
        "friend_request_channel",
        "Friend Requests",
        NotificationManager.IMPORTANCE_HIGH
    ).apply {
        description = "Notifications for incoming friend requests"
    }

    val notificationManager: NotificationManager =
        context.getSystemService(NotificationManager::class.java)
    notificationManager.createNotificationChannel(channel)
}
