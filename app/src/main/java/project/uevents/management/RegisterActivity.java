package project.uevents.management;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import project.uevents.management.Models.User;
import project.uevents.management.databinding.ActivityRegisterBinding;

public class RegisterActivity extends AppCompatActivity {
    ActivityRegisterBinding binding;
    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;
    private boolean isUsernameExists = false;
    Context context;
    private DatabaseReference databaseReference;
    private List<String> batchList = new ArrayList<>();
    private List<String> departmentList = new ArrayList<>();
    private List<String> semesterList = new ArrayList<>();
    private ArrayAdapter<String> batchAdapter, departmentAdapter, semesterAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;
        Window window = getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));

        databaseReference = FirebaseDatabase.getInstance().getReference();

        setupAdapters();
        fetchBatches();
        fetchDepartments();
        fetchSemesters();


        binding.ivTogglePassword.setOnClickListener(v -> togglePasswordVisibility());
        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, LoginActivity.class));
            }
        });
        binding.ivToggleConfirmPassword.setOnClickListener(v -> toggleConfirmPasswordVisibility());

        binding.btnRegister.setOnClickListener(v -> registerUser());

        binding.etUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                checkUsernameAvailable(s.toString());
            }
        });
    }

    private void checkUsernameAvailable(String userName) {
        FirebaseDatabase.getInstance().getReference("Users")
                .orderByChild("username").equalTo(userName)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            isUsernameExists = true;
                            binding.etUsername.setError("Username already exits.");
                        }else{
                            isUsernameExists = false;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Util.dismiss();
                    }
                });

    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            binding.etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            binding.ivTogglePassword.setImageResource(R.drawable.baseline_remove_red_eye_24);
        } else {
            binding.etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            binding.ivTogglePassword.setImageResource(R.drawable.baseline_visibility_off_24);
        }
        isPasswordVisible = !isPasswordVisible;
        binding.etPassword.setSelection(binding.etPassword.getText().length());
    }

    private void toggleConfirmPasswordVisibility() {
        if (isConfirmPasswordVisible) {
            binding.etConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            binding.ivToggleConfirmPassword.setImageResource(R.drawable.baseline_remove_red_eye_24);
        } else {
            binding.etConfirmPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            binding.ivToggleConfirmPassword.setImageResource(R.drawable.baseline_visibility_off_24);
        }
        isConfirmPasswordVisible = !isConfirmPasswordVisible;
        binding.etConfirmPassword.setSelection(binding.etConfirmPassword.getText().length());
    }

    public void finishView(View view){
        finish();
    }

    private void registerUser() {
        if(isUsernameExists){
            Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show();
            return;
        }

        String name = binding.etName.getText().toString().trim();
        String semester = binding.spinnerSemester.getSelectedItem().toString().trim();
        String dept = binding.spinnerDepartment.getSelectedItem().toString().trim();
        String batch = binding.spinnerBatch.getSelectedItem().toString().trim();
        String username = binding.etUsername.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();
        String phone = binding.etPhone.getText().toString().trim();
        String address = binding.etAddress.getText().toString().trim();
        String password = binding.etPassword.getText().toString();
        String confirmPassword = binding.etConfirmPassword.getText().toString();

        if (name.isEmpty() || username.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }
        if(password.length() < 8){
            Toast.makeText(this, "Please enter at least 8 digits password.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(batch.equals("Select Batch")){
            Toast.makeText(context, "Please select batch.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(semester.equals("Select Semester")){
            Toast.makeText(context, "Please select semester.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(dept.equals("Select Department")){
            Toast.makeText(context, "Please select department.", Toast.LENGTH_SHORT).show();
            return;
        }

        Util.show(context, "Creating account.");
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    binding.btnRegister.setEnabled(true);
                    binding.btnRegister.setText("Register");

                    if (task.isSuccessful()) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null) {
                                String uid = FirebaseAuth.getInstance().getUid();
                                User newUser = new User(name, email, username, address, phone,dept,semester,batch);
                                newUser.setUid(uid);

                                FirebaseDatabase.getInstance().getReference("Users").child(uid).setValue(newUser)
                                        .addOnSuccessListener(aVoid -> {
                                            user.sendEmailVerification();
                                            Toast.makeText(this, "Registration successful! Please verify your email.", Toast.LENGTH_LONG).show();
                                            Util.dismiss();
                                            startActivity(new Intent(this, LoginActivity.class));
                                            finish();
                                        })
                                        .addOnFailureListener(e ->{
                                                    Util.dismiss();
                                                    Toast.makeText(this, "Failed to save user: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                                }
                                        );

                        }else{
                            Util.dismiss();
                        }
                    } else {
                        Util.dismiss();
                        Toast.makeText(this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });


        Toast.makeText(this, "User registered successfully!", Toast.LENGTH_SHORT).show();
    }



    private void setupAdapters() {
        batchAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, batchList);
        departmentAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, departmentList);
        semesterAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, semesterList);

        binding.spinnerBatch.setAdapter(batchAdapter);
        binding.spinnerDepartment.setAdapter(departmentAdapter);
        binding.spinnerSemester.setAdapter(semesterAdapter);

        setupSpinnerListener(binding.spinnerBatch);
        setupSpinnerListener(binding.spinnerDepartment);
        setupSpinnerListener(binding.spinnerSemester);
    }

    private void fetchBatches() {
        databaseReference.child("Batches").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                batchList.clear();
                batchList.add("Select Batch");
                for (DataSnapshot data : snapshot.getChildren()) {
                    batchList.add(data.child("name").getValue(String.class));
                }
                batchAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void fetchDepartments() {
        databaseReference.child("Departments").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                departmentList.clear();
                departmentList.add("Select Department");
                for (DataSnapshot data : snapshot.getChildren()) {
                    departmentList.add(data.child("name").getValue(String.class));
                }
                departmentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void fetchSemesters() {
        databaseReference.child("Semesters").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                semesterList.clear();
                semesterList.add("Select Semester");
                for (DataSnapshot data : snapshot.getChildren()) {
                    semesterList.add(data.child("name").getValue(String.class));
                }
                semesterAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void setupSpinnerListener(Spinner spinner) {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }


}