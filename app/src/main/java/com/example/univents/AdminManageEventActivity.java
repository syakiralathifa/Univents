package com.example.univents;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AdminManageEventActivity extends AppCompatActivity {

    RecyclerView recyclerRegistrations;
    List<RegistrationModel> list;
    AdminRegistrationAdapter adapter;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manage_event);

        recyclerRegistrations = findViewById(R.id.recyclerRegistrations);
        recyclerRegistrations.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        adapter = new AdminRegistrationAdapter(this, list);
        recyclerRegistrations.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        loadRegistrations();
    }

    // 🔥 REALTIME LISTENER (TANPA REFRESH)
    private void loadRegistrations() {
        db.collection("registrations")
                .whereEqualTo("status", "pending")
                .addSnapshotListener((query, error) -> {

                    if (error != null || query == null) return;

                    list.clear();

                    for (DocumentSnapshot doc : query) {

                        RegistrationModel model = doc.toObject(RegistrationModel.class);

                        if (model != null) {
                            model.setRegId(doc.getId());

                            db.collection("users")
                                    .document(model.getUserId())
                                    .get()
                                    .addOnSuccessListener(userDoc -> {

                                        if (userDoc.exists()) {
                                            model.setUserName(userDoc.getString("name"));
                                            model.setUserEmail(userDoc.getString("email"));
                                        } else {
                                            model.setUserName("Unknown");
                                            model.setUserEmail("-");
                                        }

                                        list.add(model);
                                        adapter.notifyDataSetChanged();
                                    });
                        }
                    }
                });
    }
}
