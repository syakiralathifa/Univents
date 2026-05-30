package com.example.univents;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeActivity extends AppCompatActivity {

    TextView txtWelcome;
    Button btnEventList, btnMyEvents, btnProfile, btnAdmin, btnLogout;

    FirebaseAuth auth;
    FirebaseFirestore db;
    String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        txtWelcome = findViewById(R.id.txtWelcome);
        btnEventList = findViewById(R.id.btnEventList);
        btnMyEvents = findViewById(R.id.btnMyEvents);
        btnProfile = findViewById(R.id.btnProfile);
        btnAdmin = findViewById(R.id.btnAdmin);
        btnLogout = findViewById(R.id.btnLogout);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(HomeActivity.this, UserLoginActivity.class));
            finish();
            return;
        }

        currentUserId = auth.getCurrentUser().getUid();

        loadUserInfo();

        // Open Event List
        btnEventList.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, EventListActivity.class))
        );

        // Open My Events
        btnMyEvents.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, MyEventsActivity.class))
        );

        // Open Profile
        btnProfile.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, ProfileActivity.class))
        );

        // Open Admin (Add Event)
        btnAdmin.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, AdminAddEventActivity.class))
        );

        // Logout
        btnLogout.setOnClickListener(v -> {
            auth.signOut();
            startActivity(new Intent(HomeActivity.this, UserLoginActivity.class));
            finish();
        });
    }

    private void loadUserInfo() {
        db.collection("users").document(currentUserId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String name = doc.getString("name");
                        String role = doc.getString("role");

                        if (name == null) name = "User";

                        txtWelcome.setText("Welcome, " + name + "!");

                        // Tampilkan tombol Admin hanya jika role = admin
                        if (role != null && role.equals("admin")) {
                            btnAdmin.setVisibility(View.VISIBLE);
                        } else {
                            btnAdmin.setVisibility(View.GONE);
                        }
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}
