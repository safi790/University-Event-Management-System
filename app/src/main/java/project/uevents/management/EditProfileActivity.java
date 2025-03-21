package project.uevents.management;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
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

import project.uevents.management.databinding.ActivityEditProfileBinding;
import project.uevents.management.Models.User;

public class EditProfileActivity extends AppCompatActivity {
    private ActivityEditProfileBinding binding;
    private DatabaseReference userRef;
    private FirebaseUser firebaseUser;
    Context context;
    boolean isUsernameExists = false;
    String userName = "";
    private DatabaseReference databaseReference;
    private List<String> batchList = new ArrayList<>();
    private List<String> departmentList = new ArrayList<>();
    private List<String> semesterList = new ArrayList<>();
    private ArrayAdapter<String> batchAdapter, departmentAdapter, semesterAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Window window = getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        context = this;
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userRef = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        databaseReference = FirebaseDatabase.getInstance().getReference();

        setupAdapters();
        fetchBatches();

        binding.etUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().equals(userName)){
                    isUsernameExists = false;
                    return;
                }
                checkUsernameAvailable(editable.toString());
            }
        });

        binding.btnSave.setOnClickListener(v -> {
            if (isUsernameExists){
                Toast.makeText(context, "Username already in user", Toast.LENGTH_SHORT).show();
                return;
            }
            String name = binding.etName.getText().toString().trim();
            String username = binding.etUsername.getText().toString().trim();
            String address = binding.etAddress.getText().toString().trim();
            String phone = binding.etPhone.getText().toString().trim();
            String semester = binding.spinnerSemester.getSelectedItem().toString().trim();
            String dept = binding.spinnerDepartment.getSelectedItem().toString().trim();
            String batch = binding.spinnerBatch.getSelectedItem().toString().trim();

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

            userRef.child("name").setValue(name);
            userRef.child("username").setValue(username);
            userRef.child("address").setValue(address);
            userRef.child("sem").setValue(semester);
            userRef.child("batch").setValue(batch);
            userRef.child("dept").setValue(dept);
            userRef.child("phone").setValue(phone)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Failed to update", Toast.LENGTH_SHORT).show()
                    );
        });
    }
    public void finishView(View view){
        finish();
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
        databaseReference.child("Batches").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                batchList.clear();
                batchList.add("Select Batch");
                for (DataSnapshot data : snapshot.getChildren()) {
                    batchList.add(data.child("name").getValue(String.class));
                }
                batchAdapter.notifyDataSetChanged();
                fetchDepartments();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void fetchDepartments() {
        databaseReference.child("Departments").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                departmentList.clear();
                departmentList.add("Select Department");
                for (DataSnapshot data : snapshot.getChildren()) {
                    departmentList.add(data.child("name").getValue(String.class));
                }
                departmentAdapter.notifyDataSetChanged();
                fetchSemesters();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void fetchSemesters() {
        databaseReference.child("Semesters").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot sp) {
                semesterList.clear();
                semesterList.add("Select Semester");
                for (DataSnapshot data : sp.getChildren()) {
                    semesterList.add(data.child("name").getValue(String.class));
                }
                semesterAdapter.notifyDataSetChanged();
                userRef.get().addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        User user = snapshot.getValue(User.class);
                        if (user != null) {
                            userName = user.getUsername();
                            binding.etName.setText(user.getName());
                            binding.etUsername.setText(user.getUsername());
                            binding.etAddress.setText(user.getAddress());
                            binding.etPhone.setText(user.getPhone());
                            if (user.getSem() != null) {
                                int position = semesterList.indexOf(user.getSem());
                                if (position >= 0) {
                                    binding.spinnerSemester.setSelection(position);
                                }
                            }
                            if (user.getDept() != null) {
                                int position = departmentList.indexOf(user.getDept());
                                if (position >= 0) {
                                    binding.spinnerDepartment.setSelection(position);
                                }
                            }
                            if (user.getBatch() != null) {
                                int position = batchList.indexOf(user.getBatch());
                                if (position >= 0) {
                                    binding.spinnerBatch.setSelection(position);
                                }
                            }
                        }
                    }
                });
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
