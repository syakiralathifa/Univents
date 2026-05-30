package com.example.univents;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText editEmailForgot;
    private Button btnSendLink;
    private TextView backToLogin;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        editEmailForgot = findViewById(R.id.editEmailForgot);
        btnSendLink = findViewById(R.id.btnSendLink);
        backToLogin = findViewById(R.id.backToLogin);
        mAuth = FirebaseAuth.getInstance();

        btnSendLink.setOnClickListener(v -> sendResetLink());

        backToLogin.setOnClickListener(v -> {
            // Kembali ke halaman login
            finish();
        });
    }

    private void sendResetLink() {
        String email = editEmailForgot.getText().toString().trim();

        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editEmailForgot.setError("Please enter a valid email");
            editEmailForgot.requestFocus();
            return;
        }

        btnSendLink.setEnabled(false);
        btnSendLink.setText("Sending...");

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(ForgotPasswordActivity.this, "Password reset link sent to your email.", Toast.LENGTH_LONG).show();
                        // Arahkan kembali ke login setelah berhasil
                        startActivity(new Intent(ForgotPasswordActivity.this, UserLoginActivity.class));
                        finish();
                    } else {
                        Toast.makeText(ForgotPasswordActivity.this, "Failed to send reset link: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                    // Kembalikan kondisi tombol
                    btnSendLink.setEnabled(true);
                    btnSendLink.setText("Send Reset Link");
                });
    }
}
