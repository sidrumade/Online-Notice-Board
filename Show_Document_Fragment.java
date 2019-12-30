package com.example.noticeboard10;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Show_Document_Fragment extends Fragment {

    private RecyclerView myRecycleview;
    List<SaveUrl> listData;
    MyDocAdapter adapter;
    FirebaseDatabase FDB;
    DatabaseReference DBR;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fregment_show_document,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        myRecycleview=(RecyclerView)view.findViewById(R.id.myDocumentRecycler);
        myRecycleview.setLayoutManager(new LinearLayoutManager(getActivity()));
        myRecycleview.setItemAnimator(new DefaultItemAnimator());
        myRecycleview.addItemDecoration(new DividerItemDecoration(getActivity().getApplicationContext(),LinearLayoutManager.VERTICAL));
        myRecycleview.setHasFixedSize(true);
        listData=new ArrayList<>();
        adapter=new MyDocAdapter(listData); //pass empty listArray of type setget
        FDB=FirebaseDatabase.getInstance();
        GetDataFirebase();
    }
    void GetDataFirebase()
    {

        DBR=FDB.getReference("Data");
        DBR.child("Documents").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                SaveUrl datax=dataSnapshot.getValue(SaveUrl.class);
                //now add data to array list
                listData.add(datax);

                //now add list to adapter
                myRecycleview.setAdapter(adapter);
                Toast.makeText(getActivity(),"Data Added",Toast.LENGTH_SHORT).show();

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

                Toast.makeText(getActivity(),"Database Error",Toast.LENGTH_SHORT).show();
            }
        });

    }

    //inner class for adapter of recycler view
    public class MyDocAdapter extends RecyclerView.Adapter<MyDocAdapter.MyViewHolder>{
        List<SaveUrl> listArray;

        public MyDocAdapter(List<SaveUrl> List)
        {
            this.listArray=List;
        }

        @NonNull
        @Override
        public MyDocAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.documentitemview,parent,false);

            return new MyViewHolder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

            SaveUrl data=listArray.get(position);
            holder.document_sender_name.setText("From "+data.getUser());
            holder.docdate.setText(data.getDate());
            holder.docdiscription.setText(data.getDiscription());
            holder.documentdownloadbutton.setContentDescription(data.getUrl());
            //Picasso.get().load("https://previews.123rf.com/images/urfandadashov/urfandadashov1808/urfandadashov180817380/107956703-document-vector-icon-isolated-on-transparent-background-document-logo-concept.jpg").centerCrop().fit().into(holder.docview);


        }
        @Override
        public int getItemCount() {
            return listArray.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder
        {
            TextView document_sender_name,docdate,docdiscription;
            ImageView docview;
            FloatingActionButton documentdownloadbutton;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);

                document_sender_name=(TextView)itemView.findViewById(R.id.document_sender_name);
                docdate=(TextView)itemView.findViewById(R.id.doc_date);
                docdiscription=(TextView)itemView.findViewById(R.id.doc_discription);
                docview=(ImageView)itemView.findViewById(R.id.mydoc_view);
                documentdownloadbutton=(FloatingActionButton)itemView.findViewById(R.id.Document_downloadbutton);
                documentdownloadbutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Your FAB click action here...
                        DownloadingTask docDownloadTask=new DownloadingTask();
                        docDownloadTask.execute(documentdownloadbutton.getContentDescription().toString());
                    }
                });

                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        showUpdateDialog("");
                        return false;
                    }
                });


            }


        }

    }


    class DownloadingTask extends AsyncTask<String, Integer, String>
    {

        @Override
        protected void onPreExecute() {
            Toast.makeText(getActivity(),"Download in progress...",Toast.LENGTH_SHORT).show(); }

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
                    String root = Environment.getExternalStorageDirectory().toString();
                    File myDir = new File(root + "/OnlineNoticeBoard/Documents");

                    if (!myDir.exists()) {
                        myDir.mkdirs();
                    }
                    String name = new Date().toString() + ".pdf";
                    myDir = new File(myDir, name);
                    FileOutputStream out = new FileOutputStream(myDir);
                    byte[] buffer = new byte[1024];//Set buffer type
                    int len1 = 0;//init length
                    while ((len1 = in.read(buffer)) != -1) {
                        out.write(buffer, 0, len1);//Write new file
                    }

                    in.close();
                    out.flush();
                    out.close();
                }

            }
            catch(Exception ex){
                Log.e("Exception",ex.toString());
            }
            return "Download Complete"; }

        @Override
        protected void onProgressUpdate(Integer... values) {
            Toast.makeText(getActivity(),"Downloading "+values[0],Toast.LENGTH_SHORT).show();
            super.onProgressUpdate(values); }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getActivity(),result,Toast.LENGTH_LONG).show(); }
    }

    private void deleteimage(String docid)
    {
        DatabaseReference mydatabase=FirebaseDatabase.getInstance().getReference("Data");
        mydatabase.child("Documents").child(docid).removeValue();
        Toast.makeText(getActivity().getApplicationContext(),"Document deleted",
                Toast.LENGTH_SHORT).show();
        Intent intent = getActivity().getIntent();
        getActivity().finish();
        startActivity(intent);
    }






    //Alert Dialog
    public void showUpdateDialog(final String documentId) {

        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(getActivity());
        //Setting message manually and performing action on button click
        builder.setMessage("Do you want to delete message")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // finish();
                        deleteimage(documentId);
                        Toast.makeText(getActivity(), "you choose yes action for alertbox",
                                Toast.LENGTH_SHORT).show();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //  Action for 'NO' Button
                dialog.cancel();
                Toast.makeText(getActivity(),"you choose no action for alertbox",
                        Toast.LENGTH_SHORT).show();
            }
        });
        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle("Delete Document");
        alert.show();
    }
}
