package com.example.era_4.bakingmecrazy.utils;

import android.content.Context;

import com.example.era_4.bakingmecrazy.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Recipe {
    private String name;
    private ArrayList<Ingredient> ingredients;
    private ArrayList<Step> steps;
    private int servings;
    private int id;
    private String image;

    public Recipe(String name, ArrayList<Ingredient> ingredients,ArrayList<Step> steps, int servings, int id, String image){
        this.name = name;
        this.ingredients = ingredients;
        this.steps = steps;
        this.servings = servings;
        this.id  = id;
        this.image = image;
    }

    public Recipe(String name,int servings, int id, String image){
        this.name = name;
        this.servings = servings;
        this.id  = id;
        this.image = image;
    }

    public class Ingredient {
        private float quantity;
        private String measure;
        private String name;

        public Ingredient(String name, float quantity, String measure){
            this.name = name;
            this.quantity = quantity;
            this.measure = measure;
        }
    }

    public class Step {
        private int id;
        private String shortDescr;
        private String descr;
        private String videoUrl;
        private String thumbnailUrl;

        public Step(int id, String shortDescr, String descr, String videoUrl, String thumbnailUrl){
            this.id = id;
            this.shortDescr = shortDescr;
            this.descr = descr;
            this.videoUrl = videoUrl;
            this.thumbnailUrl = thumbnailUrl;
        }
    }

    public static ArrayList<Recipe> createRecipesFromJson(String JsonString, Context context) throws JSONException {
        ArrayList<Recipe> recipes = new ArrayList<>();

        JSONArray array = new JSONArray(JsonString);
        for (int i=0; i < array.length(); i++){

            ArrayList<Recipe.Ingredient> ingredients = new ArrayList<>();
            ArrayList<Recipe.Step> steps = new ArrayList<>();

            JSONObject recipe = array.getJSONObject(i);
            //general recipe info
            int recipeId = recipe.getInt(context.getString(R.string.json_recipe_id));
            String recipeName = recipe.getString(context.getString(R.string.json_recipe_image));
            int recipeServings = Integer.parseInt(recipe.getString(context.getString((R.string.json_recipe_servings))));
            String recipeImage = recipe.getString(context.getString(R.string.json_recipe_image));

            //ingredients
            JSONArray ingredientsArray = recipe.getJSONArray(context.getString(R.string.json_ingredients_array_name));
            for (int j=0; j < ingredientsArray.length();j++){
                JSONObject ingredientJSON = ingredientsArray.getJSONObject(j);
                int quantity = ingredientJSON.getInt(context.getString(R.string.json_ingredients_quantity));
                String name = ingredientJSON.getString(context.getString(R.string.json_ingredients_name));
                String measure = ingredientJSON.getString(context.getString(R.string.json_ingredients_measure));
                //ingredients.add(new Ingredient(name,quantity,measure));
            }
            //steps
            JSONArray stepsArray = recipe.getJSONArray(context.getString(R.string.json_steps_array_name));
            for (int k=0; k < stepsArray.length();k++){
                JSONObject stepJSON = stepsArray.getJSONObject(k);
                int id = stepJSON.getInt(context.getString(R.string.json_step_id));
                String shortdescr = stepJSON.getString(context.getString(R.string.json_step_shortdescr));
                String descr = stepJSON.getString(context.getString(R.string.json_step_descr));
                String videoUrl = stepJSON.getString(context.getString(R.string.json_step_video_url));
                String thumbnailUrl = stepJSON.getString(context.getString(R.string.json_step_image_url));
                //steps.add(new Step(id,shortdescr,descr,videoUrl,thumbnailUrl));
            }
            recipes.add(new Recipe(recipeName,ingredients,steps,recipeServings,recipeId,recipeImage));
        }
        return recipes;
    }
}
