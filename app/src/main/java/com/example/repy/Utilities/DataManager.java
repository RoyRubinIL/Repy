package com.example.repy.Utilities;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.repy.Models.Appeal;
import com.example.repy.Models.AppealStatus;
import com.example.repy.Models.Ticket;
import com.example.repy.Models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DataManager {

    private static final String TAG = "DataManager";

    private static DataManager instance;
    private final FirebaseDatabase database;
    private final DatabaseReference usersRef;
    private final DatabaseReference ticketsRef;
    private final DatabaseReference appealsRef;

    private DataManager() {
        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("users");
        ticketsRef = database.getReference("tickets");
        appealsRef = database.getReference("appeals");
    }

    public static synchronized DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }

    public void storeUserData(User user, String uid, CallBack<Boolean> callback) {
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

    public String saveNewTicket(String userId, Ticket ticket) {
        if (userId != null) {
            String ticketUid = ticketsRef.push().getKey();
            ticket.setUid(ticketUid);
            ticketsRef.child(userId).child(ticketUid).setValue(ticket).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Ticket saved successfully.");
                } else {
                    Log.e(TAG, "Failed to save ticket.", task.getException());
                }
            });
            return ticketUid;
        } else {
            Log.e(TAG, "User ID is null. Cannot save ticket.");
            return null;
        }
    }

    public void getTicketByTicketUid(String currentUserUid, String ticketUid, CallBack<Ticket> callback) {
        ticketsRef.child(currentUserUid).child(ticketUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Ticket ticket = dataSnapshot.getValue(Ticket.class);
                callback.res(ticket);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Error fetching ticket details", databaseError.toException());
                callback.res(null);
            }
        });
    }

    public String saveNewAppeal(String userId, Appeal appeal) {
        if (userId != null) {
            String appealUid = appealsRef.push().getKey();
            appeal.setUid(appealUid);
            appealsRef.child(userId).child(appealUid).setValue(appeal).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Appeal saved successfully.");
                } else {
                    Log.e(TAG, "Failed to save Appeal.", task.getException());
                }
            });
            return appealUid;
        } else {
            Log.e(TAG, "User ID is null. Cannot save ticket.");
            return null;
        }
    }

    public void getUserActiveAppeals(String userUid, CallBack<List<Appeal>> callback) {
        Query query = appealsRef.child(userUid).orderByChild("status").equalTo("ACTIVE");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Appeal> activeAppeals = new ArrayList<>();
                Log.d(TAG, "Snapshot Children Count: " + dataSnapshot.getChildrenCount());
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Appeal appeal = snapshot.getValue(Appeal.class);
                    if (appeal != null) {
                        Log.d(TAG, "Appeal ID: " + appeal.getUid() + ", Status: " + appeal.getStatus());
                        activeAppeals.add(appeal);
                    }
                }
                Log.d(TAG, "Size of active appeals: " + activeAppeals.size());
                callback.res(activeAppeals);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Error fetching active appeals", databaseError.toException());
                callback.res(null);
            }
        });
    }

    public void getUserHistoryAppeals(String userUid, CallBack<List<Appeal>> callback) {
        Query query = appealsRef.child(userUid).orderByChild("status");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Appeal> historyAppeals = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Appeal appeal = snapshot.getValue(Appeal.class);
                    if (appeal != null && (appeal.getStatus() == AppealStatus.APPROVED || appeal.getStatus() == AppealStatus.REFUSED)) {
                        historyAppeals.add(appeal);
                    }
                }
                callback.res(historyAppeals);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Error fetching history appeals", databaseError.toException());
                callback.res(null);
            }
        });
    }

    public void updateAppealStatus(String userUid, String appealUid, AppealStatus status, CallBack<Boolean> callback) {
        if (userUid != null && appealUid != null) {
            appealsRef.child(userUid).child(appealUid).child("status").setValue(status.name()).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Appeal status updated successfully to " + status.name());
                    callback.res(true);
                } else {
                    Log.e(TAG, "Failed to update appeal status.", task.getException());
                    callback.res(false);
                }
            });
        } else {
            Log.e(TAG, "User UID or Appeal UID is null. Cannot update status.");
            callback.res(false);
        }
    }

    public void updateAppealPdfUrl(String userUid, String appealUid, String pdfUrl, CallBack<Boolean> callback) {
        if (userUid != null && appealUid != null) {
            appealsRef.child(userUid).child(appealUid).child("pdfUrl").setValue(pdfUrl).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d(TAG, "PDF URL updated successfully in the database.");
                    callback.res(true);
                } else {
                    Log.e(TAG, "Failed to update PDF URL in the database.", task.getException());
                    callback.res(false);
                }
            });
        } else {
            Log.e(TAG, "User UID or Appeal UID is null. Cannot update PDF URL.");
            callback.res(false);
        }
    }

    public void getUserImage(String userId, LoadImageCallBack loadImageCallBack) {
        usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                if (user != null) {
                    loadImageCallBack.OnLoadImage(user.getProfileImage());
                } else {
                    loadImageCallBack.OnLoadImage(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error fetching user image", error.toException());
            }
        });
    }

    public void updateUserImage(String uid, String imageUrl) {
        if (uid != null && imageUrl != null) {
            usersRef.child(uid).child("profileImage").setValue(imageUrl).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d(TAG, "User image updated successfully.");
                } else {
                    Log.e(TAG, "Failed to update user image.", task.getException());
                }
            });
        } else {
            Log.e(TAG, "UID or Image URL is null. Cannot update user image.");
        }
    }

    public interface CallBack<T> {
        void res(T res);
    }

    public interface LoadImageCallBack {
        void OnLoadImage(String imageUrl);
    }
}
