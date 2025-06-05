package com.example.lastpostcard;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PostcardAdapter extends RecyclerView.Adapter<PostcardAdapter.PostcardViewHolder> {
    private Context context;
    private Cursor cursor;
    private PostcardDbHelper dbHelper;

    public PostcardAdapter(Context context, Cursor cursor, PostcardDbHelper dbHelper) {
        this.context = context;
        this.cursor = cursor;
        this.dbHelper = dbHelper;
    }

    public void changeCursor(Cursor newCursor) {
        if (cursor != null) {
            cursor.close();
        }
        cursor = newCursor;
        notifyDataSetChanged();
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
            return;
        }

        // Get data from cursor
        long id = cursor.getLong(cursor.getColumnIndexOrThrow(PostcardDbHelper.COLUMN_ID));
        String postcode = cursor.getString(cursor.getColumnIndexOrThrow(PostcardDbHelper.COLUMN_POSTCODE));
        String location = cursor.getString(cursor.getColumnIndexOrThrow(PostcardDbHelper.COLUMN_LOCATION));
        byte[] imageBytes = cursor.getBlob(cursor.getColumnIndexOrThrow(PostcardDbHelper.COLUMN_PERSON_IMAGE));

        // Set data to views
        holder.locationTextView.setText(location);
        holder.postcodeTextView.setText(postcode);

        // Display thumbnail image
        if (imageBytes != null && imageBytes.length > 0) {
            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2; // Downsample image
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length, options);
                if (bitmap != null) {
                    holder.thumbnailImageView.setImageBitmap(bitmap);
                } else {
                    setPlaceholderImage(holder);
                }
            } catch (Exception e) {
                setPlaceholderImage(holder);
            }
        } else {
            setPlaceholderImage(holder);
        }

        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            Postcard postcard = dbHelper.getPostcardById(id);
            if (postcard != null) {
                Intent intent = new Intent(context, PostcardDetailActivity.class);
                intent.putExtra("postcard", postcard);
                context.startActivity(intent);
            }
        });
    }

    private void setPlaceholderImage(PostcardViewHolder holder) {
        holder.thumbnailImageView.setImageResource(android.R.drawable.ic_menu_gallery);
    }

    @Override
    public int getItemCount() {
        return cursor == null ? 0 : cursor.getCount();
    }

    static class PostcardViewHolder extends RecyclerView.ViewHolder {
        TextView locationTextView;
        TextView postcodeTextView;
        ImageView thumbnailImageView;

        public PostcardViewHolder(@NonNull View itemView) {
            super(itemView);
            locationTextView = itemView.findViewById(R.id.locationTextView);
            postcodeTextView = itemView.findViewById(R.id.postcodeTextView);
            thumbnailImageView = itemView.findViewById(R.id.thumbnailImageView);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            if (cursor != null) {
                cursor.close();
            }
        } finally {
            super.finalize();
        }
    }
}