package com.example.era_4.bakingmecrazy;


import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.example.era_4.bakingmecrazy.utils.Recipe;
import com.example.era_4.bakingmecrazy.utils.RecipeDetailFragment;
import com.example.era_4.bakingmecrazy.utils.Step;
import com.example.era_4.bakingmecrazy.utils.StepDetailFragment;

import org.json.JSONException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class RecipeDetail extends AppCompatActivity implements RecipeDetailFragment.OnStepClickListener, StepDetailFragment.OnNextStepClickListener{

    private Recipe mRecipe;
    private final String TAG = RecipeDetail.class.getSimpleName();
    private boolean mTwoPane;
    private android.support.v4.app.FragmentManager mFragmentManager;
    private StepDetailFragment mFragment;
    private String mWidgetRecipeName;
    private Step mStep;

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
            mStep = savedInstanceState.getParcelable(getString(R.string.recipe_step_extra));
            //setTwoPaneMode(mStep);
            mFragmentManager = getSupportFragmentManager();
        } else {
            //loading for the first time
            Intent intent = getIntent();
            if (intent.hasExtra(getString(R.string.recipe_parcel_name))) {
                //selected recipe is coming from MainActivity
                mRecipe = intent.getParcelableExtra(getString(R.string.recipe_parcel_name));
                mStep = mRecipe.getSteps().get(0);
                setTwoPaneMode(mStep);
                setMasterFragment();

            } else if(intent.hasExtra(getString(R.string.widget_recipe_name))) {
                //selected recipe is coming from a widget, need to build the recipe object
                mWidgetRecipeName = intent.getStringExtra(getString(R.string.widget_recipe_name));
                getRecipes();
            }
        }
        setToolbarTitle();
    }

    public void setMasterFragment(){
        //add parameters to the static fragment (present in single and dual pane modes)
        RecipeDetailFragment fragment = (RecipeDetailFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_recipe_detail_item);
        Bundle args = new Bundle();
        args.putParcelable(getString(R.string.recipe_parcel_name), mRecipe);
        fragment.setArguments(args);
    }

    public void setTwoPaneMode(Step step){
        if (mTwoPane){
            //if dual pane, load the Step fragment with the first step
            mFragmentManager = getSupportFragmentManager();
            mFragment = StepDetailFragment.newInstance(step,mRecipe, this);
            mFragmentManager.beginTransaction()
                    .add(R.id.step_container, mFragment)
                    .commit();
        }
    }

    public void setToolbarTitle(){
        //set toolbar title to recipe name
        if (mRecipe != null) {
            setTitle(mRecipe.getName());
        }
    }

    @Override
    public void onStepClick(int stepId) {
        //in single pane, start new activity
        mStep = mRecipe.getSteps().get(stepId);
        if (!(mTwoPane)) {
            Intent stepIntent = new Intent(this, StepDetail.class);
            stepIntent.putExtra(getString(R.string.recipe_step_extra), stepId);
            stepIntent.putExtra(getString(R.string.recipe_parcel_name), mRecipe);
            startActivity(stepIntent);
        }else{
            //in dual pane, update the detail fragment
            mFragment = StepDetailFragment.newInstance(mStep,mRecipe, this);
            mFragmentManager.beginTransaction()
                    .replace(R.id.step_container, mFragment)
                    .commit();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(getString(R.string.recipe_parcel_name),mRecipe);
        outState.putParcelable(getString(R.string.recipe_step_extra),mStep);
    }

    @Override
    public void onNextStepClick(int stepInt) {

    }

    public void getRecipes(){
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
                    ArrayList<Recipe> recipes;
                    Recipe widgetRecipe;

                    @Override
                    public void run() {
                        try {
                            recipes = MainActivity.createRecipesFromJson(responseString, context);
                            for (int i=0; i < recipes.size(); i++){
                                if (recipes.get(i).getName().equals(mWidgetRecipeName)){
                                    widgetRecipe = recipes.get(i);
                                    break;
                                }
                            }
                            mRecipe = widgetRecipe;
                            mStep = mRecipe.getSteps().get(0);
                            setTwoPaneMode(mStep);
                            setMasterFragment();
                            setToolbarTitle();

                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

}
