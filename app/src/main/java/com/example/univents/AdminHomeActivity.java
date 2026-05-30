package com.example.univents;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;

public class AdminHomeActivity extends AppCompatActivity
        implements AdminEventAdapter.OnEventActionListener {

    // UI
    RecyclerView recyclerEvents;
    TextView txtTotalEvents, txtTotalUsers, txtPendingCount;
    ImageView btnAdminLogout;
    Button btnAddEvent, btnManageEvent;

    // Data
    List<Event> eventList;
    AdminEventAdapter adapter;

    // Firebase
    FirebaseFirestore db;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // ===== INIT UI =====
        txtTotalEvents = findViewById(R.id.txtTotalEvents);
        txtTotalUsers = findViewById(R.id.txtTotalUsers);
        txtPendingCount = findViewById(R.id.txtPendingCount);
        btnAdminLogout = findViewById(R.id.btnAdminLogout);

        btnAddEvent = findViewById(R.id.btnAddEvent);
        btnManageEvent = findViewById(R.id.btnManageEvent);

        recyclerEvents = findViewById(R.id.recyclerEvents);
        recyclerEvents.setLayoutManager(new LinearLayoutManager(this));

        eventList = new ArrayList<>();
        adapter = new AdminEventAdapter(this, eventList, this);
        recyclerEvents.setAdapter(adapter);

        // ===== ACTIONS =====
        btnAdminLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, UserLoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        btnAddEvent.setOnClickListener(v ->
                startActivity(new Intent(this, AdminAddEventActivity.class))
        );

        btnManageEvent.setOnClickListener(v ->
                startActivity(new Intent(this, AdminManageEventActivity.class))
        );

        // ===== LOAD DATA =====
        listenStatisticsRealtime(); // statistik realtime
    }

    // 🔥 INI KUNCI UTAMANYA
    @Override
    protected void onResume() {
        super.onResume();
        loadEventsOnce(); // selalu refresh saat balik ke halaman ini
    }

    // =============================
    // LOAD EVENTS (AMAN & STABIL)
    // =============================
    private void loadEventsOnce() {
        db.collection("events")
                .get()
                .addOnSuccessListener(snaps -> {

                    eventList.clear();

                    for (DocumentSnapshot d : snaps.getDocuments()) {
                        Event event = d.toObject(Event.class);
                        if (event != null) {
                            event.setEventId(d.getId());
                            eventList.add(event);
                        }
                    }

                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to load events", Toast.LENGTH_SHORT).show()
                );
    }

    // =============================
    // REALTIME STATISTICS
    // =============================
    private void listenStatisticsRealtime() {

        // TOTAL EVENTS
        db.collection("events")
                .addSnapshotListener((snap, e) -> {
                    if (snap != null) {
                        txtTotalEvents.setText(String.valueOf(snap.size()));
                    }
                });

        // TOTAL USERS
        db.collection("users")
                .addSnapshotListener((snap, e) -> {
                    if (snap != null) {
                        txtTotalUsers.setText(String.valueOf(snap.size()));
                    }
                });

        // PENDING REGISTRATIONS
        db.collection("registrations")
                .whereEqualTo("status", "pending")
                .addSnapshotListener((snap, e) -> {
                    if (snap != null) {
                        txtPendingCount.setText(String.valueOf(snap.size()));
                    }
                });
    }

    // =============================
    // ADAPTER CALLBACKS
    // =============================
    @Override
    public void onEdit(Event event) {
        Intent i = new Intent(this, AdminEditEventActivity.class);
        i.putExtra("eventId", event.getEventId());
        startActivity(i);
    }

    @Override
    public void onDelete(Event event) {
        db.collection("registrations")
                .whereEqualTo("eventId", event.getEventId())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    WriteBatch batch = db.batch();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        batch.delete(doc.getReference());
                    }
                    batch.commit().addOnSuccessListener(aVoid -> {
                        db.collection("events")
                                .document(event.getEventId())
                                .delete()
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(this, "Event deleted", Toast.LENGTH_SHORT).show();
                                    loadEventsOnce(); // refresh langsung
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(this, "Delete failed", Toast.LENGTH_SHORT).show()
                                );
                    }).addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to delete registrations", Toast.LENGTH_SHORT).show();
                    });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to find registrations to delete", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onViewParticipants(Event event) {
        Intent i = new Intent(this, AdminViewParticipantsActivity.class);
        i.putExtra("eventId", event.getEventId());
        startActivity(i);
    }
}
