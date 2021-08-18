package com.example.praya.inclass13;
/*
InClass13
Prayas Rode and Jacob Stern
*/
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EmailDetailActivity extends AppCompatActivity {

    public static final String RECIPIENT_KEY = "recipient";

    private String emailId;
    private FirebaseAuth auth;
    private DatabaseReference dataRef;
    private String senderId;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.email_detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        auth = FirebaseAuth.getInstance();
        dataRef = FirebaseDatabase.getInstance().getReference();
        switch (item.getItemId()){
            case R.id.replyButton:
                Intent intent = new Intent(EmailDetailActivity.this, CreateMessageActivity.class);
                intent.putExtra(RECIPIENT_KEY, senderId);
                startActivity(intent);
                return true;

            case R.id.deleteMessageButton:
                dataRef.child("mailboxes").child(auth.getCurrentUser().getUid()).child(emailId).removeValue();
                Toast.makeText(this, "Message deleted", Toast.LENGTH_SHORT).show();
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_detail);

        auth = FirebaseAuth.getInstance();
        dataRef = FirebaseDatabase.getInstance().getReference();
        if (getIntent() != null && getIntent().getExtras() != null) {

            emailId = getIntent().getExtras().getString(InboxActivity.EMAIL_KEY);
            dataRef.child("mailboxes").child(auth.getCurrentUser().getUid()).child(emailId).child("read").setValue(true);
            final TextView from = findViewById(R.id.fromPlaceHolder);
            dataRef.child("mailboxes").child(auth.getCurrentUser().getUid()).child(emailId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final Email email = dataSnapshot.getValue(Email.class);
                    TextView message = findViewById(R.id.messageField);
                    message.setText(email.message);
                    senderId = email.sender;
                    dataRef.child("users").child(senderId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User sender = dataSnapshot.getValue(User.class);
                            from.append(" "+sender.firstName+" "+sender.lastName);

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

            });

        }

    }
}
