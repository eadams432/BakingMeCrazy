package com.example.era_4.bakingmecrazy;

import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.era_4.bakingmecrazy.utils.Recipe;
import com.example.era_4.bakingmecrazy.utils.Step;
import com.example.era_4.bakingmecrazy.utils.StepDetailFragment;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;

public class StepDetail extends AppCompatActivity implements StepDetailFragment.OnNextStepClickListener {

    private PlayerView mPlayerView;
    private ExoPlayer mPlayer;
    private Step mStep;
    private Recipe mRecipe;
    private int stepNumber;
    private TextView mStepDescription;
    private long mPlaybackPosition;
    private StepDetailFragment mFragment;
    private android.support.v4.app.FragmentManager mFragmentManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mFragmentManager = getSupportFragmentManager();

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            if (intent.hasExtra(getString(R.string.recipe_step_extra))) {
                stepNumber = intent.getIntExtra(getString(R.string.recipe_step_extra), 1);
            }

            if (intent.hasExtra(getString(R.string.recipe_parcel_name))) {
                mRecipe = intent.getParcelableExtra(getString(R.string.recipe_parcel_name));
                mStep = mRecipe.getSteps().get(stepNumber);
                mFragment = StepDetailFragment.newInstance(mStep,mRecipe, this);
                mFragmentManager.beginTransaction()
                        .add(R.id.step_container, mFragment)
                        .commit();
            }
        } else {
            mRecipe = savedInstanceState.getParcelable(getString(R.string.recipe_parcel_name));
            mStep = savedInstanceState.getParcelable(getString(R.string.recipe_step_extra));
        }
        if (mStep != null){
            //set toolbar title to step short descr
            setTitle(mStep.getStepShortDescr());
        }

        //if single pane and in landscape, hide the action bar
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            getSupportActionBar().hide();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(getString(R.string.recipe_parcel_name),mRecipe);
        outState.putParcelable(getString(R.string.recipe_step_extra),mStep);
    }

    @Override
    public void onNextStepClick(int stepInt) {
        //replace the step with next or previous one
        Log.i("TAG","In onNextStepClick!");
        mStep = mRecipe.getSteps().get(stepInt);
        mFragment = StepDetailFragment.newInstance(mStep,mRecipe, this);
        mFragmentManager.beginTransaction()
                .replace(R.id.step_container, mFragment)
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
