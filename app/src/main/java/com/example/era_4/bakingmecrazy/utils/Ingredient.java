package com.example.era_4.bakingmecrazy.utils;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Ethan on 8/16/2018.
 */

public class Ingredient implements Parcelable{

        private float quantity;
        private String measure;
        private String name;

        public Ingredient(String name, float quantity, String measure){
            this.name = name;
            this.quantity = quantity;
            this.measure = measure;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeFloat(quantity);
            dest.writeString(measure);
            dest.writeString(name);
        }

        public static final Parcelable.Creator<Ingredient> CREATOR
                = new Parcelable.Creator<Ingredient>() {
            public Ingredient createFromParcel(Parcel in) {

                return new Ingredient(in);
            }

            public Ingredient[] newArray(int size) {
                return new Ingredient[size];
            }
        };

        private Ingredient(Parcel in){
            this.quantity = in.readFloat();
            this.measure = in.readString();
            this.name = in.readString();
        }
}

