package com.example.era_4.bakingmecrazy;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.era_4.bakingmecrazy.utils.Recipe;

import org.json.JSONException;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * The configuration screen for the {@link BakingMeCrazyWidget BakingMeCrazyWidget} AppWidget.
 */
public class BakingMeCrazyWidgetConfigureActivity extends Activity {

    private static final String PREFS_NAME = "com.example.era_4.bakingmecrazy.BakingMeCrazyWidget";
    private static final String PREF_PREFIX_KEY = "appwidget_bakingmecrazy";
    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    //EditText mAppWidgetText;
    private Spinner mRecipeSpinner;
    private ArrayList<Recipe> mRecipes;

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = BakingMeCrazyWidgetConfigureActivity.this;

            //store recipe name and ingredients list in shared preferences
            String selectedRecipeName = mRecipeSpinner.getSelectedItem().toString();
            Recipe selectedRecipe = null;
            for (int i=0;i<mRecipes.size();i++){
                if (selectedRecipeName.equals(mRecipes.get(i).getName())){
                    selectedRecipe = mRecipes.get(i);
                    break;
                }
            }
            String recipeNameKey = getString(R.string.recipe_name_key);
            saveRecipePref(context,mAppWidgetId,recipeNameKey,selectedRecipe.getName());

            String ingredients = "";
            for (int i=0; i <selectedRecipe.getIngredients().size(); i++){
                if (i==0){
                    ingredients = selectedRecipe.getIngredients().get(i).getName();
                } else {
                    ingredients = ingredients + ", " + selectedRecipe.getIngredients().get(i).getName();
                }
            }
            String recipeIngredientsKey = getString(R.string.recipe_ingredients_key);
            saveRecipePref(context,mAppWidgetId,recipeIngredientsKey,ingredients);


            // It is the responsibility of the configuration activity to update the app widget
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            BakingMeCrazyWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);

            // Make sure we pass back the original appWidgetId
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }
    };

    public BakingMeCrazyWidgetConfigureActivity() {
        super();
    }

    // Write the prefix to the SharedPreferences object for this widget
    static void saveRecipePref(Context context, int appWidgetId,String key, String text) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(key + appWidgetId, text);
        prefs.apply();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static String loadRecipePref(Context context, int appWidgetId, String key) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String titleValue = prefs.getString(key + appWidgetId, null);
        if (titleValue != null) {
            return titleValue;
        } else {
            return context.getString(R.string.appwidget_text);
        }
    }

    static void deleteRecipePref(Context context, int appWidgetId, String key) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(key + appWidgetId);
        prefs.apply();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.baking_me_crazy_widget_configure);
        findViewById(R.id.add_button).setOnClickListener(mOnClickListener);
        mRecipeSpinner = (Spinner) findViewById(R.id.spinner_recipe_name);

        //get recipes
        try {
            Uri uri = Uri.parse(getString(R.string.recipes_url));
            URL url = null;
            try {
                url = new URL(uri.toString());
                getRecipeJSON(url,this);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

       //mAppWidgetText.setText(loadTitlePref(BakingMeCrazyWidgetConfigureActivity.this, mAppWidgetId));
    }

    public void setSpinner(){
        ArrayList<String> recipeNameArrayList = new ArrayList<>();
        for (int i=0;i<mRecipes.size();i++){
            recipeNameArrayList.add(mRecipes.get(i).getName());
        }
        ArrayAdapter<String> recipeSpinnerAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,recipeNameArrayList);
        recipeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mRecipeSpinner.setAdapter(recipeSpinnerAdapter);
    }


    public void getRecipeJSON(URL url, final Context context) throws IOException {

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseString = response.body().string();
                runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        try {
                            mRecipes = MainActivity.createRecipesFromJson(responseString, context);
                            setSpinner();
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

}

