package com.example.era_4.bakingmecrazy.utils;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


public class StepDetailFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private PlayerView mPlayerView;
    private ExoPlayer mPlayer;
    private Step mStep;
    //private Recipe mRecipe;
    private int stepNumber;
    private TextView mStepDescription;
    private long mPlaybackPosition;
    private View mRootView;

    private OnNextStepClickListener mListener;

    public interface OnNextStepClickListener {
        // TODO: Update argument type and name
        void onNextStepClick(int stepInt);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    // TODO: Rename and change types and number of parameters
    public static StepDetailFragment newInstance(Step step, Context context) {
        StepDetailFragment fragment = new StepDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(context.getString(R.string.recipe_step_extra), step);
        fragment.setArguments(args);
        return fragment;
    }
    public StepDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(getString(R.string.position_bundle_name),mPlaybackPosition);
        //outState.putParcelable(getString(R.string.recipe_parcel_name),mRecipe);
        outState.putParcelable(getString(R.string.recipe_step_extra),mStep);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPlaybackPosition = getArguments().getLong(getString(R.string.position_bundle_name));
            mStep = getArguments().getParcelable(getString(R.string.recipe_step_extra));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Bundle args = getArguments();
        mStep = args.getParcelable(getString(R.string.recipe_step_extra));
        mRootView = inflater.inflate(R.layout.fragment_step_detail, container, false);
        mPlayerView =  (PlayerView) mRootView.findViewById(R.id.exo_player_view);
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

        mPlayer.setPlayWhenReady(false);
        mPlayer.prepare(mediaSource,true,false);
    }

    private void setViews(){
        mStepDescription.setText(mStep.getStepDescr());

        if (mStep.getVideoUrl()==null || mStep.getVideoUrl().length()==0){
            mPlayerView.setVisibility(View.GONE);
        } else {
            createPlayer();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

/*
    @Override
    protected void onRestart() {
        super.onRestart();
        mPlayer.seekTo(mPlaybackPosition);
    }
*/

    @Override
    public void onPause() {
        super.onPause();
        releasePlayer();
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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(int stepNumber) {
        if (mListener != null) {
            mListener.onNextStepClick(stepNumber);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnNextStepClickListener) {
            mListener = (OnNextStepClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


}