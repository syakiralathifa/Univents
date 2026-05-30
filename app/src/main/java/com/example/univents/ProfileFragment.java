package com.example.univents;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

public class ProfileFragment extends Fragment {

    // ===== UI =====
    private TextView txtName, txtEmail, txtBio, txtCity, txtPhone;
    private ImageView imgProfile;
    private LinearLayout skillContainer;
    private Button btnAddSkill, btnEditProfile;

    // ===== EXPERIENCE =====
    private RecyclerView recyclerExperience;
    private ExperienceAdapter experienceAdapter;
    private final ArrayList<ExperienceModel> experienceList = new ArrayList<>();

    // ===== FIREBASE =====
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    // ===== SKILLS =====
    private final ArrayList<String> skills = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // ===== BIND VIEW =====
        txtName  = view.findViewById(R.id.txtProfileName);
        txtEmail = view.findViewById(R.id.txtProfileEmail);
        txtBio   = view.findViewById(R.id.txtProfileBio);
        txtCity  = view.findViewById(R.id.txtProfileCity);
        txtPhone = view.findViewById(R.id.txtProfilePhone);

        imgProfile = view.findViewById(R.id.imgProfile);
        skillContainer = view.findViewById(R.id.skillContainer);
        btnAddSkill = view.findViewById(R.id.btnAddSkill);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);

        // ===== EXPERIENCE RECYCLER =====
        recyclerExperience = view.findViewById(R.id.recyclerExperience);
        recyclerExperience.setLayoutManager(new LinearLayoutManager(requireContext()));
        experienceAdapter = new ExperienceAdapter(requireContext(), experienceList);
        recyclerExperience.setAdapter(experienceAdapter);

        // ===== FIREBASE =====
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Edit Profile
        btnEditProfile.setOnClickListener(v ->
                startActivity(new Intent(requireActivity(), EditProfileActivity.class))
        );

        // Add Skill
        btnAddSkill.setOnClickListener(v -> showAddSkillDialog());

        loadProfile();
        return view;
    }

    // ================= LOAD PROFILE =================
    private void loadProfile() {
        String userId = mAuth.getUid();
        if (userId == null) return;

        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (!isAdded() || !doc.exists()) return;

                    // ===== BASIC INFO =====
                    txtName.setText(doc.getString("name"));
                    txtEmail.setText(doc.getString("email"));
                    txtBio.setText(doc.getString("bio"));
                    txtCity.setText("City: " + safe(doc.getString("city")));
                    txtPhone.setText("Phone: " + safe(doc.getString("phone")));

                    // ===== SKILLS =====
                    skills.clear();
                    skillContainer.removeAllViews();

                    if (doc.contains("skills")) {
                        skills.addAll((ArrayList<String>) doc.get("skills"));
                    }

                    for (String s : skills) {
                        addSkillChip(s);
                    }

                    // ===== EXPERIENCE =====
                    experienceList.clear();

                    if (doc.contains("experiences")) {
                        ArrayList<HashMap<String, Object>> raw =
                                (ArrayList<HashMap<String, Object>>) doc.get("experiences");

                        if (raw != null) {
                            for (HashMap<String, Object> map : raw) {
                                experienceList.add(new ExperienceModel(
                                        (String) map.get("eventId"),
                                        (String) map.get("title"),
                                        (String) map.get("date")
                                ));
                            }
                        }
                    }

                    experienceAdapter.notifyDataSetChanged();
                });
    }

    // ================= ADD SKILL =================
    private void showAddSkillDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Add Skill");

        EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint("Skill name");

        builder.setView(input);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String newSkill = input.getText().toString().trim();
            if (!newSkill.isEmpty()) {
                skills.add(newSkill);
                saveSkills();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    // ================= SKILL CHIP =================
    private void addSkillChip(String skillName) {
        View chip = getLayoutInflater().inflate(R.layout.chip_skill, skillContainer, false);
        TextView txtSkill = chip.findViewById(R.id.txtSkill);
        txtSkill.setText(skillName);

        chip.setOnLongClickListener(v -> {
            showDeleteSkillDialog(skillName);
            return true;
        });

        skillContainer.addView(chip);
    }

    // ================= DELETE SKILL =================
    private void showDeleteSkillDialog(String skill) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Skill?")
                .setMessage("Remove \"" + skill + "\" from your skills?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    skills.remove(skill);
                    saveSkills();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ================= SAVE SKILLS =================
    private void saveSkills() {
        String userId = mAuth.getUid();
        if (userId == null) return;

        HashMap<String, Object> update = new HashMap<>();
        update.put("skills", skills);

        db.collection("users").document(userId)
                .update(update)
                .addOnSuccessListener(a -> {
                    if (!isAdded()) return;
                    skillContainer.removeAllViews();
                    for (String s : skills) addSkillChip(s);
                });
    }

    // ================= SAFE STRING =================
    private String safe(String value) {
        return value == null || value.isEmpty() ? "-" : value;
    }
}