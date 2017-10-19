package com.myvisontoday.ourmap;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonRegister;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextName;
    private EditText editTextAge;
    private EditText editTextGender;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    public String userUID;
private boolean clicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        buttonRegister = (Button) findViewById(R.id.buttonRegister);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextAge = (EditText) findViewById(R.id.editTextAge);
        editTextGender = (EditText) findViewById(R.id.editTextGender);
        progressDialog = new ProgressDialog(this);
        userUID = null;

        buttonRegister.setOnClickListener(this);
    }

    public void register(View view) {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

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
clicked = true;
        //Show progessDialog
        progressDialog.setMessage("Registering user ...");
        progressDialog.show();
        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if ((task.isSuccessful()))
                        {
                            userUID = task.getResult().getUser().getUid();
                            User user = new User();
                            user.setUserName(editTextName.getText().toString());
                            user.setAge(editTextAge.getText().toString());
                            user.setGender(editTextGender.getText().toString());
                            databaseReference.child("users").child(userUID).setValue(user);

                            // if register complete, show toast and start profile activity.
                            progressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this,"Registration Complete.", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(RegisterActivity.this, ProfileActivity.class);
                            startActivity(intent);
                        }
                        else {
                            Toast.makeText(RegisterActivity.this,"Could not register.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    public boolean buttonClicked(){
        return clicked;
    }
    //checking should still be done in the test file, the example was for a method that was actually implemented in the application
    //and contributed to the functionality
    //MOVED TO REGISTRATION TEST
    public boolean checkEmail(){
        EditText et = (EditText) findViewById(R.id.editTextEmail);
        String s = et.getText().toString();
        if(s != ""){
            String[] st = s.split("@");
            if(st[1]=="")
            {
            return false;}
            else
                {return  true;}
        }
        return false;
        }

}
