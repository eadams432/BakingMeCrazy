package com.example.era_4.bakingmecrazy;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.era_4.bakingmecrazy.utils.Recipe;
import com.example.era_4.bakingmecrazy.utils.RecipeDetailFragment;
import com.example.era_4.bakingmecrazy.utils.Step;
import com.example.era_4.bakingmecrazy.utils.StepAdapter;

import java.util.List;

public class RecipeDetail extends AppCompatActivity implements RecipeDetailFragment.OnStepClickListener{

    private Recipe mRecipe;
    private final String TAG = RecipeDetail.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState != null) {
            mRecipe = savedInstanceState.getParcelable(getString(R.string.recipe_parcel_name));
        } else {
            Log.e("TAG!","savedInstanceState is null!");
            Intent intent = getIntent();
            if (intent.hasExtra(getString(R.string.recipe_parcel_name))) {
                mRecipe = intent.getParcelableExtra(getString(R.string.recipe_parcel_name));
                //pass recipe to static fragment
                RecipeDetailFragment fragment = (RecipeDetailFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_recipe_detail_item);
                Bundle args = new Bundle();
                args.putParcelable(getString(R.string.recipe_parcel_name), mRecipe);
                fragment.setArguments(args);
            }
        }



       /* listView = (ListView)findViewById(R.id.lv_recipe_steps);

        if (savedInstanceState != null){
            mRecipe = savedInstanceState.getParcelable(getString(R.string.recipe_parcel_name));
            int position = savedInstanceState.getInt(getString(R.string.recipe_step_position),1);
            //scroll to previous position
            listView.setSelection(position);
        } else {
            Intent intent = getIntent();
            if (intent.hasExtra(getString(R.string.recipe_parcel_name))) {
                mRecipe = intent.getParcelableExtra(getString(R.string.recipe_parcel_name));
            }
        }
        stepAdapter = new StepAdapter(this, R.layout.recipe_detail_item,mRecipe.getSteps());
        listView.setAdapter(stepAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //get the step
                Step step = (Step)parent.getItemAtPosition(position);
                //create and start intent!
                Intent stepIntent = new Intent(view.getContext(),StepDetail.class);
                stepIntent.putExtra(getString(R.string.recipe_step_extra),step.getStepId());
                stepIntent.putExtra(getString(R.string.recipe_parcel_name),mRecipe);
                startActivity(stepIntent);
            }
        });*/
    }

    @Override
    public void onStepClick(int stepId) {
        //in single pane, start new activity
        Intent stepIntent = new Intent(this,StepDetail.class);
        stepIntent.putExtra(getString(R.string.recipe_step_extra),stepId);
        stepIntent.putExtra(getString(R.string.recipe_parcel_name),mRecipe);
        startActivity(stepIntent);

        //in dual pane, update the detail fragment
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(getString(R.string.recipe_parcel_name),mRecipe);
        /*int listPosition = listView.getFirstVisiblePosition();
        outState.putInt(getString(R.string.recipe_step_position),listPosition);
        */
    }
}
