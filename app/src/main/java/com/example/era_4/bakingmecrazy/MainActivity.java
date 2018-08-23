package com.example.era_4.bakingmecrazy;

import android.content.Context;
import android.net.Uri;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.era_4.bakingmecrazy.utils.Ingredient;
import com.example.era_4.bakingmecrazy.utils.Recipe;
import com.example.era_4.bakingmecrazy.utils.RecipeAdapter;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_recipe_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecipeAdapter = new RecipeAdapter();
        mRecyclerView.setAdapter(mRecipeAdapter);

        if (savedInstanceState != null && savedInstanceState.size()>0){
            //load saved stuff
            mRecipes = savedInstanceState.getParcelableArrayList(getString(R.string.recipe_array_name));
            mRecipeAdapter.updateRecipes(mRecipes);
            //reset the recyclerview state
            mRecyclerView.getLayoutManager()
                    .onRestoreInstanceState(savedInstanceState.getParcelable(getString(R.string.recyclerview_parcel_name)));
        } else {
            String recipeJSON = "";
            try {
                Uri uri = Uri.parse(getString(R.string.recipes_url));
                URL url = null;
                try {
                    url = new URL(uri.toString());
                    getRecipeJSON(url);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        //outState.putParcelableArrayList(getString(R.string.recipe_array_name),mRecipes);
        //save state of recyclerview
        Parcelable recyclerViewState =  mRecyclerView.getLayoutManager().onSaveInstanceState();
        outState.putParcelable(getString(R.string.recyclerview_parcel_name),recyclerViewState);
    }

    public void getRecipeJSON(URL url) throws IOException{
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
                                    mRecipes = createRecipesFromJson(responseString);
                                    mRecipeAdapter.updateRecipes(mRecipes);
                                }catch (JSONException e){
                                    e.printStackTrace();
                                }
                            }
                        });
                }
            });
    }


    public ArrayList<Recipe> createRecipesFromJson(String JsonString) throws JSONException {
        ArrayList<Recipe> recipes = new ArrayList<>();

        JSONArray array = new JSONArray(JsonString);
        for (int i=0; i < array.length(); i++){

            ArrayList<Ingredient> ingredients = new ArrayList<>();
            ArrayList<Step> steps = new ArrayList<>();

            JSONObject recipeJSON = array.getJSONObject(i);
            //general recipe info
            int recipeId = recipeJSON.getInt(getString(R.string.json_recipe_id));
            String recipeName = recipeJSON.getString(getString(R.string.json_recipe_name));
            int recipeServings = Integer.parseInt(recipeJSON.getString(getString((R.string.json_recipe_servings))));
            String recipeImage = recipeJSON.getString(getString(R.string.json_recipe_image));
            Recipe recipe = new Recipe(recipeName,recipeServings,recipeId, recipeImage);

            //ingredients
            JSONArray ingredientsArray = recipeJSON.getJSONArray(getString(R.string.json_ingredients_array_name));
            for (int j=0; j < ingredientsArray.length();j++){
                JSONObject ingredientJSON = ingredientsArray.getJSONObject(j);
                int quantity = ingredientJSON.getInt(getString(R.string.json_ingredients_quantity));
                String name = ingredientJSON.getString(getString(R.string.json_ingredients_name));
                String measure = ingredientJSON.getString(getString(R.string.json_ingredients_measure));
                recipe.addIngredient(name,quantity,measure);
            }
            //steps
            JSONArray stepsArray = recipeJSON.getJSONArray(getString(R.string.json_steps_array_name));
            for (int k=0; k < stepsArray.length();k++){
                JSONObject stepJSON = stepsArray.getJSONObject(k);
                int id = stepJSON.getInt(getString(R.string.json_step_id));
                String shortdescr = stepJSON.getString(getString(R.string.json_step_shortdescr));
                String descr = stepJSON.getString(getString(R.string.json_step_descr));
                String videoUrl = stepJSON.getString(getString(R.string.json_step_video_url));
                String thumbnailUrl = stepJSON.getString(getString(R.string.json_step_image_url));
                recipe.addStep(id,shortdescr,descr,videoUrl,thumbnailUrl);
            }
            recipes.add(recipe);
        }
        return recipes;
    }
}
