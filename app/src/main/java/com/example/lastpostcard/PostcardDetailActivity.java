package com.example.lastpostcard;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class PostcardDetailActivity extends AppCompatActivity {

    // 视图组件
    private TextView[] postcodeTextViews = new TextView[6];
    private TextView locationTextView;
    private TextView messageTextView;
    private ImageView personImageView;
    private ImageView foodImageView;
    private ImageView sceneryImageView;
    private ImageView funnyImageView;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postcard_detail);

        // 初始化视图
        initViews();

        // 从Intent获取Postcard对象
        Postcard postcard = getIntent().getParcelableExtra("postcard");
        if (postcard != null) {
            displayPostcardDetails(postcard);
        } else {
            // 如果没有获取到明信片数据，显示错误信息并返回
            Toast.makeText(this, "无法加载明信片数据", Toast.LENGTH_SHORT).show();
            finish();
        }

        // 设置返回按钮点击事件
        backButton.setOnClickListener(v -> finish());
    }

    private void initViews() {
        // 初始化邮政编码TextView数组
        postcodeTextViews[0] = findViewById(R.id.detailPostcode1);
        postcodeTextViews[1] = findViewById(R.id.detailPostcode2);
        postcodeTextViews[2] = findViewById(R.id.detailPostcode3);
        postcodeTextViews[3] = findViewById(R.id.detailPostcode4);
        postcodeTextViews[4] = findViewById(R.id.detailPostcode5);
        postcodeTextViews[5] = findViewById(R.id.detailPostcode6);

        // 初始化其他视图
        locationTextView = findViewById(R.id.detailLocationTextView);
        messageTextView = findViewById(R.id.detailMessageTextView);
        personImageView = findViewById(R.id.detailPersonImage);
        foodImageView = findViewById(R.id.detailFoodImage);
        sceneryImageView = findViewById(R.id.detailSceneryImage);
        funnyImageView = findViewById(R.id.detailFunnyImage);
        backButton = findViewById(R.id.backButton);
    }

    private void displayPostcardDetails(Postcard postcard) {
        // 显示邮政编码（每位数字分开显示）
        String postcode = postcard.getPostcode();
        if (postcode != null && postcode.length() == 6) {
            for (int i = 0; i < 6; i++) {
                postcodeTextViews[i].setText(String.valueOf(postcode.charAt(i)));
            }
        }

        // 显示地点和留言
        locationTextView.setText(postcard.getLocation());
        messageTextView.setText(postcard.getMessage());

        // 显示图片（如果有）
        Bitmap[] images = postcard.getImages();
        if (images != null) {
            if (images.length > 0 && images[0] != null) {
                personImageView.setImageBitmap(images[0]);
            } else {
                personImageView.setImageResource(R.drawable.default_person); // 设置默认图片
            }

            if (images.length > 1 && images[1] != null) {
                foodImageView.setImageBitmap(images[1]);
            } else {
                foodImageView.setImageResource(R.drawable.default_food); // 设置默认图片
            }

            if (images.length > 2 && images[2] != null) {
                sceneryImageView.setImageBitmap(images[2]);
            } else {
                sceneryImageView.setImageResource(R.drawable.default_scenery); // 设置默认图片
            }

            if (images.length > 3 && images[3] != null) {
                funnyImageView.setImageBitmap(images[3]);
            } else {
                funnyImageView.setImageResource(R.drawable.default_funny); // 设置默认图片
            }
        } else {
            // 如果images数组为null，设置所有图片为默认图片
            personImageView.setImageResource(R.drawable.default_person);
            foodImageView.setImageResource(R.drawable.default_food);
            sceneryImageView.setImageResource(R.drawable.default_scenery);
            funnyImageView.setImageResource(R.drawable.default_funny);
        }
    }
}