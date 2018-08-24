package com.example.era_4.bakingmecrazy;

import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

      /*  mPlayerView = (PlayerView)findViewById(R.id.exo_player_view);
        mStepDescription = (TextView) findViewById(R.id.tv_step_description);

        if (savedInstanceState != null && savedInstanceState.size() > 0 ){
            mRecipe = savedInstanceState.getParcelable(getString(R.string.recipe_parcel_name));
            mStep = savedInstanceState.getParcelable(getString(R.string.recipe_step_extra));
            mPlaybackPosition = savedInstanceState.getLong(getString(R.string.position_bundle_name));
            setViews();
            mPlayer.seekTo(mPlaybackPosition);
        }*/

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
        }
    }

    @Override
    public void onNextStepClick(int stepInt) {
        //replace the step with next or previous one
        mStep = mRecipe.getSteps().get(stepInt);
        mFragment = StepDetailFragment.newInstance(mStep,mRecipe, this);
        mFragmentManager.beginTransaction()
                .replace(R.id.step_container, mFragment)
                .commit();
    }

    /*
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mPlaybackPosition = mPlayer.getCurrentPosition();
        outState.putLong(getString(R.string.position_bundle_name),mPlaybackPosition);
        outState.putParcelable(getString(R.string.recipe_parcel_name),mRecipe);
        outState.putParcelable(getString(R.string.recipe_step_extra),mStep);
    }

    private void setViews(){
        mStepDescription.setText(mStep.getStepDescr());

        if (mStep.getVideoUrl()==null || mStep.getVideoUrl().length()==0){
            mPlayerView.setVisibility(View.GONE);
        } else {
            createPlayer();
        }
    }

    private void createPlayer(){
        Uri videoUri = Uri.parse(mStep.getVideoUrl());
        MediaSource mediaSource = new ExtractorMediaSource.Factory(
                new DefaultHttpDataSourceFactory(getString(R.string.user_agent_string)))
                .createMediaSource(videoUri);

        DefaultRenderersFactory defaultRenderersFactory = new DefaultRenderersFactory(this);
        DefaultTrackSelector defaultTrackSelector = new DefaultTrackSelector();
        DefaultLoadControl defaultLoadControl = new DefaultLoadControl();
        mPlayer = ExoPlayerFactory.newSimpleInstance(defaultRenderersFactory,defaultTrackSelector,defaultLoadControl);
        mPlayerView.setPlayer(mPlayer);
        //start player?

        mPlayer.setPlayWhenReady(false);
        mPlayer.prepare(mediaSource,true,false);

    }

    private void releasePlayer() {
        if (mPlayer != null) {
            mPlaybackPosition = mPlayer.getCurrentPosition();
            //currentWindow = mPlayer.getCurrentWindowIndex();
            //playWhenReady = mPlayer.getPlayWhenReady();
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mPlayer.seekTo(mPlaybackPosition);
    }

    @Override
    protected void onPause() {
        super.onPause();
        releasePlayer();
    }*/
}
