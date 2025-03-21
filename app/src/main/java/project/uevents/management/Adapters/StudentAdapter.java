package project.uevents.management.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import project.uevents.management.Models.User;
import project.uevents.management.databinding.ItemStudentBinding;
import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.ViewHolder> {

    private List<User> studentList;
    Context context;

    public StudentAdapter(List<User> studentList, Context context) {
        this.studentList = studentList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemStudentBinding binding = ItemStudentBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User student = studentList.get(position);
        holder.binding.tvName.setText(student.getName());
        holder.binding.tvEmail.setText(student.getEmail());
        holder.binding.tvBatch.setText("Batch: " + student.getBatch());
        holder.binding.tvSemester.setText("Semester: " + student.getSem());
        holder.binding.tvDepartment.setText("Dept: " + student.getDept());

        if(student.isOrganizer()){
            holder.binding.tvOrganizer.setText("Organizer: YES");
        }else{
            holder.binding.tvOrganizer.setText("Organizer: NO");
        }

        holder.itemView.setOnClickListener(v -> showOrganizerPopup(student));

    }
    private void showOrganizerPopup(User student) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Change Organizer Status");
        builder.setMessage("Do you want to change the organizer status for " + student.getName() + "?");

        builder.setPositiveButton(student.isOrganizer() ? "Remove as Organizer" : "Make Organizer", (dialog, which) -> {
            boolean newStatus = !student.isOrganizer();
            updateOrganizerStatus(student, newStatus);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void updateOrganizerStatus(User student, boolean newStatus) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(student.getUid());
        userRef.child("isOrganizer").setValue(newStatus).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                student.setOrganizer(newStatus);
                notifyDataSetChanged();
                Toast.makeText(context, "Organizer status updated", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Failed to update", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public int getItemCount() {
        return studentList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemStudentBinding binding;

        public ViewHolder(ItemStudentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
