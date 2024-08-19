package com.example.repy.Views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.repy.Adapters.ActiveAdapter;
import com.example.repy.Adapters.HistoryAdapter;
import com.example.repy.R;
import com.example.repy.Utilities.DataManager;
import com.example.repy.Utilities.SlideOutItemAnimator;
import com.example.repy.Utilities.UserManager;

public class AppealsHistoryActivity extends AppCompatActivity {
    private ImageButton returnButton;
    private DataManager dataManager;
    private UserManager userManager;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupFullScreenMode();
        setContentView(R.layout.activity_appeals_history);

        initializeViews();

        dataManager = DataManager.getInstance();
        userManager = new UserManager();
        // Fetch history appeals and set them in the adapter
        fetchHistoryAppeals(recyclerView);
        returnButton.setOnClickListener(v->{returnToMainManu();});

    }

    private void setupFullScreenMode() {
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
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.historyAppeal_RV_appeals);
        returnButton = findViewById(R.id.historyAppeal_BTN_return);
    }


    private void fetchHistoryAppeals(RecyclerView recyclerView) {
        String userId = userManager.getCurrentUserUid();
        if (userId != null) {
            dataManager.getUserHistoryAppeals(userId, historyAppeals -> {
                if (historyAppeals != null && !historyAppeals.isEmpty()) {
                    HistoryAdapter adapter = new HistoryAdapter(AppealsHistoryActivity.this,historyAppeals);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
                    linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
                    recyclerView.setLayoutManager(linearLayoutManager);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setItemAnimator(new SlideOutItemAnimator(this));
                } else {
                    Log.d("AppealsHistoryActivity", "No History appeals found for the user.");
                }
            });
        } else {
            Log.e("AppealsHistoryActivity", "User ID is null, cannot fetch active appeals.");
        }
    }

    private void returnToMainManu() {
        Intent intent = new Intent(AppealsHistoryActivity.this, MenuActivity.class);
        startActivity(intent);
        finish();
    }
}