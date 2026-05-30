package com.example.univents;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.util.ArrayList;

public class MyEventsActivity extends AppCompatActivity {

    RecyclerView recyclerMyEvents;
    ArrayList<MyEventModel> list;
    MyEventAdapter adapter;

    FirebaseFirestore db;
    FirebaseAuth auth;

    String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_events);

        recyclerMyEvents = findViewById(R.id.recyclerMyEvents);
        recyclerMyEvents.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        adapter = new MyEventAdapter(this, list);
        recyclerMyEvents.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        currentUserId = auth.getCurrentUser().getUid();

        loadMyEvents();
    }

    private void loadMyEvents() {
        db.collection("registrations")
                .whereEqualTo("userId", currentUserId)
                .get()
                .addOnSuccessListener(regQuery -> {
                    list.clear();

                    for (DocumentSnapshot regDoc : regQuery) {
                        String eventId = regDoc.getString("eventId");
                        String status = regDoc.getString("status");

                        if (eventId == null || eventId.isEmpty()) continue;

                        db.collection("events").document(eventId)
                                .get()
                                .addOnSuccessListener(eventDoc -> {
                                    if (eventDoc.exists()) {
                                        String title = eventDoc.getString("title");
                                        String date = eventDoc.getString("date");

                                        list.add(new MyEventModel(eventId, title, date, status));
                                        adapter.notifyDataSetChanged();
                                    }
                                });
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}
