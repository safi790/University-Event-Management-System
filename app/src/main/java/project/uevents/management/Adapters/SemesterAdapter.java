package project.uevents.management.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import project.uevents.management.Models.Semester;
import project.uevents.management.R;
import java.util.List;

public class SemesterAdapter extends RecyclerView.Adapter<SemesterAdapter.ViewHolder> {

    private List<Semester> semesterList;
    private OnSemesterClickListener listener;

    public interface OnSemesterClickListener {
        void onSemesterClick(Semester semester);
    }

    public SemesterAdapter(List<Semester> semesterList, OnSemesterClickListener listener) {
        this.semesterList = semesterList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_semester, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Semester semester = semesterList.get(position);
        holder.txtName.setText(semester.getName());

        holder.itemView.setOnClickListener(v -> listener.onSemesterClick(semester));
    }

    @Override
    public int getItemCount() {
        return semesterList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName;

        public ViewHolder(View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtSemesterName);
        }
    }
}
