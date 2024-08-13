package com.example.repy.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.repy.Models.Appeal;
import com.example.repy.R;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private Context context;
    private List<Appeal> data;

    public MyAdapter(Context context, List<Appeal> data) {
        this.context = context;
        this.data = data;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageButton approveButton;
        ImageButton declineButton;

        public ViewHolder(View view) {
            super(view);
            cardView = view.findViewById(R.id.card_view);
            approveButton = view.findViewById(R.id.approve_button);
            declineButton = view.findViewById(R.id.decline_button);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_appeal_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Appeal item = data.get(position);

        holder.approveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, android.R.color.holo_green_light));
                removeItem(holder.getAdapterPosition());
            }
        });

        holder.declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, android.R.color.holo_red_light));
                removeItem(holder.getAdapterPosition());
            }
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
