package com.example.noticeboard10;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
public class Show_Message_Fragment extends Fragment {
    private RecyclerView myRecycleview;
    List<Notes> listData;
    MyAdapter adapter;
    FirebaseDatabase FDB;
    DatabaseReference DBR;
    FloatingActionButton refresh;
    private String fullname="";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_show_message, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        refresh=(FloatingActionButton)view.findViewById(R.id.refreshbutton);
        myRecycleview = (RecyclerView)view.findViewById(R.id.myRecycler);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        linearLayoutManager.setStackFromEnd(true); //to show last message in recycler view
        myRecycleview.setLayoutManager(linearLayoutManager);
        myRecycleview.setItemAnimator(new DefaultItemAnimator());
        myRecycleview.addItemDecoration(new DividerItemDecoration(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL));
        myRecycleview.setHasFixedSize(true);
        listData = new ArrayList<>();
        adapter = new MyAdapter(listData); //pass empty listArray of type setget
        FDB = FirebaseDatabase.getInstance();
        GetDataFirebase();
        fullname = GoogleAuthActivity.fullname;
        refresh.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Refresh(view);
            }
        });
    }

    void GetDataFirebase() {

        DBR = FDB.getReference("Data");
        DBR.child("Messages").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                Notes datax = dataSnapshot.getValue(Notes.class);
                //now add data to array list

                if (!dataSnapshot.exists()) {
                    // remove this organization item from the RecyclerView
                    listData.remove(datax);
                }
                listData.add(datax);

                //now add list to adapter
                myRecycleview.setAdapter(adapter);
                //   Toast.makeText(showmsg.this, "Data Added", Toast.LENGTH_SHORT).show();






            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
/*                Notes datax = dataSnapshot.getValue(Notes.class);
                //new add data to array list
                listData.add(datax);

                //now add list to adapter
                myRecycleview.setAdapter(adapter);
                Toast.makeText(getActivity(), "Data Changed", Toast.LENGTH_SHORT).show();
                refresh.setImageDrawable(
                        ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.norefresh));
                refresh.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.red_primary,null)));
       */     }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Notes datax = dataSnapshot.getValue(Notes.class);

                //now add list to adapter
                // myRecycleview.setAdapter(adapter);
                Toast.makeText(getActivity(), "Data Removed", Toast.LENGTH_LONG).show();
                refresh.setImageDrawable(
                        ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.norefresh));
                refresh.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.red_primary,null)));


            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
/*
                listData.clear();
                Notes datax = dataSnapshot.getValue(Notes.class);
                //new add data to array list
                listData.add(datax);

                //now add list to adapter
                myRecycleview.setAdapter(adapter);
                Toast.makeText(getActivity(), "Data Moved", Toast.LENGTH_SHORT).show();
                refresh.setImageDrawable(
                        ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.norefresh));
                refresh.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.red_primary,null)));
                adapter.notifyDataSetChanged();*/
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                refresh.setImageDrawable(
                        ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.norefresh));
                refresh.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.red_primary,null)));
                adapter.notifyDataSetChanged();
            }
        });

    }
    public void Refresh(View view) {
        Intent intent = getActivity().getIntent();
        getActivity().finish();
        startActivity(intent);
        refresh.setBackgroundResource(R.drawable.norefresh);
        refresh.setImageDrawable(
                ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.refresh1));
        refresh.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.green_primary,null)));
    }

    //inner class for adapter of recycler view
    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
        List<Notes> listArray;

        public MyAdapter(List<Notes> List) {
            this.listArray = List;
        }

        @NonNull
        @Override
        public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemview, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

            Notes data = listArray.get(position);
            holder.personName.setText(data.getUser());
            holder.tvdate.setText(data.getDate());
            holder.tvmessage.setText(data.getMessage());
            holder.tvmessage.setContentDescription(data.getmKey());//this is id of msg

        }

        @Override
        public int getItemCount() {
            return listArray.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder{
            TextView tvdate, tvmessage,personName;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);

                tvdate = (TextView) itemView.findViewById(R.id.tvdate);
                tvmessage = (TextView) itemView.findViewById(R.id.tvmessage);
                personName=(TextView)itemView.findViewById(R.id.personName);
                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        String id="my id";
                        id=tvmessage.getContentDescription().toString();
                        showUpdateDialog(id);
                        return false;
                    }
                });

            }
        }

    }

    private void deletemessage(String msgid)
    {
        DatabaseReference mydatabase=FirebaseDatabase.getInstance().getReference("Data");
        mydatabase.child("Messages").child(msgid).removeValue();
        Toast.makeText(getActivity().getApplicationContext(),"message deleted",
                Toast.LENGTH_SHORT).show();
    }


    //Alert Dialog
    private void showUpdateDialog(final String messageId) {

        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(getActivity());
        //Setting message manually and performing action on button click
        builder.setMessage("Do you want to delete message")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // finish(); to close current activity
                        deletemessage(messageId);
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //  Action for 'NO' Button
                dialog.cancel();
                Toast.makeText(getActivity().getApplicationContext(),"no action taken",
                        Toast.LENGTH_SHORT).show();
            }
        });
        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle("Choose Action");
        alert.show();
    }

}
