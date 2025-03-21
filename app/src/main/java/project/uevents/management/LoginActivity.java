package project.uevents.management;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import project.uevents.management.Admin.AdminMainActivity;
import project.uevents.management.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;
    Context context;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Window window = getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        context = this;
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null){
            if (Objects.requireNonNull(Objects.requireNonNull(auth.getCurrentUser()).getEmail())
                    .equalsIgnoreCase("admin@gmail.com")){
                startActivity(new Intent(context, AdminMainActivity.class));
                finish();
            }else{
                startActivity(new Intent(context, MainActivity.class));
                finish();
            }
        }

        binding.ivTogglePassword.setOnClickListener(view -> {
            if (binding.etPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
                binding.etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                binding.ivTogglePassword.setImageResource(R.drawable.baseline_visibility_off_24);
            } else {
                binding.etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                binding.ivTogglePassword.setImageResource(R.drawable.baseline_remove_red_eye_24);
            }
            binding.etPassword.setSelection(binding.etPassword.getText().length()); // Move cursor to end
        });

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(binding.etUsername.getText().toString().equalsIgnoreCase("iamadmin")){
                    auth.signInWithEmailAndPassword("admin@gmail.com", binding.etPassword.getText().toString().trim()).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            if (user != null) {
                                Toast.makeText(context, "Login Successful!", Toast.LENGTH_SHORT).show();
                                Util.dismiss();
                                startActivity(new Intent(context, AdminMainActivity.class));
                                finish();
                            }
                        } else {
                            Util.dismiss();
                            Toast.makeText(context, "Login Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    loginUser();
                }
            }
        });
        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, RegisterActivity.class));
            }
        });
    }

    public void finishView(View view){
        finish();
    }

    private void loginUser() {
        String userName = binding.etUsername.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();
        final String[] email = {""};
        Util.show(context, "Signing in.");
        FirebaseDatabase.getInstance().getReference("Users")
                .orderByChild("username").equalTo(userName)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            for (DataSnapshot ds: snapshot.getChildren()){
                                email[0] = ds.child("email").getValue(String.class);
                                Log.e("ffff", "onDataChange: " + email[0]);
                                Log.e("ffff", "onDataChange: OBJECT - " + ds.getKey());
                                break;
                            }
                            if (email[0].isEmpty() || password.isEmpty()) {
                                Toast.makeText(context, "Please enter valid username", Toast.LENGTH_SHORT).show();
                                Util.dismiss();
                                return;
                            }

                            auth.signInWithEmailAndPassword(email[0], password).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = auth.getCurrentUser();
                                    if (user != null) {
                                        if (!user.isEmailVerified()){
                                            user.sendEmailVerification();
                                            Toast.makeText(context, "Your email not verified, check your inbox and verify your email first.", Toast.LENGTH_LONG).show();
                                            return;
                                        }
                                        Toast.makeText(context, "Login Successful!", Toast.LENGTH_SHORT).show();
                                        Util.dismiss();
                                        startActivity(new Intent(context, MainActivity.class)); // Redirect to Home
                                        finish();
                                    }
                                } else {
                                    Util.dismiss();
                                    Toast.makeText(context, "Login Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Util.dismiss();
                    }
                });
    }
}
