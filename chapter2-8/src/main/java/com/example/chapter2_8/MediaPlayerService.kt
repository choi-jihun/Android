package com.example.chapter2_8

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.drawable.Icon
import android.media.MediaPlayer
import android.os.IBinder
import androidx.core.content.ContextCompat
import android.Manifest
import android.content.IntentFilter
import android.os.Build.VERSION_CODES.P

class MediaPlayerService : Service() {

    private var mediaPlayer : MediaPlayer ?= null
    private var receiver = LowBatteryReceiver()

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        initReceiver()

        var playIcon = Icon.createWithResource(baseContext, R.drawable.baseline_play_arrow_24)
        var pauseIcon = Icon.createWithResource(baseContext, R.drawable.baseline_pause_24)
        var stopIcon = Icon.createWithResource(baseContext, R.drawable.baseline_stop_24)

        val mainPendingIntent = PendingIntent.getActivity(baseContext,0,Intent(baseContext,MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        },PendingIntent.FLAG_IMMUTABLE
        )

        val pausePendingIntent = PendingIntent.getService(baseContext,0,Intent(baseContext,MediaPlayerService::class.java).apply {
            action = MEDIA_PLAYER_PAUSE },
        PendingIntent.FLAG_IMMUTABLE
        )

        val playPendingIntent = PendingIntent.getService(baseContext,0,Intent(baseContext,MediaPlayerService::class.java).apply {
            action = MEDIA_PLAYER_PLAY },
            PendingIntent.FLAG_IMMUTABLE
        )

        val stopPendingIntent = PendingIntent.getService(baseContext,0,Intent(baseContext,MediaPlayerService::class.java).apply {
            action = MEDIA_PLAYER_STOP },
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = Notification.Builder(baseContext, CHANNEL_ID)
            .setStyle(
                Notification.MediaStyle()
                    .setShowActionsInCompactView(0,1,2)
            )
            .setVisibility(Notification.VISIBILITY_PUBLIC)
            .setSmallIcon(R.drawable.baseline_star_24)
            .addAction(
                Notification.Action.Builder(
                    pauseIcon,
                    "Pause",
                    pausePendingIntent
                ).build()
            )
            .addAction(
                Notification.Action.Builder(
                    playIcon,
                    "Play",
                    playPendingIntent
                ).build()
            )
            .addAction(
                Notification.Action.Builder(
                    stopIcon,
                    "Stop",
                    stopPendingIntent
                ).build()
            )
            .setContentIntent(mainPendingIntent)
            .setContentTitle("음악재생")
            .setContentText("음원이 재생 중 입니다...")
            .build()

        startForeground(100,notification)
    }

    private fun initReceiver() {
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_BATTERY_LOW)
        }
        registerReceiver(receiver,filter)
    }

    private fun createNotificationChannel(){
        var channel = NotificationChannel(CHANNEL_ID,"MEDIA_PLAYER",NotificationManager.IMPORTANCE_DEFAULT)

        val notificationManager = baseContext.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action) {
            MEDIA_PLAYER_PLAY -> {
                if(mediaPlayer == null) {
                    mediaPlayer = MediaPlayer.create(baseContext, R.raw.cookie)
                }
                mediaPlayer?.start()
            }
            MEDIA_PLAYER_PAUSE -> {
                mediaPlayer?.pause()
            }
            MEDIA_PLAYER_STOP -> {
                mediaPlayer?.stop()
                mediaPlayer?.release()
                mediaPlayer = null
                stopSelf()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        mediaPlayer?.apply {
            stop()
            release()
        }
        mediaPlayer = null
        unregisterReceiver(receiver)
        super.onDestroy()
    }

}