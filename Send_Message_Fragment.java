package com.example.noticeboard10;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

public class Send_Message_Fragment extends Fragment {
    Button bt1;
    EditText et;
    TextView tv;
    DatabaseReference mydatabase,ref;
    FirebaseAuth auth;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_send_message, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        auth = FirebaseAuth.getInstance();
        bt1 = (Button) view.findViewById(R.id.button1);
        et = (EditText) view.findViewById(R.id.editText1);
        tv = (TextView) view.findViewById(R.id.textView1);
        mydatabase = FirebaseDatabase.getInstance().getReference("Data");
       // ref = FirebaseDatabase.getInstance().getReference("Admin");
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    SendButton(v);


            }
        });


    }

    public void SendButton(View view) {

        String msg = et.getText().toString();
        if (TextUtils.isEmpty(msg.trim())) {
            Toast.makeText(getActivity(), "message cant empty", Toast.LENGTH_SHORT).show();
        } else {

                Helper help=new Helper();
                String id=help.getPushKey(mydatabase);

                if(id!="")
                {


                    Date d = new Date();
                    SimpleDateFormat formatter = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
                    String strDate = formatter.format(d);
                    String user = auth.getCurrentUser().getDisplayName();

                    Toast.makeText(getActivity(), id+" "+user+" "+strDate, Toast.LENGTH_SHORT).show();


                    Notes n = new Notes(strDate, msg, id, user);
                        mydatabase.child("Messages").child(id).setValue(n).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity().getApplicationContext(), "No Access", Toast.LENGTH_LONG).show();
                            }
                        }).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                               // tv.setText("id is:"+id);
                                et.setText("");
                            }
                        });

                        // DontExecute:
                        //     Toast.makeText(getActivity(), "Error is fucking here", Toast.LENGTH_SHORT).show();


                }
                else{
                    Toast.makeText(getActivity(), "got empty id", Toast.LENGTH_SHORT).show();
                }



        }


    }
}
