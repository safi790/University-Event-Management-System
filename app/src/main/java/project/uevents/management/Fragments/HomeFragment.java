package project.uevents.management.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import project.uevents.management.Adapters.EventAdapter;
import project.uevents.management.Models.Event;
import project.uevents.management.Models.User;
import project.uevents.management.R;
import project.uevents.management.Utils.GlobalUser;

public class HomeFragment extends Fragment {
    private RecyclerView recyclerView;
    private ImageView empty;
    private EventAdapter eventAdapter;
    private ArrayList<Event> eventList;
    private TextView tvWelcome;

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        GlobalUser.getInstance();

        tvWelcome = view.findViewById(R.id.tvWelcome);
        empty = view.findViewById(R.id.empty);
        recyclerView = view.findViewById(R.id.recyclerViewEvents);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        eventList = new ArrayList<>();
        eventAdapter = new EventAdapter(getContext(), eventList);
        recyclerView.setAdapter(eventAdapter);

        loadUserData();

        loadEvents();
        return view;
    }

    private void loadUserData() {
        new Thread(() -> {
            while (GlobalUser.getInstance().getCurrentUser() == null) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if (isAdded() && getActivity() != null) {
                requireActivity().runOnUiThread(() -> {
                    User user = GlobalUser.getInstance().getCurrentUser();
                    tvWelcome.setText("Welcome, " + user.getName() + "!");
                });
            }
        }).start();

    }

    private void loadEvents() {
        DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference("Events");
        eventRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    recyclerView.setVisibility(View.VISIBLE);
                    empty.setVisibility(View.GONE);

                    eventList.clear();
                    for (DataSnapshot eventSnapshot : snapshot.getChildren()) {
                        Event event = eventSnapshot.getValue(Event.class);
                        if (event != null) {
                            eventList.add(event);
                        }
                    }
                    Collections.reverse(eventList);
                    eventAdapter.notifyDataSetChanged();

                }else{
                    recyclerView.setVisibility(View.GONE);
                    empty.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load events", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
