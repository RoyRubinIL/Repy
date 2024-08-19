package com.example.repy.Views;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.repy.Models.Appeal;
import com.example.repy.Network.ChatGPTService;
import com.example.repy.R;
import com.example.repy.Utilities.DataManager;
import com.example.repy.Utilities.UserManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.tashila.pleasewait.PleaseWaitDialog;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AppealActivity extends AppCompatActivity {

    private Appeal newAppeal;
    private String appealID;
    private DataManager dataManager;
    private UserManager userManager;
    private MaterialTextView dateOfIssueTextView, ticketIdTextView, generateAppeal;
    private TextInputEditText appealReason;
    private ImageButton returnButton;
    private ChatGPTService chatApi;
    private PleaseWaitDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setupFullScreenMode();
        setContentView(R.layout.activity_appeal);

        dataManager = DataManager.getInstance();
        userManager = new UserManager();
        chatApi = new ChatGPTService(null, this);

        initializeViews();

        String ticketId = getIntent().getStringExtra("newTicketID");
        String ticketUid = getIntent().getStringExtra("newTicketUid");

        progressDialog = new PleaseWaitDialog(this);
        progressDialog.setMessage("Generating Appeal...");

        appealID = userManager.getCurrentUserUid()+ticketId;
        setupAppealDetails(ticketId);
        returnButton.setOnClickListener(v->{moveBackToTicketActivity();});
        generateAppeal.setOnClickListener(v->{createNewAppeal(ticketId, ticketUid);});
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
        dateOfIssueTextView = findViewById(R.id.appeal_LBL_dateOfIssue);
        ticketIdTextView = findViewById(R.id.appeal_LBL_ticketId);
        appealReason = findViewById(R.id.appeal_EDT_reason);
        generateAppeal = findViewById(R.id.appeal_LBL_generate);
        returnButton = findViewById(R.id.appeal_BTN_return);
    }


    private void setupAppealDetails(String ticketId) {
        dateOfIssueTextView.setText(new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
        ticketIdTextView.setText(ticketId);
    }

    private void createNewAppeal(String ticketId, String ticketUid){
        String appealRes = appealReason.getText().toString();

        if (appealRes.isEmpty()) {
            System.err.println("Error: Appeal reason cannot be empty.");
            return;
        }
        progressDialog.show();

        newAppeal = new Appeal(null, appealID, ticketId, appealRes);

        String appealUid = dataManager.saveNewAppeal(userManager.getCurrentUserUid(), newAppeal);
        newAppeal.setUid(appealUid);

        MakeAppealFile(newAppeal, ticketUid);

    }

    private void MakeAppealFile(Appeal newAppeal, String ticketUid) {

        chatApi.generateAppealDocument(newAppeal,ticketUid, new ChatGPTService.OnDocumentGeneratedListener() {
            @Override
            public void onSuccess(String pdfUrl) {
                // The PDF is now saved in Firebase Storage and the URL is saved in the database
                progressDialog.dismiss();
                newAppeal.setPdfUrl(pdfUrl);
                moveToDownloadAppealActivity(newAppeal.getUid());
            }

            @Override
            public void onFailure(Throwable t) {
                // Handle failure, maybe show an error message
                progressDialog.dismiss();
                t.printStackTrace();
                // Notify the user about the failure
            }
        });
    }



    private void moveToDownloadAppealActivity(String appealUid){
        Intent intent = new Intent(AppealActivity.this, DownloadAppealActivity.class);
        intent.putExtra("AppealUid", appealUid);
        startActivity(intent);
    }

    private void moveBackToTicketActivity(){
        Intent intent = new Intent(AppealActivity.this, TicketActivity.class);
        startActivity(intent);
    }
}
