package project.uevents.management.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import project.uevents.management.Models.Event;
import project.uevents.management.R;
import project.uevents.management.Utils.GlobalUser;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    private Context context;
    private ArrayList<Event> eventList;

    public EventAdapter(Context context, ArrayList<Event> eventList) {
        this.context = context;
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.title.setText(event.getTitle());
        holder.description.setText(event.getDesc());
        holder.date.setText("Date: " + event.getEventDate());
        holder.time.setText("Time: " + event.getEventStartTime());
        holder.duration.setText("Duration: " + event.getEventDuration());

        if (event.getParticipatedStudentsList() != null && !event.getParticipatedStudentsList().isEmpty()){
            holder.btnRequestBooking.setBackgroundResource(R.drawable.applybtn);
            holder.btnRequestBooking.setTextColor(ContextCompat.getColor(context, R.color.green));
            holder.btnRequestBooking.setText("Already Applied");
            holder.btnRequestBooking.setEnabled(false);
        }

        if (event.getStudentId().equals(FirebaseAuth.getInstance().getUid())){
            holder.btnRequestBooking.setBackgroundResource(R.drawable.applybtn);
            holder.btnRequestBooking.setTextColor(ContextCompat.getColor(context, R.color.green));
            holder.btnRequestBooking.setText("Your Event");
            holder.btnRequestBooking.setEnabled(false);
        }

        if (event.getImageUrl() != null && !event.getImageUrl().isEmpty()) {
            Glide.with(context).load(event.getImageUrl()).into(holder.eventImage);
        }

        holder.btnRequestBooking.setOnClickListener(v -> {
            String currentUserId = GlobalUser.getInstance().getCurrentUser().getUid();
            DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference("Events")
                    .child(event.getEventId()).child("participatedStudentsList");

            eventRef.child(currentUserId).setValue(false).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(context, "Booking Request Sent!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Failed to Request Booking!", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, date, time, duration;
        ImageView eventImage;
        Button btnRequestBooking;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvEventTitle);
            description = itemView.findViewById(R.id.tvEventDesc);
            date = itemView.findViewById(R.id.tvEventDate);
            time = itemView.findViewById(R.id.tvEventTime);
            duration = itemView.findViewById(R.id.tvEventDuration);
            eventImage = itemView.findViewById(R.id.ivEventImage);
            btnRequestBooking = itemView.findViewById(R.id.btnRequestBooking);
        }
    }
}
