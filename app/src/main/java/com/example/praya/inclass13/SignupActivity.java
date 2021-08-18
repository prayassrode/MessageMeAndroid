package com.example.praya.inclass13;
/*
InClass13
Prayas Rode and Jacob Stern
*/
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Button cancelButton = findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        Button signupButton = findViewById(R.id.signupButton);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText firstNameField = findViewById(R.id.firstNameField);
                final String firstName = firstNameField.getText().toString();
                EditText lastNameField = findViewById(R.id.lastNameField);
                final String lastName = lastNameField.getText().toString();
                EditText emailField = findViewById(R.id.emailField);
                String email = emailField.getText().toString();
                EditText passwordField = findViewById(R.id.passwordField);
                String password = passwordField.getText().toString();
                EditText confirmPasswordField = findViewById(R.id.confirmPasswordField);
                String confirmPassword = confirmPasswordField.getText().toString();
                if (firstName.length() == 0 || lastName.length() == 0 || email.length() == 0 ||
                        password.length() == 0 || confirmPassword.length() == 0) {
                    Toast.makeText(SignupActivity.this, "Please fill in every field", Toast.LENGTH_SHORT).show();
                } else if (!password.equals(confirmPassword)) {
                    Toast.makeText(SignupActivity.this, "The password fields must match", Toast.LENGTH_SHORT).show();
                } else if (password.length() < 6) {
                    Toast.makeText(SignupActivity.this, "Your password must be at least 6 characters long", Toast.LENGTH_SHORT).show();
                } else {
                    final FirebaseAuth auth = FirebaseAuth.getInstance();
                    auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference();
                                        User user = new User(firstName, lastName);
                                        dataRef.child("users").child(auth.getCurrentUser().getUid()).setValue(user);
                                        Intent intent = new Intent(SignupActivity.this, InboxActivity.class);
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(SignupActivity.this, "User already present", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }
}
