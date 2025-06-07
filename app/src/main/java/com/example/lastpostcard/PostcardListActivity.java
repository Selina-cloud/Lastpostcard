package com.example.lastpostcard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

public class PostcardListActivity extends AppCompatActivity {
    private static final String TAG = "PostcardListActivity";
    private RecyclerView recyclerView;
    private PostcardAdapter adapter;
    private PostcardDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postcard_list);
        Log.d(TAG, "onCreate");

        dbHelper = new PostcardDbHelper(this);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        setupAdapter();

        Button createNewButton = findViewById(R.id.createNewButton);
        createNewButton.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
        });
    }

    private void setupAdapter() {
        Cursor cursor = dbHelper.getAllPostcards();
        // 在PostcardListActivity中修改点击监听器
        adapter = new PostcardAdapter(this, cursor, new PostcardAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(long id) {
                Postcard postcard = dbHelper.getPostcardById(id);
                if (postcard != null) {
                    // 跳转到详情页
                    Intent intent = new Intent(PostcardListActivity.this, PostcardDetailActivity.class);
                    intent.putExtra("postcard", postcard);
                    startActivity(intent);
                }
            }

            @Override
            public void onDeleteClick(long id) {
                // 删除逻辑
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void refreshData() {
        Cursor newCursor = dbHelper.getAllPostcards();
        adapter.changeCursor(newCursor);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshData();
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}