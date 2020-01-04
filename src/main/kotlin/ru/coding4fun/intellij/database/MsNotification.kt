package ru.coding4fun.intellij.database

import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications

object MsNotification {
    fun error(title: String, content: String) {
        Notifications.Bus.notify(
            Notification(
                "MsSql",
                title,
                content,
                NotificationType.ERROR
            )
        )
    }
}