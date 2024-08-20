package com.example.repy.Utilities;

import android.app.Activity;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserManager {
    private FirebaseAuth mAuth;

    public UserManager() {
        mAuth = FirebaseAuth.getInstance();
    }
    public boolean isUserLoggedIn() {
        return mAuth.getCurrentUser() != null;
    }

    public void logOutUser() {
        mAuth.signOut();
    }

    public void createUser(String email, String password, Activity activity, OnUserCreationListener listener) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(activity, task -> {
            if (task.isSuccessful()) {
                String uid = task.getResult().getUser().getUid();
                listener.onUserCreated(uid);
            } else {
                Toast.makeText(activity, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                listener.onFailure(task.getException());
            }
        });
    }

    public void loginUser(String email, String password, Activity activity, OnLoginListener listener) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, task -> {
                    if (task.isSuccessful()) {
                        String uid = mAuth.getCurrentUser().getUid();
                        listener.onLoginSuccess(uid);
                    } else {
                        listener.onLoginFailure(task.getException());
                    }
                });
    }

    public String getCurrentUserUid() {
        if (mAuth.getCurrentUser() != null) {
            return mAuth.getCurrentUser().getUid();
        }
        return null;
    }

    public void updateUserPassword(String newPassword, OnPasswordUpdateListener listener) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.updatePassword(newPassword).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    listener.onPasswordUpdateSuccess();
                } else {
                    listener.onPasswordUpdateFailure(task.getException());
                }
            });
        } else {
            listener.onPasswordUpdateFailure(new Exception("User not logged in"));
        }
    }

    public interface OnPasswordUpdateListener {
        void onPasswordUpdateSuccess();
        void onPasswordUpdateFailure(Exception exception);
    }


    public FirebaseUser getCurrentUser(){
        return mAuth.getCurrentUser();
    }

    public interface OnLoginListener {
        void onLoginSuccess(String uid);

        void onLoginFailure(Exception e);
    }

    public interface OnUserCreationListener {
        void onUserCreated(String uid);

        void onFailure(Exception exception);
    }
}
