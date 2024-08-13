package com.example.repy.Utilities;

import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import android.content.Context;


import com.bumptech.glide.Glide;
import com.example.repy.Models.Appeal;
import com.example.repy.Models.User;
import com.example.repy.R;
import com.example.repy.Views.LoginActivity;
import com.example.repy.Views.SignupActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class DataManager {

    private static final String TAG = "DataManager";

    private static DataManager instance;
    private final FirebaseDatabase database;
    private final DatabaseReference usersRef;


    private DataManager() {
        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("users");
    }

    public static synchronized DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }

    public void storeUserData(User user, String uid, CallBack<Boolean> callback){
        if (uid != null) {
            usersRef.child(uid).setValue(user).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    callback.res(true);
                } else {
                    callback.res(false);
                }
            });
        } else {
            callback.res(false);
        }
    }

    public void getUserData(String uid, CallBack<User> callback) {
        if (uid != null) {
            usersRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        callback.res(user);
                    } else {
                        callback.res(null);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "Error fetching user data", databaseError.toException());
                    callback.res(null);
                }
            });
        } else {
            callback.res(null);
        }
    }

    public void loadProfileImage(Context context, String imageUrl, ImageView imageView) {
        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.img_default_profile_image)
                .error(R.drawable.img_default_profile_image)
                .into(imageView);
    }


    public interface CallBack<T> {
        void res(T res);
    }

}
