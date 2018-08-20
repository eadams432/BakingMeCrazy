package com.example.era_4.bakingmecrazy.utils;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Ethan on 8/16/2018.
 */

public class Step implements Parcelable {

        private int id;
        private String shortDescr;
        private String descr;
        private String videoUrl;
        private String thumbnailUrl;

        public Step(int id, String shortDescr, String descr, String videoUrl, String thumbnailUrl){
            this.id = id;
            this.shortDescr = shortDescr;
            this.descr = descr;
            this.videoUrl = videoUrl;
            this.thumbnailUrl = thumbnailUrl;
        }

        public String getStepShortDescr(){
            return shortDescr;
        }

        public String getStepDescr(){
            return descr;
        }

        public int getStepId(){
            return id;
        }

        public String getVideoUrl(){
            return videoUrl;
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(id);
            dest.writeString(shortDescr);
            dest.writeString(descr);
            dest.writeString(videoUrl);
            dest.writeString(thumbnailUrl);
        }

        public static final Parcelable.Creator<Step> CREATOR
                = new Parcelable.Creator<Step>() {
            public Step createFromParcel(Parcel in) {
                return new Step(in);
            }

            public Step[] newArray(int size) {
                return new Step[size];
            }
        };

        private Step(Parcel in){
            this.id = in.readInt();
            this.shortDescr = in.readString();
            this.descr = in.readString();
            this.videoUrl = in.readString();
            this.thumbnailUrl = in.readString();

        }
}
