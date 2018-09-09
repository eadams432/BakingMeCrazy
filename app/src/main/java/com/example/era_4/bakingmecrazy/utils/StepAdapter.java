package com.example.era_4.bakingmecrazy.utils;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.era_4.bakingmecrazy.R;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by Ethan on 8/14/2018.
 */

public class StepAdapter extends ArrayAdapter<Step> {
    private Context mContext;
    private int layoutResourceId;
    //private ArrayList mData = new ArrayList();
    private ArrayList mData;
    private final String TAG = StepAdapter.class.getSimpleName();

    public StepAdapter(@NonNull Context context, int resource, ArrayList data) {
        super(context, resource, data);
        this.mContext = context;
        this.layoutResourceId = resource;
        this.mData = data;
    }

    static class ViewHolder {
        ImageView stepImageView;
        TextView shortDescrTextView;
        TextView stepIdTextView;
    }

    public void updateStepAdapter(ArrayList data){
        this.mData = data;
        notifyDataSetChanged();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        Step currentStep = getItem(position);

        if (convertView == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.shortDescrTextView = (TextView) convertView.findViewById(R.id.tv_recipe_shortdescr);
            holder.stepIdTextView = (TextView) convertView.findViewById(R.id.tv_recipe_step_number);
            holder.stepImageView = (ImageView) convertView.findViewById(R.id.iv_step_image);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (currentStep!= null) {

            holder.shortDescrTextView.setText(currentStep.getStepShortDescr());
            //set step image, or hide view if not present
            if (currentStep.getVideoUrl().length() > 0){
                Picasso.get()
                        .load(currentStep.getVideoUrl())
                        .into(holder.stepImageView);
            } else {
                holder.stepImageView.setVisibility(View.GONE);
            }

            if (currentStep.getStepId()>0) {
               // holder.stepIdTextView.setText(String.valueOf(currentStep.getStepId()));
            } else {
               // holder.stepIdTextView.setText("Intro! ");
            }
        }else {
            Log.e(TAG,"No step found!!");
        }
        return convertView;
    }
}
