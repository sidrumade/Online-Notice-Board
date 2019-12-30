package com.example.noticeboard10;

import android.app.Person;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.QuickContactBadge;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    FirebaseUser mFirebaseUser;
    FirebaseAuth auth;
    NavigationView navigationView;
    FirebaseAuth.AuthStateListener authListener;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navigationView=findViewById(R.id.nav_view);
        drawer=findViewById(R.id.drawer_layout);
navigationView.setNavigationItemSelectedListener(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toggle = new ActionBarDrawerToggle
                (
                        this,
                        drawer,
                        R.string.navigation_drawer_open,
                        R.string.navigation_drawer_close
                )
        {
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new Show_Message_Fragment()).commit();

        //firebase auth listener
        auth=FirebaseAuth.getInstance();
        mFirebaseUser = auth.getCurrentUser();

        authListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() ==null)
                {
                    startActivity(new Intent(MainActivity.this,GoogleAuthActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    finish();
                    Toast.makeText(getApplicationContext(),"User Logged Out Successful",Toast.LENGTH_SHORT).show();

                }
            }
        };





        //side menu data sewtter
        View hView = navigationView.getHeaderView(0);
        TextView header_name = (TextView)hView.findViewById(R.id.header_name);
        TextView header_email = (TextView)hView.findViewById(R.id.header_email);
        final TextView header_uid = (TextView)hView.findViewById(R.id.header_uid);

        header_name.setText(auth.getCurrentUser().getDisplayName());
        header_email.setText(auth.getCurrentUser().getEmail());
        header_uid.setText(auth.getCurrentUser().getUid());
        ImageButton copybut=(ImageButton)hView.findViewById(R.id.copy_uid_button);
        copybut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("uid", header_uid.getText());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getApplicationContext(),"UID copyed to clipboard",Toast.LENGTH_LONG).show();
            }
        });














        //////////





    }

    //this is for action bar toggle button click
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(toggle.onOptionsItemSelected(item))
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {

            case R.id.nav_item_show_message:
                //do somthing
                 getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new Show_Message_Fragment()).commit();

                Toast.makeText(this,"show message",Toast.LENGTH_LONG).show();
                break;
            case R.id.nav_item_show_image:
                //do somthing
                  getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new Show_Image_Fragment()).commit();
                Toast.makeText(this,"show image",Toast.LENGTH_LONG).show();
                break;

            case R.id.nav_item_show_document:
                //do somthing
                  getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new Show_Document_Fragment()).commit();
                Toast.makeText(this,"show doc",Toast.LENGTH_LONG).show();
                break;

            case R.id.nav_item_send_message:
                // getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,new  DocumentFragement()).commit();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new Send_Message_Fragment()).commit();
                break;
            case R.id.nav_item_send_image:
                // getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,new  DocumentFragement()).commit();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new Send_Image_Fragment()).commit();
                break;
            case R.id.nav_item_send_document:
                // getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,new  DocumentFragement()).commit();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new Send_Document_Fragment()).commit();
                break;

            case R.id.nav_item_add_admin:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new AdminManagementFragment()).commit();

                Toast.makeText(this,"admin management",Toast.LENGTH_LONG).show();
                break;

            default:
                //do somthing
                // getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,new StudentHomeFragement()).commit();
                break;

        }
        //close navigation drawer
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
