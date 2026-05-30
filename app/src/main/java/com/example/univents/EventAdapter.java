package com.example.univents;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.MyViewHolder> {

    private Context context;
    private List<Event> list;

    public EventAdapter(Context context, List<Event> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_event, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Event event = list.get(position);

        holder.txtTitle.setText(event.getTitle());
        holder.txtDate.setText(event.getDate());

        // BADGE — sementara static
        holder.txtBadge.setText("Open");

        holder.itemView.setOnClickListener(v -> {
            Intent i = new Intent(context, EventDetailActivity.class);
            i.putExtra("eventId", event.getEventId());
            context.startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    // =============================
    // 🔥 INI YANG KURANG SEBELUMNYA
    // =============================
    public void updateList(ArrayList<Event> newList) {
        list.clear();
        list.addAll(newList);
        notifyDataSetChanged();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtTitle, txtDate, txtBadge;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtBadge = itemView.findViewById(R.id.txtBadge);
        }
    }
}
