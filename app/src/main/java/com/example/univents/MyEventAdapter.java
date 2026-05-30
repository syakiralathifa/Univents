package com.example.univents;

import android.content.Context;
import android.view.*;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyEventAdapter extends RecyclerView.Adapter<MyEventAdapter.ViewHolder> {

    Context context;
    ArrayList<MyEventModel> list;

    public MyEventAdapter(Context context, ArrayList<MyEventModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_my_event, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MyEventModel event = list.get(position);

        holder.txtMyEventTitle.setText(event.title);
        holder.txtMyEventDate.setText(event.date);
        holder.txtMyEventStatus.setText("Status: " + event.status);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtMyEventTitle, txtMyEventDate, txtMyEventStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtMyEventTitle = itemView.findViewById(R.id.txtMyEventTitle);
            txtMyEventDate = itemView.findViewById(R.id.txtMyEventDate);
            txtMyEventStatus = itemView.findViewById(R.id.txtMyEventStatus);
        }
    }
}
