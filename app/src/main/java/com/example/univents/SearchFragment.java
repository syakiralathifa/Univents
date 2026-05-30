package com.example.univents;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private EditText edtSearch;
    private RecyclerView recyclerSearch;
    private ProgressBar progressBar;
    private TextView tvEmpty;

    private EventAdapter adapter;
    private List<Event> eventList = new ArrayList<>();
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View v = inflater.inflate(R.layout.fragment_search, container, false);

        edtSearch = v.findViewById(R.id.edtSearch);
        recyclerSearch = v.findViewById(R.id.recyclerSearch);
        progressBar = v.findViewById(R.id.progressBar);
        tvEmpty = v.findViewById(R.id.tvEmpty);

        recyclerSearch.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new EventAdapter(getContext(), eventList);
        recyclerSearch.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        loadAllEvents();

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterEvents(s.toString());
            }
        });

        return v;
    }

    private void loadAllEvents() {
        progressBar.setVisibility(View.VISIBLE);

        db.collection("events")
                .get()
                .addOnSuccessListener(snap -> {
                    eventList.clear();

                    for (DocumentSnapshot d : snap) {
                        Event e = d.toObject(Event.class);
                        if (e != null) {
                            e.setEventId(d.getId());
                            eventList.add(e);
                        }
                    }

                    adapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                    tvEmpty.setVisibility(eventList.isEmpty() ? View.VISIBLE : View.GONE);
                });
    }

    private void filterEvents(String keyword) {
        List<Event> filtered = new ArrayList<>();

        for (Event e : eventList) {
            if (e.getTitle() != null &&
                    e.getTitle().toLowerCase().contains(keyword.toLowerCase())) {
                filtered.add(e);
            }
        }

        adapter = new EventAdapter(getContext(), filtered);
        recyclerSearch.setAdapter(adapter);

        tvEmpty.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
    }
}
