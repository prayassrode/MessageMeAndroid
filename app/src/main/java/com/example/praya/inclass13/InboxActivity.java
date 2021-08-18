package com.example.praya.inclass13;
/*
InClass13
Prayas Rode and Jacob Stern
*/
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InboxActivity extends AppCompatActivity {

    public static final String EMAIL_KEY = "email";
    private FirebaseAuth auth;
    private DatabaseReference dataRef;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.email_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.logoutButton:
                auth.signOut();
                Intent intent = new Intent(InboxActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;

            case R.id.createMessageButton:
                Intent intent1 = new Intent(InboxActivity.this, CreateMessageActivity.class);
                startActivity(intent1);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);
        setTitle("Inbox");

        auth = FirebaseAuth.getInstance();
        dataRef = FirebaseDatabase.getInstance().getReference();
        final ListView emailListView = findViewById(R.id.emailListView);
        dataRef.child("mailboxes").child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final ArrayList<String> emailIds = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String id = snapshot.getKey();
                    emailIds.add(id);
                }
                Collections.reverse(emailIds);
                EmailAdapter adapter = new EmailAdapter(InboxActivity.this, R.layout.email_list_layout, emailIds);
                emailListView.setAdapter(adapter);
                emailListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(InboxActivity.this, EmailDetailActivity.class);
                        intent.putExtra(EMAIL_KEY, emailIds.get(position));
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private class EmailAdapter extends ArrayAdapter<String>{

        public EmailAdapter(@NonNull Context context, int resource, @NonNull List<String> objects) {
            super(context, resource, objects);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            final String emailId = getItem(position);
            final ViewHolder viewHolder;
            if(convertView==null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.email_list_layout, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.name = convertView.findViewById(R.id.userName);
                viewHolder.timestamp = convertView.findViewById(R.id.timestamp);
                viewHolder.message = convertView.findViewById(R.id.message);
                viewHolder.isRead = convertView.findViewById(R.id.isReadicon);
                convertView.setTag(viewHolder);
            }else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final DatabaseReference emailRef = dataRef.child("mailboxes").child(auth.getCurrentUser()
                    .getUid()).child(emailId);
            emailRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Email email = dataSnapshot.getValue(Email.class);
                    dataRef.child("users").child(email.sender).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            viewHolder.name.setText(user.firstName+" "+user.lastName);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    viewHolder.message.setText(email.message);
                    viewHolder.timestamp.setText(email.timestamp);
                    if (email.read){
                        viewHolder.isRead.setImageResource(R.drawable.circle_grey);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            return convertView;
        }
    }
    private class ViewHolder{
        TextView name, timestamp, message;
        ImageView isRead;
    }
}
