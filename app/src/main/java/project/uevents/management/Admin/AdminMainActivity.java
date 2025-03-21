package project.uevents.management.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;

import project.uevents.management.Adapters.StudentAdapter;
import project.uevents.management.LoginActivity;
import project.uevents.management.Models.User;
import project.uevents.management.databinding.ActivityAdminMainBinding;

public class AdminMainActivity extends AppCompatActivity {

    private ActivityAdminMainBinding binding;
    private DatabaseReference databaseReference;
    private List<User> studentList;
    private StudentAdapter studentAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.welcomeText.setText("Welcome, Admin");

        binding.btnBatches.setOnClickListener(v -> startActivity(new Intent(this, AdminBatchesActivity.class)));
        binding.btnDepartments.setOnClickListener(v -> startActivity(new Intent(this, AdminDepartmentsActivity.class)));
        binding.btnSemesters.setOnClickListener(v -> startActivity(new Intent(this, AdminSemestersActivity.class)));

        studentList = new ArrayList<>();
        studentAdapter = new StudentAdapter(studentList, AdminMainActivity.this);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(studentAdapter);

        // Fetch students from Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        binding.btnLogout.setOnClickListener(v -> showLogoutConfirmationDialog());

        fetchStudents();
        updateFCMToken();
    }
    private void fetchStudents() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            binding.recyclerView.setVisibility(View.VISIBLE);
                            binding.empty.setVisibility(View.GONE);

                            studentList.clear();
                            for (DataSnapshot data : snapshot.getChildren()) {
                                User student = data.getValue(User.class);
                                studentList.add(student);
                            }
                            studentAdapter.notifyDataSetChanged();
                        }else{
                            binding.recyclerView.setVisibility(View.GONE);
                            binding.empty.setVisibility(View.VISIBLE);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }
    public void finishView(View view){
        finish();
    }
    private void showLogoutConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> logoutUser())
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void logoutUser() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, LoginActivity.class));
        this.finish();
    }

    public static void updateFCMToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.e("FCM", "Fetching FCM token failed", task.getException());
                        return;
                    }

                    // Get new FCM token
                    String token = task.getResult();
                    Log.d("FCM", "FCM Token: " + token);

                    // Update token in Firebase
                    saveTokenToFirebase(token);
                });
    }

    private static void saveTokenToFirebase(String token) {

        DatabaseReference userRef = FirebaseDatabase.getInstance()
                .getReference("Admin")
                .child("fcmToken");

        userRef.setValue(token)
                .addOnSuccessListener(aVoid -> Log.d("FCM", "Token updated successfully"))
                .addOnFailureListener(e -> Log.e("FCM", "Failed to update token", e));
    }


}

