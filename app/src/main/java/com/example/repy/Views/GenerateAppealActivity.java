package com.example.repy.Views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.repy.Models.TicketType;
import com.example.repy.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;

public class GenerateAppealActivity extends AppCompatActivity {

    private ShapeableImageView muniButton;
    private ShapeableImageView policeButton;
    private LinearLayout ticketChoiceLayout;
    private MaterialButton manualTyping;
//    private MaterialButton scanTicket;
    private TicketType selectedTicketType;  // To store the selected TicketType

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
        setContentView(R.layout.activity_generate_appeal);

        muniButton = findViewById(R.id.muniButton);
        policeButton = findViewById(R.id.policeButton);
        ticketChoiceLayout = findViewById(R.id.ticket_choice_LAY);
        manualTyping = findViewById(R.id.manual_typing);
//        scanTicket = findViewById(R.id.scan_ticket);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ticketChoiceLayout.setVisibility(View.VISIBLE);
                resetButtons();
                v.setBackgroundResource(R.drawable.button_settings_selected);

                if (v == muniButton) {
                    selectedTicketType = TicketType.MUNICIPALITY;
                } else if (v == policeButton) {
                    selectedTicketType = TicketType.POLICE;
                }
            }
        };

        muniButton.setOnClickListener(listener);
        policeButton.setOnClickListener(listener);


        manualTyping.setOnClickListener(v->{moveToTicketActivity(selectedTicketType);});

        //scanTicket.setOnClickListener(v -> {moveToScanActivity(selectedTicketType);});
    }

    private void resetButtons() {
        muniButton.setBackgroundResource(R.drawable.button_selector);
        policeButton.setBackgroundResource(R.drawable.button_selector);
    }

    private void moveToTicketActivity(TicketType ticketType) {
        Intent intent = new Intent(GenerateAppealActivity.this, TicketActivity.class);
        intent.putExtra("ticketType", ticketType);
        startActivity(intent);
    }

//    private void moveToScanActivity(TicketType ticketType) {
//        Intent intent = new Intent(GenerateAppealActivity.this, ScanActivity.class);
//        intent.putExtra("ticketType", ticketType);
//        startActivity(intent);
//    }
}
