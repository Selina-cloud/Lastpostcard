package com.example.lastpostcard;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class PostcardDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postcard_detail);

        // 获取传递过来的Postcard对象
        Postcard postcard = getIntent().getParcelableExtra("postcard");
        if (postcard == null) {
            finish();
            return;
        }

        // 初始化视图
        initViews();

        // 加载数据
        displayPostcard(postcard);

        // 返回按钮点击事件
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());
    }

    private void initViews() {
        // 初始化所有视图
        // 这里不需要具体实现，因为我们在displayPostcard中直接查找视图
    }

    private void displayPostcard(Postcard postcard) {
        // 显示邮政编码
        String postcode = postcard.getPostcode();
        if (postcode != null && postcode.length() == 6) {
            TextView[] postcodeViews = {
                    findViewById(R.id.detailPostcode1),
                    findViewById(R.id.detailPostcode2),
                    findViewById(R.id.detailPostcode3),
                    findViewById(R.id.detailPostcode4),
                    findViewById(R.id.detailPostcode5),
                    findViewById(R.id.detailPostcode6)
            };
            for (int i = 0; i < 6; i++) {
                postcodeViews[i].setText(String.valueOf(postcode.charAt(i)));
            }
        }

        // 显示地点和留言
        ((TextView)findViewById(R.id.detailLocationTextView)).setText(postcard.getLocation());
        ((TextView)findViewById(R.id.detailMessageTextView)).setText(postcard.getMessage());

        // 显示图片
        Bitmap[] images = postcard.getImages();
        if (images != null) {
            ImageView personImage = findViewById(R.id.detailPersonImage);
            ImageView foodImage = findViewById(R.id.detailFoodImage);
            ImageView sceneryImage = findViewById(R.id.detailSceneryImage);
            ImageView funnyImage = findViewById(R.id.detailFunnyImage);

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
}