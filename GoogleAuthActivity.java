package com.example.noticeboard10;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class GoogleAuthActivity extends AppCompatActivity {

    SignInButton signInButton;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    ProgressBar AuthProgressBar;
    public static String fullname , fname , lname ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_auth);

        AuthProgressBar=(ProgressBar)findViewById(R.id.AuthProgressBar);
        signInButton = (SignInButton) findViewById(R.id.signinbtn);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)

                .requestIdToken(getString(R.string.default_web_client_id))

                .requestEmail()

                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();


    }
    // [START on_start_check_user]

    @Override

    public void onStart() {

        super.onStart();

        // Check if user is signed in (non-null) and update UI accordingly.

        FirebaseUser currentUser = mAuth.getCurrentUser();

        updateUI(currentUser);

    }
    // [START onactivityresult]

    @Override

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);


        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);

        if (requestCode == RC_SIGN_IN) {

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {

                // Google Sign In was successful, authenticate with Firebase

                GoogleSignInAccount account = task.getResult(ApiException.class);

                getInfo(account);

                firebaseAuthWithGoogle(account);

            } catch (ApiException e) {

                // Google Sign In failed, update UI appropriately

                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                // [START_EXCLUDE]

                updateUI(null);

                // [END_EXCLUDE]

            }

        }

    }


    // [START auth_with_google]

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        // Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        // [START_EXCLUDE silent]
        AuthProgressBar.setVisibility(View.VISIBLE);
        //stop user interaction while progress is going on
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        // [END_EXCLUDE]


        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        mAuth.signInWithCredential(credential)

                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override

                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            // Sign in success, update UI with the signed-in user's information



                            FirebaseUser user = mAuth.getCurrentUser();

                            updateUI(user);

                        } else {

                            // If sign in fails, display a message to the user.


                            //    Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            Toast.makeText(getApplicationContext(), "Authentication Failed", Toast.LENGTH_SHORT).show();
                            updateUI(null);

                        }


                        // [START_EXCLUDE]

                        // hideProgressDialog();
                        AuthProgressBar.setVisibility(View.GONE);
                        //give back user interaction
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        // [END_EXCLUDE]

                    }

                });

    }

    // [END auth_with_google]


    // [START signin]

    private void signIn() {

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();

        startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    // [END signin]


    private void updateUI(FirebaseUser currentUser) {
        if (currentUser != null) {
            try {
                saveUserInfo(fullname);
            }
            catch (Exception e)
            {
                Toast.makeText(getApplicationContext(), "error in creating user file", Toast.LENGTH_SHORT).show();
            }
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            i.putExtra("Fullname", fullname);
            startActivity(i);
            finish();
            Toast.makeText(getApplicationContext(), "User login succesful", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "User Authentication Fail", Toast.LENGTH_SHORT).show();

        }


    }

    private void saveUserInfo(String fullname) throws IOException {
        //  File file = new File(getApplicationContext().getFilesDir(), "client.txt");
        FileOutputStream fOut = openFileOutput("client.txt",getApplicationContext().MODE_PRIVATE);
        fOut.write(fullname.getBytes());
        fOut.close();
    }

    private void getInfo(GoogleSignInAccount account) {
        String name = account.getDisplayName();

        String email = account.getEmail();
        fullname = account.getDisplayName();
        String[] parts = fullname.split("\\s+");

        if (parts.length == 2) {
            String firstname = parts[0];
            String lastname = parts[1];

        } else if (parts.length == 3) {
            String firstname = parts[0];
            String middlename = parts[1];
            String lastname = parts[2];

        }

    }
}

