package com.example.noticeboard10;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

//
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//

public class AdminManagementFragment extends Fragment {

    FirebaseAuth auth;
    DatabaseReference mydatabase;
    ImageButton add_admin, remove_admin;
    EditText edit_text_uid;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.manage_admin, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        auth = FirebaseAuth.getInstance();
        mydatabase = FirebaseDatabase.getInstance().getReference();
        add_admin = (ImageButton) view.findViewById(R.id.add_admin_button);
        remove_admin = (ImageButton) view.findViewById(R.id.remove_admin_button);
        edit_text_uid = (EditText) view.findViewById(R.id.ET_UID);

        add_admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edit_text_uid.getText().toString().trim().equals("")) {
                    Toast.makeText(getActivity(), "Enter valid UID", Toast.LENGTH_LONG).show();
                } else {
                    final String key = edit_text_uid.getText().toString();
                    //i am using uid as a unique key for firebase entry
                    mydatabase.child("Admin").child(key).setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getActivity(), "Admin Added", Toast.LENGTH_LONG).show();
                            add_admin_to_db(key); //key is uid of user which going to be admin
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), "Invalid Operation", Toast.LENGTH_LONG).show();
                        }
                    });
                }
                edit_text_uid.setText("");
            }

        });

        remove_admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edit_text_uid.getText().toString().trim().equals("")) {
                    Toast.makeText(getActivity(), "Enter valid UID", Toast.LENGTH_LONG).show();
                } else {
                    final String key = edit_text_uid.getText().toString(); //here key id UID of user to be remove

                    //i am using uid as a unique key for firebase entry
                    mydatabase.child("Admin").child(key).removeValue().addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), "Invalid Operation", Toast.LENGTH_LONG).show();

                        }
                    }).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {


                            Helper help = new Helper();
                            String mKey = help.getMKey(key); //key is uid of user that has to be remove
                            Toast.makeText(getActivity(), mKey, Toast.LENGTH_SHORT).show();
                            if (mKey !=null) {
                                remove_admin_from_db(mKey);
                            }
                            else {
                                Toast.makeText(getActivity().getApplicationContext(), "mkey is null", Toast.LENGTH_LONG).show();
                            }


                        }
                    });

                }
                edit_text_uid.setText("");
            }

        });


    }

    protected void add_admin_to_db(String uid) {

        String mKey = mydatabase.push().getKey();
        Admin a = new Admin(mKey, uid);
        mydatabase.child("Data").child("Admins").child(mKey).setValue(a).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity().getApplicationContext(), "Admin Exist", Toast.LENGTH_LONG).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getActivity().getApplicationContext(), "Admin Added", Toast.LENGTH_LONG).show();
            }
        });



    }

    protected void remove_admin_from_db(String mKey) {
        Toast.makeText(getActivity().getApplicationContext(), mKey, Toast.LENGTH_LONG).show();
        mydatabase.child("Data").child("Admins").child(mKey).removeValue().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity().getApplicationContext(), "Unknown Error", Toast.LENGTH_LONG).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getActivity().getApplicationContext(), "Admin Removed from db", Toast.LENGTH_LONG).show();
            }
        });


    }


}
