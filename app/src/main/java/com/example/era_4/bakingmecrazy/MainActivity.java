package com.example.era_4.bakingmecrazy;

import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.test.espresso.IdlingResource;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.era_4.bakingmecrazy.utils.Ingredient;
import com.example.era_4.bakingmecrazy.utils.Recipe;
import com.example.era_4.bakingmecrazy.utils.RecipeAdapter;
import com.example.era_4.bakingmecrazy.utils.SimpleIdlingResource;
import com.example.era_4.bakingmecrazy.utils.Step;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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


public class MainActivity extends AppCompatActivity {

    private RecipeAdapter mRecipeAdapter;
    private RecyclerView mRecyclerView;
    private ArrayList<Recipe> mRecipes;
    private boolean mTwoPane;

    @Nullable private SimpleIdlingResource mIdlingResource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_recipe_list);

        mIdlingResource = new SimpleIdlingResource();

        if (findViewById(R.id.tablet_layout) != null){
            mTwoPane = true;
            int columns = 3;
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
                columns = 3;
            } else {
                columns = 2;
            }
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this,columns);
            mRecyclerView.setLayoutManager(gridLayoutManager);
        } else {
            mTwoPane = false;
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            mRecyclerView.setLayoutManager(linearLayoutManager);
        }

        //mRecyclerView.setHasFixedSize(true);
        mRecipeAdapter = new RecipeAdapter();
        mRecyclerView.setAdapter(mRecipeAdapter);

        if (savedInstanceState != null && savedInstanceState.size()>0){
            //load saved stuff
            mRecipes = savedInstanceState.getParcelableArrayList(getString(R.string.recipe_array_name));
            mRecipeAdapter.updateRecipes(mRecipes);
        } else {
            String recipeJSON = "";
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
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(getString(R.string.recipe_array_name),mRecipes);

    }

    public void getRecipeJSON(URL url, final Context context) throws IOException{

        if (!(mIdlingResource==null)){
            mIdlingResource.setIdleState(false);
        }

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
                                    mRecipes = createRecipesFromJson(responseString, context);
                                    mRecipeAdapter.updateRecipes(mRecipes);
                                    if (!(mIdlingResource==null)){
                                        mIdlingResource.setIdleState(true);
                                    }
                                }catch (JSONException e){
                                    e.printStackTrace();
                                }
                            }
                        });
                }
            });
    }

    public IdlingResource getIdlingResource(){
        return mIdlingResource;
    }


    public static ArrayList<Recipe> createRecipesFromJson(String JsonString, Context context) throws JSONException {
        ArrayList<Recipe> recipes = new ArrayList<>();

        JSONArray array = new JSONArray(JsonString);
        for (int i=0; i < array.length(); i++){

            ArrayList<Ingredient> ingredients = new ArrayList<>();
            ArrayList<Step> steps = new ArrayList<>();

            JSONObject recipeJSON = array.getJSONObject(i);
            //general recipe info
            int recipeId = recipeJSON.getInt(context.getString(R.string.json_recipe_id));
            String recipeName = recipeJSON.getString(context.getString(R.string.json_recipe_name));
            int recipeServings = Integer.parseInt(recipeJSON.getString(context.getString((R.string.json_recipe_servings))));
            String recipeImage = recipeJSON.getString(context.getString(R.string.json_recipe_image));
            Recipe recipe = new Recipe(recipeName,recipeServings,recipeId, recipeImage);

            //ingredients
            JSONArray ingredientsArray = recipeJSON.getJSONArray(context.getString(R.string.json_ingredients_array_name));
            for (int j=0; j < ingredientsArray.length();j++){
                JSONObject ingredientJSON = ingredientsArray.getJSONObject(j);
                int quantity = ingredientJSON.getInt(context.getString(R.string.json_ingredients_quantity));
                String name = ingredientJSON.getString(context.getString(R.string.json_ingredients_name));
                String measure = ingredientJSON.getString(context.getString(R.string.json_ingredients_measure));
                recipe.addIngredient(name,quantity,measure);
            }
            //steps
            JSONArray stepsArray = recipeJSON.getJSONArray(context.getString(R.string.json_steps_array_name));
            for (int k=0; k < stepsArray.length();k++){
                JSONObject stepJSON = stepsArray.getJSONObject(k);
                int id = stepJSON.getInt(context.getString(R.string.json_step_id));
                String shortdescr = stepJSON.getString(context.getString(R.string.json_step_shortdescr));
                String descr = stepJSON.getString(context.getString(R.string.json_step_descr));
                String videoUrl = stepJSON.getString(context.getString(R.string.json_step_video_url));
                String thumbnailUrl = stepJSON.getString(context.getString(R.string.json_step_image_url));
                recipe.addStep(id,shortdescr,descr,videoUrl,thumbnailUrl);
            }
            recipes.add(recipe);
        }
        return recipes;
    }
}
