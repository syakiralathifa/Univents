package com.example.univents;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    private EditText editName, editCity, editPhone, editBio, editExperience;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Edit Profile");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        editName = findViewById(R.id.editName);
        editCity = findViewById(R.id.editCity);
        editPhone = findViewById(R.id.editPhone);
        editBio = findViewById(R.id.editBio);
        editExperience = findViewById(R.id.editExperience);

        Button btnSave = findViewById(R.id.btnSaveProfile);

        loadCurrentData();
        btnSave.setOnClickListener(v -> saveProfile());
    }

    private void loadCurrentData() {
        if (mAuth.getCurrentUser() == null) return;

        db.collection("users").document(mAuth.getUid())
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc == null || !doc.exists()) return;

                    editName.setText(safe(doc.getString("name")));
                    editCity.setText(safe(doc.getString("city")));
                    editPhone.setText(safe(doc.getString("phone")));
                    editBio.setText(safe(doc.getString("bio")));

                    // LOAD EXPERIENCE
                    if (doc.contains("experiences")) {
                        ArrayList<HashMap<String, Object>> raw =
                                (ArrayList<HashMap<String, Object>>) doc.get("experiences");

                        if (raw != null && !raw.isEmpty()) {
                            StringBuilder sb = new StringBuilder();
                            for (HashMap<String, Object> map : raw) {
                                sb.append(map.get("title"));
                                if (map.get("date") != null && !map.get("date").toString().isEmpty()) {
                                    sb.append(" | ").append(map.get("date"));
                                }
                                sb.append("\n");
                            }
                            editExperience.setText(sb.toString().trim());
                        }
                    }
                });
    }

    private void saveProfile() {
        if (mAuth.getCurrentUser() == null) return;

        String name = editName.getText().toString().trim();
        String city = editCity.getText().toString().trim();
        String phone = editPhone.getText().toString().trim();
        String bio = editBio.getText().toString().trim();
        String expText = editExperience.getText().toString().trim();

        ArrayList<Map<String, Object>> experiences = new ArrayList<>();

        if (!expText.isEmpty()) {
            String[] lines = expText.split("\n");
            for (String line : lines) {
                if (!line.trim().isEmpty()) {

                    String title = line.trim();
                    String year = "";

                    if (line.contains("|")) {
                        String[] parts = line.split("\\|");
                        title = parts[0].trim();
                        year = parts.length > 1 ? parts[1].trim() : "";
                    }

                    Map<String, Object> exp = new HashMap<>();
                    exp.put("eventId", "");
                    exp.put("title", title);
                    exp.put("date", year);

                    experiences.add(exp);
                }
            }
        }

        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("city", city);
        data.put("phone", phone);
        data.put("bio", bio);
        data.put("experiences", experiences);

        db.collection("users").document(mAuth.getUid())
                .update(data)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private String safe(String v) {
        return v == null ? "" : v;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}