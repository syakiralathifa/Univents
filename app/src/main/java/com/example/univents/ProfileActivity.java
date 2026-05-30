package com.example.univents;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.*;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";

    TextView txtProfileName, txtProfileEmail;
    RecyclerView recyclerExperience;

    FirebaseAuth auth;
    FirebaseFirestore db;

    ArrayList<ExperienceModel> list;
    ExperienceAdapter adapter;

    String userIdToLoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // ================= JARING PENGAMAN (TRY-CATCH) =================
        try {
            // Menghubungkan variabel dengan ID dari file XML
            txtProfileName = findViewById(R.id.txtProfileName);
            txtProfileEmail = findViewById(R.id.txtProfileEmail);
            recyclerExperience = findViewById(R.id.recyclerExperience);
        } catch (Exception e) {
            // Jika ada error findViewById (misal ID salah), aplikasi tidak akan crash.
            Log.e(TAG, "Error linking views from XML. Check your layout file for correct IDs.", e);
            Toast.makeText(this, "Layout Error. Please contact developer.", Toast.LENGTH_LONG).show();
            finish(); // Tutup activity dengan aman
            return;
        }
        // ==============================================================

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Cek apakah ada "USER_ID" yang dikirim melalui Intent
        if (getIntent().hasExtra("USER_ID")) {
            userIdToLoad = getIntent().getStringExtra("USER_ID");
        } else {
            FirebaseUser currentUser = auth.getCurrentUser();
            if (currentUser != null) {
                userIdToLoad = currentUser.getUid();
            }
        }

        if (userIdToLoad == null || userIdToLoad.isEmpty()) {
            Toast.makeText(this, "Fatal Error: User ID is missing.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        Log.d(TAG, "Loading profile for user: " + userIdToLoad);

        // Setup RecyclerView
        recyclerExperience.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();
        adapter = new ExperienceAdapter(this, list);
        recyclerExperience.setAdapter(adapter);

        // Muat data
        loadProfile();
        loadExperiences();
    }

    private void loadProfile() {
        db.collection("users").document(userIdToLoad)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc != null && doc.exists()) {
                        txtProfileName.setText(doc.getString("name"));
                        txtProfileEmail.setText(doc.getString("email"));
                    } else {
                        txtProfileName.setText("User Not Found");
                        txtProfileEmail.setText("-");
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error loading profile", e));
    }

    // ================== PERBAIKAN UTAMA PENYEBAB CRASH ==================
    private void loadExperiences() {
        // Kita hapus .orderBy("timestamp") untuk sementara untuk mencegah crash.
        // Ini adalah cara paling efektif untuk mendiagnosis masalah.
        db.collection("users").document(userIdToLoad)
                .collection("experiences")
                .get() // <-- .orderBy("timestamp") DIHAPUS
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        list.clear();
                        if (task.getResult().isEmpty()) {
                            Toast.makeText(this, "This user has no experiences yet.", Toast.LENGTH_SHORT).show();
                        } else {
                            for (DocumentSnapshot doc : task.getResult()) {
                                String eventId = doc.getString("eventId");
                                if (eventId != null && !eventId.isEmpty()) {
                                    db.collection("events").document(eventId)
                                            .get()
                                            .addOnSuccessListener(eventDoc -> {
                                                if (eventDoc.exists()) {
                                                    String title = eventDoc.getString("title");
                                                    String date = eventDoc.getString("date");
                                                    list.add(new ExperienceModel(eventId, title, date));
                                                    adapter.notifyDataSetChanged();
                                                }
                                            });
                                } else {
                                    Log.w(TAG, "Found an experience with a null or empty eventId. Skipping.");
                                }
                            }
                        }
                    } else {
                        // Jika query gagal (misalnya karena masalah permission atau network)
                        Log.e(TAG, "Error loading experiences: ", task.getException());
                        Toast.makeText(this, "Error loading experiences.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
