package com.example.era_4.bakingmecrazy.utils;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.era_4.bakingmecrazy.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Recipe implements Parcelable {
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

    public String getName() {
        return name;
    }

    public int getNumberOfSteps(){
        if (steps==null){
            return 0;
        }
        else {
            return steps.size();
        }
    }

    public int getNumberOfIngredients(){
        if (ingredients==null){
            return 0;
        }
        else {
            return ingredients.size();
        }
    }

    public ArrayList<Ingredient> getIngredients() {
        return ingredients;
    }

    public ArrayList<Step> getSteps() {
        return steps;
    }

    public int getServings() {
        return servings;
    }

    public int getId() {
        return id;
    }

    public String getImage() {
        return image;
    }

    public void addIngredient(String name, float quantity, String measure){
        this.ingredients.add(new Ingredient(name, quantity,measure));
    }

    public void addStep(int id,String shortDescr, String descr, String videoUrl, String thumbnaulUrl ){
        this.steps.add(new Step(id,shortDescr,descr,videoUrl,thumbnaulUrl));
    }

    public void setSteps(ArrayList<Step> steps){
        this.steps = steps;
    }

    public void setIngredients(ArrayList<Ingredient> ingredients){
        this.ingredients = ingredients;
    }

    public Recipe(String name,int servings, int id, String image){
        this.name = name;
        this.servings = servings;
        this.id  = id;
        this.image = image;
        this.ingredients = new ArrayList<>();
        this.steps = new ArrayList<>();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeInt(servings);
        dest.writeList(ingredients);
        dest.writeList(steps);

    }

    public static final Parcelable.Creator<Recipe> CREATOR
            = new Parcelable.Creator<Recipe>() {
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };

    private Recipe(Parcel in){
        this.id = in.readInt();
        this.name = in.readString();
        this.servings = in.readInt();
        this.ingredients = in.readArrayList(Ingredient.class.getClassLoader());
        this.steps = in.readArrayList(Step.class.getClassLoader());
    }
}
