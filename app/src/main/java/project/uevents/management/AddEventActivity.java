package project.uevents.management;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import project.uevents.management.Models.Event;
import project.uevents.management.Utils.GlobalUser;
import project.uevents.management.Utils.SendNotificationWorker;
import project.uevents.management.databinding.ActivityAddEventBinding;

public class AddEventActivity extends AppCompatActivity {
    private ActivityAddEventBinding binding;
    private DatabaseReference eventRef;
    private ActivityResultLauncher<String> photoPickerLauncher;
    boolean isImage = false;
    Uri uri = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddEventBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        eventRef = FirebaseDatabase.getInstance().getReference("Events");

        binding.btnSelectDate.setOnClickListener(v -> selectDate());
        binding.btnSelectTime.setOnClickListener(v -> selectTime());
        binding.btnCreateEvent.setOnClickListener(v -> createEvent());


        // Initialize the ActivityResultLauncher inside onViewCreated
        photoPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        if (result != null) {
                            binding.profileImage.setVisibility(View.VISIBLE);
                            binding.profileImage.setImageURI(result);
                        }
                    }
                });

        binding.uploadPhotoBtn.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // Launch photo picker for Android 13+
                photoPickerLauncher.launch("image/*");
            } else {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, 200);
            }
        });
    }

    private void selectDate() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                    binding.etEventDate.setText(selectedDate);
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void selectTime() {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minute) -> {
                    String selectedTime = hourOfDay + ":" + (minute < 10 ? "0" + minute : minute);
                    binding.etEventStartTime.setText(selectedTime);
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        timePickerDialog.show();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 200 && resultCode == AddEventActivity.this.RESULT_OK) {
            Uri imageUri = data.getData();
            binding.profileImage.setVisibility(View.VISIBLE);
            binding.profileImage.setImageURI(imageUri);
            uri = imageUri;
            isImage = true;
        }else{
            binding.profileImage.setVisibility(View.GONE);
            uri = null;
            isImage = false;
        }
    }
    private void createEvent() {
        String title = binding.etTitle.getText().toString().trim();
        String desc = binding.etDesc.getText().toString().trim();
        String eventDate = binding.etEventDate.getText().toString().trim();
        String eventStartTime = binding.etEventStartTime.getText().toString().trim();
        String eventDuration = binding.etDuration.getText().toString().trim();

        if (title.isEmpty() || desc.isEmpty() || eventDate.isEmpty() || eventStartTime.isEmpty() || eventDuration.isEmpty()) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        String eventId = eventRef.push().getKey();
        String studentId = GlobalUser.getInstance().getCurrentUser().getUid();
        String imageUrl = "";

        Event event = new Event(eventId,title, desc, studentId, imageUrl, eventDate, eventDuration, eventStartTime);

        Map<String, Object> eventMap = new HashMap<>();
        eventMap.put(eventId, event);

        eventRef.updateChildren(eventMap).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (isImage){
                Util.uploadImage(this,uri,eventRef.child(eventId).child("imageUrl"));
            }

                Toast.makeText(this, "Event Created!", Toast.LENGTH_SHORT).show();



                WorkManager workManager = WorkManager.getInstance(this);
                Data data = new Data.Builder()
                        .putString("title", title)
                        .putString("desc", desc)
                        .build();

                OneTimeWorkRequest notificationWork = new OneTimeWorkRequest.Builder(SendNotificationWorker.class)
                        .setInputData(data)
                        .build();

                workManager.enqueue(notificationWork);



                startActivity(new Intent(AddEventActivity.this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Failed to create event!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
