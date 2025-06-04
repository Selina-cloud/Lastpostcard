package com.example.lastpostcard;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
public class Postcard implements Parcelable {
    private String postcode;
    private String location;
    private String message;
    private Bitmap[] images; // 0:人物, 1:美食, 2:景观, 3:趣事

    public Postcard(String postcode, String location, String message, Bitmap[] images) {
        this.postcode = postcode;
        this.location = location;
        this.message = message;
        this.images = images;
    }

    // Getters
    public String getPostcode() { return postcode; }
    public String getLocation() { return location; }
    public String getMessage() { return message; }
    public Bitmap[] getImages() { return images; }
    protected Postcard(Parcel in) {
        postcode
                = in.readString();
        location
                = in.readString();
        message
                = in.readString();
        // Bitmap 也需要特殊处理
        images
                = new Bitmap[4];
        in
                .readTypedArray(images, Bitmap.CREATOR);
    }

    public static final Creator<Postcard> CREATOR = new Creator<Postcard>() {
        @Override
        public Postcard createFromParcel(Parcel in) {
            return new Postcard(in);
        }

        @Override
        public Postcard[] newArray(int size) {
            return new Postcard[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest
                .writeString(postcode);
        dest
                .writeString(location);
        dest
                .writeString(message);
        dest
                .writeTypedArray(images, flags);
    }
}
