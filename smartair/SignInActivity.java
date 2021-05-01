package com.example.smartair;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

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
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;

public class SignInActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private TextView register, forgotPassword;
    private EditText editTextEmail, editTextPassword;
    private Button signIn;
    SignInButton googleSignIn;
    GoogleSignInClient googleSignInClient;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        mAuth = FirebaseAuth.getInstance();
        loadLocale();

        //When user is already signed in Redirect to Home Activity
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            startActivity(new Intent(this, HomeActivity.class));
            this.finish();
        }

        //Email Sign in
        signIn = findViewById(R.id.login_SignIn);
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInUser();
            }
        });
        editTextEmail = findViewById(R.id.username_signIn);
        editTextPassword = findViewById(R.id.password_signIn);

        progressBar = findViewById(R.id.loading_signIn);

        forgotPassword = findViewById(R.id.forgot_password_signIn);

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ForgetPassword.class));
            }
        });

        register = findViewById(R.id.create_account_signIn);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
            }
        });


        //google sign in authentication
        googleSignIn = findViewById(R.id.google_sign_in_btn);

        //Initialize sign in options
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN
        ).requestIdToken("774190615419-0i5lu61ae22q2jg4ipcaqbtpeav97m1i.apps.googleusercontent.com")
                .requestEmail()
                .build();

        //Initialize sign in Client
        googleSignInClient = GoogleSignIn.getClient(SignInActivity.this, googleSignInOptions);

        googleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intitialize sign in intent
                Intent intent = googleSignInClient.getSignInIntent();
                //Start Activity for result
                startActivityForResult(intent, 100);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Check Condition
        if(requestCode == 100){
            //When request code is equal to 100
            //Intitialize task
            Task<GoogleSignInAccount> signInAccountTask = GoogleSignIn
                    .getSignedInAccountFromIntent(data);
            if(signInAccountTask.isSuccessful()){
                //WHen google sign in is successfull
                //Initialize string
                String s = getString(R.string.googlesSignInsuccess);
                //Display toast
                displayToast(s);

                try {
                    //Initialize sign in Account
                    GoogleSignInAccount googleSignInAccount = signInAccountTask
                            .getResult(ApiException.class);
                    //Check condition
                    if(googleSignInAccount != null){
                        //Initialize auth credential if not equal to null
                        AuthCredential authCredential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);
                        //Check successful
                        mAuth.signInWithCredential(authCredential)
                                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        //Check Condition
                                        if(task.isSuccessful()){
                                            //If successful redirect to profile activity
                                            startActivity(new Intent(SignInActivity.this, HomeActivity.class)
                                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                                            displayToast(getString(R.string.googlesSignInsuccess));
                                        }else{
                                            displayToast(getString(R.string.googleSignInFailed) + task.getException().getMessage());
                                        }
                                    }
                                });
                    }
                } catch (ApiException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private void displayToast(String s){
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }
    private void signInUser(){

        String emailAddress = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();


        if(password.isEmpty()){
            editTextPassword.setError(getString(R.string.passwordRequired));
            editTextPassword.requestFocus();
            return;
        }
        if(password.length() < 6){
            editTextPassword.setError(getString(R.string.invalid_password));
        }
        if(emailAddress.isEmpty()){
            editTextEmail.setError(getString(R.string.emailRequired));
            editTextEmail.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()){
            editTextEmail.setError(getString(R.string.invalid_email));
            editTextEmail.requestFocus();
            return;
        }



        mAuth.signInWithEmailAndPassword(emailAddress, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user.isEmailVerified()){
                        progressBar.setVisibility(View.VISIBLE);
                        startActivity(new Intent(SignInActivity.this, HomeActivity.class));
                    }else{
                        user.sendEmailVerification();
                        Toast.makeText(SignInActivity.this, getString(R.string.verifyEmail), Toast.LENGTH_LONG).show();
                    }

                }else {
                    Toast.makeText(SignInActivity.this, getString(R.string.userSignInFailed), Toast.LENGTH_LONG).show();
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
    @Override
    public void onBackPressed() {
        finish();
    }
}