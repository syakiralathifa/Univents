package com.example.univents;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ParticipantAdapter
        extends RecyclerView.Adapter<ParticipantAdapter.ViewHolder> {

    private Context context;
    private List<RegistrationModel> list;

    public ParticipantAdapter(Context context, List<RegistrationModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.item_participant, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {

        RegistrationModel m = list.get(position);

        h.txtName.setText(
                m.getUserName() != null ? m.getUserName() : "No Name"
        );

        h.txtEmail.setText(
                m.getUserEmail() != null ? m.getUserEmail() : "No Email"
        );

        String role = m.getRole() == null ? "" : m.getRole().toLowerCase();

        if (role.equals("committee")) {
            h.txtRole.setText("COMMITTEE");
            h.txtRole.setBackgroundResource(R.drawable.badge_role_committee);
        } else {
            h.txtRole.setText("PARTICIPANT");
            h.txtRole.setBackgroundResource(R.drawable.badge_role_participant);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtName, txtEmail, txtRole;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtName = itemView.findViewById(R.id.txtName);
            txtEmail = itemView.findViewById(R.id.txtEmail);
            txtRole = itemView.findViewById(R.id.txtRole);
        }
    }
}
