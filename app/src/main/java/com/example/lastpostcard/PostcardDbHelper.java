package com.example.lastpostcard;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

public class PostcardDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "postcards.db";
    private static final int DATABASE_VERSION = 2;

    public static final String TABLE_POSTCARDS = "postcards";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_POSTCODE = "postcode";
    public static final String COLUMN_LOCATION = "location";
    public static final String COLUMN_MESSAGE = "message";
    public static final String COLUMN_PERSON_IMAGE = "person_image";
    public static final String COLUMN_FOOD_IMAGE = "food_image";
    public static final String COLUMN_SCENERY_IMAGE = "scenery_image";
    public static final String COLUMN_FUNNY_IMAGE = "funny_image";

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_POSTCARDS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_POSTCODE + " TEXT NOT NULL, " +
                    COLUMN_LOCATION + " TEXT NOT NULL, " +
                    COLUMN_MESSAGE + " TEXT, " +
                    COLUMN_PERSON_IMAGE + " BLOB, " +
                    COLUMN_FOOD_IMAGE + " BLOB, " +
                    COLUMN_SCENERY_IMAGE + " BLOB, " +
                    COLUMN_FUNNY_IMAGE + " BLOB);";

    public PostcardDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_POSTCARDS);
        onCreate(db);
    }

    public long insertPostcard(Postcard postcard) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_POSTCODE, postcard.getPostcode());
        values.put(COLUMN_LOCATION, postcard.getLocation());
        values.put(COLUMN_MESSAGE, postcard.getMessage());

        Bitmap[] images = postcard.getImages();
        if (images != null) {
            if (images.length > 0) values.put(COLUMN_PERSON_IMAGE, bitmapToBytes(images[0]));
            if (images.length > 1) values.put(COLUMN_FOOD_IMAGE, bitmapToBytes(images[1]));
            if (images.length > 2) values.put(COLUMN_SCENERY_IMAGE, bitmapToBytes(images[2]));
            if (images.length > 3) values.put(COLUMN_FUNNY_IMAGE, bitmapToBytes(images[3]));
        }

        long id = db.insert(TABLE_POSTCARDS, null, values);
        db.close();
        return id;
    }

    public Postcard getPostcardById(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_POSTCARDS,
                null,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)},
                null, null, null
        );

        if (cursor != null && cursor.moveToFirst()) {
            Postcard postcard = new Postcard(
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_POSTCODE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOCATION)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MESSAGE)),
                    new Bitmap[]{
                            bytesToBitmap(cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_PERSON_IMAGE))),
                            bytesToBitmap(cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_FOOD_IMAGE))),
                            bytesToBitmap(cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_SCENERY_IMAGE))),
                            bytesToBitmap(cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_FUNNY_IMAGE)))
                    }
            );
            postcard.setId(id);
            cursor.close();
            return postcard;
        }
        return null;
    }

    public Cursor getAllPostcards() {
        return getReadableDatabase().query(
                TABLE_POSTCARDS,
                new String[]{COLUMN_ID, COLUMN_LOCATION, COLUMN_PERSON_IMAGE},
                null, null, null, null,
                COLUMN_ID + " DESC"
        );
    }

    public int updatePostcard(Postcard postcard) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_POSTCODE, postcard.getPostcode());
        values.put(COLUMN_LOCATION, postcard.getLocation());
        values.put(COLUMN_MESSAGE, postcard.getMessage());

        Bitmap[] images = postcard.getImages();
        if (images != null) {
            if (images.length > 0) values.put(COLUMN_PERSON_IMAGE, bitmapToBytes(images[0]));
            if (images.length > 1) values.put(COLUMN_FOOD_IMAGE, bitmapToBytes(images[1]));
            if (images.length > 2) values.put(COLUMN_SCENERY_IMAGE, bitmapToBytes(images[2]));
            if (images.length > 3) values.put(COLUMN_FUNNY_IMAGE, bitmapToBytes(images[3]));
        }

        return db.update(
                TABLE_POSTCARDS,
                values,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(postcard.getId())}
        );
    }

    public int deletePostcard(long id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(
                TABLE_POSTCARDS,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
    }

    private byte[] bitmapToBytes(Bitmap bitmap) {
        if (bitmap == null) return null;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    private Bitmap bytesToBitmap(byte[] bytes) {
        if (bytes == null) return null;
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}