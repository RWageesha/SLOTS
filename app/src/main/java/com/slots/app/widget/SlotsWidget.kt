package com.slots.app.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import com.slots.app.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SlotsWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    companion object {
        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val views = RemoteViews(context.packageName, R.layout.widget_slots)
            views.setTextViewText(R.id.widget_title, context.getString(R.string.app_name))
            views.setTextViewText(R.id.widget_tasks_count, "Loading...")
            views.setTextViewText(R.id.widget_budget_info, "Loading...")
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
