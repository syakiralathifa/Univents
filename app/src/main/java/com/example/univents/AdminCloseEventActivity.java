package com.example.univents;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.*;
import com.google.firebase.firestore.*;

import java.util.HashMap;
import java.util.Map;

public class AdminCloseEventActivity extends AppCompatActivity {

    TextView txtCloseEventTitle, txtCloseEventDesc;
    Button btnCloseEvent;

    FirebaseFirestore db;
    String eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_close_event);

        txtCloseEventTitle = findViewById(R.id.txtCloseEventTitle);
        txtCloseEventDesc = findViewById(R.id.txtCloseEventDesc);
        btnCloseEvent = findViewById(R.id.btnCloseEvent);

        db = FirebaseFirestore.getInstance();

        eventId = getIntent().getStringExtra("eventId");

        loadEventInfo(); // NEW

        btnCloseEvent.setOnClickListener(v -> closeEvent());
    }

    private void loadEventInfo() {
        db.collection("events")
                .document(eventId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        txtCloseEventTitle.setText(doc.getString("title"));
                        txtCloseEventDesc.setText(doc.getString("description"));
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private void closeEvent() {

        db.collection("events")
                .document(eventId)
                .update("status", "closed")
                .addOnSuccessListener(a -> processParticipants())
                .addOnFailureListener(e ->
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private void processParticipants() {

        db.collection("registrations")
                .whereEqualTo("eventId", eventId)
                .whereEqualTo("status", "accepted")
                .get()
                .addOnSuccessListener(query -> {

                    if (query.size() == 0) {
                        Toast.makeText(this,
                                "Event closed! (No accepted participants)",
                                Toast.LENGTH_LONG).show();
                        finish();
                        return;
                    }

                    for (DocumentSnapshot doc : query) {

                        String userId = doc.getString("userId");
                        String role = doc.getString("role");

                        Map<String, Object> exp = new HashMap<>();
                        exp.put("eventId", eventId);
                        exp.put("role", role);  // NEW
                        exp.put("timestamp", FieldValue.serverTimestamp());

                        db.collection("users")
                                .document(userId)
                                .collection("experiences")
                                .add(exp);
                    }

                    Toast.makeText(this,
                            "Event closed and experiences added!",
                            Toast.LENGTH_LONG).show();

                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}
