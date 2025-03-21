package project.uevents.management.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

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
import project.uevents.management.R;
import project.uevents.management.Util;
import project.uevents.management.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {
    FragmentProfileBinding binding;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);


        context = requireContext();
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
                        Toast.makeText(context, "Profile updated", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(context, "Failed to update", Toast.LENGTH_SHORT).show()
                    );
        });

        return binding.getRoot();
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
        batchAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, batchList);
        departmentAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, departmentList);
        semesterAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, semesterList);

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
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}