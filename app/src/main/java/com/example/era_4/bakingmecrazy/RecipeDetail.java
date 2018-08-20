package com.example.era_4.bakingmecrazy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.era_4.bakingmecrazy.utils.Recipe;
import com.example.era_4.bakingmecrazy.utils.Step;
import com.example.era_4.bakingmecrazy.utils.StepAdapter;

import java.util.List;

public class RecipeDetail extends AppCompatActivity {

    private Recipe mRecipe;
    private final String TAG = RecipeDetail.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        if (intent.hasExtra(getString(R.string.recipe_parcel_name))){
            //do the thing!
            mRecipe = intent.getParcelableExtra(getString(R.string.recipe_parcel_name));
            Log.i(TAG,"Found recipe with " + mRecipe.getSteps().size() + " steps");
            ListView listView = (ListView)findViewById(R.id.lv_recipe_steps);
            StepAdapter stepAdapter = new StepAdapter(this, R.layout.recipe_detail_item,mRecipe.getSteps());
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
            });
        } else {
            Log.e(TAG,"No intent received");
        }

    }

}
