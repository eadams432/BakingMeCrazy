package com.example.era_4.bakingmecrazy.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.era_4.bakingmecrazy.R;
import com.example.era_4.bakingmecrazy.StepDetail;

public class RecipeDetailFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String RECIPE_PARCEL_STRING =  "";
    private static final String RECIPE_STEP_STRING = "step";

    private ListView mListView;
    private StepAdapter mStepAdapter;
    private Recipe mRecipe;
    private View mRootView;

    private OnStepClickListener mListener;

    public interface OnStepClickListener {
        void onStepClick(int stepId);
    }

    public RecipeDetailFragment() {
        // Required empty public constructor
    }

    /*public static RecipeDetailFragment newInstance(Recipe recipe) {
        RecipeDetailFragment fragment = new RecipeDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(RECIPE_PARAM_STRING, recipe);
        fragment.setArguments(args);
        return fragment;
    }*/

    public void addRecipe(Recipe recipe) {
        //RecipeDetailFragment fragment = new RecipeDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(getString(R.string.recipe_parcel_name), recipe);
        this.setArguments(args);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(getString(R.string.recipe_parcel_name),mRecipe);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mRecipe = getArguments().getParcelable(getString(R.string.recipe_parcel_name));
        }
    }

    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);
        setViews();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_recipe_detail, container, false);
        mListView = (ListView) mRootView.findViewById(R.id.lv_recipe_steps);


        return mRootView;
    }

    public void setViews(){
        //get parameters
        if (isAdded()) {
            Bundle args = getArguments();
            mRecipe = args.getParcelable("recipe");

            mStepAdapter = new StepAdapter(mRootView.getContext(), R.layout.recipe_detail_item, mRecipe.getSteps());
            mListView.setAdapter(mStepAdapter);

            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //get the step
                    Step step = (Step) parent.getItemAtPosition(position);
                    mListener.onStepClick(step.getStepId());
                }
            });
        }

    }

/*    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }*/

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnStepClickListener) {
            mListener = (OnStepClickListener) context;
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
