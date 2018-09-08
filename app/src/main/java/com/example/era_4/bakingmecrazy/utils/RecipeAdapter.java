package com.example.era_4.bakingmecrazy.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.era_4.bakingmecrazy.R;
import com.example.era_4.bakingmecrazy.RecipeDetail;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private ArrayList<Recipe> mRecipes;

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate view's layout
        Context context = parent.getContext();
        int layoutId = R.layout.recipe_list_item;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(layoutId,parent,false);
        RecipeViewHolder recipeViewHolder = new RecipeViewHolder(view);
        return recipeViewHolder;
    }

    public void updateRecipes(ArrayList<Recipe> newRecipes){
        mRecipes = newRecipes;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        holder.mRecipe = mRecipes.get(position);
        holder.recipeName.setText(holder.mRecipe.getName());
    }



    @Override
    public int getItemCount() {
        if (mRecipes == null) {
            return 0;
        }
        else {
            return mRecipes.size();
        }
    }

    class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView recipeName;
        private Recipe mRecipe;

        public RecipeViewHolder(View itemView){
            super(itemView);
            recipeName = (TextView) itemView.findViewById(R.id.tv_recipe_name);
            itemView.setOnClickListener(this);
        }

        private Recipe getRecipe(){
            return mRecipe;
        }

        @Override
        public void onClick(View v) {
         //Use intent to trigger next activity
            Intent intent = new Intent(v.getContext(), RecipeDetail.class);
            //put parcelable arraylist extra?
           intent.putExtra(v.getContext().getResources().getString(R.string.recipe_parcel_name),getRecipe());
           v.getContext().startActivity(intent);
        }
    }
}
