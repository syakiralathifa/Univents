package com.example.univents;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserLoginActivity extends AppCompatActivity {

    private EditText loginEmail, loginPassword;
    private Button btnLogin;
    private TextView goToRegister, txtForgotPassword;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        // ================= FIREBASE =================
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // ================= INIT VIEW =================
        loginEmail = findViewById(R.id.loginEmail);
        loginPassword = findViewById(R.id.loginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        goToRegister = findViewById(R.id.goToRegister);
        txtForgotPassword = findViewById(R.id.txtForgotPassword);

        // ================= LOGIN =================
        btnLogin.setOnClickListener(v -> loginWithEmail());

        // ================= SIGN UP =================
        goToRegister.setOnClickListener(v -> {
            startActivity(new Intent(
                    UserLoginActivity.this,
                    UserRegisterActivity.class
            ));
        });

        // ================= FORGOT PASSWORD (DIPERBAIKI UNTUK MEMBUKA HALAMAN BARU) =================
        txtForgotPassword.setOnClickListener(v -> {
            // Logika ini sekarang hanya membuka activity baru, tidak lagi mengirim email.
            startActivity(new Intent(UserLoginActivity.this, ForgotPasswordActivity.class));
        });
    }

    // =================================================
    // LOGIN EMAIL & PASSWORD + CEK ROLE
    // =================================================
    private void loginWithEmail() {

        String email = loginEmail.getText().toString().trim();
        String password = loginPassword.getText().toString().trim();

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            loginEmail.setError("Invalid email");
            loginEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            loginPassword.setError("Password required");
            loginPassword.requestFocus();
            return;
        }

        // Disable button before process
        btnLogin.setEnabled(false);
        btnLogin.setText("Logging in...");

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {

                    String uid = mAuth.getCurrentUser().getUid();

                    // 🔥 CEK ROLE DI FIRESTORE
                    db.collection("users")
                            .document(uid)
                            .get()
                            .addOnSuccessListener(doc -> {

                                if (!doc.exists()) {
                                    Toast.makeText(this,
                                            "User data not found",
                                            Toast.LENGTH_SHORT).show();
                                    resetButton();
                                    return;
                                }

                                String role = doc.getString("role");

                                if (role == null) {
                                    Toast.makeText(this,
                                            "Role not assigned",
                                            Toast.LENGTH_SHORT).show();
                                    resetButton();
                                    return;
                                }

                                if (role.equalsIgnoreCase("admin")) {
                                    startActivity(new Intent(this, AdminHomeActivity.class));
                                } else {
                                    startActivity(new Intent(this, MainActivity.class));
                                }

                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this,
                                        "Failed to load user role: " + e.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                                resetButton();
                            });

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this,
                            "Login failed: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    resetButton();
                });
    }

    // --- METODE forgotPassword() TELAH DIHAPUS KARENA FUNGSINYA PINDAH ---

    private void resetButton() {
        btnLogin.setEnabled(true);
        btnLogin.setText("Log In");
    }
}
