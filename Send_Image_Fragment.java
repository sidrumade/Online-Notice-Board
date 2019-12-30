package com.example.noticeboard10;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;

import static android.app.Activity.RESULT_OK;



public class Send_Image_Fragment extends Fragment {

    Button choosebutton,uploadbutton;
    TextView showchossed;
    EditText dis;

    DatabaseReference mydatabase;  //usae for upleading url of file
    StorageReference storage; //use for uopload files
    Uri filepath;
    String downloadLink;
    ProgressBar progressbar;
    String filename="not available";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_send_image, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        choosebutton=(Button)view.findViewById(R.id.choosebutton);
        uploadbutton=(Button)view.findViewById(R.id.uploadbutton);
        showchossed=(TextView)view.findViewById(R.id.showchossed);
        mydatabase=FirebaseDatabase.getInstance().getReference("Data");
        progressbar = (ProgressBar)view.findViewById(R.id.progressbar);
        dis=(EditText)view.findViewById(R.id.discrip);


        choosebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //check sd card read permission
                if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)
                {
                    selectFile();
                }
                else
                {//if no permission of read external
                    ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},9);

                }

            }
        });
        uploadbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(filepath!=null)//user has successful selected the file
                {
                    progressbar.setVisibility(View.VISIBLE);// To Show ProgressBar
                    getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    uploadFile(filepath);
                }
                else
                {
                    Toast.makeText(getActivity(),"Please Select File",Toast.LENGTH_SHORT).show();
                }
                filepath=null;

            }
        });


    }

    //acknowledgement for permission granted
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==9 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
        {
            selectFile();

        }
        else {
            Toast.makeText(getActivity(),"Please Provide Permission",Toast.LENGTH_SHORT).show();
        }

    }

    private void selectFile()
    {
        //offer user to select file for upload
        //using explicit intent

        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT); //fetch file related intent for selection of file
        startActivityForResult(intent,38);

    }

    //acknowledgement for check user successfully selected the file for upload
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==38 && resultCode==RESULT_OK && data!=null)
        //resultcode=check file explorer successfully closed
        {
            filepath=data.getData(); //exact location of file is fetched
            filename=getFileName(filepath);
            showchossed.setText("A file is selected :"+filename);

        }
        else
        {
            Toast.makeText(getActivity(),"Please Select File",Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadFile(final Uri filepath)
    {
        storage=FirebaseStorage.getInstance().getReference();//return an object of firebase storage root
        storage.child("UploadedImages").child(filename).putFile(filepath).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                progressbar.setVisibility(View.INVISIBLE);// To Hide ProgressBar
                showchossed.setText("");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                if (taskSnapshot.getMetadata() != null) {
                    if (taskSnapshot.getMetadata().getReference() != null) {
                        Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                        result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                downloadLink = uri.toString();
                                //showchossed.setText(downloadLink);
                                Toast.makeText( getActivity(),"File Uploaded Successfully",Toast.LENGTH_SHORT).show();

                                //insert the link into database
                                insertLinkToDB();
                            }
                            public void insertLinkToDB()
                            {
                                String id = mydatabase.push().getKey();
                                Date d = new Date();
                                if(dis.getText().toString().trim().equals(""))
                                    dis.setText("No discription");
                                String user=GoogleAuthActivity.fullname;
                                SaveUrl n=new SaveUrl(d.toString(),downloadLink,dis.getText().toString(),user,id);
                                dis.setText("");
                                mydatabase.child("Images").child(id).setValue(n).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {


                                        //   progressbar.setIndeterminate(true);
                                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);


                                        if(task.isSuccessful())
                                        {
                                            Toast.makeText( getActivity(),"Database Updated",Toast.LENGTH_SHORT).show();
                                        }
                                        else
                                        {
                                            Toast.makeText( getActivity(),"Faild to Update Database",Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                });
                            }
                        });
                    }
                }
            }}).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText( getActivity(),"File Upload Faild",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor =  getActivity().getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

}
