package com.example.repy.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import android.widget.ImageButton;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private Context context;
    private List<Appeal> data;
    private StorageManager storageManager;
    private UserManager userManager;

    public HistoryAdapter(Context context, List<Appeal> data) {
        this.data = data;
        this.context = context;
        this.userManager = new UserManager();
        this.storageManager = StorageManager.getInstance(context);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageButton approveButton, declineButton, downloadButton;
        MaterialTextView appealText, date, tickedId, status;

        public ViewHolder(View view) {
            super(view);
            cardView = view.findViewById(R.id.horizontalAppeal_LAY_cardView);
            approveButton = view.findViewById(R.id.horizontalAppeal_BTN_approve);
            declineButton = view.findViewById(R.id.horizontalAppeal_BTN_decline);
            downloadButton = view.findViewById(R.id.horizontalAppeal_BTN_download);
            appealText = view.findViewById(R.id.horizontalAppeal_LBL_text);
            date = view.findViewById(R.id.horizontalAppeal_LBL_dateOfIssueHor);
            tickedId = view.findViewById(R.id.horizontalAppeal_LBL_ticketIdHor);
            status = view.findViewById(R.id.horizontalAppeal_LBL_statusText);
        }
    }

    @Override
    public HistoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_appeal_item, parent, false);
        return new HistoryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final HistoryAdapter.ViewHolder holder, int position) {
        Appeal item = data.get(position);
        Log.d("HistoryAdapter", "Binding appeal at position " + position + ": " + item.getUid());

        String appealId = "Appeal " + (position + 1);
        holder.appealText.setText(appealId);
        holder.date.setText(item.getDate().toString());
        holder.tickedId.setText(item.getTicketId());

        // Update the status text and color based on the appeal status
        if (item.getStatus() == AppealStatus.APPROVED) {
            holder.status.setText("APPROVED");
            holder.status.setTextColor(ContextCompat.getColor(context, android.R.color.holo_green_dark));
        } else if (item.getStatus() == AppealStatus.REFUSED) {
            holder.status.setText("REFUSED");
            holder.status.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark));
        }

        holder.downloadButton.setOnClickListener(v -> {
            storageManager.downloadAppealFile(userManager.getCurrentUserUid(),item.getUid(), context, success -> {
                if (success) {
                    // Handle success (e.g., notify the user)
                    Log.d("ActiveAdapter", "Appeal downloaded successfully: " + item.getUid());
                } else {
                    // Handle failure (e.g., show an error message)
                    Log.e("ActiveAdapter", "Failed to download appeal: " + item.getUid());
                }
            });
        });

        // Hide the approve and decline buttons since this is a history view
        holder.approveButton.setVisibility(View.GONE);
        holder.declineButton.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
