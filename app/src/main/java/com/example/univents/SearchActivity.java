package com.example.univents;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    private EditText edtSearch;
    private RecyclerView recyclerSearch;
    private EventAdapter adapter;
    private final ArrayList<Event> eventList = new ArrayList<>();

    private FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        edtSearch = findViewById(R.id.edtSearch);
        recyclerSearch = findViewById(R.id.recyclerSearch);
        ImageView btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        recyclerSearch.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EventAdapter(this, eventList);
        recyclerSearch.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        loadEvents();

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }
        });
    }

    private void loadEvents() {
        db.collection("events")
                .get()
                .addOnSuccessListener(q -> {
                    eventList.clear();
                    for (DocumentSnapshot d : q) {
                        Event e = d.toObject(Event.class);
                        if (e != null) {
                            e.setEventId(d.getId());
                            eventList.add(e);
                        }
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    private void filter(String keyword) {
        keyword = keyword.toLowerCase();

        ArrayList<Event> filtered = new ArrayList<>();
        for (Event e : eventList) {
            if (e.getTitle() != null &&
                    e.getTitle().toLowerCase().contains(keyword)) {
                filtered.add(e);
            }
        }

        adapter.updateList(filtered);
    }
}
