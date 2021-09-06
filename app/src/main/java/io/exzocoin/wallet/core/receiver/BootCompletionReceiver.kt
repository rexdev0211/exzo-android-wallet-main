package io.exzocoin.wallet.core.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import io.exzocoin.wallet.core.notifications.NotificationWorker

class BootCompletionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, arg1: Intent?) {
        NotificationWorker.startPeriodicWorker(context)
    }
}
