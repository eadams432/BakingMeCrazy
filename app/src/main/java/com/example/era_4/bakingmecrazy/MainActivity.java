package com.example.era_4.bakingmecrazy;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.era_4.bakingmecrazy.utils.Recipe;
import com.example.era_4.bakingmecrazy.utils.RecipeAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {

    private RecipeAdapter mRecipeAdapter;
    private RecyclerView mRecyclerView;

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
    }

    public String getRecipeJSON(String url) throws IOException{
        OkHttpClient client = new OkHttpClient();
        String responseString = "";

        Request request = new Request.Builder()
                .url(url)
                .build();
        try {
            Response response = client.newCall(request).execute();
            responseString = response.body().string();
        } catch (Exception e){
            e.getMessage();
        }
        return responseString;
    }

    public static ArrayList<Recipe> createRecipesFromJson(String JsonString, Context context) throws JSONException {
        ArrayList<Recipe> recipes = new ArrayList<>();

        JSONArray array = new JSONArray(JsonString);
        for (int i=0; i < array.length(); i++){

            ArrayList<Recipe.Ingredient> ingredients = new ArrayList<>();
            ArrayList<Recipe.Step> steps = new ArrayList<>();

            JSONObject recipeJSON = array.getJSONObject(i);
            //general recipe info
            int recipeId = recipeJSON.getInt(context.getString(R.string.json_recipe_id));
            String recipeName = recipeJSON.getString(context.getString(R.string.json_recipe_image));
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
                ingredients.add(recipe.new Ingredient(name,quantity,measure));
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
                steps.add(recipe.new Step(id,shortdescr,descr,videoUrl,thumbnailUrl));
            }
            //recipes.add(new Recipe(recipeName,ingredients,steps,recipeServings,recipeId,recipeImage));
        }
        return recipes;
    }


}
