package project.uevents.management;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import project.uevents.management.Fragments.HomeFragment;
import project.uevents.management.Fragments.NotificationsFragment;
import project.uevents.management.Fragments.ProfileFragment;
import project.uevents.management.Models.User;
import project.uevents.management.Utils.GlobalUser;
import project.uevents.management.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private User globalUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Window window = getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));


        globalUser = GlobalUser.getInstance().getCurrentUser();

        loadUserData();

        Util.checkAndRequestStoragePermission(this);

        // Set default fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();

        // Bottom Navigation Logic
        binding.bottomNav.setOnItemSelectedListener(this::onNavigationItemSelected);
        checkUserData();
        updateFCMToken();
    }

    private void loadUserData() {
        updateBottomNav();
    }

    private void updateBottomNav() {
        MenuItem addEventItem = binding.bottomNav.getMenu().findItem(R.id.nav_add_event);
        if (globalUser != null && globalUser.isOrganizer()) {
            addEventItem.setVisible(true);
        } else {
            addEventItem.setVisible(false);
        }
    }

    private boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment = null;
        int itemId = item.getItemId();

        if (itemId == R.id.nav_home) {
            selectedFragment = new HomeFragment();
        } else if (itemId == R.id.nav_profile) {
            selectedFragment = new ProfileFragment();
        } else if (itemId == R.id.nav_notifications) {
            selectedFragment = new NotificationsFragment();
        } else if (itemId == R.id.nav_add_event) {
            startActivity(new Intent(MainActivity.this, AddEventActivity.class));
            return true;
        } else if (itemId == R.id.nav_logout) {
            new AlertDialog.Builder(this)
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        finish();
                    })
                    .setNegativeButton("No", null)
                    .show();
            return true;
        }

        if (selectedFragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
        }

        return true;
    }


    private void checkUserData() {
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (globalUser != null && globalUser.isOrganizer()) {
                    binding.bottomNav.getMenu().findItem(R.id.nav_add_event)
                            .setVisible(globalUser.isOrganizer());
                } else {
                    handler.postDelayed(this, 1000);
                }
            }
        };
        handler.post(runnable);
    }

    public static void updateFCMToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.e("FCM", "Fetching FCM token failed", task.getException());
                        return;
                    }

                    // Get new FCM token
                    String token = task.getResult();
                    Log.d("FCM", "FCM Token: " + token);

                    // Update token in Firebase
                    saveTokenToFirebase(token);
                });
    }

    private static void saveTokenToFirebase(String token) {
        String uid = FirebaseAuth.getInstance().getCurrentUser() != null ?
                FirebaseAuth.getInstance().getCurrentUser().getUid() : null;

        if (uid == null) {
            Log.e("FCM", "User is not logged in");
            return;
        }

        DatabaseReference userRef = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(uid)
                .child("fcmToken");

        userRef.setValue(token)
                .addOnSuccessListener(aVoid -> Log.d("FCM", "Token updated successfully"))
                .addOnFailureListener(e -> Log.e("FCM", "Failed to update token", e));
    }



}
