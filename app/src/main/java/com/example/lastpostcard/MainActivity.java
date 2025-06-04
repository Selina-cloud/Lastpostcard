package com.example.lastpostcard;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "lastPostCard";
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int SELECT_LOCATION_REQUEST = 2;

    private ImageView currentSelectedImageView;
    private EditText[] postcodeEditTexts = new EditText[6];
    private EditText locationEditText;
    private EditText messageEditText;
    private PostcardDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "Activity created");

        // Initialize database helper
        dbHelper = new PostcardDbHelper(this);

        // Initialize postcode EditTexts
        postcodeEditTexts[0] = findViewById(R.id.postcode1);
        postcodeEditTexts[1] = findViewById(R.id.postcode2);
        postcodeEditTexts[2] = findViewById(R.id.postcode3);
        postcodeEditTexts[3] = findViewById(R.id.postcode4);
        postcodeEditTexts[4] = findViewById(R.id.postcode5);
        postcodeEditTexts[5] = findViewById(R.id.postcode6);

        locationEditText = findViewById(R.id.locationEditText);
        messageEditText = findViewById(R.id.messageEditText);

        // Set up image selection buttons
        setupImageSelectionButtons();

        // Set up location selection button
        Button selectLocationButton = findViewById(R.id.selectLocationButton);
        selectLocationButton.setOnClickListener(v -> {
            Log.d(TAG, "Location selection button clicked");
            openPostcodeMap();
        });

        // Set up save button
        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(v -> {
            Log.d(TAG, "Save button clicked");
            savePostcard();
        });
    }

    private void setupImageSelectionButtons() {
        Button selectPersonButton = findViewById(R.id.selectPersonButton);
        Button selectFoodButton = findViewById(R.id.selectFoodButton);
        Button selectSceneryButton = findViewById(R.id.selectSceneryButton);
        Button selectFunnyButton = findViewById(R.id.selectFunnyButton);

        ImageView personImage = findViewById(R.id.personImage);
        ImageView foodImage = findViewById(R.id.foodImage);
        ImageView sceneryImage = findViewById(R.id.sceneryImage);
        ImageView funnyImage = findViewById(R.id.funnyImage);

        selectPersonButton.setOnClickListener(v -> {
            currentSelectedImageView = personImage;
            openImageChooser();
        });

        selectFoodButton.setOnClickListener(v -> {
            currentSelectedImageView = foodImage;
            openImageChooser();
        });

        selectSceneryButton.setOnClickListener(v -> {
            currentSelectedImageView = sceneryImage;
            openImageChooser();
        });

        selectFunnyButton.setOnClickListener(v -> {
            currentSelectedImageView = funnyImage;
            openImageChooser();
        });
    }

    private void openImageChooser() {
        Log.d(TAG, "Opening image chooser");
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "选择图片"), PICK_IMAGE_REQUEST);
    }

    private void openPostcodeMap() {
        Log.d(TAG, "Opening postcode map");
        String url = "https://map.360.cn/zt/postcode.html";
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivityForResult(intent, SELECT_LOCATION_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                currentSelectedImageView.setImageBitmap(bitmap);
                Log.d(TAG, "Image selected and set successfully");
            } catch (IOException e) {
                Log.e(TAG, "Error loading image: " + e.getMessage());
                Toast.makeText(this, "加载图片失败", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == SELECT_LOCATION_REQUEST && resultCode == RESULT_OK && data != null) {
            // Handle location selection from map
            Log.d(TAG, "Location selected from map");
            Toast.makeText(this, "地点已选择", Toast.LENGTH_SHORT).show();
        }
    }

    private void savePostcard() {
        // Validate postcode
        boolean postcodeComplete = true;
        StringBuilder postcodeBuilder = new StringBuilder();

        for (EditText editText : postcodeEditTexts) {
            if (editText.getText().toString().isEmpty()) {
                postcodeComplete = false;
                break;
            }
            postcodeBuilder.append(editText.getText().toString());
        }

        if (!postcodeComplete) {
            Toast.makeText(this, "请输入完整的邮政编码", Toast.LENGTH_SHORT).show();
            Log.w(TAG, "Postcode is incomplete");
            return;
        }

        String postcode = postcodeBuilder.toString();
        String location = locationEditText.getText().toString();
        String message = messageEditText.getText().toString();

        // Validate location and message
        if (location.isEmpty()) {
            Toast.makeText(this, "请输入地点", Toast.LENGTH_SHORT).show();
            Log.w(TAG, "Location is empty");
            return;
        }

        if (message.isEmpty()) {
            Toast.makeText(this, "请输入留言", Toast.LENGTH_SHORT).show();
            Log.w(TAG, "Message is empty");
            return;
        }

        // Get images from ImageViews
        Bitmap[] images = new Bitmap[4];
        images[0] = getBitmapFromImageView(R.id.personImage);
        images[1] = getBitmapFromImageView(R.id.foodImage);
        images[2] = getBitmapFromImageView(R.id.sceneryImage);
        images[3] = getBitmapFromImageView(R.id.funnyImage);

        // Create postcard object
        Postcard postcard = new Postcard(postcode, location, message, images);

        // Save to database
        long id = dbHelper.insertPostcard(postcard);
        if (id != -1) {
            Log.i(TAG, "Postcard saved - ID: " + id);
            Toast.makeText(this, "明信片已保存", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, PostcardListActivity.class));
            finish();
        } else {
            Log.e(TAG, "Failed to save postcard");
            Toast.makeText(this, "保存失败", Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap getBitmapFromImageView(int imageViewId) {
        ImageView imageView = findViewById(imageViewId);
        if (imageView.getDrawable() != null) {
            return ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        }
        return null;
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}