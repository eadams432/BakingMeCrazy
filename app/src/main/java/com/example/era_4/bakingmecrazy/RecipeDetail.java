package com.example.era_4.bakingmecrazy;


import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.era_4.bakingmecrazy.utils.Recipe;
import com.example.era_4.bakingmecrazy.utils.RecipeDetailFragment;
import com.example.era_4.bakingmecrazy.utils.StepDetailFragment;


public class RecipeDetail extends AppCompatActivity implements RecipeDetailFragment.OnStepClickListener, StepDetailFragment.OnNextStepClickListener{

    private Recipe mRecipe;
    private final String TAG = RecipeDetail.class.getSimpleName();
    private boolean mTwoPane;
    private android.support.v4.app.FragmentManager mFragmentManager;
    private StepDetailFragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
       // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        if (findViewById(R.id.two_pane_layout) != null){
            mTwoPane = true;
        }else{
            mTwoPane = false;
        }

        if (savedInstanceState != null) {
            mRecipe = savedInstanceState.getParcelable(getString(R.string.recipe_parcel_name));
        } else {
            Intent intent = getIntent();
            if (intent.hasExtra(getString(R.string.recipe_parcel_name))) {
                mRecipe = intent.getParcelableExtra(getString(R.string.recipe_parcel_name));
                if (mTwoPane){
                    //if dual pane, load the Step fragment with the first step
                    mFragmentManager = getSupportFragmentManager();
                    mFragment = StepDetailFragment.newInstance(mRecipe.getSteps().get(0),mRecipe, this);
                    mFragmentManager.beginTransaction()
                            .add(R.id.step_container, mFragment)
                            .commit();
                }
                //add parameters to the static fragment (present in single and dual pane modes)
                RecipeDetailFragment fragment = (RecipeDetailFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_recipe_detail_item);
                Bundle args = new Bundle();
                args.putParcelable(getString(R.string.recipe_parcel_name), mRecipe);
                fragment.setArguments(args);
            }
        }
        //set toolbar title to recipe name
        if (mRecipe != null) {
            setTitle(mRecipe.getName());
        }

    }

    @Override
    public void onStepClick(int stepId) {
        //in single pane, start new activity
        if (!(mTwoPane)) {
            Intent stepIntent = new Intent(this, StepDetail.class);
            stepIntent.putExtra(getString(R.string.recipe_step_extra), stepId);
            stepIntent.putExtra(getString(R.string.recipe_parcel_name), mRecipe);
            startActivity(stepIntent);
        }else{
            //in dual pane, update the detail fragment
            mFragment = StepDetailFragment.newInstance(mRecipe.getSteps().get(stepId),mRecipe, this);
            mFragmentManager.beginTransaction()
                    .replace(R.id.step_container, mFragment)
                    .commit();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(getString(R.string.recipe_parcel_name),mRecipe);
    }

    @Override
    public void onNextStepClick(int stepInt) {

    }
}
