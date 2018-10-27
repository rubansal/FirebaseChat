package com.codingblocks.firebase;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.crashlytics.android.Crashlytics;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    public static final String TAG="fbse";
    private static final int RC_SIGN_IN =1000 ;

    Button button;
    EditText editText;
    ArrayList<String> notes=new ArrayList<>();
    ListView listView;
    ArrayAdapter<String> arrayAdapter;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //we can atmost 5 apps connected to firebase
        //if we want more apps connected to firebase then either request or take a paid plane
        //when we connect the app with firebase a file google json downloaded
        //if we delete this file then link with firebase removed

        button=findViewById(R.id.btnDb);
        editText=findViewById(R.id.etNote);
        listView=findViewById(R.id.listView);

        //Crashlytics.getInstance().crash();

        arrayAdapter=new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                android.R.id.text1,
                notes);
        listView.setAdapter(arrayAdapter);

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

        if(firebaseUser!=null)
        {
            //Logged in
            addListners();
        }
        else
        {
            //Logged out
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setIsSmartLockEnabled(false)
                            .setAvailableProviders(Arrays.asList(
                                    new AuthUI.IdpConfig.GoogleBuilder().build(),
                                    new AuthUI.IdpConfig.EmailBuilder().build(),
                                    new AuthUI.IdpConfig.PhoneBuilder().build()))
                            .build(),
                    RC_SIGN_IN);
        }


    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            // Successfully signed in
            if (resultCode == RESULT_OK) {
                firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
                addListners();
            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    return;
                }

                if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    return;
                }

            }
        }
    }

    public void addListners()
    {
        final DatabaseReference dbRef=FirebaseDatabase.getInstance().getReference();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String note=editText.getText().toString();
                //Note n=new Note("Hello","World");
                //upload the note to firebase
                //the firebase works on the nodes and references
                //now gets reference to the root node


                //FirebaseDatabase.getInstance().getReference().setValue(note);
                //this line gets refernce to the root node and saves the notes to this
                //but in this it over writes the new data with the old data

                //so we have to use this
                //FirebaseDatabase.getInstance().getReference().push().setValue(note);

                //now i want to manage data creating two nodes in which one hold the notes and other is todos
                dbRef.child("note").child(firebaseUser.getUid()).push().setValue(note);//for different database of every user
                //dbRef.child("todo").push().setValue(note);

            }
        });

        dbRef.child("note").child(firebaseUser.getUid()).addChildEventListener(new ChildEventListener() {//in realtime database there is a listner means
            // every time whenever be the data changes we have nothing to do this listner automatically callback and fetch the data
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //called when a new data node is inserted to the "note" node
                //Note data=dataSnapshot.getValue(Note.class);

                String data=dataSnapshot.getValue(String.class);
                Log.d(TAG, "onChildAdded: "+data);
                //the datasnapshot carries the newly added data
                //so we have to take hello world into a class so the class is string
                notes.add(data);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //an existing data node updated
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                //when a data at a subnode is removed
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //when the position of a subnode changes
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //when the read operation failed
            }
        });
    }
}
