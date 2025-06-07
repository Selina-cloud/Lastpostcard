package com.example.lastpostcard;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PostcardAdapter extends RecyclerView.Adapter<PostcardAdapter.PostcardViewHolder> {
    private static final String TAG = "PostcardAdapter";
    private final Context context;
    private Cursor cursor;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(long id);
        void onDeleteClick(long id);
    }

    public PostcardAdapter(Context context, Cursor cursor, OnItemClickListener listener) {
        this.context = context;
        this.cursor = cursor;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PostcardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_postcard, parent, false);
        return new PostcardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostcardViewHolder holder, int position) {
        if (!cursor.moveToPosition(position)) {
            Log.e(TAG, "Couldn't move cursor to position " + position);
            return;
        }

        try {
            // 获取数据
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(PostcardDbHelper.COLUMN_ID));
            String location = cursor.getString(cursor.getColumnIndexOrThrow(PostcardDbHelper.COLUMN_LOCATION));
            byte[] imageBytes = cursor.getBlob(cursor.getColumnIndexOrThrow(PostcardDbHelper.COLUMN_PERSON_IMAGE));

            // 设置位置文本
            holder.locationTextView.setText(location != null ? location : "未知地点");

            // 加载并显示缩略图（优化内存使用）
            loadThumbnail(holder.thumbnailImageView, imageBytes);

            // 设置点击事件
            holder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(id);
                }
            });

            // 设置删除按钮点击事件
            holder.deleteButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClick(id);
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Error binding view holder", e);
            holder.locationTextView.setText("数据加载错误");
            setPlaceholderImage(holder.thumbnailImageView);
        }
    }

    private void loadThumbnail(ImageView imageView, byte[] imageBytes) {
        if (imageBytes == null || imageBytes.length == 0) {
            setPlaceholderImage(imageView);
            return;
        }

        try {
            // 使用BitmapFactory.Options优化图片加载
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length, options);

            // 计算采样率
            options.inSampleSize = calculateInSampleSize(options, 100, 100);
            options.inJustDecodeBounds = false;

            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length, options);
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            } else {
                setPlaceholderImage(imageView);
            }
        } catch (OutOfMemoryError e) {
            Log.e(TAG, "Out of memory error loading thumbnail", e);
            setPlaceholderImage(imageView);
        } catch (Exception e) {
            Log.e(TAG, "Error loading thumbnail", e);
            setPlaceholderImage(imageView);
        }
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // 原始图片尺寸
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // 计算最大的inSampleSize值，保持宽高都大于请求尺寸
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    private void setPlaceholderImage(ImageView imageView) {
        imageView.setImageResource(R.drawable.ic_default_thumbnail);
    }

    @Override
    public int getItemCount() {
        return cursor == null ? 0 : cursor.getCount();
    }

    public void changeCursor(Cursor newCursor) {
        if (cursor != null) {
            cursor.close();
        }
        cursor = newCursor;
        notifyDataSetChanged();
    }

    static class PostcardViewHolder extends RecyclerView.ViewHolder {
        final TextView locationTextView;
        final ImageView thumbnailImageView;
        final ImageButton deleteButton;

        public PostcardViewHolder(@NonNull View itemView) {
            super(itemView);
            locationTextView = itemView.findViewById(R.id.locationTextView);
            thumbnailImageView = itemView.findViewById(R.id.thumbnailImageView);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        } finally {
            super.finalize();
        }
    }
}