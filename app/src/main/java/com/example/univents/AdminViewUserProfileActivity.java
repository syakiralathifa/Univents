package com.example.univents;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdminViewUserProfileActivity extends AppCompatActivity {

    // Profile
    TextView txtName, txtEmail, txtBio, txtCity, txtPhone;

    // Skills
    RecyclerView rvSkills;
    SkillAdapter skillAdapter;
    ArrayList<String> skillList = new ArrayList<>();

    // Experiences
    RecyclerView recyclerUserExp;
    ExperienceAdapter adapter;
    ArrayList<ExperienceModel> list = new ArrayList<>();

    FirebaseFirestore db;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view_user_profile);

        // INIT VIEW
        txtName = findViewById(R.id.txtName);
        txtEmail = findViewById(R.id.txtEmail);
        txtBio = findViewById(R.id.txtBio);
        txtCity = findViewById(R.id.txtCity);
        txtPhone = findViewById(R.id.txtPhone);

        rvSkills = findViewById(R.id.rvSkills);
        recyclerUserExp = findViewById(R.id.recyclerUserExp);

        db = FirebaseFirestore.getInstance();
        userId = getIntent().getStringExtra("userId");

        if (userId == null) {
            Toast.makeText(this, "User ID tidak ditemukan", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // SKILLS RV
        rvSkills.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );
        skillAdapter = new SkillAdapter(this, skillList);
        rvSkills.setAdapter(skillAdapter);

        // EXPERIENCE RV
        recyclerUserExp.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ExperienceAdapter(this, list);
        recyclerUserExp.setAdapter(adapter);

        loadProfile();
        loadExperiences(); // 🔥 INI YANG DIBENERIN
    }

    // ================= USER PROFILE =================
    private void loadProfile() {
        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) return;

                    txtName.setText(doc.getString("name"));
                    txtEmail.setText(doc.getString("email"));

                    txtBio.setText(
                            doc.getString("bio") != null ? doc.getString("bio") : "-"
                    );

                    txtCity.setText("City: " +
                            (doc.getString("city") != null ? doc.getString("city") : "-"));

                    txtPhone.setText("Phone: " +
                            (doc.getString("phone") != null ? doc.getString("phone") : "-"));

                    List<String> skills = (List<String>) doc.get("skills");
                    if (skills != null) {
                        skillList.clear();
                        skillList.addAll(skills);
                        skillAdapter.notifyDataSetChanged();
                    }
                });
    }

    // ================= EXPERIENCES (FIXED) =================
    private void loadExperiences() {
        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(doc -> {

                    list.clear();

                    if (!doc.exists()) {
                        adapter.notifyDataSetChanged();
                        return;
                    }

                    @SuppressWarnings("unchecked")
                    ArrayList<Map<String, Object>> experiences =
                            (ArrayList<Map<String, Object>>) doc.get("experiences");

                    if (experiences == null || experiences.isEmpty()) {
                        adapter.notifyDataSetChanged();
                        return;
                    }

                    for (Map<String, Object> exp : experiences) {

                        String title = exp.get("title") != null
                                ? exp.get("title").toString()
                                : "-";

                        String date = exp.get("date") != null
                                ? exp.get("date").toString()
                                : "-";

                        String eventId = exp.get("eventId") != null
                                ? exp.get("eventId").toString()
                                : "";

                        list.add(new ExperienceModel(eventId, title, date));
                    }

                    adapter.notifyDataSetChanged();
                });
    }
}
