package com.example.era_4.bakingmecrazy;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link BakingMeCrazyWidgetConfigureActivity BakingMeCrazyWidgetConfigureActivity}
 */
public class BakingMeCrazyWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        String recipeNameKey = context.getString(R.string.recipe_name_key);
        String recipeIngredientsKey = context.getString(R.string.recipe_ingredients_key);
        //get recipe name and ingredients from preferences
        CharSequence widgetIngredients = BakingMeCrazyWidgetConfigureActivity.loadRecipePref(context, appWidgetId, recipeIngredientsKey);
        CharSequence widgetName = BakingMeCrazyWidgetConfigureActivity.loadRecipePref(context, appWidgetId,recipeNameKey);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.baking_me_crazy_widget);
        views.setTextViewText(R.id.appwidget_text, widgetIngredients);
        views.setTextViewText(R.id.appwidget_recipe_name,widgetName);

        // can't get this to work - the recipe name passed in the intent doesn't match the name displayed in the widget.
        // it's using the default value, set to 'Oatmeal Cookies'
        //Intent intent = new Intent(context, RecipeDetail.class);
        //intent.putExtra(context.getString(R.string.widget_recipe_name), widgetName);

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.widget_layout, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {

        String recipeNameKey = context.getString(R.string.recipe_name_key);
        String recipeIngredientsKey = context.getString(R.string.recipe_ingredients_key);

        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            BakingMeCrazyWidgetConfigureActivity.deleteRecipePref(context, appWidgetId, recipeIngredientsKey);
            BakingMeCrazyWidgetConfigureActivity.deleteRecipePref(context, appWidgetId, recipeNameKey);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

