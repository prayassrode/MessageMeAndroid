package com.example.praya.inclass13;
/*
InClass13
Prayas Rode and Jacob Stern
*/
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CreateMessageActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private DatabaseReference dataRef;
    private String recipientUid;
    private boolean isReply = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_message);
        setTitle("Create new message");

        auth = FirebaseAuth.getInstance();
        dataRef = FirebaseDatabase.getInstance().getReference();

        if (getIntent() != null && getIntent().getExtras() != null) {
            isReply = true;
            String recipient = getIntent().getExtras().getString(EmailDetailActivity.RECIPIENT_KEY);
            recipientUid = recipient;
            dataRef.child("users").child(recipient).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    TextView to = findViewById(R.id.toField);
                    to.append(" "+user.firstName+" "+ user.lastName);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        if (!isReply) {
            ImageButton selectRecipient = findViewById(R.id.selectRecipient);
            selectRecipient.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dataRef.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            ArrayList<User> users = new ArrayList<>();
                            final ArrayList<String> uids = new ArrayList<>();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                users.add(snapshot.getValue(User.class));
                                uids.add(snapshot.getKey());
                            }
                            final String[] userNames = new String[users.size()];
                            for (int i = 0; i < users.size(); i++) {
                                String name = users.get(i).firstName + " " + users.get(i).lastName;
                                userNames[i] = name;
                            }
                            AlertDialog.Builder builder = new AlertDialog.Builder(CreateMessageActivity.this);
                            builder.setTitle("Users");
                            builder.setItems(userNames, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                    TextView to = findViewById(R.id.toField);
                                    to.setText(R.string.toFieldText);
                                    to.append(" " + userNames[i]);
                                    recipientUid = uids.get(i);
                                }
                            });
                            builder.create().show();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            });
        }

        Button sendButton = findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText messageBox = findViewById(R.id.messageEditBox);
                if (messageBox.getText().toString().length()==0){
                    Toast.makeText(CreateMessageActivity.this, "Please enter a message to be sent", Toast.LENGTH_SHORT).show();
                }else if (recipientUid == null){
                    Toast.makeText(CreateMessageActivity.this, "Please select a recipient", Toast.LENGTH_SHORT).show();
                }
                else {
                    DatabaseReference reference = dataRef.child("mailboxes").child(recipientUid).push();
                    reference.child("message").setValue(messageBox.getText().toString());
                    reference.child("read").setValue(false);
                    reference.child("sender").setValue(auth.getCurrentUser().getUid());
                    Date date = Calendar.getInstance().getTime();
                    SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy, HH:mm a");
                    reference.child("timestamp").setValue(formatter.format(date));
                    Toast.makeText(CreateMessageActivity.this, "Message sent", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });


    }
}
