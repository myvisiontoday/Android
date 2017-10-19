package com.myvisontoday.ourmap;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private ProgressDialog progressDialog;
    public String userUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
        mAuthTask = FirebaseAuth.getInstance();

        if (mAuthTask.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, ProfileActivity.class));
            finish();
        }
        progressDialog = new ProgressDialog(this);
        mLoginFormView = findViewById(R.id.login_form);
    }

    private void attemptLogin(){

        String email = mEmailView.getText().toString().trim();
        String password = mPasswordView.getText().toString().trim();

        if(TextUtils.isEmpty(email))
        {
            //Email field is empty
            Toast.makeText(this, "Please enter your email.", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(password))
        {
            //Password field is empty
            Toast.makeText(this, "Please enter your Password.", Toast.LENGTH_LONG).show();
            return;
        }

        //Show progessDialog
        progressDialog.setMessage("Logging in ...");
        progressDialog.show();
        mAuthTask.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if ((task.isSuccessful())) {
                            userUID = task.getResult().getUser().getUid();

                            // if register complete, show toast and start profile activity.
                            progressDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "Login Complete.", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
                            startActivity(intent);

                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "Could not Login.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    public void Signup(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
}
