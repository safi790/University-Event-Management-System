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
import project.uevents.management.Adapters.BatchAdapter;
import project.uevents.management.Models.Batch;
import project.uevents.management.R;
import project.uevents.management.databinding.ActivityAdminBatchesBinding;
import java.util.ArrayList;
import java.util.List;

public class AdminBatchesActivity extends AppCompatActivity {

    private ActivityAdminBatchesBinding binding;
    private DatabaseReference databaseReference;
    private List<Batch> batchList;
    private BatchAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminBatchesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseReference = FirebaseDatabase.getInstance().getReference("Batches");
        batchList = new ArrayList<>();

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BatchAdapter(batchList, this::showEditDeletePopup);
        binding.recyclerView.setAdapter(adapter);

        binding.btnAddBatch.setOnClickListener(v -> showAddPopup());
        fetchBatches();
    }

    private void fetchBatches() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    binding.recyclerView.setVisibility(View.VISIBLE);
                    binding.empty.setVisibility(View.GONE);

                    batchList.clear();
                    for (DataSnapshot data : snapshot.getChildren()) {
                        Batch batch = data.getValue(Batch.class);
                        batchList.add(batch);
                    }
                    adapter.notifyDataSetChanged();
                }else{
                    binding.recyclerView.setVisibility(View.GONE);
                    binding.empty.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminBatchesActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_batch, null);
        EditText edtName = view.findViewById(R.id.edtBatchName);
        Button btnSave = view.findViewById(R.id.btnSaveBatch);

        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();

        btnSave.setOnClickListener(v -> {
            String name = edtName.getText().toString().trim();
            if (!name.isEmpty()) {
                String id = databaseReference.push().getKey();
                Batch batch = new Batch(id, name);
                databaseReference.child(id).setValue(batch)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Batch added", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        });
            } else {
                Toast.makeText(this, "Enter batch name", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEditDeletePopup(Batch batch) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit or Delete");
        builder.setMessage("Choose an action");

        builder.setPositiveButton("Edit", (dialog, which) -> showEditPopup(batch));
        builder.setNegativeButton("Delete", (dialog, which) -> deleteBatch(batch));

        builder.create().show();
    }

    private void showEditPopup(Batch batch) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_batch, null);
        EditText edtName = view.findViewById(R.id.edtBatchName);
        Button btnSave = view.findViewById(R.id.btnSaveBatch);

        edtName.setText(batch.getName());
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();

        btnSave.setOnClickListener(v -> {
            String newName = edtName.getText().toString().trim();
            if (!newName.isEmpty()) {
                databaseReference.child(batch.getId()).child("name").setValue(newName)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Batch updated", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        });
            } else {
                Toast.makeText(this, "Enter batch name", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteBatch(Batch batch) {
        databaseReference.child(batch.getId()).removeValue()
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Batch deleted", Toast.LENGTH_SHORT).show());
    }
    public void finishView(View view){
        finish();
    }
}
