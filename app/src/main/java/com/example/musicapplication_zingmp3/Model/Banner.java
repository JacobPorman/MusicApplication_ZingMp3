package com.example.musicapplication_zingmp3.Model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Banner implements Parcelable {
    String id;
    String image;
    String checkBanner;

    public String getCheckBanner() {
        return checkBanner;
    }

    public Banner(String id, String image, String checkBanner) {
        this.id = id;
        this.image = image;
        this.checkBanner = checkBanner;
    }

    protected Banner(Parcel in) {
        id = in.readString();
        image = in.readString();
        checkBanner = in.readString();
    }

    public static final Creator<Banner> CREATOR = new Creator<Banner>() {
        @Override
        public Banner createFromParcel(Parcel in) {
            return new Banner(in);
        }

        @Override
        public Banner[] newArray(int size) {
            return new Banner[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(image);
        parcel.writeString(checkBanner);
    }

    public String getId() {
        return id;
    }

    public String getImage() {
        return image;
    }
}
