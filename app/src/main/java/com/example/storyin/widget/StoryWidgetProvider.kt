package com.example.storyin.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.storyin.R

@Suppress("DEPRECATION")
class StoryWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        context?.let {
            val appWidgetManager = AppWidgetManager.getInstance(it)
            val componentName = ComponentName(it, StoryWidgetProvider::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)

            if (appWidgetIds != null && appWidgetIds.isNotEmpty()) {
                onUpdate(it, appWidgetManager, appWidgetIds)
            }
        }
    }

    companion object {
        private fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val intent = Intent(context, StoryWidgetService::class.java)
            val views = RemoteViews(context.packageName, R.layout.story_widget)

            views.setRemoteAdapter(R.id.stack_view, intent)

            val clickIntent = Intent(context, StoryWidgetProvider::class.java)
            clickIntent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            val pendingIntent = PendingIntent.getBroadcast(
                context, 0, clickIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setPendingIntentTemplate(R.id.stack_view, pendingIntent)

            views.setEmptyView(R.id.stack_view, R.id.empty_view)

            appWidgetManager.updateAppWidget(appWidgetId, views)

            appWidgetManager.notifyAppWidgetViewDataChanged(
                appWidgetId,
                R.id.stack_view
            )
        }
    }
}