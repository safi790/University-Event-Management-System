package project.uevents.management;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import project.uevents.management.Models.User;
import project.uevents.management.databinding.ActivitySplashScreenBinding;

@SuppressLint("CustomSplashScreen")
public class SplashScreen extends AppCompatActivity {
    ActivitySplashScreenBinding binding;
    Context context;
    private FirebaseAuth firebaseAuth;
    private static final int RC_SIGN_IN = 100;
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Log.d("Permission", "Notification permission granted.");
                } else {
                    Log.d("Permission", "Notification permission denied.");
                }
            });

    private GoogleSignInClient googleSignInClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;

        Util.fadeIn(binding.logo, context);
        Util.slideUp(binding.appName, context);
        firebaseAuth = FirebaseAuth.getInstance();

        configureGoogleSignIn();
        binding.googleSignInButton.setOnClickListener(view -> signInWithGoogle());

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null){
                    if (!user.isEmailVerified()){
                        user.sendEmailVerification();
                        Toast.makeText(context, "Your email not verified, check your inbox and verify your email first.", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(context, LoginActivity.class));
                        finish();
                        return;
                    }
                    startActivity(new Intent(context, MainActivity.class));
                    finish();
                    return;
                }
                binding.splash.setVisibility(View.GONE);
                Util.fadeIn(binding.welcome, context);
                binding.welcome.setVisibility(View.VISIBLE);
            }
        }, 3000);

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, LoginActivity.class));
                finish();
            }
        });
        binding.btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, RegisterActivity.class));
                finish();
            }
        });


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        checkAndRequestNotificationPermission();

    }




    private void configureGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    // Start Google Sign-In Intent
    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    // Handle Google Sign-In Result
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    firebaseAuthWithGoogle(account);
                }
            } catch (ApiException e) {
                Log.e("Google Sign-In", "Sign-in failed", e);
                Toast.makeText(this, "Google sign-in failed!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            FirebaseDatabase.getInstance().getReference("Users")
                                            .orderByChild("email").equalTo(firebaseUser.getEmail())
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if (snapshot.exists()){
                                                        Toast.makeText(context, "Login successful!", Toast.LENGTH_LONG).show();
                                                        startActivity(new Intent(context, MainActivity.class));
                                                        finish();
                                                    }else{
                                                        saveUserToDatabase(firebaseUser);
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                        }
                    } else {
                        Log.e("Firebase Auth", "Sign-in failed", task.getException());
                        Toast.makeText(context, "Authentication Failed!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserToDatabase(FirebaseUser firebaseUser) {
        String uid = firebaseUser.getUid();
        String name = firebaseUser.getDisplayName();
        String email = firebaseUser.getEmail();
        String phone = firebaseUser.getPhoneNumber();
        String address = "";
        String username = email.split("@")[0];

        User user = new User(name, email, username, address, phone, "", "", "");
        user.setUid(uid);

        FirebaseDatabase.getInstance().getReference("Users").child(uid).setValue(user)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Registration successful!", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to save user: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }

    private void checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13 (API 33) and above
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.d("Permission", "Notification permission already granted.");
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.POST_NOTIFICATIONS)) {
                // Optional: Show a dialog explaining why you need the permission
                Log.d("Permission", "Showing rationale for notification permission.");
                showPermissionRationaleDialog();
            } else {
                // Request permission
                Log.d("Permission", "Requesting notification permission.");
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS);
            }
        } else {
            // No need to request permission below Android 13
            Log.d("Permission", "No need to request notification permission for Android 12 or below.");
        }
    }
    private void showPermissionRationaleDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Notification Permission Required")
                .setMessage("This app requires permission to show notifications.")
                .setPositiveButton("Allow", (dialog, which) -> {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    Log.d("Permission", "User denied the permission rationale dialog.");
                })
                .create()
                .show();
    }
}