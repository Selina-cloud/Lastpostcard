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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int SELECT_LOCATION_REQUEST = 2;

    private ImageView currentSelectedImageView;
    private EditText[] postcodeEditTexts = new EditText[6];
    private EditText locationEditText;
    private EditText messageEditText;
    private PostcardDbHelper dbHelper;
    private Postcard currentPostcard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate");

        dbHelper = new PostcardDbHelper(this);

        // 初始化UI组件
        initViews();

        // 检查是否有传递过来的明信片数据
        if (getIntent().hasExtra("postcard")) {
            currentPostcard = getIntent().getParcelableExtra("postcard");
            if (currentPostcard != null) {
                Log.d(TAG, "Loading existing postcard ID: " + currentPostcard.getId());
                loadPostcardData(currentPostcard);
            }
        }

        setupImageSelectionButtons();
    }

    private void initViews() {
        postcodeEditTexts[0] = findViewById(R.id.postcode1);
        postcodeEditTexts[1] = findViewById(R.id.postcode2);
        postcodeEditTexts[2] = findViewById(R.id.postcode3);
        postcodeEditTexts[3] = findViewById(R.id.postcode4);
        postcodeEditTexts[4] = findViewById(R.id.postcode5);
        postcodeEditTexts[5] = findViewById(R.id.postcode6);

        locationEditText = findViewById(R.id.locationEditText);
        messageEditText = findViewById(R.id.messageEditText);

        Button selectLocationButton = findViewById(R.id.selectLocationButton);
        selectLocationButton.setOnClickListener(v -> openPostcodeMap());

        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(v -> savePostcard());
    }

    private void loadPostcardData(Postcard postcard) {
        // 加载邮政编码
        String postcode = postcard.getPostcode();
        if (postcode != null && postcode.length() == 6) {
            for (int i = 0; i < 6; i++) {
                postcodeEditTexts[i].setText(String.valueOf(postcode.charAt(i)));
            }
        }

        // 加载地点和留言
        locationEditText.setText(postcard.getLocation());
        messageEditText.setText(postcard.getMessage());

        // 加载图片
        Bitmap[] images = postcard.getImages();
        if (images != null) {
            ImageView personImage = findViewById(R.id.personImage);
            ImageView foodImage = findViewById(R.id.foodImage);
            ImageView sceneryImage = findViewById(R.id.sceneryImage);
            ImageView funnyImage = findViewById(R.id.funnyImage);

            if (images.length > 0 && images[0] != null) {
                personImage.setImageBitmap(images[0]);
            }
            if (images.length > 1 && images[1] != null) {
                foodImage.setImageBitmap(images[1]);
            }
            if (images.length > 2 && images[2] != null) {
                sceneryImage.setImageBitmap(images[2]);
            }
            if (images.length > 3 && images[3] != null) {
                funnyImage.setImageBitmap(images[3]);
            }
        }
    }

    private void setupImageSelectionButtons() {
        findViewById(R.id.selectPersonButton).setOnClickListener(v -> {
            currentSelectedImageView = findViewById(R.id.personImage);
            openImageChooser();
        });
        findViewById(R.id.selectFoodButton).setOnClickListener(v -> {
            currentSelectedImageView = findViewById(R.id.foodImage);
            openImageChooser();
        });
        findViewById(R.id.selectSceneryButton).setOnClickListener(v -> {
            currentSelectedImageView = findViewById(R.id.sceneryImage);
            openImageChooser();
        });
        findViewById(R.id.selectFunnyButton).setOnClickListener(v -> {
            currentSelectedImageView = findViewById(R.id.funnyImage);
            openImageChooser();
        });
    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "选择图片"), PICK_IMAGE_REQUEST);
    }

    private void openPostcodeMap() {
        String url = "https://map.360.cn/zt/postcode.html";
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                currentSelectedImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                Toast.makeText(this, "图片加载失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void savePostcard() {
        // 验证邮政编码
        StringBuilder postcodeBuilder = new StringBuilder();
        for (EditText editText : postcodeEditTexts) {
            String digit = editText.getText().toString();
            if (digit.isEmpty()) {
                Toast.makeText(this, "请输入完整的邮政编码", Toast.LENGTH_SHORT).show();
                return;
            }
            postcodeBuilder.append(digit);
        }

        String location = locationEditText.getText().toString();
        if (location.isEmpty()) {
            Toast.makeText(this, "请输入地点", Toast.LENGTH_SHORT).show();
            return;
        }

        String message = messageEditText.getText().toString();
        if (message.isEmpty()) {
            Toast.makeText(this, "请输入留言", Toast.LENGTH_SHORT).show();
            return;
        }

        // 获取图片
        Bitmap[] images = new Bitmap[4];
        images[0] = getBitmapFromImageView(R.id.personImage);
        images[1] = getBitmapFromImageView(R.id.foodImage);
        images[2] = getBitmapFromImageView(R.id.sceneryImage);
        images[3] = getBitmapFromImageView(R.id.funnyImage);

        // 保存或更新
        if (currentPostcard == null) {
            currentPostcard = new Postcard(postcodeBuilder.toString(), location, message, images);
            long id = dbHelper.insertPostcard(currentPostcard);
            if (id != -1) {
                currentPostcard.setId(id);
                Toast.makeText(this, "明信片已保存", Toast.LENGTH_SHORT).show();
            }
        } else {
            currentPostcard.setPostcode(postcodeBuilder.toString());
            currentPostcard.setLocation(location);
            currentPostcard.setMessage(message);
            currentPostcard.setImages(images);
            dbHelper.updatePostcard(currentPostcard);
            Toast.makeText(this, "明信片已更新", Toast.LENGTH_SHORT).show();
        }

        startActivity(new Intent(this, PostcardListActivity.class));
        finish();
    }

    private Bitmap getBitmapFromImageView(int imageViewId) {
        ImageView imageView = findViewById(imageViewId);
        return imageView.getDrawable() != null ?
                ((BitmapDrawable) imageView.getDrawable()).getBitmap() : null;
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}