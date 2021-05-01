package com.example.smartair;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Account_sett extends AppCompatActivity {
    private FirebaseUser user;
    private DatabaseReference reference;

    private String userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_sett);

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference(getString(R.string.databaseName_users));
        userID = user.getUid();

        final TextView usernameTextView = findViewById(R.id.username_settings);
        final TextView emailTextView = findViewById(R.id.email_settings);
        final TextView passwordTextView = findViewById(R.id.password_settings);
        final TextView ageTextView = findViewById(R.id.age_settings);

        //For when the users database is updated
        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);

                if(userProfile != null){
                    String username = userProfile.username;
                    String email = userProfile.email;
                    String age = userProfile.age;

                    usernameTextView.setText(username);
                    emailTextView.setText(email);
                    ageTextView.setText(age);

                    passwordTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(getApplicationContext(), ForgetPassword.class));
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Account_sett.this, getString(R.string.dbError_account), Toast.LENGTH_LONG).show();
            }
        });

        //For Google sign in
        if(user != null){
            //firebase user is not null
            //set name on text view
            usernameTextView.setText(getString(R.string.prompt_username) + ": " + user.getDisplayName());
            emailTextView.setText(getString(R.string.prompt_email) + ": " + user.getEmail());

        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
}