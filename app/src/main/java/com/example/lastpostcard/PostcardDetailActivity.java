package com.example.lastpostcard;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class PostcardDetailActivity extends AppCompatActivity {
    private static final String TAG = "PostcardDetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postcard_detail);
        Log.d(TAG, "onCreate started");

        // 1. 获取并验证传递过来的Postcard对象
        Postcard postcard = getIntent().getParcelableExtra("postcard");
        if (postcard == null) {
            Log.e(TAG, "No postcard data received");
            showErrorMessage("无法加载明信片数据");
            finish();
            return;
        }

        Log.d(TAG, "Displaying postcard - Location: " + postcard.getLocation() +
                ", Postcode: " + postcard.getPostcode());

       // 初始化视图
        initViews();

        //  加载并显示数据
        displayPostcard(postcard);

        // 设置返回按钮
        setupBackButton();
    }

    private void initViews() {
    }

    private void displayPostcard(Postcard postcard) {
        // 显示邮政编码
        displayPostcode(postcard.getPostcode());

        // 显示地点和留言
        displayTextContent(postcard.getLocation(), postcard.getMessage());

        // 显示图片
        displayImages(postcard.getImages());
    }

    private void displayPostcode(String postcode) {
        if (postcode == null || postcode.length() != 6) {
            Log.w(TAG, "Invalid postcode: " + postcode);
            return;
        }

        TextView[] postcodeViews = {
                findViewById(R.id.detailPostcode1),
                findViewById(R.id.detailPostcode2),
                findViewById(R.id.detailPostcode3),
                findViewById(R.id.detailPostcode4),
                findViewById(R.id.detailPostcode5),
                findViewById(R.id.detailPostcode6)
        };

        for (int i = 0; i < 6; i++) {
            try {
                postcodeViews[i].setText(String.valueOf(postcode.charAt(i)));
            } catch (Exception e) {
                Log.e(TAG, "Error setting postcode digit", e);
            }
        }
    }

    private void displayTextContent(String location, String message) {
        try {
            TextView locationView = findViewById(R.id.detailLocationTextView);
            TextView messageView = findViewById(R.id.detailMessageTextView);

            locationView.setText(location != null ? location : "未填写地点");
            messageView.setText(message != null ? message : "未填写留言");
        } catch (Exception e) {
            Log.e(TAG, "Error displaying text content", e);
        }
    }

    private void displayImages(Bitmap[] images) {
        if (images == null || images.length < 4) {
            Log.w(TAG, "Invalid images array");
            return;
        }

        try {
            ImageView personImage = findViewById(R.id.detailPersonImage);
            ImageView foodImage = findViewById(R.id.detailFoodImage);
            ImageView sceneryImage = findViewById(R.id.detailSceneryImage);
            ImageView funnyImage = findViewById(R.id.detailFunnyImage);

            // 设置图片，如果为null则保持默认图片
            personImage.setImageBitmap(images[0]);
            foodImage.setImageBitmap(images[1]);
            sceneryImage.setImageBitmap(images[2]);
            funnyImage.setImageBitmap(images[3]);
        } catch (Exception e) {
            Log.e(TAG, "Error displaying images", e);
        }
    }

    private void setupBackButton() {
        try {
            Button backButton = findViewById(R.id.backButton);
            backButton.setOnClickListener(v -> finish());
        } catch (Exception e) {
            Log.e(TAG, "Error setting up back button", e);
        }
    }

    private void showErrorMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }
}