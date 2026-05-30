package com.example.univents;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AdminAddEventActivity extends AppCompatActivity {

    EditText inputTitle, inputDescription, inputDate, inputTime, inputLocation;
    Switch switchNeedsCommittee;
    Button btnSaveEvent;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_event);

        inputTitle = findViewById(R.id.inputTitle);
        inputDescription = findViewById(R.id.inputDescription);
        inputDate = findViewById(R.id.inputDate);
        inputTime = findViewById(R.id.inputTime);
        inputLocation = findViewById(R.id.inputLocation);
        switchNeedsCommittee = findViewById(R.id.switchNeedsCommittee);
        btnSaveEvent = findViewById(R.id.btnSaveEvent);

        db = FirebaseFirestore.getInstance();

        inputDate.setOnClickListener(v -> pickDate());
        inputTime.setOnClickListener(v -> pickTime());

        btnSaveEvent.setOnClickListener(v -> saveEvent());
    }

    private void pickDate() {
        Calendar cal = Calendar.getInstance();
        DatePickerDialog dp = new DatePickerDialog(this,
                (view, year, month, day) ->
                        inputDate.setText(day + "/" + (month + 1) + "/" + year),
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH));

        dp.show();
    }

    private void pickTime() {
        Calendar cal = Calendar.getInstance();
        TimePickerDialog tp = new TimePickerDialog(this,
                (view, hour, minute) ->
                        inputTime.setText(String.format("%02d:%02d", hour, minute)),
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                true);

        tp.show();
    }

    private void saveEvent() {
        String id = UUID.randomUUID().toString();

        Map<String, Object> event = new HashMap<>();
        event.put("eventId", id);
        event.put("title", inputTitle.getText().toString());
        event.put("description", inputDescription.getText().toString());
        event.put("date", inputDate.getText().toString());
        event.put("time", inputTime.getText().toString());
        event.put("location", inputLocation.getText().toString());
        event.put("needsCommittee", switchNeedsCommittee.isChecked());
        event.put("participantsCount", 0); // NEW FIELD

        db.collection("events")
                .document(id)
                .set(event)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Event Created!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
