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
import project.uevents.management.Adapters.SemesterAdapter;
import project.uevents.management.Models.Semester;
import project.uevents.management.R;
import project.uevents.management.databinding.ActivityAdminSemestersBinding;
import java.util.ArrayList;
import java.util.List;

public class AdminSemestersActivity extends AppCompatActivity {

    private ActivityAdminSemestersBinding binding;
    private DatabaseReference databaseReference;
    private List<Semester> semesterList;
    private SemesterAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminSemestersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseReference = FirebaseDatabase.getInstance().getReference("Semesters");
        semesterList = new ArrayList<>();

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SemesterAdapter(semesterList, this::showEditDeletePopup);
        binding.recyclerView.setAdapter(adapter);

        binding.btnAddSemester.setOnClickListener(v -> showAddPopup());
        fetchSemesters();
    }

    private void fetchSemesters() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    binding.recyclerView.setVisibility(View.VISIBLE);
                    binding.empty.setVisibility(View.GONE);
                    semesterList.clear();
                    for (DataSnapshot data : snapshot.getChildren()) {
                        Semester semester = data.getValue(Semester.class);
                        semesterList.add(semester);
                    }
                    adapter.notifyDataSetChanged();

                }else{
                    binding.recyclerView.setVisibility(View.GONE);
                    binding.empty.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminSemestersActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_semester, null);
        EditText edtName = view.findViewById(R.id.edtSemesterName);
        Button btnSave = view.findViewById(R.id.btnSaveSemester);

        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();

        btnSave.setOnClickListener(v -> {
            String name = edtName.getText().toString().trim();
            if (!name.isEmpty()) {
                String id = databaseReference.push().getKey();
                Semester semester = new Semester(id, name);
                databaseReference.child(id).setValue(semester)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Semester added", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        });
            } else {
                Toast.makeText(this, "Enter semester name", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEditDeletePopup(Semester semester) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit or Delete");
        builder.setMessage("Choose an action");

        builder.setPositiveButton("Edit", (dialog, which) -> showEditPopup(semester));
        builder.setNegativeButton("Delete", (dialog, which) -> deleteSemester(semester));

        builder.create().show();
    }

    private void showEditPopup(Semester semester) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_semester, null);
        EditText edtName = view.findViewById(R.id.edtSemesterName);
        Button btnSave = view.findViewById(R.id.btnSaveSemester);

        edtName.setText(semester.getName());
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();

        btnSave.setOnClickListener(v -> {
            String newName = edtName.getText().toString().trim();
            if (!newName.isEmpty()) {
                databaseReference.child(semester.getId()).child("name").setValue(newName)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Semester updated", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        });
            } else {
                Toast.makeText(this, "Enter semester name", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteSemester(Semester semester) {
        databaseReference.child(semester.getId()).removeValue()
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Semester deleted", Toast.LENGTH_SHORT).show());
    }
    public void finishView(View view){
        finish();
    }
}
