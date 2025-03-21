package project.uevents.management;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.google.firebase.FirebaseApp;

import project.uevents.management.Utils.GlobalUser;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        GlobalUser.getInstance();
        createNotificationChannel();
        FirebaseApp.initializeApp(this);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel channel1 = new NotificationChannel("BroadCast", "BroadCast", NotificationManager.IMPORTANCE_HIGH);
            channel1.setDescription("This channel responsible for broadcasts");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
        }
    }
}
