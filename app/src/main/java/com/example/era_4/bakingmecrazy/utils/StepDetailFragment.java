package com.example.era_4.bakingmecrazy.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.era_4.bakingmecrazy.R;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;


public class StepDetailFragment extends Fragment implements View.OnClickListener{

    private PlayerView mPlayerView;
    private ExoPlayer mPlayer;
    private Step mStep;
    private Recipe mRecipe;
    private int stepNumber;
    private TextView mStepDescription;
    private long mPlaybackPosition;
    private int mPlaybackState;
    private View mRootView;
    private Button nextButton;
    private Button prevButton;
    private boolean isLandscape;
    private boolean isTwoPane;
    private boolean playWhenReady;

    private OnNextStepClickListener mListener;

    public interface OnNextStepClickListener {
        void onNextStepClick(int stepInt);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        int newStepNumber;
        //add or subtract from the step number
        if (id == R.id.next_button){
            newStepNumber = mStep.getStepId() + 1;
        } else {
            newStepNumber = mStep.getStepId() - 1;
        }
        onButtonPressed(newStepNumber);
    }

    public void onButtonPressed(int stepNumber) {
        if (mListener != null) {
            mListener.onNextStepClick(stepNumber);
        } else {
            Log.i("TAG","listener is null!");
        }
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    public static StepDetailFragment newInstance(Step step, Recipe recipe, Context context) {
        StepDetailFragment fragment = new StepDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(context.getString(R.string.recipe_step_extra), step);
        args.putParcelable(context.getString(R.string.recipe_parcel_name),recipe);
        fragment.setArguments(args);
        return fragment;
    }
    public StepDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mPlayer != null) {
            mPlaybackPosition = mPlayer.getCurrentPosition();
            mPlaybackState = mPlayer.getPlaybackState();
        }
        outState.putLong(getString(R.string.position_bundle_name),mPlaybackPosition);
        //outState.putInt(getString(R.string.exoplayer_state),mPlaybackState);
        outState.putBoolean(getString(R.string.play_when_ready),playWhenReady);
        outState.putParcelable(getString(R.string.recipe_step_extra),mStep);
        outState.putParcelable(getString(R.string.recipe_parcel_name),mRecipe);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPlaybackPosition = getArguments().getLong(getString(R.string.position_bundle_name));
            mStep = getArguments().getParcelable(getString(R.string.recipe_step_extra));
            mRecipe = getArguments().getParcelable(getString(R.string.recipe_parcel_name));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Bundle args = getArguments();
        mStep = args.getParcelable(getString(R.string.recipe_step_extra));
        mRecipe = args.getParcelable(getString(R.string.recipe_parcel_name));
        playWhenReady = false;

        if (savedInstanceState != null){
            mPlaybackPosition = savedInstanceState.getLong(getString(R.string.position_bundle_name));
            //mPlaybackState = savedInstanceState.getInt(getString(R.string.exoplayer_state));
            playWhenReady = savedInstanceState.getBoolean(getString(R.string.play_when_ready));
        }

        mRootView = inflater.inflate(R.layout.fragment_step_detail, container, false);
        mPlayerView =  (PlayerView) mRootView.findViewById(R.id.exo_player_view);

        if (mRootView.findViewById(R.id.two_pane_layout)==null){
            isTwoPane = false;
        } else {
            isTwoPane = true;
        }

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            isLandscape = true;
        } else {
            isLandscape = false;
            /* use buttons only in portrait mode */
            nextButton = (Button) mRootView.findViewById(R.id.next_button);
            prevButton = (Button) mRootView.findViewById(R.id.prev_button);
            //only display buttons if there's another step to show
            if (mStep.getStepId() == mRecipe.getSteps().size()-1 || isTwoPane){
                nextButton.setVisibility(View.GONE);
            } else {
                nextButton.setOnClickListener(this);
            }

            if (mStep.getStepId() == 0 || isTwoPane){
                prevButton.setVisibility(View.GONE);
            } else {
                prevButton.setOnClickListener(this);
            }
        }

        mStepDescription = (TextView) mRootView.findViewById(R.id.tv_step_description);

        setViews();

        return mRootView;
    }

    private void createPlayer(){
        Uri videoUri = Uri.parse(mStep.getVideoUrl());
        MediaSource mediaSource = new ExtractorMediaSource.Factory(
                new DefaultHttpDataSourceFactory(getString(R.string.user_agent_string)))
                .createMediaSource(videoUri);

        DefaultRenderersFactory defaultRenderersFactory = new DefaultRenderersFactory(mRootView.getContext());
        DefaultTrackSelector defaultTrackSelector = new DefaultTrackSelector();
        DefaultLoadControl defaultLoadControl = new DefaultLoadControl();
        mPlayer = ExoPlayerFactory.newSimpleInstance(defaultRenderersFactory,defaultTrackSelector,defaultLoadControl);
        mPlayerView.setPlayer(mPlayer);
        //start player?

        mPlayer.setPlayWhenReady(playWhenReady);
        mPlayer.prepare(mediaSource,true,false);
        if (mPlaybackPosition > 0){
            mPlayer.seekTo(mPlaybackPosition);
        }
    }

    private void setViews(){
        if (mStepDescription != null) {
            mStepDescription.setText(mStep.getStepDescr());
        }
        //if no video present, hide player view
        if (mStep.getVideoUrl()==null || mStep.getVideoUrl().length()==0){
            mPlayerView.setVisibility(View.GONE);
        } else {
            //if in landscape mode, Single pane, and there's a video, hide other views
            if (mStepDescription != null && isLandscape && !isTwoPane) {
                mStepDescription.setVisibility(View.GONE);
            }
           // nextButton.setVisibility(View.GONE);
          //  prevButton.setVisibility(View.GONE);
            createPlayer();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    @Override
    public void onPause() {
        super.onPause();
        releasePlayer();
    }

    private void releasePlayer() {
        if (mPlayer != null) {
            mPlaybackPosition = mPlayer.getCurrentPosition();
            //currentWindow = mPlayer.getCurrentWindowIndex();
            playWhenReady = mPlayer.getPlayWhenReady();
            mPlaybackState = mPlayer.getPlaybackState();

            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnNextStepClickListener) {
            mListener = (OnNextStepClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnNextStepClickListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


}
