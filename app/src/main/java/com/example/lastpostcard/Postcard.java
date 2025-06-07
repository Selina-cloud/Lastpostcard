package com.example.lastpostcard;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.ByteArrayOutputStream;

public class Postcard implements Parcelable {
    private String postcode;
    private String location;
    private String message;
    private Bitmap[] images;
    private long id; // 数据库ID

    public Postcard(String postcode, String location, String message, Bitmap[] images) {
        this.postcode = postcode;
        this.location = location;
        this.message = message;
        this.images = images != null ? images : new Bitmap[4];
    }

    public long getId() { return id; }
    public String getPostcode() { return postcode; }
    public String getLocation() { return location; }
    public String getMessage() { return message; }
    public Bitmap[] getImages() { return images; }

    public void setId(long id) { this.id = id; }
    public void setPostcode(String postcode) { this.postcode = postcode; }
    public void setLocation(String location) { this.location = location; }
    public void setMessage(String message) { this.message = message; }
    public void setImages(Bitmap[] images) { this.images = images; }

    // Parcelable实现
    protected Postcard(Parcel in) {
        id = in.readLong();
        postcode = in.readString();
        location = in.readString();
        message = in.readString();

        // 读取图片数组
        int imageCount = in.readInt();
        images = new Bitmap[imageCount];
        for (int i = 0; i < imageCount; i++) {
            byte[] byteArray = in.createByteArray();
            if (byteArray != null && byteArray.length > 0) {
                images[i] = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            }
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(postcode);
        dest.writeString(location);
        dest.writeString(message);

        // 写入图片数组
        dest.writeInt(images != null ? images.length : 0);
        if (images != null) {
            for (Bitmap bitmap : images) {
                if (bitmap != null) {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    dest.writeByteArray(stream.toByteArray());
                } else {
                    dest.writeByteArray(null);
                }
            }
        }
    }

    @Override
    public int describeContents() {
        return 0;
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

    // 辅助方法
    public Bitmap getPersonImage() {
        return images != null && images.length > 0 ? images[0] : null;
    }

    public Bitmap getFoodImage() {
        return images != null && images.length > 1 ? images[1] : null;
    }

    public Bitmap getSceneryImage() {
        return images != null && images.length > 2 ? images[2] : null;
    }

    public Bitmap getFunnyImage() {
        return images != null && images.length > 3 ? images[3] : null;
    }

    public void setPersonImage(Bitmap bitmap) {
        if (images == null) images = new Bitmap[4];
        images[0] = bitmap;
    }

    public void setFoodImage(Bitmap bitmap) {
        if (images == null) images = new Bitmap[4];
        images[1] = bitmap;
    }

    public void setSceneryImage(Bitmap bitmap) {
        if (images == null) images = new Bitmap[4];
        images[2] = bitmap;
    }

    public void setFunnyImage(Bitmap bitmap) {
        if (images == null) images = new Bitmap[4];
        images[3] = bitmap;
    }
}
