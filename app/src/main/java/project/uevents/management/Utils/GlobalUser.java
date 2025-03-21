package project.uevents.management.Utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import project.uevents.management.Models.User;

public class GlobalUser {
    private static GlobalUser instance;
    private User currentUser;
    private DatabaseReference userRef;
    private ValueEventListener userListener;

    private GlobalUser() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            userRef = FirebaseDatabase.getInstance().getReference("Users").child(uid);

            userListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        currentUser = snapshot.getValue(User.class);
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                }
            };

            userRef.addValueEventListener(userListener);
        }
    }

    public static synchronized GlobalUser getInstance() {
        if (instance == null) {
            instance = new GlobalUser();
        }
        return instance;
    }

    public synchronized User getCurrentUser() {
        return currentUser;
    }
}
