package com.example.noticeboard10;

import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Helper {
    FirebaseAuth auth;
    String id = "";
    String mKey=null;

    protected String getPushKey(DatabaseReference mydatabase) {
        try {
            id = mydatabase.push().getKey();
        } catch (Exception e) {
            String s = "";
            return s;
        }
        return id;


    }

    protected String getMKey(final String uid) {

        DatabaseReference mydatabase;
        mydatabase=FirebaseDatabase.getInstance().getReference("Data");
        mydatabase.child("Admins")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot  item_snapshot : dataSnapshot.getChildren()) {
                            Admin a=item_snapshot.getValue(Admin.class);
                            if(a.uid==uid)
                            {
                                mKey=a.mKey;
                                System.out.println("mkey is"+mKey);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
        System.out.println("i am writining"+mKey);
        return mKey;

    }
}
