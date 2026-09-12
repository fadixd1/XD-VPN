package com.fadixd.fadivpn.vpn

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.net.VpnService
import android.os.Build
import android.os.IBinder

class FadiVpnService : VpnService() {

    private var vpnInterface: android.os.ParcelFileDescriptor? = null

    companion object {
        const val ACTION_START = "com.fadixd.fadivpn.START_VPN"
        const val ACTION_STOP = "com.fadixd.fadivpn.STOP_VPN"

        private const val CHANNEL_ID = "fadi_vpn"
        private const val NOTIFICATION_ID = 1001
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {

        if (intent?.action == ACTION_STOP) {
            stopVpn()
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
            return START_NOT_STICKY
        }

        createNotificationChannel()

        val notification = Notification.Builder(this, CHANNEL_ID)
            .setContentTitle("FADI - VPN")
            .setContentText("VPN متصل")
            .setSmallIcon(android.R.drawable.ic_lock_lock)
            .setOngoing(true)
            .build()

        startForeground(NOTIFICATION_ID, notification)

        if (vpnInterface == null) {
            startVpn()
        }

        return START_STICKY
    }

    private fun startVpn() {
        try {
            vpnInterface = Builder()
                .setSession("FADI - VPN")
                .addAddress("10.99.0.2", 32)
                .addRoute("10.99.0.0", 24)
                .setBlocking(false)
                .establish()
        } catch (e: Exception) {
            vpnInterface = null
            stopSelf()
        }
    }

    private fun stopVpn() {
        try {
            vpnInterface?.close()
        } catch (_: Exception) {
        }

        vpnInterface = null
    }

    override fun onDestroy() {
        stopVpn()
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? {
        return super.onBind(intent)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NotificationManager::class.java)

            val channel = NotificationChannel(
                CHANNEL_ID,
                "FADI VPN",
                NotificationManager.IMPORTANCE_LOW
            )

            manager.createNotificationChannel(channel)
        }
    }
}
