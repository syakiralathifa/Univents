package com.example.univents;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ExperienceAdapter extends RecyclerView.Adapter<ExperienceAdapter.ViewHolder> {

    Context context;
    ArrayList<ExperienceModel> list;

    public ExperienceAdapter(Context context, ArrayList<ExperienceModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_experience, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ExperienceModel exp = list.get(position);

        // Set title
        holder.txtExperienceTitle.setText(exp.title);

        // Set date (aman dari null / kosong)
        if (exp.date == null || exp.date.trim().isEmpty()) {
            holder.txtExperienceDate.setText("Completed on: -");
        } else {
            holder.txtExperienceDate.setText("Completed on: " + exp.date);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtExperienceTitle, txtExperienceDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtExperienceTitle = itemView.findViewById(R.id.txtExperienceTitle);
            txtExperienceDate  = itemView.findViewById(R.id.txtExperienceDate);
        }
    }
}
