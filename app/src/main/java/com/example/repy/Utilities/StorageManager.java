package com.example.repy.Utilities;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.repy.Models.Appeal;
import com.example.repy.Network.ChatGPTService;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class StorageManager {

    private static final String TAG = "StorageManager";

    private static StorageManager instance;
    private final FirebaseStorage storage;
    private final StorageReference imageRef;
    private final StorageReference pdfRef;

    private StorageManager(Context context) {
        this.storage = FirebaseStorage.getInstance();
        this.imageRef = storage.getReference("Users_Profile_Images");
        this.pdfRef = storage.getReference("Users_Appeal_pdf");
    }

    public static synchronized StorageManager getInstance(Context context) {
        if (instance == null) {
            instance = new StorageManager(context);
        }
        return instance;
    }

    // Uploads a profile image to Firebase Storage and updates the profile image URL
    public void uploadProfileImage(String uid, Uri uri, DataManager.CallBack<String> callback) {
        StorageReference fileReference = imageRef.child(uid + ".jpg");
        UploadTask uploadTask = fileReference.putFile(uri);

        uploadTask.addOnSuccessListener(taskSnapshot -> {
            fileReference.getDownloadUrl().addOnSuccessListener(uri1 -> {
                String imageUrl = uri1.toString();
                callback.res(imageUrl);
            }).addOnFailureListener(e -> {
                Log.e(TAG, "Failed to retrieve download URL", e);
                callback.res(null);
            });
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Failed to upload image", e);
            callback.res(null);
        });
    }



    // Uploads a PDF file to Firebase Storage and updates the appeal with the file URL
    public void uploadPdfToFirebase(String userUid, String pdfPath, Appeal appeal, ChatGPTService.OnDocumentGeneratedListener listener) {
        StorageReference userPdfRef = this.pdfRef.child(userUid).child(appeal.getUid() + ".pdf");

        Uri fileUri = Uri.fromFile(new File(pdfPath));
        UploadTask uploadTask = userPdfRef.putFile(fileUri);

        uploadTask.addOnSuccessListener(taskSnapshot -> {
            userPdfRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                String downloadUrl = downloadUri.toString();
                appeal.setPdfUrl(downloadUrl);

                // Update the appeal in the database
                DataManager.getInstance().updateAppealPdfUrl(userUid, appeal.getUid(), downloadUrl, success -> {
                    if (success) {
                        listener.onSuccess(downloadUrl);
                    } else {
                        listener.onFailure(new Exception("Failed to update PDF URL in the database."));
                    }
                });
            }).addOnFailureListener(listener::onFailure);
        }).addOnFailureListener(listener::onFailure);
    }

    public void downloadAppealFile(String userUid, String appealUid, Context context, DataManager.CallBack<Boolean> callback) {
        StorageReference appealRef = pdfRef.child(userUid).child(appealUid + ".pdf");

        // Define the path Documents/Reply/AppealsPdf
        File appealsDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "Repy/AppealsPdf");
        if (!appealsDir.exists()) {
            appealsDir.mkdirs();
        }

        // Create the file in the specified directory
        File localFile = new File(appealsDir, "appeal_" + appealUid + ".pdf");

        appealRef.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
            // File downloaded successfully
            Toast.makeText(context, "Appeal downloaded successfully!", Toast.LENGTH_SHORT).show();
            callback.res(true);
        }).addOnFailureListener(e -> {
            // Handle any errors
            Toast.makeText(context, "Failed to download appeal: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            callback.res(false);
        });
    }



}
