package com.example.univents;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class EventListActivity extends AppCompatActivity {

    RecyclerView recyclerEvents;
    ArrayList<Event> list;
    EventAdapter adapter;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

        recyclerEvents = findViewById(R.id.recyclerEvents);
        recyclerEvents.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();

        list = new ArrayList<>();
        adapter = new EventAdapter(this, list);
        recyclerEvents.setAdapter(adapter);

        loadEvents();
    }

    private void loadEvents() {
        db.collection("events")
                .get()
                .addOnSuccessListener(query -> {
                    list.clear();
                    for (DocumentSnapshot doc : query) {
                        Event event = doc.toObject(Event.class);

                        if (event != null) {
                            event.setEventId(doc.getId()); // FIXED
                            list.add(event);
                        }
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(EventListActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}
