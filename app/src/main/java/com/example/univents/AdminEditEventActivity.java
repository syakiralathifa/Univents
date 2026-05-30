package com.example.univents;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.*;
import android.content.Intent;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AdminEditEventActivity extends AppCompatActivity {

    EditText editTitle, editDescription, editDate, editTime, editLocation;
    Switch switchNeedsCommittee;
    Button btnUpdateEvent;

    String eventId;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_edit_event);

        editTitle = findViewById(R.id.editTitle);
        editDescription = findViewById(R.id.editDescription);
        editDate = findViewById(R.id.editDate);
        editTime = findViewById(R.id.editTime);
        editLocation = findViewById(R.id.editLocation);
        switchNeedsCommittee = findViewById(R.id.switchNeedsCommittee); // NEW
        btnUpdateEvent = findViewById(R.id.btnUpdateEvent);

        eventId = getIntent().getStringExtra("eventId");

        loadEventData();

        editDate.setOnClickListener(v -> showDatePicker());
        editTime.setOnClickListener(v -> showTimePicker());

        btnUpdateEvent.setOnClickListener(v -> updateEvent());
    }

    private void loadEventData() {
        db.collection("events")
                .document(eventId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        editTitle.setText(doc.getString("title"));
                        editDescription.setText(doc.getString("description"));
                        editDate.setText(doc.getString("date"));
                        editTime.setText(doc.getString("time"));
                        editLocation.setText(doc.getString("location"));

                        Boolean needs = doc.getBoolean("needsCommittee");
                        switchNeedsCommittee.setChecked(needs != null && needs);
                    }
                });
    }

    private void showDatePicker() {
        Calendar c = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, y, m, d) -> editDate.setText(d + "/" + (m + 1) + "/" + y),
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)
        );
        dialog.show();
    }

    private void showTimePicker() {
        Calendar c = Calendar.getInstance();
        TimePickerDialog dialog = new TimePickerDialog(
                this,
                (view, h, m) -> editTime.setText(String.format("%02d:%02d", h, m)),
                c.get(Calendar.HOUR_OF_DAY),
                c.get(Calendar.MINUTE),
                true
        );
        dialog.show();
    }

    private void updateEvent() {
        Map<String, Object> updated = new HashMap<>();
        updated.put("title", editTitle.getText().toString());
        updated.put("description", editDescription.getText().toString());
        updated.put("date", editDate.getText().toString());
        updated.put("time", editTime.getText().toString());
        updated.put("location", editLocation.getText().toString());
        updated.put("needsCommittee", switchNeedsCommittee.isChecked()); // NEW

        db.collection("events")
                .document(eventId)
                .update(updated)
                .addOnSuccessListener(a -> {
                    Toast.makeText(this, "Event updated!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
