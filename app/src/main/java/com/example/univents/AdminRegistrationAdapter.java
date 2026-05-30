package com.example.univents;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class AdminRegistrationAdapter
        extends RecyclerView.Adapter<AdminRegistrationAdapter.ViewHolder> {

    private Context context;
    private List<RegistrationModel> list;
    private FirebaseFirestore db;

    public AdminRegistrationAdapter(Context context, List<RegistrationModel> list) {
        this.context = context;
        this.list = list;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.item_registration, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        RegistrationModel m = list.get(position);

        h.txtUserName.setText(m.getUserName() != null ? m.getUserName() : "No Name");
        h.txtUserEmail.setText(m.getUserEmail() != null ? m.getUserEmail() : "No Email");
        h.txtRegStatus.setText("Status: " + (m.getStatus() != null ? m.getStatus() : "-"));
        h.txtRegRole.setText("Role: " + (m.getRole() != null ? m.getRole() : "-"));

        String division = m.getDivision() == null ? "" : m.getDivision().trim();
        h.txtRegDivision.setText(
                division.isEmpty() ? "Division: -" : "Division: " + division
        );

        String eventId = m.getEventId();
        if (eventId != null && !eventId.isEmpty()) {
            db.collection("events")
                    .document(eventId)
                    .get()
                    .addOnSuccessListener(eventDoc -> {
                        String title = eventDoc.getString("title");
                        h.txtEventTitle.setText("Event: " + (title != null ? title : "-"));
                    });
        } else {
            h.txtEventTitle.setText("Event: -");
        }

        String status = m.getStatus() == null ? "" : m.getStatus().trim();
        if (status.equalsIgnoreCase("pending")) {
            h.btnAccept.setVisibility(View.VISIBLE);
            h.btnReject.setVisibility(View.VISIBLE);
        } else {
            h.btnAccept.setVisibility(View.GONE);
            h.btnReject.setVisibility(View.GONE);
        }

        h.btnAccept.setOnClickListener(v -> updateStatus(h, m, "accepted"));
        h.btnReject.setOnClickListener(v -> updateStatus(h, m, "rejected"));

        // ================== FIX UTAMA ==================
        h.itemView.setOnClickListener(v -> {

            String targetUserId = m.getUserId();

            if (targetUserId == null || targetUserId.isEmpty()) {
                Toast.makeText(context,
                        "User ID not found, cannot view profile.",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            // 🔥 LOG DEBUG (WAJIB ADA)
            Log.d("ADMIN_CLICK", "OPEN USER PROFILE ID = " + targetUserId);

            // 🔥 BUKA ACTIVITY KHUSUS ADMIN
            Intent intent = new Intent(context, AdminViewUserProfileActivity.class);
            intent.putExtra("userId", targetUserId);
            context.startActivity(intent);
        });
        // ===============================================
    }


    // Helper method untuk update status dan menonaktifkan tombol
    private void updateStatus(ViewHolder holder, RegistrationModel model, String newStatus) {
        holder.btnAccept.setEnabled(false);
        holder.btnReject.setEnabled(false);

        db.collection("registrations").document(model.getRegId()).update("status", newStatus)
                .addOnSuccessListener(unused -> {
                    if (newStatus.equalsIgnoreCase("accepted")) {
                        incrementParticipants(model.getEventId());
                    }
                    Toast.makeText(context, "Registration " + newStatus, Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    holder.btnAccept.setEnabled(true);
                    holder.btnReject.setEnabled(true);
                    Toast.makeText(context, "Failed to " + newStatus + " registration", Toast.LENGTH_SHORT).show();
                });
    }

    private void incrementParticipants(String eventId) {
        db.collection("events")
                .document(eventId)
                .update("participantsCount", FieldValue.increment(1));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtUserName, txtUserEmail, txtRegStatus, txtRegRole, txtEventTitle, txtRegDivision;
        Button btnAccept, btnReject;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtUserName = itemView.findViewById(R.id.txtUserName);
            txtUserEmail = itemView.findViewById(R.id.txtUserEmail);
            txtRegStatus = itemView.findViewById(R.id.txtRegStatus);
            txtRegRole = itemView.findViewById(R.id.txtRegRole);
            txtEventTitle = itemView.findViewById(R.id.txtEventTitle);
            txtRegDivision = itemView.findViewById(R.id.txtRegDivision);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnReject = itemView.findViewById(R.id.btnReject);
        }
    }
}
