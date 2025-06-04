package com.example.lastpostcard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class PostcardListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private PostcardAdapter adapter;
    private PostcardDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postcard_list);

        // Initialize database helper
        dbHelper = new PostcardDbHelper(this);

        // Set up RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load postcards from database
        loadPostcards();

        // Set up create new postcard button
        Button createNewButton = findViewById(R.id.createNewButton);
        createNewButton.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the list when returning to this activity
        loadPostcards();
    }

    private void loadPostcards() {
        // Get all postcards from database
        Cursor cursor = dbHelper.getAllPostcards();

        // Initialize or update adapter
        if (adapter == null) {
            adapter = new PostcardAdapter(this, cursor);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.changeCursor(cursor);
        }
    }

    @Override
    protected void onDestroy() {
        // Close database helper when activity is destroyed
        dbHelper.close();
        super.onDestroy();
    }
}