package com.example.lastpostcard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

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

        // 初始化数据库帮助类
        dbHelper = new PostcardDbHelper(this);

        // 初始化RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 设置适配器
        setupAdapter();

        // 设置创建新明信片按钮的点击事件
        Button createNewButton = findViewById(R.id.createNewButton);
        createNewButton.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
        });
    }

    private void setupAdapter() {
        // 获取所有明信片数据
        Cursor cursor = dbHelper.getAllPostcards();

        // 创建适配器并设置点击监听器
        adapter = new PostcardAdapter(this, cursor, new PostcardAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(long id) {
                // 点击项事件处理
                handleItemClick(id);
            }

            @Override
            public void onDeleteClick(long id) {
                // 删除项事件处理
                handleDeleteClick(id);
            }
        });

        // 设置适配器
        recyclerView.setAdapter(adapter);
    }

    private void handleItemClick(long id) {
        // 从数据库获取指定ID的明信片
        Postcard postcard = dbHelper.getPostcardById(id);
        if (postcard != null) {
            // 跳转到详情页并传递明信片数据
            Intent intent = new Intent(PostcardListActivity.this, PostcardDetailActivity.class);
            intent.putExtra("postcard", postcard);
            startActivity(intent);
        } else {
            Toast.makeText(this, "明信片数据加载失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleDeleteClick(long id) {
        // 删除指定ID的明信片
        int deletedRows = dbHelper.deletePostcard(id);
        if (deletedRows > 0) {
            Toast.makeText(this, "明信片已删除", Toast.LENGTH_SHORT).show();
            // 刷新数据
            refreshData();
        } else {
            Toast.makeText(this, "删除失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void refreshData() {
        // 获取更新后的数据
        Cursor newCursor = dbHelper.getAllPostcards();
        // 更新适配器数据
        adapter.changeCursor(newCursor);
        // 通知数据变化
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 每次返回活动时刷新数据
        refreshData();
    }

    @Override
    protected void onDestroy() {
        // 关闭数据库连接
        dbHelper.close();
        // 关闭适配器中的Cursor
        if (adapter != null) {
            adapter.changeCursor(null);
        }
        super.onDestroy();
    }
}