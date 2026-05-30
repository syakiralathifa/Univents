package com.example.univents;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdminViewParticipantsActivity extends AppCompatActivity {

    RecyclerView recyclerParticipants;
    View progressBar, tvNoParticipants;

    FirebaseFirestore db;
    List<RegistrationModel> list;
    ParticipantAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view_participants);

        recyclerParticipants = findViewById(R.id.recyclerParticipants);
        progressBar = findViewById(R.id.progressBar);
        tvNoParticipants = findViewById(R.id.tvNoParticipants);

        recyclerParticipants.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        adapter = new ParticipantAdapter(this, list);
        recyclerParticipants.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        String eventId = getIntent().getStringExtra("eventId");
        loadParticipants(eventId);
    }

    private void loadParticipants(String eventId) {

        progressBar.setVisibility(View.VISIBLE);

        db.collection("registrations")
                .whereEqualTo("eventId", eventId)
                .whereIn("status", Arrays.asList("accepted", "approved"))
                .addSnapshotListener((snap, e) -> {

                    progressBar.setVisibility(View.GONE);

                    if (snap == null || snap.isEmpty()) {
                        tvNoParticipants.setVisibility(View.VISIBLE);
                        recyclerParticipants.setVisibility(View.GONE);
                        return;
                    }

                    list.clear();

                    for (DocumentSnapshot d : snap) {

                        RegistrationModel m = d.toObject(RegistrationModel.class);
                        if (m == null) continue;

                        // 🔥 AMBIL DATA USER
                        db.collection("users")
                                .document(m.getUserId())
                                .get()
                                .addOnSuccessListener(userDoc -> {

                                    if (userDoc.exists()) {
                                        m.setUserName(userDoc.getString("name"));
                                        m.setUserEmail(userDoc.getString("email"));
                                    } else {
                                        m.setUserName("Unknown");
                                        m.setUserEmail("-");
                                    }

                                    list.add(m);
                                    adapter.notifyDataSetChanged();
                                });
                    }

                    recyclerParticipants.setVisibility(View.VISIBLE);
                    tvNoParticipants.setVisibility(View.GONE);
                });
    }
}
