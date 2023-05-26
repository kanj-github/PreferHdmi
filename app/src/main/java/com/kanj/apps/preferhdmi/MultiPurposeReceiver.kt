package com.kanj.apps.preferhdmi

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.tv.TvContract
import android.media.tv.TvInputInfo
import android.media.tv.TvInputManager
import android.os.PowerManager
import android.os.SystemClock
import android.util.Log
import android.widget.Toast

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
        Toast.makeText(context, "Alarm triggered", Toast.LENGTH_SHORT).show()

        val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
        powerManager ?: return

        // Is screen on?
        if (!powerManager.isInteractive) {
            return
        }

        val tvInputManager = context.getSystemService(Context.TV_INPUT_SERVICE) as? TvInputManager
        tvInputManager ?: return

        val firstHdmi = tvInputManager.tvInputList.firstOrNull {
            it.type == TvInputInfo.TYPE_HDMI
                    && it.isPassthroughInput
                    && tvInputManager.getInputState(it.id) == TvInputManager.INPUT_STATE_CONNECTED
        }
        firstHdmi?.let {
            selectInput(it, context)
        } ?: Toast.makeText(context, "HDMI not found", Toast.LENGTH_SHORT).show()
        // Is the selected HDMI available?
    }

    private fun selectInput(inputInfo: TvInputInfo, context: Context) {
        val uri = TvContract.buildChannelUriForPassthroughInput(inputInfo.id)
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
            Toast.makeText(context, "View ${inputInfo.loadLabel(context)}", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "No activity to view ${inputInfo.loadLabel(context)}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onScreenOn(context: Context) {
        Toast.makeText(context, "Screen on", Toast.LENGTH_SHORT).show()
        setAlarm(context)
    }

    companion object {

        private const val ALARM_DELAY = 60 * 1000 // 1 minute
        private const val ACTION_ALARM_TRIGGERED = "com.kanj.apps.preferhdmi.ACTION_ALARM_TRIGGERED"

        fun setAlarm(context: Context) {

            val broadcastIntent = Intent(context, MultiPurposeReceiver::class.java).apply {
                action = ACTION_ALARM_TRIGGERED
            }
            val pendingIntent = PendingIntent.getBroadcast(context, 0, broadcastIntent,
                PendingIntent.FLAG_IMMUTABLE)
            val start = SystemClock.elapsedRealtime() + ALARM_DELAY // 1 minute after now
            val end = start + 10 * 60 * 1000 // 10 minutes, the minimum possible

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
            alarmManager?.let {
                it.setWindow(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    start,
                    end,
                    pendingIntent
                )
                Log.v("Kanj", "Set window alarm")
            }
        }
    }
}
