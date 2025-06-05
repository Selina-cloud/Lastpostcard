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
    // 数据库信息
    private static final String DATABASE_NAME = "postcards.db";
    private static final int DATABASE_VERSION = 1;

    // 表名和列名
    public static final String TABLE_POSTCARDS = "postcards";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_POSTCODE = "postcode";
    public static final String COLUMN_LOCATION = "location";
    public static final String COLUMN_MESSAGE = "message";
    public static final String COLUMN_PERSON_IMAGE = "person_image";
    public static final String COLUMN_FOOD_IMAGE = "food_image";
    public static final String COLUMN_SCENERY_IMAGE = "scenery_image";
    public static final String COLUMN_FUNNY_IMAGE = "funny_image";

    // 创建表的SQL语句
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

    // 插入新明信片
    public long insertPostcard(Postcard postcard) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_POSTCODE, postcard.getPostcode());
        values.put(COLUMN_LOCATION, postcard.getLocation());
        values.put(COLUMN_MESSAGE, postcard.getMessage());

        // 处理图片数据
        Bitmap[] images = postcard.getImages();
        if (images != null) {
            if (images.length > 0 && images[0] != null) {
                values.put(COLUMN_PERSON_IMAGE, getBitmapAsByteArray(images[0]));
            }
            if (images.length > 1 && images[1] != null) {
                values.put(COLUMN_FOOD_IMAGE, getBitmapAsByteArray(images[1]));
            }
            if (images.length > 2 && images[2] != null) {
                values.put(COLUMN_SCENERY_IMAGE, getBitmapAsByteArray(images[2]));
            }
            if (images.length > 3 && images[3] != null) {
                values.put(COLUMN_FUNNY_IMAGE, getBitmapAsByteArray(images[3]));
            }
        }

        long id = db.insert(TABLE_POSTCARDS, null, values);
        db.close();
        return id;
    }

    // 根据ID获取明信片
    public Postcard getPostcardById(long id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_POSTCARDS,
                null,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)},
                null, null, null
        );

        if (cursor != null && cursor.moveToFirst()) {
            // 获取基本数据
            String postcode = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_POSTCODE));
            String location = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOCATION));
            String message = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MESSAGE));

            // 获取图片数据
            byte[] personImageBytes = cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_PERSON_IMAGE));
            byte[] foodImageBytes = cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_FOOD_IMAGE));
            byte[] sceneryImageBytes = cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_SCENERY_IMAGE));
            byte[] funnyImageBytes = cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_FUNNY_IMAGE));

            // 将byte数组转换为Bitmap
            Bitmap[] images = new Bitmap[4];
            if (personImageBytes != null && personImageBytes.length > 0) {
                images[0] = BitmapFactory.decodeByteArray(personImageBytes, 0, personImageBytes.length);
            }
            if (foodImageBytes != null && foodImageBytes.length > 0) {
                images[1] = BitmapFactory.decodeByteArray(foodImageBytes, 0, foodImageBytes.length);
            }
            if (sceneryImageBytes != null && sceneryImageBytes.length > 0) {
                images[2] = BitmapFactory.decodeByteArray(sceneryImageBytes, 0, sceneryImageBytes.length);
            }
            if (funnyImageBytes != null && funnyImageBytes.length > 0) {
                images[3] = BitmapFactory.decodeByteArray(funnyImageBytes, 0, funnyImageBytes.length);
            }

            // 创建并返回Postcard对象
            Postcard postcard = new Postcard(postcode, location, message, images);
            cursor.close();
            return postcard;
        }

        if (cursor != null) {
            cursor.close();
        }
        return null;
    }

    // 获取所有明信片
    public Cursor getAllPostcards() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(
                TABLE_POSTCARDS,
                new String[]{COLUMN_ID, COLUMN_POSTCODE, COLUMN_LOCATION, COLUMN_PERSON_IMAGE},
                null, null, null, null,
                COLUMN_ID + " DESC"
        );
    }

    // 删除明信片
    public int deletePostcard(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(
                TABLE_POSTCARDS,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
    }

    // 更新明信片
    public int updatePostcard(Postcard postcard) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_POSTCODE, postcard.getPostcode());
        values.put(COLUMN_LOCATION, postcard.getLocation());
        values.put(COLUMN_MESSAGE, postcard.getMessage());

        // 处理图片数据
        Bitmap[] images = postcard.getImages();
        if (images != null) {
            if (images.length > 0 && images[0] != null) {
                values.put(COLUMN_PERSON_IMAGE, getBitmapAsByteArray(images[0]));
            }
            if (images.length > 1 && images[1] != null) {
                values.put(COLUMN_FOOD_IMAGE, getBitmapAsByteArray(images[1]));
            }
            if (images.length > 2 && images[2] != null) {
                values.put(COLUMN_SCENERY_IMAGE, getBitmapAsByteArray(images[2]));
            }
            if (images.length > 3 && images[3] != null) {
                values.put(COLUMN_FUNNY_IMAGE, getBitmapAsByteArray(images[3]));
            }
        }

        return db.update(
                TABLE_POSTCARDS,
                values,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(postcard.getId())}
        );
    }

    // 将Bitmap转换为byte数组
    private byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        return outputStream.toByteArray();
    }
}