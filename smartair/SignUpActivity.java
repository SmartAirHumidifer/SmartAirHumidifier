package com.example.smartair;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText name, email, pWord, editTextage;
    private Button signIn, back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        loadLocale();

        signIn = findViewById(R.id.signin_button);
        back = findViewById(R.id.back);


       name = findViewById(R.id.editTextTextPersonName);
       email = findViewById(R.id.editTextTextEmailAddress);
       pWord = findViewById(R.id.editTextTextPassword);
       editTextage = findViewById(R.id.editTextNumber);

        mAuth = FirebaseAuth.getInstance();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        });

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                registerUser();
            }
        });
    }

    private void registerUser(){
        String username = name.getText().toString().trim();
        String emailAddress = email.getText().toString().trim();
        String password = pWord.getText().toString().trim();
        String age = editTextage.getText().toString().trim();

        if(username.isEmpty()){
            name.setError(getString(R.string.invalid_username));
            name.requestFocus();
            return;
        }
        if(age.isEmpty()){
            editTextage.setError(getString(R.string.ageRequired));
            editTextage.requestFocus();
            return;
        }
        if(password.isEmpty()){
            pWord.setError(getString(R.string.passwordRequired));
            pWord.requestFocus();
            return;
        }
        if(password.length() < 6){
            pWord.setError(getString(R.string.invalid_password));
        }
        if(emailAddress.isEmpty()){
            email.setError(getString(R.string.usernameRequired));
            email.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()){
            email.setError(getString(R.string.invalid_email));
            email.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(emailAddress, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    User user = new User(username, age, emailAddress);
                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(SignUpActivity.this,getString(R.string.userRegistrationSuccessful), Toast.LENGTH_LONG).show();
                            }else {
                                Toast.makeText(SignUpActivity.this, getString(R.string.userRegistrationFailed), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                }else {
                    Toast.makeText(SignUpActivity.this, getString(R.string.registrationFailed), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
//        save data to shared preferences
        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("My_Lang", lang);
        editor.apply();
    }

    public void loadLocale(){
        SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language = prefs.getString("My_Lang", "");
        setLocale(language);
    }
}