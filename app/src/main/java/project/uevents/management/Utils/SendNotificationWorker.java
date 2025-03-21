package project.uevents.management.Utils;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import okhttp3.*;
import project.uevents.management.FCM.FcmNotificationsSender;
import project.uevents.management.Models.Notification;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class SendNotificationWorker extends Worker {

    public SendNotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String eventTitle = getInputData().getString("title");
        String eventDesc = getInputData().getString("desc");

        if (eventTitle == null || eventDesc == null) return Result.failure();

        DatabaseReference studentsRef = FirebaseDatabase.getInstance().getReference("Users");

        studentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot studentSnapshot : snapshot.getChildren()) {
                    if (Objects.equals(studentSnapshot.getKey(), FirebaseAuth.getInstance().getUid())){
                        continue;
                    }
                    String fcmToken = studentSnapshot.child("fcmToken").getValue(String.class);
                    if (fcmToken != null && !fcmToken.isEmpty()) {
                        FcmNotificationsSender sender
                                = new FcmNotificationsSender(
                                        fcmToken,eventTitle,eventDesc,getApplicationContext()
                        );
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMMM-yyyy hh:mm a");
                        String formattedDate = sdf.format(new Date());
                        Notification notification = new Notification(
                                eventTitle, eventDesc, formattedDate
                        );
                        FirebaseDatabase.getInstance().getReference("Users")
                                        .child(Objects.requireNonNull(studentSnapshot.getKey()))
                                                .child("Notifications")
                                                        .child(formattedDate).setValue(notification);
                        sender.SendNotifications();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        return Result.success();
    }
}
