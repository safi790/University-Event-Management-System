package project.uevents.management.Admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import project.uevents.management.Adapters.DepartmentAdapter;
import project.uevents.management.Models.Department;
import project.uevents.management.R;
import project.uevents.management.databinding.ActivityAdminDepartmentsBinding;
import java.util.ArrayList;
import java.util.List;

public class AdminDepartmentsActivity extends AppCompatActivity {

    private ActivityAdminDepartmentsBinding binding;
    private DatabaseReference databaseReference;
    private List<Department> departmentList;
    private DepartmentAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminDepartmentsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseReference = FirebaseDatabase.getInstance().getReference("Departments");
        departmentList = new ArrayList<>();

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DepartmentAdapter(departmentList, this::showEditDeletePopup);
        binding.recyclerView.setAdapter(adapter);

        binding.btnAddDepartment.setOnClickListener(v -> showAddPopup());
        fetchDepartments();
    }

    private void fetchDepartments() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){
                    binding.recyclerView.setVisibility(View.VISIBLE);
                    binding.empty.setVisibility(View.GONE);
                    departmentList.clear();
                    for (DataSnapshot data : snapshot.getChildren()) {
                        Department department = data.getValue(Department.class);
                        departmentList.add(department);
                    }
                    adapter.notifyDataSetChanged();

                }else{
                    binding.recyclerView.setVisibility(View.GONE);
                    binding.empty.setVisibility(View.VISIBLE);
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminDepartmentsActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_department, null);
        EditText edtName = view.findViewById(R.id.edtDepartmentName);
        Button btnSave = view.findViewById(R.id.btnSaveDepartment);

        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();

        btnSave.setOnClickListener(v -> {
            String name = edtName.getText().toString().trim();
            if (!name.isEmpty()) {
                String id = databaseReference.push().getKey();
                Department department = new Department(id, name);
                databaseReference.child(id).setValue(department)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Department added", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        });
            } else {
                Toast.makeText(this, "Enter department name", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEditDeletePopup(Department department) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit or Delete");
        builder.setMessage("Choose an action");

        builder.setPositiveButton("Edit", (dialog, which) -> showEditPopup(department));
        builder.setNegativeButton("Delete", (dialog, which) -> deleteDepartment(department));

        builder.create().show();
    }

    private void showEditPopup(Department department) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_department, null);
        EditText edtName = view.findViewById(R.id.edtDepartmentName);
        Button btnSave = view.findViewById(R.id.btnSaveDepartment);

        edtName.setText(department.getName());
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();

        btnSave.setOnClickListener(v -> {
            String newName = edtName.getText().toString().trim();
            if (!newName.isEmpty()) {
                databaseReference.child(department.getId()).child("name").setValue(newName)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Department updated", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        });
            } else {
                Toast.makeText(this, "Enter department name", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteDepartment(Department department) {
        databaseReference.child(department.getId()).removeValue()
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Department deleted", Toast.LENGTH_SHORT).show());
    }

    public void finishView(View view){
        finish();
    }
}
