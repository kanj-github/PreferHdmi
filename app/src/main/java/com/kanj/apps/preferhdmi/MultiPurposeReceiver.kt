package com.kanj.apps.preferhdmi

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.util.Log

class MultiPurposeReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        Log.v("Kanj", "MultiPurposeReceiver - ${intent.action ?: "null action"}")

        when(intent.action) {
            Intent.ACTION_SCREEN_ON -> onScreenOn(context)
            ACTION_ALARM_TRIGGERED -> onAlarmTriggered(context)
        }
    }

    private fun onAlarmTriggered(context: Context) {
        Log.v("Kanj", "onAlarmTriggered")
    }

    private fun onScreenOn(context: Context) {
        setAlarm(context)
    }

    companion object {

        private const val ALARM_DELAY = 60 * 1000 // 1 minute
        private const val ACTION_ALARM_TRIGGERED = "com.kanj.apps.preferhdmi.ACTION_ALARM_TRIGGERED"

        fun setAlarm(context: Context) {

            val broadcastIntent = Intent(ACTION_ALARM_TRIGGERED)
            val pendingIntent = PendingIntent.getBroadcast(context, 0, broadcastIntent,
                PendingIntent.FLAG_IMMUTABLE)
            val start = SystemClock.elapsedRealtime() + ALARM_DELAY // 1 minute after now
            val end = start + 10 * 60 * 1000 // 10 minutes, the minimum possible

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
            alarmManager?.setWindow(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                start,
                end,
                pendingIntent
            )
        }
    }
}
