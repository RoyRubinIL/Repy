package com.example.repy.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.repy.Models.Appeal;
import com.example.repy.Models.AppealStatus;
import com.example.repy.R;
import com.example.repy.Utilities.DataManager;
import com.example.repy.Utilities.StorageManager;
import com.example.repy.Utilities.UserManager;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

public class ActiveAdapter extends RecyclerView.Adapter<ActiveAdapter.ViewHolder> {
    private Context context;
    private List<Appeal> data;
    private UserManager userManager;
    private StorageManager storageManager;

    public ActiveAdapter(Context context, List<Appeal> data) {
        this.data = data;
        this.context = context;
        this.userManager = new UserManager();
        this.storageManager = StorageManager.getInstance(context); // Initialize StorageManager
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageButton approveButton, declineButton, downloadButton;
        MaterialTextView appealText, date, tickedId;

        public ViewHolder(View view) {
            super(view);
            cardView = view.findViewById(R.id.horizontalAppeal_LAY_cardView);
            approveButton = view.findViewById(R.id.horizontalAppeal_BTN_approve);
            declineButton = view.findViewById(R.id.horizontalAppeal_BTN_decline);
            downloadButton = view.findViewById(R.id.horizontalAppeal_BTN_download);
            appealText = view.findViewById(R.id.horizontalAppeal_LBL_text);
            date = view.findViewById(R.id.horizontalAppeal_LBL_dateOfIssueHor);
            tickedId = view.findViewById(R.id.horizontalAppeal_LBL_ticketIdHor);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_appeal_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Appeal item = data.get(position);
        Log.d("MyAdapter", "Binding appeal at position " + position + ": " + item.getUid());

        String appealId = "Appeal " + (position + 1);
        holder.appealText.setText(appealId);
        holder.date.setText(item.getDate().toString());
        holder.tickedId.setText(item.getTicketId());

        holder.downloadButton.setOnClickListener(v -> {
            storageManager.downloadAppealFile(userManager.getCurrentUserUid(), item.getUid(), context, success -> {
                if (success) {
                    // Handle success (e.g., notify the user)
                    Log.d("ActiveAdapter", "Appeal downloaded successfully: " + item.getUid());
                } else {
                    // Handle failure (e.g., show an error message)
                    Log.e("ActiveAdapter", "Failed to download appeal: " + item.getUid());
                }
            });
        });

        holder.approveButton.setOnClickListener(v -> {
            DataManager.getInstance().updateAppealStatus(userManager.getCurrentUserUid(), item.getUid(), AppealStatus.APPROVED, success -> {
                if (success) {
                    item.setStatus(AppealStatus.APPROVED);
                    holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, android.R.color.holo_green_light));
                    removeItem(holder.getAdapterPosition());
                } else {
                    Log.e("ActiveAdapter", "Failed to update status to APPROVED on Firebase.");
                }
            });
        });

        holder.declineButton.setOnClickListener(v -> {
            DataManager.getInstance().updateAppealStatus(userManager.getCurrentUserUid(), item.getUid(), AppealStatus.REFUSED, success -> {
                if (success) {
                    item.setStatus(AppealStatus.REFUSED);
                    holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, android.R.color.holo_red_light));
                    removeItem(holder.getAdapterPosition());
                } else {
                    Log.e("ActiveAdapter", "Failed to update status to REFUSED on Firebase.");
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    private void removeItem(int position) {
        data.remove(position);
        notifyItemRemoved(position);
    }
}
