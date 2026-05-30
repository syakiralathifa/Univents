package com.example.univents;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EventDetailActivity extends AppCompatActivity {

    TextView txtTitleDetail, txtDateDetail, txtDescriptionDetail, txtDivisionLabel;
    Spinner spinnerRole, spinnerDivision;
    Button btnRegisterEvent;

    FirebaseFirestore db;
    FirebaseAuth auth;

    String eventId;

    private final String[] roles = new String[] { "Participant", "Committee" };
    private final String[] divisions = new String[] {
            "Select division",
            "Sponsorship",
            "Event Organizer",
            "Documentation",
            "Logistics",
            "Public Relations",
            "Security",
            "Decoration",
            "MC / Host"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        eventId = getIntent().getStringExtra("eventId");

        txtTitleDetail = findViewById(R.id.txtTitleDetail);
        txtDateDetail = findViewById(R.id.txtDateDetail);
        txtDescriptionDetail = findViewById(R.id.txtDescriptionDetail);
        txtDivisionLabel = findViewById(R.id.txtDivisionLabel);
        spinnerRole = findViewById(R.id.spinnerRole);
        spinnerDivision = findViewById(R.id.spinnerDivision);
        btnRegisterEvent = findViewById(R.id.btnRegisterEvent);

        setupRoleSpinner();
        setupDivisionSpinner();
        loadEventDetails();

        btnRegisterEvent.setOnClickListener(v -> registerUser());
    }

    private void setupRoleSpinner() {
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(adapter);

        spinnerRole.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String role = roles[position];
                if (role.equalsIgnoreCase("committee")) {
                    txtDivisionLabel.setVisibility(View.VISIBLE);
                    spinnerDivision.setVisibility(View.VISIBLE);
                } else {
                    txtDivisionLabel.setVisibility(View.GONE);
                    spinnerDivision.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    private void setupDivisionSpinner() {
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, divisions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDivision.setAdapter(adapter);
    }

    private void loadEventDetails() {
        db.collection("events")
                .document(eventId)
                .get()
                .addOnSuccessListener(d -> {
                    if (d != null && d.exists()) {
                        txtTitleDetail.setText(d.getString("title"));
                        txtDateDetail.setText(d.getString("date"));
                        txtDescriptionDetail.setText(d.getString("description"));
                    }
                });
    }

    private void registerUser() {
        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "Please login first.", Toast.LENGTH_SHORT).show();
            return;
        }

        final String role = spinnerRole.getSelectedItem().toString();
        final String divisionFinal;

        if (role.equalsIgnoreCase("committee")) {
            String d = spinnerDivision.getSelectedItem() != null
                    ? spinnerDivision.getSelectedItem().toString()
                    : null;

            if (d == null || d.equalsIgnoreCase("select division")) {
                Toast.makeText(this, "Please choose a division first.", Toast.LENGTH_SHORT).show();
                return;
            }

            divisionFinal = d;
        } else {
            divisionFinal = null;
        }

        String userId = auth.getUid();

        // Cek apakah user sudah pernah register di event ini
        db.collection("registrations")
                .whereEqualTo("eventId", eventId)
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(q -> {
                    if (!q.isEmpty()) {
                        Toast.makeText(this, "You already registered for this event.", Toast.LENGTH_SHORT).show();
                    } else {
                        createRegistration(role, divisionFinal, userId);

                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private void createRegistration(String role, String division, String userId) {
        Map<String, Object> reg = new HashMap<>();
        reg.put("eventId", eventId);
        reg.put("userId", userId);
        reg.put("role", role);
        reg.put("timestamp", FieldValue.serverTimestamp());

        if (role.equalsIgnoreCase("participant")) {
            reg.put("status", "accepted");
        } else {
            reg.put("status", "pending");
        }

        if (division != null) {
            reg.put("division", division);
        }

        db.collection("registrations")
                .add(reg)
                .addOnSuccessListener(unused -> {
                    if (role.equalsIgnoreCase("participant")) {
                        incrementParticipants();
                    }
                    Toast.makeText(this, "Registered as " + role, Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private void incrementParticipants() {
        db.collection("events")
                .document(eventId)
                .update("participantsCount", FieldValue.increment(1));
    }
}