package com.example.univents;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdminEventAdapter extends RecyclerView.Adapter<AdminEventAdapter.EventViewHolder> {

    private Context context;
    private List<Event> eventList;
    private OnEventActionListener listener;

    // INTERFACE UNTUK ADMIN EVENT ACTIONS
    public interface OnEventActionListener {
        void onEdit(Event event);
        void onDelete(Event event);
        void onViewParticipants(Event event);
    }

    public AdminEventAdapter(Context context, List<Event> eventList, OnEventActionListener listener) {
        this.context = context;
        this.eventList = eventList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_event_admin, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);

        holder.tvEventTitle.setText(event.getTitle());
        holder.tvEventDate.setText(event.getDate());
        holder.tvParticipantsCount.setText("Participants: " + event.getParticipantsCount());

        holder.btnEdit.setOnClickListener(v -> listener.onEdit(event));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(event));
        holder.btnViewParticipants.setOnClickListener(v -> listener.onViewParticipants(event));
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {

        TextView tvEventTitle, tvEventDate, tvParticipantsCount;
        Button btnEdit, btnDelete, btnViewParticipants;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);

            tvEventTitle = itemView.findViewById(R.id.tvEventTitle);
            tvEventDate = itemView.findViewById(R.id.tvEventDate);
            tvParticipantsCount = itemView.findViewById(R.id.tvParticipantsCount);

            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnViewParticipants = itemView.findViewById(R.id.btnViewParticipants);
        }
    }
}
