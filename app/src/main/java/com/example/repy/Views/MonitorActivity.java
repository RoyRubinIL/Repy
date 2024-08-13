package com.example.repy.Views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.repy.Adapters.MyAdapter;
import com.example.repy.R;
import com.example.repy.Utilities.SlideOutItemAnimator;

public class MonitorActivity extends AppCompatActivity {

    private ImageButton returnButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        );
        setContentView(R.layout.activity_monitor);

//        RecyclerView recyclerView = findViewById(R.id.main_active_appels);
//        MyAdapter adapter = new MyAdapter(this, data);
//        recyclerView.setAdapter(adapter);
//        recyclerView.setItemAnimator(new SlideOutItemAnimator(this));

        returnButton = findViewById(R.id.button_return_monitor);

        returnButton.setOnClickListener(v->{returnToMainManu();});

    }

    private void returnToMainManu() {
        Intent intent = new Intent(MonitorActivity.this, MenuActivity.class);
        startActivity(intent);
        finish();
    }
}