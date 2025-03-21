package project.uevents.management.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import project.uevents.management.Models.Batch;
import project.uevents.management.R;
import java.util.List;

public class BatchAdapter extends RecyclerView.Adapter<BatchAdapter.ViewHolder> {

    private List<Batch> batchList;
    private OnBatchClickListener listener;

    public interface OnBatchClickListener {
        void onBatchClick(Batch batch);
    }

    public BatchAdapter(List<Batch> batchList, OnBatchClickListener listener) {
        this.batchList = batchList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_batch, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Batch batch = batchList.get(position);
        holder.txtName.setText(batch.getName());

        holder.itemView.setOnClickListener(v -> listener.onBatchClick(batch));
    }

    @Override
    public int getItemCount() {
        return batchList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName;

        public ViewHolder(View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtBatchName);
        }
    }

}
