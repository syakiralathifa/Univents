package com.example.univents;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private TextView txtUserName;
    private ImageView imgProfile;
    private EditText edtSearch;

    private RecyclerView recyclerRecommended, recyclerAllEvents;

    private EventHorizontalAdapter adapterRecommended;
    private EventAdapter adapterAll;

    private final ArrayList<Event> recommendedList = new ArrayList<>();
    private final ArrayList<Event> allEvents = new ArrayList<>();
    private final ArrayList<Event> allEventsBackup = new ArrayList<>();

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        txtUserName = view.findViewById(R.id.txtUserName);
        imgProfile = view.findViewById(R.id.imgProfile);
        edtSearch = view.findViewById(R.id.edtSearch);

        recyclerRecommended = view.findViewById(R.id.recyclerRecommended);
        recyclerAllEvents = view.findViewById(R.id.recyclerAllEvents);

        recyclerRecommended.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        adapterRecommended = new EventHorizontalAdapter(getContext(), recommendedList);
        recyclerRecommended.setAdapter(adapterRecommended);

        recyclerAllEvents.setLayoutManager(new LinearLayoutManager(getContext()));
        adapterAll = new EventAdapter(getContext(), allEvents);
        recyclerAllEvents.setAdapter(adapterAll);

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterEvents(s.toString());
            }
        });

        loadUserInfo();
        loadRecommendedEvents();
        loadAllEvents();

        return view;
    }

    private void filterEvents(String keyword) {
        keyword = keyword.toLowerCase();
        allEvents.clear();

        if (keyword.isEmpty()) {
            allEvents.addAll(allEventsBackup);
        } else {
            for (Event e : allEventsBackup) {
                if (e.getTitle() != null &&
                        e.getTitle().toLowerCase().contains(keyword)) {
                    allEvents.add(e);
                }
            }
        }
        adapterAll.notifyDataSetChanged();
    }

    private void loadUserInfo() {
        if (auth.getCurrentUser() == null) return;

        db.collection("users")
                .document(auth.getUid())
                .get()
                .addOnSuccessListener(d ->
                        txtUserName.setText(d.getString("name")));
    }

    private void loadRecommendedEvents() {
        db.collection("events")
                .limit(5)
                .get()
                .addOnSuccessListener(q -> {
                    recommendedList.clear();
                    for (DocumentSnapshot d : q) {
                        Event e = d.toObject(Event.class);
                        if (e != null) {
                            e.setEventId(d.getId());
                            recommendedList.add(e);
                        }
                    }
                    adapterRecommended.notifyDataSetChanged();
                });
    }

    private void loadAllEvents() {
        db.collection("events")
                .get()
                .addOnSuccessListener(q -> {
                    allEvents.clear();
                    allEventsBackup.clear();
                    for (DocumentSnapshot d : q) {
                        Event e = d.toObject(Event.class);
                        if (e != null) {
                            e.setEventId(d.getId());
                            allEvents.add(e);
                            allEventsBackup.add(e);
                        }
                    }
                    adapterAll.notifyDataSetChanged();
                });
    }
}
