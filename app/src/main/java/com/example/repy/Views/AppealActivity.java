package com.example.repy.Views;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.example.repy.Models.Appeal;
import com.example.repy.R;
import com.example.repy.Utilities.DataManager;
import com.example.repy.Utilities.UserManager;
import com.google.android.material.textview.MaterialTextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AppealActivity extends AppCompatActivity {

    private Appeal newAppeal;
    private String appealID;
    private DataManager dataManager;
    private UserManager userManager;
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
        setContentView(R.layout.activity_appeal);

        dataManager = DataManager.getInstance();
        userManager = new UserManager();

        String ticketId = retrieveTicketIdFromFirebase();
        setupAppealDetails(ticketId);
    }

    private String retrieveTicketIdFromFirebase() {

        return "SampleTicketId";
    }

    private void setupAppealDetails(String ticketId) {
        appealID = appealID+ userManager.getCurrentUserId();

        MaterialTextView dateOfIssueTextView = findViewById(R.id.date_of_issue_appeal);
        dateOfIssueTextView.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

        MaterialTextView ticketIdTextView = findViewById(R.id.ticket_id_appeal);
        ticketIdTextView.setText(ticketId);



        //newAppeal = new Appeal(appealID, ticketId, );
    }
}
