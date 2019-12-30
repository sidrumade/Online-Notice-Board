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
import java.util.List;


public class Show_Image_Fragment extends Fragment {

    private RecyclerView myRecycleview;
    List<SaveUrl> listData;
    MyAdapter adapter;
    FirebaseDatabase FDB;
    DatabaseReference DBR;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_show_image, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        myRecycleview=(RecyclerView)view.findViewById(R.id.myImageRecycler);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setStackFromEnd(true); //to show last uploaded image  in recycler view
        myRecycleview.setLayoutManager(linearLayoutManager);
        myRecycleview.setItemAnimator(new DefaultItemAnimator());
        myRecycleview.addItemDecoration(new DividerItemDecoration(getActivity().getApplicationContext(),LinearLayoutManager.VERTICAL));
        myRecycleview.setHasFixedSize(true);
        listData=new ArrayList<>();
        adapter=new MyAdapter(listData); //pass empty listArray of type setget
        FDB=FirebaseDatabase.getInstance();
        GetDataFirebase();
    }
    void GetDataFirebase()
    {

        DBR=FDB.getReference("Data");
        DBR.child("Images").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                SaveUrl datax=dataSnapshot.getValue(SaveUrl.class);
                //now add data to array list
                listData.add(datax);

                //now add list to adapter
                myRecycleview.setAdapter(adapter);
                Toast.makeText(getActivity(),"Data Added",Toast.LENGTH_SHORT).show();

                //////notification
                NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
                String NOTIFICATION_CHANNEL_ID = "my_channel_id_02";

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notification", NotificationManager.IMPORTANCE_HIGH);

                    // Configure the notification channel.
                    notificationChannel.setDescription("Channel description");
                    notificationChannel.enableLights(true);
                    notificationChannel.setLightColor(Color.RED);
                    notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
                    notificationChannel.enableVibration(true);
                    notificationManager.createNotificationChannel(notificationChannel);
                }


                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getActivity(), NOTIFICATION_CHANNEL_ID);
                Intent i=new Intent(getActivity(),MainActivity.class);
                PendingIntent pi=PendingIntent.getActivity(getActivity(),0,i,0);
                notificationBuilder.setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setWhen(System.currentTimeMillis())
                        .setSmallIcon(R.drawable.symbol)
                        .setTicker("Hearty365")
                        //     .setPriority(Notification.PRIORITY_MAX)
                        .setContentTitle("New Image")
                        .setContentText("Got something new on notice board")
                        .setContentInfo("Info")
                        .setContentIntent(pi);

                notificationManager.notify(/*notification id*/1, notificationBuilder.build());
                //notification end

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                SaveUrl datax=dataSnapshot.getValue(SaveUrl.class);
                //new add data to array list
                listData.add(datax);

                //now add list to adapter
                myRecycleview.setAdapter(adapter);
                Toast.makeText(getActivity(),"Data Changed",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                SaveUrl datax=dataSnapshot.getValue(SaveUrl.class);

                //now add list to adapter
                myRecycleview.setAdapter(adapter);
                Toast.makeText(getActivity(),"Data Removed",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                listData.clear();
                SaveUrl datax=dataSnapshot.getValue(SaveUrl.class);
                //new add data to array list
                listData.add(datax);

                //now add list to adapter
                myRecycleview.setAdapter(adapter);
                Toast.makeText(getActivity(),"Data Moved",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(getActivity(),databaseError.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });

    }

    //inner class for adapter of recycler view
    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>{
        List<SaveUrl> listArray;

        public MyAdapter(List<SaveUrl> List)
        {
            this.listArray=List;
        }

        @NonNull
        @Override
        public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.imageitemview,parent,false);

            return new MyViewHolder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

            SaveUrl data=listArray.get(position);
            holder.image_sender_name.setText("From "+data.getUser());
            holder.imgdate.setText(data.getDate());
            holder.imgdiscription.setText(data.getDiscription());
            holder.imagedownloadbutton.setContentDescription(data.getUrl());
            holder.imgview.setContentDescription(data.getmKey());
            Picasso.get().load(data.getUrl()).centerCrop().fit().into(holder.imgview);


        }
        @Override
        public int getItemCount() {
            return listArray.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder
        {
            TextView image_sender_name,imgdate,imgdiscription;
            ImageView imgview;
            FloatingActionButton imagedownloadbutton;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);

                image_sender_name=(TextView)itemView.findViewById(R.id.image_sender_name);
                imgdate=(TextView)itemView.findViewById(R.id.imagedate);
                imgdiscription=(TextView)itemView.findViewById(R.id.imagediscription);
                imgview=(ImageView)itemView.findViewById(R.id.myimageview);
                imagedownloadbutton=(FloatingActionButton)itemView.findViewById(R.id.Imagedownloadbutton);
                imagedownloadbutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Your FAB click action here...

                        ImageDownloadTask IdownloadTask=new  ImageDownloadTask();
                        IdownloadTask.execute(imagedownloadbutton.getContentDescription().toString());
                    }
                });
                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        String id="my id";//this is just for database entry. not for actual storage where the image is saved
                        id=imgview.getContentDescription().toString();
                        showUpdateDialog(id);
                        return false;
                    }
                });


            }
        }

    }


    class ImageDownloadTask extends AsyncTask<String, Integer, String>
    {

        @Override
        protected void onPreExecute() {
            Toast.makeText(getActivity().getApplicationContext(),"Download in progress...",Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(String... params) {
            String path=params[0];
            InputStream in =null;
            Bitmap bmp=null;
            int responseCode = -1;
            try{

                URL url = new URL(path);//"http://192.xx.xx.xx/mypath/img1.jpg or i am taking from firebase
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                con.setDoInput(true);
                con.connect();
                responseCode = con.getResponseCode();
                if(responseCode == HttpURLConnection.HTTP_OK)
                {
                    //download
                    in = con.getInputStream();
                    bmp = BitmapFactory.decodeStream(in);
                    in.close();
                    ////////////////////////

                    String root = Environment.getExternalStorageDirectory().toString();
                    File myDir = new File(root + "/OnlineNoticeBoard/Images");

                    if (!myDir.exists()) {
                        myDir.mkdirs();
                    }

                    String name = new Date().toString() + ".jpg";
                    myDir = new File(myDir, name);
                    FileOutputStream out = new FileOutputStream(myDir);
                    bmp.compress(Bitmap.CompressFormat.JPEG, 90, out);

                    out.flush();
                    out.close();



//////////////////////////////////
                }

            }
            catch(Exception ex){
                Log.e("Exception",ex.toString());
            }
            return "Download Complete";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            Toast.makeText(getActivity().getApplicationContext(),"Downloading "+values[0],Toast.LENGTH_SHORT).show();
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getActivity().getApplicationContext(),result,Toast.LENGTH_LONG).show();
        }
    }


    private void deleteimage(String imgid)
    {
        DatabaseReference mydatabase=FirebaseDatabase.getInstance().getReference("Data");
        mydatabase.child("Images").child(imgid).removeValue();
        Toast.makeText(getActivity().getApplicationContext(),"Image deleted",
                Toast.LENGTH_SHORT).show();

        Intent intent = getActivity().getIntent();
        getActivity().finish();
        startActivity(intent);
    }


    //Alert Dialog for deleting image
    public void showUpdateDialog(final String imageId) {

        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(getActivity());
        //Setting message manually and performing action on button click
        builder.setMessage("Do you want to delete message")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // finish();
                        deleteimage(imageId);
                        Toast.makeText(getActivity().getApplicationContext(), "you choose yes action for alertbox",
                                Toast.LENGTH_SHORT).show();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //  Action for 'NO' Button
                dialog.cancel();
                Toast.makeText(getActivity().getApplicationContext(),"you choose no action for alertbox",
                        Toast.LENGTH_SHORT).show();
            }
        });
        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle("Delete Image");
        alert.show();
    }
}
